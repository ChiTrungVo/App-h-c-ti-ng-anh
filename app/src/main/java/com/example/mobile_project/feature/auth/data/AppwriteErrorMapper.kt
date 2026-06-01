package com.example.mobile_project.feature.auth.data

import io.appwrite.exceptions.AppwriteException
import java.io.IOException

fun Throwable.toVietnameseAuthMessage(): String {
    val rawMessage = message.orEmpty()
    return when (this) {
        is AppwriteException -> when {
            code == 401 || rawMessage.contains("Invalid credentials", ignoreCase = true) ->
                "Email hoặc mật khẩu chưa đúng."
            rawMessage.contains("User cancelled login", ignoreCase = true) ||
                rawMessage.contains("cancelled", ignoreCase = true) ||
                rawMessage.contains("canceled", ignoreCase = true) ->
                "Bạn đã huỷ đăng nhập Google."
            rawMessage.contains("Invalid Origin", ignoreCase = true) ->
                "Appwrite chưa đăng ký đúng Android platform cho gói ứng dụng này."
            rawMessage.contains("invalid_client", ignoreCase = true) ->
                "Google OAuth client chưa hợp lệ. Kiểm tra Client ID/Client Secret trong Appwrite."
            rawMessage.contains("Invalid `url` param", ignoreCase = true) ||
                rawMessage.contains("Invalid URI", ignoreCase = true) ->
                "URL chuyển hướng chưa được đăng ký trong Appwrite Platform."
            rawMessage.contains("already exists", ignoreCase = true) ||
                rawMessage.contains("user_already_exists", ignoreCase = true) ->
                "Email này đã được đăng ký. Vui lòng đăng nhập hoặc dùng email khác."
            rawMessage.contains("password", ignoreCase = true) &&
                rawMessage.contains("8", ignoreCase = true) ->
                "Mật khẩu cần ít nhất 8 ký tự."
            rawMessage.contains("verification", ignoreCase = true) ||
                rawMessage.contains("redirect", ignoreCase = true) ->
                "Không thể gửi email xác minh. Kiểm tra URL chuyển hướng và cấu hình email trong Appwrite."
            rawMessage.contains("recovery", ignoreCase = true) ||
                rawMessage.contains("token", ignoreCase = true) ||
                rawMessage.contains("secret", ignoreCase = true) ->
                "Liên kết đặt lại mật khẩu hoặc xác minh đã hết hạn."
            rawMessage.contains("network", ignoreCase = true) ||
                rawMessage.contains("timeout", ignoreCase = true) ->
                "Không thể kết nối Appwrite. Vui lòng kiểm tra mạng."
            rawMessage.isNotBlank() -> rawMessage
            else -> "Không thể kết nối Appwrite."
        }
        is IOException -> "Không thể kết nối mạng. Vui lòng thử lại."
        else -> rawMessage.ifBlank { "Đã có lỗi xảy ra." }
    }
}

fun Throwable.toVietnameseAvatarUploadMessage(): String {
    val rawMessage = message.orEmpty()
    return when (this) {
        is AppwriteException -> when {
            code == 401 ->
                "Hệ thống chưa cấp quyền tải ảnh đại diện lên kho lưu trữ cho tài khoản đang đăng nhập."
            code == 413 || rawMessage.contains("size", ignoreCase = true) ->
                "Ảnh đại diện quá lớn. Vui lòng chọn ảnh nhỏ hơn."
            rawMessage.contains("extension", ignoreCase = true) ||
                rawMessage.contains("mime", ignoreCase = true) ||
                rawMessage.contains("type", ignoreCase = true) ->
                "Định dạng ảnh chưa được hỗ trợ. Vui lòng chọn ảnh JPG, PNG hoặc WEBP."
            rawMessage.contains("bucket", ignoreCase = true) ||
                rawMessage.contains("permission", ignoreCase = true) ||
                rawMessage.contains("not authorized", ignoreCase = true) ->
                "Kho lưu trữ ảnh đại diện chưa được cấu hình quyền upload cho người dùng."
            rawMessage.contains("network", ignoreCase = true) ||
                rawMessage.contains("timeout", ignoreCase = true) ->
                "Không thể kết nối Appwrite. Vui lòng kiểm tra mạng."
            rawMessage.isNotBlank() -> rawMessage
            else -> "Không thể tải ảnh đại diện lên. Vui lòng thử lại."
        }
        is IOException -> "Không thể kết nối mạng. Vui lòng thử lại."
        else -> rawMessage.ifBlank { "Không thể tải ảnh đại diện lên. Vui lòng thử lại." }
    }
}
