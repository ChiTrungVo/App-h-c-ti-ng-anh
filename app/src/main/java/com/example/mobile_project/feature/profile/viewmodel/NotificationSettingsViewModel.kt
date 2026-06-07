package com.example.mobile_project.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.feature.auth.data.toVietnameseAuthMessage
import com.example.mobile_project.feature.profile.data.AppwriteNotificationSettingsRepository
import com.example.mobile_project.feature.profile.data.NotificationSettings
import com.example.mobile_project.feature.profile.data.NotificationSettingsForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobile_project.feature.notification.worker.NotificationHelper
import com.example.mobile_project.feature.notification.worker.ReminderScheduler


data class NotificationSettingsUiState(
    val settings: NotificationSettings? = null,
    val form: NotificationSettingsForm = NotificationSettingsForm(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class NotificationSettingsViewModel(
    application: Application,
    private val repository: AppwriteNotificationSettingsRepository = AppwriteNotificationSettingsRepository()
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    private val context get() = getApplication<Application>().applicationContext
    fun loadSettings(force: Boolean = false) {
        if (!force && (_uiState.value.isLoading || _uiState.value.settings != null)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getSettings() }
                .onSuccess { settings ->
                    _uiState.update {
                        it.copy(
                            settings = settings,
                            form = NotificationSettingsForm.fromSettings(settings),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun updateForm(transform: (NotificationSettingsForm) -> NotificationSettingsForm) {
        _uiState.update { it.copy(form = transform(it.form), errorMessage = null, infoMessage = null) }
    }

    fun saveSettings(onSaved: () -> Unit = {}) {
        val form = _uiState.value.form
        val validationError = validate(form)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.saveSettings(form) }
                .onSuccess { settings ->
                    // Thêm 3 dòng này
                    NotificationHelper.createChannel(context)
                    if (form.isEnabled) {
                        ReminderScheduler.schedule(context, form.reminderTime, form.reminderDays)
                    } else {
                        ReminderScheduler.cancel(context)
                    }

                    _uiState.update {
                        it.copy(
                            settings = settings,
                            form = NotificationSettingsForm.fromSettings(settings),
                            isSaving = false,
                            infoMessage = "Đã lưu cài đặt nhắc học."
                        )
                    }
                    onSaved()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    private fun validate(form: NotificationSettingsForm): String? {
        return when {
            !TIME_REGEX.matches(form.reminderTime) -> "Giờ nhắc học cần có định dạng HH:mm."
            form.isEnabled && form.reminderDays.isEmpty() -> "Vui lòng chọn ít nhất một ngày nhắc học."
            form.timezone.isBlank() -> "Múi giờ không được để trống."
            else -> null
        }
    }
    
    companion object {
        val TIME_REGEX = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotificationSettingsViewModel(application) as T
                }
            }
    }

}
