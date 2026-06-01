package com.example.mobile_project.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.MinLishTopBar
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.MinLishTertiaryContainer
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun HomeScreen(
    onStartLearning: () -> Unit,
    onProfileClick: () -> Unit,
    onAddSet: () -> Unit,
    onQuiz: () -> Unit,
    onProgress: () -> Unit
) {
    val stats = SampleData.daily_learning_stats
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        MinLishTopBar(title = "MinLish", onProfileClick = onProfileClick)
        Spacer(Modifier.height(22.dp))
        Text("Chào bạn, ${SampleData.user.displayName}", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Sẵn sàng cho bài học hôm nay chưa?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        DailyGoalBento(onStartLearning = onStartLearning)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Từ vựng", R.drawable.ic_vocabulary, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.74f), onAddSet, Modifier.weight(1f))
            QuickActionCard("Làm bài", R.drawable.ic_quiz, MinLishTertiaryContainer.copy(alpha = 0.7f), onQuiz, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        PrimaryButton("Bắt đầu học", onClick = onStartLearning)
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniStat("120", "Từ đã học", R.drawable.ic_book, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            MiniStat("${stats.streakDays}", "Chuỗi ngày", R.drawable.ic_clock, MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
            MiniStat("${stats.quizAccuracy}%", "Chính xác", R.drawable.ic_check_circle, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Tiến độ", R.drawable.ic_chart, MinLishSurface, onProgress, Modifier.weight(1f))
            QuickActionCard("Flashcard", R.drawable.ic_flashcard, MinLishSurface, onStartLearning, Modifier.weight(1f))
        }
        Spacer(Modifier.height(132.dp))
    }
}

@Composable
private fun DailyGoalBento(onStartLearning: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = MinLishSurface),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(74.dp)
                    .background(MinLishPrimaryContainer.copy(alpha = 0.3f), CircleShape)
            )
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BubbleIcon(R.drawable.ic_check_circle, MaterialTheme.colorScheme.tertiary)
                    Text(
                        "Mục tiêu hôm nay",
                        modifier = Modifier.padding(start = 10.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(16.dp))
                GoalProgressRow("15 phút học", "10/15", 0.66f, MaterialTheme.colorScheme.primary, R.drawable.ic_clock)
                Spacer(Modifier.height(14.dp))
                GoalProgressRow("20 từ mới", "5/20", 0.25f, MaterialTheme.colorScheme.secondary, R.drawable.ic_vocabulary)
                Spacer(Modifier.height(18.dp))
                PrimaryButton("Học ngay", onClick = onStartLearning)
            }
        }
    }
}

@Composable
private fun GoalProgressRow(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    @DrawableRes icon: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BubbleIcon(icon, color)
        Text(
            label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.labelLarge, color = color)
    }
    Spacer(Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(99.dp)),
        color = color,
        trackColor = MaterialTheme.colorScheme.surfaceContainer
    )
}

@Composable
private fun QuickActionCard(
    title: String,
    @DrawableRes icon: Int,
    container: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(128.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.58f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BubbleIcon(icon, MaterialTheme.colorScheme.primary, size = 54.dp)
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun MiniStat(
    value: String,
    label: String,
    @DrawableRes icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MinLishSurface),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(color)
            )
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BubbleIcon(@DrawableRes icon: Int, color: Color, size: androidx.compose.ui.unit.Dp = 38.dp) {
    Surface(
        shape = CircleShape,
        color = MinLishSurface,
        shadowElevation = 3.dp,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(size * 0.48f),
                colorFilter = ColorFilter.tint(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    Mobile_projectTheme {
        HomeScreen(
            onStartLearning = {},
            onProfileClick = {},
            onAddSet = {},
            onQuiz = {},
            onProgress = {}
        )
    }
}
