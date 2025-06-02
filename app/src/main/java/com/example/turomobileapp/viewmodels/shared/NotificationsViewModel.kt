package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.ui.notifications.NotificationEntity
import com.example.turomobileapp.ui.notifications.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    repository: NotificationRepository
) : ViewModel() {
    val notifications: StateFlow<List<NotificationEntity>> =
        repository.streamNotifications()
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}