package com.example.mobile_project.feature.progress.data.repository

import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.data.model.DailyLearningStats
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.models.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Repository thao tác với collection "daily_learning_stats" trên Appwrite.
 * Theo dõi số từ đã học, đã ôn, mastered và thời gian học trong ngày.
 */
class AppwriteDailyLearningStatsRepository {

    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val account get() = AppwriteClientProvider.account

    companion object {
        private const val COLLECTION_ID = "daily_learning_stats"

        private val DATE_FORMATTER = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() =
                SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
        }
    }

    /**
     * Lấy thống kê của ngày hôm nay. Nếu chưa có thì tạo mới.
     */
    suspend fun getTodayStats(): DailyLearningStats {
        val user = account.get()
        val today = todayDateString()
        
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.equal("date", today),
                Query.limit(1)
            )
        )

        return if (result.documents.isNotEmpty()) {
            result.documents.first().toDailyLearningStats()
        } else {
            createEmptyStats(user.id, today)
        }
    }

    /**
     * Cập nhật số liệu học tập.
     */
    suspend fun incrementStats(
        learnedDelta: Int = 0,
        reviewedDelta: Int = 0,
        masteredDelta: Int = 0,
        minutesDelta: Int = 0
    ): DailyLearningStats {
        val current = getTodayStats()
        
        val document = databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = current.id,
            data = mapOf(
                "learnedWords" to current.learnedWords + learnedDelta,
                "reviewedWords" to current.reviewedWords + reviewedDelta,
                "masteredWords" to current.masteredWords + masteredDelta,
                "studyMinutes" to current.studyMinutes + minutesDelta
            )
        )
        return document.toDailyLearningStats()
    }
    
    suspend fun updateStats(stats: DailyLearningStats, docId: String): DailyLearningStats {
        val document = databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = docId,
            data = mapOf(
                "learnedWords" to stats.learnedWords,
                "reviewedWords" to stats.reviewedWords,
                "masteredWords" to stats.masteredWords,
                "studyMinutes" to stats.studyMinutes,
                "quizAccuracy" to stats.quizAccuracy,
                "streakDays" to stats.streakDays
            )
        )
        return document.toDailyLearningStats()
    }

    private suspend fun createEmptyStats(userId: String, date: String): DailyLearningStats {
        val document = databases.createDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = ID.unique(),
            data = mapOf(
                "userId" to userId,
                "date" to date,
                "learnedWords" to 0,
                "reviewedWords" to 0,
                "masteredWords" to 0,
                "studyMinutes" to 0,
                "quizAccuracy" to 0,
                "streakDays" to 0
            ),
            permissions = listOf(
                Permission.read(Role.user(userId)),
                Permission.update(Role.user(userId)),
                Permission.delete(Role.user(userId))
            )
        )
        return document.toDailyLearningStats()
    }

    private fun todayDateString(): String = DATE_FORMATTER.get()!!.format(Date())

    private fun Document<Map<String, Any>>.toDailyLearningStats(): DailyLearningStats {
        val d = data
        return DailyLearningStats(
            id = id,
            userId = d["userId"] as? String ?: "",
            date = d["date"] as? String ?: "",
            learnedWords = (d["learnedWords"] as? Number)?.toInt() ?: 0,
            reviewedWords = (d["reviewedWords"] as? Number)?.toInt() ?: 0,
            masteredWords = (d["masteredWords"] as? Number)?.toInt() ?: 0,
            studyMinutes = (d["studyMinutes"] as? Number)?.toInt() ?: 0,
            quizAccuracy = (d["quizAccuracy"] as? Number)?.toInt() ?: 0,
            streakDays = (d["streakDays"] as? Number)?.toInt() ?: 0
        )
    }
    suspend fun checkAndResetStreakIfNeeded() {
        val user = account.get()
        val today = todayDateString()
        val yesterday = yesterdayDateString()

        // Tìm document gần nhất không phải hôm nay
        val lastResult = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.notEqual("date", today),
                Query.orderDesc("date"),
                Query.limit(1)
            )
        )

        if (lastResult.documents.isEmpty()) return

        val lastDate = lastResult.documents.first().data["date"] as? String ?: return

        // Nếu ngày cuối cùng học không phải hôm qua → reset streak về 0
        if (lastDate != yesterday) {
            val todayStats = getTodayStats()
            updateStats(
                stats = todayStats.copy(streakDays = 0),
                docId = todayStats.id
            )
        }
    }

    private fun yesterdayDateString(): String {
        val cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(java.util.Calendar.DATE, -1)
        return DATE_FORMATTER.get()!!.format(cal.time)
    }
}
