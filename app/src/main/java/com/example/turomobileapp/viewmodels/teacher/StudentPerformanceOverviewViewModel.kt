package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentPerformanceListResponse
import com.example.turomobileapp.repositories.StudentProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentPerformanceOverviewViewModel @Inject constructor(
    private val studentProgressRepository: StudentProgressRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(StudentPerformanceOverviewUIState())
    val uiState: StateFlow<StudentPerformanceOverviewUIState> = _uiState.asStateFlow()

    init {
        getStudentListProgress()
    }

    fun getStudentListProgress(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            studentProgressRepository.getCourseProgressForAllStudents(_courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            numberOfAssessment = resp.overallNumberOfAssessments,
                            studentProgressList = resp.progresses
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class StudentPerformanceOverviewUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val numberOfAssessment: Int = 0,
    val studentProgressList: List<StudentPerformanceListResponse> = emptyList()
)