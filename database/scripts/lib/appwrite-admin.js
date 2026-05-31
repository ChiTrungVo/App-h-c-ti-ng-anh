import { Client, Databases, Query, Storage } from 'node-appwrite';
import dotenv from 'dotenv';
import { COLLECTIONS, userCollectionPermissions } from './schema.js';

dotenv.config();

const requiredEnv = ['APPWRITE_ENDPOINT', 'APPWRITE_PROJECT_ID', 'APPWRITE_API_KEY', 'DATABASE_ID'];
const missingEnv = requiredEnv.filter((key) => !process.env[key]);

if (missingEnv.length > 0) {
  console.error(`Thiếu biến môi trường: ${missingEnv.join(', ')}`);
  console.error('Hãy tạo database/scripts/.env từ .env.example trước khi chạy migrate.');
  process.exit(1);
}

export const config = {
  endpoint: process.env.APPWRITE_ENDPOINT,
  projectId: process.env.APPWRITE_PROJECT_ID,
  databaseId: process.env.DATABASE_ID,
  mediaBucketId: process.env.MEDIA_BUCKET_ID || 'minlish_media'
};

export const client = new Client()
  .setEndpoint(config.endpoint)
  .setProject(config.projectId)
  .setKey(process.env.APPWRITE_API_KEY);

export const databases = new Databases(client);
export const storage = new Storage(client);

export const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export function isConflict(error) {
  return error?.code === 409 || error?.type === 'document_already_exists';
}

export function isNotFound(error) {
  return error?.code === 404;
}

export async function ensureDatabase() {
  try {
    await databases.get(config.databaseId);
    console.log(`✓ Database đã tồn tại: ${config.databaseId}`);
  } catch (error) {
    if (!isNotFound(error)) throw error;
    await databases.create(config.databaseId, 'Vocabulary Learning App');
    console.log(`✓ Da tao database: ${config.databaseId}`);
    await delay(1000);
  }
}

export async function ensureCollection(collectionId, name, permissions = userCollectionPermissions, documentSecurity = true) {
  try {
    await databases.getCollection(config.databaseId, collectionId);
    console.log(`✓ Collection đã tồn tại: ${collectionId}`);
  } catch (error) {
    if (!isNotFound(error)) throw error;
    await databases.createCollection(config.databaseId, collectionId, name, permissions, documentSecurity);
    console.log(`✓ Da tao collection: ${collectionId}`);
    await delay(1000);
  }
}

export async function ensureAttribute(collectionId, attributeConfig) {
  const { type, key, ...value } = attributeConfig;

  try {
    switch (type) {
      case 'string':
        await databases.createStringAttribute(
          config.databaseId,
          collectionId,
          key,
          value.size,
          value.required,
          value.default,
          value.array || false,
          value.encrypt || false
        );
        break;
      case 'integer':
        await databases.createIntegerAttribute(
          config.databaseId,
          collectionId,
          key,
          value.required,
          value.min,
          value.max,
          value.default,
          value.array || false
        );
        break;
      case 'double':
        await databases.createFloatAttribute(
          config.databaseId,
          collectionId,
          key,
          value.required,
          value.min,
          value.max,
          value.default,
          value.array || false
        );
        break;
      case 'boolean':
        await databases.createBooleanAttribute(
          config.databaseId,
          collectionId,
          key,
          value.required,
          value.default,
          value.array || false
        );
        break;
      case 'datetime':
        await databases.createDatetimeAttribute(
          config.databaseId,
          collectionId,
          key,
          value.required,
          value.default,
          value.array || false
        );
        break;
      default:
        throw new Error(`Unsupported attribute type: ${type}`);
    }

    console.log(`  ✓ Attribute: ${collectionId}.${key}`);
    await delay(350);
  } catch (error) {
    if (isConflict(error)) {
      console.log(`  ✓ Attribute đã tồn tại: ${collectionId}.${key}`);
      return;
    }
    throw error;
  }
}

export async function ensureIndex(collectionId, indexConfig) {
  try {
    await databases.createIndex(
      config.databaseId,
      collectionId,
      indexConfig.key,
      indexConfig.type,
      indexConfig.attributes,
      indexConfig.orders || []
    );
    console.log(`  ✓ Index: ${collectionId}.${indexConfig.key}`);
    await delay(350);
  } catch (error) {
    if (isConflict(error)) {
      console.log(`  ✓ Index đã tồn tại: ${collectionId}.${indexConfig.key}`);
      return;
    }
    throw error;
  }
}

export async function ensureSchemaMigrations() {
  await ensureCollection(
    COLLECTIONS.schemaMigrations,
    'Schema Migrations',
    [],
    false
  );

  await ensureAttribute(COLLECTIONS.schemaMigrations, {
    type: 'string',
    key: 'version',
    size: 32,
    required: true
  });
  await ensureAttribute(COLLECTIONS.schemaMigrations, {
    type: 'string',
    key: 'name',
    size: 255,
    required: true
  });
  await ensureAttribute(COLLECTIONS.schemaMigrations, {
    type: 'string',
    key: 'checksum',
    size: 64,
    required: true
  });
  await ensureAttribute(COLLECTIONS.schemaMigrations, {
    type: 'datetime',
    key: 'executedAt',
    required: true
  });
  await delay(2000);
  await ensureIndex(COLLECTIONS.schemaMigrations, {
    key: 'idx_migrations_version',
    type: 'unique',
    attributes: ['version'],
    orders: ['ASC']
  });
}

export async function getAppliedMigrations() {
  const result = await databases.listDocuments(
    config.databaseId,
    COLLECTIONS.schemaMigrations,
    [Query.limit(100)]
  );
  return new Map(result.documents.map((document) => [document.version, document]));
}

export async function recordMigration({ version, name, checksum }) {
  try {
    await databases.createDocument(
      config.databaseId,
      COLLECTIONS.schemaMigrations,
      version,
      {
        version,
        name,
        checksum,
        executedAt: new Date().toISOString()
      }
    );
  } catch (error) {
    if (isConflict(error)) return;
    throw error;
  }
}
