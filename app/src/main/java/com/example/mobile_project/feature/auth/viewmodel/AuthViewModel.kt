package com.example.mobile_project.feature.auth.viewmodel

import androidx.activity.ComponentActivity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.core.ui.FormFieldKeys
import com.example.mobile_project.feature.auth.data.AppwriteAuthRepository
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import com.example.mobile_project.feature.auth.data.toVietnameseAuthMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: MinLishAuthUser? = null,
    val isCheckingSession: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap()
)

class AuthViewModel(
    private val repository: AppwriteAuthRepository = AppwriteAuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshSession()
    }

    fun refreshSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingSession = true, errorMessage = null, fieldErrors = emptyMap()) }
            runCatching { repository.syncCurrentUserProfile() }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isCheckingSession = false,
                            isLoading = false,
                            fieldErrors = emptyMap(),
                            infoMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            user = null,
                            isCheckingSession = false,
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        val fieldErrors = validateEmailPassword(email, password)
        if (fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null, fieldErrors = fieldErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null, fieldErrors = emptyMap()) }
            runCatching { repository.login(email, password) }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null,
                            fieldErrors = emptyMap(),
                            infoMessage = if (user.isEmailVerified) {
                                null
                            } else {
                                "Email chưa được xác minh. Vui lòng kiểm tra hộp thư hoặc gửi lại email xác minh."
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun register(displayName: String, email: String, password: String, confirmPassword: String) {
        val fieldErrors = validateRegister(displayName, email, password, confirmPassword)
        if (fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null, fieldErrors = fieldErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null, fieldErrors = emptyMap()) }
            runCatching { repository.register(displayName, email, password) }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null,
                            fieldErrors = emptyMap(),
                            infoMessage = if (user.isEmailVerified) {
                                null
                            } else {
                                "Tài khoản đã được tạo. MinLish đã gửi email xác minh cho bạn."
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun loginWithGoogle(activity: ComponentActivity) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    fieldErrors = emptyMap(),
                    infoMessage = "Đang mở Google để đăng nhập..."
                )
            }
            runCatching { repository.loginWithGoogle(activity) }
                .onSuccess {
                    refreshSession()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap(),
                            infoMessage = null
                        )
                    }
                }
        }
    }

    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null, fieldErrors = emptyMap()) }
            runCatching { repository.logout() }
                .onSuccess {
                    _uiState.update {
                        AuthUiState(user = null, isCheckingSession = false)
                    }
                    onLoggedOut()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null, fieldErrors = emptyMap()) }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, infoMessage = null, fieldErrors = emptyMap())
            }
            runCatching { repository.sendEmailVerification() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fieldErrors = emptyMap(),
                            infoMessage = "Đã gửi lại email xác minh. Vui lòng kiểm tra hộp thư."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun sendPasswordRecovery(email: String) {
        val fieldErrors = validateEmail(email)
        if (fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null, fieldErrors = fieldErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null, fieldErrors = emptyMap()) }
            runCatching { repository.sendPasswordRecovery(email) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fieldErrors = emptyMap(),
                            infoMessage = "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap()
                        )
                    }
                }
        }
    }

    fun completePasswordRecovery(
        userId: String,
        secret: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        val fieldErrors = buildMap {
            if (password.isBlank()) {
                put(FormFieldKeys.PASSWORD, "Vui lòng nhập mật khẩu mới.")
            } else if (password.length < 8) {
                put(FormFieldKeys.PASSWORD, "Mật khẩu mới cần ít nhất 8 ký tự.")
            }
            if (confirmPassword.isBlank()) {
                put(FormFieldKeys.CONFIRM_PASSWORD, "Vui lòng nhập lại mật khẩu mới.")
            } else if (password != confirmPassword) {
                put(FormFieldKeys.CONFIRM_PASSWORD, "Mật khẩu nhập lại chưa khớp.")
            }
        }
        if (userId.isBlank() || secret.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Liên kết đặt lại mật khẩu không hợp lệ.",
                    infoMessage = null,
                    fieldErrors = emptyMap()
                )
            }
            return
        }
        if (fieldErrors.isNotEmpty()) {
            _uiState.update { it.copy(errorMessage = null, infoMessage = null, fieldErrors = fieldErrors) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    fieldErrors = emptyMap(),
                    infoMessage = "Đang đặt lại mật khẩu..."
                )
            }
            runCatching { repository.completePasswordRecovery(userId, secret, password) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fieldErrors = emptyMap(),
                            infoMessage = "Mật khẩu đã được đặt lại. Vui lòng đăng nhập lại."
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap(),
                            infoMessage = null
                        )
                    }
                }
        }
    }

    fun completeEmailVerification(uri: Uri) {
        val shouldRefresh = uri.getQueryParameter("refresh") == "1" ||
            uri.getQueryParameter("verified") == "1"
        if (shouldRefresh) {
            refreshSession()
            return
        }

        val userId = uri.getQueryParameter("userId")
        val secret = uri.getQueryParameter("secret")
        if (userId.isNullOrBlank() || secret.isNullOrBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Liên kết xác minh email không hợp lệ.", fieldErrors = emptyMap())
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    fieldErrors = emptyMap(),
                    infoMessage = "Đang xác minh email..."
                )
            }
            runCatching { repository.completeEmailVerification(userId, secret) }
                .onSuccess { user ->
                    _uiState.update {
                        if (user == null) {
                            it.copy(
                                user = null,
                                isLoading = false,
                                errorMessage = null,
                                fieldErrors = emptyMap(),
                                infoMessage = "Email đã được xác minh. Vui lòng đăng nhập lại để tiếp tục."
                            )
                        } else {
                            it.copy(
                                user = user,
                                isLoading = false,
                                errorMessage = null,
                                fieldErrors = emptyMap(),
                                infoMessage = "Email đã được xác minh."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toVietnameseAuthMessage(),
                            fieldErrors = emptyMap(),
                            infoMessage = null
                        )
                    }
                }
        }
    }

    private fun validateEmailPassword(email: String, password: String): Map<String, String> {
        return buildMap {
            putAll(validateEmail(email))
            when {
                password.isBlank() -> put(FormFieldKeys.PASSWORD, "Vui lòng nhập mật khẩu.")
                password.length < 8 -> put(FormFieldKeys.PASSWORD, "Mật khẩu cần ít nhất 8 ký tự.")
            }
        }
    }

    private fun validateEmail(email: String): Map<String, String> {
        return buildMap {
            when {
                email.isBlank() -> put(FormFieldKeys.EMAIL, "Vui lòng nhập email.")
                !EMAIL_REGEX.matches(email.trim()) -> put(FormFieldKeys.EMAIL, "Email không hợp lệ.")
            }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    }

    private fun validateRegister(
        displayName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Map<String, String> {
        return buildMap {
            if (displayName.isBlank()) {
                put(FormFieldKeys.DISPLAY_NAME, "Vui lòng nhập tên hiển thị.")
            }
            putAll(validateEmailPassword(email, password))
            when {
                confirmPassword.isBlank() -> put(FormFieldKeys.CONFIRM_PASSWORD, "Vui lòng nhập lại mật khẩu.")
                password != confirmPassword -> put(FormFieldKeys.CONFIRM_PASSWORD, "Mật khẩu nhập lại chưa khớp.")
            }
        }
    }
}
