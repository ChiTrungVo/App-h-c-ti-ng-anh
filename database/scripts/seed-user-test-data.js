import { Query, Users } from 'node-appwrite';
import {
  client,
  config,
  databases,
  isConflict,
  isNotFound
} from './lib/appwrite-admin.js';
import { COLLECTIONS, ownerDocumentPermissions } from './lib/schema.js';

const users = new Users(client);
const targetEmail = process.argv[2] || process.env.SEED_EMAIL;

if (!targetEmail) {
  console.error('Usage: npm run seed:user -- <email>');
  process.exit(1);
}

const now = new Date();
const nowIso = now.toISOString();
const today = nowIso.slice(0, 10);
const yesterdayIso = new Date(now.getTime() - 24 * 60 * 60 * 1000).toISOString();
const twoDaysAgoIso = new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000).toISOString();
const tomorrowIso = new Date(now.getTime() + 24 * 60 * 60 * 1000).toISOString();
const nextWeekIso = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000).toISOString();

async function main() {
  console.log(`Seeding MinLish test data for ${targetEmail}...`);

  const user = await findUserByEmail(targetEmail);
  const ids = buildIds(user.$id);
  const sets = buildSets(ids);
  const words = buildWords(ids);
  const progress = buildProgress(ids);

  await ensureProfile(user);
  await ensureNotificationSettings(user.$id);
  await seedVocabulary(user.$id, sets, words);
  await seedProgress(user.$id, words, progress);
  await seedStudyPlan(user.$id, ids);
  await seedDailyStats(user.$id, ids);
  await seedQuiz(user.$id, ids, words);

  console.log('');
  console.log('Seed data ready.');
  console.log(`User email: ${targetEmail}`);
  console.log(`User id: ${user.$id}`);
  console.log(`Vocabulary sets: ${sets.length}`);
  console.log(`Vocabulary words: ${words.length}`);
}

async function findUserByEmail(email) {
  const result = await users.list([Query.equal('email', email), Query.limit(1)]);
  const user = result.users?.[0];
  if (!user) {
    throw new Error(`User not found for email: ${email}`);
  }
  console.log(`✓ Found user: ${email}`);
  return user;
}

function buildIds(userId) {
  const slug = userId.replace(/[^A-Za-z0-9_-]/g, '').slice(0, 10);
  const prefix = `u_${slug}`;
  return {
    toeicSet: `${prefix}_toeic`,
    academicSet: `${prefix}_acad`,
    studyPlan: `${prefix}_plan`,
    quizAttempt: `${prefix}_quiz1`,
    dailyStats: `${prefix}_stats_${today.replaceAll('-', '')}`,
    wordIds: Array.from({ length: 12 }, (_, index) => `${prefix}_w${String(index + 1).padStart(2, '0')}`),
    progressIds: Array.from({ length: 12 }, (_, index) => `${prefix}_p${String(index + 1).padStart(2, '0')}`),
    answerIds: Array.from({ length: 5 }, (_, index) => `${prefix}_a${String(index + 1).padStart(2, '0')}`)
  };
}

function buildSets(ids) {
  return [
    {
      id: ids.toeicSet,
      title: 'TOEIC Workplace Essentials',
      description: 'Data thật để test danh sách bộ từ, flashcard, quiz và SRS.',
      tags: ['TOEIC', 'Workplace', 'Seed'],
      isPublic: false
    },
    {
      id: ids.academicSet,
      title: 'Academic Communication',
      description: 'Bộ từ học thuật để test import/export và luồng ôn tập nhiều bộ.',
      tags: ['Academic', 'Communication', 'Seed'],
      isPublic: false
    }
  ];
}

