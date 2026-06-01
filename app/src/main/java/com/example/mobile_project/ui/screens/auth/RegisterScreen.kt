package com.example.mobile_project.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox

@Composable
fun RegisterScreen(
    authState: AuthUiState,
    onRegister: (String, String, String, String) -> Unit,
    onLogin: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    var displayName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(Unit) {
        onClearMessage()
    }
    
    val nameError = authState.fieldErrors[FormFieldKeys.DISPLAY_NAME]
    val emailError = authState.fieldErrors[FormFieldKeys.EMAIL]
    val passwordError = authState.fieldErrors[FormFieldKeys.PASSWORD]
    val confirmPasswordError = authState.fieldErrors[FormFieldKeys.CONFIRM_PASSWORD]
    val generalError = authState.errorMessage

    OceanBubblyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            MascotBadge(size = 124.dp, mood = MimiMood.Welcome)
            Spacer(Modifier.height(14.dp))
            Text("Đăng ký", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            Text(
                "Tạo tài khoản MinLish của bạn!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            OceanCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(22.dp)) {
                    OceanTextField(
                        value = displayName,
                        onValueChange = { 
                            displayName = it
                            onClearMessage()
                        },
                        label = "Tên hiển thị",
                        iconRes = R.drawable.ic_profile,
                        enabled = !authState.isLoading,
                        isError = nameError != null,
                        supportingText = nameError,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(Modifier.height(12.dp))
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
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
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                    contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = confirmPassword,
                        onValueChange = { 
                            confirmPassword = it
                            onClearMessage()
                        },
                        label = "Nhập lại mật khẩu",
                        iconRes = R.drawable.ic_lock_retype,
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { 
                            focusManager.clearFocus()
                            onRegister(displayName, email, password, confirmPassword) 
                        })
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    generalError?.let { message ->
                        ValidationMessageBox(message = message)
                        Spacer(Modifier.height(16.dp))
                    }
                    
                    PrimaryButton(
                        if (authState.isLoading) "Đang tạo tài khoản..." else "Đăng ký",
                        onClick = { onRegister(displayName, email, password, confirmPassword) },
                        enabled = !authState.isLoading
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Quay lại đăng nhập",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    onClearMessage()
                    onLogin()
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
