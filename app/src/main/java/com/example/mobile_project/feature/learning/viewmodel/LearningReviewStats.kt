package com.example.mobile_project.feature.learning.viewmodel

import com.example.mobile_project.data.model.UserWordProgress

data class LearningReviewDelta(
    val studiedWords: Int = 1,
    val newWords: Int,
    val reviewedWords: Int,
    val masteredWords: Int,
    val studyMinutes: Int = 1
)

object LearningReviewStats {
    fun deltaForReview(
        previousProgress: UserWordProgress,
        updatedProgress: UserWordProgress
    ): LearningReviewDelta {
        val isNew = previousProgress.status == "NOT_STARTED"
        val becameMastered = previousProgress.status != "MASTERED" &&
            updatedProgress.status == "MASTERED"

        return LearningReviewDelta(
            newWords = if (isNew) 1 else 0,
            reviewedWords = if (isNew) 0 else 1,
            masteredWords = if (becameMastered) 1 else 0
        )
    }
}
