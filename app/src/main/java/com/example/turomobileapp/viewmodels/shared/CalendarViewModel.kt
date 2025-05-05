package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.repositories.CalendarRepository
import com.example.turomobileapp.viewmodels.CalendarUiState
import com.example.turomobileapp.viewmodels.higherorderfunctions.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadAllCalendarEvents()
    }

    fun loadAllCalendarEvents(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

            calendarRepository.getCalendarEventsForUser(_userId.value, page = 0, pageSize = 0).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { eventList ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            eventList = eventList
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            errorMessage = error
                        )
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(
                            loading = true
                        )
                    }
                )
            }
        }
    }
}