package com.example.mobile_project.feature.notification.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Đọc lại settings đã lưu rồi schedule lại
            // Tạm thời để trống — khi tích hợp Appwrite thì gọi load settings ở đây
        }
    }
}