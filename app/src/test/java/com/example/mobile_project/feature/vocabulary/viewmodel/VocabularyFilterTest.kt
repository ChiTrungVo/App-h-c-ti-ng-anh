package com.example.mobile_project.feature.vocabulary.viewmodel

import com.example.mobile_project.data.model.VocabularySet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyFilterTest {

    private val sampleSets = listOf(
        VocabularySet(
            setId = "1", userId = "u1", title = "IELTS Vocabulary",
            description = "High level words", tags = listOf("IELTS", "Academic"),
            wordCount = 10, isPublic = true, progress = 0.5f, status = "Learning"
        ),
        VocabularySet(
            setId = "2", userId = "u1", title = "Daily English",
            description = "Common phrases", tags = listOf("Daily", "Basic"),
            wordCount = 20, isPublic = true, progress = 0.2f, status = "Learning"
        ),
        VocabularySet(
            setId = "3", userId = "u1", title = "TOEIC Listening",
            description = "Business context", tags = listOf("TOEIC", "Business"),
            wordCount = 15, isPublic = false, progress = 0.0f, status = "New"
        )
    )

    // Mô phỏng lại logic applyFilter từ ViewModel
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

    @Test
    fun `test search by title`() {
        val result = applyFilter(sampleSets, "IELTS", "Tất cả")
        assertEquals(1, result.size)
        assertEquals("IELTS Vocabulary", result[0].title)
    }

    @Test
    fun `test search case insensitive`() {
        val result = applyFilter(sampleSets, "daily", "Tất cả")
        assertEquals(1, result.size)
        assertTrue(result[0].title.contains("Daily"))
    }

    @Test
    fun `test filter by tag`() {
        val result = applyFilter(sampleSets, "", "Business")
        assertEquals(1, result.size)
        assertEquals("TOEIC Listening", result[0].title)
    }

    @Test
    fun `test search and tag combined`() {
        val result = applyFilter(sampleSets, "English", "Daily")
        assertEquals(1, result.size)
        assertEquals("Daily English", result[0].title)
    }

    @Test
    fun `test search no results`() {
        val result = applyFilter(sampleSets, "French", "Tất cả")
        assertEquals(0, result.size)
    }

    @Test
    fun `test tag 'All' shows everything`() {
        val result = applyFilter(sampleSets, "", "Tất cả")
        assertEquals(3, result.size)
    }
}
