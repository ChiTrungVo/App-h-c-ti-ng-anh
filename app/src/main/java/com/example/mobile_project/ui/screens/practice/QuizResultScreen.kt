package com.example.mobile_project.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.feature.practice.viewmodel.PracticeViewModel
import com.example.mobile_project.ui.components.CelebrationSprinkles
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun QuizResultScreen(
    onReviewWrong: () -> Unit,
    onBackToVocabulary: () -> Unit,
    onRetry: () -> Unit,
    practiceViewModel: PracticeViewModel = viewModel()
) {
    val state by practiceViewModel.uiState.collectAsState()

    val mood = if (state.scorePercent >= 80) MimiMood.Celebrate else MimiMood.NeedCare
    val durationMinutes = state.durationSeconds / 60
    val durationSecs = state.durationSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(36.dp))

        if (state.scorePercent >= 80) CelebrationSprinkles()

        WhaleMascot(size = 122.dp, mood = mood, animated = true)

        Spacer(Modifier.height(12.dp))

        Text("Kết quả quiz", style = MaterialTheme.typography.headlineLarge)

        if (state.setTitle.isNotBlank()) {
            Text(
                state.setTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(8.dp))

        // Điểm lớn
        Text(
            "${state.scorePercent}%",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "Bạn trả lời đúng ${state.correctCount}/${state.totalQuestions} câu",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        // StatCards
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                "Câu đúng",
                "${state.correctCount}/${state.totalQuestions}",
                R.drawable.ic_check_circle,
                Modifier.weight(1f)
            )
            StatCard(
                "Câu sai",
                "${state.totalQuestions - state.correctCount}",
                R.drawable.ic_close_circle,
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                "Điểm",
                "${state.scorePercent}%",
                R.drawable.ic_chart,
                Modifier.weight(1f)
            )
            StatCard(
                "Thời lượng",
                if (durationMinutes > 0) "${durationMinutes}p ${durationSecs}s" else "${durationSecs}s",
                R.drawable.ic_clock,
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Nếu có câu sai, cho phép xem lại
        if (state.correctCount < state.totalQuestions) {
            PrimaryButton("Làm lại bài quiz", onClick = onRetry)
            Spacer(Modifier.height(12.dp))
        }

        SecondaryButton("Về danh sách từ", onClick = onBackToVocabulary)

        Spacer(Modifier.height(132.dp))
    }
}

