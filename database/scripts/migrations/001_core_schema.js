import { coreCollections } from '../lib/schema.js';

export const name = 'core_schema';

export async function up({ ensureCollection, ensureAttribute, delay }) {
  for (const collection of coreCollections) {
    await ensureCollection(collection.id, collection.name);

    for (const attribute of collection.attributes) {
      await ensureAttribute(collection.id, attribute);
    }

    console.log(`  Đang đợi attributes sẵn sàng cho ${collection.id}...`);
    await delay(5000);
  }
}
