package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.ScreeningExamContent
import com.example.turomobileapp.models.ScreeningExamOption
import com.example.turomobileapp.models.ScreeningExamQuestion
import com.example.turomobileapp.models.ScreeningExamResponse
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

//TODO accept skipped answers
@HiltViewModel
class ScreeningExamAttemptViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val notificationService: TuroNotificationService,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _screeningExamId: String = checkNotNull(savedStateHandle["screeningExamId"])

    private val _uiState = MutableStateFlow(ScreeningExamAttemptUIState())
    val uiState: StateFlow<ScreeningExamAttemptUIState> = _uiState.asStateFlow()

    private val _events = Channel<ScreeningExamAttemptEvent>(Channel.BUFFERED)
    val events: Flow<ScreeningExamAttemptEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            launch { loadMetadata() }
            launch { loadQuizContent() }
        }
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMetadata = true,errorMessage = null) }

            val longQuiz = quizRepository.getScreeningExam(_screeningExamId).first()
            handleResult(
                result = longQuiz,
                onSuccess = { resp ->
                    _uiState.update { it.copy(loadingMetadata = false, screeningExamDetail = resp) }
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

            quizRepository.getScreeningExamContent(_screeningExamId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        val allQuestions = resp.flatMap { it.topics }.flatMap { it.questions }
                        val questionCount = (uiState.value.screeningExamDetail?.numberOfQuestions
                            ?: allQuestions.size).coerceAtMost(allQuestions.size)

                        val selectedQuestions = allQuestions.shuffled().take(questionCount)

                        _uiState.update {
                            it.copy(
                                loadingContent = false,
                                content = resp,
                                selectedQuestions = selectedQuestions,
                                currentIndex = 0,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingContent = false, errorMessage = err) }
                    }
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
            _events.send(ScreeningExamAttemptEvent.SubmitQuiz)
        }
    }

    fun getQuestionByFlatIndex(index: Int): ScreeningExamQuestion? {
        val flatList = uiState.value.content.flatMap { it.topics }.flatMap { it.questions }
        return flatList.getOrNull(index)
    }

    fun onOptionSelected(option: ScreeningExamOption) {
        val question = getQuestionByFlatIndex(_uiState.value.currentIndex)
        _uiState.update { st ->
            st.copy(
                answers = st.answers + (st.currentIndex to ScreeningExamAnswer.OptionAnswer(
                    questionId = question!!.questionId,
                    optionId = option.optionId,
                    isCorrect = option.isCorrect
                ))
            )
        }
    }

    fun onShortAnswerEntered(text: String) {
        val question = getQuestionByFlatIndex(uiState.value.currentIndex)
        _uiState.update { state ->
            state.copy(
                answers = state.answers + (state.currentIndex to ScreeningExamAnswer.TextAnswer(question, text))
            )
        }
    }

    fun onNextClicked() {
        _uiState.update {
            val next = (it.currentIndex + 1).coerceAtMost(it.selectedQuestions.size - 1)
            it.copy(currentIndex = next)
        }
    }

    fun onSubmitClicked() {
        if (_uiState.value.hasSubmitted) return

        viewModelScope.launch {
            _uiState.update { it.copy(loadingSubmit = true, errorMessage = null, hasSubmitted = true) }

            _events.send(ScreeningExamAttemptEvent.SubmitQuiz)

            val selectedQuestions = uiState.value.selectedQuestions

            val answersList = selectedQuestions.map { question ->
                val answer = uiState.value.answers.values.firstOrNull {
                    when (it) {
                        is ScreeningExamAnswer.OptionAnswer -> it.questionId == question.questionId
                        is ScreeningExamAnswer.TextAnswer -> it.response?.questionId == question.questionId
                    }
                }

                when (answer) {
                    is ScreeningExamAnswer.OptionAnswer -> AnswerUploadRequest(
                        questionId = answer.questionId,
                        optionId = answer.optionId,
                        isCorrect = if (answer.isCorrect) 1 else 0
                    )
                    is ScreeningExamAnswer.TextAnswer -> {
                        val match = question.options.firstOrNull { opt ->
                            opt.optionText.trim().uppercase() == answer.text.trim().uppercase()
                        }

                        AnswerUploadRequest(
                            questionId = question.questionId,
                            optionId = match?.optionId ?: "",
                            isCorrect = if (match?.isCorrect == true) 1 else 0
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
                    selectedQuestions.firstOrNull { q -> q.questionId == answer.questionId }?.score ?: 0
                } else 0
            }

            val maxScore = selectedQuestions.sumOf { it.score }
            val scorePercentage: Double = if (maxScore > 0) {
                (score.toDouble() / maxScore.toDouble()) * 100.0
            } else 0.0

            val studentId: String = sessionManager.userId.filterNotNull().first()

            assessmentResultRepository.saveScreeningExamResult(
                studentId = studentId,
                screeningExamId = _screeningExamId,
                scorePercentage = scorePercentage,
                earnedPoints = score,
                answers = answersList
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        notificationService.showNotification(
                            notificationTitle = "Quiz Submitted",
                            notificationText = "You have completed the screening exam. Tap to see your results.",
                            //TODO: Replace with correct result screen route
                            route = ""
                        )
                        _events.trySend(ScreeningExamAttemptEvent.SubmitSuccess(response))
                    },
                    onFailure = { err ->
                        _events.trySend(ScreeningExamAttemptEvent.SubmitError(err.toString()))
                    },
                )
            }
        }
    }
}

data class ScreeningExamAttemptUIState(
    val loadingMetadata: Boolean = false,
    val loadingContent: Boolean = false,
    val loadingSubmit: Boolean = false,
    val errorMessage: String? = null,
    val screeningExamDetail: ScreeningExamResponse? = null,
    val content: List<ScreeningExamContent> = emptyList(),
    val currentIndex: Int = 0,
    val timeRemaining: Int? = 0,
    val answers: Map<Int, ScreeningExamAnswer> = emptyMap(),
    val hasSubmitted: Boolean = false,
    val selectedQuestions: List<ScreeningExamQuestion> = emptyList()
){
    val loading: Boolean get() = loadingMetadata || loadingContent
}

sealed class ScreeningExamAnswer {
    data class OptionAnswer(val questionId: String,val optionId: String, val isCorrect: Boolean) : ScreeningExamAnswer()
    data class TextAnswer(val response: ScreeningExamQuestion?, val text: String) : ScreeningExamAnswer()
}

sealed class ScreeningExamAttemptEvent {
    data class NextQuestion(val index: Int) : ScreeningExamAttemptEvent()
    object SubmitQuiz : ScreeningExamAttemptEvent()
    data class SubmitSuccess(val response: AssessmentResultUploadResponse) : ScreeningExamAttemptEvent()
    data class SubmitError(val errorMessage: String) : ScreeningExamAttemptEvent()
}