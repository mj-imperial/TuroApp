package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.models.QuizResponse
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
class AssessmentResultViewModel @Inject constructor(
    private val assessmentResultRepository: AssessmentResultRepository,
    private val quizRepository: QuizRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _quizId: String = checkNotNull(savedStateHandle["quizId"])

    private val _uiState = MutableStateFlow(AssessmentResultUIState())
    val uiState: StateFlow<AssessmentResultUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadMetadata() }
            launch { loadAssessmentResults() }
        }
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }
            val quizResult = quizRepository.getQuiz(_quizId).first()
            handleResult(
                result = quizResult,
                onSuccess = { quiz ->
                    _uiState.update { it.copy(loading = false, quiz = quiz) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err,loading = false) }
                },
            )

            val quizContent = quizRepository.getQuizContent(_quizId).first()
            handleResult(
                result = quizContent,
                onSuccess = { content ->
                    _uiState.update { it.copy(loading = false,content = content) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(loading = false, errorMessage = err) }
                },
            )
        }
    }

    private fun loadAssessmentResults(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.getAssessmentResultsForQuizAndStudent(studentId, _quizId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {  results ->
                        _uiState.update { it.copy(loading = false, results = results) }
                    },
                    onFailure = {  err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class AssessmentResultUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val results: List<AssessmentResultResponse> = emptyList(),
    val quiz: QuizResponse? = null,
    val content: List<QuizContentResponse> = emptyList(),
)