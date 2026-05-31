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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
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
                        value = "Minh Anh",
                        onValueChange = {},
                        label = "Tên hiển thị",
                        iconRes = R.drawable.ic_profile
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = "minhanh@example.com",
                        onValueChange = {},
                        label = "Email",
                        iconRes = R.drawable.ic_search,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = "123456",
                        onValueChange = {},
                        label = "Mật khẩu",
                        iconRes = R.drawable.ic_close_circle,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(12.dp))
                    OceanTextField(
                        value = "12345",
                        onValueChange = {},
                        label = "Nhập lại mật khẩu",
                        iconRes = R.drawable.ic_close_circle,
                        isError = true,
                        supportingText = "Mật khẩu nhập lại chưa khớp.",
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f),
                        border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.8f))
                    ) {
                        Text(
                            "Tài khoản đã sẵn sàng. Bạn có thể bắt đầu học ngay.",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton("Đăng ký", onClick = onRegister)
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
