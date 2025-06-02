package com.example.turomobileapp.ui.notifications

import android.content.Context
import androidx.room.Room
import com.example.turomobileapp.ui.notifications.AppDatabase
import com.example.turomobileapp.ui.notifications.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "turo_app_database"
        )
            .build()
    }

    @Provides
    fun provideNotificationDao(db: AppDatabase): NotificationDao {
        return db.notificationDao()
    }
}
