// Cập nhật kế hoạch học tập ngày tiếp theo
package com.example.mobile_project.feature.progress.domain.usecase

import com.example.mobile_project.feature.progress.domain.model.ProgressItem
import java.util.Date

class UpdateLearningPlanUseCase {

    /**
     * Lọc và phân loại danh sách từ vựng thành Kế hoạch học tập của ngày hôm nay
     * @param allProgress Toàn bộ trạng thái SRS của các từ vựng từ Database/Store
     * @return Cặp dữ liệu gồm: Pair(Danh sách từ cần ôn tập, Danh sách từ mới)
     */
    operator fun invoke(allProgress: List<ProgressItem>): Pair<List<ProgressItem>, List<List<ProgressItem>>> {
        val today = Date()

        // 1. Lọc các từ đến hạn ôn tập (nextReviewDate <= hôm nay) và đã từng được kích hoạt (repetitions > 0)
        val reviewWords = allProgress.filter { 
            it.repetitions > 0 && (it.nextReviewDate.before(today) || isSameDay(it.nextReviewDate, today)) 
        }

        // 2. Lọc các từ mới hoàn toàn chưa học (chưa có dữ liệu tiến trình hoặc repetitions = 0)
        val newWords = allProgress.filter { it.repetitions == 0 }

        return Pair(reviewWords, newWords)
    }

    // Hàm hỗ trợ so sánh xem hai mốc thời gian có cùng một ngày hay không
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { time = date1 }
        val cal2 = java.util.Calendar.getInstance().apply { time = date2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}