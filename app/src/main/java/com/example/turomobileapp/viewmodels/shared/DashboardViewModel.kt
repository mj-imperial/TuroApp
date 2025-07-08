package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CourseResponse
import com.example.turomobileapp.repositories.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
): ViewModel(){

    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState: StateFlow<DashboardUIState> = _uiState.asStateFlow()

    fun loadCourses(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            courseRepository.getCoursesForUser().collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { courses ->
                        _uiState.update { it.copy(loading = false, courses = courses) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class DashboardUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val courses: List<CourseResponse> = emptyList(),
)