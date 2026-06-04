package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularyWord

/**
 * Logic khớp tìm kiếm từ vựng dùng chung cho repository và ViewModel.
 * Tách riêng để unit test được mà không cần gọi mạng / Appwrite.
 */
object VocabularyWordMatcher {

    /**
     * Một từ có khớp với [query] không.
     * So khớp (không phân biệt hoa thường) trên: word, meaning, definition, example, pronunciation.
     * Query rỗng/whitespace coi như khớp tất cả.
     */
    fun matches(word: VocabularyWord, query: String): Boolean {
        val q = query.trim()
        if (q.isBlank()) return true
        return word.word.contains(q, ignoreCase = true) ||
            word.meaning.contains(q, ignoreCase = true) ||
            word.definition.contains(q, ignoreCase = true) ||
            word.example.contains(q, ignoreCase = true) ||
            word.pronunciation.contains(q, ignoreCase = true)
    }

    /** Lọc danh sách từ theo [query]; query rỗng trả về nguyên danh sách. */
    fun filter(words: List<VocabularyWord>, query: String): List<VocabularyWord> =
        if (query.trim().isBlank()) words else words.filter { matches(it, query) }
}
