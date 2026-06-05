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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import com.example.mobile_project.feature.learning.viewmodel.LearningViewModel
import com.example.mobile_project.ui.components.FlashcardView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun FlashcardSessionScreen(
    setId: String = "",
    onFinish: () -> Unit,
    learningViewModel: LearningViewModel = viewModel()
) {
    val words by learningViewModel.sessionWords.collectAsState()
    val currentWordIndex by learningViewModel.currentWordIndex.collectAsState()
    val isLoading by learningViewModel.isLoading.collectAsState()
    val isEvaluating by learningViewModel.isEvaluating.collectAsState()
    val errorMessage by learningViewModel.errorMessage.collectAsState()
    var showBack by remember { mutableStateOf(false) }

    LaunchedEffect(setId) {
        learningViewModel.startFlashcardSession(setId)
    }

    LaunchedEffect(currentWordIndex) {
        showBack = false
    }

    LaunchedEffect(currentWordIndex, words.size, isLoading) {
        if (!isLoading && words.isNotEmpty() && currentWordIndex >= words.size) {
            onFinish()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        Text("Flashcard", style = MaterialTheme.typography.headlineLarge)

        if (isLoading) {
            Spacer(Modifier.height(48.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            return@Column
        }

        errorMessage?.let { message ->
            Spacer(Modifier.height(16.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (words.isEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Chua co tu nao den han on hoac tu moi de hoc.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(28.dp))
            PrimaryButton("Quay lai", onClick = onFinish)
            return@Column
        }

        if (currentWordIndex >= words.size) {
            Spacer(Modifier.height(48.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            return@Column
        }

        val word = words[currentWordIndex]
        val progress = (currentWordIndex + 1).toFloat() / words.size.toFloat()
        fun evaluateCurrentWord(quality: Int) = learningViewModel.evaluateWord(quality)

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
                SecondaryButton("Lai", onClick = { evaluateCurrentWord(0) }, modifier = Modifier.weight(1f), enabled = !isEvaluating)
                SecondaryButton("Kho", onClick = { evaluateCurrentWord(1) }, modifier = Modifier.weight(1f), enabled = !isEvaluating)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("On", onClick = { evaluateCurrentWord(3) }, modifier = Modifier.weight(1f), enabled = !isEvaluating)
                PrimaryButton("De", onClick = { evaluateCurrentWord(5) }, modifier = Modifier.weight(1f), enabled = !isEvaluating)
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
