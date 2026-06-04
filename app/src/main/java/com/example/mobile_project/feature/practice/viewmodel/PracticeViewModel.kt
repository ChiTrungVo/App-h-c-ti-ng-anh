package com.example.mobile_project.feature.practice.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobile_project.data.model.QuizQuestion
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.data.sample.VocabularyDemoStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
data class PracticeUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isChecked: Boolean = false,
    val correctCount: Int = 0,
    val isFinished: Boolean = false,
    val startTimeMs: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val setTitle: String = ""
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int get() = questions.size
    val scorePercent: Int get() =
        if (totalQuestions == 0) 0 else (correctCount * 100 / totalQuestions)
    val isLastQuestion: Boolean get() = currentIndex >= totalQuestions - 1
    val isCorrect: Boolean get() = selectedAnswer == currentQuestion?.correctAnswer
}

class PracticeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()
    private val _navigateToQuiz = MutableStateFlow(false)
    val navigateToQuiz: StateFlow<Boolean> = _navigateToQuiz.asStateFlow()
    fun startQuiz(setId: String) {
        val words = if (setId.isBlank()) VocabularyDemoStore.vocabularies.toList()
        else VocabularyDemoStore.wordsForSet(setId)
        val setTitle = VocabularyDemoStore.getSet(setId)?.title ?: if (setId.isBlank()) "Tất cả từ vựng" else ""

        if (words.isEmpty()) {
            _uiState.update { it.copy(isFinished = true, setTitle = setTitle) }
            return
        }

        val questions = buildQuestions(words)
        _uiState.value = PracticeUiState(
            questions = questions,
            startTimeMs = System.currentTimeMillis(),
            setTitle = setTitle
        )
    }

    fun selectAnswer(answer: String) {
        val state = _uiState.value
        if (state.isChecked) return // đã kiểm tra rồi, không cho đổi
        _uiState.update { it.copy(selectedAnswer = answer, isChecked = false) }
    }

    fun checkAnswer() {
        val state = _uiState.value
        if (state.selectedAnswer == null || state.isChecked) return
        _uiState.update { it.copy(isChecked = true) }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.isChecked) return

        val addCorrect = if (state.isCorrect) 1 else 0
        val newCorrectCount = state.correctCount + addCorrect

        if (state.isLastQuestion) {
            // Kết thúc quiz
            val durationSeconds = ((System.currentTimeMillis() - state.startTimeMs) / 1000).toInt()
            _uiState.update {
                it.copy(
                    correctCount = newCorrectCount,
                    isFinished = true,
                    durationSeconds = durationSeconds,
                    selectedAnswer = null,
                    isChecked = false
                )
            }
            // TODO: Lưu kết quả lên Appwrite tại đây khi tích hợp backend
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    correctCount = newCorrectCount,
                    selectedAnswer = null,
                    isChecked = false
                )
            }
        }
    }

    fun retry() {
        val currentSetId = _uiState.value.questions.firstOrNull()?.wordId
            ?.let { wordId -> VocabularyDemoStore.vocabularies.firstOrNull { it.wordId == wordId }?.setId }
            ?: return
        startQuiz(currentSetId)
        _navigateToQuiz.update { true }
    }
    fun onNavigatedToQuiz() {
        _navigateToQuiz.update { false }
    }
    private fun buildQuestions(words: List<VocabularyWord>): List<QuizQuestion> {
        val shuffled = words.shuffled()
        return shuffled.map { word ->
            val wrongOptions = words
                .filter { it.wordId != word.wordId }
                .shuffled()
                .take(3)
                .map { it.meaning }

            val options = (wrongOptions + word.meaning).shuffled()

            QuizQuestion(
                questionId = "q_${word.wordId}",
                wordId = word.wordId,
                questionText = "\"${word.word}\" nghĩa là gì?",
                options = options,
                correctAnswer = word.meaning,
                selectedAnswer = null
            )
        }
    }
}