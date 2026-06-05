package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularyWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyImportExportCodecTest {

    @Test
    fun parseCsv_readsQuotedCellsAndSkipsInvalidRows() {
        val csv = """
            word,meaning,pronunciation,definition,example,collocations,note
            apple,quả táo,/ˈæp.əl/,"a fruit, often red","I eat an apple.",red fruit; green apple,"line 1
            line 2"
            ,thiếu từ,,,,,
            book,sách,,,,,
        """.trimIndent()

        val result = VocabularyImportExportCodec.parseCsv(csv)

        assertEquals(2, result.words.size)
        assertEquals(1, result.skippedRows)
        assertEquals("a fruit, often red", result.words.first().definition)
        assertEquals(listOf("red fruit", "green apple"), result.words.first().collocations)
        assertTrue(result.words.first().note.contains("line 2"))
    }

    @Test
    fun csvExport_canBeParsedBack() {
        val csv = VocabularyImportExportCodec.toCsv(
            listOf(
                word(
                    word = "hello",
                    meaning = "xin chào",
                    example = "She said, \"hello\"."
                )
            )
        )

        val result = VocabularyImportExportCodec.parseCsv(csv)

        assertEquals(1, result.words.size)
        assertEquals("hello", result.words.single().word)
        assertEquals("She said, \"hello\".", result.words.single().example)
    }

    @Test
    fun xlsxExport_canBeParsedBack() {
        val bytes = VocabularyImportExportCodec.toXlsx(
            listOf(
                word(
                    word = "ocean",
                    meaning = "đại dương",
                    definition = "large body of salt water",
                    collocations = listOf("deep ocean", "open ocean"),
                    relatedWords = listOf("sea", "marine"),
                    note = "test note"
                )
            )
        )

        val result = VocabularyImportExportCodec.parseXlsx(bytes)

        assertEquals(1, result.words.size)
        assertEquals("ocean", result.words.single().word)
        assertEquals("đại dương", result.words.single().meaning)
        assertEquals(listOf("deep ocean", "open ocean"), result.words.single().collocations)
        assertEquals(listOf("sea", "marine"), result.words.single().relatedWords)
        assertEquals("test note", result.words.single().note)
    }

    private fun word(
        word: String,
        meaning: String,
        pronunciation: String = "",
        definition: String = "",
        example: String = "",
        collocations: List<String> = emptyList(),
        relatedWords: List<String> = emptyList(),
        note: String = ""
    ) = VocabularyWord(
        wordId = "word-id",
        setId = "set-id",
        userId = "user-id",
        word = word,
        pronunciation = pronunciation,
        meaning = meaning,
        definition = definition,
        example = example,
        collocations = collocations,
        relatedWords = relatedWords,
        note = note,
        imageUrl = null
    )
}
