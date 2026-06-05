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
                // Kiểm tra reset streak trước
                dailyStatsRepository.checkAndResetStreakIfNeeded()

                // Sau đó load data bình thường
                val todayStats = dailyStatsRepository.getTodayStats()
                val quizResults = progressRepository.getQuizResults()

                val avgAccuracy = if (quizResults.isEmpty()) 0
                else quizResults.values
                    .map { (correct, total) ->
                        if (total == 0) 0 else correct * 100 / total
                    }
                    .average().toInt()

                _uiState.update {
                    it.copy(
                        streakDays = todayStats.streakDays,
                        totalWordsLearned = todayStats.learnedWords,
                        studiedMinutesToday = todayStats.studyMinutes,
                        quizAccuracy = avgAccuracy,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}