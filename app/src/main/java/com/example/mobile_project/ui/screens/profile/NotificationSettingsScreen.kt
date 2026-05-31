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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.feature.profile.data.NotificationSettingsForm
import com.example.mobile_project.feature.profile.viewmodel.NotificationSettingsUiState
import com.example.mobile_project.ui.components.MascotBadge
import com.example.mobile_project.ui.components.OceanCard
import com.example.mobile_project.ui.components.OceanTextField
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsUiState,
    onFormChange: ((NotificationSettingsForm) -> NotificationSettingsForm) -> Unit,
    onSave: () -> Unit
) {
    val form = state.form
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
                Text("Lưu lịch nhắc học vào Appwrite để dùng lại trên các thiết bị.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                if (form.isEnabled) "Đang bật nhắc học" else "Đang tắt nhắc học",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = form.isEnabled,
                            onCheckedChange = { checked -> onFormChange { it.copy(isEnabled = checked) } },
                            enabled = !state.isSaving
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OceanTextField(
                    form.reminderTime,
                    { value -> onFormChange { it.copy(reminderTime = value.take(5)) } },
                    "Giờ nhắc học (HH:mm)",
                    R.drawable.ic_clock,
                    enabled = form.isEnabled && !state.isSaving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(16.dp))
                Text("Ngày nhắc", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach { day ->
                        val selected = day in form.reminderDays
                        AssistChip(
                            enabled = form.isEnabled && !state.isSaving,
                            onClick = {
                                onFormChange {
                                    val days = if (selected) it.reminderDays - day else it.reminderDays + day
                                    it.copy(reminderDays = days)
                                }
                            },
                            label = { Text(day) },
                            shape = MaterialTheme.shapes.extraLarge,
                            border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected) MinLishPrimaryContainer.copy(alpha = 0.34f) else MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                OceanTextField(
                    form.timezone,
                    { value -> onFormChange { it.copy(timezone = value) } },
                    "Múi giờ",
                    R.drawable.ic_progress,
                    enabled = !state.isSaving
                )
                state.errorMessage?.let { message ->
                    Spacer(Modifier.height(16.dp))
                    MessageBox(message = message, isError = true)
                }
                state.infoMessage?.let { message ->
                    Spacer(Modifier.height(16.dp))
                    MessageBox(message = message, isError = false)
                }
                Spacer(Modifier.height(18.dp))
                PrimaryButton(
                    if (state.isSaving) "Đang lưu..." else "Lưu cài đặt",
                    onClick = onSave,
                    enabled = !state.isSaving && !state.isLoading
                )
            }
        }
        Spacer(Modifier.height(132.dp))
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
