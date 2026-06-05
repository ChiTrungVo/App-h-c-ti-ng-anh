package com.example.mobile_project.feature.progress.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role

class AppwriteProgressRepository {
    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val account get() = AppwriteClientProvider.account
    private val vocabularySetRepository = AppwriteVocabularySetRepository()

    suspend fun getMySets(): List<VocabularySet> {
        return vocabularySetRepository.getMySets() // ← dùng lại
    }

    suspend fun getWordCount(setId: String): Int {
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = "vocabularies",
            queries = listOf(
                Query.equal("setId", setId),
                Query.limit(1)
            )
        )
        return result.total.toInt()
    }

    suspend fun getQuizResults(): Map<String, Pair<Int, Int>> {
        val user = account.get()
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = "quiz_attempts",
            queries = listOf(Query.equal("userId", user.id))
        )
        return result.documents.associate { doc ->
            val setId = doc.data["setId"] as? String ?: ""
            val correct = (doc.data["correctAnswers"] as? Number)?.toInt() ?: 0
            val total = (doc.data["totalQuestions"] as? Number)?.toInt() ?: 0
            setId to Pair(correct, total)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveQuizResult(setId: String, correctCount: Int, totalCount: Int) {
        val user = account.get()
        val now = java.time.Instant.now().toString()
        databases.createDocument(
            databaseId = databaseId,
            collectionId = "quiz_attempts",
            documentId = ID.unique(),
            data = mapOf(
                "userId" to user.id,
                "setId" to setId,
                "totalQuestions" to totalCount,
                "correctAnswers" to correctCount,
                "scorePercent" to if (totalCount == 0) 0.0 else correctCount.toDouble() / totalCount * 100,
                "quizType" to "multiple_choice",
                "status" to "COMPLETED",
                "createdAt" to now,
                "completedAt" to now
            ),
            permissions = listOf(
                Permission.read(Role.user(user.id)),
                Permission.update(Role.user(user.id)),
                Permission.delete(Role.user(user.id))
            )
        )
    }
}