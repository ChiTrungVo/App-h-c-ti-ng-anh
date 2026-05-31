package com.example.mobile_project.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.StatCard
import com.example.mobile_project.ui.components.WhaleMascot
import com.example.mobile_project.ui.theme.MinLishPrimaryLight
import com.example.mobile_project.ui.theme.MinLishWave

@Composable
fun ProgressDashboardScreen(
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val stats = SampleData.daily_learning_stats
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Row {
            Column(Modifier.weight(1f)) {
                Text("Tiến độ học tập", style = MaterialTheme.typography.headlineLarge)
                Text("Theo dõi nhịp học và kết quả quiz.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            WhaleMascot(size = 72.dp)
        }
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Chuỗi ngày học", stats.streakDays.toString(), R.drawable.ic_clock, Modifier.weight(1f))
            StatCard("Từ đã ghi nhớ", stats.masteredWords.toString(), R.drawable.ic_check_circle, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Độ chính xác quiz", "${stats.quizAccuracy}%", R.drawable.ic_quiz, Modifier.weight(1f))
            StatCard("Phút hôm nay", stats.studyMinutes.toString(), R.drawable.ic_chart, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Từ đã học", stats.learnedWords.toString(), R.drawable.ic_book, Modifier.weight(1f))
            StatCard("Từ đã ôn", stats.reviewedWords.toString(), R.drawable.ic_flashcard, Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Biểu đồ tuần", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                    val barWidth = size.width / 11f
                    val values = listOf(0.45f, 0.62f, 0.4f, 0.78f, 0.56f, 0.9f, 0.7f)
                    values.forEachIndexed { index, value ->
                        val left = index * barWidth * 1.55f + barWidth
                        drawRoundRect(
                            color = MinLishPrimaryLight,
                            topLeft = Offset(left, size.height * (1f - value)),
                            size = Size(barWidth, size.height * value),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f)
                        )
                    }
                    drawCircle(Color(0x55BDEFFF), radius = 24f, center = Offset(size.width * 0.82f, size.height * 0.18f))
                    drawCircle(MinLishWave, radius = 10f, center = Offset(size.width * 0.72f, size.height * 0.3f))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        ProgressEntryCard("Từ cần ôn", "18 từ đang chờ bạn quay lại hôm nay.", R.drawable.ic_flashcard, onProfileClick)
        Spacer(Modifier.height(12.dp))
        ProgressEntryCard("Lịch sử quiz gần đây", "Lần gần nhất đạt 86% trong 4 phút.", R.drawable.ic_quiz, onProfileClick)
        Spacer(Modifier.height(12.dp))
        ProgressEntryCard("Hồ sơ", "Xem thông tin cá nhân và mục tiêu học tập.", R.drawable.ic_profile, onProfileClick)
        Spacer(Modifier.height(12.dp))
        ProgressEntryCard("Cài đặt nhắc học", "Giữ chuỗi ngày bằng lịch nhắc nhẹ nhàng.", R.drawable.ic_bell, onNotificationsClick)
    }
}

@Composable
private fun ProgressEntryCard(
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