function buildWords(ids) {
  return [
    word(ids.wordIds[0], ids.toeicSet, 'invoice', '/ˈɪn.vɔɪs/', 'hóa đơn', 'A document listing goods or services and the amount to pay.', 'The supplier sent an invoice after delivery.', ['issue an invoice', 'pay an invoice'], ['bill', 'receipt']),
    word(ids.wordIds[1], ids.toeicSet, 'deadline', '/ˈded.laɪn/', 'hạn chót', 'The latest time or date by which work should be completed.', 'The deadline for the report is Friday.', ['meet a deadline', 'miss a deadline'], ['due date', 'schedule']),
    word(ids.wordIds[2], ids.toeicSet, 'proposal', '/prəˈpoʊ.zəl/', 'đề xuất', 'A plan or suggestion presented for consideration.', 'The manager reviewed the proposal carefully.', ['submit a proposal', 'business proposal'], ['plan', 'suggestion']),
    word(ids.wordIds[3], ids.toeicSet, 'attend', '/əˈtend/', 'tham dự', 'To be present at an event or meeting.', 'All employees must attend the training session.', ['attend a meeting', 'attend a seminar'], ['join', 'participate']),
    word(ids.wordIds[4], ids.toeicSet, 'negotiate', '/nəˈɡoʊ.ʃi.eɪt/', 'đàm phán', 'To discuss something in order to reach an agreement.', 'The teams negotiated a new contract.', ['negotiate a contract', 'negotiate terms'], ['bargain', 'discuss']),
    word(ids.wordIds[5], ids.toeicSet, 'shipment', '/ˈʃɪp.mənt/', 'lô hàng', 'A quantity of goods sent together.', 'The shipment arrived two days early.', ['track a shipment', 'delayed shipment'], ['delivery', 'cargo']),
    word(ids.wordIds[6], ids.academicSet, 'analyze', '/ˈæn.əl.aɪz/', 'phân tích', 'To examine something carefully to understand it.', 'Students analyze the article before discussion.', ['analyze data', 'analyze a text'], ['examine', 'study']),
    word(ids.wordIds[7], ids.academicSet, 'evidence', '/ˈev.ə.dəns/', 'bằng chứng', 'Facts or information showing whether something is true.', 'The essay needs stronger evidence.', ['provide evidence', 'supporting evidence'], ['proof', 'support']),
    word(ids.wordIds[8], ids.academicSet, 'context', '/ˈkɑːn.tekst/', 'ngữ cảnh', 'The situation in which something happens or exists.', 'The meaning changes depending on the context.', ['historical context', 'social context'], ['background', 'setting']),
    word(ids.wordIds[9], ids.academicSet, 'summarize', '/ˈsʌm.ə.raɪz/', 'tóm tắt', 'To express the main ideas briefly.', 'Please summarize the lecture in one paragraph.', ['summarize an article', 'briefly summarize'], ['recap', 'outline']),
    word(ids.wordIds[10], ids.academicSet, 'compare', '/kəmˈper/', 'so sánh', 'To examine similarities and differences.', 'Compare the two research methods.', ['compare results', 'compare ideas'], ['contrast', 'evaluate']),
    word(ids.wordIds[11], ids.academicSet, 'conclusion', '/kənˈkluː.ʒən/', 'kết luận', 'A final decision or idea based on information.', 'The conclusion summarizes the main findings.', ['draw a conclusion', 'final conclusion'], ['result', 'judgment'])
  ];
}

function word(id, setId, text, pronunciation, meaning, definition, example, collocations, relatedWords) {
  return {
    id,
    setId,
    word: text,
    pronunciation,
    meaning,
    definition,
    example,
    collocations,
    relatedWords,
    note: 'Seed data for MinLish QA.'
  };
}

function buildProgress(ids) {
  return {
    [ids.wordIds[0]]: review('REVIEWING', 2, 1, yesterdayIso, yesterdayIso, 3),
    [ids.wordIds[1]]: review('NOT_STARTED', 0, 1, nowIso, nowIso, 0),
    [ids.wordIds[2]]: review('LEARNING', 0, 1, nowIso, yesterdayIso, 1),
    [ids.wordIds[3]]: review('MASTERED', 5, 7, nextWeekIso, yesterdayIso, 5),
    [ids.wordIds[4]]: review('REVIEWING', 1, 1, twoDaysAgoIso, twoDaysAgoIso, 3),
    [ids.wordIds[5]]: review('NOT_STARTED', 0, 1, nowIso, nowIso, 0),
    [ids.wordIds[6]]: review('NOT_STARTED', 0, 1, nowIso, nowIso, 0),
    [ids.wordIds[7]]: review('REVIEWING', 2, 1, yesterdayIso, yesterdayIso, 3),
    [ids.wordIds[8]]: review('LEARNING', 1, 1, nowIso, yesterdayIso, 2),
    [ids.wordIds[9]]: review('NOT_STARTED', 0, 1, nowIso, nowIso, 0),
    [ids.wordIds[10]]: review('REVIEWING', 1, 1, yesterdayIso, yesterdayIso, 3),
    [ids.wordIds[11]]: review('MASTERED', 4, 7, nextWeekIso, yesterdayIso, 5)
  };
}

