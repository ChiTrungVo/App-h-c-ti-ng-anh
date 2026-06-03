package com.example.mobile_project.feature.progress.data.repository

import com.example.mobile_project.data.sample.VocabularyDemoStore
import com.example.mobile_project.feature.progress.domain.model.ProgressItem
import com.example.mobile_project.feature.progress.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl : ProgressRepository {

    // Bộ nhớ tạm In-Memory lưu trữ trạng thái học tập (SRS) của từng từ vựng
    private val srsCache = MutableStateFlow<Map<String, ProgressItem>>(emptyMap())
    
    // Bộ nhớ tạm lưu kết quả thi Quiz tốt nhất của từng bộ từ
    private val quizCache = mutableMapOf<String, Pair<Int, Int>>()

    init {
        // Tự động khởi tạo dữ liệu SRS trống ban đầu cho toàn bộ từ vựng trong hệ thống
        val initialMap = mutableMapOf<String, ProgressItem>()
        VocabularyDemoStore.vocabularySets.forEach { set ->
            VocabularyDemoStore.wordsForSet(set.setId).forEach { word ->
                initialMap[word.wordId] = ProgressItem(vocabularyId = word.wordId)
            }
        }
        srsCache.value = initialMap
    }


    override fun getAllSrsProgress(): Flow<List<ProgressItem>> {
        // Trả về một luồng dữ liệu thay đổi liên tục của toàn bộ tiến trình SRS
        // Tích hợp Appwrite: Thay bằng luồng gọi: databases.listDocuments(collectionId = "srs_progress")
        return srsCache.map { it.values.toList() }
    }

    override suspend fun updateSrsProgress(item: ProgressItem) {
        // Cập nhật trạng thái mới của từ vựng vào Cache
        // Tích hợp Appwrite databases.updateDocument(collectionId = "srs_progress", documentId = item.vocabularyId, data = item)
        val currentMap = srsCache.value.toMutableMap()
        currentMap[item.vocabularyId] = item
        srsCache.value = currentMap
    }


    override suspend fun saveQuizResult(setId: String, correctCount: Int, totalCount: Int) {
        // Lưu hoặc cập nhật kết quả thi tốt nhất
        // Khi tích hợp Appwrite databases.createDocument(collectionId = "quiz_attempts", ...)
        val existing = quizCache[setId]
        if (existing == null) {
            quizCache[setId] = Pair(correctCount, totalCount)
        } else {
            val bestCorrect = maxOf(existing.first, correctCount)
            quizCache[setId] = Pair(bestCorrect, totalCount)
        }
    }

    override suspend fun getBestQuizResult(setId: String): Pair<Int, Int>? {
        // Lấy ra kết quả làm bài thi tốt nhất của bộ từ đó
        return quizCache[setId]
    }
}