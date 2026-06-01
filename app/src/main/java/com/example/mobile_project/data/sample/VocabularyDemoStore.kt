//package com.example.mobile_project.data.sample
//
//import androidx.compose.runtime.mutableStateListOf
//import com.example.mobile_project.data.model.VocabularySet
//import com.example.mobile_project.data.model.VocabularyWord
//
//object VocabularyDemoStore {
//    val vocabularySets = mutableStateListOf<VocabularySet>()
//    val vocabularies = mutableStateListOf<VocabularyWord>()
//
//    init {
//        vocabularies.addAll(SampleData.vocabularies)
//        vocabularySets.addAll(
//            SampleData.vocabulary_sets.map { set ->
//                set.copy(wordCount = vocabularies.count { it.setId == set.setId })
//            }
//        )
//    }
//
//    fun getSet(setId: String): VocabularySet? = vocabularySets.firstOrNull { it.setId == setId }
//
//    fun wordsForSet(setId: String): List<VocabularyWord> = vocabularies.filter { it.setId == setId }
//
//    fun getWord(wordId: String?): VocabularyWord? = vocabularies.firstOrNull { it.wordId == wordId }
//
//    fun saveSet(
//        setId: String?,
//        title: String,
//        description: String,
//        tags: List<String>,
//        isPublic: Boolean
//    ): String {
//        val normalizedTitle = title.trim().ifBlank { "Bộ từ mới" }
//        val existingIndex = vocabularySets.indexOfFirst { it.setId == setId }
//        val resolvedSetId = if (existingIndex >= 0) vocabularySets[existingIndex].setId else buildId("set", normalizedTitle)
//        val current = vocabularySets.getOrNull(existingIndex)
//        val savedSet = VocabularySet(
//            setId = resolvedSetId,
//            userId = SampleData.user.userId,
//            title = normalizedTitle,
//            description = description.trim(),
//            tags = tags,
//            wordCount = vocabularies.count { it.setId == resolvedSetId },
//            isPublic = isPublic,
//            progress = current?.progress ?: 0f,
//            status = current?.status ?: "Chưa học"
//        )
//
//        if (existingIndex >= 0) {
//            vocabularySets[existingIndex] = savedSet
//        } else {
//            vocabularySets.add(0, savedSet)
//        }
//        return resolvedSetId
//    }
//
//    fun saveWord(
//        wordId: String?,
//        setId: String,
//        word: String,
//        pronunciation: String,
//        meaning: String,
//        definition: String,
//        example: String,
//        collocations: List<String>,
//        note: String,
//        imageUrl: String?
//    ): String {
//        val normalizedWord = word.trim().ifBlank { "new word" }
//        val existingIndex = vocabularies.indexOfFirst { it.wordId == wordId }
//        val resolvedWordId = if (existingIndex >= 0) vocabularies[existingIndex].wordId else buildId("word", normalizedWord)
//        val savedWord = VocabularyWord(
//            wordId = resolvedWordId,
//            setId = setId,
//            userId = SampleData.user.userId,
//            word = normalizedWord,
//            pronunciation = pronunciation.trim(),
//            meaning = meaning.trim(),
//            definition = definition.trim(),
//            example = example.trim(),
//            collocations = collocations,
//            note = note.trim(),
//            imageUrl = imageUrl?.trim()?.ifBlank { null }
//        )
//
//        if (existingIndex >= 0) {
//            vocabularies[existingIndex] = savedWord
//        } else {
//            vocabularies.add(0, savedWord)
//        }
//        refreshWordCount(setId)
//        return resolvedWordId
//    }
//
//    fun deleteSet(setId: String) {
//        val removed = vocabularySets.removeAll { it.setId == setId }
//        if (removed) {
//            vocabularies.removeAll { it.setId == setId }
//        }
//    }
//
//    fun deleteWord(wordId: String) {
//        val existing = vocabularies.firstOrNull { it.wordId == wordId } ?: return
//        vocabularies.removeAll { it.wordId == wordId }
//        refreshWordCount(existing.setId)
//    }
//
//    private fun refreshWordCount(setId: String) {
//        val index = vocabularySets.indexOfFirst { it.setId == setId }
//        if (index >= 0) {
//            vocabularySets[index] = vocabularySets[index].copy(
//                wordCount = vocabularies.count { it.setId == setId }
//            )
//        }
//    }
//
//    private fun buildId(prefix: String, value: String): String {
//        val slug = value
//            .lowercase()
//            .replace(Regex("[^a-z0-9]+"), "_")
//            .trim('_')
//            .ifBlank { "item" }
//        return "${prefix}_${slug}_${System.currentTimeMillis()}"
//    }
//}
