import { ID, Permission, Query, Role, Users } from 'node-appwrite';
import {
  client,
  config,
  databases,
  isConflict,
  isNotFound
} from './lib/appwrite-admin.js';
import { COLLECTIONS, ownerDocumentPermissions } from './lib/schema.js';

const users = new Users(client);

const demoUser = {
  id: 'demo_user_minlish',
  email: 'demo@minlish.test',
  password: 'Minlish@123',
  name: 'Demo MinLish'
};

const now = new Date();
const nowIso = now.toISOString();
const today = nowIso.slice(0, 10);
const yesterdayIso = new Date(now.getTime() - 24 * 60 * 60 * 1000).toISOString();
const tomorrowIso = new Date(now.getTime() + 24 * 60 * 60 * 1000).toISOString();
const nextWeekIso = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000).toISOString();

const sets = [
  {
    id: 'demo_set_toeic_basic',
    title: 'TOEIC Basic Demo',
    description: 'Bộ từ mẫu để test flashcard, quiz, SRS và import/export.',
    tags: ['TOEIC', 'Demo'],
    isPublic: false
  },
  {
    id: 'demo_set_travel',
    title: 'Travel Demo',
    description: 'Bộ từ chủ đề du lịch dùng để test danh sách bộ từ và khám phá.',
    tags: ['Travel', 'Demo'],
    isPublic: true
  }
];

const words = [
  {
    id: 'demo_word_invoice',
    setId: 'demo_set_toeic_basic',
    word: 'invoice',
    pronunciation: '/ˈɪn.vɔɪs/',
    meaning: 'hóa đơn',
    definition: 'A document listing goods or services and the amount to pay.',
    example: 'The supplier sent an invoice after delivery.',
    collocations: ['issue an invoice', 'pay an invoice'],
    relatedWords: ['bill', 'receipt'],
    note: 'Dùng nhiều trong chủ đề business.'
  },
  {
    id: 'demo_word_deadline',
    setId: 'demo_set_toeic_basic',
    word: 'deadline',
    pronunciation: '/ˈded.laɪn/',
    meaning: 'hạn chót',
    definition: 'The latest time or date by which something should be completed.',
    example: 'The deadline for the report is Friday.',
    collocations: ['meet a deadline', 'miss a deadline'],
    relatedWords: ['due date', 'schedule'],
    note: ''
  },
  {
    id: 'demo_word_proposal',
    setId: 'demo_set_toeic_basic',
    word: 'proposal',
    pronunciation: '/prəˈpoʊ.zəl/',
    meaning: 'đề xuất',
    definition: 'A plan or suggestion presented for consideration.',
    example: 'The manager reviewed the proposal carefully.',
    collocations: ['submit a proposal', 'business proposal'],
    relatedWords: ['plan', 'suggestion'],
    note: ''
  },
  {
    id: 'demo_word_attend',
    setId: 'demo_set_toeic_basic',
    word: 'attend',
    pronunciation: '/əˈtend/',
    meaning: 'tham dự',
    definition: 'To be present at an event or meeting.',
    example: 'All employees must attend the training session.',
    collocations: ['attend a meeting', 'attend a seminar'],
    relatedWords: ['join', 'participate'],
    note: ''
  },
  {
    id: 'demo_word_itinerary',
    setId: 'demo_set_travel',
    word: 'itinerary',
    pronunciation: '/aɪˈtɪn.ə.rer.i/',
    meaning: 'lịch trình',
    definition: 'A planned route or journey schedule.',
    example: 'Our itinerary includes three cities.',
    collocations: ['travel itinerary', 'change an itinerary'],
    relatedWords: ['schedule', 'route'],
    note: ''
  },
  {
    id: 'demo_word_reservation',
    setId: 'demo_set_travel',
    word: 'reservation',
    pronunciation: '/ˌrez.ɚˈveɪ.ʃən/',
    meaning: 'đặt chỗ',
    definition: 'An arrangement to have a seat, room, or service kept for you.',
    example: 'I made a reservation for two nights.',
    collocations: ['hotel reservation', 'confirm a reservation'],
    relatedWords: ['booking'],
    note: ''
  }
];

