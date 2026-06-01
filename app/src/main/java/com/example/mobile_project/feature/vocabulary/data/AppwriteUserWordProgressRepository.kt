package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.data.model.UserWordProgress
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Repository thao tác với collection "user_word_progress" trên Appwrite.
 *
 * Lưu trạng thái SRS (Spaced Repetition System) cho từng từ của từng người dùng.
 * Thuật toán dựa trên SM-2:
 *   - easeFactor: hệ số dễ dàng (mặc định 2.5)
 *   - repetitionCount: số lần ôn liên tiếp trả lời đúng (quality >= 3)
 *   - intervalDays: khoảng cách ôn lại (ngày)
 *   - nextReviewAt: thời điểm ôn tiếp theo
 *
 * Phân quyền: chỉ chủ sở hữu được truy cập.
 */
class AppwriteUserWordProgressRepository {

    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val account get() = AppwriteClientProvider.account

    companion object {
        private const val COLLECTION_ID = "user_word_progress"

        // SM-2 constants
        const val DEFAULT_EASE_FACTOR = 2.5
        const val MIN_EASE_FACTOR = 1.3

        private val ISO_FORMATTER = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue() =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
        }
    }

    // ------------------------------------------------------------------ //
    //  READ                                                              //
    // ------------------------------------------------------------------ //

    /**
     * Lấy tiến độ học của một từ cụ thể.
     */
    suspend fun getProgress(userId: String, setId: String, wordId: String): UserWordProgress? {
        return try {
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.equal("setId", setId),
                    Query.equal("wordId", wordId),
                    Query.limit(1)
                )
            )
            result.documents.firstOrNull()?.toUserWordProgress()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Lấy tiến độ học của tất cả từ trong một bộ từ.
     */
    suspend fun getProgressForSet(setId: String): List<UserWordProgress> {
        val user = account.get()
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.equal("setId", setId),
                Query.limit(500)
            )
        )
        return result.documents.map { it.toUserWordProgress() }
    }

    /**
     * Lấy danh sách từ cần ôn hôm nay (nextReviewAt <= now).
     * Dùng cho Daily Learning Plan.
     */
    suspend fun getDueWords(setId: String): List<UserWordProgress> {
        val user = account.get()
        val today = nowIso()
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.equal("setId", setId),
                Query.lessThanEqual("nextReviewAt", today),
                Query.orderAsc("nextReviewAt"),
                Query.limit(100)
            )
        )
        return result.documents.map { it.toUserWordProgress() }
    }

    /**
     * Lấy danh sách từ mới chưa học (status = NOT_STARTED).
     * Dùng để gợi ý từ mới trong Daily Learning Plan.
     */
    suspend fun getNewWords(setId: String, limit: Int = 10): List<UserWordProgress> {
        val user = account.get()
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.equal("setId", setId),
                Query.equal("status", "NOT_STARTED"),
                Query.limit(limit)
            )
        )
        return result.documents.map { it.toUserWordProgress() }
    }

    /**
     * Đếm số từ theo trạng thái trong một bộ từ.
     * Trả về map: status → count.
     * Hỗ trợ pagination để xử lý bộ từ có >500 từ.
     */
    suspend fun countByStatus(setId: String): Map<String, Int> {
        val user = account.get()
        val allProgress = mutableListOf<UserWordProgress>()
        var lastDocId: String? = null

        while (true) {
            val queries = mutableListOf(
                Query.equal("userId", user.id),
                Query.equal("setId", setId),
                Query.limit(500)
            )
            lastDocId?.let { queries.add(Query.cursorAfter(it)) }

            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                queries = queries
            )

            val docs = result.documents.map { it.toUserWordProgress() }
            if (docs.isEmpty()) break
            allProgress.addAll(docs)
            lastDocId = docs.last().id

            if (docs.size < 500) break
        }

        return allProgress
            .groupingBy { it.status }
            .eachCount()
    }

    // ------------------------------------------------------------------ //
    //  WRITE                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Tạo bản ghi tiến độ mới cho một từ.
     * Gọi khi từ được thêm vào bộ từ.
     */
    suspend fun createProgress(
        setId: String,
        wordId: String,
        status: String = "NOT_STARTED"
    ): UserWordProgress {
        val user = account.get()
        val now = nowIso()
        val document = databases.createDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = ID.unique(),
            data = mapOf(
                "userId" to user.id,
                "setId" to setId,
                "wordId" to wordId,
                "status" to status,
                "easinessFactor" to DEFAULT_EASE_FACTOR,
                "repetitions" to 0,
                "intervalDays" to 1,
                "nextReviewAt" to now,
                "lastReviewedAt" to now,
                "lastQuality" to 0,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = ownerOnlyPermissions(user.id)
        )
        return document.toUserWordProgress()
    }

    /**
     * Cập nhật tiến độ sau mỗi lượt học/ôn.
     * Áp dụng thuật toán SM-2.
     *
     * @param quality Mức độ ghi nhớ (0-5):
     *   0 = Không nhớ gì (Again)
     *   1 = Nhớ một chút (Hard)
     *   3 = Nhớ được (Good)
     *   5 = Nhớ hoàn hảo (Easy)
     */
    suspend fun updateProgressAfterReview(
        progressId: String,
        quality: Int
    ): UserWordProgress {
        val user = account.get()
        val existing = databases.getDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = progressId
        ).toUserWordProgress()

        val sm2 = calculateSM2(existing, quality)
        val now = nowIso()

        val document = databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = progressId,
            data = mapOf(
                "status" to sm2.status,
                "easinessFactor" to sm2.easeFactor,
                "repetitions" to sm2.repetitionCount,
                "intervalDays" to sm2.intervalDays,
                "nextReviewAt" to sm2.nextReviewAt,
                "lastReviewedAt" to now,
                "lastQuality" to quality,
                "updatedAt" to now
            )
        )
        return document.toUserWordProgress()
    }

    /**
     * Xóa tiến độ của một từ.
     * Gọi khi xóa từ vựng.
     */
    suspend fun deleteProgress(progressId: String) {
        databases.deleteDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = progressId
        )
    }

    /**
     * Xóa tiến độ của một từ cụ thể (theo userId, setId, wordId).
     */
    suspend fun deleteProgressByUserSetWord(userId: String, setId: String, wordId: String) {
        val existing = getProgress(userId, setId, wordId) ?: return
        runCatching {
            databases.deleteDocument(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                documentId = existing.id
            )
        }
    }

    /**
     * Xóa toàn bộ tiến độ trong một bộ từ.
     * Gọi khi xóa bộ từ (cascade).
     * Hỗ trợ pagination để xử lý bộ từ có >500 từ.
     */
    suspend fun deleteAllProgressInSet(setId: String) {
        val user = account.get()
        var lastDocId: String? = null

        while (true) {
            val queries = mutableListOf(
                Query.equal("userId", user.id),
                Query.equal("setId", setId),
                Query.limit(500)
            )
            lastDocId?.let { queries.add(Query.cursorAfter(it)) }

            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                queries = queries
            )

            if (result.documents.isEmpty()) break

            result.documents.forEach { doc ->
                runCatching {
                    databases.deleteDocument(
                        databaseId = databaseId,
                        collectionId = COLLECTION_ID,
                        documentId = doc.id
                    )
                }
            }

            lastDocId = result.documents.last().id
            if (result.documents.size < 500) break
        }
    }

    // ------------------------------------------------------------------ //
    //  SM-2 ALGORITHM                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Tính toán SM-2.
     *
     * quality: 0-5 (0=complete blackout, 1=incorrect but remembered on seeing answer,
     *               3=correct with difficulty, 5=perfect)
     */
    private fun calculateSM2(
        current: UserWordProgress,
        quality: Int
    ): SM2Result {
        val oldEF = current.easinessFactor.let {
            if (it < MIN_EASE_FACTOR) MIN_EASE_FACTOR else it
        }
        val newEF = oldEF + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        val clampedEF = if (newEF < MIN_EASE_FACTOR) MIN_EASE_FACTOR else newEF

        val newRepetitionCount: Int
        val newIntervalDays: Int
        val newStatus: String

        if (quality < 3) {
            // Trả lời sai → reset
            newRepetitionCount = 0
            newIntervalDays = 1
            newStatus = "LEARNING"
        } else {
            newRepetitionCount = current.boxLevel + 1
            newIntervalDays = when (newRepetitionCount) {
                1 -> 1
                2 -> 6
                else -> (current.intervalDays * clampedEF).toInt()
            }
            newStatus = if (newRepetitionCount >= 5) "MASTERED" else "REVIEWING"
        }

        val nextReview = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.DAY_OF_YEAR, newIntervalDays)
        }.time

        return SM2Result(
            status = newStatus,
            easeFactor = clampedEF,
            repetitionCount = newRepetitionCount,
            intervalDays = newIntervalDays,
            nextReviewAt = Companion.ISO_FORMATTER.get().format(nextReview)
        )
    }

    private data class SM2Result(
        val status: String,
        val easeFactor: Double,
        val repetitionCount: Int,
        val intervalDays: Int,
        val nextReviewAt: String
    )

    // ------------------------------------------------------------------ //
    //  HELPERS                                                           //
    // ------------------------------------------------------------------ //

    private fun ownerOnlyPermissions(ownerId: String): List<String> = listOf(
        Permission.read(Role.user(ownerId)),
        Permission.update(Role.user(ownerId)),
        Permission.delete(Role.user(ownerId))
    )

    private fun nowIso(): String = Companion.ISO_FORMATTER.get().format(Date())
}

