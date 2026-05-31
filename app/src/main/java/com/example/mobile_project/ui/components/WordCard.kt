package com.example.mobile_project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.model.VocabularyWord
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@Composable
fun WordCard(
    word: VocabularyWord,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val content: @Composable () -> Unit = {
        Column(Modifier.padding(18.dp)) {
            Row {
                Text(word.word, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.width(10.dp))
                Text(word.pronunciation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Text(word.meaning, style = MaterialTheme.typography.bodyLarge)
            Text(word.example, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.58f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            content()
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.58f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            content()
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
fun WordCardPreview() {
    WordCard(
        word = VocabularyWord(
            wordId = "1",
            setId = "1",
            userId = "1",
            word = "Eloquent",
            pronunciation = "/ˈɛl.ə.kwənt/",
            meaning = "Fluent or persuasive in speaking or writing.",
            definition = "Eloquent is an adjective that describes someone who can express themselves clearly and effectively, often in a way that is moving or persuasive.",
            example = "She gave an eloquent speech that moved the audience to tears.",
            collocations = listOf("eloquent speaker", "eloquent writing"),
            note = "Often used to describe speeches, writing, or people who are skilled at communication.",
            imageUrl = null
        )
    )
}