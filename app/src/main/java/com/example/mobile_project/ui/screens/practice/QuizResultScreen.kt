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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.StatCard

@Composable
fun QuizResultScreen(onReviewWrong: () -> Unit) {
    val attempt = SampleData.quiz_attempts
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(36.dp))
        Text("Kết quả quiz", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("${attempt.scorePercent}%", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        Text("Bạn trả lời đúng ${attempt.correctAnswers}/${attempt.totalQuestions} câu", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Câu đúng", attempt.correctAnswers.toString(), R.drawable.ic_check_circle, Modifier.weight(1f))
            StatCard("Tổng câu", attempt.totalQuestions.toString(), R.drawable.ic_quiz, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Thời lượng", "${attempt.durationSeconds / 60}p", R.drawable.ic_clock, Modifier.weight(1f))
            StatCard("Điểm", "${attempt.scorePercent}%", R.drawable.ic_chart, Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Xem lại câu sai", onClick = onReviewWrong)
    }
}
