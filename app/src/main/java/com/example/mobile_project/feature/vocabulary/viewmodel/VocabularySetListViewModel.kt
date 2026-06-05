package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state cho màn hình danh sách bộ từ vựng.
 */
data class VocabularySetListUiState(
    val sets: List<VocabularySet> = emptyList(),
    val filteredSets: List<VocabularySet> = emptyList(),
    val allTags: List<String> = listOf("Tất cả"),
    val selectedTag: String = "Tất cả",
    val selectedTab: VocabularyTab = VocabularyTab.Mine,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLoadingPublic: Boolean = false,
    val publicSets: List<VocabularySet> = emptyList(),
    val forkLoadingSetId: String? = null
)

/**
 * ViewModel cho VocabularySetListScreen.
 * Quản lý:
 *   - Tải danh sách bộ từ từ Appwrite
 *   - Tìm kiếm bộ từ
 *   - Lọc theo tag
 */
class VocabularySetListViewModel(
    private val repository: AppwriteVocabularySetRepository = AppwriteVocabularySetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularySetListUiState())
    val uiState: StateFlow<VocabularySetListUiState> = _uiState.asStateFlow()

    init {
        loadSets()
    }

    /**
     * Tải toàn bộ bộ từ của người dùng từ Appwrite.
     * Gọi lại khi cần refresh (pull-to-reload, sau khi thêm/xóa).
     */
    fun loadSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getMySets() }
                .onSuccess { sets ->
                    val allTags = listOf("Tất cả") +
                        sets.flatMap { it.tags }.distinct().sorted()
                    _uiState.update { state ->
                        state.copy(
                            sets = sets,
                            filteredSets = applyFilter(sets, state.searchQuery, state.selectedTag),
                            allTags = allTags,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể tải danh sách bộ từ."
                        )
                    }
                }
        }
    }

    /**
     * Cập nhật từ khóa tìm kiếm và lọc lại danh sách.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredSets = applyFilter(state.sets, query, state.selectedTag)
            )
        }
    }

    /**
     * Cập nhật tag được chọn và lọc lại danh sách.
     */
    fun onTagSelected(tag: String) {
        _uiState.update { state ->
            state.copy(
                selectedTag = tag,
                filteredSets = applyFilter(state.sets, state.searchQuery, tag)
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ------------------------------------------------------------------ //
    //  PRIVATE HELPERS                                                   //
    // ------------------------------------------------------------------ //

    private fun applyFilter(
        sets: List<VocabularySet>,
        query: String,
        tag: String
    ): List<VocabularySet> {
        return sets.filter { set ->
            val matchesSearch = query.isBlank() ||
                set.title.contains(query, ignoreCase = true) ||
                set.description.contains(query, ignoreCase = true) ||
                set.tags.any { it.contains(query, ignoreCase = true) }
            val matchesTag = tag == "Tất cả" || tag in set.tags
            matchesSearch && matchesTag
        }
    }
    fun onTabSelected(tab: VocabularyTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == VocabularyTab.Discover) loadPublicSets()
    }

    private fun loadPublicSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPublic = true) }
            try {
                val sets = repository.getPublicSets()
                _uiState.update { it.copy(isLoadingPublic = false, publicSets = sets) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingPublic = false) }
            }
        }
    }

    fun forkSet(setId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(forkLoadingSetId = setId) }
            try {
                repository.forkSet(setId)
                loadSets()
            } catch (e: Exception) {
                // handle error nếu cần
            } finally {
                _uiState.update { it.copy(forkLoadingSetId = null) }
            }
        }
    }
}
