package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.mobile_project.feature.vocabulary.viewmodel.EditWordViewModel
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun EditWordScreen(
    setId: String,
    wordId: String?,
    onSave: () -> Unit,
    viewModel: EditWordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Khởi tạo form khi mở màn hình
    LaunchedEffect(setId, wordId) {
        viewModel.initForm(setId, wordId)
    }

    // Lưu thành công → callback
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.clearSaveSuccess()
            onSave()
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
            if (uiState.isEditing) "Sửa từ vựng" else "Thêm từ vựng",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Thêm từ mới vào bộ từ để học flashcard và quiz.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = uiState.form.word,
            onValueChange = { viewModel.onWordChanged(it) },
            label = { Text("Từ vựng") },
            isError = uiState.wordError != null,
            supportingText = {
                uiState.wordError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.wordError != null) {
            Spacer(Modifier.height(8.dp))
            ValidationMessageBox(uiState.wordError!!)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.pronunciation,
            onValueChange = { viewModel.onPronunciationChanged(it) },
            label = { Text("Phiên âm IPA") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.meaning,
            onValueChange = { viewModel.onMeaningChanged(it) },
            label = { Text("Nghĩa tiếng Việt") },
            isError = uiState.meaningError != null,
            supportingText = {
                uiState.meaningError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.meaningError != null) {
            Spacer(Modifier.height(8.dp))
            ValidationMessageBox(uiState.meaningError!!)
        }

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.definition,
            onValueChange = { viewModel.onDefinitionChanged(it) },
            label = { Text("Định nghĩa tiếng Anh") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.example,
            onValueChange = { viewModel.onExampleChanged(it) },
            label = { Text("Ví dụ") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.collocations,
            onValueChange = { viewModel.onCollocationsChanged(it) },
            label = { Text("Cụm từ đi kèm (cách nhau bằng dấu phẩy)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.note,
            onValueChange = { viewModel.onNoteChanged(it) },
            label = { Text("Ghi chú") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = uiState.form.imageUrl,
            onValueChange = { viewModel.onImageUrlChanged(it) },
            label = { Text("URL hình ảnh (tùy chọn)") },
            modifier = Modifier.fillMaxWidth()
        )

        uiState.errorMessage?.let { msg ->
            Spacer(Modifier.height(12.dp))
            ValidationMessageBox(msg)
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            if (uiState.isSaving) "Đang lưu..." else "Lưu từ",
            enabled = !uiState.isSaving,
            onClick = { viewModel.save() }
        )
        Spacer(Modifier.height(132.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun EditWordScreenPreview() {
    Mobile_projectTheme {
        EditWordScreen(
            setId = "preview_set_id",
            wordId = null,
            onSave = {}
        )
    }
}
