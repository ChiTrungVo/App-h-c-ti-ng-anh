package com.example.mobile_project.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.feature.practice.viewmodel.PracticeViewModel
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.QuizOptionCard
import com.example.mobile_project.ui.components.QuizOptionState
import com.example.mobile_project.ui.components.SecondaryButton

@Composable
fun QuizScreen(
    setId: String,
    onResult: () -> Unit,
    practiceViewModel: PracticeViewModel = viewModel()
) {
    val state by practiceViewModel.uiState.collectAsState()

    // Khởi động quiz khi màn hình được mở
    LaunchedEffect(setId) {
        practiceViewModel.startQuiz(setId)
    }

    // Chuyển sang màn kết quả khi quiz xong
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onResult()
    }

    val question = state.currentQuestion ?: return

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))

        // Tiêu đề + tên bộ từ
        Text("Quiz từ vựng", style = MaterialTheme.typography.headlineLarge)
        Text(
            state.setTitle.ifBlank { "Bộ từ đã chọn" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        // Số câu hiện tại
        Text(
            "Câu ${state.currentIndex + 1}/${state.totalQuestions}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(10.dp))

        // Thanh tiến độ
        LinearProgressIndicator(
            progress = {
                if (state.totalQuestions == 0) 0f
                else (state.currentIndex + 1).toFloat() / state.totalQuestions
            },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )

        Spacer(Modifier.height(28.dp))

        // Câu hỏi
        Text(question.questionText, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(18.dp))

        // 4 đáp án
        question.options.forEach { option ->
            val optionState = when {
                !state.isChecked && state.selectedAnswer == option -> QuizOptionState.Selected
                !state.isChecked -> QuizOptionState.Default
                option == question.correctAnswer -> QuizOptionState.Correct
                option == state.selectedAnswer -> QuizOptionState.Incorrect
                else -> QuizOptionState.Default
            }

            QuizOptionCard(
                text = option,
                state = optionState,
                onClick = { practiceViewModel.selectAnswer(option) }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(14.dp))

        // Phản hồi sau khi kiểm tra
        if (state.isChecked) {
            Text(
                text = if (state.isCorrect)
                    "Chính xác! ✓"
                else
                    "Chưa đúng. Đáp án đúng là: ${question.correctAnswer}",
                color = if (state.isCorrect)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(14.dp))
        }

        Spacer(Modifier.height(4.dp))

        // Nút Kiểm tra
        if (!state.isChecked) {
            SecondaryButton(
                "Kiểm tra",
                onClick = { practiceViewModel.checkAnswer() },
                enabled = state.selectedAnswer != null
            )
        }

        Spacer(Modifier.height(10.dp))

        // Nút Câu tiếp theo / Nộp bài
        PrimaryButton(
            text = if (state.isLastQuestion) "Nộp bài" else "Câu tiếp theo",
            onClick = { practiceViewModel.nextQuestion() },
            enabled = state.isChecked
        )
    }
}

