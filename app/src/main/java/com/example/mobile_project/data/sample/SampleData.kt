package com.example.mobile_project.data.sample

import com.example.mobile_project.data.model.DailyLearningStats
import com.example.mobile_project.data.model.NotificationSettings
import com.example.mobile_project.data.model.QuizAttempt
import com.example.mobile_project.data.model.QuizQuestion
import com.example.mobile_project.data.model.UserProfile
import com.example.mobile_project.data.model.UserWordProgress
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.model.VocabularyWord

object SampleData {
    val user = UserProfile(
        userId = "user_demo_001",
        displayName = "Minh Anh",
        email = "minhanh@example.com",
        avatarUrl = null,
        phone = "0901234567",
        bio = "Mình đang học tiếng Anh mỗi ngày để thi IELTS.",
        nativeLanguage = "Tiếng Việt",
        targetLanguage = "Tiếng Anh",
        proficiencyLevel = "Beginner",
        dailyTargetMinutes = 20
    )

    val vocabulary_sets = listOf(
        VocabularySet(
            setId = "set_travel",
            userId = user.userId,
            title = "Travel Essentials",
            description = "Từ vựng cần thiết khi đi du lịch.",
            tags = listOf("Travel", "Daily"),
            wordCount = 24,
            isPublic = false,
            progress = 0.64f,
            status = "Đang học"
        ),
        VocabularySet(
            setId = "set_ocean",
            userId = user.userId,
            title = "Ocean Life",
            description = "Chủ đề đại dương và sinh vật biển.",
            tags = listOf("Nature", "Ocean"),
            wordCount = 18,
            isPublic = true,
            progress = 0.38f,
            status = "Đang học"
        ),
        VocabularySet(
            setId = "set_work",
            userId = user.userId,
            title = "Workplace Basics",
            description = "Giao tiếp cơ bản nơi công sở.",
            tags = listOf("Work", "Speaking"),
            wordCount = 32,
            isPublic = false,
            progress = 1f,
            status = "Đã hoàn thành"
        )
    )

    val vocabularies = listOf(
        VocabularyWord(
            wordId = "word_journey",
            setId = "set_travel",
            userId = user.userId,
            word = "journey",
            pronunciation = "/ˈdʒɜːrni/",
            meaning = "chuyến đi",
            definition = "An act of travelling from one place to another.",
            example = "The journey across the coast was peaceful.",
            collocations = listOf("long journey", "safe journey"),
            note = "Thường dùng trong văn kể chuyện.",
            imageUrl = null
        ),
        VocabularyWord(
            wordId = "word_current",
            setId = "set_ocean",
            userId = user.userId,
            word = "current",
            pronunciation = "/ˈkʌrənt/",
            meaning = "dòng chảy",
            definition = "A continuous movement of water or air.",
            example = "The ocean current carried the boat away.",
            collocations = listOf("strong current", "ocean current"),
            note = "Cũng có nghĩa là hiện tại.",
            imageUrl = null
        ),
        VocabularyWord(
            wordId = "word_meeting",
            setId = "set_work",
            userId = user.userId,
            word = "meeting",
            pronunciation = "/ˈmiːtɪŋ/",
            meaning = "cuộc họp",
            definition = "An occasion when people come together to discuss something.",
            example = "We have a team meeting every Monday.",
            collocations = listOf("team meeting", "weekly meeting"),
            note = "Dùng nhiều trong môi trường công việc.",
            imageUrl = null
        )
    )

//    val user_word_progress = listOf(
//        UserWordProgress(user.userId, "set_travel", "word_journey", "REVIEWING", "STARTED", 1, 2.5),
//        UserWordProgress(user.userId, "set_ocean", "word_current", "LEARNING", "NOT_STARTED", 2, 3.0)
//    )

    val daily_learning_stats = DailyLearningStats(
        userId = user.userId,
        date = "2026-05-31",
        wordsLearned = 12,
        wordsReviewed = 18,
        wordsMastered = 7,
        studyMinutes = 22,
        quizCount = 1,
        correctAnswers = 6,
        totalQuestions = 7,
        avgScore = 86.0
    )

    val quiz_attempts = QuizAttempt(
        attemptId = "attempt_demo_001",
        userId = user.userId,
        setId = "set_travel",
        scorePercent = 86,
        correctAnswers = 6,
        totalQuestions = 7,
        durationSeconds = 245
    )

    val quizQuestions = listOf(
        QuizQuestion(
            questionId = "quiz_001",
            wordId = "word_journey",
            questionText = "Từ nào có nghĩa là chuyến đi?",
            options = listOf("journey", "current", "meeting", "harbor"),
            correctAnswer = "journey",
            selectedAnswer = "journey"
        )
    )

    val notification_settings = NotificationSettings(
        userId = user.userId,
        reminderTime = "20:30",
        reminderDays = listOf("T2", "T4", "T6", "CN"),
        timezone = "Asia/Ho_Chi_Minh",
        isEnabled = true
    )
}
