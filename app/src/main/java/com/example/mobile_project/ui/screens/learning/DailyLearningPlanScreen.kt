package com.example.mobile_project.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun DailyLearningPlanScreen(
    onFlashcard: () -> Unit,
    onQuiz: () -> Unit
) {
    val stats = SampleData.daily_learning_stats
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Kế hoạch hôm nay", style = MaterialTheme.typography.headlineLarge)
                Text("Học gọn trong 20 phút với nhịp ôn phù hợp.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            WhaleMascot(size = 76.dp)
        }
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Từ mới", "10", R.drawable.ic_book, Modifier.weight(1f))
            StatCard("Đến hạn ôn", stats.wordsReviewed.toString(), R.drawable.ic_flashcard, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Thời gian học", "${stats.studyMinutes}p", R.drawable.ic_clock, Modifier.weight(1f))
            StatCard("Đã ghi nhớ", stats.wordsMastered.toString(), R.drawable.ic_check_circle, Modifier.weight(1f))
        }
        Spacer(Modifier.height(22.dp))
        Text("Từ cần ôn", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        LearningEntryCard("Flashcard", "Ôn 18 từ đến hạn bằng thẻ lật.", R.drawable.ic_flashcard, onFlashcard)
        Spacer(Modifier.height(12.dp))
        LearningEntryCard("Quiz nhanh", "Kiểm tra lại các từ vừa học trong 5 phút.", R.drawable.ic_quiz, onQuiz)
        Spacer(Modifier.height(12.dp))
        LearningEntryCard("Luyện tập", "Khung giao diện cho trắc nghiệm, điền từ, nghe và ghép cặp.", R.drawable.ic_practice, onQuiz)
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Bắt đầu học", onClick = onFlashcard)
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun LearningEntryCard(
    title: String,
    description: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.height(34.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyLearningPlanScreenPreview() {
    Mobile_projectTheme {
        DailyLearningPlanScreen(
            onFlashcard = {},
            onQuiz = {}
        )
    }
}
