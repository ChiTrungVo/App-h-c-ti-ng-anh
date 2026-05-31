package com.example.mobile_project.feature.auth.data

import androidx.activity.ComponentActivity
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.enums.OAuthProvider
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AppwriteAuthRepository {
    private val account get() = AppwriteClientProvider.account
    private val databases get() = AppwriteClientProvider.databases
    private val databaseId get() = AppwriteClientProvider.databaseId
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun currentUser(): MinLishAuthUser? {
        return try {
            account.get().toMinLishUser()
        } catch (error: AppwriteException) {
            if (error.code == 401) null else throw error
        }
    }

    suspend fun login(email: String, password: String): MinLishAuthUser {
        account.createEmailPasswordSession(email.trim(), password)
        val user = account.get().toMinLishUser()
        syncProfile(user)
        return user
    }

    suspend fun register(displayName: String, email: String, password: String): MinLishAuthUser {
        val accountUser = account.create(
            userId = ID.unique(),
            email = email.trim(),
            password = password,
            name = displayName.trim()
        ).toMinLishUser()

        account.createEmailPasswordSession(email.trim(), password)
        syncProfile(accountUser.copy(displayName = displayName.trim()))
        return accountUser.copy(displayName = displayName.trim())
    }

    suspend fun loginWithGoogle(activity: ComponentActivity) {
        account.createOAuth2Session(
            activity = activity,
            provider = OAuthProvider.GOOGLE
        )
    }

    suspend fun logout() {
        try {
            account.deleteSession("current")
        } catch (error: AppwriteException) {
            if (error.code != 401) throw error
        }
    }

    suspend fun syncCurrentUserProfile(): MinLishAuthUser? {
        val user = currentUser() ?: return null
        syncProfile(user)
        return user
    }

    private suspend fun syncProfile(user: MinLishAuthUser) {
        val now = isoFormatter.format(Date())
        val permissions = listOf(
            Permission.read(Role.user(user.id)),
            Permission.update(Role.user(user.id)),
            Permission.delete(Role.user(user.id))
        )

        databases.upsertDocument(
            databaseId = databaseId,
            collectionId = "user_profiles",
            documentId = user.id,
            data = mapOf(
                "userId" to user.id,
                "displayName" to user.displayName.ifBlank { user.email.substringBefore("@") },
                "email" to user.email,
                "nativeLanguage" to "vi",
                "targetLanguage" to "en",
                "proficiencyLevel" to "beginner",
                "dailyTargetMinutes" to 15,
                "status" to "active",
                "lastLoginAt" to now,
                "createdAt" to now,
                "updatedAt" to now
            ),
            permissions = permissions
        )
    }
}

private fun User<Map<String, Any>>.toMinLishUser(): MinLishAuthUser {
    return MinLishAuthUser(
        id = id,
        displayName = name.ifBlank { email.substringBefore("@") },
        email = email
    )
}
