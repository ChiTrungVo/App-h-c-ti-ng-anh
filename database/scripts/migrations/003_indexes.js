import { coreCollections } from '../lib/schema.js';

export const name = 'indexes';

export async function up({ ensureIndex }) {
  for (const collection of coreCollections) {
    for (const index of collection.indexes) {
      await ensureIndex(collection.id, index);
    }
  }
}