const progressByWordId = {
  demo_word_invoice: {
    status: 'REVIEWING',
    repetitions: 2,
    intervalDays: 1,
    nextReviewAt: yesterdayIso,
    lastReviewedAt: yesterdayIso,
    lastQuality: 3
  },
  demo_word_deadline: {
    status: 'NOT_STARTED',
    repetitions: 0,
    intervalDays: 1,
    nextReviewAt: nowIso,
    lastReviewedAt: nowIso,
    lastQuality: 0
  },
  demo_word_proposal: {
    status: 'LEARNING',
    repetitions: 0,
    intervalDays: 1,
    nextReviewAt: nowIso,
    lastReviewedAt: yesterdayIso,
    lastQuality: 1
  },
  demo_word_attend: {
    status: 'MASTERED',
    repetitions: 5,
    intervalDays: 7,
    nextReviewAt: nextWeekIso,
    lastReviewedAt: tomorrowIso,
    lastQuality: 5
  },
  demo_word_itinerary: {
    status: 'NOT_STARTED',
    repetitions: 0,
    intervalDays: 1,
    nextReviewAt: nowIso,
    lastReviewedAt: nowIso,
    lastQuality: 0
  },
  demo_word_reservation: {
    status: 'REVIEWING',
    repetitions: 1,
    intervalDays: 1,
    nextReviewAt: yesterdayIso,
    lastReviewedAt: yesterdayIso,
    lastQuality: 3
  }
};

async function main() {
  console.log('Seeding MinLish demo data...');

  await ensureDemoUser();
  await seedProfile();
  await seedNotificationSettings();
  await seedVocabulary();
  await seedProgress();
  await seedStudyPlan();
  await seedDailyStats();
  await seedQuiz();

  console.log('');
  console.log('Demo data ready.');
  console.log(`Login email: ${demoUser.email}`);
  console.log(`Password: ${demoUser.password}`);
}

async function ensureDemoUser() {
  try {
    await users.get(demoUser.id);
    await users.updateName(demoUser.id, demoUser.name);
    await users.updatePassword(demoUser.id, demoUser.password);
    await users.updateStatus(demoUser.id, true);
    console.log(`✓ User exists: ${demoUser.email}`);
  } catch (error) {
    if (!isNotFound(error)) throw error;
    await users.create(demoUser.id, demoUser.email, undefined, demoUser.password, demoUser.name);
    console.log(`✓ Created user: ${demoUser.email}`);
  }

  await users.updateEmailVerification(demoUser.id, true);
}

async function seedProfile() {
  await upsertDocument(COLLECTIONS.userProfiles, demoUser.id, {
    userId: demoUser.id,
    displayName: demoUser.name,
    email: demoUser.email,
    phone: '0900000000',
    bio: 'Tài khoản demo để test toàn bộ luồng MinLish.',
    nativeLanguage: 'vi',
    targetLanguage: 'en',
    proficiencyLevel: 'beginner',
    studyGoal: 'Ôn từ vựng mỗi ngày và làm quiz thử.',
    dailyTargetMinutes: 20,
    preferredLearningStyle: 'flashcard',
    soundEnabled: true,
    darkModeEnabled: false,
    status: 'active',
    lastLoginAt: nowIso,
    createdAt: nowIso,
    updatedAt: nowIso
  });
}

async function seedNotificationSettings() {
  await upsertDocument(COLLECTIONS.notificationSettings, demoUser.id, {
    userId: demoUser.id,
    reminderTime: '20:00',
    reminderDays: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
    timezone: 'Asia/Ho_Chi_Minh',
    isEnabled: true,
    createdAt: nowIso,
    updatedAt: nowIso
  });
}

async function seedVocabulary() {
  for (const set of sets) {
    const wordCount = words.filter((word) => word.setId === set.id).length;
    await upsertDocument(COLLECTIONS.vocabularySets, set.id, {
      userId: demoUser.id,
      title: set.title,
      description: set.description,
      tags: set.tags,
      wordCount,
      isPublic: set.isPublic,
      createdAt: nowIso,
      updatedAt: nowIso
    });
  }

  for (const word of words) {
    await upsertDocument(COLLECTIONS.vocabularies, word.id, {
      setId: word.setId,
      userId: demoUser.id,
      word: word.word,
      pronunciation: word.pronunciation,
      meaning: word.meaning,
      definition: word.definition,
      example: word.example,
      collocations: word.collocations,
      relatedWords: word.relatedWords,
      note: word.note,
      createdAt: nowIso,
      updatedAt: nowIso
    });
  }
}

