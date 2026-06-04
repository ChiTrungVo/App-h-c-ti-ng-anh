package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.BuildConfig
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.data.model.VocabularyWord
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.models.InputFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Repository thao tác với collection "vocabularies" trên Appwrite.
 *
 * Mỗi từ vựng là một document, trỏ về bộ từ qua field `setId`.
 * Phân quyền: chỉ chủ sở hữu (theo userId) được đọc/sửa/xóa.
 */
class AppwriteVocabularyWordRepository {

    private val databases get() = AppwriteClientProvider.databases
    private val storage get() = AppwriteClientProvider.storage
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val mediaBucketId get() = AppwriteClientProvider.mediaBucketId
    private val account get() = AppwriteClientProvider.account

    companion object {
        private const val COLLECTION_ID = "vocabularies"

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
     * Lấy tất cả từ vựng trong một bộ từ.
     * Sắp xếp theo createdAt giảm dần (mới nhất trước).
     */
    suspend fun getWordsInSet(setId: String): List<VocabularyWord> {
        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = listOf(
                Query.equal("setId", setId),
                Query.orderDesc("createdAt"),
                Query.limit(500)
            )
        )
        return result.documents.map { it.toVocabularyWord() }
    }

    /**
     * Lấy một từ vựng theo ID.
     */
    suspend fun getWord(wordId: String): VocabularyWord? {
        return try {
            databases.getDocument(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                documentId = wordId
            ).toVocabularyWord()
        } catch (error: AppwriteException) {
            if (error.code == 404) null else throw error
        }
    }

