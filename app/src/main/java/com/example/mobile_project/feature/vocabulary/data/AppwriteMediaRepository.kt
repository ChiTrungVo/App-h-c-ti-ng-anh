package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.models.InputFile
import java.io.File

class AppwriteMediaRepository {
    private val storage get() = AppwriteClientProvider.storage
    private val bucketId get() = AppwriteClientProvider.mediaBucketId

    suspend fun uploadVocabularyMedia(file: File, ownerUserId: String): String {
        val uploaded = storage.createFile(
            bucketId = bucketId,
            fileId = ID.unique(),
            file = InputFile.fromFile(file),
            permissions = ownerPermissions(ownerUserId)
        )
        return uploaded.id
    }

    suspend fun deleteVocabularyMedia(fileId: String) {
        storage.deleteFile(bucketId = bucketId, fileId = fileId)
    }

    suspend fun downloadVocabularyMedia(fileId: String): ByteArray {
        return storage.getFileView(bucketId = bucketId, fileId = fileId)
    }

    private fun ownerPermissions(ownerUserId: String): List<String> = listOf(
        Permission.read(Role.user(ownerUserId)),
        Permission.update(Role.user(ownerUserId)),
        Permission.delete(Role.user(ownerUserId))
    )
}
