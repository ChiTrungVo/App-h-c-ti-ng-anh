import crypto from 'node:crypto';
import fs from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import {
  config,
  databases,
  delay,
  ensureAttribute,
  ensureCollection,
  ensureDatabase,
  ensureIndex,
  ensureSchemaMigrations,
  getAppliedMigrations,
  isConflict,
  isNotFound,
  recordMigration,
  storage
} from './lib/appwrite-admin.js';

const dirname = path.dirname(fileURLToPath(import.meta.url));
const migrationsDir = path.join(dirname, 'migrations');

async function getMigrationFiles() {
  const files = await fs.readdir(migrationsDir);
  return files
    .filter((file) => /^\d{3}_.+\.js$/.test(file))
    .sort();
}

async function checksum(filePath) {
  const content = await fs.readFile(filePath);
  return crypto.createHash('sha256').update(content).digest('hex');
}

function migrationVersion(fileName) {
  return fileName.split('_')[0];
}

async function run() {
  console.log('='.repeat(60));
  console.log('BẮT ĐẦU MIGRATE APPWRITE');
  console.log('='.repeat(60));

  await ensureDatabase();
  await ensureSchemaMigrations();

  const applied = await getAppliedMigrations();
  const files = await getMigrationFiles();

  for (const fileName of files) {
    const version = migrationVersion(fileName);
    const filePath = path.join(migrationsDir, fileName);
    const fileChecksum = await checksum(filePath);
    const migration = await import(pathToFileURL(filePath));
    const name = migration.name || fileName.replace(/\.js$/, '');

    if (applied.has(version)) {
      console.log(`✓ Migration ${version} da chay: ${applied.get(version).name}`);
      continue;
    }

    console.log(`\n→ Đang chạy migration ${version}: ${name}`);
    await migration.up({
      config,
      databases,
      storage,
      delay,
      ensureCollection,
      ensureAttribute,
      ensureIndex,
      isConflict,
      isNotFound
    });
    await recordMigration({ version, name, checksum: fileChecksum });
    console.log(`✓ Hoàn tất migration ${version}: ${name}`);
  }

  console.log('\n' + '='.repeat(60));
  console.log('✓ MIGRATE HOAN TAT');
  console.log('='.repeat(60));
  console.log(`Database: ${config.databaseId}`);
  console.log(`Media bucket: ${config.mediaBucketId}`);
}

run().catch((error) => {
  console.error('\n✗ MIGRATE THẤT BẠI:', error.message);
  console.error(error);
  process.exit(1);
});
