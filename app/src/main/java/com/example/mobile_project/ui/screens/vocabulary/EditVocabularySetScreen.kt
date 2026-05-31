package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun EditVocabularySetScreen(
    setId: String?,
    onSave: (String) -> Unit
) {
    val set = VocabularyDemoStore.getSet(setId.orEmpty())
    var title by rememberSaveable(setId) { mutableStateOf(set?.title.orEmpty()) }
    var description by rememberSaveable(setId) { mutableStateOf(set?.description.orEmpty()) }
    var tagsText by rememberSaveable(setId) { mutableStateOf(set?.tags?.joinToString(", ").orEmpty()) }
    var isPublic by rememberSaveable(setId) { mutableStateOf(set?.isPublic ?: false) }
    val isEditing = set != null

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text(if (isEditing) "Sửa bộ từ" else "Tạo bộ từ", style = MaterialTheme.typography.headlineLarge)
        Text("Bộ từ sẽ tương ứng một document trong collection vocabulary_sets.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(title, { title = it }, label = { Text("Tên bộ từ") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(description, { description = it }, label = { Text("Mô tả") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text("Thẻ chủ đề", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(tagsText, { tagsText = it }, label = { Text("Tags, cách nhau bằng dấu phẩy") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            parseList(tagsText).forEach { AssistChip(onClick = {}, label = { Text(it) }) }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Công khai/Riêng tư", style = MaterialTheme.typography.titleMedium)
                Text(if (isPublic) "Bộ từ công khai" else "Chỉ mình tôi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isPublic, onCheckedChange = { isPublic = it })
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            "Lưu",
            onClick = {
                val savedSetId = VocabularyDemoStore.saveSet(
                    setId = set?.setId,
                    title = title,
                    description = description,
                    tags = parseList(tagsText),
                    isPublic = isPublic
                )
                onSave(savedSetId)
            }
        )
        Spacer(Modifier.height(132.dp))
    }
}

private fun parseList(value: String): List<String> = value
    .split(",")
    .map { it.trim() }
    .filter { it.isNotBlank() }
