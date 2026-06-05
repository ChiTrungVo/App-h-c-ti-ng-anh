package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteUserWordProgressRepository
import com.example.mobile_project.feature.vocabulary.data.DictionaryRepository
import com.example.mobile_project.feature.vocabulary.data.WordForm
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state cho màn hình tạo/sửa từ vựng.
 */
data class EditWordUiState(
    val form: WordForm = WordForm(),
    val setId: String = "",
    val wordId: String? = null,      // null = tạo mới, non-null = đang sửa
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val wordError: String? = null,
    val meaningError: String? = null,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    // --- Gợi ý từ điển online ---
    val suggestions: List<String> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val isLoadingDetails: Boolean = false
)

/**
 * ViewModel cho EditWordScreen.
 * Quản lý:
 *   - Load dữ liệu từ cũ (nếu đang sửa)
 *   - Validate form
 *   - Lưu (tạo mới / cập nhật) lên Appwrite
 *   - Tạo bản ghi UserWordProgress khi tạo từ mới
 *   - Cập nhật wordCount ở VocabularySet
 */
class EditWordViewModel(
    private val wordRepository: AppwriteVocabularyWordRepository = AppwriteVocabularyWordRepository(),
    private val progressRepository: AppwriteUserWordProgressRepository = AppwriteUserWordProgressRepository(),
    private val setRepository: com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository =
        com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository(),
    private val dictionaryRepository: DictionaryRepository = DictionaryRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditWordUiState())
    val uiState: StateFlow<EditWordUiState> = _uiState.asStateFlow()

    /** Job debounce cho gợi ý autocomplete; hủy job cũ mỗi lần gõ phím. */
    private var suggestJob: Job? = null

    /**
     * Khởi tạo form.
     * @param setId ID của bộ từ chứa từ này
     * @param wordId ID của từ (null nếu tạo mới)
     */
    fun initForm(setId: String, wordId: String?) {
        _uiState.update { it.copy(setId = setId) }

        if (wordId == null || wordId == "new") {
            _uiState.update {
                it.copy(
                    form = WordForm(),
                    wordId = null,
                    isEditing = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, wordId = wordId, isEditing = true) }
        viewModelScope.launch {
            runCatching { wordRepository.getWord(wordId) }
                .onSuccess { word ->
                    if (word != null) {
                        _uiState.update {
                            it.copy(
                                form = WordForm.fromWord(word),
                                wordId = word.wordId,
                                isEditing = true,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Không tìm thấy từ vựng."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể tải dữ liệu từ vựng."
                        )
                    }
                }
        }
    }

    fun onWordChanged(value: String) {
        _uiState.update {
            it.copy(form = it.form.copy(word = value), wordError = null)
        }
        scheduleSuggestions(value)
    }

    /**
     * Debounce 350ms rồi gọi Datamuse lấy gợi ý từ theo prefix.
     * Hủy job cũ để tránh gọi mạng liên tục khi đang gõ.
     */
    private fun scheduleSuggestions(prefix: String) {
        suggestJob?.cancel()
        val trimmed = prefix.trim()
        if (trimmed.length < 2) {
            _uiState.update { it.copy(suggestions = emptyList(), isLoadingSuggestions = false) }
            return
        }
        suggestJob = viewModelScope.launch {
            delay(350)
            _uiState.update { it.copy(isLoadingSuggestions = true) }
            val result = runCatching { dictionaryRepository.suggest(trimmed) }
                .getOrDefault(emptyList())
            _uiState.update {
                it.copy(suggestions = result, isLoadingSuggestions = false)
            }
        }
    }

    /**
     * Người dùng chọn một từ gợi ý: điền tên từ ngay, ẩn danh sách gợi ý,
     * rồi tải chi tiết (IPA, định nghĩa, ví dụ, nghĩa tiếng Việt, collocations) để tự điền form.
     * Chỉ ghi đè các field đang trống để giữ dữ liệu người dùng đã tự nhập.
     */
    fun onSuggestionSelected(suggested: String) {
        suggestJob?.cancel()
        _uiState.update {
            it.copy(
                form = it.form.copy(word = suggested),
                suggestions = emptyList(),
                isLoadingSuggestions = false,
                isLoadingDetails = true,
                wordError = null
            )
        }

        viewModelScope.launch {
            val details = runCatching { dictionaryRepository.lookup(suggested) }.getOrNull()
            if (details == null) {
                _uiState.update { it.copy(isLoadingDetails = false) }
                return@launch
            }
            _uiState.update { state ->
                val f = state.form
                state.copy(
                    isLoadingDetails = false,
                    form = f.copy(
                        word = details.word.ifBlank { f.word },
                        pronunciation = f.pronunciation.ifBlank { details.pronunciation },
                        meaning = f.meaning.ifBlank { details.meaning },
                        definition = f.definition.ifBlank { details.definition },
                        example = f.example.ifBlank { details.example },
                        collocations = f.collocations.ifBlank {
                            details.collocations.joinToString(", ")
                        },
                        relatedWords = f.relatedWords.ifBlank {
                            details.collocations.joinToString(", ")
                        }
                    )
                )
            }
        }
    }

    /** Ẩn danh sách gợi ý (khi người dùng bấm ra ngoài hoặc rời ô nhập). */
    fun dismissSuggestions() {
        suggestJob?.cancel()
        _uiState.update { it.copy(suggestions = emptyList(), isLoadingSuggestions = false) }
    }

    fun onPronunciationChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(pronunciation = value)) }
    }

    fun onMeaningChanged(value: String) {
        _uiState.update {
            it.copy(form = it.form.copy(meaning = value), meaningError = null)
        }
    }

    fun onDefinitionChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(definition = value)) }
    }

    fun onExampleChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(example = value)) }
    }

    fun onCollocationsChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(collocations = value)) }
    }

    fun onRelatedWordsChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(relatedWords = value)) }
    }

    fun onNoteChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(note = value)) }
    }

    fun onImageUrlChanged(value: String) {
        _uiState.update { it.copy(form = it.form.copy(imageUrl = value)) }
    }

    /**
     * Validate và lưu từ vựng.
     */
    fun save() {
        val state = _uiState.value
        val form = state.form

        // Validate
        var wordError: String? = null
        var meaningError: String? = null
        when {
            form.word.isBlank() -> wordError = "Từ vựng không được để trống."
        }
        when {
            form.meaning.isBlank() -> meaningError = "Nghĩa tiếng Việt không được để trống."
        }
        if (wordError != null || meaningError != null) {
            _uiState.update { it.copy(wordError = wordError, meaningError = meaningError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                if (state.isEditing && state.wordId != null) {
                    // Cập nhật từ có sẵn
                    wordRepository.updateWord(
                        wordId = state.wordId,
                        setId = state.setId,
                        word = form.word,
                        pronunciation = form.pronunciation,
                        meaning = form.meaning,
                        definition = form.definition,
                        example = form.example,
                        collocations = form.collocationList,
                        relatedWords = form.relatedWordList,
                        note = form.note,
                        imageUrl = form.imageUrl.takeIf { it.isNotBlank() }
//                        isSetPublic = state.isSetPublic
                    )
                } else {
                    // Tạo từ mới
                    val newWord = wordRepository.createWord(
                        setId = state.setId,
                        word = form.word,
                        pronunciation = form.pronunciation,
                        meaning = form.meaning,
                        definition = form.definition,
                        example = form.example,
                        collocations = form.collocationList,
                        relatedWords = form.relatedWordList,
                        note = form.note,
                        imageUrl = form.imageUrl.takeIf { it.isNotBlank() }
//                        isSetPublic = state.isSetPublic
                    )

                    // Tạo bản ghi progress cho từ mới sử dụng ID thật của từ vừa tạo
                    progressRepository.createProgress(
                        setId = state.setId,
                        wordId = newWord.wordId
                    )

                    // Cập nhật wordCount cho bộ từ
                    val newCount = wordRepository.countWordsInSet(state.setId)
                    setRepository.updateWordCount(state.setId, newCount)
                }

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.localizedMessage
                            ?: "Không thể lưu từ vựng."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
