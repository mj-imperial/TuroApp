package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
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
class TeacherProfileViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(TeacherProfileUIState(
        teacherName = "${sessionManager.firstName.value} ${sessionManager.lastName.value}",
        email = sessionManager.email.value.toString(),
        role = sessionManager.role.value.toString(),
        profilePic = sessionManager.profilePic.value
    ))
    val uiState: StateFlow<TeacherProfileUIState> = _uiState.asStateFlow()

    init {
        getTeacherCourses()
    }

    fun getTeacherCourses(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val teacherId: String = sessionManager.userId.filterNotNull().first()

            courseRepository.getCoursesForTeacher(teacherId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        resp.forEach {
                            _uiState.update { it.copy(
                                loading = false,
                                coursesTaught = resp.associate { course -> course.courseCode to course.courseName }
                            ) }
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

data class TeacherProfileUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val teacherName: String = "",
    val email: String = "",
    val role: String = "",
    val profilePic: ByteArray? = null,
    val coursesTaught: Map<String, String> = emptyMap()
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as TeacherProfileUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (teacherName!=other.teacherName) return false
        if (email!=other.email) return false
        if (role!=other.role) return false
        if (!profilePic.contentEquals(other.profilePic)) return false
        if (coursesTaught!=other.coursesTaught) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + teacherName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        result = 31 * result + coursesTaught.hashCode()
        return result
    }
}