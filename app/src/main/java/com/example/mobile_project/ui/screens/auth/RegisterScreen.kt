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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun RegisterScreen(
    authState: AuthUiState,
    onRegister: (String, String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                        onValueChange = { displayName = it },
                        label = "Tên hiển thị",
                        iconRes = R.drawable.ic_profile,
                        enabled = !authState.isLoading
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        iconRes = R.drawable.ic_profile,
                        enabled = !authState.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Mật khẩu",
                        iconRes = R.drawable.ic_close_circle,
                        enabled = !authState.isLoading,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Nhập lại mật khẩu",
                        iconRes = R.drawable.ic_close_circle,
                        enabled = !authState.isLoading,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(16.dp))
                    authState.errorMessage?.let { message ->
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.58f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f))
                        ) {
                            Text(
                                message,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
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
                modifier = Modifier.clickable(onClick = onLogin)
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
