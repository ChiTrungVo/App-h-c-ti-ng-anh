package com.example.mobile_project.feature.learning.viewmodel

import com.example.mobile_project.data.model.UserWordProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class LearningReviewStatsTest {

    @Test
    fun `new word counts as learned but not reviewed`() {
        val delta = LearningReviewStats.deltaForReview(
            previousProgress = progress(status = "NOT_STARTED"),
            updatedProgress = progress(status = "REVIEWING")
        )

        assertEquals(1, delta.studiedWords)
        assertEquals(1, delta.newWords)
        assertEquals(0, delta.reviewedWords)
        assertEquals(0, delta.masteredWords)
        assertEquals(1, delta.studyMinutes)
    }

    @Test
    fun `reviewed word counts as reviewed`() {
        val delta = LearningReviewStats.deltaForReview(
            previousProgress = progress(status = "REVIEWING"),
            updatedProgress = progress(status = "REVIEWING")
        )

        assertEquals(0, delta.newWords)
        assertEquals(1, delta.reviewedWords)
        assertEquals(0, delta.masteredWords)
    }

    @Test
    fun `mastered transition counts once`() {
        val delta = LearningReviewStats.deltaForReview(
            previousProgress = progress(status = "REVIEWING"),
            updatedProgress = progress(status = "MASTERED")
        )

        assertEquals(1, delta.reviewedWords)
        assertEquals(1, delta.masteredWords)
    }

    @Test
    fun `already mastered word does not increment mastered again`() {
        val delta = LearningReviewStats.deltaForReview(
            previousProgress = progress(status = "MASTERED"),
            updatedProgress = progress(status = "MASTERED")
        )

        assertEquals(1, delta.reviewedWords)
        assertEquals(0, delta.masteredWords)
    }

    private fun progress(status: String) = UserWordProgress(
        id = "progress-id",
        userId = "user-id",
        setId = "set-id",
        wordId = "word-id",
        status = status,
        boxLevel = 0,
        easinessFactor = 2.5,
        repetitions = 0,
        intervalDays = 1,
        nextReviewAt = "",
        lastReviewedAt = "",
        lastQuality = 0,
        createdAt = "",
        updatedAt = ""
    )
}
