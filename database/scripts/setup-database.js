import { Client, Databases, ID, Permission, Role } from 'node-appwrite';
import dotenv from 'dotenv';

dotenv.config();

const client = new Client()
  .setEndpoint(process.env.APPWRITE_ENDPOINT)
  .setProject(process.env.APPWRITE_PROJECT_ID)
  .setKey(process.env.APPWRITE_API_KEY);

const databases = new Databases(client);
const databaseId = process.env.DATABASE_ID;

// Hàm delay để tránh rate limit
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Hàm tạo database
async function createDatabase() {
  try {
    console.log('Đang tạo database...');
    await databases.create(databaseId, 'Vocabulary Learning App');
    console.log('✓ Đã tạo database thành công');
    await delay(1000);
  } catch (error) {
    if (error.code === 409) {
      console.log('✓ Database đã tồn tại');
    } else {
      throw error;
    }
  }
}

// Hàm tạo collection
async function createCollection(collectionId, collectionName) {
  try {
    console.log(`\nĐang tạo collection: ${collectionName}...`);
    await databases.createCollection(
      databaseId,
      collectionId,
      collectionName,
      [
        Permission.read(Role.users()),
        Permission.create(Role.users()),
        Permission.update(Role.users()),
        Permission.delete(Role.users())
      ],
      true // documentSecurity enabled
    );
    console.log(`✓ Đã tạo collection ${collectionName}`);
    await delay(1000);
  } catch (error) {
    if (error.code === 409) {
      console.log(`✓ Collection ${collectionName} đã tồn tại`);
    } else {
      throw error;
    }
  }
}

// Hàm tạo attribute
async function createAttribute(collectionId, attributeConfig) {
  try {
    const { type, key, ...config } = attributeConfig;

    switch (type) {
      case 'string':
        await databases.createStringAttribute(databaseId, collectionId, key, config.size, config.required, config.default, config.array || false);
        break;
      case 'integer':
        await databases.createIntegerAttribute(databaseId, collectionId, key, config.required, config.min, config.max, config.default, config.array || false);
        break;
      case 'double':
        await databases.createFloatAttribute(databaseId, collectionId, key, config.required, config.min, config.max, config.default, config.array || false);
        break;
      case 'boolean':
        await databases.createBooleanAttribute(databaseId, collectionId, key, config.required, config.default, config.array || false);
        break;
      case 'datetime':
        await databases.createDatetimeAttribute(databaseId, collectionId, key, config.required, config.default, config.array || false);
        break;
    }

    console.log(`  ✓ Attribute: ${key}`);
    await delay(500);
  } catch (error) {
    if (error.code === 409) {
      console.log(`  ✓ Attribute ${attributeConfig.key} đã tồn tại`);
    } else {
      console.error(`  ✗ Lỗi tạo attribute ${attributeConfig.key}:`, error.message);
    }
  }
}

// Hàm tạo index
async function createIndex(collectionId, indexConfig) {
  try {
    await databases.createIndex(
      databaseId,
      collectionId,
      indexConfig.key,
      indexConfig.type,
      indexConfig.attributes,
      indexConfig.orders || []
    );
    console.log(`  ✓ Index: ${indexConfig.key}`);
    await delay(500);
  } catch (error) {
    if (error.code === 409) {
      console.log(`  ✓ Index ${indexConfig.key} đã tồn tại`);
    } else {
      console.error(`  ✗ Lỗi tạo index ${indexConfig.key}:`, error.message);
    }
  }
}

