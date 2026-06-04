package com.example.mobile_project.feature.progress.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.data.sample.VocabularyDemoStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mobile_project.feature.progress.domain.model.ProgressItem
import com.example.mobile_project.feature.progress.domain.usecase.CalculateNextReviewUseCase
import com.example.mobile_project.feature.progress.domain.usecase.UpdateLearningPlanUseCase
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
    val isLoading: Boolean = true,

    // Trạng thái dành cho Kế hoạch học tập SRS từng ngày
    val dailyReviewWords: List<ProgressItem> = emptyList(), // Các từ cần ôn tập hôm nay (Interval <= 0)
    val dailyNewWords: List<ProgressItem> = emptyList()     // Các từ mới hoàn toàn chưa kích hoạt học
)

// ---------- ViewModel ----------

/*
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
*/

class ProgressViewModel(
    // Nhúng các UseCase xử lý thuật toán và lên kế hoạch học tập
    private val calculateNextReviewUseCase: CalculateNextReviewUseCase = CalculateNextReviewUseCase(),
    private val updateLearningPlanUseCase: UpdateLearningPlanUseCase = UpdateLearningPlanUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private val quizResults = mutableMapOf<String, Pair<Int, Int>>()
    
    // Bộ lưu trữ In-memory theo dõi trạng thái SRS của từng từ vựng (Key = wordId)
    // Khi tích hợp Appwrite, dữ liệu này sẽ được lưu và lấy từ một Collection riêng tên là srs_progress
    private val srsProgressStore = mutableMapOf<String, ProgressItem>()

    init {
        // Khởi tạo dữ liệu nền SRS cho toàn bộ các từ vựng hiện có
        initSrsDataIfEmpty()
        loadProgress()
    }

    /**
     * Tạo trạng thái SRS mặc định (chưa học) cho tất cả từ vựng trong hệ thống
     */
    private fun initSrsDataIfEmpty() {
        if (srsProgressStore.isEmpty()) {
            VocabularyDemoStore.vocabularySets.forEach { set ->
                VocabularyDemoStore.wordsForSet(set.setId).forEach { word ->
                    srsProgressStore[word.wordId] = ProgressItem(vocabularyId = word.wordId)
                }
            }
        }
    }

    /**
     * Gọi hàm này khi người dùng học Flashcard và bấm nút tự đánh giá độ nhớ
     * @param wordId ID của từ vựng vừa lật thẻ
     * @param quality Điểm số từ 0 đến 5 (0: quên hoàn toàn -> 5: nhớ hoàn hảo)
     */
    fun answerFlashcard(wordId: String, quality: Int) {
        val currentProgress = srsProgressStore[wordId] ?: ProgressItem(vocabularyId = wordId)
        
        // 1. Áp dụng thuật toán SM-2 để tính toán lịch ôn tập tiếp theo dựa theo điểm số quality
        val updatedProgress = calculateNextReviewUseCase(currentProgress, quality)
        
        // 2. Lưu trạng thái mới cập nhật vào bộ nhớ tạm
        srsProgressStore[wordId] = updatedProgress
        
        // 3. Gọi hàm làm mới lại dữ liệu để cập nhật đồng thời cả Thống kê lẫn Lộ trình học tập mới
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

        // Lấy danh sách tiến trình SRS hiện tại và chạy UseCase lọc lịch học cho hôm nay
        val allSrsItems = srsProgressStore.values.toList()
        val (reviewWords, newWords) = updateLearningPlanUseCase(allSrsItems)

        _uiState.update {
            it.copy(
                setProgressList = setProgressList,
                totalSets = sets.size,
                totalWords = totalWords,
                totalCorrect = totalCorrect,
                overallPercent = overallPercent,
                
                // Đẩy dữ liệu lộ trình học tập ra ngoài giao diện UI
                dailyReviewWords = reviewWords,
                dailyNewWords = newWords,
                isLoading = false
            )
        }
    }
}