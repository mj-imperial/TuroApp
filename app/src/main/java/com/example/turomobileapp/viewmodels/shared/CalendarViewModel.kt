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

            val role = sessionManager.role.value
            if (role.isNullOrBlank()) {
                _uiState.update { it.copy(loading = false, errorMessage = "No role") }
                return@launch
            }

            _uiState.update { it.copy(loading = true, errorMessage = null) }

            if (role == "STUDENT"){
                calendarRepository.getCalendarEventsForStudent(userId).collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { events ->
                            _uiState.update { it.copy(loading = false, rawEvents = events) }

                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

                            events.forEach {
                                val now = java.time.LocalDate.now()
                                val eventDate = it.date.toLocalDate()

                                if (!notifiedIds.contains(it.eventId) && java.time.temporal.ChronoUnit.DAYS.between(now, eventDate) == 7L) {
                                    val whenText = it.date.format(formatter)
                                    val title = "Upcoming Event: ${it.title}"
                                    val text = "This event is scheduled for $whenText — that's 1 week from today!"

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

                            }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        },
                    )
                }
            } else {
                calendarRepository.getCalendarEventsForTeacher(userId).collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { events ->
                            _uiState.update { it.copy(loading = false, rawEvents = events) }

                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

                            events.forEach {
                                val now = java.time.LocalDate.now()
                                val eventDate = it.date.toLocalDate()

                                if (!notifiedIds.contains(it.eventId) && java.time.temporal.ChronoUnit.DAYS.between(now, eventDate) == 7L) {
                                    val whenText = it.date.format(formatter)
                                    val title = "Upcoming Event: ${it.title}"
                                    val text = "This event is scheduled for $whenText — that's 1 week from today!"

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

                            }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        }
                    )
                }
            }

        }
    }
}

data class CalendarUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val rawEvents: List<CalendarResponse> = emptyList()
)