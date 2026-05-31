package com.example.mobile_project.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSurface

@Composable
fun OceanBubblyBackground(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "oceanBackground")
    val drift by transition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleDrift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
    ) {
        Bubble(
            Modifier.align(Alignment.TopStart).offset(x = (-38).dp, y = 14.dp).size(118.dp),
            0.26f,
            drift
        )
        Bubble(
            Modifier.align(Alignment.TopEnd).offset(x = 34.dp, y = 126.dp).size(66.dp),
            0.22f,
            -drift * 0.8f
        )
        Bubble(
            Modifier.align(Alignment.CenterStart).offset(x = (-46).dp).size(84.dp),
            0.18f,
            drift * 0.55f
        )
        Bubble(
            Modifier.align(Alignment.BottomEnd).offset(x = 42.dp, y = (-72).dp).size(132.dp),
            0.2f,
            -drift
        )
        content()
    }
}

@Composable
fun OceanCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MinLishSurface,
    radius: Dp = 32.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        content()
    }
}

@Composable
fun MascotBadge(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    mood: MimiMood = MimiMood.Default
) {
    Surface(
        shape = CircleShape,
        color = MinLishSurface,
        shadowElevation = 10.dp,
        tonalElevation = 4.dp,
        modifier = modifier
            .size(size)
            .border(4.dp, MinLishSurface, CircleShape)
    ) {
        WhaleMascot(size = size, mood = mood, animated = true)
    }
}

@Composable
fun OceanTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    minLines: Int = 1,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            )
        },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        minLines = minLines,
        enabled = enabled,
        singleLine = minLines == 1,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            errorContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.55f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun Bubble(modifier: Modifier, alpha: Float, drift: Float = 0f) {
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = drift
                translationY = -drift * 0.45f
            }
            .background(
                color = MinLishPrimaryContainer.copy(alpha = alpha),
                shape = CircleShape
            )
    )
}

@Preview
@Composable
fun OceanBubblyBackgroundPreview() {
    OceanBubblyBackground {
        Text("Hello, MinLish!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}
