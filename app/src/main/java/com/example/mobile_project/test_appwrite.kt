package com.example.mobile_project

import android.util.Log
import com.example.mobile_project.core.appwrite.AppwriteClient
import com.example.mobile_project.core.appwrite.AppwriteConfig
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException

private const val APPWRITE_TEST_TAG = "APPWRITE_TEST"

private fun logTest(message: String) {
    // Sử dụng ASSERT level để log hiện màu nổi bật trong Logcat
    Log.println(Log.ASSERT, APPWRITE_TEST_TAG, message)
}

/**
 * Hàm test kết nối và thao tác với Database Appwrite dựa trên schema thực tế.
 */
suspend fun testAppwriteConnection() {
    val runId = System.currentTimeMillis()
    logTest("==============================")
    logTest("TEST APPWRITE RUN: $runId")
    logTest("==============================")
    try {
        logTest("BẮT ĐẦU KIỂM TRA DATABASE")

        val dbId = AppwriteConfig.DATABASE_ID
        logTest("Đang sử dụng Database ID: $dbId")

        // 1. Thử đọc dữ liệu từ collection 'vocabulary_sets'
        val setsCollectionId = "vocabulary_sets"
        logTest("Đang thử đọc dữ liệu từ collection: $setsCollectionId...")
        val setsResponse = AppwriteClient.databases.listDocuments(
            databaseId = dbId,
            collectionId = setsCollectionId
        )
        logTest("KẾT NỐI DATABASE THÀNH CÔNG!")
        logTest("Tổng số Vocabulary Sets tìm thấy: ${setsResponse.total}")

        // 2. Thử tạo một bộ từ vựng (Set) mới
        logTest("Đang thử tạo Vocabulary Set test...")
        val testSetData = mapOf(
            "userId" to "debug_user_123",
            "title" to "Test Set $runId",
            "description" to "Bộ từ vựng tạo từ code debug",
            "wordCount" to 0,
            "isPublic" to false
        )

        val newSet = AppwriteClient.databases.createDocument(
            databaseId = dbId,
            collectionId = setsCollectionId,
            documentId = ID.unique(),
            data = testSetData
        )
        logTest("TẠO SET THÀNH CÔNG! ID: ${newSet.id}")

        // 3. Thử tạo một từ vựng thuộc Set vừa tạo
        val vocabCollectionId = "vocabularies"
        logTest("Đang thử tạo từ vựng (Vocabulary) test...")
        val testVocabData = mapOf(
            "setId" to newSet.id,
            "userId" to "debug_user_123",
            "word" to "Hello",
            "meaning" to "Xin chào",
            "pronunciation" to "/həˈloʊ/",
            "example" to "Hello world!"
        )

        val newVocab = AppwriteClient.databases.createDocument(
            databaseId = dbId,
            collectionId = vocabCollectionId,
            documentId = ID.unique(),
            data = testVocabData
        )
        logTest("TẠO TỪ VỰNG THÀNH CÔNG! ID: ${newVocab.id}")

    } catch (e: AppwriteException) {
        logTest("LỖI APPWRITE (Code: ${e.code}): ${e.message}")
        Log.e(APPWRITE_TEST_TAG, "Gợi ý: Kiểm tra lại Permissions trong Appwrite Console cho Role 'any'.")
    } catch (e: Exception) {
        logTest("LỖI KHÔNG XÁC ĐỊNH: ${e.message}")
    } finally {
        logTest("KẾT THÚC TEST APPWRITE RUN: $runId")
        logTest("==============================")
    }
}