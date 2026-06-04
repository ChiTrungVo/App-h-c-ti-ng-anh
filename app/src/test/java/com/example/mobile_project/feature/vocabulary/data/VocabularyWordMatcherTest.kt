package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularyWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test cho VocabularyWordMatcher — logic khớp tìm kiếm từ vựng.
 * Dùng object thật trong production, không mock.
 */
class VocabularyWordMatcherTest {

    // ── sample words ──────────────────────────────────────────────
    private val hello = VocabularyWord(
        wordId = "w1", setId = "s1", userId = "u1",
        word = "hello", pronunciation = "/həˈloʊ/",
        meaning = "xin chào", definition = "used as a greeting",
        example = "Hello, how are you?", collocations = listOf("say hello"),
        note = "", imageUrl = null
    )
    private val cat = VocabularyWord(
        wordId = "w2", setId = "s1", userId = "u1",
        word = "cat", pronunciation = "/kæt/",
        meaning = "con mèo", definition = "a small domesticated carnivorous mammal",
        example = "The cat is sleeping.", collocations = listOf("black cat"),
        note = "", imageUrl = null
    )
    private val apple = VocabularyWord(
        wordId = "w3", setId = "s1", userId = "u1",
        word = "apple", pronunciation = "/ˈæp.əl/",
        meaning = "quả táo", definition = "a round fruit with red or green skin",
        example = "I eat an apple every day.", collocations = listOf("apple pie"),
        note = "fruit", imageUrl = null
    )
    private val allWords = listOf(hello, cat, apple)

    // ── matches ───────────────────────────────────────────────────

    @Test
    fun `matches word field exactly`() {
        assertTrue(VocabularyWordMatcher.matches(hello, "hello"))
    }

    @Test
    fun `matches word field case insensitive`() {
        assertTrue(VocabularyWordMatcher.matches(hello, "HELLO"))
    }

    @Test
    fun `matches word field partially`() {
        assertTrue(VocabularyWordMatcher.matches(hello, "hel"))
    }

    @Test
    fun `matches meaning field`() {
        assertTrue(VocabularyWordMatcher.matches(cat, "mèo"))
    }

    @Test
    fun `matches meaning field partially`() {
        assertTrue(VocabularyWordMatcher.matches(cat, "mè"))
    }

    @Test
    fun `matches definition field`() {
        assertTrue(VocabularyWordMatcher.matches(apple, "round fruit"))
    }

    @Test
    fun `matches example field`() {
        assertTrue(VocabularyWordMatcher.matches(hello, "how are you"))
    }

    @Test
    fun `matches pronunciation field`() {
        assertTrue(VocabularyWordMatcher.matches(hello, "həˈloʊ"))
    }

    @Test
    fun `does not match unrelated text`() {
        assertFalse(VocabularyWordMatcher.matches(hello, "goodbye"))
    }

    @Test
    fun `blank query matches everything`() {
        assertTrue(VocabularyWordMatcher.matches(hello, ""))
        assertTrue(VocabularyWordMatcher.matches(hello, "   "))
    }

    @Test
    fun `matches on note field only should be false`() {
        // "fruit" xuất hiện trong note của apple, nhưng note KHÔNG nằm trong scope matcher.
        // Tuy nhiên "fruit" cũng có trong definition → phải dùng từ chỉ có trong note.
        // Tạo word mà "onlyInNote" chỉ có ở note, không ở field nào khác.
        val word = VocabularyWord(
            wordId = "w99", setId = "s1", userId = "u1",
            word = "unique",
            pronunciation = "/juˈniːk/",
            meaning = "độc đáo",
            definition = "being the only one of its kind",
            example = "a unique opportunity",
            collocations = emptyList(),
            note = "onlyInNote",
            imageUrl = null
        )
        // "onlyInNote" appears only in note — matcher should NOT find it
        assertFalse(VocabularyWordMatcher.matches(word, "onlyInNote"))
    }

    // ── filter ────────────────────────────────────────────────────

    @Test
    fun `filter with blank query returns all`() {
        val result = VocabularyWordMatcher.filter(allWords, "")
        assertEquals(3, result.size)
    }

    @Test
    fun `filter with whitespace query returns all`() {
        val result = VocabularyWordMatcher.filter(allWords, "   ")
        assertEquals(3, result.size)
    }

    @Test
    fun `filter by word returns match`() {
        val result = VocabularyWordMatcher.filter(allWords, "cat")
        assertEquals(1, result.size)
        assertEquals("cat", result[0].word)
    }

    @Test
    fun `filter by meaning returns match`() {
        val result = VocabularyWordMatcher.filter(allWords, "chào")
        assertEquals(1, result.size)
        assertEquals("hello", result[0].word)
    }

    @Test
    fun `filter by example returns match`() {
        val result = VocabularyWordMatcher.filter(allWords, "sleeping")
        assertEquals(1, result.size)
        assertEquals("cat", result[0].word)
    }

    @Test
    fun `filter no match returns empty`() {
        val result = VocabularyWordMatcher.filter(allWords, "zzznotexist")
        assertEquals(0, result.size)
    }

    @Test
    fun `filter with query matching multiple fields returns all hits`() {
        // "a" appears in word "cat"/"apple", meaning "xin chào"/"con mèo"/"quả táo",
        // definition of cat/apple, etc.
        val result = VocabularyWordMatcher.filter(allWords, "a")
        assertEquals(3, result.size)
    }

    @Test
    fun `filter empty list returns empty`() {
        val result = VocabularyWordMatcher.filter(emptyList(), "hello")
        assertEquals(0, result.size)
    }
}
