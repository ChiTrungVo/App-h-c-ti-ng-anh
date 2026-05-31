package com.example.mobile_project.feature.profile.data

import android.content.Context
import android.net.Uri
import com.example.mobile_project.BuildConfig
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import io.appwrite.models.InputFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AppwriteProfileRepository {
    private val account get() = AppwriteClientProvider.account
    private val databases get() = AppwriteClientProvider.databases
    private val storage get() = AppwriteClientProvider.storage
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val mediaBucketId get() = AppwriteClientProvider.mediaBucketId
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun getProfile(): UserProfile {
        val user = account.get().toMinLishUser()
        return ensureUserProfile(user)
    }

    suspend fun ensureUserProfile(user: MinLishAuthUser): UserProfile {
        val existing = getProfileOrNull(user.id)
        return if (existing == null) {
            createDefaultProfile(user)
        } else {
            val now = now()
            val updated = databases.updateDocument(
                databaseId = databaseId,
                collectionId = USER_PROFILES,
                documentId = user.id,
                data = mapOf(
                    "displayName" to user.displayName.ifBlank { existing.displayName },
                    "email" to user.email,
                    "lastLoginAt" to now,
                    "updatedAt" to now
                )
            )
            updated.toUserProfile()
        }
    }

    suspend fun updateProfile(form: ProfileEditForm): UserProfile {
        val user = account.get().toMinLishUser()
        val minutes = form.dailyTargetMinutes.toIntOrNull() ?: 0
        val updated = databases.updateDocument(
            databaseId = databaseId,
            collectionId = USER_PROFILES,
            documentId = user.id,
            data = mapOf(
                "displayName" to form.displayName.trim(),
                "phone" to form.phone.trim(),
                "bio" to form.bio.trim(),
                "nativeLanguage" to form.nativeLanguage.trim().ifBlank { "vi" },
                "targetLanguage" to form.targetLanguage.trim().ifBlank { "en" },
                "proficiencyLevel" to form.proficiencyLevel,
                "studyGoal" to form.studyGoal.trim(),
                "dailyTargetMinutes" to minutes,
                "preferredLearningStyle" to form.preferredLearningStyle.trim(),
                "soundEnabled" to form.soundEnabled,
                "darkModeEnabled" to form.darkModeEnabled,
                "updatedAt" to now()
            )
        )
        if (user.displayName != form.displayName.trim()) {
            account.updateName(form.displayName.trim())
        }
        return updated.toUserProfile()
    }

    suspend fun updateAvatar(context: Context, uri: Uri): UserProfile {
        val user = account.get().toMinLishUser()
        val profile = ensureUserProfile(user)
        val tempFile = copyUriToTempFile(context, uri)
        val uploaded = try {
            storage.createFile(
                bucketId = mediaBucketId,
                fileId = ID.unique(),
                file = InputFile.fromFile(tempFile),
                permissions = ownerPermissions(user.id)
            )
        } finally {
            tempFile.delete()
        }
        val avatarUrl = "${BuildConfig.APPWRITE_ENDPOINT}/storage/buckets/$mediaBucketId/files/${uploaded.id}/view?project=${BuildConfig.APPWRITE_PROJECT_ID}"
        val updated = databases.updateDocument(
            databaseId = databaseId,
            collectionId = USER_PROFILES,
            documentId = user.id,
            data = mapOf(
                "avatarUrl" to avatarUrl,
                "avatarFileId" to uploaded.id,
                "updatedAt" to now()
            )
        )
        profile.avatarFileId?.takeIf { it.isNotBlank() && it != uploaded.id }?.let { oldFileId ->
            runCatching { storage.deleteFile(mediaBucketId, oldFileId) }
        }
        return updated.toUserProfile()
    }

    suspend fun downloadAvatar(fileId: String): ByteArray {
        return storage.getFileView(mediaBucketId, fileId)
    }

    suspend fun updateEmail(newEmail: String, currentPassword: String): UserProfile {
        val updatedUser = account.updateEmail(newEmail.trim(), currentPassword).toMinLishUser()
        val updated = databases.updateDocument(
            databaseId = databaseId,
            collectionId = USER_PROFILES,
            documentId = updatedUser.id,
            data = mapOf(
                "email" to updatedUser.email,
                "updatedAt" to now()
            )
        )
        return updated.toUserProfile()
    }

    suspend fun updatePassword(newPassword: String, currentPassword: String) {
        account.updatePassword(newPassword, currentPassword)
    }

    suspend fun softDeleteAccount() {
        val user = account.get().toMinLishUser()
        databases.updateDocument(
            databaseId = databaseId,
            collectionId = USER_PROFILES,
            documentId = user.id,
            data = mapOf(
                "status" to "deleted",
                "updatedAt" to now()
            )
        )
        try {
            account.deleteSession("current")
        } catch (error: AppwriteException) {
            if (error.code != 401) throw error
        }
    }

    private suspend fun getProfileOrNull(userId: String): UserProfile? {
        return try {
            databases.getDocument(
                databaseId = databaseId,
                collectionId = USER_PROFILES,
                documentId = userId
            ).toUserProfile()
        } catch (error: AppwriteException) {
            if (error.code == 404) null else throw error
        }
    }

    private suspend fun createDefaultProfile(user: MinLishAuthUser): UserProfile {
        val now = now()
        val created = databases.createDocument(
            databaseId = databaseId,
            collectionId = USER_PROFILES,
            documentId = user.id,
            data = mapOf(
                "userId" to user.id,
                "displayName" to user.displayName.ifBlank { user.email.substringBefore("@") },
                "email" to user.email,
                "nativeLanguage" to "vi",
                "targetLanguage" to "en",
                "proficiencyLevel" to "beginner",
                "studyGoal" to "Duy trì học từ vựng mỗi ngày",
                "dailyTargetMinutes" to 15,
                "preferredLearningStyle" to "",
                "soundEnabled" to true,
                "darkModeEnabled" to false,
                "status" to "active",
                "lastLoginAt" to now,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = ownerPermissions(user.id)
        )
        return created.toUserProfile()
    }

    private fun copyUriToTempFile(context: Context, uri: Uri): File {
        val extension = when (context.contentResolver.getType(uri)) {
            "image/png" -> ".png"
            "image/webp" -> ".webp"
            "image/gif" -> ".gif"
            else -> ".jpg"
        }
        val tempFile = File.createTempFile("minlish_avatar_", extension, context.cacheDir)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Không thể đọc ảnh đại diện." }
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        return tempFile
    }

    private fun now(): String = isoFormatter.format(Date())

    private fun ownerPermissions(ownerUserId: String): List<String> = listOf(
        Permission.read(Role.user(ownerUserId)),
        Permission.update(Role.user(ownerUserId)),
        Permission.delete(Role.user(ownerUserId))
    )

    private companion object {
        const val USER_PROFILES = "user_profiles"
    }
}

private fun io.appwrite.models.User<Map<String, Any>>.toMinLishUser(): MinLishAuthUser {
    return MinLishAuthUser(
        id = id,
        displayName = name.ifBlank { email.substringBefore("@") },
        email = email,
        isEmailVerified = emailVerification
    )
}

private fun Document<Map<String, Any>>.toUserProfile(): UserProfile {
    val values = data
    return UserProfile(
        userId = values.string("userId") ?: id,
        displayName = values.string("displayName").orEmpty(),
        email = values.string("email").orEmpty(),
        avatarUrl = values.string("avatarUrl"),
        avatarFileId = values.string("avatarFileId"),
        phone = values.string("phone").orEmpty(),
        bio = values.string("bio").orEmpty(),
        nativeLanguage = values.string("nativeLanguage") ?: "vi",
        targetLanguage = values.string("targetLanguage") ?: "en",
        proficiencyLevel = values.string("proficiencyLevel") ?: "beginner",
        studyGoal = values.string("studyGoal") ?: "Duy trì học từ vựng mỗi ngày",
        dailyTargetMinutes = values.int("dailyTargetMinutes") ?: 15,
        preferredLearningStyle = values.string("preferredLearningStyle").orEmpty(),
        soundEnabled = values.boolean("soundEnabled") ?: true,
        darkModeEnabled = values.boolean("darkModeEnabled") ?: false,
        status = values.string("status") ?: "active",
        lastLoginAt = values.string("lastLoginAt"),
        createdAt = values.string("createdAt"),
        updatedAt = values.string("updatedAt")
    )
}

private fun Map<String, Any>.string(key: String): String? = this[key] as? String

private fun Map<String, Any>.boolean(key: String): Boolean? = this[key] as? Boolean

private fun Map<String, Any>.int(key: String): Int? = when (val value = this[key]) {
    is Int -> value
    is Long -> value.toInt()
    is Double -> value.toInt()
    is Number -> value.toInt()
    else -> null
}
