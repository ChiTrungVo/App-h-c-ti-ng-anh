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
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val wordsLearned: Int = 0,
    val wordsReviewed: Int = 0,
    val wordsMastered: Int = 0,
    val quizCount: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0,
    val avgScore: Double = 0.0,
    val studyMinutes: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
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
