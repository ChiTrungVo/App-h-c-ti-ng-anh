package com.example.mobile_project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.ui.theme.MinLishErrorContainer
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer
import com.example.mobile_project.ui.theme.MinLishSuccessContainer

@Composable
fun QuizOptionCard(
    text: String,
    state: QuizOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = when (state) {
        QuizOptionState.Correct -> MinLishSuccessContainer
        QuizOptionState.Incorrect -> MinLishErrorContainer
        QuizOptionState.Selected -> MaterialTheme.colorScheme.primaryContainer
        QuizOptionState.Default -> MaterialTheme.colorScheme.surface
    }
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.72f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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