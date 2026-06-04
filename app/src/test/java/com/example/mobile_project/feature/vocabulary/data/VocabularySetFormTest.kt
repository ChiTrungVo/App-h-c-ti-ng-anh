package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularySet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test cho VocabularySetForm — validate, tagList parsing, fromSet mapping.
 *
 * Phần validate mirror logic từ EditVocabularySetViewModel.save():
 *   - title trống → lỗi "Tên bộ từ không được để trống."
 *
 * Phần filter mirror logic từ VocabularySetListViewModel.applyFilter():
 *   - Tìm kiếm trên title, description, tags
 *   - Lọc theo tag được chọn
 */
class VocabularySetFormTest {

    // ── sample sets ───────────────────────────────────────────────
    private val ielts = VocabularySet(
        setId = "1", userId = "u1", title = "IELTS Vocabulary",
        description = "High level words", tags = listOf("IELTS", "Academic"),
        wordCount = 10, isPublic = true, progress = 0.5f, status = "Learning"
    )
    private val daily = VocabularySet(
        setId = "2", userId = "u1", title = "Daily English",
        description = "Common phrases", tags = listOf("Daily", "Basic"),
        wordCount = 20, isPublic = true, progress = 0.2f, status = "Learning"
    )
    private val toeic = VocabularySet(
        setId = "3", userId = "u1", title = "TOEIC Listening",
        description = "Business context", tags = listOf("TOEIC", "Business"),
        wordCount = 15, isPublic = false, progress = 0.0f, status = "New"
    )
    private val business = VocabularySet(
        setId = "4", userId = "u1", title = "Business English",
        description = "Office communication", tags = listOf("Business", "TOEIC"),
        wordCount = 8, isPublic = true, progress = 0.6f, status = "Learning"
    )
    private val allSets = listOf(ielts, daily, toeic, business)

    // ── validate (mirror EditVocabularySetViewModel.save) ─────────

    @Test
    fun `validate with non-blank title returns null`() {
        val form = VocabularySetForm(title = "My Set")
        assertNull(validate(form))
    }

    @Test
    fun `validate with blank title returns error`() {
        val form = VocabularySetForm(title = "")
        assertEquals("Tên bộ từ không được để trống.", validate(form))
    }

    @Test
    fun `validate with whitespace title returns error`() {
        val form = VocabularySetForm(title = "   ")
        assertEquals("Tên bộ từ không được để trống.", validate(form))
    }

    // ── tagList parsing (real property) ───────────────────────────

    @Test
    fun `tagList splits comma separated values`() {
        val form = VocabularySetForm(tags = "IELTS, Academic, Advanced")
        assertEquals(listOf("IELTS", "Academic", "Advanced"), form.tagList)
    }

    @Test
    fun `tagList trims whitespace`() {
        val form = VocabularySetForm(tags = " IELTS ,  Academic  , Advanced ")
        assertEquals(listOf("IELTS", "Academic", "Advanced"), form.tagList)
    }

    @Test
    fun `tagList filters blank entries`() {
        val form = VocabularySetForm(tags = "IELTS, , Academic,   , Advanced")
        assertEquals(listOf("IELTS", "Academic", "Advanced"), form.tagList)
    }

    @Test
    fun `tagList empty string returns empty list`() {
        val form = VocabularySetForm(tags = "")
        assertEquals(0, form.tagList.size)
    }

    @Test
    fun `tagList only commas returns empty list`() {
        val form = VocabularySetForm(tags = ",,,")
        assertEquals(0, form.tagList.size)
    }

    @Test
    fun `tagList single value returns single element list`() {
        val form = VocabularySetForm(tags = "IELTS")
        assertEquals(listOf("IELTS"), form.tagList)
    }

    // ── fromSet mapping (real companion function) ─────────────────

    @Test
    fun `fromSet maps all fields correctly`() {
        val set = VocabularySet(
            setId = "s1", userId = "u1", title = "IELTS",
            description = "Advanced words", tags = listOf("IELTS", "Academic"),
            wordCount = 10, isPublic = true, progress = 0.5f, status = "Learning"
        )
        val form = VocabularySetForm.fromSet(set)

        assertEquals("IELTS", form.title)
        assertEquals("Advanced words", form.description)
        assertEquals("IELTS, Academic", form.tags)
        assertEquals(listOf("IELTS", "Academic"), form.tagList)
        assertTrue(form.isPublic)
    }

