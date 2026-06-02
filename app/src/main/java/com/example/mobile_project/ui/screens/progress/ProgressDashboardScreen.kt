package com.example.mobile_project.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_project.R
import com.example.mobile_project.feature.progress.viewmodel.ProgressViewModel
import com.example.mobile_project.feature.progress.viewmodel.SetProgress
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot

@Composable
fun ProgressDashboardScreen(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    progressViewModel: ProgressViewModel = viewModel()
) {
    val state by progressViewModel.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Tiến độ học tập", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Tiến độ quiz theo từng bộ từ.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            WhaleMascot(size = 72.dp)
        }

        Spacer(Modifier.height(20.dp))

        // Tổng quan
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                "Bộ từ",
                state.totalSets.toString(),
                R.drawable.ic_book,
                Modifier.weight(1f)
            )
            StatCard(
                "Tổng từ vựng",
                state.totalWords.toString(),
                R.drawable.ic_flashcard,
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                "Từ đã đúng",
                state.totalCorrect.toString(),
                R.drawable.ic_check_circle,
                Modifier.weight(1f)
            )
            StatCard(
                "Tiến độ chung",
                "${(state.overallPercent * 100).toInt()}%",
                R.drawable.ic_chart,
                Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Tiêu đề danh sách bộ từ
        Text(
            "Tiến độ theo bộ từ",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (state.setProgressList.isEmpty()) {
            Text(
                "Chưa có bộ từ nào. Hãy tạo bộ từ và bắt đầu quiz!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.setProgressList.forEach { setProgress ->
                SetProgressCard(setProgress = setProgress)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun SetProgressCard(setProgress: SetProgress) {
    val percent = (setProgress.progressPercent * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Tên bộ từ + %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setProgress.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        percent >= 80 -> MaterialTheme.colorScheme.primary
                        percent >= 50 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Thanh tiến độ
            LinearProgressIndicator(
                progress = { setProgress.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = when {
                    percent >= 80 -> MaterialTheme.colorScheme.primary
                    percent >= 50 -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.tertiary
                },
                trackColor = MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(Modifier.height(6.dp))

            // Chi tiết
            if (setProgress.quizzedWords > 0) {
                Text(
                    "${setProgress.correctWords}/${setProgress.totalWords} từ trả lời đúng",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "${setProgress.totalWords} từ · Chưa quiz lần nào",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}