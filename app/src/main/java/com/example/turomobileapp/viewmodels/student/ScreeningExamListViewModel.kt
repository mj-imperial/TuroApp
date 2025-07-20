package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ScreeningExamListResponse
import com.example.turomobileapp.repositories.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreeningExamListViewModel @Inject constructor(
   private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(ScreeningExamListUIState())
    val uiState: StateFlow<ScreeningExamListUIState> = _uiState.asStateFlow()

    init {
        getScreeningExams()
    }

    fun getScreeningExams(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            quizRepository.getScreeningExamList(_courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, exams = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class ScreeningExamListUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val exams: List<ScreeningExamListResponse> = emptyList()
)