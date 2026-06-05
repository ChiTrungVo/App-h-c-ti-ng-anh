package com.example.mobile_project.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.feature.progress.data.repository.AppwriteProgressRepository
import com.example.mobile_project.feature.progress.data.repository.AppwriteDailyLearningStatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val streakDays: Int = 0,
    val totalWordsLearned: Int = 0,
    val studiedMinutesToday: Int = 0,
    val quizAccuracy: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val progressRepository: AppwriteProgressRepository = AppwriteProgressRepository(),
    private val dailyStatsRepository: AppwriteDailyLearningStatsRepository = AppwriteDailyLearningStatsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                val todayStats = dailyStatsRepository.getTodayStats()
                val streak = dailyStatsRepository.calculateStreak()

                val avgAccuracy = if (todayStats.totalQuestions == 0) 0
                else todayStats.correctAnswers * 100 / todayStats.totalQuestions

                _uiState.update {
                    it.copy(
                        streakDays = streak,
                        totalWordsLearned = todayStats.wordsLearned,
                        studiedMinutesToday = todayStats.studyMinutes,
                        quizAccuracy = avgAccuracy,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeVM", "Failed to load home data: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}