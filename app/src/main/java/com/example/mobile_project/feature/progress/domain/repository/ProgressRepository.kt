package com.example.mobile_project.feature.progress.domain.repository

import com.example.mobile_project.feature.progress.domain.model.ProgressItem
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getAllSrsProgress(): Flow<List<ProgressItem>>
    suspend fun updateSrsProgress(item: ProgressItem)
    suspend fun saveQuizResult(setId: String, correctCount: Int, totalCount: Int)
    suspend fun getBestQuizResult(setId: String): Pair<Int, Int>?
}
