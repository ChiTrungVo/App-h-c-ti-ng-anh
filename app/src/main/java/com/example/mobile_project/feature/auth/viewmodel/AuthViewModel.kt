package com.example.mobile_project.feature.auth.viewmodel

import androidx.activity.ComponentActivity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.feature.auth.data.AppwriteAuthRepository
import com.example.mobile_project.feature.auth.data.MinLishAuthUser
import io.appwrite.exceptions.AppwriteException
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
    val infoMessage: String? = null
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
            _uiState.update { it.copy(isCheckingSession = true, errorMessage = null) }
            runCatching { repository.syncCurrentUserProfile() }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isCheckingSession = false,
                            isLoading = false,
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
                            errorMessage = error.toUserMessage()
                        )
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        val validationError = validateEmailPassword(email, password)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.login(email, password) }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null,
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
                        it.copy(isLoading = false, errorMessage = error.toUserMessage())
                    }
                }
        }
    }

    fun register(displayName: String, email: String, password: String, confirmPassword: String) {
        val validationError = validateRegister(displayName, email, password, confirmPassword)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.register(displayName, email, password) }
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null,
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
                        it.copy(isLoading = false, errorMessage = error.toUserMessage())
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
                            errorMessage = error.toUserMessage(),
                            infoMessage = null
                        )
                    }
                }
        }
    }

    fun logout(onLoggedOut: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.logout() }
                .onSuccess {
                    _uiState.update {
                        AuthUiState(user = null, isCheckingSession = false)
                    }
                    onLoggedOut()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.toUserMessage())
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, infoMessage = null)
            }
            runCatching { repository.sendEmailVerification() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = "Đã gửi lại email xác minh. Vui lòng kiểm tra hộp thư."
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.toUserMessage())
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
                it.copy(errorMessage = "Liên kết xác minh email không hợp lệ.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
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
                                infoMessage = "Email đã được xác minh. Vui lòng đăng nhập lại để tiếp tục."
                            )
                        } else {
                            it.copy(
                                user = user,
                                isLoading = false,
                                errorMessage = null,
                                infoMessage = "Email đã được xác minh."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUserMessage(),
                            infoMessage = null
                        )
                    }
            }
        }
    }

    private fun validateEmailPassword(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Vui lòng nhập email."
            "@" !in email -> "Email không hợp lệ."
            password.length < 8 -> "Mật khẩu cần ít nhất 8 ký tự theo yêu cầu Appwrite."
            else -> null
        }
    }

    private fun validateRegister(
        displayName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            displayName.isBlank() -> "Vui lòng nhập tên hiển thị."
            else -> validateEmailPassword(email, password)
                ?: if (password != confirmPassword) "Mật khẩu nhập lại chưa khớp." else null
        }
    }
}

private fun Throwable.toUserMessage(): String {
    val rawMessage = message.orEmpty()
    return when (this) {
        is AppwriteException -> when {
            rawMessage.contains("User cancelled login", ignoreCase = true) ||
                rawMessage.contains("cancelled", ignoreCase = true) ||
                rawMessage.contains("canceled", ignoreCase = true) ->
                "Bạn đã huỷ đăng nhập Google."
            rawMessage.contains("Invalid Origin", ignoreCase = true) ->
                "Appwrite chưa đăng ký đúng Android platform cho gói ứng dụng này."
            rawMessage.contains("invalid_client", ignoreCase = true) ->
                "Google OAuth client chưa hợp lệ. Kiểm tra Client ID/Client Secret trong Appwrite."
            rawMessage.contains("Invalid `url` param", ignoreCase = true) ||
                rawMessage.contains("Invalid URI", ignoreCase = true) ->
                "URL xác minh email chưa được đăng ký trong Appwrite Platform."
            rawMessage.contains("already exists", ignoreCase = true) ||
                rawMessage.contains("user_already_exists", ignoreCase = true) ->
                "Email này đã được đăng ký. Vui lòng đăng nhập hoặc dùng email khác."
            rawMessage.contains("verification", ignoreCase = true) || rawMessage.contains("redirect", ignoreCase = true) ->
                "Không thể gửi email xác minh. Kiểm tra URL chuyển hướng và cấu hình email trong Appwrite."
            rawMessage.isNotBlank() -> rawMessage
            else -> "Không thể kết nối Appwrite."
        }
        else -> rawMessage.ifBlank { "Đã có lỗi xảy ra." }
    }
}
