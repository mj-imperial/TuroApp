package com.example.turomobileapp.ui.notifications

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val dao: NotificationDao
) {
    fun streamNotifications(): Flow<List<NotificationEntity>> = dao.getAllNotifications()

    suspend fun saveNotification(title: String, message: String, route: String) {
        val entity = NotificationEntity(
            title = title,
            message = message,
            route = route
        )
        dao.insert(entity)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun deleteNotification(notification: NotificationEntity) {
        dao.delete(notification)
    }
}