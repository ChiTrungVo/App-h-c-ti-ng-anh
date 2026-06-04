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
                "wordsLearned" to current.wordsLearned + learnedDelta,
                "wordsReviewed" to current.wordsReviewed + reviewedDelta,
                "wordsMastered" to current.wordsMastered + masteredDelta,
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
                "wordsLearned" to stats.wordsLearned,
                "wordsReviewed" to stats.wordsReviewed,
                "wordsMastered" to stats.wordsMastered,
                "studyMinutes" to stats.studyMinutes,
                "quizCount" to stats.quizCount,
                "correctAnswers" to stats.correctAnswers,
                "totalQuestions" to stats.totalQuestions,
                "avgScore" to stats.avgScore
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
                "wordsLearned" to 0,
                "wordsReviewed" to 0,
                "wordsMastered" to 0,
                "quizCount" to 0,
                "correctAnswers" to 0,
                "totalQuestions" to 0,
                "avgScore" to 0.0,
                "studyMinutes" to 0
            ),
            permissions = listOf(
                Permission.read(Role.user(userId)),
                Permission.update(Role.user(userId)),
                Permission.delete(Role.user(userId))
            )
        )
        return document.toDailyLearningStats()
    }

    suspend fun calculateStreak(): Int {
        val user = account.get()
        val today = todayDateString()
        val yesterday = previousDay(today)

        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.orderDesc("date"),
                Query.limit(30)
            )
        )

        val dates = result.documents
            .filter { doc ->
                val wordsLearned = (doc.data["wordsLearned"] as? Number)?.toInt() ?: 0
                val wordsReviewed = (doc.data["wordsReviewed"] as? Number)?.toInt() ?: 0
                val quizCount = (doc.data["quizCount"] as? Number)?.toInt() ?: 0
                val studyMinutes = (doc.data["studyMinutes"] as? Number)?.toInt() ?: 0
                wordsLearned > 0 || wordsReviewed > 0 || quizCount > 0 || studyMinutes > 0
            }
            .mapNotNull { it.data["date"] as? String }
            .toSortedSet(reverseOrder())

        if (dates.isEmpty()) return 0

        val mostRecent = dates.first()
        if (mostRecent != today && mostRecent != yesterday) return 0

        var streak = 0
        var checking = mostRecent
        for (date in dates) {
            if (date == checking) {
                streak++
                checking = previousDay(checking)
            } else {
                break
            }
        }
        return streak
    }

    private fun todayDateString(): String = DATE_FORMATTER.get()!!.format(Date())

    private fun previousDay(dateStr: String): String {
        val cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.time = DATE_FORMATTER.get()!!.parse(dateStr)!!
        cal.add(java.util.Calendar.DATE, -1)
        return DATE_FORMATTER.get()!!.format(cal.time)
    }

    private fun Document<Map<String, Any>>.toDailyLearningStats(): DailyLearningStats {
        val d = data
        return DailyLearningStats(
            id = id,
            userId = d["userId"] as? String ?: "",
            date = d["date"] as? String ?: "",
            wordsLearned = (d["wordsLearned"] as? Number)?.toInt() ?: 0,
            wordsReviewed = (d["wordsReviewed"] as? Number)?.toInt() ?: 0,
            wordsMastered = (d["wordsMastered"] as? Number)?.toInt() ?: 0,
            quizCount = (d["quizCount"] as? Number)?.toInt() ?: 0,
            correctAnswers = (d["correctAnswers"] as? Number)?.toInt() ?: 0,
            totalQuestions = (d["totalQuestions"] as? Number)?.toInt() ?: 0,
            avgScore = (d["avgScore"] as? Number)?.toDouble() ?: 0.0,
            studyMinutes = (d["studyMinutes"] as? Number)?.toInt() ?: 0,
            createdAt = d["createdAt"] as? String ?: "",
            updatedAt = d["updatedAt"] as? String ?: ""
        )
    }
}
