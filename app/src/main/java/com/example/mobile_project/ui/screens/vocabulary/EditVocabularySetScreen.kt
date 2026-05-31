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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun EditVocabularySetScreen(onSave: () -> Unit) {
    val set = SampleData.vocabulary_sets.first()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("Thêm hoặc sửa bộ từ", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(set.title, {}, label = { Text("Tên bộ từ") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(set.description, {}, label = { Text("Mô tả") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text("Thẻ chủ đề", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            set.tags.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Công khai/Riêng tư", style = MaterialTheme.typography.titleMedium)
                Text(if (set.isPublic) "Bộ từ công khai" else "Chỉ mình tôi", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = set.isPublic, onCheckedChange = {})
        }
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Lưu", onClick = onSave)
    }
}
