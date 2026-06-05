package com.example.mobile_project.feature.vocabulary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteUserWordProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class VocabularySetListViewModel(
    private val repository: AppwriteVocabularySetRepository = AppwriteVocabularySetRepository(),
    private val wordRepository: AppwriteVocabularyWordRepository = AppwriteVocabularyWordRepository(),
    private val progressRepository: AppwriteUserWordProgressRepository = AppwriteUserWordProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularySetListUiState())
    val uiState: StateFlow<VocabularySetListUiState> = _uiState.asStateFlow()

    init {
        loadSets()
    }

    fun loadSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getMySets() }
                .onSuccess { sets ->
                    val allTags = listOf("Tất cả") + sets.flatMap { it.tags }.distinct().sorted()
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
                            errorMessage = error.localizedMessage ?: "Không thể tải danh sách bộ từ."
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredSets = applyFilter(state.sets, query, state.selectedTag)
            )
        }
    }

    fun onTagSelected(tag: String) {
        _uiState.update { state ->
            state.copy(
                selectedTag = tag,
                filteredSets = applyFilter(state.sets, state.searchQuery, tag)
            )
        }
    }

    fun onTabSelected(tab: VocabularyTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        if (tab == VocabularyTab.Discover && _uiState.value.publicSets.isEmpty()) {
            loadPublicSets()
        }
    }

    /**
     * Tải danh sách bộ từ công khai từ tất cả người dùng.
     */
    fun loadPublicSets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPublic = true, errorMessage = null) }
            runCatching { repository.getPublicSets() }
                .onSuccess { sets ->
                    _uiState.update {
                        it.copy(publicSets = sets, isLoadingPublic = false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingPublic = false,
                            errorMessage = error.localizedMessage
                                ?: "Không thể tải danh sách khám phá."
                        )
                    }
                }
        }
    }

    /**
     * Sao chép (fork) một bộ từ công khai về bộ sưu tập của người dùng hiện tại.
     * Copy set metadata + tất cả từ vựng + tạo progress cho từng từ.
     */
    fun forkSet(sourceSetId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(forkLoadingSetId = sourceSetId, errorMessage = null) }

            runCatching {
                // 1. Lấy thông tin bộ từ nguồn
                val sourceSet = repository.getSet(sourceSetId)
                    ?: throw IllegalStateException("Không tìm thấy bộ từ nguồn.")

                // 2. Tạo bộ từ mới cho người dùng hiện tại
                val newSet = repository.createSet(
                    title = "${sourceSet.title} (sao chép)",
                    description = sourceSet.description,
                    tags = sourceSet.tags,
                    isPublic = false // Bản sao mặc định là riêng tư
                )

                // 3. Copy tất cả từ vựng (có pagination)
                copyWords(sourceSetId, newSet.setId)

                // 4. Cập nhật wordCount
                val newCount = wordRepository.countWordsInSet(newSet.setId)
                repository.updateWordCount(newSet.setId, newCount)

                newSet.setId
            }
                .onSuccess {
                    _uiState.update { it.copy(forkLoadingSetId = null) }
                    // Refresh tab "Của tôi"
                    loadSets()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            forkLoadingSetId = null,
                            errorMessage = error.localizedMessage
                                ?: "Không thể sao chép bộ từ."
                        )
                    }
                }
        }
    }

    /**
     * Copy từ vựng từ bộ nguồn sang bộ đích (có pagination).
     */
    private suspend fun copyWords(sourceSetId: String, targetSetId: String) {
        // getWordsInSet đã có limit 500, nhưng vì bộ từ công khai được
        // quyền Role.users() đọc, nên getWordsInSet sẽ trả về đầy đủ.
        val words = wordRepository.getWordsInSet(sourceSetId)
        words.forEach { word ->
            val newWord = wordRepository.createWord(
                setId = targetSetId,
                word = word.word,
                pronunciation = word.pronunciation,
                meaning = word.meaning,
                definition = word.definition,
                example = word.example,
                collocations = word.collocations,
                relatedWords = word.relatedWords,
                note = word.note,
                imageUrl = word.imageUrl, // Copy luôn URL ảnh (nếu có)
                isSetPublic = false
            )
            // Tạo progress cho từ mới
            progressRepository.createProgress(
                setId = targetSetId,
                wordId = newWord.wordId
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

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
}
