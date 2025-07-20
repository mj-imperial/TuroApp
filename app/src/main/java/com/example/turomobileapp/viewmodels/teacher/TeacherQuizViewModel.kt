package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.turomobileapp.models.AssessmentScoreResponse
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TeacherQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val assessmentResultRepository: AssessmentResultRepository,
): ViewModel(){
    private val _quizId: String = checkNotNull(savedStateHandle["activityId"])

    private val _uiState = MutableStateFlow(TeacherQuizDetailUIState())
    val uiState: StateFlow<TeacherQuizDetailUIState> = _uiState.asStateFlow()
}

data class TeacherQuizDetailUIState(
    val loadingMetadata: Boolean = false,
    val loadingResults: Boolean = false,
    val errorMessage: String? = null,
    val quizName: String = "",
    val quiz: QuizResponse? = null,
    val scores: List<AssessmentScoreResponse> = emptyList(),
){
    val loading: Boolean get() = loadingMetadata || loadingResults
}