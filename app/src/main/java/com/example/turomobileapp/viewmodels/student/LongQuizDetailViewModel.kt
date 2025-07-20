package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.LongQuizAssessmentResultResponse
import com.example.turomobileapp.models.LongQuizResponse
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
class LongQuizDetailViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val _longQuizId: String = checkNotNull(savedStateHandle["longQuizId"])

    private val _uiState = MutableStateFlow(LongQuizDetailUIState())
    val uiState: StateFlow<LongQuizDetailUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadLongQuizMetadata() }
            launch { loadAttemptHistory() }
        }
    }

    fun loadLongQuizMetadata(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMetadata = true, errorMessage = null) }

            val longQuiz = quizRepository.getLongQuiz(_longQuizId).first()
            handleResult(
                result = longQuiz,
                onSuccess = { resp ->
                    _uiState.update { it.copy(loadingMetadata = false, longQuizDetail = resp) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(loadingMetadata = false, errorMessage = err) }
                }
            )
        }
    }

    fun loadAttemptHistory(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingResults = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.getLongQuizResultsForStudent(_longQuizId, studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { scores ->
                        _uiState.update { it.copy(loadingResults = false, results = scores) }
                    },
                    onFailure = {  err ->
                        _uiState.update { it.copy(loadingResults = false, errorMessage = err) }
                    },
                )
            }
        }
    }
}

data class LongQuizDetailUIState(
    val loadingMetadata: Boolean = false,
    val loadingResults: Boolean = false,
    val errorMessage: String? = null,
    val longQuizDetail: LongQuizResponse? = null,
    val results: List<LongQuizAssessmentResultResponse> = emptyList()
){
    val loading: Boolean get() = loadingMetadata || loadingResults
}