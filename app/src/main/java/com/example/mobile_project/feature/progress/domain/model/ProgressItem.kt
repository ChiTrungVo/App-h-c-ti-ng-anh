// Chứa thông tin trạng thái học (độ nhớ, khoảng cách lặp lại...)
package com.example.mobile_project.feature.progress.domain.model

import java.util.Date

data class ProgressItem(
    val vocabularyId: String,          // Identification of vocab/Flashcard
    val repetitions: Int = 0,          // Số lần lặp lại thành công liên tiếp
    val intervalDays: Int = 0,         // Khoảng cách ngày lặp lại tiếp theo
    val easinessFactor: Double = 2.5,  // Hệ số độ dễ (thiết lập mặc định ban đầu)
    val nextReviewDate: Date = Date()  // Ngày cần ôn tập tiếp theo
)