// ------------------------------------------------------------------ //
//  EXTENSION: Map Appwrite Document → UserWordProgress                //
// ------------------------------------------------------------------ //

@Suppress("UNCHECKED_CAST")
private fun Document<Map<String, Any>>.toUserWordProgress(): UserWordProgress {
    val d = data
    return UserWordProgress(
        id = id,
        userId = d["userId"] as? String ?: "",
        setId = d["setId"] as? String ?: "",
        wordId = d["wordId"] as? String ?: "",
        status = d["status"] as? String ?: "NOT_STARTED",
        boxLevel = (d["boxLevel"] as? Number)?.toInt() ?: 0,
        easinessFactor = (d["easinessFactor"] as? Number)?.toDouble() ?: AppwriteUserWordProgressRepository.DEFAULT_EASE_FACTOR,
        repetitions = (d["repetitions"] as? Number)?.toInt() ?: 0,
        intervalDays = (d["intervalDays"] as? Number)?.toInt() ?: 1,
        nextReviewAt = d["nextReviewAt"] as? String ?: "",
        lastReviewedAt = d["lastReviewedAt"] as? String ?: "",
        lastQuality = (d["lastQuality"] as? Number)?.toInt() ?: -1,
        createdAt = d["createdAt"] as? String ?: "",
        updatedAt = d["updatedAt"] as? String ?: ""
    )
}
