import { COLLECTIONS } from '../lib/schema.js';

export const name = 'profile_avatar_file_id';

export async function up({ ensureAttribute, delay }) {
  await ensureAttribute(COLLECTIONS.userProfiles, {
    type: 'string',
    key: 'avatarFileId',
    size: 36,
    required: false
  });

  await delay(2000);
}
