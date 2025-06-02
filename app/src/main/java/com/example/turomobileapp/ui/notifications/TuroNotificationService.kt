package com.example.turomobileapp.ui.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.turomobileapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class TuroNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
) {
    private val notificationManager =
        context.getSystemService(NotificationManager::class.java)

    fun showNotification(
        notificationTitle: String,
        notificationText: String,
        route: String,
    ) {
        val deepLinkUri = "turo://$route".toUri()
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            setClassName(context, "com.example.turomobileapp.MainActivity")
        }

        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val contentPendingIntent = PendingIntent.getActivity(context,0,intent,pendingIntentFlags)

        val notification = NotificationCompat.Builder(context, "turo_notification")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.notification)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        notificationManager.notify(Random.nextInt(), notification)

        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.saveNotification(title = notificationTitle, message = notificationText, route = route)
        }
    }
}