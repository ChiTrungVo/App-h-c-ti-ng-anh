package com.example.mobile_project.ui.screens.learning

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
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot

@Composable
fun SessionResultScreen(
    onContinue: () -> Unit,
    onReview: () -> Unit
) {
    val stats = SampleData.daily_learning_stats
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(36.dp))
        WhaleMascot(size = 128.dp, mood = MimiMood.Celebrate)
        Spacer(Modifier.height(16.dp))
        Text("Hoàn thành buổi học", style = MaterialTheme.typography.headlineLarge)
        Text("Bạn đã giữ nhịp học rất tốt hôm nay.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Từ đã học", stats.learnedWords.toString(), R.drawable.ic_book, Modifier.weight(1f))
            StatCard("Từ đã ôn", stats.reviewedWords.toString(), R.drawable.ic_flashcard, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Từ đã ghi nhớ", stats.masteredWords.toString(), R.drawable.ic_check_circle, Modifier.weight(1f))
            StatCard("Phút học", stats.studyMinutes.toString(), R.drawable.ic_clock, Modifier.weight(1f))
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Tiếp tục", onClick = onContinue)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Ôn lại", onClick = onReview)
    }
}
