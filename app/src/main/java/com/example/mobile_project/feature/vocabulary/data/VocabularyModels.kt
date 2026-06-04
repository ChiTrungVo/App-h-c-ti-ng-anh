package com.example.mobile_project.feature.vocabulary.data

/**
 * Mở rộng model cho bộ từ vựng, bổ sung thêm các field cần thiết cho Appwrite mapping.
 * Không xóa VocabularySet cũ trong data/model/ để tránh ảnh hưởng SampleData,
 * mà dùng file nàm mapping layer features/vocabulary.
 */

data class VocabularySetForm(
    val title: String = "",
    val description: String = "",
    val tags: String = "", // Chuỗi cách nhau bằng dấu phẩy, hiển thị trong TextField
    val isPublic: Boolean = false
) {
    val tagList: List<String>
        get() = tags.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

    companion object {
        fun fromSet(set: com.example.mobile_project.data.model.VocabularySet): VocabularySetForm =
            VocabularySetForm(
                title = set.title,
                description = set.description,
                tags = set.tags.joinToString(", "),
                isPublic = set.isPublic
            )
    }
}

/**
 * WordForm: Giữ state của form nhập liệu từ vựng.
 * Khác VocabularyWord ở chỗ collocations là String (input người dùng)
 * thay vì List<String>.
 */
data class WordForm(
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val definition: String = "",
    val example: String = "",
    val collocations: String = "", // Chuỗi cách nhau bằng dấu phẩy
    val note: String = "",
    val imageUrl: String = ""
) {
    val collocationList: List<String>
        get() = collocations.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

    companion object {
        fun fromWord(w: com.example.mobile_project.data.model.VocabularyWord): WordForm =
            WordForm(
                word = w.word,
                pronunciation = w.pronunciation,
                meaning = w.meaning,
                definition = w.definition,
                example = w.example,
                collocations = w.collocations.joinToString(", "),
                note = w.note,
                imageUrl = w.imageUrl.orEmpty()
            )
    }
}
