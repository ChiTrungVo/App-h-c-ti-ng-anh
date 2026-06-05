package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import com.example.mobile_project.feature.vocabulary.data.VocabularyImportExportCodec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * UI state cho màn hình chi tiết bộ từ vựng.
 */
data class VocabularySetDetailUiState(
    val set: VocabularySet? = null,
    val words: List<VocabularyWord> = emptyList(),
    val filteredWords: List<VocabularyWord> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isImporting: Boolean = false,
    val errorMessage: String? = null,
    val importExportMessage: String? = null,
    val deleteSuccess: Boolean = false
)

data class VocabularyExportFile(
    val fileName: String,
    val bytes: ByteArray
)

enum class VocabularyExportFormat {
    Csv,
    Xlsx
}

/**
 * ViewModel cho VocabularySetDetailScreen.
 * Quản lý:
 *   - Tải chi tiết bộ từ
 *   - Tải danh sách từ vựng trong bộ
 *   - Tìm kiếm từ trong bộ
 *   - Xóa bộ từ (cascade xóa từ + progress)
 *   - Xóa từ vựng
 */
class VocabularySetDetailViewModel(
    private val setRepository: AppwriteVocabularySetRepository = AppwriteVocabularySetRepository(),
    private val wordRepository: AppwriteVocabularyWordRepository = AppwriteVocabularyWordRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularySetDetailUiState())
    val uiState: StateFlow<VocabularySetDetailUiState> = _uiState.asStateFlow()

    private var currentSetId: String = ""

    /**
     * Tải dữ liệu bộ từ và danh sách từ vựng.
     * Gọi khi mở màn hình hoặc sau khi thêm/sửa/xóa từ.
     */
    fun loadSet(setId: String) {
        currentSetId = setId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refreshSet(setId)
        }
    }

    /**
     * Tìm kiếm từ vựng trong bộ từ.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.words
            } else {
                state.words.filter { word ->
                    word.word.contains(query, ignoreCase = true) ||
                        word.meaning.contains(query, ignoreCase = true) ||
                        word.definition.contains(query, ignoreCase = true) ||
                        word.example.contains(query, ignoreCase = true) ||
                        word.pronunciation.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredWords = filtered)
        }
    }

    /**
     * Xóa bộ từ (cascade: xóa luôn từ vựng + progress bên trong).
     */
    fun deleteSet() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching {
                // Cascade: xóa tất cả từ vựng trong bộ trước
                wordRepository.deleteAllWordsInSet(currentSetId)
                // Sau đó xóa bộ từ
                setRepository.deleteSet(currentSetId)
            }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể xóa bộ từ."
                        )
                    }
                }
        }
    }

    /**
     * Xóa một từ vựng khỏi bộ từ.
     */
    fun deleteWord(wordId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            runCatching { wordRepository.deleteWord(wordId) }
                .onSuccess {
                    // Reload danh sách từ sau khi xóa
                    loadSet(currentSetId)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.localizedMessage
                                ?: "Không thể xóa từ vựng."
                        )
                    }
                }
        }
    }

    fun importFile(fileName: String?, mimeType: String?, bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImporting = true,
                    errorMessage = null,
                    importExportMessage = null
                )
            }

            runCatching {
                val parseResult = when {
                    isCsvFile(fileName, mimeType) -> {
                        VocabularyImportExportCodec.parseCsv(bytes.toString(StandardCharsets.UTF_8))
                    }

                    isXlsxFile(fileName, mimeType) -> {
                        VocabularyImportExportCodec.parseXlsx(bytes)
                    }

                    else -> error("Chỉ hỗ trợ file CSV hoặc Excel .xlsx.")
                }

                if (parseResult.words.isEmpty()) {
                    ImportSummary(imported = 0, skipped = parseResult.skippedRows)
                } else {
                    wordRepository.createWords(currentSetId, parseResult.words)
                    val wordCount = wordRepository.countWordsInSet(currentSetId)
                    setRepository.updateWordCount(currentSetId, wordCount)
                    refreshSet(currentSetId, showLoading = false)
                    ImportSummary(
                        imported = parseResult.words.size,
                        skipped = parseResult.skippedRows
                    )
                }
            }
                .onSuccess { summary ->
                    val message = if (summary.imported == 0) {
                        "Không có dòng hợp lệ để import. File cần có cột word và meaning."
                    } else {
                        buildString {
                            append("Đã import ${summary.imported} từ.")
                            if (summary.skipped > 0) append(" Bỏ qua ${summary.skipped} dòng thiếu word/meaning.")
                        }
                    }
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            importExportMessage = message
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            errorMessage = error.localizedMessage ?: "Không thể import file."
                        )
                    }
                }
        }
    }

    fun buildExportFile(format: VocabularyExportFormat): VocabularyExportFile {
        val state = uiState.value
        val baseName = state.set?.title.orEmpty()
            .ifBlank { "minlish-vocabulary" }
            .toSafeFileName()
        return when (format) {
            VocabularyExportFormat.Csv -> VocabularyExportFile(
                fileName = "$baseName.csv",
                bytes = ("\uFEFF" + VocabularyImportExportCodec.toCsv(state.words))
                    .toByteArray(StandardCharsets.UTF_8)
            )

            VocabularyExportFormat.Xlsx -> VocabularyExportFile(
                fileName = "$baseName.xlsx",
                bytes = VocabularyImportExportCodec.toXlsx(state.words)
            )
        }
    }

    fun showExportSuccess() {
        _uiState.update {
            it.copy(importExportMessage = "Đã export bộ từ thành công.", errorMessage = null)
        }
    }

    fun showExportError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message, importExportMessage = null)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearImportExportMessage() {
        _uiState.update { it.copy(importExportMessage = null) }
    }

    fun clearDeleteSuccess() {
        _uiState.update { it.copy(deleteSuccess = false) }
    }

    private suspend fun refreshSet(setId: String, showLoading: Boolean = true) {
        if (showLoading) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        }

        val setResult = runCatching { setRepository.getSet(setId) }
        val wordsResult = runCatching { wordRepository.getWordsInSet(setId) }

        val set = setResult.getOrNull()
        val words = wordsResult.getOrNull() ?: emptyList()

        if (set == null && setResult.isFailure) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = setResult.exceptionOrNull()?.localizedMessage
                        ?: "Không tìm thấy bộ từ."
                )
            }
            return
        }

        _uiState.update {
            val filteredWords = if (it.searchQuery.isBlank()) {
                words
            } else {
                words.filterByQuery(it.searchQuery)
            }
            it.copy(
                set = set,
                words = words,
                filteredWords = filteredWords,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    private fun List<VocabularyWord>.filterByQuery(query: String): List<VocabularyWord> {
        return filter { word ->
            word.word.contains(query, ignoreCase = true) ||
                word.meaning.contains(query, ignoreCase = true) ||
                word.definition.contains(query, ignoreCase = true) ||
                word.example.contains(query, ignoreCase = true) ||
                word.pronunciation.contains(query, ignoreCase = true)
        }
    }

    private fun isCsvFile(fileName: String?, mimeType: String?): Boolean {
        val lowerName = fileName.orEmpty().lowercase(Locale.US)
        val lowerMime = mimeType.orEmpty().lowercase(Locale.US)
        return lowerName.endsWith(".csv") ||
            lowerMime == "text/csv" ||
            lowerMime == "text/comma-separated-values"
    }

    private fun isXlsxFile(fileName: String?, mimeType: String?): Boolean {
        val lowerName = fileName.orEmpty().lowercase(Locale.US)
        val lowerMime = mimeType.orEmpty().lowercase(Locale.US)
        return lowerName.endsWith(".xlsx") ||
            lowerMime == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    private fun String.toSafeFileName(): String {
        return lowercase(Locale.US)
            .replace(Regex("[^a-z0-9\\-_]+"), "-")
            .trim('-')
            .ifBlank { "minlish-vocabulary" }
    }

    private data class ImportSummary(
        val imported: Int,
        val skipped: Int
    )
}
