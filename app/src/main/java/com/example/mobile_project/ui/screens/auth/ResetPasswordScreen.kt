package com.example.mobile_project.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun ResetPasswordScreen(
    authState: AuthUiState,
    onSubmit: (String, String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

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
                        onValueChange = { password = it },
                        label = "Mật khẩu mới",
                        iconRes = R.drawable.ic_lock,
                        enabled = !authState.isLoading,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Nhập lại mật khẩu mới",
                        iconRes = R.drawable.ic_lock,
                        enabled = !authState.isLoading,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    authState.errorMessage?.let { message ->
                        Spacer(Modifier.height(14.dp))
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    authState.infoMessage?.let { message ->
                        Spacer(Modifier.height(14.dp))
                        Text(message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(
                        if (authState.isLoading) "Đang đặt lại..." else "Đặt lại mật khẩu",
                        onClick = { onSubmit(password, confirmPassword) },
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
