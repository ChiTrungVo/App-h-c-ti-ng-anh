package com.example.mobile_project.feature.practice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.QuizQuestion
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.feature.progress.data.repository.AppwriteProgressRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularySetRepository
import com.example.mobile_project.feature.vocabulary.data.AppwriteVocabularyWordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PracticeUiState(
    val setId: String = "",
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

class PracticeViewModel(
    private val wordRepository: AppwriteVocabularyWordRepository = AppwriteVocabularyWordRepository(),
    private val setRepository: AppwriteVocabularySetRepository = AppwriteVocabularySetRepository(),
    private val progressRepository: AppwriteProgressRepository = AppwriteProgressRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private val _navigateToQuiz = MutableStateFlow(false)
    val navigateToQuiz: StateFlow<Boolean> = _navigateToQuiz.asStateFlow()

    fun startQuiz(setId: String) {
        viewModelScope.launch {
            try {
                val words = wordRepository.getWordsInSet(setId)
                val setTitle = setRepository.getSet(setId)?.title ?: ""

                if (words.isEmpty()) {
                    _uiState.update { it.copy(isFinished = true, setTitle = setTitle) }
                    return@launch
                }

                // Nếu ít hơn 4 từ thì lấy thêm từ các bộ khác để làm đáp án sai
                val extraWords = if (words.size < 4) {
                    val allSets = setRepository.getPublicSets()
                    val otherSetIds = allSets.map { it.setId }.filter { it != setId }
                    otherSetIds
                        .flatMap { wordRepository.getWordsInSet(it) }
                        .filter { extra -> words.none { it.wordId == extra.wordId } }
                        .shuffled()
                        .take(4 - words.size + 3) // lấy dư để có đủ lựa chọn
                } else emptyList()

                val questions = buildQuestions(words, extraWords)
                _uiState.value = PracticeUiState(
                    setId = setId,
                    questions = questions,
                    startTimeMs = System.currentTimeMillis(),
                    setTitle = setTitle
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isFinished = true) }
            }
        }
    }

    fun retry() {
        val currentSetId = _uiState.value.setId
        if (currentSetId.isBlank()) return
        startQuiz(currentSetId)
        _navigateToQuiz.update { true }
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
            // Lưu kết quả lên Appwrite
            viewModelScope.launch {
                try {
                    progressRepository.saveQuizResult(
                        setId = state.setId,
                        correctCount = newCorrectCount,
                        totalCount = state.totalQuestions
                    )
                } catch (e: Exception) {
                    // không block UI nếu lưu lỗi
                }
            }
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

    fun onNavigatedToQuiz() {
        _navigateToQuiz.update { false }
    }
    private fun buildQuestions(
        words: List<VocabularyWord>,
        extraWords: List<VocabularyWord> = emptyList()
    ): List<QuizQuestion> {
        val shuffled = words.shuffled()
        val allWordsForOptions = words + extraWords

        return shuffled.map { word ->
            val wrongOptions = allWordsForOptions
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