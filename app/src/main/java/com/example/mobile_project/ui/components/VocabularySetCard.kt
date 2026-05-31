package com.example.mobile_project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.model.VocabularySet
import com.example.mobile_project.ui.theme.MinLishPrimaryContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VocabularySetCard(
    vocabularySet: VocabularySet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MinLishPrimaryContainer.copy(alpha = 0.68f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(vocabularySet.title, style = MaterialTheme.typography.titleMedium)
                Text("${vocabularySet.wordCount} từ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(6.dp))
            Text(vocabularySet.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { vocabularySet.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MinLishPrimaryContainer.copy(alpha = 0.34f)
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vocabularySet.tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag, fontWeight = FontWeight.SemiBold) },
                        shape = RoundedCornerShape(99.dp),
                        border = BorderStroke(1.dp, MinLishPrimaryContainer.copy(alpha = 0.65f)),
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MinLishPrimaryContainer.copy(alpha = 0.22f),
                            labelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}
