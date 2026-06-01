package com.example.mobile_project.feature.profile.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.feature.auth.data.toVietnameseAvatarUploadMessage
import com.example.mobile_project.feature.auth.data.toVietnameseAuthMessage
import com.example.mobile_project.feature.profile.data.AccountSecurityForm
import com.example.mobile_project.feature.profile.data.AppwriteProfileRepository
import com.example.mobile_project.feature.profile.data.ProfileEditForm
import com.example.mobile_project.feature.profile.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: UserProfile? = null,
    val form: ProfileEditForm = ProfileEditForm(),
    val securityForm: AccountSecurityForm = AccountSecurityForm(),
    val avatarBytes: ByteArray? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val repository: AppwriteProfileRepository = AppwriteProfileRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(force: Boolean = false) {
        if (!force && (_uiState.value.isLoading || _uiState.value.profile != null)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getProfile() }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            form = ProfileEditForm.fromProfile(profile),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    loadAvatar(profile.avatarFileId)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun updateForm(transform: (ProfileEditForm) -> ProfileEditForm) {
        _uiState.update { it.copy(form = transform(it.form), errorMessage = null, infoMessage = null) }
    }

    fun updateSecurityForm(transform: (AccountSecurityForm) -> AccountSecurityForm) {
        _uiState.update {
            it.copy(securityForm = transform(it.securityForm), errorMessage = null, infoMessage = null)
        }
    }

    fun saveProfile(onSaved: () -> Unit = {}) {
        val form = _uiState.value.form
        val validationError = validateProfile(form)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.updateProfile(form) }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            form = ProfileEditForm.fromProfile(profile),
                            isSaving = false,
                            infoMessage = "Đã lưu hồ sơ."
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

    fun uploadAvatar(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.updateAvatar(context.applicationContext, uri) }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            form = ProfileEditForm.fromProfile(profile),
                            isSaving = false,
                            infoMessage = "Đã cập nhật ảnh đại diện."
                        )
                    }
                    loadAvatar(profile.avatarFileId, force = true)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "Không thể upload ảnh đại diện: ${error.toVietnameseAvatarUploadMessage()}"
                        )
                    }
                }
        }
    }

    fun updateEmail(onUpdated: () -> Unit = {}) {
        val form = _uiState.value.securityForm
        val error = when {
            !EMAIL_REGEX.matches(form.newEmail.trim()) -> "Email mới không hợp lệ."
            form.currentPasswordForEmail.length < 8 -> "Vui lòng nhập mật khẩu hiện tại."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error, infoMessage = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.updateEmail(form.newEmail, form.currentPasswordForEmail) }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            profile = profile,
                            form = ProfileEditForm.fromProfile(profile),
                            securityForm = it.securityForm.copy(currentPasswordForEmail = ""),
                            isSaving = false,
                            infoMessage = "Đã đổi email. Bạn có thể cần xác minh lại email mới."
                        )
                    }
                    onUpdated()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun updatePassword() {
        val form = _uiState.value.securityForm
        val error = when {
            form.currentPassword.length < 8 -> "Vui lòng nhập mật khẩu hiện tại."
            form.newPassword.length < 8 -> "Mật khẩu mới cần ít nhất 8 ký tự."
            form.newPassword != form.confirmNewPassword -> "Mật khẩu nhập lại chưa khớp."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error, infoMessage = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.updatePassword(form.newPassword, form.currentPassword) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            securityForm = it.securityForm.copy(
                                currentPassword = "",
                                newPassword = "",
                                confirmNewPassword = ""
                            ),
                            isSaving = false,
                            infoMessage = "Đã đổi mật khẩu."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun softDeleteAccount(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.softDeleteAccount() }
                .onSuccess {
                    _uiState.update {
                        ProfileUiState(isLoading = false, isSaving = false, isLoggedOut = true)
                    }
                    onDeleted()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun loadAvatar(fileId: String?, force: Boolean = false) {
        if (fileId.isNullOrBlank()) {
            _uiState.update { it.copy(avatarBytes = null) }
            return
        }
        if (!force && _uiState.value.avatarBytes != null) return
        viewModelScope.launch {
            runCatching { repository.downloadAvatar(fileId) }
                .onSuccess { bytes -> _uiState.update { it.copy(avatarBytes = bytes) } }
        }
    }

    private fun validateProfile(form: ProfileEditForm): String? {
        val minutes = form.dailyTargetMinutes.toIntOrNull()
        return when {
            form.displayName.isBlank() -> "Tên hiển thị không được để trống."
            form.phone.isNotBlank() && !PHONE_REGEX.matches(form.phone.trim()) ->
                "Số điện thoại chỉ được chứa số, dấu +, khoảng trắng và dấu -."
            minutes == null || minutes !in 1..240 -> "Mục tiêu hằng ngày phải từ 1 đến 240 phút."
            else -> null
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
        val PHONE_REGEX = Regex("^[0-9+\\s-]+$")
    }
}
