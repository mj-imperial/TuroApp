package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AssessmentScoreResponse
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
class QuizDetailViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _quizId: String = checkNotNull(savedStateHandle["quizId"])

    private val _uiState = MutableStateFlow(QuizDetailUIState())
    val uiState: StateFlow<QuizDetailUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadQuizMetadata() }
            launch { loadAttemptHistory() }
        }
    }

    private fun loadQuizMetadata(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }

            val quiz = quizRepository.getQuiz(_quizId).first()
            handleResult(
                result = quiz,
                onSuccess = { quiz ->
                    _uiState.update { it.copy(loading = false, quiz = quiz) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err,loading = false) }
                },
            )
        }
    }

    private fun loadAttemptHistory(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.getScoresForStudentAndQuiz(studentId, _quizId).collect { result ->
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

data class QuizDetailUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val quizName: String = "",
    val quiz: QuizResponse? = null,
    val scores: List<AssessmentScoreResponse> = emptyList()
)