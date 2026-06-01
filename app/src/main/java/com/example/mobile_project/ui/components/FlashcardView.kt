package com.example.mobile_project.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun FlashcardView(
    word: VocabularyWord,
    showBack: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (showBack) 180f else 0f,
        label = "flashcardFlip"
    )
    val isBackVisible = rotation > 90f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            },
        shape = RoundedCornerShape(36.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .graphicsLayer {
                    if (isBackVisible) rotationY = 180f
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isBackVisible) {
                Text(word.meaning, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(word.definition, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(word.example, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            } else {
                Text(word.word, style = MaterialTheme.typography.displayLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(word.pronunciation, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
//data class VocabularyWord(
//    val wordId: String,
//    val setId: String,
//    val userId: String,
//    val word: String,
//    val pronunciation: String,
//    val meaning: String,
//    val definition: String,
//    val example: String,
//    val collocations: List<String>,
//    val note: String,
//    val imageUrl: String?
//)
@Preview
@Composable
fun FlashcardViewPreview() {
    FlashcardView(
        word = VocabularyWord(
            wordId = "1",
            setId = "1",
            userId = "user123",
            word = "Hello",
            pronunciation = "həˈloʊ",
            meaning = "Xin chào",
            definition = "A greeting or expression of goodwill.",
            example = "She said hello to everyone in the room.",
            collocations = listOf("say hello", "greet with hello"),
            note = "Commonly used as a greeting.",
            imageUrl = null
        ),
        showBack = false
    )
}
