import { Compression } from 'node-appwrite';
import { mediaBucketPermissions } from '../lib/schema.js';

export const name = 'media_bucket_permissions';

export async function up({ config, storage, delay }) {
  const bucket = await storage.getBucket(config.mediaBucketId);

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

  console.log(`✓ Đã cập nhật quyền upload cho bucket: ${config.mediaBucketId}`);
  await delay(1000);
}
