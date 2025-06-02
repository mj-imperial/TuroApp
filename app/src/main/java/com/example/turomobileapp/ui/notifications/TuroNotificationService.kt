package com.example.turomobileapp.ui.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.turomobileapp.R
import kotlin.random.Random

class TuroNotificationService(
    private val context: Context
){
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showNotification(
        notificationTitle: String,
        notificationText: String
    ){
        val notification = NotificationCompat.Builder(context, "turo_notification")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.notification)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }
}