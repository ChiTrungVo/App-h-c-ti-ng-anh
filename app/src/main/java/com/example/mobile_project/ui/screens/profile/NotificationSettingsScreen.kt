package com.example.mobile_project.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotificationSettingsScreen(onSave: () -> Unit) {
    val settings = SampleData.notification_settings
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MascotBadge(size = 72.dp)
            Column {
                Text("Cài đặt nhắc học", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                Text("Giữ chuỗi học đều bằng lịch nhắc nhẹ nhàng.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(18.dp))
        OceanCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.26f),
                    border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f))
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Bật nhắc nhở", style = MaterialTheme.typography.titleLarge)
                            Text(
                                if (settings.isEnabled) "Đang bật nhắc học" else "Đang tắt nhắc học",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = settings.isEnabled, onCheckedChange = {})
                    }
                }
                Spacer(Modifier.height(16.dp))
                OceanTextField(settings.reminderTime, {}, "Giờ nhắc học", R.drawable.ic_clock, enabled = settings.isEnabled)
                Spacer(Modifier.height(16.dp))
                Text("Ngày nhắc", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                        AssistChip(
                            enabled = settings.isEnabled,
                            onClick = {},
                            label = { Text(day) },
                            shape = MaterialTheme.shapes.extraLarge,
                            border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (day in listOf("T2", "T4", "T6", "CN")) MinLishPrimaryContainer.copy(alpha = 0.34f) else MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OceanTextField(settings.timezone, {}, "Múi giờ", R.drawable.ic_progress)
                Spacer(Modifier.height(16.dp))
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                    border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f))
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Thông báo thiết bị đã sẵn sàng", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                        Text("Lưu cài đặt thành công. Nếu có lỗi mạng, trạng thái sẽ báo lưu lỗi.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(18.dp))
                PrimaryButton("Lưu cài đặt", onClick = onSave)
            }
        }
        Spacer(Modifier.height(132.dp))
    }
}
