import { Client, Databases } from 'node-appwrite';
import dotenv from 'dotenv';

dotenv.config();

const client = new Client()
  .setEndpoint(process.env.APPWRITE_ENDPOINT)
  .setProject(process.env.APPWRITE_PROJECT_ID)
  .setKey(process.env.APPWRITE_API_KEY);

const databases = new Databases(client);
const databaseId = process.env.DATABASE_ID;

async function fixQuizType() {
  try {
    console.log('Đang tạo attribute quizType...');
    await databases.createStringAttribute(
      databaseId,
      'quiz_attempts',
      'quizType',
      50,
      true, // required
      undefined, // không có default
      false
    );
    console.log('✓ Đã tạo attribute quizType thành công');
  } catch (error) {
    if (error.code === 409) {
      console.log('✓ Attribute quizType đã tồn tại');
    } else {
      console.error('✗ Lỗi:', error.message);
    }
  }
}

fixQuizType();
