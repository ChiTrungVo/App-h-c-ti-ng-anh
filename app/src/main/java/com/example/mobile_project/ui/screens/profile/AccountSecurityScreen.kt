package com.example.mobile_project.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.profile.data.AccountSecurityForm
import com.example.mobile_project.feature.profile.viewmodel.ProfileUiState
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun AccountSecurityScreen(
    state: ProfileUiState,
    onFormChange: ((AccountSecurityForm) -> AccountSecurityForm) -> Unit,
    onUpdateEmail: () -> Unit,
    onUpdatePassword: () -> Unit,
    onSoftDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val form = state.securityForm

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Text("Tài khoản & bảo mật", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            "Đổi email, mật khẩu hoặc vô hiệu hóa hồ sơ MinLish.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))

        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Đổi email", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.newEmail,
                    { value -> onFormChange { it.copy(newEmail = value) } },
                    "Email mới",
                    R.drawable.ic_profile,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.currentPasswordForEmail,
                    { value -> onFormChange { it.copy(currentPasswordForEmail = value) } },
                    "Mật khẩu hiện tại",
                    R.drawable.ic_lock,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(14.dp))
                PrimaryButton(
                    if (state.isSaving) "Đang cập nhật..." else "Đổi email",
                    onClick = onUpdateEmail,
                    enabled = !state.isSaving
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Đổi mật khẩu", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.currentPassword,
                    { value -> onFormChange { it.copy(currentPassword = value) } },
                    "Mật khẩu hiện tại",
                    R.drawable.ic_lock,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.newPassword,
                    { value -> onFormChange { it.copy(newPassword = value) } },
                    "Mật khẩu mới",
                    R.drawable.ic_lock,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.confirmNewPassword,
                    { value -> onFormChange { it.copy(confirmNewPassword = value) } },
                    "Nhập lại mật khẩu mới",
                    R.drawable.ic_lock,
                    visualTransformation = PasswordVisualTransformation(),
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(14.dp))
                PrimaryButton(
                    if (state.isSaving) "Đang cập nhật..." else "Đổi mật khẩu",
                    onClick = onUpdatePassword,
                    enabled = !state.isSaving
                )
            }
        }

        state.errorMessage?.let { message ->
            Spacer(Modifier.height(14.dp))
            MessageBox(message = message, isError = true)
        }
        state.infoMessage?.let { message ->
            Spacer(Modifier.height(14.dp))
            MessageBox(message = message, isError = false)
        }

        Spacer(Modifier.height(14.dp))
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.28f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("Xóa tài khoản mềm", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
                Text(
                    "Hồ sơ sẽ được đánh dấu deleted và phiên đăng nhập hiện tại sẽ bị xóa. Appwrite Auth user không bị xóa khỏi server.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                SecondaryButton("Xóa tài khoản", onClick = { showDeleteDialog = true }, enabled = !state.isSaving)
            }
        }
        Spacer(Modifier.height(132.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa tài khoản?") },
            text = { Text("Hành động này sẽ đánh dấu hồ sơ là deleted và đăng xuất khỏi MinLish.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onSoftDelete()
                    }
                ) {
                    Text("Xóa", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
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
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
        },
        border = BorderStroke(
            1.dp,
            if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.45f) else MinLishPrimaryContainer.copy(alpha = 0.72f)
        )
    ) {
        Text(
            message,
            modifier = Modifier.padding(14.dp),
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
