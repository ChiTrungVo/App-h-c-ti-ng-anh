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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.FlashcardView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton

@Composable
fun FlashcardSessionScreen(onFinish: () -> Unit) {
    val word = SampleData.vocabularies.first()
    var showBack by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("Flashcard", style = MaterialTheme.typography.headlineLarge)
        Text("3/12 từ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        LinearProgressIndicator(progress = { 0.25f }, modifier = Modifier.fillMaxWidth().height(10.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primaryContainer)
        Spacer(Modifier.height(28.dp))
        FlashcardView(word = word, showBack = showBack)
        Spacer(Modifier.height(22.dp))
        if (!showBack) {
            PrimaryButton("Lật thẻ", onClick = { showBack = true })
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("Lại", onClick = { showBack = false }, modifier = Modifier.weight(1f))
                SecondaryButton("Khó", onClick = { showBack = false }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("Ổn", onClick = { showBack = false }, modifier = Modifier.weight(1f))
                PrimaryButton("Dễ", onClick = onFinish, modifier = Modifier.weight(1f))
            }
        }
    }
}
