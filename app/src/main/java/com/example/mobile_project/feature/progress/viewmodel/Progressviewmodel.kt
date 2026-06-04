package com.example.mobile_project.feature.progress.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.sample.VocabularyDemoStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class ProgressViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    /**
     * Kết quả quiz đã làm: Map<setId, Pair<correctCount, totalCount>>
     * Dùng in-memory store. Khi tích hợp Appwrite, thay bằng query từ
     * collection quiz_attempts.
     */
    private val quizResults = mutableMapOf<String, Pair<Int, Int>>()

    init {
        loadProgress()
    }

    /**
     * Gọi sau mỗi lần quiz xong để cập nhật tiến độ bộ từ đó.
     */
    fun recordQuizResult(setId: String, correctCount: Int, totalCount: Int) {
        val existing = quizResults[setId]
        if (existing == null) {
            quizResults[setId] = Pair(correctCount, totalCount)
        } else {
            // Giữ kết quả tốt nhất
            val bestCorrect = maxOf(existing.first, correctCount)
            quizResults[setId] = Pair(bestCorrect, totalCount)
        }
        loadProgress()
    }

    fun loadProgress() {
        val sets = VocabularyDemoStore.vocabularySets.toList()

        val setProgressList = sets.map { set ->
            val totalWords = VocabularyDemoStore.wordsForSet(set.setId).size
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
    }
}