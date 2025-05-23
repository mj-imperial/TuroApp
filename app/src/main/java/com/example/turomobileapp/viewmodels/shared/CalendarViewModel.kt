package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CalendarResponse
import com.example.turomobileapp.repositories.CalendarRepository
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(CalendarUIState())
    val uiState: StateFlow<CalendarUIState> = _uiState.asStateFlow()

    init {
        getCalendarEventsForUser()
    }

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