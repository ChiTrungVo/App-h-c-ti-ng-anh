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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.feature.learning.viewmodel.LearningViewModel
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun DailyLearningPlanScreen(
    onFlashcard: (String) -> Unit,
    onQuiz: (String) -> Unit,
    learningViewModel: LearningViewModel = viewModel()
) {
    val stats by learningViewModel.dailyStats.collectAsState()
    val dueWordsCount by learningViewModel.dueWordsCount.collectAsState()
    val newWordsCount by learningViewModel.newWordsCount.collectAsState()
    val availableSets by learningViewModel.availableSets.collectAsState()
    val selectedSetId by learningViewModel.selectedSetId.collectAsState()
    val isLoading by learningViewModel.isLoading.collectAsState()
    val errorMessage by learningViewModel.errorMessage.collectAsState()
    val selectedSet = availableSets.firstOrNull { it.setId == selectedSetId }

    LaunchedEffect(Unit) {
        learningViewModel.loadDailyPlan()
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Kế hoạch hôm nay", style = MaterialTheme.typography.headlineLarge)
                Text(
                    selectedSet?.let { "Bộ từ: ${it.title}" }
                        ?: "Chọn hoặc tạo một bộ từ để bắt đầu học.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            WhaleMascot(size = 76.dp)
        }
        Spacer(Modifier.height(22.dp))

        if (availableSets.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableSets, key = { it.setId }) { set ->
                    val selected = set.setId == selectedSetId
                    AssistChip(
                        onClick = { learningViewModel.loadDailyPlan(set.setId) },
                        label = { Text(set.title) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            labelColor = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(22.dp))
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Từ mới", newWordsCount.toString(), R.drawable.ic_book, Modifier.weight(1f))
            StatCard("Đến hạn ôn", dueWordsCount.toString(), R.drawable.ic_flashcard, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Thời gian học", "${stats?.studyMinutes ?: 0}p", R.drawable.ic_clock, Modifier.weight(1f))
            StatCard("Đã ghi nhớ", (stats?.wordsMastered ?: 0).toString(), R.drawable.ic_check_circle, Modifier.weight(1f))
        }
        Spacer(Modifier.height(22.dp))
        Text("Từ cần ôn", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(10.dp))
        LearningEntryCard(
            "Flashcard",
            "Học $newWordsCount từ mới và ôn $dueWordsCount từ đến hạn bằng thẻ lật.",
            R.drawable.ic_flashcard,
            enabled = selectedSetId.isNotBlank(),
            onClick = { onFlashcard(selectedSetId) }
        )
        Spacer(Modifier.height(12.dp))
        LearningEntryCard(
            "Quiz nhanh",
            "Kiểm tra lại bộ từ đang học trong 5 phút.",
            R.drawable.ic_quiz,
            enabled = selectedSetId.isNotBlank(),
            onClick = { onQuiz(selectedSetId) }
        )
        Spacer(Modifier.height(12.dp))
        LearningEntryCard(
            "Luyện tập",
            "Làm bài trắc nghiệm với bộ từ đang chọn.",
            R.drawable.ic_practice,
            enabled = selectedSetId.isNotBlank(),
            onClick = { onQuiz(selectedSetId) }
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            "Bắt đầu học",
            onClick = { onFlashcard(selectedSetId) },
            enabled = selectedSetId.isNotBlank() && !isLoading
        )
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun LearningEntryCard(
    title: String,
    description: String,
    iconRes: Int,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = { if (enabled) onClick() },
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
