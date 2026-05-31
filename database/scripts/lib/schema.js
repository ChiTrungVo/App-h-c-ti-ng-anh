import { Permission, Role } from 'node-appwrite';

export const COLLECTIONS = {
  schemaMigrations: 'schema_migrations',
  userProfiles: 'user_profiles',
  notificationSettings: 'notification_settings',
  vocabularySets: 'vocabulary_sets',
  vocabularies: 'vocabularies',
  userWordProgress: 'user_word_progress',
  studyPlans: 'study_plans',
  dailyLearningStats: 'daily_learning_stats',
  quizAttempts: 'quiz_attempts',
  quizAnswers: 'quiz_answers'
};

export const userCollectionPermissions = [
  Permission.read(Role.users()),
  Permission.create(Role.users()),
  Permission.update(Role.users()),
  Permission.delete(Role.users())
];

export const ownerDocumentPermissions = (userId) => [
  Permission.read(Role.user(userId)),
  Permission.update(Role.user(userId)),
  Permission.delete(Role.user(userId))
];

export const mediaBucketPermissions = [
  Permission.read(Role.users()),
  Permission.create(Role.users()),
  Permission.update(Role.users()),
  Permission.delete(Role.users())
];

export const coreCollections = [
  {
    id: COLLECTIONS.userProfiles,
    name: 'User Profiles',
    attributes: [
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
      { type: 'integer', key: 'dailyTargetMinutes', required: false, default: 15 },
      { type: 'string', key: 'preferredLearningStyle', size: 100, required: false },
      { type: 'boolean', key: 'soundEnabled', required: false, default: true },
      { type: 'boolean', key: 'darkModeEnabled', required: false, default: false },
      { type: 'string', key: 'status', size: 50, required: false, default: 'active' },
      { type: 'datetime', key: 'lastLoginAt', required: false },
      { type: 'datetime', key: 'createdAt', required: false },
      { type: 'datetime', key: 'updatedAt', required: false }
    ],
    indexes: [
      { key: 'idx_profiles_userId', type: 'unique', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_profiles_email', type: 'key', attributes: ['email'], orders: ['ASC'] },
      { key: 'idx_profiles_status', type: 'key', attributes: ['status'], orders: ['ASC'] }
    ]
  },
  {
    id: COLLECTIONS.notificationSettings,
    name: 'Notification Settings',
    attributes: [
      { type: 'string', key: 'userId', size: 36, required: true },
      { type: 'string', key: 'reminderTime', size: 10, required: false, default: '08:00' },
      { type: 'string', key: 'reminderDays', size: 10, required: false, array: true },
      { type: 'string', key: 'timezone', size: 64, required: false, default: 'Asia/Ho_Chi_Minh' },
      { type: 'boolean', key: 'isEnabled', required: false, default: true },
      { type: 'string', key: 'fcmToken', size: 500, required: false },
      { type: 'datetime', key: 'createdAt', required: false },
      { type: 'datetime', key: 'updatedAt', required: false }
    ],
    indexes: [
      { key: 'idx_notifications_userId', type: 'unique', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_notifications_enabled', type: 'key', attributes: ['isEnabled'], orders: ['ASC'] }
    ]
  },
  {
    id: COLLECTIONS.vocabularySets,
    name: 'Vocabulary Sets',
    attributes: [
      { type: 'string', key: 'userId', size: 36, required: true },
      { type: 'string', key: 'title', size: 255, required: true },
      { type: 'string', key: 'description', size: 500, required: false },
      { type: 'string', key: 'tags', size: 50, required: false, array: true },
      { type: 'integer', key: 'wordCount', required: false, default: 0 },
      { type: 'boolean', key: 'isPublic', required: false, default: false },
      { type: 'datetime', key: 'createdAt', required: false },
      { type: 'datetime', key: 'updatedAt', required: false },
      { type: 'datetime', key: 'deletedAt', required: false }
    ],
    indexes: [
      { key: 'idx_sets_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_sets_user_title', type: 'key', attributes: ['userId', 'title'], orders: ['ASC', 'ASC'] },
      { key: 'idx_sets_public', type: 'key', attributes: ['isPublic'], orders: ['ASC'] },
      { key: 'idx_sets_updatedAt', type: 'key', attributes: ['updatedAt'], orders: ['DESC'] }
    ]
  },
  {
    id: COLLECTIONS.vocabularies,
    name: 'Vocabularies',
    attributes: [
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
      { type: 'string', key: 'imageFileId', size: 36, required: false },
      { type: 'string', key: 'audioUrl', size: 1000, required: false },
      { type: 'string', key: 'audioFileId', size: 36, required: false },
      { type: 'datetime', key: 'createdAt', required: false },
      { type: 'datetime', key: 'updatedAt', required: false },
      { type: 'datetime', key: 'deletedAt', required: false }
    ],
    indexes: [
      { key: 'idx_vocab_setId', type: 'key', attributes: ['setId'], orders: ['ASC'] },
      { key: 'idx_vocab_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_vocab_set_word', type: 'key', attributes: ['setId', 'word'], orders: ['ASC', 'ASC'] },
      { key: 'idx_vocab_word', type: 'key', attributes: ['word'], orders: ['ASC'] },
      { key: 'idx_vocab_media_image', type: 'key', attributes: ['imageFileId'], orders: ['ASC'] },
      { key: 'idx_vocab_media_audio', type: 'key', attributes: ['audioFileId'], orders: ['ASC'] },
      { key: 'idx_vocab_updatedAt', type: 'key', attributes: ['updatedAt'], orders: ['DESC'] }
    ]
  },
  {
    id: COLLECTIONS.userWordProgress,
    name: 'User Word Progress',
    attributes: [
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
    ],
    indexes: [
      { key: 'idx_progress_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_progress_wordId', type: 'key', attributes: ['wordId'], orders: ['ASC'] },
      { key: 'idx_progress_user_word', type: 'unique', attributes: ['userId', 'wordId'], orders: ['ASC', 'ASC'] },
      { key: 'idx_progress_user_due', type: 'key', attributes: ['userId', 'nextReviewAt'], orders: ['ASC', 'ASC'] },
      { key: 'idx_progress_user_set', type: 'key', attributes: ['userId', 'setId'], orders: ['ASC', 'ASC'] }
    ]
  },
  {
    id: COLLECTIONS.studyPlans,
    name: 'Study Plans',
    attributes: [
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
    ],
    indexes: [
      { key: 'idx_plans_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_plans_user_active', type: 'key', attributes: ['userId', 'isActive'], orders: ['ASC', 'ASC'] },
      { key: 'idx_plans_user_set', type: 'key', attributes: ['userId', 'setId'], orders: ['ASC', 'ASC'] }
    ]
  },
  {
    id: COLLECTIONS.dailyLearningStats,
    name: 'Daily Learning Stats',
    attributes: [
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
    ],
    indexes: [
      { key: 'idx_stats_user_date', type: 'unique', attributes: ['userId', 'date'], orders: ['ASC', 'ASC'] },
      { key: 'idx_stats_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_stats_date', type: 'key', attributes: ['date'], orders: ['DESC'] }
    ]
  },
  {
    id: COLLECTIONS.quizAttempts,
    name: 'Quiz Attempts',
    attributes: [
      { type: 'string', key: 'userId', size: 36, required: true },
      { type: 'string', key: 'setId', size: 36, required: false },
      { type: 'string', key: 'quizType', size: 50, required: true },
      { type: 'string', key: 'status', size: 50, required: false, default: 'COMPLETED' },
      { type: 'integer', key: 'totalQuestions', required: false, default: 0 },
      { type: 'integer', key: 'correctAnswers', required: false, default: 0 },
      { type: 'double', key: 'scorePercent', required: false, default: 0 },
      { type: 'integer', key: 'durationSeconds', required: false, default: 0 },
      { type: 'datetime', key: 'startedAt', required: false },
      { type: 'datetime', key: 'completedAt', required: false },
      { type: 'datetime', key: 'createdAt', required: false },
      { type: 'datetime', key: 'updatedAt', required: false }
    ],
    indexes: [
      { key: 'idx_attempts_userId', type: 'key', attributes: ['userId'], orders: ['ASC'] },
      { key: 'idx_attempts_user_completed', type: 'key', attributes: ['userId', 'completedAt'], orders: ['ASC', 'DESC'] },
      { key: 'idx_attempts_user_set', type: 'key', attributes: ['userId', 'setId'], orders: ['ASC', 'ASC'] },
      { key: 'idx_attempts_status', type: 'key', attributes: ['status'], orders: ['ASC'] }
    ]
  },
  {
    id: COLLECTIONS.quizAnswers,
    name: 'Quiz Answers',
    attributes: [
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
    ],
    indexes: [
      { key: 'idx_answers_attemptId', type: 'key', attributes: ['attemptId'], orders: ['ASC'] },
      { key: 'idx_answers_user_word', type: 'key', attributes: ['userId', 'wordId'], orders: ['ASC', 'ASC'] },
      { key: 'idx_answers_isCorrect', type: 'key', attributes: ['isCorrect'], orders: ['ASC'] }
    ]
  }
];
