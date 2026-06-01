package com.example.mobile_project.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import androidx.compose.runtime.LaunchedEffect
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun LoginScreen(
    authState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        onClearMessage()
    }
    val errorMessage = authState.errorMessage
    val emailError = errorMessage?.takeIf { it.isLoginEmailError() }
    val passwordError = errorMessage?.takeIf { it.isLoginPasswordError() }
    val generalError = errorMessage?.takeUnless {
        it.isLoginEmailError() || it.isLoginPasswordError()
    }

    OceanBubblyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            MascotBadge(size = 128.dp, mood = MimiMood.Welcome)
            Spacer(Modifier.height(16.dp))
            Text("MinLish", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.68f)),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "Chào mừng bạn trở lại!",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))

            OceanCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp)) {
                    Text("Đăng nhập", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Tiếp tục hành trình học từ vựng của bạn.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(18.dp))
                    OceanTextField(
                        value = email,
                        onValueChange = { 
                            email = it
                            onClearMessage()
                        },
                        label = "Email",
                        iconRes = R.drawable.ic_email,
                        enabled = !authState.isLoading,
                        isError = emailError != null,
                        supportingText = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            onClearMessage()
                        },
                        label = "Mật khẩu",
                        iconRes = R.drawable.ic_lock,
                        enabled = !authState.isLoading,
                        isError = passwordError != null,
                        supportingText = passwordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onLogin(email, password)
                            }
                        ),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "Ẩn" else "Hiện",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "Quên mật khẩu?",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable {
                                    onClearMessage()
                                    onForgotPassword()
                                }
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    generalError?.let { message ->
                        ValidationMessageBox(message = message)
                        Spacer(Modifier.height(12.dp))
                    }
                    authState.infoMessage?.let { message ->
                        Text(
                            message,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    PrimaryButton(
                        if (authState.isLoading) "Đang đăng nhập..." else "Đăng nhập",
                        onClick = {
                            focusManager.clearFocus()
                            onLogin(email, password)
                        },
                        enabled = !authState.isLoading
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                        Text(
                            "hoặc",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    }
                    Spacer(Modifier.height(18.dp))
                    OutlinedButton(
                        onClick = onGoogleLogin,
                        enabled = !authState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp, pressedElevation = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "  Tiếp tục với Google",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Chưa có tài khoản? Đăng ký",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    onClearMessage()
                    onRegister()
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    Mobile_projectTheme() {
        LoginScreen(
            authState = AuthUiState(),
            onLogin = { _, _ -> },
            onGoogleLogin = {},
            onRegister = {},
            onForgotPassword = {}
        )
    }
}
private fun String.isLoginEmailError(): Boolean {
    return contains("email", ignoreCase = true) &&
        (contains("không hợp lệ", ignoreCase = true) || contains("nhập", ignoreCase = true))
}

private fun String.isLoginPasswordError(): Boolean {
    return contains("mật khẩu", ignoreCase = true) &&
        (contains("ít nhất", ignoreCase = true) || contains("nhập", ignoreCase = true))
}
