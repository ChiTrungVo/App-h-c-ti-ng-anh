package com.example.mobile_project.data.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val phone: String,
    val bio: String,
    val nativeLanguage: String,
    val targetLanguage: String,
    val proficiencyLevel: String,
    val dailyTargetMinutes: Int
)

data class VocabularySet(
    val setId: String,
    val userId: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val wordCount: Int,
    val isPublic: Boolean,
    val progress: Float,
    val status: String
)

data class VocabularyWord(
    val wordId: String,
    val setId: String,
    val userId: String,
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val definition: String,
    val example: String,
    val collocations: List<String>,
    val note: String,
    val imageUrl: String?
)

data class UserWordProgress(
    val id: String,
    val userId: String,
    val setId: String,
    val wordId: String,
    val status: String,
    val boxLevel: Int,
    val easinessFactor: Double,
    val repetitions: Int,
    val intervalDays: Int,
    val nextReviewAt: String,
    val lastReviewedAt: String,
    val lastQuality: Int,
    val createdAt: String,
    val updatedAt: String
)

data class DailyLearningStats(
    val userId: String,
    val date: String,
    val learnedWords: Int,
    val reviewedWords: Int,
    val masteredWords: Int,
    val studyMinutes: Int,
    val quizAccuracy: Int,
    val streakDays: Int
)

data class QuizAttempt(
    val attemptId: String,
    val userId: String,
    val setId: String,
    val scorePercent: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val durationSeconds: Int
)

data class QuizQuestion(
    val questionId: String,
    val wordId: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val selectedAnswer: String?
)

data class NotificationSettings(
    val userId: String,
    val reminderTime: String,
    val reminderDays: List<String>,
    val timezone: String,
    val isEnabled: Boolean
)
