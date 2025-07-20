package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.LongQuizResponse
import com.example.turomobileapp.models.QuestionResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.QuizRepository
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
class LongQuizAttemptViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val notificationService: TuroNotificationService,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val _longQuizId: String = checkNotNull(savedStateHandle["longQuizId"])

    private val _uiState = MutableStateFlow(LongQuizAttemptUIState())
    val uiState: StateFlow<LongQuizAttemptUIState> = _uiState.asStateFlow()

    private val _events = Channel<LongQuizAttemptEvent>(Channel.BUFFERED)
    val events: Flow<LongQuizAttemptEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            launch { loadMetadata() }
            launch { loadQuizContent() }
        }
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMetadata = true,errorMessage = null) }

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

    private fun loadQuizContent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingContent = true, errorMessage = null) }

            quizRepository.getLongQuizContent(_longQuizId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        val quizDetail = _uiState.value.longQuizDetail
                        val shuffled = resp.shuffled().take(quizDetail?.numberOfQuestions ?: resp.size)
                        _uiState.update {
                            it.copy(
                                loadingContent = false,
                                content = resp,
                                shuffledQuestions = shuffled,
                                currentIndex = 0,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingContent = false, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun startTimer(timeLimit: Int?) {
        viewModelScope.launch {
            _uiState.update { it.copy(timeRemaining = timeLimit) }
            _uiState.value.timeRemaining?.let {
                while (it > 0) {
                    delay(1_000L)
                    _uiState.update { state -> state.copy(timeRemaining = state.timeRemaining?.minus(
                        1
                    )) }
                }
            }
            onTimeUp()
        }
    }

    private fun onTimeUp() {
        viewModelScope.launch {
            _events.send(LongQuizAttemptEvent.SubmitQuiz)
        }
    }

    fun onOptionSelected(option: QuestionResponse) {
        val question = uiState.value.shuffledQuestions[uiState.value.currentIndex]
        _uiState.update { st ->
            st.copy(
                answers = st.answers + (st.currentIndex to LongQuizAnswer.OptionAnswer(
                    questionId = question.questionId,
                    optionId   = option.optionId,
                    isCorrect  = option.isCorrect
                ))
            )
        }
    }

    fun onShortAnswerEntered(text: String) {
        val question = uiState.value.shuffledQuestions[uiState.value.currentIndex]
        _uiState.update { state ->
            state.copy(
                answers = state.answers + (state.currentIndex to LongQuizAnswer.TextAnswer(question, text))
            )
        }
    }

    fun onNextClicked() {
        _uiState.update {
            val next = (it.currentIndex + 1).coerceAtMost(it.shuffledQuestions.size - 1)
            it.copy(currentIndex = next)
        }
    }

    fun onSubmitClicked(){
        if (_uiState.value.hasSubmitted) return

        viewModelScope.launch {
            _uiState.update { it.copy(loadingSubmit = true, errorMessage = null, hasSubmitted = true) }

            _events.send(LongQuizAttemptEvent.SubmitQuiz)

            val selectedQuestions = uiState.value.shuffledQuestions

            val answersList = selectedQuestions.mapIndexed { index, question ->
                val answer = uiState.value.answers[index]
                when (answer) {
                    is LongQuizAnswer.OptionAnswer -> AnswerUploadRequest(
                        questionId = answer.questionId,
                        optionId = answer.optionId,
                        isCorrect = if (answer.isCorrect) 1 else 0
                    )
                    is LongQuizAnswer.TextAnswer -> {
                        val match = question.options.firstOrNull { opt ->
                            opt.optionText.trim().uppercase() == answer.text.trim().uppercase()
                        }

                        AnswerUploadRequest(
                            questionId = question.questionId,
                            optionId = match?.optionId ?: "",
                            isCorrect = if(match?.isCorrect == true) 1 else 0
                        )
                    }
                    else -> AnswerUploadRequest(
                        questionId = question.questionId,
                        optionId = "",
                        isCorrect = 0
                    )
                }
            }

            val score = answersList.sumOf { answer ->
                if (answer.isCorrect == 1) {
                    selectedQuestions
                        .first { q -> q.questionId == answer.questionId }
                        .score
                } else {
                    0
                }
            }

            val maxScore = selectedQuestions.sumOf { it.score }
            val scorePercentage: Double = if (maxScore > 0) {
                (score.toDouble() / maxScore.toDouble()) * 100.0
            } else {
                0.0
            }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.saveLongQuizAssessmentResult(
                studentId = studentId,
                courseId = _courseId,
                longQuizId = _longQuizId,
                scorePercentage = scorePercentage,
                earnedPoints = score,
                answers = answersList
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        notificationService.showNotification(
                            notificationTitle = "Quiz Submitted",
                            notificationText = "You have completed ${_uiState.value.longQuizDetail?.longQuizName}} attempt. Tap to see your results.",
                            route = "student_long_quiz_result_screen/$_courseId/$_longQuizId/false"
                        )
                        _events.trySend(LongQuizAttemptEvent.SubmitSuccess(response))
                    },
                    onFailure = { err ->
                        _events.trySend(LongQuizAttemptEvent.SubmitError(err.toString()))
                    },
                )
            }
        }
    }

}

data class LongQuizAttemptUIState(
    val loadingMetadata: Boolean = false,
    val loadingContent: Boolean = false,
    val loadingSubmit: Boolean = false,
    val errorMessage: String? = null,
    val longQuizDetail: LongQuizResponse? = null,
    val content: List<QuizContentResponse> = emptyList(),
    val shuffledQuestions: List<QuizContentResponse> = emptyList(),
    val currentIndex: Int = 0,
    val timeRemaining: Int? = 0,
    val answers: Map<Int, LongQuizAnswer> = emptyMap(),
    val hasSubmitted: Boolean = false
){
    val loading: Boolean get() = loadingMetadata || loadingContent
}

sealed class LongQuizAnswer {
    data class OptionAnswer(val questionId: String,val optionId: String, val isCorrect: Boolean) : LongQuizAnswer()
    data class TextAnswer(val response: QuizContentResponse, val text: String) : LongQuizAnswer()
}

sealed class LongQuizAttemptEvent {
    data class NextQuestion(val index: Int) : LongQuizAttemptEvent()
    object SubmitQuiz : LongQuizAttemptEvent()
    data class SubmitSuccess(val response: AssessmentResultUploadResponse) : LongQuizAttemptEvent()
    data class SubmitError(val errorMessage: String) : LongQuizAttemptEvent()
}