async function seedProgress() {
  for (const word of words) {
    const progress = progressByWordId[word.id];
    await upsertDocument(COLLECTIONS.userWordProgress, `demo_progress_${word.id}`, {
      userId: demoUser.id,
      setId: word.setId,
      wordId: word.id,
      status: progress.status,
      boxLevel: 0,
      easinessFactor: 2.5,
      repetitions: progress.repetitions,
      intervalDays: progress.intervalDays,
      nextReviewAt: progress.nextReviewAt,
      lastReviewedAt: progress.lastReviewedAt,
      lastQuality: progress.lastQuality,
      createdAt: nowIso,
      updatedAt: nowIso
    });
  }
}

async function seedStudyPlan() {
  await upsertDocument(COLLECTIONS.studyPlans, 'demo_plan_default', {
    userId: demoUser.id,
    setId: 'demo_set_toeic_basic',
    planName: 'Demo daily plan',
    targetWordsPerDay: 5,
    targetReviewPerDay: 10,
    startDate: today,
    endDate: '2026-12-31',
    isActive: true,
    createdAt: nowIso,
    updatedAt: nowIso
  });
}

async function seedDailyStats() {
  await upsertDocument(COLLECTIONS.dailyLearningStats, `demo_stats_${today}`, {
    userId: demoUser.id,
    date: today,
    wordsLearned: 2,
    wordsReviewed: 3,
    wordsMastered: 1,
    quizCount: 1,
    correctAnswers: 3,
    totalQuestions: 4,
    avgScore: 75,
    studyMinutes: 18,
    createdAt: nowIso,
    updatedAt: nowIso
  });
}

async function seedQuiz() {
  const attemptId = 'demo_quiz_attempt_1';
  await upsertDocument(COLLECTIONS.quizAttempts, attemptId, {
    userId: demoUser.id,
    setId: 'demo_set_toeic_basic',
    quizType: 'multiple_choice',
    status: 'COMPLETED',
    totalQuestions: 4,
    correctAnswers: 3,
    scorePercent: 75,
    durationSeconds: 120,
    startedAt: yesterdayIso,
    completedAt: nowIso,
    createdAt: nowIso,
    updatedAt: nowIso
  });

  const answers = [
    ['demo_answer_invoice', 'demo_word_invoice', 'invoice', 'hóa đơn', true, 5],
    ['demo_answer_deadline', 'demo_word_deadline', 'deadline', 'hạn chót', true, 5],
    ['demo_answer_proposal', 'demo_word_proposal', 'proposal', 'kế hoạch', false, 1],
    ['demo_answer_attend', 'demo_word_attend', 'attend', 'tham dự', true, 5]
  ];

  for (const [id, wordId, questionText, userAnswer, isCorrect, quality] of answers) {
    const word = words.find((item) => item.id === wordId);
    await upsertDocument(COLLECTIONS.quizAnswers, id, {
      attemptId,
      userId: demoUser.id,
      wordId,
      questionType: 'meaning',
      questionText,
      correctAnswer: word?.meaning || '',
      userAnswer,
      isCorrect,
      quality,
      answeredAt: nowIso,
      createdAt: nowIso
    });
  }
}

async function upsertDocument(collectionId, documentId, data) {
  const permissions = ownerDocumentPermissions(demoUser.id);
  try {
    await databases.createDocument(
      config.databaseId,
      collectionId,
      documentId,
      data,
      permissions
    );
    console.log(`✓ Created ${collectionId}/${documentId}`);
  } catch (error) {
    if (!isConflict(error)) throw error;
    await databases.updateDocument(
      config.databaseId,
      collectionId,
      documentId,
      data,
      permissions
    );
    console.log(`✓ Updated ${collectionId}/${documentId}`);
  }
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
