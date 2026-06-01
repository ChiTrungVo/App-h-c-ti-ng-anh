package com.example.mobile_project.ui.screens.profile

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
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun EditProfileScreen(onSave: () -> Unit) {
    val user = SampleData.user
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
                    "Cập nhật thông tin để Mimi giúp bạn học tốt hơn nhé!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                OceanTextField(user.displayName, {}, "Tên hiển thị", R.drawable.ic_profile, isError = true, supportingText = "Tên hiển thị không được để trống.")
                Spacer(Modifier.height(12.dp))
                OceanTextField(user.phone, {}, "Số điện thoại", R.drawable.ic_bell, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                Spacer(Modifier.height(12.dp))
                OceanTextField(user.bio, {}, "Giới thiệu ngắn", R.drawable.ic_edit, minLines = 2)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LangPill("Ngôn ngữ mẹ đẻ", user.nativeLanguage, Modifier.weight(1f))
                    LangPill("Ngôn ngữ học", user.targetLanguage, Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text("Trình độ", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                        AssistChip(
                            onClick = {},
                            label = { Text(level) },
                            shape = MaterialTheme.shapes.extraLarge,
                            border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (level == "Intermediate") MinLishPrimaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                OceanTextField("Duy trì học từ vựng mỗi ngày", {}, "Mục tiêu học tập", R.drawable.ic_check_circle)
                Spacer(Modifier.height(12.dp))
                OceanTextField("${user.dailyTargetMinutes}", {}, "Mục tiêu hằng ngày (phút)", R.drawable.ic_clock, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), isError = true, supportingText = "Mục tiêu phút phải lớn hơn 0.")
                Spacer(Modifier.height(10.dp))
                ProfileToggleRow("Bật âm thanh", true)
                ProfileToggleRow("Bật chế độ tối", false)
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                    border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f))
                ) {
                    Text(
                        "Lưu thành công. Nếu mất kết nối, hệ thống sẽ báo lưu thất bại.",
                        modifier = Modifier.padding(14.dp),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(18.dp))
                PrimaryButton("Lưu thay đổi", onClick = onSave)
            }
        }
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun LangPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ProfileToggleRow(label: String, checked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    Mobile_projectTheme {
        EditProfileScreen(onSave = {})
    }
}
