package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CourseResponse
import com.example.turomobileapp.models.TeacherCourseResponse
import com.example.turomobileapp.repositories.CourseRepository
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState: StateFlow<DashboardUIState> = _uiState.asStateFlow()

    fun loadCourses(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val role: String = sessionManager.role.filterNotNull().first()


            if (role == "STUDENT"){
                courseRepository.getCoursesForStudent().collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { courses ->
                            _uiState.update { it.copy(loading = false, courses = courses, isStudent = true) }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        }
                    )
                }
            }else{
                val teacherId: String = sessionManager.userId.filterNotNull().first()
                courseRepository.getCoursesForTeacher(teacherId).collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { courses ->
                            _uiState.update { it.copy(loading = false, teacherCourses = courses, isStudent = false) }
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

data class DashboardUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val courses: List<CourseResponse> = emptyList(),
    val teacherCourses: List<TeacherCourseResponse> = emptyList(),
    val isStudent: Boolean = true
)