function review(status, repetitions, intervalDays, nextReviewAt, lastReviewedAt, lastQuality) {
  return {
    status,
    repetitions,
    intervalDays,
    nextReviewAt,
    lastReviewedAt,
    lastQuality
  };
}

async function ensureProfile(user) {
  const existing = await getDocumentOrNull(COLLECTIONS.userProfiles, user.$id);
  const displayName = user.name || targetEmail.split('@')[0];
  const base = {
    userId: user.$id,
    displayName,
    email: targetEmail,
    nativeLanguage: 'vi',
    targetLanguage: 'en',
    proficiencyLevel: 'intermediate',
    studyGoal: 'Test đầy đủ luồng học từ vựng, flashcard, quiz và tiến độ.',
    dailyTargetMinutes: 25,
    preferredLearningStyle: 'flashcard',
    soundEnabled: true,
    darkModeEnabled: false,
    status: 'active',
    lastLoginAt: nowIso,
    updatedAt: nowIso
  };

  if (existing) {
    await updateDocument(COLLECTIONS.userProfiles, user.$id, base);
    console.log(`✓ Updated ${COLLECTIONS.userProfiles}/${user.$id}`);
    return;
  }

  await createDocument(COLLECTIONS.userProfiles, user.$id, {
    ...base,
    phone: '',
    bio: 'Tài khoản test MinLish.',
    createdAt: nowIso
  }, user.$id);
}

async function ensureNotificationSettings(userId) {
  await upsertDocument(COLLECTIONS.notificationSettings, userId, {
    userId,
    reminderTime: '20:30',
    reminderDays: ['T2', 'T3', 'T4', 'T5', 'T6'],
    timezone: 'Asia/Ho_Chi_Minh',
    isEnabled: true,
    createdAt: nowIso,
    updatedAt: nowIso
  }, userId);
}

async function seedVocabulary(userId, sets, words) {
  for (const set of sets) {
    const wordCount = words.filter((item) => item.setId === set.id).length;
    await upsertDocument(COLLECTIONS.vocabularySets, set.id, {
      userId,
      title: set.title,
      description: set.description,
      tags: set.tags,
      wordCount,
      isPublic: set.isPublic,
      createdAt: nowIso,
      updatedAt: nowIso
    }, userId);
  }

  for (const item of words) {
    await upsertDocument(COLLECTIONS.vocabularies, item.id, {
      setId: item.setId,
      userId,
      word: item.word,
      pronunciation: item.pronunciation,
      meaning: item.meaning,
      definition: item.definition,
      example: item.example,
      collocations: item.collocations,
      relatedWords: item.relatedWords,
      note: item.note,
      createdAt: nowIso,
      updatedAt: nowIso
    }, userId);
  }
}

async function seedProgress(userId, words, progressByWordId) {
  for (const [index, item] of words.entries()) {
    const progress = progressByWordId[item.id];
    await upsertDocument(COLLECTIONS.userWordProgress, buildIds(userId).progressIds[index], {
      userId,
      setId: item.setId,
      wordId: item.id,
      status: progress.status,
      boxLevel: progress.status === 'MASTERED' ? 5 : progress.repetitions,
      easinessFactor: 2.5,
      repetitions: progress.repetitions,
      intervalDays: progress.intervalDays,
      nextReviewAt: progress.nextReviewAt,
      lastReviewedAt: progress.lastReviewedAt,
      lastQuality: progress.lastQuality,
      createdAt: nowIso,
      updatedAt: nowIso
    }, userId);
  }
}

