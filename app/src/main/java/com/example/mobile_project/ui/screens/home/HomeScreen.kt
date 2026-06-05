package com.example.mobile_project.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.R
import com.example.mobile_project.ui.components.OceanBubblyBackground
import com.example.mobile_project.ui.theme.MinLishPrimary
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSecondary
import com.example.mobile_project.ui.theme.MinLishSecondaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.MinLishSurfaceContainer
import com.example.mobile_project.ui.theme.MinLishTertiary
import com.example.mobile_project.ui.theme.MinLishTertiaryContainer
import com.example.mobile_project.ui.theme.MinLishTextPrimary
import com.example.mobile_project.ui.theme.MinLishTextSecondary
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun HomeScreen(
    displayName: String,
    dailyTargetMinutes: Int,
    studiedMinutesToday: Int,
    totalWordsLearned: Int,
    streakDays: Int,
    quizAccuracy: Int,
    onStartLearning: () -> Unit,
    onProfileClick: () -> Unit,
    onAddSet: () -> Unit,
    onVocabulary: () -> Unit,
    onQuiz: () -> Unit,
    onProgress: () -> Unit
) {
    OceanBubblyBackground(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            HomeHero(
                displayName = displayName,
                onProfileClick = onProfileClick
            )
            Spacer(Modifier.height(20.dp))
            DailyGoalSticker(
                dailyTargetMinutes = dailyTargetMinutes,
                studiedMinutesToday = studiedMinutesToday
            )
            Spacer(Modifier.height(16.dp))
            StartLearningButton(onClick = onStartLearning)
            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat(
                    value = "$totalWordsLearned",
                    label = "Từ đã học",
                    icon = R.drawable.ic_book,
                    color = MinLishPrimary,
                    modifier = Modifier.weight(1f)
                )
                MiniStat(
                    value = "$streakDays",
                    label = "Chuỗi ngày",
                    icon = R.drawable.ic_clock,
                    color = MinLishTertiary,
                    modifier = Modifier.weight(1f)
                )
                MiniStat(
                    value = "$quizAccuracy%",
                    label = "Chính xác",
                    icon = R.drawable.ic_check_circle,
                    color = MinLishSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))
            SectionTitle("Tiện ích nhanh")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Từ vựng",
                    icon = R.drawable.ic_vocabulary,
                    containerColor = MinLishPrimaryContainer.copy(alpha = 0.72f),
                    shadowColor = MinLishPrimary.copy(alpha = 0.18f),
                    iconTint = MinLishPrimary,
                    onClick = onVocabulary,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Làm bài",
                    icon = R.drawable.ic_quiz,
                    containerColor = MinLishTertiaryContainer.copy(alpha = 0.76f),
                    shadowColor = MinLishTertiary.copy(alpha = 0.22f),
                    iconTint = MinLishTertiary,
                    onClick = onQuiz,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Tiến độ",
                    icon = R.drawable.ic_chart,
                    containerColor = MinLishSecondaryContainer.copy(alpha = 0.78f),
                    shadowColor = MinLishSecondary.copy(alpha = 0.18f),
                    iconTint = MinLishSecondary,
                    onClick = onProgress,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Thêm bộ",
                    icon = R.drawable.ic_add,
                    containerColor = MinLishSurface,
                    shadowColor = MinLishPrimary.copy(alpha = 0.16f),
                    iconTint = MinLishPrimary,
                    onClick = onAddSet,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(132.dp))
        }
    }
}

@Composable
private fun HomeHero(
    displayName: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        HomeMascotImage()
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(
                topStart = 26.dp,
                topEnd = 26.dp,
                bottomEnd = 26.dp,
                bottomStart = 8.dp
            ),
            color = MinLishSurface,
            border = BorderStroke(2.dp, MinLishPrimaryContainer.copy(alpha = 0.74f)),
            shadowElevation = 2.dp
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Chào bạn, $displayName",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        color = MinLishTextPrimary
                    )
                    Surface(
                        onClick = onProfileClick,
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MinLishPrimaryContainer.copy(alpha = 0.82f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(R.drawable.ic_profile),
                                contentDescription = "Cá nhân",
                                modifier = Modifier.size(20.dp),
                                colorFilter = ColorFilter.tint(MinLishPrimary)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sẵn sàng lặn xuống đại dương từ vựng hôm nay chưa?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinLishTextSecondary
                )
            }
        }
    }
}

