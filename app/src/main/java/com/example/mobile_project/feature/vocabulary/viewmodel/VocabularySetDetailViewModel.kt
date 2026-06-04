package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.feature.vocabulary.data.AppwriteUserWordProgressRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import com.example.mobile_project.feature.vocabulary.data.VocabularyWordMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val errorMessage: String? = null,
    val deleteSuccess: Boolean = false
)

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
    private val wordRepository: AppwriteVocabularyWordRepository = AppwriteVocabularyWordRepository(),
    private val progressRepository: AppwriteUserWordProgressRepository = AppwriteUserWordProgressRepository()
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

            // Tải song song bộ từ và danh sách từ
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
                return@launch
            }

            _uiState.update {
                it.copy(
                    set = set,
                    words = words,
                    filteredWords = words,
                    isLoading = false,
                    errorMessage = null
                )
            }
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
                // Cascade: xóa progress → words → set (đúng thứ tự data integrity)
                progressRepository.deleteAllProgressInSet(currentSetId)
                wordRepository.deleteAllWordsInSet(currentSetId)
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
     * Xóa một từ vựng khỏi bộ từ, kèm cascade xóa progress.
     * Dùng local list removal thay vì reload toàn bộ set để tiết kiệm API call.
     */
    fun deleteWord(wordId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }
            val userId = _uiState.value.set?.userId ?: ""
            // Cascade: xóa progress trước, rồi mới xóa từ
            if (userId.isNotBlank()) {
                runCatching {
                    progressRepository.deleteProgressByUserSetWord(userId, currentSetId, wordId)
                }
            }
            runCatching { wordRepository.deleteWord(wordId) }
                .onSuccess {
                    // Xóa khỏi local list thay vì gọi API loadSet
                    _uiState.update { state ->
                        val newWords = state.words.filter { it.wordId != wordId }
                        state.copy(
                            words = newWords,
                            filteredWords = VocabularyWordMatcher.filter(newWords, state.searchQuery)
                        )
                    }
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearDeleteSuccess() {
        _uiState.update { it.copy(deleteSuccess = false) }
    }
}
