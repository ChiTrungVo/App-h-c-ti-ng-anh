package com.example.mobile_project.feature.notification.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.mobile_project.R
import android.os.Build
object NotificationHelper {

    private const val CHANNEL_ID = "minlish_reminder"
    private const val CHANNEL_NAME = "Nhắc học MinLish"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Thông báo nhắc học từ vựng hàng ngày"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendReminder(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_check_circle) // thay icon phù hợp
            .setContentTitle("Đến giờ học rồi! 📚")
            .setContentText("Hôm nay bạn chưa ôn từ vựng. Học thôi nào!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(1001, notification)
    }
}