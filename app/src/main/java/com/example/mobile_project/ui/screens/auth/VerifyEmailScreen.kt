package com.example.mobile_project.ui.screens.auth

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile_project.feature.auth.viewmodel.AuthUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton

@Composable
fun VerifyEmailScreen(
    authState: AuthUiState,
    onResendEmail: () -> Unit,
    onRefresh: () -> Unit,
    onBackToLogin: () -> Unit,
    onLogout: () -> Unit
) {
    val email = authState.user?.email.orEmpty()
    val hasSession = authState.user != null

    OceanBubblyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            MascotBadge(size = 132.dp, mood = MimiMood.Welcome)
            Spacer(Modifier.height(18.dp))
            OceanCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Xác minh email",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "MinLish đã gửi liên kết xác minh đến:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        email.ifBlank { "email của bạn" },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Hãy mở email và bấm liên kết xác minh. Sau khi xác minh xong, quay lại app hoặc bấm kiểm tra lại.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    authState.infoMessage?.let { message ->
                        Spacer(Modifier.height(16.dp))
                        Text(
                            message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                    authState.errorMessage?.let { message ->
                        Spacer(Modifier.height(16.dp))
                        Text(
                            message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    if (hasSession) {
                        PrimaryButton(
                            text = if (authState.isLoading) "Đang kiểm tra..." else "Tôi đã xác minh",
                            onClick = onRefresh,
                            enabled = !authState.isLoading
                        )
                        Spacer(Modifier.height(12.dp))
                        SecondaryButton(
                            text = "Gửi lại email xác minh",
                            onClick = onResendEmail,
                            enabled = !authState.isLoading
                        )
                        Spacer(Modifier.height(12.dp))
                        SecondaryButton(
                            text = "Đăng xuất",
                            onClick = onLogout,
                            enabled = !authState.isLoading
                        )
                    } else {
                        PrimaryButton(
                            text = "Đăng nhập",
                            onClick = onBackToLogin,
                            enabled = !authState.isLoading
                        )
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
