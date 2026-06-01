package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.feature.vocabulary.viewmodel.EditVocabularySetViewModel
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun EditVocabularySetScreen(
    setId: String?,
    onSave: (String) -> Unit,
    viewModel: EditVocabularySetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Khởi tạo form khi mở màn hình
    LaunchedEffect(setId) {
        viewModel.initForm(setId)
    }

    // Lưu thành công → callback
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess && uiState.savedSetId != null) {
            viewModel.clearSaveSuccess()
            onSave(uiState.savedSetId!!)
        }
    }

    if (uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(100.dp))
            CircularProgressIndicator()
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Text(
            if (uiState.isEditing) "Sửa bộ từ" else "Tạo bộ từ",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Tổ chức từ vựng theo chủ đề để học hiệu quả hơn.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = uiState.form.title,
            onValueChange = { viewModel.onTitleChanged(it) },
            label = { Text("Tên bộ từ") },
            isError = uiState.titleError != null,
            supportingText = {
                uiState.titleError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.titleError != null) {
            Spacer(Modifier.height(8.dp))
            ValidationMessageBox(uiState.titleError!!)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.description,
            onValueChange = { viewModel.onDescriptionChanged(it) },
            label = { Text("Mô tả") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Text("Thẻ chủ đề", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = uiState.form.tags,
            onValueChange = { viewModel.onTagsChanged(it) },
            label = { Text("Tags, cách nhau bằng dấu phẩy") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            uiState.form.tagList.forEach { tag ->
                AssistChip(onClick = {}, label = { Text(tag) })
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Công khai/Riêng tư", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (uiState.form.isPublic) "Bộ từ công khai" else "Chỉ mình tôi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = uiState.form.isPublic,
                onCheckedChange = { viewModel.onPublicChanged(it) }
            )
        }

        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(12.dp))
            ValidationMessageBox(msg)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            if (uiState.isSaving) "Đang lưu..." else "Lưu",
            enabled = !uiState.isSaving && uiState.titleError == null,
            onClick = { viewModel.save() }
        )
        Spacer(Modifier.height(132.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EditVocabularySetScreenPreview() {
    Mobile_projectTheme {
        EditVocabularySetScreen(
            setId = null,
            onSave = {}
        )
    }
}
