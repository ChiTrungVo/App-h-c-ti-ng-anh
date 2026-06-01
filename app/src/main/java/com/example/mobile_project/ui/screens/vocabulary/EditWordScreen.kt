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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.ui.components.PrimaryButton
import com.example.mobile_project.ui.components.ValidationMessageBox
import com.example.mobile_project.ui.theme.Mobile_projectTheme

@Composable
fun EditWordScreen(
    setId: String,
    wordId: String?,
    onSave: () -> Unit
) {
    val existingWord = VocabularyDemoStore.getWord(wordId)
    var word by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.word.orEmpty()) }
    var pronunciation by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.pronunciation.orEmpty()) }
    var meaning by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.meaning.orEmpty()) }
    var definition by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.definition.orEmpty()) }
    var example by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.example.orEmpty()) }
    var collocations by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.collocations?.joinToString(", ").orEmpty()) }
    var note by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.note.orEmpty()) }
    var imageUrl by rememberSaveable(wordId, setId) { mutableStateOf(existingWord?.imageUrl.orEmpty()) }
    var showErrors by rememberSaveable(wordId, setId) { mutableStateOf(false) }
    val isEditing = existingWord != null
    val wordError = word.trim().isEmpty()
    val meaningError = meaning.trim().isEmpty()

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text(if (isEditing) "Sửa từ vựng" else "Thêm từ vựng", style = MaterialTheme.typography.headlineLarge)
        Text("Từ này sẽ lưu vào vocabularies và trỏ về bộ từ bằng setId.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(word, { word = it }, label = { Text("Từ vựng") }, modifier = Modifier.fillMaxWidth())
        if (showErrors && wordError) {
            Spacer(Modifier.height(8.dp))
            ValidationMessageBox("Từ vựng không được để trống.")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(pronunciation, { pronunciation = it }, label = { Text("Phiên âm IPA") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(meaning, { meaning = it }, label = { Text("Nghĩa tiếng Việt") }, modifier = Modifier.fillMaxWidth())
        if (showErrors && meaningError) {
            Spacer(Modifier.height(8.dp))
            ValidationMessageBox("Nghĩa tiếng Việt không được để trống.")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(definition, { definition = it }, label = { Text("Định nghĩa") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(example, { example = it }, label = { Text("Ví dụ") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(collocations, { collocations = it }, label = { Text("Cụm từ đi kèm") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(note, { note = it }, label = { Text("Ghi chú") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(imageUrl, { imageUrl = it }, label = { Text("URL hình ảnh") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            "Lưu từ",
            enabled = !wordError && !meaningError,
            onClick = {
                showErrors = true
                if (wordError || meaningError) return@PrimaryButton
                VocabularyDemoStore.saveWord(
                    wordId = existingWord?.wordId,
                    setId = setId,
                    word = word,
                    pronunciation = pronunciation,
                    meaning = meaning,
                    definition = definition,
                    example = example,
                    collocations = parseList(collocations),
                    note = note,
                    imageUrl = imageUrl
                )
                onSave()
            }
        )
        Spacer(Modifier.height(132.dp))
    }
}

private fun parseList(value: String): List<String> = value
    .split(",")
    .map { it.trim() }
    .filter { it.isNotBlank() }

@Preview(showBackground = true)
@Composable
private fun EditWordScreenPreview() {
    val setId = VocabularyDemoStore.vocabularySets.firstOrNull()?.setId.orEmpty()
    val wordId = VocabularyDemoStore.wordsForSet(setId).firstOrNull()?.wordId
    Mobile_projectTheme {
        EditWordScreen(
            setId = setId,
            wordId = wordId,
            onSave = {}
        )
    }
}
