package com.example.mobile_project.ui.screens.auth

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.mobile_project.ui.components.SecondaryButton

@Composable
fun ForgotPasswordScreen(
    authState: AuthUiState,
    onSubmit: (String) -> Unit,
    onBackToLogin: () -> Unit,
    onClearMessage: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    val emailError = authState.fieldErrors[FormFieldKeys.EMAIL]

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
                        onValueChange = {
                            email = it
                            onClearMessage()
                        },
                        label = "Email",
                        iconRes = R.drawable.ic_search,
                        enabled = !authState.isLoading,
                        isError = emailError != null,
                        supportingText = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    authState.errorMessage?.let { message ->
                        FeedbackMessageBox(message = message, type = FeedbackMessageType.Error)
                        Spacer(Modifier.height(12.dp))
                    }
                    authState.infoMessage?.let { message ->
                        FeedbackMessageBox(message = message, type = FeedbackMessageType.Success)
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
