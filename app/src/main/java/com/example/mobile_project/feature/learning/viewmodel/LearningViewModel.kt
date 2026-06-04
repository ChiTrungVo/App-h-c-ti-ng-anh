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

    // --- Daily Plan State ---
    private val _dailyStats = MutableStateFlow<DailyLearningStats?>(null)
    val dailyStats: StateFlow<DailyLearningStats?> = _dailyStats.asStateFlow()

    private val _dueWordsCount = MutableStateFlow(0)
    val dueWordsCount: StateFlow<Int> = _dueWordsCount.asStateFlow()

    private val _newWordsCount = MutableStateFlow(0)
    val newWordsCount: StateFlow<Int> = _newWordsCount.asStateFlow()

    // --- Flashcard Session State ---
    private val _sessionWords = MutableStateFlow<List<VocabularyWord>>(emptyList())
    val sessionWords: StateFlow<List<VocabularyWord>> = _sessionWords.asStateFlow()

    private val _currentWordIndex = MutableStateFlow(0)
    val currentWordIndex: StateFlow<Int> = _currentWordIndex.asStateFlow()

    private val _sessionProgress = MutableStateFlow<List<UserWordProgress>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadDailyPlan(setId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _dailyStats.value = statsRepo.getTodayStats()
                val due = progressRepo.getDueWords(setId)
                val new = progressRepo.getNewWords(setId)
                _dueWordsCount.value = due.size
                _newWordsCount.value = new.size
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startFlashcardSession(setId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dueProgress = progressRepo.getDueWords(setId)
                val newProgress = progressRepo.getNewWords(setId, limit = 5)
                val allProgress = dueProgress + newProgress
                
                _sessionProgress.value = allProgress
                
                val words = allProgress.mapNotNull { progress ->
                    wordRepo.getWord(progress.wordId)
                }
                
                _sessionWords.value = words
                _currentWordIndex.value = 0
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * @param quality 0-5 (0=Again, 1=Hard, 3=Good, 5=Easy)
     */
    fun evaluateWord(quality: Int) {
        val currentIndex = _currentWordIndex.value
        val words = _sessionWords.value
        val progressList = _sessionProgress.value
        
        if (currentIndex >= words.size) return

        val currentProgress = progressList[currentIndex]

        viewModelScope.launch {
            try {
                // 1. Update SRS progress
                progressRepo.updateProgressAfterReview(currentProgress.id, quality)
                
                // 2. Update Daily Stats
                val isNew = currentProgress.status == "NOT_STARTED"
                val isNowMastered = quality >= 3 && currentProgress.repetitions >= 4 // Example logic
                
                statsRepo.incrementStats(
                    learnedDelta = if (isNew) 1 else 0,
                    reviewedDelta = 1,
                    masteredDelta = if (isNowMastered) 1 else 0,
                    minutesDelta = 1 // Assume 1 min per word session for simplicity
                )

                // 3. Move to next word
                if (currentIndex < words.size - 1) {
                    _currentWordIndex.value = currentIndex + 1
                } else {
                    // Session finished
                    _currentWordIndex.value = words.size
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
