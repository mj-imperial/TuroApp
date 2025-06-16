package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentPerformanceListResponse
import com.example.turomobileapp.models.StudentPerformanceModuleList
import com.example.turomobileapp.repositories.StudentProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentPerformanceViewModel @Inject constructor(
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
                            lowestAssessmentAverageQuizName = resp.lowestAssessmentAverageQuizName,
                            lowestAssessmentAverage = resp.lowestAssessmentAverage,
                            highestAssessmentAverageQuizName = resp.highestAssessmentAverageQuizName,
                            highestAssessmentAverage = resp.highestAssessmentAverage,
                            lowestScoringModuleName = resp.lowestScoringModuleName,
                            lowestScoringModuleAverage = resp.lowestScoringModuleAverage,
                            highestScoringModuleName = resp.highestScoringModuleName,
                            highestScoringModuleAverage = resp.highestScoringModuleAverage,
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

    fun updateIndividualStudentInfo(
        studentId: String,
        studentName: String,
        profilePic: String,
        completedAssessments: Int,
        averageGrade: Double,
        points: Int,
        rank: Int
    ){
        _uiState.update { it.copy(currentStudentInfo = CurrentStudentInfo(
            studentId = studentId,
            studentName = studentName,
            profilePic = profilePic,
            completedAssessments = completedAssessments,
            averageGrade = averageGrade,
            points = points,
            rank = rank
        )) }
    }

    fun getIndividualStudentProgress(studentId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            studentProgressRepository.getIndividualStudentCourseProgress(studentId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, currentStudentScores = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun clearCurrentStudentInfo() {
        _uiState.update { it.copy(currentStudentInfo = null) }
    }
}

data class StudentPerformanceOverviewUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val numberOfAssessment: Int = 0,
    val studentProgressList: List<StudentPerformanceListResponse> = emptyList(),
    val lowestAssessmentAverageQuizName: String = "",
    val lowestAssessmentAverage: Double = 0.0,
    val highestAssessmentAverageQuizName: String = "",
    val highestAssessmentAverage: Double = 0.0,
    val lowestScoringModuleName: String = "",
    val lowestScoringModuleAverage: Double = 0.0,
    val highestScoringModuleName: String = "",
    val highestScoringModuleAverage: Double = 0.0,
    val currentStudentInfo: CurrentStudentInfo? = null,
    val currentStudentScores: List<StudentPerformanceModuleList> = emptyList()
)

data class CurrentStudentInfo(
    val studentId: String,
    val studentName: String,
    val profilePic: String,
    val completedAssessments: Int,
    val averageGrade: Double,
    val points: Int,
    val rank: Int
)