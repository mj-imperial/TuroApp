package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ScreeningExamLearningResources
import com.example.turomobileapp.models.ScreeningExamResponse
import com.example.turomobileapp.models.ScreeningExamResultsResponse
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
class ScreeningExamResultViewModel @Inject constructor(
    private val assessmentResultRepository: AssessmentResultRepository,
    private val quizRepository: QuizRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _screeningExamId: String = checkNotNull(savedStateHandle["screeningExamId"])

    private val _uiState = MutableStateFlow(ScreeningExamResultUIState())
    val uiState: StateFlow<ScreeningExamResultUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { loadMetaData() }
            launch { loadResults() }
        }
    }

    fun loadMetaData(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMetadata = true,errorMessage = null) }

            quizRepository.getScreeningExam(_screeningExamId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loadingMetadata = false, exam = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingMetadata = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun loadResults(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingResults = true,errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.getScreeningExamResult(studentId, _screeningExamId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loadingResults = false, result = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingResults = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun loadLearningResources(conceptId: String, topicId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }

            quizRepository.getLearningResources(conceptId, topicId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, resources = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun setCatchUp(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            quizRepository.setCatchUp(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, message = resp.message) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class ScreeningExamResultUIState(
    val loadingMetadata: Boolean = false,
    val loadingResults: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val message: String = "",
    val exam: ScreeningExamResponse? = null,
    val result: ScreeningExamResultsResponse? = null,
    val resources: List<ScreeningExamLearningResources> = emptyList()
){
    val loadingInit: Boolean get() = loadingMetadata || loadingResults
}