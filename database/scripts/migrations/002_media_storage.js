import { Compression } from 'node-appwrite';
import { COLLECTIONS, mediaBucketPermissions } from '../lib/schema.js';

export const name = 'media_storage';

export async function up({ config, storage, ensureAttribute, isConflict, isNotFound, delay }) {
  try {
    const bucket = await storage.getBucket(config.mediaBucketId);
    console.log(`✓ Storage bucket đã tồn tại: ${config.mediaBucketId}`);
    await storage.updateBucket(
      config.mediaBucketId,
      bucket.name || 'MinLish Media',
      mediaBucketPermissions,
      bucket.fileSecurity ?? true,
      bucket.enabled ?? true,
      bucket.maximumFileSize || 15 * 1024 * 1024,
      bucket.allowedFileExtensions || ['jpg', 'jpeg', 'png', 'webp', 'gif', 'mp3', 'm4a', 'wav', 'ogg'],
      bucket.compression || Compression.Gzip,
      bucket.encryption ?? true,
      bucket.antivirus ?? true
    );
    console.log(`  ✓ Đã đồng bộ permissions bucket: ${config.mediaBucketId}`);
  } catch (error) {
    if (!isNotFound(error)) throw error;
    await storage.createBucket(
      config.mediaBucketId,
      'MinLish Media',
      mediaBucketPermissions,
      true,
      true,
      15 * 1024 * 1024,
      ['jpg', 'jpeg', 'png', 'webp', 'gif', 'mp3', 'm4a', 'wav', 'ogg'],
      Compression.Gzip,
      true,
      true
    );
    console.log(`✓ Da tao storage bucket: ${config.mediaBucketId}`);
  }

  const mediaAttributes = [
    { type: 'string', key: 'imageFileId', size: 36, required: false },
    { type: 'string', key: 'audioUrl', size: 1000, required: false },
    { type: 'string', key: 'audioFileId', size: 36, required: false }
  ];

  for (const attribute of mediaAttributes) {
    try {
      await ensureAttribute(COLLECTIONS.vocabularies, attribute);
    } catch (error) {
      if (!isConflict(error)) throw error;
    }
  }

  await delay(2000);
}
