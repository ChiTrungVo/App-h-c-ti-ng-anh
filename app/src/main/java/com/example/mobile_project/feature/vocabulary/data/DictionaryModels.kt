package com.example.mobile_project.feature.vocabulary.data

/**
 * Chi tiết một từ vựng lấy từ từ điển online (Datamuse + Free Dictionary API + MyMemory).
 * Dùng để tự động điền form khi người dùng chọn một từ gợi ý.
 *
 * Tất cả field có default rỗng vì mỗi nguồn API có thể thiếu dữ liệu;
 * UI chỉ điền những field non-blank để không ghi đè dữ liệu người dùng tự nhập.
 */
data class WordSuggestionDetails(
    val word: String,
    val pronunciation: String = "",      // IPA, ví dụ "/həˈloʊ/"
    val partOfSpeech: String = "",       // noun, verb, adjective...
    val definition: String = "",         // Định nghĩa tiếng Anh
    val example: String = "",            // Câu ví dụ tiếng Anh
    val meaning: String = "",            // Nghĩa tiếng Việt (dịch từ MyMemory)
    val collocations: List<String> = emptyList() // Từ đồng nghĩa / cụm liên quan
)
