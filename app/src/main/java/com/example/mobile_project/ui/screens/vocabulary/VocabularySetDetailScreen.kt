package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.R
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.ui.components.EmptyStateView
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.SecondaryButton
import com.example.mobile_project.ui.components.WordCard

@Composable
fun VocabularySetDetailScreen(
    setId: String,
    onAddWord: () -> Unit,
    onEditWord: (String) -> Unit,
    onEditSet: () -> Unit,
    onStartLearning: () -> Unit,
    onQuiz: () -> Unit
) {
    val set = VocabularyDemoStore.getSet(setId)
    val words = VocabularyDemoStore.wordsForSet(setId)
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(set?.title ?: "Không tìm thấy bộ từ", style = MaterialTheme.typography.headlineLarge)
            Text("${set?.wordCount ?: 0} từ", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Tiến độ bộ từ", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(progress = { set?.progress ?: 0f }, modifier = Modifier.fillMaxWidth().height(10.dp), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.primaryContainer)
                    Spacer(Modifier.height(8.dp))
                    Text("${((set?.progress ?: 0f) * 100).toInt()}% đã hoàn thành", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Thêm từ", onClick = onAddWord, modifier = Modifier.weight(1f))
                SecondaryButton("Sửa bộ từ", onClick = onEditSet, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            PrimaryButton("Bắt đầu học", onClick = onStartLearning)
            Spacer(Modifier.height(10.dp))
            SecondaryButton("Làm quiz", onClick = onQuiz)
            Spacer(Modifier.height(10.dp))
            Text("Danh sách từ", style = MaterialTheme.typography.titleLarge)
        }
        if (words.isEmpty()) {
            item {
                EmptyStateView(
                    title = "Chưa có từ trong bộ này",
                    message = "Thêm từ đầu tiên để flashcard, quiz và SRS có dữ liệu học.",
                    asset = R.drawable.mimi_an_ui
                )
            }
        } else {
            items(words, key = { it.wordId }) { word ->
                WordCard(word = word, onClick = { onEditWord(word.wordId) })
            }
        }
        item {
            Spacer(Modifier.height(132.dp))
        }
    }
}
