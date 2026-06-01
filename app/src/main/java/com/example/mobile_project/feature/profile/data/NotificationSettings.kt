package com.example.mobile_project.feature.profile.data

data class NotificationSettings(
    val userId: String,
    val reminderTime: String = "20:30",
    val reminderDays: List<String> = listOf("T2", "T4", "T6", "CN"),
    val timezone: String = "Asia/Ho_Chi_Minh",
    val isEnabled: Boolean = true,
    val fcmToken: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class NotificationSettingsForm(
    val reminderTime: String = "20:30",
    val reminderDays: List<String> = listOf("T2", "T4", "T6", "CN"),
    val timezone: String = "Asia/Ho_Chi_Minh",
    val isEnabled: Boolean = true
) {
    companion object {
        fun fromSettings(settings: NotificationSettings): NotificationSettingsForm =
            NotificationSettingsForm(
                reminderTime = settings.reminderTime,
                reminderDays = settings.reminderDays,
                timezone = settings.timezone,
                isEnabled = settings.isEnabled
            )
    }
}
