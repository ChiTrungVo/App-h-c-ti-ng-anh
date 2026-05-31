import 'dotenv/config';
import { mkdtemp, rm } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { createRequire } from 'node:module';
import { execFile } from 'node:child_process';
import { promisify } from 'node:util';
import { Client, Functions, Role } from 'node-appwrite';

const execFileAsync = promisify(execFile);
const dirname = path.dirname(fileURLToPath(import.meta.url));
const require = createRequire(import.meta.url);
const { InputFile } = require(path.join(dirname, 'node_modules', 'node-appwrite', 'dist', 'inputFile.js'));
const functionDir = path.join(dirname, '..', 'functions', 'email-verification');
const functionId = 'minlish-email-verify';
const verifyDomain = process.env.APPWRITE_VERIFY_DOMAIN || 'minlish-email-verify.sgp.appwrite.run';
const verifyPlatformId = 'minlish_verify_function';

function requireEnv(name) {
  const value = process.env[name];
  if (!value) throw new Error(`Missing ${name} in database/scripts/.env`);
  return value;
}

async function ensureFunction(functions) {
  try {
    return await functions.get(functionId);
  } catch (error) {
    if (error.code !== 404) throw error;
  }

  return functions.create(
    functionId,
    'MinLish Email Verification',
    'node-22',
    [Role.any()],
    undefined,
    undefined,
    15,
    true,
    true,
    'src/main.js',
    'npm install'
  );
}

async function upsertVariable(functions, key, value) {
  const variables = await functions.listVariables(functionId);
  const existing = variables.variables.find((variable) => variable.key === key);
  if (existing) {
    return functions.updateVariable(functionId, existing.$id, key, value);
  }
  return functions.createVariable(functionId, key, value);
}

async function apiFetch(pathname, options = {}) {
  const response = await fetch(`${requireEnv('APPWRITE_ENDPOINT')}${pathname}`, {
    ...options,
    headers: {
      'X-Appwrite-Project': requireEnv('APPWRITE_PROJECT_ID'),
      'X-Appwrite-Key': requireEnv('APPWRITE_API_KEY'),
      'Content-Type': 'application/json',
      ...(options.headers || {})
    }
  });

  if (response.status === 204) return null;

  const text = await response.text();
  const body = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(body?.message || `Appwrite API failed: ${response.status}`);
  }

  return body;
}

async function ensureVerifyPlatform() {
  const projectId = requireEnv('APPWRITE_PROJECT_ID');
  const existing = await apiFetch(`/projects/${projectId}/platforms`);
  const platform = existing.platforms.find((item) => item.$id === verifyPlatformId);

  if (platform) {
    if (platform.hostname === verifyDomain) return platform;

    return apiFetch(`/projects/${projectId}/platforms/${verifyPlatformId}`, {
      method: 'PUT',
      body: JSON.stringify({
        name: 'MinLish Email Verification Function',
        hostname: verifyDomain
      })
    });
  }

  return apiFetch(`/projects/${projectId}/platforms`, {
    method: 'POST',
    body: JSON.stringify({
      platformId: verifyPlatformId,
      type: 'web',
      name: 'MinLish Email Verification Function',
      hostname: verifyDomain
    })
  });
}

async function ensureFunctionProxyRule() {
  const existing = await apiFetch('/proxy/rules');
  const rule = existing.rules.find((item) =>
    item.domain === verifyDomain &&
    item.deploymentResourceType === 'function' &&
    item.deploymentResourceId === functionId
  );

  if (rule) return rule;

  return apiFetch('/proxy/rules/function', {
    method: 'POST',
    body: JSON.stringify({
      domain: verifyDomain,
      functionId
    })
  });
}

async function packageFunction() {
  const tempDir = await mkdtemp(path.join(tmpdir(), 'minlish-email-verify-'));
  const archivePath = path.join(tempDir, 'code.tar.gz');
  await execFileAsync('tar', ['-czf', archivePath, '-C', functionDir, '.']);
  return { tempDir, archivePath };
}

const client = new Client()
  .setEndpoint(requireEnv('APPWRITE_ENDPOINT'))
  .setProject(requireEnv('APPWRITE_PROJECT_ID'))
  .setKey(requireEnv('APPWRITE_API_KEY'));

const functions = new Functions(client);

const created = await ensureFunction(functions);
console.log(`Function ready: ${created.$id}`);

await upsertVariable(functions, 'APPWRITE_ENDPOINT', requireEnv('APPWRITE_ENDPOINT'));
await upsertVariable(functions, 'APPWRITE_PROJECT_ID', requireEnv('APPWRITE_PROJECT_ID'));
console.log('Function variables ready.');

const { tempDir, archivePath } = await packageFunction();
try {
  const deployment = await functions.createDeployment(
    functionId,
    InputFile.fromPath(archivePath, 'code.tar.gz'),
    true,
    'src/main.js',
    'npm install'
  );
  console.log(`Deployment created: ${deployment.$id}`);
  console.log(JSON.stringify(await functions.get(functionId), null, 2));
} finally {
  await rm(tempDir, { recursive: true, force: true });
}

const platform = await ensureVerifyPlatform();
console.log(`Verification platform ready: ${platform.hostname}`);

const rule = await ensureFunctionProxyRule();
console.log(`Verification function URL ready: https://${rule.domain}`);
