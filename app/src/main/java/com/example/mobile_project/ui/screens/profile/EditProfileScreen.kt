package com.example.mobile_project.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.profile.data.ProfileEditForm
import com.example.mobile_project.feature.profile.viewmodel.ProfileUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun EditProfileScreen(
    state: ProfileUiState,
    onFormChange: ((ProfileEditForm) -> ProfileEditForm) -> Unit,
    onUploadAvatar: (android.net.Uri) -> Unit,
    onSave: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onUploadAvatar)
    }
    val form = state.form
    val errorMessage = state.errorMessage
    val isNameError = state.profile != null && form.displayName.isBlank() ||
        errorMessage?.contains("Tên hiển thị", ignoreCase = true) == true
    val isPhoneError = errorMessage?.contains("Số điện thoại", ignoreCase = true) == true
    val isDailyTargetError = errorMessage?.contains("Mục tiêu hằng ngày", ignoreCase = true) == true

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MascotBadge(size = 72.dp)
            Column {
                Text("Hồ sơ", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    "Cập nhật thông tin để Mimi cá nhân hóa bài học.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                SecondaryButton(
                    text = if (state.isSaving) "Đang xử lý ảnh..." else "Đổi ảnh đại diện",
                    onClick = { launcher.launch("image/*") },
                    enabled = !state.isSaving
                )
                Spacer(Modifier.height(14.dp))
                errorMessage?.let { message ->
                    ValidationMessageBox(message = message)
                    Spacer(Modifier.height(14.dp))
                }
                state.infoMessage?.let { message ->
                    MessageBox(message = message, isError = false)
                    Spacer(Modifier.height(14.dp))
                }
                OceanTextField(
                    form.displayName,
                    { value -> onFormChange { it.copy(displayName = value) } },
                    "Tên hiển thị",
                    R.drawable.ic_profile,
                    isError = isNameError,
                    supportingText = if (isNameError) "Tên hiển thị không được để trống." else null
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.phone,
                    { value -> onFormChange { it.copy(phone = value) } },
                    "Số điện thoại",
                    R.drawable.ic_bell,
                    isError = isPhoneError,
                    supportingText = if (isPhoneError) errorMessage else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.bio,
                    { value -> onFormChange { it.copy(bio = value) } },
                    "Giới thiệu ngắn",
                    R.drawable.ic_edit,
                    minLines = 2
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OceanTextField(
                        form.nativeLanguage,
                        { value -> onFormChange { it.copy(nativeLanguage = value) } },
                        "Ngôn ngữ mẹ đẻ",
                        R.drawable.ic_profile,
                        modifier = Modifier.weight(1f)
                    )
                    OceanTextField(
                        form.targetLanguage,
                        { value -> onFormChange { it.copy(targetLanguage = value) } },
                        "Ngôn ngữ học",
                        R.drawable.ic_learning,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Trình độ", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("beginner", "intermediate", "advanced").forEach { level ->
                        AssistChip(
                            onClick = { onFormChange { it.copy(proficiencyLevel = level) } },
                            label = { Text(level.replaceFirstChar { char -> char.uppercase() }) },
                            shape = MaterialTheme.shapes.extraLarge,
                            border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (level == form.proficiencyLevel) {
                                    MinLishPrimaryContainer.copy(alpha = 0.35f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                OceanTextField(
                    form.studyGoal,
                    { value -> onFormChange { it.copy(studyGoal = value) } },
                    "Mục tiêu học tập",
                    R.drawable.ic_check_circle
                )
                Spacer(Modifier.height(12.dp))
                OceanTextField(
                    form.dailyTargetMinutes,
                    { value -> onFormChange { it.copy(dailyTargetMinutes = value.filter(Char::isDigit)) } },
                    "Mục tiêu hằng ngày (phút)",
                    R.drawable.ic_clock,
                    isError = isDailyTargetError,
                    supportingText = if (isDailyTargetError) errorMessage else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(10.dp))
                ProfileToggleRow(
                    label = "Bật âm thanh",
                    checked = form.soundEnabled,
                    onCheckedChange = { checked -> onFormChange { it.copy(soundEnabled = checked) } }
                )
                ProfileToggleRow(
                    label = "Bật chế độ tối",
                    checked = form.darkModeEnabled,
                    onCheckedChange = { checked -> onFormChange { it.copy(darkModeEnabled = checked) } }
                )
                Spacer(Modifier.height(18.dp))
                PrimaryButton(
                    text = if (state.isSaving) "Đang lưu..." else "Lưu thay đổi",
                    onClick = onSave,
                    enabled = !state.isSaving && !state.isLoading
                )
            }
        }
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun ProfileToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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
