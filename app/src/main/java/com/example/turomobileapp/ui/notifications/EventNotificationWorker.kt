package com.example.turomobileapp.ui.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


//TODO if not needed then delete
@HiltWorker
class EventNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationService: TuroNotificationService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: ""
        val route = inputData.getString("route") ?: "calendar_screen"

        notificationService.showNotification(title, message, route)
        return Result.success()
    }
}