// Chứa logic thuật toán SM-2
package com.example.mobile_project.feature.progress.domain.usecase

import com.example.mobile_project.feature.progress.domain.model.ProgressItem
import java.util.Calendar
import java.util.Date
import kotlin.math.max

class CalculateNextReviewUseCase {

    /**
     * @param currentProgress: Trạng thái hiện tại của từ vựng
     * @param quality: Điểm đánh giá của người dùng từ 0 đến 5
     */
    operator fun invoke(currentProgress: ProgressItem, quality: Int): ProgressItem {
        // Nếu câu trả lời là sai (quality < 3), reset số lần lặp lại và tính lại khoảng cách ôn tập là 1 ngày
        if (quality < 3) {
            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            return currentProgress.copy(
                repetitions = 0,
                intervalDays = 1,
                nextReviewDate = calendar.time
            )
        }

        // 1. Tính toán số lần lặp lại liên tiếp thành công
        val nextRepetitions = currentProgress.repetitions + 1

        // 2. Tính toán khoảng cách ngày ôn tập tiếp theo (Interval)
        val nextIntervalDays = when (nextRepetitions) {
            1 -> 1
            2 -> 6
            else -> (currentProgress.intervalDays * currentProgress.easinessFactor).toInt()
        }

        // 3. Tính toán Hệ số độ dễ mới (Easiness Factor) dựa theo công thức chuẩn SM-2
        // EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val qDiff = 5 - quality
        var nextEF = currentProgress.easinessFactor + (0.1 - qDiff * (0.08 + qDiff * 0.02))
        
        // Không để hệ số Easiness Factor thấp hơn mức tối thiểu 1.3
        nextEF = max(1.3, nextEF)

        // 4. Tính toán chính xác ngày cần lặp lại (Next Review Date)
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, nextIntervalDays)
        }

        return currentProgress.copy(
            repetitions = nextRepetitions,
            intervalDays = nextIntervalDays,
            easinessFactor = nextEF,
            nextReviewDate = calendar.time
        )
    }
}