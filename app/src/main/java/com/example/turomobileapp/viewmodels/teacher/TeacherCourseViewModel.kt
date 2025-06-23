package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherCourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(TeacherCourseUIState())
    val uiState: StateFlow<TeacherCourseUIState> = _uiState.asStateFlow()

    init {
        getCoursePicture()
    }

    fun getCoursePicture(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            courseRepository.getCoursePicture().collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, coursePic = resp.coursePic) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class TeacherCourseUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val coursePic: ByteArray = byteArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as TeacherCourseUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (!coursePic.contentEquals(other.coursePic)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + coursePic.contentHashCode()
        return result
    }
}