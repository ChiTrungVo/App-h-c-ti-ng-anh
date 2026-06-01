package com.example.mobile_project.feature.profile.data

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
    val avatarFileId: String? = null,
    val phone: String = "",
    val bio: String = "",
    val nativeLanguage: String = "vi",
    val targetLanguage: String = "en",
    val proficiencyLevel: String = "beginner",
    val studyGoal: String = "Duy trì học từ vựng mỗi ngày",
    val dailyTargetMinutes: Int = 15,
    val preferredLearningStyle: String = "",
    val soundEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val status: String = "active",
    val lastLoginAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class ProfileEditForm(
    val displayName: String = "",
    val phone: String = "",
    val bio: String = "",
    val nativeLanguage: String = "vi",
    val targetLanguage: String = "en",
    val proficiencyLevel: String = "beginner",
    val studyGoal: String = "",
    val dailyTargetMinutes: String = "15",
    val preferredLearningStyle: String = "",
    val soundEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false
) {
    companion object {
        fun fromProfile(profile: UserProfile): ProfileEditForm = ProfileEditForm(
            displayName = profile.displayName,
            phone = profile.phone,
            bio = profile.bio,
            nativeLanguage = profile.nativeLanguage,
            targetLanguage = profile.targetLanguage,
            proficiencyLevel = profile.proficiencyLevel,
            studyGoal = profile.studyGoal,
            dailyTargetMinutes = profile.dailyTargetMinutes.toString(),
            preferredLearningStyle = profile.preferredLearningStyle,
            soundEnabled = profile.soundEnabled,
            darkModeEnabled = profile.darkModeEnabled
        )
    }
}

data class AccountSecurityForm(
    val newEmail: String = "",
    val currentPasswordForEmail: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = ""
)
