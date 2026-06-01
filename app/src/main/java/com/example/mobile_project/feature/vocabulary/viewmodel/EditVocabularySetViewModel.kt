package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import com.example.mobile_project.feature.vocabulary.data.VocabularySetForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state cho màn hình tạo/sửa bộ từ vựng.
 */
data class EditVocabularySetUiState(
    val form: VocabularySetForm = VocabularySetForm(),
    val setId: String? = null,       // null = tạo mới, non-null = đang sửa
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val titleError: String? = null,
    val saveSuccess: Boolean = false,
    val savedSetId: String? = null,
    val errorMessage: String? = null
)

/**
 * ViewModel cho EditVocabularySetScreen.
 * Quản lý:
 *   - Load dữ liệu bộ từ cũ (nếu đang sửa)
 *   - Validate form
 *   - Lưu (tạo mới / cập nhật) lên Appwrite
 */
class EditVocabularySetViewModel(
    private val repository: AppwriteVocabularySetRepository = AppwriteVocabularySetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditVocabularySetUiState())
    val uiState: StateFlow<EditVocabularySetUiState> = _uiState.asStateFlow()

    /**
     * Khởi tạo form.
     * Nếu setId != null → load bộ từ cũ để sửa.
     * Nếu setId == null → form rỗng, tạo mới.
     */
    fun initForm(setId: String?) {
        if (setId == null || setId == "new") {
            _uiState.update {
                it.copy(
                    form = VocabularySetForm(),
                    setId = null,
                    isEditing = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, setId = setId, isEditing = true) }
        viewModelScope.launch {
            runCatching { repository.getSet(setId) }
                .onSuccess { set ->
                    if (set != null) {
                        _uiState.update {
                            it.copy(
                                form = VocabularySetForm.fromSet(set),
                                setId = set.setId,
                                isEditing = true,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Không tìm thấy bộ từ."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể tải dữ liệu bộ từ."
                        )
                    }
                }
        }
    }

    /**
     * Cập nhật title trong form.
     */
    fun onTitleChanged(value: String) {
        _uiState.update {
            it.copy(
                form = it.form.copy(title = value),
                titleError = null
            )
        }
    }

    /**
     * Cập nhật description trong form.
     */
    fun onDescriptionChanged(value: String) {
        _uiState.update {
            it.copy(form = it.form.copy(description = value))
        }
    }

    /**
     * Cập nhật tags (chuỗi cách nhau bằng dấu phẩy).
     */
    fun onTagsChanged(value: String) {
        _uiState.update {
            it.copy(form = it.form.copy(tags = value))
        }
    }

    /**
     * Cập nhậu trạng thái công khai/riêng tư.
     */
    fun onPublicChanged(value: Boolean) {
        _uiState.update {
            it.copy(form = it.form.copy(isPublic = value))
        }
    }

    /**
     * Validate và lưu bộ từ.
     * Nếu là tạo mới → createSet
     * Nếu là sửa → updateSet
     */
    fun save() {
        val state = _uiState.value
        val form = state.form

        // Validate
        val titleError = when {
            form.title.isBlank() -> "Tên bộ từ không được để trống."
            else -> null
        }
        if (titleError != null) {
            _uiState.update { it.copy(titleError = titleError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                if (state.isEditing && state.setId != null) {
                    repository.updateSet(
                        setId = state.setId,
                        title = form.title,
                        description = form.description,
                        tags = form.tagList,
                        isPublic = form.isPublic
                    )
                } else {
                    repository.createSet(
                        title = form.title,
                        description = form.description,
                        tags = form.tagList,
                        isPublic = form.isPublic
                    )
                }
            }
                .onSuccess { savedSet ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveSuccess = true,
                            savedSetId = savedSet.setId
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể lưu bộ từ."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false, savedSetId = null) }
    }
}
