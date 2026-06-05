package com.example.mobile_project.ui.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.ui.components.FlashcardView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun FlashcardSessionScreen(
    setId: String = "",
    onFinish: () -> Unit
) {
    val words = remember(setId, VocabularyDemoStore.vocabularies.size) {
        if (setId.isBlank()) {
            VocabularyDemoStore.vocabularies.toList()
        } else {
            VocabularyDemoStore.wordsForSet(setId)
        }
    }
    var currentWordIndex by remember(setId, words.size) { mutableStateOf(0) }
    var showBack by remember { mutableStateOf(false) }

    LaunchedEffect(setId, currentWordIndex) {
        showBack = false
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Text("Flashcard", style = MaterialTheme.typography.headlineLarge)

        if (words.isEmpty()) {
            Text(
                "Chua co tu nao de hoc.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(28.dp))
            PrimaryButton("Quay lai", onClick = onFinish)
            return@Column
        }

        val word = words[currentWordIndex]
        val progress = (currentWordIndex + 1).toFloat() / words.size.toFloat()
        fun evaluateCurrentWord() {
            if (currentWordIndex < words.lastIndex) {
                currentWordIndex += 1
                showBack = false
            } else {
                onFinish()
            }
        }

        Text(
            "${currentWordIndex + 1}/${words.size} tu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
        Spacer(Modifier.height(28.dp))
        FlashcardView(word = word, showBack = showBack)
        Spacer(Modifier.height(22.dp))

        if (!showBack) {
            PrimaryButton("Lat the", onClick = { showBack = true })
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("Lai", onClick = { evaluateCurrentWord() }, modifier = Modifier.weight(1f))
                SecondaryButton("Kho", onClick = { evaluateCurrentWord() }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("On", onClick = { evaluateCurrentWord() }, modifier = Modifier.weight(1f))
                PrimaryButton("De", onClick = { evaluateCurrentWord() }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FlashcardSessionScreenPreview() {
    Mobile_projectTheme {
        FlashcardSessionScreen(onFinish = {})
    }
}
