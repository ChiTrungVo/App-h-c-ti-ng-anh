package com.example.mobile_project.feature.profile.data

import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import io.appwrite.Permission
import io.appwrite.Query
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AppwriteNotificationSettingsRepository {
    private val account get() = AppwriteClientProvider.account
    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun getSettings(): NotificationSettings {
        val userId = account.get().id
        return getSettingsOrNull(userId) ?: createDefaultSettings(userId)
    }

    suspend fun saveSettings(form: NotificationSettingsForm): NotificationSettings {
        val userId = account.get().id
        val existing = getSettingsOrNull(userId)
        val now = now()
        val data = mapOf(
            "userId" to userId,
            "reminderTime" to form.reminderTime,
            "reminderDays" to form.reminderDays,
            "timezone" to form.timezone,
            "isEnabled" to form.isEnabled,
            "updatedAt" to now
        )

        val document = if (existing == null) {
            databases.createDocument(
                databaseId = databaseId,
                collectionId = NOTIFICATION_SETTINGS,
                documentId = userId,
                data = data + mapOf("createdAt" to now),
                permissions = ownerPermissions(userId)
            )
        } else {
            databases.updateDocument(
                databaseId = databaseId,
                collectionId = NOTIFICATION_SETTINGS,
                documentId = userId,
                data = data
            )
        }
        return document.toNotificationSettings()
    }

    private suspend fun getSettingsOrNull(userId: String): NotificationSettings? {
        return try {
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = NOTIFICATION_SETTINGS,
                queries = listOf(
                    Query.equal("\$id", userId),
                    Query.limit(1)
                )
            )
            result.documents.firstOrNull()?.toNotificationSettings()
        } catch (error: Exception) {
            null
        }
    }

    private suspend fun createDefaultSettings(userId: String): NotificationSettings {
        val now = now()
        val created = databases.createDocument(
            databaseId = databaseId,
            collectionId = NOTIFICATION_SETTINGS,
            documentId = userId,
            data = mapOf(
                "userId" to userId,
                "reminderTime" to "20:30",
                "reminderDays" to listOf("T2", "T4", "T6", "CN"),
                "timezone" to TimeZone.getDefault().id.ifBlank { "Asia/Ho_Chi_Minh" },
                "isEnabled" to true,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = ownerPermissions(userId)
        )
        return created.toNotificationSettings()
    }

    private fun now(): String = isoFormatter.format(Date())

    private fun ownerPermissions(ownerUserId: String): List<String> = listOf(
        Permission.read(Role.user(ownerUserId)),
        Permission.update(Role.user(ownerUserId)),
        Permission.delete(Role.user(ownerUserId))
    )

    private companion object {
        const val NOTIFICATION_SETTINGS = "notification_settings"
    }
}

private fun Document<Map<String, Any>>.toNotificationSettings(): NotificationSettings {
    val values = data
    return NotificationSettings(
        userId = values.string("userId") ?: id,
        reminderTime = values.string("reminderTime") ?: "20:30",
        reminderDays = values.stringList("reminderDays").ifEmpty { listOf("T2", "T4", "T6", "CN") },
        timezone = values.string("timezone") ?: "Asia/Ho_Chi_Minh",
        isEnabled = values.boolean("isEnabled") ?: true,
        fcmToken = values.string("fcmToken"),
        createdAt = values.string("createdAt"),
        updatedAt = values.string("updatedAt")
    )
}

private fun Map<String, Any>.string(key: String): String? = this[key] as? String

private fun Map<String, Any>.boolean(key: String): Boolean? = this[key] as? Boolean

private fun Map<String, Any>.stringList(key: String): List<String> {
    @Suppress("UNCHECKED_CAST")
    return when (val value = this[key]) {
        is List<*> -> value.filterIsInstance<String>()
        is Array<*> -> value.filterIsInstance<String>()
        else -> emptyList()
    }
}
