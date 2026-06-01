package com.example.mobile_project.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R

enum class MimiMood(@param:DrawableRes val assetRes: Int) {
    Default(R.drawable.mimi_whale),
    Welcome(R.drawable.mimi_chao_mung),
    NeedCare(R.drawable.mimi_an_ui),
    Celebrate(R.drawable.mimi_chuc_mung),
    Sad(R.drawable.mini_buon)
}

@Composable
fun WhaleMascot(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    mood: MimiMood = MimiMood.Default,
    animated: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "mimiIdle")
    val bob by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mimiBob"
    )
    val tilt by transition.animateFloat(
        initialValue = -1.8f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mimiTilt"
    )

    Image(
        painter = painterResource(mood.assetRes),
        contentDescription = "Linh vật cá voi MinLish",
        modifier = modifier
            .size(size)
            .graphicsLayer {
                if (animated) {
                    translationY = bob
                    rotationZ = tilt
                }
            }
    )
}

@Preview
@Composable
fun WhaleMascotPreview() {
    WhaleMascot(mood = MimiMood.Welcome)
}
