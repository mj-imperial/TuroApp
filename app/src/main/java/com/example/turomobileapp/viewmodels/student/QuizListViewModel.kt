package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.enums.QuizType
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.repositories.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val typeStr: String = checkNotNull(savedStateHandle["type"])

    val quizType: QuizType = QuizType.valueOf(typeStr)

    private val _uiState = MutableStateFlow(QuizListUIState(quizType = quizType))
    val uiState: StateFlow<QuizListUIState> = _uiState.asStateFlow()

    init {
        loadQuizList()
    }

    private fun loadQuizList(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            quizRepository.getQuizzesInCourse(_courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { quizzes ->
                        _uiState.update { it.copy(loading = false, quizList = quizzes) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class QuizListUIState(
    val quizType: QuizType,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val quizList: List<QuizResponse> = emptyList(),
)