    /**
     * Tìm kiếm từ vựng trong một bộ từ theo từ khóa.
     * Tìm theo field `word` (tiếng Anh) và `meaning` (tiếng Việt).
     * Appwrite chỉ hỗ trợ search trên một field, nên ưu tiên `word`,
     * rồi filter thêm `meaning` ở memory.
     */
    suspend fun searchWords(setId: String, query: String): List<VocabularyWord> {
        val trimmedQuery = query.trim()
        val queries = mutableListOf<String>()
        queries.add(Query.equal("setId", setId))

        if (trimmedQuery.isNotBlank()) {
            queries.add(Query.search("word", trimmedQuery))
        }

        queries.add(Query.orderDesc("createdAt"))
        queries.add(Query.limit(500))

        val result = databases.listDocuments(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            queries = queries
        )

        // Lọc thêm meaning ở memory vì Appwrite chỉ search 1 field
        val docs = result.documents.map { it.toVocabularyWord() }
        return if (trimmedQuery.isBlank()) {
            docs
        } else {
            docs.filter { word ->
                word.word.contains(trimmedQuery, ignoreCase = true) ||
                    word.meaning.contains(trimmedQuery, ignoreCase = true) ||
                    word.definition.contains(trimmedQuery, ignoreCase = true) ||
                    word.example.contains(trimmedQuery, ignoreCase = true) ||
                    word.pronunciation.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  WRITE                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Tạo từ vựng mới.
     * @param isSetPublic nếu true, cấp quyền đọc cho mọi user đã đăng nhập.
     * @return Document ID vừa tạo.
     */
    suspend fun createWord(
        setId: String,
        word: String,
        pronunciation: String,
        meaning: String,
        definition: String,
        example: String,
        collocations: List<String>,
        note: String,
        imageFileId: String? = null,
        isSetPublic: Boolean = false
    ): VocabularyWord {
        val user = account.get()
        val now = nowIso()
        val normalizedWord = word.trim().ifBlank { "new word" }

        val document = databases.createDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = ID.unique(),
            data = mapOf(
                "setId" to setId,
                "userId" to user.id,
                "word" to normalizedWord,
                "pronunciation" to pronunciation.trim(),
                "meaning" to meaning.trim(),
                "definition" to definition.trim(),
                "example" to example.trim(),
                "collocations" to collocations.map { it.trim() }.filter { it.isNotBlank() },
                "note" to note.trim(),
                "imageFileId" to imageFileId,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = wordPermissions(user.id, isSetPublic)
        )
        return document.toVocabularyWord()
    }

    /**
     * Cập nhật từ vựng.
     * @param isSetPublic nếu true, cấp quyền đọc cho mọi user đã đăng nhập.
     */
    suspend fun updateWord(
        wordId: String,
        setId: String,
        word: String,
        pronunciation: String,
        meaning: String,
        definition: String,
        example: String,
        collocations: List<String>,
        note: String,
        imageFileId: String?,
        isSetPublic: Boolean = false
    ): VocabularyWord {
        val user = account.get()
        val document = databases.updateDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = wordId,
            data = mapOf(
                "setId" to setId,
                "userId" to user.id,
                "word" to word.trim(),
                "pronunciation" to pronunciation.trim(),
                "meaning" to meaning.trim(),
                "definition" to definition.trim(),
                "example" to example.trim(),
                "collocations" to collocations.map { it.trim() }.filter { it.isNotBlank() },
                "note" to note.trim(),
                "imageFileId" to imageFileId,
                "updatedAt" to nowIso()
            ),
            permissions = wordPermissions(user.id, isSetPublic)
        )
        return document.toVocabularyWord()
    }

    /**
     * Xóa một từ vựng.
     * Nếu từ có ảnh (imageFileId != null), xóa luôn file trong Storage.
     */
    suspend fun deleteWord(wordId: String) {
        // Lấy thông tin từ trước để biết có ảnh không
        val word = try {
            getWord(wordId)
        } catch (_: Exception) {
            null
        }

        // Xóa document
        databases.deleteDocument(
            databaseId = databaseId,
            collectionId = COLLECTION_ID,
            documentId = wordId
        )

        // Xóa file ảnh trong Storage (nếu có)
        word?.imageUrl?.takeIf { it.isNotBlank() }?.let { url ->
            // imageUrl có dạng: ".../storage/buckets/{bucketId}/files/{fileId}/view?..."
            // Cắt ra fileId từ URL
            val regex = Regex("/files/([a-zA-Z0-9_]+)/")
            regex.find(url)?.groupValues?.get(1)?.let { fileId ->
                runCatching { storage.deleteFile(mediaBucketId, fileId) }
            }
        }
    }

    /**
     * Xóa tất cả từ vựng trong một bộ từ (có pagination).
     * Dùng khi xóa bộ từ (cascade delete).
     * Hỗ trợ bộ từ có >500 từ bằng cách duyệt tuần tự từng trang.
     */
    suspend fun deleteAllWordsInSet(setId: String) {
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

    /**
     * Upload ảnh của từ vựng lên Appwrite Storage.
     * @return URL của ảnh đã upload.
     */
    suspend fun uploadWordImage(file: File, ownerUserId: String): String {
        val uploaded = storage.createFile(
            bucketId = mediaBucketId,
            fileId = ID.unique(),
            file = InputFile.fromFile(file),
            permissions = ownerOnlyPermissions(ownerUserId)
        )
        return "${BuildConfig.APPWRITE_ENDPOINT}/storage/buckets/$mediaBucketId/files/${uploaded.id}/view?project=${BuildConfig.APPWRITE_PROJECT_ID}"
    }

    /**
     * Xóa ảnh trong Storage theo fileId.
     */
    suspend fun deleteWordImage(fileId: String) {
        runCatching { storage.deleteFile(mediaBucketId, fileId) }
    }

    /**
     * Đếm số từ trong một bộ từ.
     */
    suspend fun countWordsInSet(setId: String): Int {
        return try {
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = COLLECTION_ID,
                queries = listOf(
                    Query.equal("setId", setId),
                    Query.limit(1)
                )
            )
            result.total.toInt()
        } catch (_: Exception) {
            0
        }
    }

    // ------------------------------------------------------------------ //
    //  HELPERS                                                           //
    // ------------------------------------------------------------------ //

    private fun ownerOnlyPermissions(ownerId: String): List<String> = listOf(
        Permission.read(Role.user(ownerId)),
        Permission.update(Role.user(ownerId)),
        Permission.delete(Role.user(ownerId))
    )

    private fun wordPermissions(ownerId: String, isPublic: Boolean): List<String> {
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

    private fun nowIso(): String = Companion.ISO_FORMATTER.get().format(Date())
}

// ------------------------------------------------------------------ //
//  EXTENSION: Map Appwrite Document → VocabularyWord                  //
// ------------------------------------------------------------------ //

@Suppress("UNCHECKED_CAST")
private fun Document<Map<String, Any>>.toVocabularyWord(): VocabularyWord {
    val d = data
    return VocabularyWord(
        wordId = id,
        setId = d["setId"] as? String ?: "",
        userId = d["userId"] as? String ?: "",
        word = d["word"] as? String ?: "",
        pronunciation = d["pronunciation"] as? String ?: "",
        meaning = d["meaning"] as? String ?: "",
        definition = d["definition"] as? String ?: "",
        example = d["example"] as? String ?: "",
        collocations = (d["collocations"] as? List<*>)?.mapNotNull { it as? String }
            ?: emptyList(),
        note = d["note"] as? String ?: "",
        imageUrl = d["imageFileId"]?.let { fileId ->
            "${BuildConfig.APPWRITE_ENDPOINT}/storage/buckets/${BuildConfig.APPWRITE_MEDIA_BUCKET_ID}/files/$fileId/view?project=${BuildConfig.APPWRITE_PROJECT_ID}"
        }
    )
}
