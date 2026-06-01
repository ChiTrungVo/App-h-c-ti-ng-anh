package com.example.mobile_project.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.mobile_project.R
import com.example.mobile_project.core.ui.FormFieldKeys
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.FeedbackMessageBox
import com.example.mobile_project.ui.components.FeedbackMessageType
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun ResetPasswordScreen(
    authState: AuthUiState,
    onSubmit: (String, String) -> Unit,
    onBackToLogin: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val passwordError = authState.fieldErrors[FormFieldKeys.PASSWORD]
    val confirmPasswordError = authState.fieldErrors[FormFieldKeys.CONFIRM_PASSWORD]

    LaunchedEffect(Unit) {
        onClearMessage()
    }

    OceanBubblyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(38.dp))
            MascotBadge(size = 128.dp)
            Spacer(Modifier.height(18.dp))
            OceanCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Đặt mật khẩu mới", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Nhập mật khẩu mới cho tài khoản MinLish của bạn.",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(22.dp))
                    OceanTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onClearMessage()
                        },
                        label = "Mật khẩu mới",
                        iconRes = R.drawable.ic_lock,
                        enabled = !authState.isLoading,
                        isError = passwordError != null,
                        supportingText = passwordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                    contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            onClearMessage()
                        },
                        label = "Nhập lại mật khẩu mới",
                        iconRes = R.drawable.ic_lock,
                        enabled = !authState.isLoading,
                        isError = confirmPasswordError != null,
                        supportingText = confirmPasswordError,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (confirmPasswordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                    contentDescription = if (confirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onSubmit(password, confirmPassword)
                            }
                        )
                    )
                    authState.errorMessage?.let { message ->
                        Spacer(Modifier.height(14.dp))
                        FeedbackMessageBox(message = message, type = FeedbackMessageType.Error)
                    }
                    authState.infoMessage?.let { message ->
                        Spacer(Modifier.height(14.dp))
                        FeedbackMessageBox(message = message, type = FeedbackMessageType.Success)
                    }
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(
                        if (authState.isLoading) "Đang đặt lại..." else "Đặt lại mật khẩu",
                        onClick = {
                            focusManager.clearFocus()
                            onSubmit(password, confirmPassword)
                        },
                        enabled = !authState.isLoading
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Quay lại đăng nhập",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable(onClick = onBackToLogin)
            )
        }
    }
}
