package com.example.mobile_project.feature.progress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.feature.progress.data.repository.AppwriteProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------- Model ----------

data class SetProgress(
    val setId: String,
    val title: String,
    val totalWords: Int,
    val quizzedWords: Int,   // số từ đã từng quiz ít nhất 1 lần
    val correctWords: Int,   // số từ đã trả lời đúng ít nhất 1 lần
    val progressPercent: Float  // correctWords / totalWords
)

// ---------- State ----------

data class ProgressUiState(
    val setProgressList: List<SetProgress> = emptyList(),
    val totalSets: Int = 0,
    val totalWords: Int = 0,
    val totalCorrect: Int = 0,
    val overallPercent: Float = 0f,
    val isLoading: Boolean = true
)

// ---------- ViewModel ----------

class ProgressViewModel(
    private val repository: AppwriteProgressRepository = AppwriteProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    fun loadProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val sets = repository.getMySets()
                val quizResults = repository.getQuizResults()

                val setProgressList = sets.map { set ->
                    val totalWords = repository.getWordCount(set.setId)
                    val result = quizResults[set.setId]
                    val correctWords = result?.first ?: 0
                    val quizzedWords = result?.second ?: 0
                    val progressPercent = if (totalWords == 0) 0f
                    else correctWords.toFloat() / totalWords

                    SetProgress(
                        setId = set.setId,
                        title = set.title,
                        totalWords = totalWords,
                        quizzedWords = quizzedWords,
                        correctWords = correctWords,
                        progressPercent = progressPercent
                    )
                }

                val totalWords = setProgressList.sumOf { it.totalWords }
                val totalCorrect = setProgressList.sumOf { it.correctWords }
                val overallPercent = if (totalWords == 0) 0f
                else totalCorrect.toFloat() / totalWords

                _uiState.update {
                    it.copy(
                        setProgressList = setProgressList,
                        totalSets = sets.size,
                        totalWords = totalWords,
                        totalCorrect = totalCorrect,
                        overallPercent = overallPercent,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun recordQuizResult(setId: String, correctCount: Int, totalCount: Int) {
        viewModelScope.launch {
            try {
                repository.saveQuizResult(setId, correctCount, totalCount)
                loadProgress()
            } catch (e: Exception) {
            }
        }
    }
}