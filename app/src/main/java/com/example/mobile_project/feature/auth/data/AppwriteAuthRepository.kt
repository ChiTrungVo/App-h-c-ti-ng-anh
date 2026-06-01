package com.example.mobile_project.feature.auth.data

import androidx.activity.ComponentActivity
import com.example.mobile_project.core.appwrite.AppwriteClientProvider
import com.example.mobile_project.feature.profile.data.AppwriteProfileRepository
import io.appwrite.ID
import io.appwrite.enums.OAuthProvider
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.User

class AppwriteAuthRepository {
    private companion object {
        const val EmailVerificationRedirectUrl = "https://minlish-email-verify.sgp.appwrite.run"
        const val PasswordRecoveryRedirectUrl = "https://minlish-password-recovery.sgp.appwrite.run"
    }

    private val account get() = AppwriteClientProvider.account
    private val profileRepository = AppwriteProfileRepository()

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
        profileRepository.ensureUserProfile(user)
        return user
    }

    suspend fun register(displayName: String, email: String, password: String): MinLishAuthUser {
        account.create(
            userId = ID.unique(),
            email = email.trim(),
            password = password,
            name = displayName.trim()
        )

        account.createEmailPasswordSession(email.trim(), password)
        val sessionUser = account.get().toMinLishUser().copy(displayName = displayName.trim())
        profileRepository.ensureUserProfile(sessionUser)
        if (!sessionUser.isEmailVerified) {
            sendEmailVerification()
        }
        return sessionUser
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
        profileRepository.ensureUserProfile(user)
        return user
    }

    suspend fun sendEmailVerification() {
        account.createVerification(EmailVerificationRedirectUrl)
    }

    suspend fun completeEmailVerification(userId: String, secret: String): MinLishAuthUser? {
        account.updateVerification(userId, secret)
        val user = currentUser() ?: return null
        profileRepository.ensureUserProfile(user)
        return user
    }

    suspend fun sendPasswordRecovery(email: String) {
        account.createRecovery(email.trim(), PasswordRecoveryRedirectUrl)
    }

    suspend fun completePasswordRecovery(userId: String, secret: String, password: String) {
        account.updateRecovery(userId, secret, password)
    }

    suspend fun updateDisplayName(name: String): MinLishAuthUser {
        return account.updateName(name.trim()).toMinLishUser()
    }

    suspend fun updateEmail(email: String, currentPassword: String): MinLishAuthUser {
        return account.updateEmail(email.trim(), currentPassword).toMinLishUser()
    }

    suspend fun updatePassword(newPassword: String, currentPassword: String) {
        account.updatePassword(newPassword, currentPassword)
    }

    suspend fun softDeleteAccount() {
        profileRepository.softDeleteAccount()
    }
}

private fun User<Map<String, Any>>.toMinLishUser(): MinLishAuthUser {
    return MinLishAuthUser(
        id = id,
        displayName = name.ifBlank { email.substringBefore("@") },
        email = email,
        isEmailVerified = emailVerification
    )
}
