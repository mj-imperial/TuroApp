package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.QuestionResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.QuizRepository
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizAttemptViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _quizId: String = checkNotNull(savedStateHandle["quizId"])

    private val _uiState = MutableStateFlow(QuizAttemptUIState())
    val uiState: StateFlow<QuizAttemptUIState> = _uiState.asStateFlow()

    private val _events = Channel<QuizAttemptEvent>(Channel.BUFFERED)
    val events: Flow<QuizAttemptEvent> = _events.receiveAsFlow()

    init {
        loadMetadata()
        loadQuizContent()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true,errorMessage = null) }
            quizRepository.getQuiz(_quizId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { quiz ->
                        _uiState.update {
                            it.copy(
                                quizName = quiz.quizName,
                                quizType = quiz.quizTypeName,
                                timeLimit = quiz.timeLimit,
                                loading = false
                            )
                        }
                        startTimer(quiz.timeLimit)
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(errorMessage = err,loading = false) }
                    },
                )
            }
        }
    }

    private fun loadQuizContent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            quizRepository.getQuizContent(_quizId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { content ->
                        _uiState.update { it.copy(
                            loading = false,
                            content = content,
                            currentIndex = 0,
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun startTimer(timeLimit: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(timeRemaining = timeLimit) }
            while (_uiState.value.timeRemaining > 0) {
                delay(1_000L)
                _uiState.update { state -> state.copy(timeRemaining = state.timeRemaining - 1) }
            }
            onTimeUp()
        }
    }

    private fun onTimeUp() {
        viewModelScope.launch {
            _events.send(QuizAttemptEvent.SubmitQuiz)
        }
    }

    fun onOptionSelected(option: QuestionResponse) {
        val question = uiState.value.content[uiState.value.currentIndex]
        _uiState.update { st ->
            st.copy(
                answers = st.answers + (st.currentIndex to Answer.OptionAnswer(
                    questionId = question.questionId,
                    optionId   = option.optionId,
                    isCorrect  = option.isCorrect
                ))
            )
        }
    }

    fun onShortAnswerEntered(text: String) {
        val question = uiState.value.content[uiState.value.currentIndex]
        _uiState.update { state ->
            state.copy(
                answers = state.answers + (state.currentIndex to Answer.TextAnswer(question, text))
            )
        }
    }

    fun onNextClicked() {
        _uiState.update {
            val next = (it.currentIndex + 1).coerceAtMost(it.content.size - 1)
            it.copy(currentIndex = next)
        }
    }

    fun onSubmitClicked(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            _events.send(QuizAttemptEvent.SubmitQuiz)

            val answersList = uiState.value.answers.map { (index, answer) ->
                when (answer) {
                    is Answer.OptionAnswer -> {
                        AnswerUploadRequest(
                            questionId = answer.questionId,
                            optionId = answer.optionId,
                            isCorrect = answer.isCorrect
                        )
                    }

                    is Answer.TextAnswer -> {
                        val match: QuestionResponse? = answer.response.options
                            .firstOrNull { opt ->
                                opt.optionText.trim().equals(answer.text.trim(), ignoreCase = true)
                            }

                        AnswerUploadRequest(
                            questionId = uiState.value.content[index].toString(),
                            optionId   = match?.optionId ?: "",
                            isCorrect  = match?.isCorrect ?: false
                        )
                    }
                }
            }

            val score = answersList.sumOf { answer ->
                if (answer.isCorrect) {
                    uiState.value.content
                        .first { q -> q.questionId == answer.questionId }
                        .score
                } else {
                    0
                }
            }
            val maxScore = uiState.value.content.sumOf { it.score }
            val scorePercentage: Double = if (maxScore > 0) {
                (score.toDouble() / maxScore.toDouble()) * 100.0
            } else {
                0.0
            }
            val points: Int = when(scorePercentage){
                100.0 -> 5
                in 90.0..99.99 -> 4
                in 85.0..89.99 -> 3
                in 80.0..84.99 -> 2
                in 70.0..79.99 -> 1
                else -> 0
            }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.saveAssessmentResult(
                studentId = studentId,
                activityId =  _quizId,
                scorePercentage =  scorePercentage,
                earnedPoints =  points,
                answers = answersList
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        notificationService.showNotification(
                            notificationTitle = "Quiz Submitted",
                            notificationText = "You have completed ${_uiState.value.quizName} attempt. Tap to see your results.",
                            route = Screen.StudentQuizResult.createRoute(_quizId, false)
                        )
                        _events.trySend(QuizAttemptEvent.SubmitSuccess(response))
                    },
                    onFailure = { err ->
                        _events.trySend(QuizAttemptEvent.SubmitError(err.toString()))
                    },
                )
            }
        }
    }
}

data class QuizAttemptUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val quizName: String = "",
    val quizType: String = "",
    val timeLimit: Int = 0,
    val content: List<QuizContentResponse> = emptyList(),
    val currentIndex: Int = 0,
    val timeRemaining: Int = 0,
    val answers: Map<Int, Answer> = emptyMap()
)

sealed class Answer {
    data class OptionAnswer(val questionId: String,val optionId: String, val isCorrect: Boolean) : Answer()
    data class TextAnswer(val response: QuizContentResponse, val text: String) : Answer()
}

sealed class QuizAttemptEvent {
    data class NextQuestion(val index: Int) : QuizAttemptEvent()
    object SubmitQuiz : QuizAttemptEvent()
    data class SubmitSuccess(val response: AssessmentResultUploadResponse) : QuizAttemptEvent()
    data class SubmitError(val errorMessage: String) : QuizAttemptEvent()
}