    @Test
    fun `fromSet with empty tags returns empty tags string`() {
        val set = VocabularySet(
            setId = "s1", userId = "u1", title = "Test",
            description = "", tags = emptyList(),
            wordCount = 0, isPublic = false, progress = 0f, status = "New"
        )
        val form = VocabularySetForm.fromSet(set)
        assertEquals("", form.tags)
        assertEquals(0, form.tagList.size)
        assertFalse(form.isPublic)
    }

    // ── default values ────────────────────────────────────────────

    @Test
    fun `VocabularySetForm defaults to empty strings and false`() {
        val form = VocabularySetForm()
        assertEquals("", form.title)
        assertEquals("", form.description)
        assertEquals("", form.tags)
        assertFalse(form.isPublic)
        assertEquals(0, form.tagList.size)
    }

    // ── filter (mirror VocabularySetListViewModel.applyFilter) ────

    @Test
    fun `filter by title with exact word`() {
        val result = applyFilter(allSets, "IELTS", "Tất cả")
        assertEquals(1, result.size)
        assertEquals("IELTS Vocabulary", result[0].title)
    }

    @Test
    fun `filter by title case insensitive`() {
        val result = applyFilter(allSets, "daily", "Tất cả")
        assertEquals(1, result.size)
        assertEquals("Daily English", result[0].title)
    }

    @Test
    fun `filter by description`() {
        val result = applyFilter(allSets, "office", "Tất cả")
        assertEquals(1, result.size)
        assertEquals("Business English", result[0].title)
    }

    @Test
    fun `filter by tag name`() {
        val result = applyFilter(allSets, "TOEIC", "Tất cả")
        assertEquals(2, result.size) // TOEIC Listening + Business English
    }

    @Test
    fun `filter combined search and tag`() {
        val result = applyFilter(allSets, "English", "TOEIC")
        assertEquals(1, result.size)
        assertEquals("Business English", result[0].title)
    }

    @Test
    fun `filter tag All returns everything matching search`() {
        val result = applyFilter(allSets, "English", "Tất cả")
        // "Daily English" + "Business English" có "English" trong title;
        // "IELTS Vocabulary" không có "English" trong title/description/tags
        assertEquals(2, result.size)
        val titles = result.map { it.title }.toSet()
        assertTrue(titles.contains("Daily English"))
        assertTrue(titles.contains("Business English"))
    }

    @Test
    fun `filter tag with no match returns empty`() {
        val result = applyFilter(allSets, "IELTS", "Daily")
        assertEquals(0, result.size)
    }

    @Test
    fun `filter blank search with specific tag returns tag matches`() {
        val result = applyFilter(allSets, "", "Business")
        assertEquals(2, result.size) // TOEIC Listening + Business English
    }

    @Test
    fun `filter blank search and All tag returns all`() {
        val result = applyFilter(allSets, "", "Tất cả")
        assertEquals(4, result.size)
    }

    @Test
    fun `filter no results returns empty`() {
        val result = applyFilter(allSets, "zzznotfound", "Tất cả")
        assertEquals(0, result.size)
    }

    // ── helper: mirror VocabularySetListViewModel.applyFilter ──────

    private fun applyFilter(
        sets: List<VocabularySet>,
        query: String,
        tag: String
    ): List<VocabularySet> {
        return sets.filter { set ->
            val matchesSearch = query.isBlank() ||
                set.title.contains(query, ignoreCase = true) ||
                set.description.contains(query, ignoreCase = true) ||
                set.tags.any { it.contains(query, ignoreCase = true) }
            val matchesTag = tag == "Tất cả" || tag in set.tags
            matchesSearch && matchesTag
        }
    }

    /** Mirror logic từ EditVocabularySetViewModel.save(). */
    private fun validate(form: VocabularySetForm): String? = when {
        form.title.isBlank() -> "Tên bộ từ không được để trống."
        else -> null
    }
}
