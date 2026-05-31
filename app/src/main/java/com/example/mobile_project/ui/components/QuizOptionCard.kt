package com.example.mobile_project.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.theme.MinLishErrorContainer
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSuccessContainer
import kotlin.math.roundToInt

@Composable
fun QuizOptionCard(
    text: String,
    state: QuizOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetContainer = when (state) {
        QuizOptionState.Correct -> MinLishSuccessContainer
        QuizOptionState.Incorrect -> MinLishErrorContainer
        QuizOptionState.Selected -> MaterialTheme.colorScheme.primaryContainer
        QuizOptionState.Default -> MaterialTheme.colorScheme.surface
    }
    val container by animateColorAsState(targetContainer, label = "quizOptionColor")
    val elevation by animateDpAsState(
        targetValue = if (state == QuizOptionState.Default) 4.dp else 8.dp,
        label = "quizOptionElevation"
    )
    val scale = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }

    LaunchedEffect(state) {
        if (state == QuizOptionState.Correct || state == QuizOptionState.Selected) {
            scale.animateTo(1.025f, tween(120))
            scale.animateTo(1f, tween(160))
        }
        if (state == QuizOptionState.Incorrect) {
            shake.animateTo(-10f, tween(60))
            shake.animateTo(10f, tween(70))
            shake.animateTo(-6f, tween(60))
            shake.animateTo(0f, tween(90))
        }
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shake.value.roundToInt(), 0) }
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp), style = MaterialTheme.typography.bodyLarge)
    }
}

enum class QuizOptionState {
    Default,
    Selected,
    Correct,
    Incorrect
}

@Preview
@Composable
fun QuizOptionCardPreview() {
    Column {
        QuizOptionCard(text = "Option 1", state = QuizOptionState.Default, onClick = {})
        QuizOptionCard(text = "Option 2", state = QuizOptionState.Selected, onClick = {})
        QuizOptionCard(text = "Option 3", state = QuizOptionState.Correct, onClick = {})
        QuizOptionCard(text = "Option 4", state = QuizOptionState.Incorrect, onClick = {})
    }
}