async function seedStudyPlan(userId, ids) {
  await upsertDocument(COLLECTIONS.studyPlans, ids.studyPlan, {
    userId,
    setId: ids.toeicSet,
    planName: 'Workplace vocabulary daily plan',
    targetWordsPerDay: 6,
    targetReviewPerDay: 12,
    startDate: today,
    endDate: '2026-12-31',
    isActive: true,
    createdAt: nowIso,
    updatedAt: nowIso
  }, userId);
}

async function seedDailyStats(userId, ids) {
  const existing = await findDocument(COLLECTIONS.dailyLearningStats, [
    Query.equal('userId', userId),
    Query.equal('date', today),
    Query.limit(1)
  ]);
  const documentId = existing?.$id || ids.dailyStats;
  const data = {
    userId,
    date: today,
    wordsLearned: 5,
    wordsReviewed: 7,
    wordsMastered: 2,
    quizCount: 2,
    correctAnswers: 8,
    totalQuestions: 10,
    avgScore: 80,
    studyMinutes: 22,
    createdAt: existing?.createdAt || nowIso,
    updatedAt: nowIso
  };

  if (existing) {
    await updateDocument(COLLECTIONS.dailyLearningStats, documentId, data);
    console.log(`✓ Updated ${COLLECTIONS.dailyLearningStats}/${documentId}`);
  } else {
    await createDocument(COLLECTIONS.dailyLearningStats, documentId, data, userId);
  }
}

async function seedQuiz(userId, ids, words) {
  await upsertDocument(COLLECTIONS.quizAttempts, ids.quizAttempt, {
    userId,
    setId: ids.toeicSet,
    quizType: 'multiple_choice',
    status: 'COMPLETED',
    totalQuestions: 5,
    correctAnswers: 4,
    scorePercent: 80,
    durationSeconds: 165,
    startedAt: yesterdayIso,
    completedAt: nowIso,
    createdAt: nowIso,
    updatedAt: nowIso
  }, userId);

  const answers = [
    [ids.answerIds[0], words[0], 'invoice', 'hóa đơn', true, 5],
    [ids.answerIds[1], words[1], 'deadline', 'hạn chót', true, 5],
    [ids.answerIds[2], words[2], 'proposal', 'kế hoạch', false, 1],
    [ids.answerIds[3], words[4], 'negotiate', 'đàm phán', true, 5],
    [ids.answerIds[4], words[5], 'shipment', 'lô hàng', true, 5]
  ];

  for (const [id, item, questionText, userAnswer, isCorrect, quality] of answers) {
    await upsertDocument(COLLECTIONS.quizAnswers, id, {
      attemptId: ids.quizAttempt,
      userId,
      wordId: item.id,
      questionType: 'meaning',
      questionText,
      correctAnswer: item.meaning,
      userAnswer,
      isCorrect,
      quality,
      answeredAt: nowIso,
      createdAt: nowIso
    }, userId);
  }
}

async function upsertDocument(collectionId, documentId, data, userId) {
  try {
    await createDocument(collectionId, documentId, data, userId);
  } catch (error) {
    if (!isConflict(error)) throw error;
    await updateDocument(collectionId, documentId, data);
    console.log(`✓ Updated ${collectionId}/${documentId}`);
  }
}

async function createDocument(collectionId, documentId, data, userId) {
  await databases.createDocument(
    config.databaseId,
    collectionId,
    documentId,
    data,
    ownerDocumentPermissions(userId)
  );
  console.log(`✓ Created ${collectionId}/${documentId}`);
}

async function updateDocument(collectionId, documentId, data) {
  await databases.updateDocument(
    config.databaseId,
    collectionId,
    documentId,
    data
  );
}

async function getDocumentOrNull(collectionId, documentId) {
  try {
    return await databases.getDocument(config.databaseId, collectionId, documentId);
  } catch (error) {
    if (isNotFound(error)) return null;
    throw error;
  }
}

async function findDocument(collectionId, queries) {
  const result = await databases.listDocuments(config.databaseId, collectionId, queries);
  return result.documents[0] || null;
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
