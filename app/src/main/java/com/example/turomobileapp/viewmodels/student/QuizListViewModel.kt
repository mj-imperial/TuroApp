package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.enums.QuizType
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.QuizRepository
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.utils.unlockQuizzesInOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val savedStateHandle: SavedStateHandle,
    private val sessionManager: SessionManager
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
                        viewModelScope.launch {
                            val studentId: String = sessionManager.userId.filterNotNull().first()

                            val filtered = quizzes.filter { it.quizTypeName == quizType.name }

                            val enriched = filtered.map { quiz ->
                                val attempts = withContext(Dispatchers.IO) {
                                    getAssessmentResults(studentId, quiz.quizId)
                                }

                                val hasAnswered = attempts.isNotEmpty()
                                val passed = attempts.any { it.scorePercentage >= 75 }

                                quiz.copy(
                                    hasAnswered = hasAnswered,
                                    isUnlocked = passed
                                )
                            }

                            val unlocked = unlockQuizzesInOrder(
                                quizzes = enriched,
                                quizTypeOrder = listOf("PRACTICE", "SHORT", "LONG")
                            )

                            _uiState.update {
                                it.copy(loading = false, quizList = unlocked)
                            }
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    suspend fun getAssessmentResults(studentId: String, activityId: String): List<AssessmentResultResponse> {
        var resultList: List<AssessmentResultResponse> = emptyList()

        assessmentResultRepository.getAssessmentResultsForQuizAndStudent(studentId, activityId).collect { result ->
            handleResult(
                result = result,
                onSuccess = { resultList = it },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err) }
                }
            )
        }

        return resultList
    }
}

data class QuizListUIState(
    val quizType: QuizType,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val quizList: List<QuizResponse> = emptyList(),
)