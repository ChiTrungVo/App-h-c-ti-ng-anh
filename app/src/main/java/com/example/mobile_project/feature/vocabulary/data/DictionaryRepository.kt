package com.example.mobile_project.feature.vocabulary.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Repository lấy gợi ý và chi tiết từ vựng từ các API từ điển công khai (miễn phí, không cần key):
 *
 *  - Datamuse (https://api.datamuse.com)        : gợi ý từ theo prefix + từ đồng nghĩa
 *  - Free Dictionary API (dictionaryapi.dev)    : phiên âm IPA, định nghĩa, ví dụ tiếng Anh
 *  - MyMemory (api.mymemory.translated.net)     : dịch nghĩa sang tiếng Việt
 *
 * Mọi lời gọi mạng chạy trên Dispatchers.IO và "thất bại mềm" (trả về rỗng / dữ liệu một phần)
 * để không làm gián đoạn luồng nhập liệu của người dùng.
 */
class DictionaryRepository(
    private val client: OkHttpClient = defaultClient
) {

    companion object {
        private const val DATAMUSE_BASE = "https://api.datamuse.com"
        private const val DICTIONARY_BASE = "https://api.dictionaryapi.dev/api/v2/entries/en"
        private const val MYMEMORY_BASE = "https://api.mymemory.translated.net/get"

        private val defaultClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build()
        }
    }

    // ------------------------------------------------------------------ //
    //  AUTOCOMPLETE                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Gợi ý danh sách từ tiếng Anh bắt đầu bằng (hoặc gần giống) [prefix].
     * Ví dụ: "he" -> ["hello", "help", "her", ...].
     *
     * @param max Số lượng gợi ý tối đa.
     * @return Danh sách từ; rỗng nếu prefix quá ngắn hoặc lỗi mạng.
     */
    suspend fun suggest(prefix: String, max: Int = 8): List<String> {
        val trimmed = prefix.trim()
        if (trimmed.length < 2) return emptyList()

        val url = "$DATAMUSE_BASE/sug?s=${trimmed.encode()}&max=$max"
        val body = httpGet(url) ?: return emptyList()

        return runCatching {
            val array = JSONArray(body)
            (0 until array.length()).mapNotNull { i ->
                array.optJSONObject(i)?.optString("word")?.takeIf { it.isNotBlank() }
            }
        }.getOrDefault(emptyList())
    }

    // ------------------------------------------------------------------ //
    //  LOOKUP CHI TIẾT                                                   //
    // ------------------------------------------------------------------ //

    /**
     * Lấy đầy đủ thuộc tính của một từ: IPA, từ loại, định nghĩa, ví dụ,
     * nghĩa tiếng Việt và từ đồng nghĩa (collocations).
     *
     * Gọi 3 nguồn độc lập; nguồn nào lỗi thì field tương ứng để rỗng.
     */
    suspend fun lookup(word: String): WordSuggestionDetails {
        val normalized = word.trim()
        if (normalized.isBlank()) return WordSuggestionDetails(word = word)

        val entry = fetchDictionaryEntry(normalized)
        val vietnamese = translateToVietnamese(normalized)
        val synonyms = fetchSynonyms(normalized)

        return WordSuggestionDetails(
            word = normalized,
            pronunciation = entry?.pronunciation.orEmpty(),
            partOfSpeech = entry?.partOfSpeech.orEmpty(),
            definition = entry?.definition.orEmpty(),
            example = entry?.example.orEmpty(),
            meaning = vietnamese,
            collocations = synonyms
        )
    }

    // ------------------------------------------------------------------ //
    //  NGUỒN: Free Dictionary API                                        //
    // ------------------------------------------------------------------ //

    private data class DictionaryEntry(
        val pronunciation: String,
        val partOfSpeech: String,
        val definition: String,
        val example: String
    )

    private suspend fun fetchDictionaryEntry(word: String): DictionaryEntry? {
        val url = "$DICTIONARY_BASE/${word.encode()}"
        val body = httpGet(url) ?: return null

        return runCatching {
            val root = JSONArray(body)
            val first = root.optJSONObject(0) ?: return@runCatching null

            // IPA: ưu tiên field "phonetic", nếu không có thì tìm trong mảng "phonetics"
            val phonetic = first.optString("phonetic").ifBlank {
                val phonetics = first.optJSONArray("phonetics")
                var found = ""
                if (phonetics != null) {
                    for (i in 0 until phonetics.length()) {
                        val text = phonetics.optJSONObject(i)?.optString("text").orEmpty()
                        if (text.isNotBlank()) {
                            found = text
                            break
                        }
                    }
                }
                found
            }

            val meanings = first.optJSONArray("meanings")
            val firstMeaning = meanings?.optJSONObject(0)
            val partOfSpeech = firstMeaning?.optString("partOfSpeech").orEmpty()
            val definitions = firstMeaning?.optJSONArray("definitions")
            val firstDef = definitions?.optJSONObject(0)

            DictionaryEntry(
                pronunciation = phonetic,
                partOfSpeech = partOfSpeech,
                definition = firstDef?.optString("definition").orEmpty(),
                example = firstDef?.optString("example").orEmpty()
            )
        }.getOrNull()
    }

    // ------------------------------------------------------------------ //
    //  NGUỒN: Datamuse (từ đồng nghĩa)                                   //
    // ------------------------------------------------------------------ //

    private suspend fun fetchSynonyms(word: String, max: Int = 5): List<String> {
        val url = "$DATAMUSE_BASE/words?rel_syn=${word.encode()}&max=$max"
        val body = httpGet(url) ?: return emptyList()

        return runCatching {
            val array = JSONArray(body)
            (0 until array.length()).mapNotNull { i ->
                array.optJSONObject(i)?.optString("word")?.takeIf { it.isNotBlank() }
            }
        }.getOrDefault(emptyList())
    }

    // ------------------------------------------------------------------ //
    //  NGUỒN: MyMemory (dịch tiếng Việt)                                 //
    // ------------------------------------------------------------------ //

    private suspend fun translateToVietnamese(text: String): String {
        val url = "$MYMEMORY_BASE?q=${text.encode()}&langpair=en|vi"
        val body = httpGet(url) ?: return ""

        return runCatching {
            val root = JSONObject(body)
            root.optJSONObject("responseData")?.optString("translatedText").orEmpty()
        }.getOrDefault("")
    }

    // ------------------------------------------------------------------ //
    //  HELPERS                                                           //
    // ------------------------------------------------------------------ //

    /** Thực hiện GET đồng bộ trên Dispatchers.IO; trả về body hoặc null nếu lỗi. */
    private suspend fun httpGet(url: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "MinLish-Android")
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) response.body?.string() else null
            }
        }.getOrNull()
    }

    private fun String.encode(): String = URLEncoder.encode(this, "UTF-8")
}
