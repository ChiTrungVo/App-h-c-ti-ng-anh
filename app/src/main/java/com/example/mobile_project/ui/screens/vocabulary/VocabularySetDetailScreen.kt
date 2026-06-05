package com.example.mobile_project.ui.screens.vocabulary

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.feature.vocabulary.viewmodel.VocabularyExportFormat
import com.example.mobile_project.feature.vocabulary.viewmodel.VocabularySetDetailViewModel
import com.example.mobile_project.ui.components.EmptyStateView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.WordCard
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun VocabularySetDetailScreen(
    setId: String,
    onAddWord: () -> Unit,
    onEditWord: (String) -> Unit,
    onEditSet: () -> Unit,
    onStartLearning: () -> Unit,
    onQuiz: () -> Unit,
    onDeleteSet: () -> Unit,
    viewModel: VocabularySetDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var pendingExportBytes by remember { mutableStateOf<ByteArray?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Không thể đọc file đã chọn.")
            viewModel.importFile(
                fileName = uri.displayName(context),
                mimeType = context.contentResolver.getType(uri),
                bytes = bytes
            )
        }.onFailure { error ->
            viewModel.showExportError(error.localizedMessage ?: "Không thể đọc file đã chọn.")
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        writePendingExport(context, uri, pendingExportBytes, viewModel)
        pendingExportBytes = null
    }

    val xlsxExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
    ) { uri ->
        writePendingExport(context, uri, pendingExportBytes, viewModel)
        pendingExportBytes = null
    }

    // Tải dữ liệu khi mở màn hình
    LaunchedEffect(setId) {
        viewModel.loadSet(setId)
    }

    // Xóa thành công → quay lại
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            viewModel.clearDeleteSuccess()
            onDeleteSet()
        }
    }

    // Dialog xác nhận xóa bộ từ
    var confirmDeleteSet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    if (confirmDeleteSet) {
        AlertDialog(
            onDismissRequest = { confirmDeleteSet = false },
            title = { Text("Xóa bộ từ") },
            text = { Text("Bạn có chắc muốn xóa bộ từ này? Tất cả từ vựng bên trong sẽ bị xóa.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSet()
                        confirmDeleteSet = false
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteSet = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog xác nhận xóa từ vựng
    var deletingWordId by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    if (deletingWordId != null) {
        AlertDialog(
            onDismissRequest = { deletingWordId = null },
            title = { Text("Xóa từ vựng") },
            text = { Text("Bạn có chắc muốn xóa từ này khỏi bộ từ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deletingWordId?.let { viewModel.deleteWord(it) }
                        deletingWordId = null
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingWordId = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.set == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Không tìm thấy bộ từ",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                "Bộ từ đã bị xóa hoặc không tồn tại.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            EmptyStateView(
                title = "Không có dữ liệu",
                message = "Hãy quay lại danh sách và tạo bộ từ mới để bắt đầu."
            )
        }
        return
    }

    val set = uiState.set!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(set.title, style = MaterialTheme.typography.headlineLarge)
            Text(
                "${set.wordCount} từ",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tiến độ bộ từ", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { set.progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${(set.progress * 100).toInt()}% đã hoàn thành",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Thêm từ", onClick = onAddWord, modifier = Modifier.weight(1f))
                SecondaryButton("Sửa bộ từ", onClick = onEditSet, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                text = if (uiState.isImporting) "Đang import..." else "Import CSV/Excel",
                onClick = {
                    importLauncher.launch(
                        arrayOf(
                            "text/csv",
                            "text/comma-separated-values",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            "application/octet-stream"
                        )
                    )
                },
                enabled = !uiState.isImporting
            )
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton(
                    text = "Export CSV",
                    onClick = {
                        val exportFile = viewModel.buildExportFile(VocabularyExportFormat.Csv)
                        pendingExportBytes = exportFile.bytes
                        csvExportLauncher.launch(exportFile.fileName)
                    },
                    enabled = uiState.words.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "Export Excel",
                    onClick = {
                        val exportFile = viewModel.buildExportFile(VocabularyExportFormat.Xlsx)
                        pendingExportBytes = exportFile.bytes
                        xlsxExportLauncher.launch(exportFile.fileName)
                    },
                    enabled = uiState.words.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }
            if (uiState.isImporting) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            uiState.importExportMessage?.let { message ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Xóa bộ từ", onClick = { confirmDeleteSet = true })
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                "Bắt đầu học",
                onClick = onStartLearning,
                enabled = uiState.words.isNotEmpty()
            )
            Spacer(Modifier.height(10.dp))
            SecondaryButton(
                "Làm quiz",
                onClick = onQuiz,
                enabled = uiState.words.isNotEmpty()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Tìm trong bộ từ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Danh sách từ (${uiState.filteredWords.size}/${uiState.words.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (uiState.words.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Chưa có từ trong bộ này",
                    message = "Thêm từ đầu tiên để flashcard, quiz và SRS có dữ liệu học.",
                    asset = R.drawable.mimi_an_ui
                )
            }
        } else if (uiState.filteredWords.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Không tìm thấy từ phù hợp",
                    message = "Thử tìm theo từ vựng, nghĩa hoặc ví dụ khác."
                )
            }
        } else {
            items(uiState.filteredWords, key = { it.wordId }) { word ->
                Column {
                    WordCard(word = word, onClick = { onEditWord(word.wordId) })
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { deletingWordId = word.wordId }) {
                            Text("Xóa")
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(132.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VocabularySetDetailScreenPreview() {
    Mobile_projectTheme {
        VocabularySetDetailScreen(
            setId = "preview_set_id",
            onAddWord = {},
            onEditWord = {},
            onEditSet = {},
            onStartLearning = {},
            onQuiz = {},
            onDeleteSet = {}
        )
    }
}

private fun writePendingExport(
    context: Context,
    uri: Uri?,
    bytes: ByteArray?,
    viewModel: VocabularySetDetailViewModel
) {
    if (uri == null || bytes == null) return

    runCatching {
        context.contentResolver.openOutputStream(uri)?.use { output ->
            output.write(bytes)
        } ?: error("Không thể ghi file export.")
    }.onSuccess {
        viewModel.showExportSuccess()
    }.onFailure { error ->
        viewModel.showExportError(error.localizedMessage ?: "Không thể ghi file export.")
    }
}

private fun Uri.displayName(context: Context): String? {
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return lastPathSegment
}
