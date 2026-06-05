package com.example.mobile_project.feature.practice.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_project.data.model.QuizQuestion
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.feature.progress.data.repository.AppwriteDailyLearningStatsRepository
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
    val isReady: Boolean = false,
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
    private val progressRepository: AppwriteProgressRepository = AppwriteProgressRepository(),
    private val dailyStatsRepository: AppwriteDailyLearningStatsRepository = AppwriteDailyLearningStatsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private val _navigateToQuiz = MutableStateFlow(false)
    val navigateToQuiz: StateFlow<Boolean> = _navigateToQuiz.asStateFlow()

    fun startQuiz(setId: String) {
        viewModelScope.launch {
            val resolvedSetId = resolvePracticeSetId(setId)
            _uiState.value = PracticeUiState(
                setId = resolvedSetId,
                isReady = false,
                isFinished = false,
                questions = emptyList()
            )
            try {
                val words = loadWordsForPractice(resolvedSetId)
                val setTitle = loadSetTitle(resolvedSetId)

                if (words.isEmpty()) {
                    _uiState.update { it.copy(isFinished = true, isReady = true, setTitle = setTitle) }
                    return@launch
                }

                val extraWords = loadExtraWordsForOptions(resolvedSetId, words)

                val questions = buildQuestions(words, extraWords)
                _uiState.update {
                    it.copy(
                        questions = questions,
                        startTimeMs = System.currentTimeMillis(),
                        setTitle = setTitle,
                        isReady = true // ← set true khi load xong
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isFinished = true, isReady = true) }
            }
        }
    }

    private suspend fun resolvePracticeSetId(setId: String): String {
        if (setId.isNotBlank()) return setId

        return VocabularyDemoStore.vocabularySets.firstOrNull { it.wordCount > 0 }?.setId
            ?: VocabularyDemoStore.vocabularySets.firstOrNull()?.setId
            ?: runCatching {
                (setRepository.getMySets() + setRepository.getPublicSets())
                    .firstOrNull { it.wordCount > 0 }
                    ?.setId
            }.getOrNull()
            ?: ""
    }

    private suspend fun loadWordsForPractice(setId: String): List<VocabularyWord> {
        if (setId.isBlank()) return VocabularyDemoStore.vocabularies

        val remoteWords = runCatching {
            wordRepository.getWordsInSet(setId)
        }.getOrDefault(emptyList())

        return remoteWords.ifEmpty {
            VocabularyDemoStore.wordsForSet(setId)
        }
    }

    private suspend fun loadSetTitle(setId: String): String {
        if (setId.isBlank()) return ""

        val remoteTitle = runCatching {
            setRepository.getSet(setId)?.title
                ?: findSetInLists(setId, setRepository.getMySets(), setRepository.getPublicSets())?.title
        }.getOrNull()

        return remoteTitle
            ?: VocabularyDemoStore.getSet(setId)?.title
            ?: ""
    }

    private suspend fun loadExtraWordsForOptions(
        setId: String,
        words: List<VocabularyWord>
    ): List<VocabularyWord> {
        if (words.size >= 4) return emptyList()

        val remoteExtraWords = runCatching {
            (setRepository.getMySets() + setRepository.getPublicSets())
                .map { it.setId }
                .filter { it != setId }
                .flatMap { wordRepository.getWordsInSet(it) }
        }.getOrDefault(emptyList())

        val sampleExtraWords = VocabularyDemoStore.vocabularies
            .filter { it.setId != setId }

        return (remoteExtraWords + sampleExtraWords)
            .filter { extra -> words.none { it.wordId == extra.wordId } }
            .distinctBy { it.wordId }
            .shuffled()
            .take(4 - words.size + 3)
    }

    private fun findSetInLists(
        setId: String,
        vararg sets: List<VocabularySet>
    ): VocabularySet? = sets.asSequence()
        .flatMap { it.asSequence() }
        .firstOrNull { it.setId == setId }

    fun retry() {
        val currentSetId = _uiState.value.setId
        if (currentSetId.isBlank()) return
        _uiState.value = PracticeUiState(
            setId = currentSetId,
            isReady = false,
            isFinished = false,
            questions = emptyList()
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

    @RequiresApi(Build.VERSION_CODES.O)
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

                    // Cập nhật daily stats
                    val current = dailyStatsRepository.getTodayStats()
                    val newCorrectAnswers = current.correctAnswers + newCorrectCount
                    val newTotalQuestions = current.totalQuestions + state.totalQuestions
                    dailyStatsRepository.updateStats(
                        stats = current.copy(
                            quizCount      = current.quizCount + 1,
                            correctAnswers = newCorrectAnswers,
                            totalQuestions = newTotalQuestions,
                            avgScore       = if (newTotalQuestions == 0) 0.0
                            else newCorrectAnswers.toDouble() / newTotalQuestions * 100
                        ),
                        docId = current.id
                    )

                    android.util.Log.d("PracticeVM", "Saved quiz result: setId=${state.setId}, correct=$newCorrectCount, total=${state.totalQuestions}")
                } catch (e: Exception) {
                    android.util.Log.e("PracticeVM", "Failed to save quiz result: ${e.message}", e)
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
        val validWords = words.filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
        val allWordsForOptions = (validWords + extraWords)
            .filter { it.word.isNotBlank() && it.meaning.isNotBlank() }
            .distinctBy { it.wordId }

        return validWords.shuffled().map { word ->
            val wrongOptions = allWordsForOptions
                .filter { it.wordId != word.wordId }
                .map { it.meaning }
                .distinct()
                .shuffled()
                .take(3)

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
