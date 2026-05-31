package com.example.mobile_project.ui.screens.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile_project.data.sample.SampleData
import com.example.mobile_project.ui.components.PrimaryButton

@Composable
fun EditWordScreen(onSave: () -> Unit) {
    val word = SampleData.vocabularies.first()
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("Thêm hoặc sửa từ", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(word.word, {}, label = { Text("Từ vựng") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.pronunciation, {}, label = { Text("Phiên âm IPA") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.meaning, {}, label = { Text("Nghĩa tiếng Việt") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.definition, {}, label = { Text("Định nghĩa") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.example, {}, label = { Text("Ví dụ") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.collocations.joinToString(", "), {}, label = { Text("Cụm từ đi kèm") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.note, {}, label = { Text("Ghi chú") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(word.imageUrl.orEmpty(), {}, label = { Text("URL hình ảnh") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Lưu từ", onClick = onSave)
    }
}
