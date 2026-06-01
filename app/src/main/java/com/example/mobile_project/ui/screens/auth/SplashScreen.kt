package com.example.mobile_project.ui.screens.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.components.MimiMood
import com.example.mobile_project.ui.components.WhaleMascot
import com.example.mobile_project.ui.theme.MinLishPrimary
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface
import com.example.mobile_project.ui.theme.MinLishSurfaceContainer

@Composable
fun SplashScreen(
    errorMessage: String? = null
) {
    val transition = rememberInfiniteTransition(label = "loadingReferenceMotion")
    val ringRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing)
        ),
        label = "loadingRingRotation"
    )
    val mascotFloat by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingMascotFloat"
    )
    val progress by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingProgressFill"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RisingBubble(0.1f, 40.dp, 6000, 0)
        RisingBubble(0.25f, 20.dp, 4000, 900)
        RisingBubble(0.5f, 35.dp, 7000, 1800)
        RisingBubble(0.7f, 15.dp, 5000, 500)
        RisingBubble(0.85f, 50.dp, 8000, 2600)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(144.dp)
                    .drawBehind {
                        rotate(ringRotation) {
                            drawCircle(
                                color = MinLishPrimary,
                                radius = size.minDimension / 2f - 4.dp.toPx(),
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(18.dp.toPx(), 12.dp.toPx())
                                    )
                                )
                            )
                            drawCircle(
                                color = MinLishPrimaryContainer,
                                radius = size.minDimension / 2f - 4.dp.toPx(),
                                style = Stroke(
                                    width = 4.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(10.dp.toPx(), 22.dp.toPx()),
                                        18.dp.toPx()
                                    )
                                )
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MinLishSurface,
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .size(124.dp)
                        .graphicsLayer {
                            translationY = mascotFloat
                            rotationZ = mascotFloat * 0.12f
                        }
                        .shadow(0.dp, CircleShape)
                        .border(4.dp, MinLishSurface, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        WhaleMascot(
                            size = 112.dp,
                            mood = MimiMood.Welcome,
                            animated = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
            Text(
                text = "Mimi đang chuẩn bị bài học cho bạn...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(34.dp))
            Text(
                text = "Đang tải dữ liệu...",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            WaterProgressBar(progress = progress)

            errorMessage?.let { message ->
                Spacer(Modifier.height(22.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .width(240.dp)
            .height(16.dp)
            .background(MinLishSurfaceContainer, RoundedCornerShape(99.dp))
            .padding(1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(MinLishPrimary, RoundedCornerShape(99.dp))
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 4.dp)
                    .fillMaxWidth(0.36f)
                    .height(3.dp)
                    .background(MinLishSurface.copy(alpha = 0.34f), RoundedCornerShape(99.dp))
            )
        }
    }
}

@Composable
private fun RisingBubble(
    horizontalPosition: Float,
    size: Dp,
    durationMillis: Int,
    delayMillis: Int
) {
    val transition = rememberInfiniteTransition(label = "bubble$horizontalPosition")
    val rise by transition.animateFloat(
        initialValue = 1.08f,
        targetValue = -0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = LinearEasing
            )
        ),
        label = "bubbleRise$horizontalPosition"
    )
    val drift by transition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis / 2, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleDrift$horizontalPosition"
    )
    val alpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis / 3, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleAlpha$horizontalPosition"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth.toFloat()
        val maxHeight = constraints.maxHeight.toFloat()
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = maxWidth * horizontalPosition + drift
                    translationY = maxHeight * rise
                    this.alpha = alpha
                }
                .size(size)
                .background(MinLishSurface.copy(alpha = 0.6f), CircleShape)
                .drawBehind {
                    drawCircle(
                        color = MinLishPrimaryContainer.copy(alpha = 0.28f),
                        radius = this.size.minDimension / 2f,
                        center = Offset(this.size.width / 2f, this.size.height / 2f)
                    )
                }
        )
    }
}
