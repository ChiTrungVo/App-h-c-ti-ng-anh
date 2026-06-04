package com.example.mobile_project.feature.learning.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.DailyLearningStats
import com.example.mobile_project.data.model.UserWordProgress
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.feature.progress.data.repository.AppwriteDailyLearningStatsRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteUserWordProgressRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LearningViewModel : ViewModel() {

    private val progressRepo = AppwriteUserWordProgressRepository()
    private val statsRepo = AppwriteDailyLearningStatsRepository()
    private val wordRepo = AppwriteVocabularyWordRepository()

    private val _dailyStats = MutableStateFlow<DailyLearningStats?>(null)
    val dailyStats: StateFlow<DailyLearningStats?> = _dailyStats.asStateFlow()

    private val _dueWordsCount = MutableStateFlow(0)
    val dueWordsCount: StateFlow<Int> = _dueWordsCount.asStateFlow()

    private val _newWordsCount = MutableStateFlow(0)
    val newWordsCount: StateFlow<Int> = _newWordsCount.asStateFlow()

    private val _sessionWords = MutableStateFlow<List<VocabularyWord>>(emptyList())
    val sessionWords: StateFlow<List<VocabularyWord>> = _sessionWords.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    private val _sessionProgress = MutableStateFlow<List<UserWordProgress>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isEvaluating = MutableStateFlow(false)
    val isEvaluating: StateFlow<Boolean> = _isEvaluating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadDailyPlan(setId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _dailyStats.value = statsRepo.getTodayStats()
                val due = progressRepo.getDueWords(setId)
                val new = progressRepo.getNewWords(setId)
                _dueWordsCount.value = due.size
                _newWordsCount.value = new.size
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Không thể tải kế hoạch học."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startFlashcardSession(setId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _sessionWords.value = emptyList()
            _sessionProgress.value = emptyList()
            _currentWordIndex.value = 0
            try {
                if (setId.isBlank()) {
                    _errorMessage.value = "Chưa chọn bộ từ để học flashcard."
                    return@launch
                }

                ensureProgressForSet(setId)

                val dueProgress = progressRepo.getDueWords(setId)
                val newProgress = progressRepo.getNewWords(setId, limit = 5)
                val allProgress = (dueProgress + newProgress).distinctBy { it.wordId }
                val sessionItems = allProgress.mapNotNull { progress ->
                    wordRepo.getWord(progress.wordId)?.let { word -> progress to word }
                }

                _sessionProgress.value = sessionItems.map { it.first }
                _sessionWords.value = sessionItems.map { it.second }
                _currentWordIndex.value = 0
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Không thể tải phiên học flashcard."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun evaluateWord(quality: Int) {
        if (_isEvaluating.value) return

        val currentIndex = _currentWordIndex.value
        val words = _sessionWords.value
        val progressList = _sessionProgress.value

        if (currentIndex >= words.size || currentIndex >= progressList.size) return

        val currentProgress = progressList[currentIndex]
        _isEvaluating.value = true

        viewModelScope.launch {
            _errorMessage.value = null
            try {
                progressRepo.updateProgressAfterReview(currentProgress.id, quality)

                val isNew = currentProgress.status == "NOT_STARTED"
                val isNowMastered = quality >= 3 && currentProgress.repetitions >= 4

                statsRepo.incrementStats(
                    learnedDelta = if (isNew) 1 else 0,
                    reviewedDelta = 1,
                    masteredDelta = if (isNowMastered) 1 else 0,
                    minutesDelta = 1
                )

                if (currentIndex < words.size - 1) {
                    _currentWordIndex.value = currentIndex + 1
                } else {
                    _currentWordIndex.value = words.size
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Không thể cập nhật tiến độ SRS."
            } finally {
                _isEvaluating.value = false
            }
        }
    }

    private suspend fun ensureProgressForSet(setId: String) {
        val words = wordRepo.getWordsInSet(setId)
        val existingProgress = progressRepo.getProgressForSet(setId)
        val existingWordIds = existingProgress.map { it.wordId }.toSet()

        words.forEach { word ->
            if (word.wordId !in existingWordIds) {
                progressRepo.createProgress(setId = setId, wordId = word.wordId)
            }
        }
    }
}
