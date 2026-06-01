package com.example.mobile_project.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishTertiaryContainer

@Composable
fun CelebrationSprinkles(
    modifier: Modifier = Modifier,
    active: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "celebrationSprinkles")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sprinklePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        val progress = if (active) phase else 0f
        val dots = listOf(
            Triple(0.18f, 0.52f, MinLishPrimaryContainer),
            Triple(0.32f, 0.2f, MinLishTertiaryContainer),
            Triple(0.5f, 0.44f, Color(0xFF93F1FD)),
            Triple(0.68f, 0.18f, MinLishTertiaryContainer),
            Triple(0.82f, 0.55f, MinLishPrimaryContainer)
        )

        dots.forEachIndexed { index, (xRatio, yRatio, color) ->
            val wave = ((progress + index * 0.18f) % 1f)
            val x = size.width * xRatio
            val y = size.height * yRatio - wave * 18f
            drawCircle(
                color = color.copy(alpha = 0.35f + (1f - wave) * 0.45f),
                radius = 5f + index % 2 * 2f,
                center = Offset(x, y)
            )
        }
    }
}
