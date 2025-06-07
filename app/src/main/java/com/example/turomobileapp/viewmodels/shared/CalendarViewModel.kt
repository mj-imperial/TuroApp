package com.example.turomobileapp.viewmodels.shared

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CalendarResponse
import com.example.turomobileapp.repositories.CalendarRepository
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val sessionManager: SessionManager,
    private val notificationService: TuroNotificationService,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val _uiState = MutableStateFlow(CalendarUIState())
    val uiState: StateFlow<CalendarUIState> = _uiState.asStateFlow()

    private val prefs: SharedPreferences = context.getSharedPreferences("calendar_prefs", Context.MODE_PRIVATE)

    private var notifiedIds: MutableSet<String> = prefs.getStringSet("notified_event_ids", emptySet())!!.toMutableSet()

    init {
        getCalendarEventsForUser()
    }

    @SuppressLint("UseKtx")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCalendarEventsForUser(){
        viewModelScope.launch {
            val userId = sessionManager.userId.value
            if (userId.isNullOrBlank()) {
                _uiState.update { it.copy(loading = false, errorMessage = "No user ID") }
                return@launch
            }

            _uiState.update { it.copy(loading = true, errorMessage = null) }

            calendarRepository.getCalendarEventsForUser(userId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { events ->
                        _uiState.update { it.copy(loading = false, rawEvents = events) }

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

                        events.forEach {
                            if (notifiedIds.contains(it.eventId)) {
                                return@forEach
                            }

                            val date = it.date

                            val whenText = date.format(formatter)
                            val title = "Reminder: ${it.title}"
                            val text = "You have an event on $whenText"
                            notificationService.showNotification(
                                notificationTitle = title,
                                notificationText = text,
                                route = "calendar_screen"
                            )

                            notifiedIds.add(it.eventId)
                            prefs.edit()
                                .putStringSet("notified_event_ids", notifiedIds)
                                .apply()
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }
}

data class CalendarUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val rawEvents: List<CalendarResponse> = emptyList()
)