@Composable
private fun HomeMascotImage() {
    Surface(
        modifier = Modifier.size(104.dp),
        shape = RoundedCornerShape(28.dp),
        color = MinLishSurface,
        border = BorderStroke(3.dp, MinLishSurface),
        shadowElevation = 6.dp
    ) {
        Image(
            painter = painterResource(R.drawable.mimi_whale),
            contentDescription = "Mimi học tiếng Anh",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DailyGoalSticker(
    dailyTargetMinutes: Int,
    studiedMinutesToday: Int
) {
    val targetMinutes = dailyTargetMinutes.coerceAtLeast(1)
    val completedMinutes = studiedMinutesToday.coerceIn(0, targetMinutes)
    val progress = completedMinutes.toFloat() / targetMinutes.toFloat()

    StickerSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(188.dp),
        containerColor = MinLishSurface,
        shadowColor = MinLishPrimary.copy(alpha = 0.18f),
        borderColor = MinLishPrimaryContainer.copy(alpha = 0.7f),
        radius = 34.dp
    ) {
        Box(Modifier.fillMaxSize()) {
            BubbleDecoration(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-24).dp)
                    .size(90.dp),
                color = MinLishPrimaryContainer.copy(alpha = 0.28f)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BubbleIcon(R.drawable.ic_check_circle, MinLishTertiary)
                    Text(
                        text = "Mục tiêu hôm nay",
                        modifier = Modifier.padding(start = 10.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MinLishTextPrimary
                    )
                }
                Spacer(Modifier.height(16.dp))
                GoalProgressRow(
                    label = "$targetMinutes phút học",
                    value = "$completedMinutes/$targetMinutes",
                    progress = progress,
                    color = MinLishPrimary,
                    icon = R.drawable.ic_clock
                )
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
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "homeGoalProgress")
    val transition = rememberInfiniteTransition(label = "homeProgressShimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homeProgressShimmerAlpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        BubbleIcon(icon, color)
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MinLishTextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
    Spacer(Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(MinLishSurfaceContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(99.dp))
                .background(color)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MinLishSurface.copy(alpha = shimmerAlpha))
            )
        }
    }
}

@Composable
private fun StartLearningButton(onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "homeCtaFloat")
    val floatOffset by transition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "homeCtaFloatOffset"
    )

    StickerSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer { translationY = floatOffset },
        containerColor = MinLishPrimary,
        shadowColor = Color(0xFF004E69),
        borderColor = Color.Transparent,
        radius = 28.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_learning),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MinLishSurface)
            )
            Text(
                text = "Bắt đầu học",
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MinLishSurface
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleLarge,
        color = MinLishTextPrimary
    )
}

@Composable
private fun QuickActionCard(
    title: String,
    @DrawableRes icon: Int,
    containerColor: Color,
    shadowColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    StickerSurface(
        modifier = modifier.height(126.dp),
        containerColor = containerColor,
        shadowColor = shadowColor,
        borderColor = MinLishSurface.copy(alpha = 0.72f),
        radius = 28.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BubbleIcon(icon, iconTint, size = 54.dp)
            Spacer(Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MinLishTextPrimary,
                textAlign = TextAlign.Center
            )
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
    StickerSurface(
        modifier = modifier.height(112.dp),
        containerColor = MinLishSurface,
        shadowColor = color.copy(alpha = 0.16f),
        borderColor = MinLishPrimaryContainer.copy(alpha = 0.42f),
        radius = 26.dp
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
            Text(value, style = MaterialTheme.typography.titleLarge, color = MinLishTextPrimary)
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MinLishTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun BubbleIcon(
    @DrawableRes icon: Int,
    color: Color,
    size: Dp = 38.dp
) {
    Surface(
        shape = CircleShape,
        color = MinLishSurface,
        shadowElevation = 2.dp,
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

@Composable
private fun StickerSurface(
    modifier: Modifier = Modifier,
    containerColor: Color,
    shadowColor: Color,
    borderColor: Color,
    radius: Dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(modifier = modifier.padding(bottom = 6.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .background(shadowColor, shape)
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            shape = shape,
            color = containerColor,
            border = BorderStroke(1.5.dp, borderColor)
        ) {
            content()
        }
    }
}

@Composable
private fun BubbleDecoration(
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier = modifier
            .background(color, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    Mobile_projectTheme {
        HomeScreen(
            displayName = "Minh Anh",
            dailyTargetMinutes = 30,
            studiedMinutesToday = 12,
            totalWordsLearned = 120,
            streakDays = 5,
            quizAccuracy = 85,
            onStartLearning = {},
            onProfileClick = {},
            onAddSet = {},
            onVocabulary = {},
            onQuiz = {},
            onProgress = {}
        )
    }
}