// Collection 1: user_profiles
async function createUserProfiles() {
  const collectionId = 'user_profiles';
  await createCollection(collectionId, 'User Profiles');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'displayName', size: 255, required: true },
    { type: 'string', key: 'email', size: 255, required: true },
    { type: 'string', key: 'avatarUrl', size: 1000, required: false },
    { type: 'string', key: 'phone', size: 20, required: false },
    { type: 'string', key: 'bio', size: 500, required: false },
    { type: 'string', key: 'nativeLanguage', size: 50, required: false, default: 'vi' },
    { type: 'string', key: 'targetLanguage', size: 50, required: false, default: 'en' },
    { type: 'string', key: 'proficiencyLevel', size: 50, required: false, default: 'beginner' },
    { type: 'string', key: 'studyGoal', size: 500, required: false },
    { type: 'integer', key: 'dailyTargetMinutes', required: false, default: 0 },
    { type: 'string', key: 'preferredLearningStyle', size: 100, required: false },
    { type: 'boolean', key: 'soundEnabled', required: false, default: true },
    { type: 'boolean', key: 'darkModeEnabled', required: false, default: false },
    { type: 'string', key: 'status', size: 50, required: false, default: 'active' },
    { type: 'datetime', key: 'lastLoginAt', required: false },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  // Đợi attributes được tạo xong
  console.log('  Đang đợi attributes được xử lý...');
  await delay(5000);

  const indexes = [
    { key: 'idx_profiles_userId', type: 'unique', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_profiles_email', type: 'key', attributes: ['email'], orders: ['ASC'] },
    { key: 'idx_profiles_status', type: 'key', attributes: ['status'], orders: ['ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 2: notification_settings
async function createNotificationSettings() {
  const collectionId = 'notification_settings';
  await createCollection(collectionId, 'Notification Settings');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'reminderTime', size: 10, required: false, default: '08:00' },
    { type: 'string', key: 'reminderDays', size: 10, required: false, array: true },
    { type: 'string', key: 'timezone', size: 64, required: false, default: 'Asia/Ho_Chi_Minh' },
    { type: 'boolean', key: 'isEnabled', required: false, default: true },
    { type: 'string', key: 'fcmToken', size: 500, required: false },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_notifications_userId', type: 'unique', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_notifications_enabled', type: 'key', attributes: ['isEnabled'], orders: ['ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 3: vocabulary_sets
async function createVocabularySets() {
  const collectionId = 'vocabulary_sets';
  await createCollection(collectionId, 'Vocabulary Sets');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'title', size: 255, required: true },
    { type: 'string', key: 'description', size: 500, required: false },
    { type: 'string', key: 'tags', size: 50, required: false, array: true },
    { type: 'integer', key: 'wordCount', required: false, default: 0 },
    { type: 'boolean', key: 'isPublic', required: false, default: false },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false },
    { type: 'datetime', key: 'deletedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_sets_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_sets_public', type: 'key', attributes: ['isPublic'], orders: ['ASC'] },
    { key: 'idx_sets_updatedAt', type: 'key', attributes: ['updatedAt'], orders: ['DESC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 4: vocabularies
async function createVocabularies() {
  const collectionId = 'vocabularies';
  await createCollection(collectionId, 'Vocabularies');

  const attributes = [
    { type: 'string', key: 'setId', size: 36, required: true },
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'word', size: 255, required: true },
    { type: 'string', key: 'pronunciation', size: 255, required: false },
    { type: 'string', key: 'meaning', size: 500, required: true },
    { type: 'string', key: 'definition', size: 1000, required: false },
    { type: 'string', key: 'example', size: 1000, required: false },
    { type: 'string', key: 'collocations', size: 500, required: false, array: true },
    { type: 'string', key: 'relatedWords', size: 500, required: false, array: true },
    { type: 'string', key: 'note', size: 1000, required: false },
    { type: 'string', key: 'imageUrl', size: 1000, required: false },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false },
    { type: 'datetime', key: 'deletedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_vocab_setId', type: 'key', attributes: ['setId'], orders: ['ASC'] },
    { key: 'idx_vocab_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_vocab_word', type: 'key', attributes: ['word'], orders: ['ASC'] },
    { key: 'idx_vocab_updatedAt', type: 'key', attributes: ['updatedAt'], orders: ['DESC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 5: user_word_progress
async function createUserWordProgress() {
  const collectionId = 'user_word_progress';
  await createCollection(collectionId, 'User Word Progress');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'setId', size: 36, required: true },
    { type: 'string', key: 'wordId', size: 36, required: true },
    { type: 'string', key: 'status', size: 50, required: false, default: 'NOT_STARTED' },
    { type: 'integer', key: 'boxLevel', required: false, default: 0 },
    { type: 'double', key: 'easinessFactor', required: false, default: 2.5 },
    { type: 'integer', key: 'repetitions', required: false, default: 0 },
    { type: 'integer', key: 'intervalDays', required: false, default: 1 },
    { type: 'datetime', key: 'nextReviewAt', required: true },
    { type: 'datetime', key: 'lastReviewedAt', required: false },
    { type: 'integer', key: 'lastQuality', required: false, min: 0, max: 5 },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_progress_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_progress_wordId', type: 'key', attributes: ['wordId'], orders: ['ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 6: study_plans
async function createStudyPlans() {
  const collectionId = 'study_plans';
  await createCollection(collectionId, 'Study Plans');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'setId', size: 36, required: false },
    { type: 'string', key: 'planName', size: 255, required: true },
    { type: 'integer', key: 'targetWordsPerDay', required: true },
    { type: 'integer', key: 'targetReviewPerDay', required: false, default: 0 },
    { type: 'string', key: 'startDate', size: 10, required: true },
    { type: 'string', key: 'endDate', size: 10, required: true },
    { type: 'boolean', key: 'isActive', required: false, default: true },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_plans_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_plans_user_active', type: 'key', attributes: ['userId', 'isActive'], orders: ['ASC', 'ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 7: daily_learning_stats
async function createDailyLearningStats() {
  const collectionId = 'daily_learning_stats';
  await createCollection(collectionId, 'Daily Learning Stats');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'date', size: 10, required: true },
    { type: 'integer', key: 'wordsLearned', required: false, default: 0 },
    { type: 'integer', key: 'wordsReviewed', required: false, default: 0 },
    { type: 'integer', key: 'wordsMastered', required: false, default: 0 },
    { type: 'integer', key: 'quizCount', required: false, default: 0 },
    { type: 'integer', key: 'correctAnswers', required: false, default: 0 },
    { type: 'integer', key: 'totalQuestions', required: false, default: 0 },
    { type: 'double', key: 'avgScore', required: false, default: 0 },
    { type: 'integer', key: 'studyMinutes', required: false, default: 0 },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_stats_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_stats_date', type: 'key', attributes: ['date'], orders: ['DESC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 8: quiz_attempts
async function createQuizAttempts() {
  const collectionId = 'quiz_attempts';
  await createCollection(collectionId, 'Quiz Attempts');

  const attributes = [
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'setId', size: 36, required: false },
    { type: 'string', key: 'quizType', size: 50, required: true, default: 'multiple_choice' },
    { type: 'string', key: 'status', size: 50, required: false, default: 'COMPLETED' },
    { type: 'integer', key: 'totalQuestions', required: false, default: 0 },
    { type: 'integer', key: 'correctAnswers', required: false, default: 0 },
    { type: 'double', key: 'scorePercent', required: false, default: 0 },
    { type: 'integer', key: 'durationSeconds', required: false, default: 0 },
    { type: 'datetime', key: 'startedAt', required: false },
    { type: 'datetime', key: 'completedAt', required: false },
    { type: 'datetime', key: 'createdAt', required: false },
    { type: 'datetime', key: 'updatedAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_attempts_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
    { key: 'idx_attempts_status', type: 'key', attributes: ['status'], orders: ['ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Collection 9: quiz_answers
async function createQuizAnswers() {
  const collectionId = 'quiz_answers';
  await createCollection(collectionId, 'Quiz Answers');

  const attributes = [
    { type: 'string', key: 'attemptId', size: 36, required: true },
    { type: 'string', key: 'userId', size: 36, required: true },
    { type: 'string', key: 'wordId', size: 36, required: true },
    { type: 'string', key: 'questionType', size: 50, required: true },
    { type: 'string', key: 'questionText', size: 1000, required: false },
    { type: 'string', key: 'correctAnswer', size: 500, required: false },
    { type: 'string', key: 'userAnswer', size: 500, required: false },
    { type: 'boolean', key: 'isCorrect', required: false, default: false },
    { type: 'integer', key: 'quality', required: false, min: 0, max: 5 },
    { type: 'datetime', key: 'answeredAt', required: false },
    { type: 'datetime', key: 'createdAt', required: false }
  ];

  for (const attr of attributes) {
    await createAttribute(collectionId, attr);
  }

  await delay(5000);

  const indexes = [
    { key: 'idx_answers_attemptId', type: 'key', attributes: ['attemptId'], orders: ['ASC'] },
    { key: 'idx_answers_isCorrect', type: 'key', attributes: ['isCorrect'], orders: ['ASC'] }
  ];

  for (const idx of indexes) {
    await createIndex(collectionId, idx);
  }
}

// Hàm chính
async function main() {
  try {
    console.log('='.repeat(60));
    console.log('BẮT ĐẦU THIẾT LẬP DATABASE APPWRITE');
    console.log('='.repeat(60));

    // Tạo database
    await createDatabase();

    // Tạo các collections theo thứ tự
    await createUserProfiles();
    await createNotificationSettings();
    await createVocabularySets();
    await createVocabularies();
    await createUserWordProgress();
    await createStudyPlans();
    await createDailyLearningStats();
    await createQuizAttempts();
    await createQuizAnswers();

    console.log('\n' + '='.repeat(60));
    console.log('✓ HOÀN THÀNH THIẾT LẬP DATABASE');
    console.log('='.repeat(60));
    console.log('\nĐã tạo thành công:');
    console.log('- 1 database: vocabulary_app_db');
    console.log('- 9 collections với đầy đủ attributes và indexes');
    console.log('\nBạn có thể kiểm tra trên Appwrite Console.');

  } catch (error) {
    console.error('\n✗ LỖI:', error.message);
    console.error('Chi tiết:', error);
    process.exit(1);
  }
}

main();
