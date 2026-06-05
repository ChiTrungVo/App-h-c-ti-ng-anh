package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularyWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test cho WordForm — validate, parsing, và mapping.
 *
 * Phần validate mirror logic từ EditWordViewModel.save():
 *   - word trống → lỗi "Từ vựng không được để trống."
 *   - meaning trống → lỗi "Nghĩa tiếng Việt không được để trống."
 *
 * Phần parsing test trực tiếp thuộc tính thật của WordForm (`collocationList`).
 * Phần mapping test `WordForm.fromWord()`.
 */
class WordFormTest {

    // ── validate (mirror EditWordViewModel.save) ──────────────────

    @Test
    fun `validate with all fields filled returns no errors`() {
        val form = WordForm(word = "hello", meaning = "xin chào")
        val (wordError, meaningError) = validate(form)
        assertNull(wordError)
        assertNull(meaningError)
    }

    @Test
    fun `validate with blank word returns error`() {
        val form = WordForm(word = "", meaning = "xin chào")
        val (wordError, meaningError) = validate(form)
        assertEquals("Từ vựng không được để trống.", wordError)
        assertNull(meaningError)
    }

    @Test
    fun `validate with whitespace word returns error`() {
        val form = WordForm(word = "   ", meaning = "xin chào")
        val (wordError, meaningError) = validate(form)
        assertEquals("Từ vựng không được để trống.", wordError)
        assertNull(meaningError)
    }

    @Test
    fun `validate with blank meaning returns error`() {
        val form = WordForm(word = "hello", meaning = "")
        val (wordError, meaningError) = validate(form)
        assertNull(wordError)
        assertEquals("Nghĩa tiếng Việt không được để trống.", meaningError)
    }

    @Test
    fun `validate with whitespace meaning returns error`() {
        val form = WordForm(word = "hello", meaning = "   ")
        val (wordError, meaningError) = validate(form)
        assertNull(wordError)
        assertEquals("Nghĩa tiếng Việt không được để trống.", meaningError)
    }

    @Test
    fun `validate with both blank returns both errors`() {
        val form = WordForm(word = "", meaning = "")
        val (wordError, meaningError) = validate(form)
        assertNotNull(wordError)
        assertNotNull(meaningError)
    }

    // ── collocationList parsing (real property) ───────────────────

    @Test
    fun `collocationList splits comma separated values`() {
        val form = WordForm(collocations = "make, do, get")
        assertEquals(listOf("make", "do", "get"), form.collocationList)
    }

    @Test
    fun `collocationList trims whitespace`() {
        val form = WordForm(collocations = " make ,  do  , get ")
        assertEquals(listOf("make", "do", "get"), form.collocationList)
    }

    @Test
    fun `collocationList filters blank entries`() {
        val form = WordForm(collocations = "make, , do,   , get")
        assertEquals(listOf("make", "do", "get"), form.collocationList)
    }

    @Test
    fun `collocationList empty string returns empty list`() {
        val form = WordForm(collocations = "")
        assertEquals(0, form.collocationList.size)
    }

    @Test
    fun `collocationList only commas returns empty list`() {
        val form = WordForm(collocations = ",,,")
        assertEquals(0, form.collocationList.size)
    }

    @Test
    fun `collocationList single value returns single element list`() {
        val form = WordForm(collocations = "hello")
        assertEquals(listOf("hello"), form.collocationList)
    }

    // ── fromWord mapping (real companion function) ────────────────

    @Test
    fun `fromWord maps all fields correctly`() {
        val word = VocabularyWord(
            wordId = "w1", setId = "s1", userId = "u1",
            word = "hello", pronunciation = "/həˈloʊ/",
            meaning = "xin chào", definition = "a greeting",
            example = "Hello!", collocations = listOf("say hello", "hello world"),
            relatedWords = listOf("hi", "greeting"),
            note = "common", imageUrl = "https://example.com/img.png"
        )
        val form = WordForm.fromWord(word)

        assertEquals("hello", form.word)
        assertEquals("/həˈloʊ/", form.pronunciation)
        assertEquals("xin chào", form.meaning)
        assertEquals("a greeting", form.definition)
        assertEquals("Hello!", form.example)
        assertEquals("say hello, hello world", form.collocations)
        assertEquals("common", form.note)
        assertEquals("https://example.com/img.png", form.imageUrl)
    }

    @Test
    fun `fromWord with null imageUrl returns empty string`() {
        val word = VocabularyWord(
            wordId = "w1", setId = "s1", userId = "u1",
            word = "test", pronunciation = "", meaning = "",
            definition = "", example = "", collocations = emptyList(),
            relatedWords = emptyList(),
            note = "", imageUrl = null
        )
        val form = WordForm.fromWord(word)
        assertEquals("", form.imageUrl)
    }

    @Test
    fun `fromWord with empty collocations returns empty string`() {
        val word = VocabularyWord(
            wordId = "w1", setId = "s1", userId = "u1",
            word = "test", pronunciation = "", meaning = "",
            definition = "", example = "", collocations = emptyList(),
            relatedWords = emptyList(),
            note = "", imageUrl = null
        )
        val form = WordForm.fromWord(word)
        assertEquals("", form.collocations)
        assertEquals(0, form.collocationList.size)
    }

    // ── default values ────────────────────────────────────────────

    @Test
    fun `WordForm default constructor sets all fields empty`() {
        val form = WordForm()
        assertEquals("", form.word)
        assertEquals("", form.pronunciation)
        assertEquals("", form.meaning)
        assertEquals("", form.definition)
        assertEquals("", form.example)
        assertEquals("", form.collocations)
        assertEquals("", form.note)
        assertEquals("", form.imageUrl)
        assertEquals(0, form.collocationList.size)
    }

    // ── helper: mirror EditWordViewModel.save() validation ────────

    /** Mirror logic từ EditWordViewModel.save() để test. */
    private data class ValidationResult(
        val wordError: String?,
        val meaningError: String?
    )

    private fun validate(form: WordForm): ValidationResult {
        var wordError: String? = null
        var meaningError: String? = null
        when {
            form.word.isBlank() -> wordError = "Từ vựng không được để trống."
        }
        when {
            form.meaning.isBlank() -> meaningError = "Nghĩa tiếng Việt không được để trống."
        }
        return ValidationResult(wordError, meaningError)
    }
}
