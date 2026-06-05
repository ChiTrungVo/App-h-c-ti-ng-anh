package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.data.model.VocabularySet
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Repository thao tác với collection "vocabulary_sets" trên Appwrite.
 *
 * Mỗi bộ từ vưu là một document.
 * Phân quyền:
 *   - Chủ sở hữu: read, update, delete
 *   - Người khác: read (nếu isPublic == true)
 */
class AppwriteVocabularySetRepository {

    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val account get() = AppwriteClientProvider.account

    companion object {
        private const val COLLECTION_ID = "vocabulary_sets"

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
     * Lấy tất cả bộ từ của người dùng hiện tại.
     * Sắp xếp mới nhất trước (theo createdAt giảm dần).
     */
    suspend fun getMySets(): List<VocabularySet> {
        val user = account.get()
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("userId", user.id),
                Query.orderDesc("createdAt"),
                Query.limit(100)
            )
        )
        return result.documents.map { it.toVocabularySet() }
    }

    /**
     * Lấy một bộ từ theo ID.
     * Trả về null nếu không tìm thấy.
     */
    suspend fun getSet(setId: String): VocabularySet? {
        return try {
            databases.getDocument(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                documentId = setId
            ).toVocabularySet()
        } catch (error: AppwriteException) {
            if (error.code == 404) null else throw error
        }
    }

    /**
     * Tìm kiếm bộ từ theo từ khóa (title hoặc description)
     * và lọc theo tag (nếu có).
     * Chỉ tìm trong bộ từ của người dùng hiện tại.
     */
    suspend fun searchSets(query: String?, tag: String?): List<VocabularySet> {
        val user = account.get()
        val queries = mutableListOf<String>()
        queries.add(Query.equal("userId", user.id))

        query?.takeIf { it.isNotBlank() }?.let {
            queries.add(Query.search("title", it.trim()))
        }

        tag?.takeIf { it.isNotBlank() && it != "Tất cả" }?.let {
            queries.add(Query.equal("tags", it.trim()))
        }

        queries.add(Query.orderDesc("createdAt"))
        queries.add(Query.limit(100))

        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = queries
        )
        return result.documents.map { it.toVocabularySet() }
    }

    // ------------------------------------------------------------------ //
    //  WRITE                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Tạo bộ từ mới.
     * @return Document ID vừa tạo.
     */
    suspend fun createSet(
        title: String,
        description: String,
        tags: List<String>,
        isPublic: Boolean
    ): VocabularySet {
        val user = account.get()
        val now = nowIso()
        val trimmedTitle = title.trim().ifBlank { "Bộ từ mới" }

        val document = databases.createDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = ID.unique(),
            data = mapOf(
                "userId" to user.id,
                "title" to trimmedTitle,
                "description" to description.trim(),
                "tags" to tags.map { it.trim() }.filter { it.isNotBlank() },
                "wordCount" to 0,
                "isPublic" to isPublic,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = setOwnerAndPublicPermissions(user.id, isPublic)
        )
        return document.toVocabularySet()
    }

    /**
     * Cập nhật bộ từ.
     */
    suspend fun updateSet(
        setId: String,
        title: String,
        description: String,
        tags: List<String>,
        isPublic: Boolean
    ): VocabularySet {
        val user = account.get()
        val document = databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = setId,
            data = mapOf(
                "title" to title.trim(),
                "description" to description.trim(),
                "tags" to tags.map { it.trim() }.filter { it.isNotBlank() },
                "isPublic" to isPublic,
                "updatedAt" to nowIso()
            ),
            permissions = setOwnerAndPublicPermissions(user.id, isPublic)
        )
        return document.toVocabularySet()
    }

    /**
     * Xóa bộ từ.
     * Lưu ý: cần xóa luôn các từ vựng liên quan (vocabularies collection).
     * Việc này nên được xử lý ở use-case hoặc ViewModel layer.
     */
    suspend fun deleteSet(setId: String) {
        databases.deleteDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = setId
        )
    }

    /**
     * Cập nhật wordCount sau khi thêm/xóa từ vựng.
     */
    suspend fun updateWordCount(setId: String, count: Int) {
        databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = setId,
            data = mapOf(
                "wordCount" to count,
                "updatedAt" to nowIso()
            )
        )
    }

    // ------------------------------------------------------------------ //
    //  HELPERS                                                           //
    // ------------------------------------------------------------------ //

    /**
     * Quyền truy cập:
     * - Owner: read, update, delete
     * - Mọi user đã đăng nhập: read (nếu isPublic == true)
     */
    private fun setOwnerAndPublicPermissions(
        ownerId: String,
        isPublic: Boolean
    ): List<String> {
        val perms = mutableListOf(
            Permission.read(Role.user(ownerId)),
            Permission.update(Role.user(ownerId)),
            Permission.delete(Role.user(ownerId))
        )
        if (isPublic) {
            perms.add(Permission.read(Role.users()))
        }
        return perms
    }

    private fun nowIso(): String =
        Companion.ISO_FORMATTER.get()!!.format(Date())
    // Lấy tất cả bộ từ public của cộng đồng
    suspend fun getPublicSets(): List<VocabularySet> {
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("isPublic", true),
                Query.limit(50)
            )
        )
        return result.documents.map { it.toVocabularySet() }
    }

    // Sao chép bộ từ của người khác về tài khoản mình
    suspend fun forkSet(setId: String) {
        val user = account.get()
        val original = databases.getDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = setId
        ).toVocabularySet()

        databases.createDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = ID.unique(),
            data = mapOf(
                "userId" to user.id,
                "title" to "${original.title} (copy)",
                "description" to original.description,
                "tags" to original.tags,
                "isPublic" to false
            ),
            permissions = listOf(
                Permission.read(Role.user(user.id)),
                Permission.update(Role.user(user.id)),
                Permission.delete(Role.user(user.id))
            )
        )
    }
}

// ------------------------------------------------------------------ //
//  EXTENSION: Map Appwrite Document → VocabularySet                  //
// ------------------------------------------------------------------ //

@Suppress("UNCHECKED_CAST")
private fun Document<Map<String, Any>>.toVocabularySet(): VocabularySet {
    val d = data
    return VocabularySet(
        setId = id,
        userId = d["userId"] as? String ?: "",
        title = d["title"] as? String ?: "",
        description = d["description"] as? String ?: "",
        tags = (d["tags"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        wordCount = (d["wordCount"] as? Number)?.toInt() ?: 0,
        isPublic = d["isPublic"] as? Boolean ?: false,
        progress = (d["progress"] as? Number)?.toFloat() ?: 0f,
        status = d["status"] as? String ?: "Đang học"
    )
}
