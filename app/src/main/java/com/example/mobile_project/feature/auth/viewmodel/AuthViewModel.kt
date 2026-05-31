package com.example.mobile_project.feature.auth.viewmodel

import androidx.activity.ComponentActivity
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                            errorMessage = error.toVietnameseAuthMessage()
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
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
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
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
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
                            errorMessage = error.toVietnameseAuthMessage(),
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
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
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
                        it.copy(isLoading = false, errorMessage = error.toVietnameseAuthMessage())
                    }
                }
        }
    }

    fun sendPasswordRecovery(email: String) {
        val validationError = validateEmail(email)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching { repository.sendPasswordRecovery(email) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            infoMessage = "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư."
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

    fun completePasswordRecovery(
        userId: String,
        secret: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        val validationError = when {
            userId.isBlank() || secret.isBlank() -> "Liên kết đặt lại mật khẩu không hợp lệ."
            password.length < 8 -> "Mật khẩu cần ít nhất 8 ký tự."
            password != confirmPassword -> "Mật khẩu nhập lại chưa khớp."
            else -> null
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, infoMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    infoMessage = "Đang đặt lại mật khẩu..."
                )
            }
            runCatching { repository.completePasswordRecovery(userId, secret, password) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
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
                            errorMessage = error.toVietnameseAuthMessage(),
                            infoMessage = null
                        )
                    }
            }
        }
    }

    private fun validateEmailPassword(email: String, password: String): String? {
        return validateEmail(email)
            ?: when {
                password.length < 8 -> "Mật khẩu cần ít nhất 8 ký tự theo yêu cầu Appwrite."
                else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Vui lòng nhập email."
            !EMAIL_REGEX.matches(email.trim()) -> "Email không hợp lệ."
            else -> null
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
    ): String? {
        return when {
            displayName.isBlank() -> "Vui lòng nhập tên hiển thị."
            else -> validateEmailPassword(email, password)
                ?: if (password != confirmPassword) "Mật khẩu nhập lại chưa khớp." else null
        }
    }
}
