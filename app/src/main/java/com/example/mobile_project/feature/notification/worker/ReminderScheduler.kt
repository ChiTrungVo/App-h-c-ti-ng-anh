package com.example.mobile_project.feature.notification.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    private const val WORK_TAG = "minlish_reminder"

    /**
     * @param reminderTime "HH:mm" ví dụ "20:30"
     * @param reminderDays danh sách ["T2","T3",...,"CN"]
     */
    fun schedule(context: Context, reminderTime: String, reminderDays: List<String>) {
        cancel(context) // hủy lịch cũ trước

        val parts = reminderTime.split(":")
        if (parts.size != 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        val dayMap = mapOf(
            "T2" to Calendar.MONDAY,
            "T3" to Calendar.TUESDAY,
            "T4" to Calendar.WEDNESDAY,
            "T5" to Calendar.THURSDAY,
            "T6" to Calendar.FRIDAY,
            "T7" to Calendar.SATURDAY,
            "CN" to Calendar.SUNDAY
        )

        reminderDays.forEach { day ->
            val calendarDay = dayMap[day] ?: return@forEach
            val delay = calculateDelayMs(hour, minute, calendarDay)

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("${WORK_TAG}_$day")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "${WORK_TAG}_$day",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }

    private fun calculateDelayMs(hour: Int, minute: Int, dayOfWeek: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Nếu giờ đã qua trong tuần này thì sang tuần sau
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.WEEK_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }
}