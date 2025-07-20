package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.LongQuiz
import com.example.turomobileapp.models.ModuleQuiz
import com.example.turomobileapp.models.ScreeningResult
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
        getStudentAnalysis()
    }

    fun getStudentAnalysis(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            studentProgressRepository.getStudentAnalysis(studentId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            section = resp.section,
                            points = resp.points,
                            courseName = resp.courseName,
                            overallGrade = resp.overallGrade,
                            moduleNames = resp.shortQuiz.module.sortedBy { it.moduleName }.map { it.moduleName },
                            practiceQuizzes = QuizModuleGroup(
                                average = resp.practiceQuiz.average,
                                modules = resp.practiceQuiz.module
                            ),
                            shortQuizzes = QuizModuleGroup(
                                average = resp.shortQuiz.average,
                                modules = resp.shortQuiz.module
                            ),
                            longQuizzes = resp.longQuiz,
                            screeningExam = listOf(resp.screening)
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

data class StudentCourseAnalyticsUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val section: String = "",
    val points: Int = 0,
    val courseName: String = "",
    val overallGrade: Double = 0.0,
    val moduleNames: List<String> = emptyList(),
    val practiceQuizzes: QuizModuleGroup? = null,
    val shortQuizzes: QuizModuleGroup? = null,
    val longQuizzes: LongQuiz? = null,
    val screeningExam: List<ScreeningResult> = emptyList()
)

data class QuizModuleGroup(
    val average: Double,
    val modules: List<ModuleQuiz>
)

