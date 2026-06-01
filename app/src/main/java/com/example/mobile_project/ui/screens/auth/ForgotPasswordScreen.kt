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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun ForgotPasswordScreen(
    authState: AuthUiState,
    onSubmit: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    OceanBubblyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(36.dp))
            Text("MinLish", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(22.dp))
            MascotBadge(size = 128.dp)
            Spacer(Modifier.height(18.dp))
            OceanCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Quên mật khẩu?", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Nhập email của bạn để nhận liên kết đặt lại mật khẩu.",
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(22.dp))
                    OceanTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        iconRes = R.drawable.ic_search,
                        enabled = !authState.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(16.dp))
                    authState.errorMessage?.let { message ->
                        MessageBox(message = message, isError = true)
                        Spacer(Modifier.height(12.dp))
                    }
                    authState.infoMessage?.let { message ->
                        MessageBox(message = message, isError = false)
                        Spacer(Modifier.height(12.dp))
                    }
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(
                        if (authState.isLoading) "Đang gửi..." else "Gửi yêu cầu",
                        onClick = { onSubmit(email) },
                        enabled = !authState.isLoading
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            SecondaryButton("Quay lại đăng nhập", onClick = onBackToLogin)
            Spacer(Modifier.height(24.dp))
            Text(
                "Cần đúng email đã đăng ký để Mimi gửi hướng dẫn.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable(onClick = onBackToLogin)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    Mobile_projectTheme() {
        ForgotPasswordScreen(
            onSubmit = {},
            onBackToLogin = {},
            authState = AuthUiState(
                isLoading = false,
                errorMessage = "Email không hợp lệ",
                infoMessage = "Liên kết đặt lại mật khẩu đã được gửi đến email của bạn."
            )
        )
    }
}
@Composable
private fun MessageBox(message: String, isError: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.58f)
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f)
        },
        border = BorderStroke(
            1.dp,
            if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.45f) else MinLishPrimaryContainer.copy(alpha = 0.8f)
        )
    ) {
        Text(
            message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}
