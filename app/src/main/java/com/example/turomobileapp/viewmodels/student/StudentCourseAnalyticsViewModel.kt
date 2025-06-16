package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentPerformanceModuleList
import com.example.turomobileapp.models.StudentPerformanceResponse
import com.example.turomobileapp.repositories.StudentProgressRepository
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
class StudentCourseAnalyticsViewModel @Inject constructor(
    private val studentProgressRepository: StudentProgressRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(StudentCourseAnalyticsUIState())
    val uiState: StateFlow<StudentCourseAnalyticsUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { getOverviewInfo() }
            launch { getStudentScoreList() }
        }
    }

    fun getOverviewInfo(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingOverview = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            studentProgressRepository.getIndividualStudentPerformanceList(studentId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loadingOverview = false, overviewInfo = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingOverview = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getStudentScoreList(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingModuleScores = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            studentProgressRepository.getIndividualStudentCourseProgress(studentId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loadingModuleScores = false, modulesScores = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingModuleScores = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun updateModuleInfo(moduleId: String){
        _uiState.update { it.copy(currentStudentModule = _uiState.value.modulesScores.find { module -> module.moduleId == moduleId }) }
    }

    fun clearModuleInfo(){
        _uiState.update { it.copy(currentStudentModule = null) }
    }
}

data class StudentCourseAnalyticsUIState(
    val loadingModuleScores: Boolean = false,
    val loadingOverview: Boolean = false,
    val errorMessage: String? = null,
    val overviewInfo: StudentPerformanceResponse? = null,
    val modulesScores: List<StudentPerformanceModuleList> = emptyList(),
    val currentStudentModule: StudentPerformanceModuleList? = null
){
    val loading: Boolean get() = loadingModuleScores || loadingOverview
}

