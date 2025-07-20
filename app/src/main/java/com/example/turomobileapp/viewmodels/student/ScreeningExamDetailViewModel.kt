package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ScreeningExamAssessmentResult
import com.example.turomobileapp.models.ScreeningExamResponse
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.QuizRepository
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
class ScreeningExamDetailViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _screeningExamId: String = checkNotNull(savedStateHandle["screeningExamId"])

    private val _uiState = MutableStateFlow(ScreeningExamDetailUIState())
    val uiState: StateFlow<ScreeningExamDetailUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadQuiz() }
            launch { loadAttemptHistory() }
        }
    }

    fun loadQuiz(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }

            val quiz = quizRepository.getScreeningExam(_screeningExamId).first()
            handleResult(
                result = quiz,
                onSuccess = { quiz ->
                    _uiState.update { it.copy(loading = false, exam = quiz) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err,loading = false) }
                },
            )
        }
    }

    fun loadAttemptHistory(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.getScreeningExamAssessmentResultForStudent(studentId, _screeningExamId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { scores ->
                        _uiState.update { it.copy(loading = false, scores = scores) }
                    },
                    onFailure = {  err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }
}

data class ScreeningExamDetailUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val exam: ScreeningExamResponse? = null,
    val scores: List<ScreeningExamAssessmentResult> = emptyList()
)