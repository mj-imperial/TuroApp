package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CreateQuizOptions
import com.example.turomobileapp.models.CreateQuizQuestions
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.repositories.QuizRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(CreateQuizUIState())
    val uiState: StateFlow<CreateQuizUIState> = _uiState.asStateFlow()

    private val _pendingQuestions = MutableStateFlow(
        List(5) { PendingQuestion(options = mutableListOf(PendingOption())) }
    )
    val pendingQuestions: StateFlow<List<PendingQuestion>> = _pendingQuestions.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    fun updateQuizName(newQuizName: String){
        _uiState.update { it.copy(quizTitle = newQuizName) }
    }

    fun updateQuizType(newQuizType: String){
        if (newQuizType == "SCREENING"){
            _uiState.update { it.copy(quizType = "SCREENING_EXAM") }
        }else{
            _uiState.update { it.copy(quizType = newQuizType) }
        }
    }

    fun updateQuizDescription(newQuizDescription: String){
        _uiState.update { it.copy(quizDescription = newQuizDescription) }
    }

    fun updateNumberOfAttempts(newNumberOfAttempts: Int){
        _uiState.update { it.copy(numberOfAttempts = newNumberOfAttempts) }
    }

    fun updateTimeLimit(newTimeLimit: Int){
        _uiState.update { it.copy(timeLimit = newTimeLimit) }
    }

    fun updateShowAnswers(newShowAnswers: Boolean){
        _uiState.update { it.copy(hasAnswersShown = newShowAnswers) }
    }

    fun updateUnlockDateTime(newDateTime: LocalDateTime?) {
        _uiState.update { it.copy(unlockDateTime = newDateTime) }
    }

    fun updateDeadlineDateTime(newDateTime: LocalDateTime?) {
        _uiState.update { it.copy(deadlineDateTime = newDateTime) }
    }


    fun addQuestion() {
        val newList = _pendingQuestions.value.toMutableList()
        newList.add(PendingQuestion())
        _pendingQuestions.value = newList
    }

    fun removeQuestion(tempQuestionId: String) {
        _pendingQuestions.update { list ->
            list.filterNot { it.tempId == tempQuestionId }
        }
    }

    fun updateQuestionText(tempQuestionId: String, newText: String) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    q.copy(text = newText, options = q.options)
                } else q
            }
        }
    }

    fun updateQuestionScore(tempQuestionId: String, newScore: Int) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    q.copy(text = q.text, options = q.options, score = newScore)
                } else q
            }
        }
    }

    fun addOption(tempQuestionId: String) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    val newOptions = q.options.toMutableList().apply {
                        add(PendingOption())
                    }
                    q.copy(options = newOptions)
                } else q
            }
        }
    }

    fun updateOptionText(
        tempQuestionId: String,
        tempOptionId: String,
        newText: String
    ) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    val updatedOpts = q.options.map { opt ->
                        if (opt.tempId == tempOptionId) {
                            opt.copy(text = newText)
                        } else opt
                    }
                    q.copy(options = updatedOpts.toMutableList())
                } else q
            }
        }
    }

    fun setSingleCorrectOption(
        tempQuestionId: String,
        tempOptionIdToMark: String
    ) {
        _pendingQuestions.update { list ->
            list.map { question ->
                if (question.tempId == tempQuestionId) {
                    val newOptions = question.options.map { opt ->
                        if (opt.tempId == tempOptionIdToMark) {
                            opt.copy(isCorrect = true)
                        } else {
                            opt.copy(isCorrect = false)
                        }
                    }.toMutableList()
                    question.copy(options = newOptions)
                } else {
                    question
                }
            }
        }
    }

    fun removeOption(tempQuestionId: String, tempOptionId: String) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    val filteredOpts = q.options.filterNot { it.tempId == tempOptionId }
                    q.copy(options = filteredOpts.toMutableList())
                } else q
            }
        }
    }

    fun isFormValid(): Boolean {
        val state = uiState.value

        if (state.quizTitle.isBlank()) return false

        if (pendingQuestions.value.isEmpty()) return false

        pendingQuestions.value.forEach { question ->
            if (question.text.isBlank()) return false

            if (question.options.isEmpty()) return false

            var foundCorrect = 0
            question.options.forEach { opt ->
                if (opt.text.isBlank()) return false
                if (opt.isCorrect) foundCorrect += 1
            }
            if (foundCorrect != 1) return false

            if (question.score <= 0) return false
        }
        return true
    }


    fun saveQuiz(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val totalScore = _pendingQuestions.value.sumOf { it.score }

            val questions = _pendingQuestions.value.map { question ->
                val questionType = if (question.options.size > 1) "MULTIPLE_CHOICE" else "SHORT_ANSWER"

                val options = question.options.map { option ->
                    CreateQuizOptions(
                        optionText = option.text,
                        isCorrect = option.isCorrect
                    )
                }

                CreateQuizQuestions(
                    questionText = question.text,
                    questionType = questionType,
                    score = question.score,
                    options = options
                )
            }

            val quizRequest = CreateQuizRequest(
                quizTitle = uiState.value.quizTitle,
                quizType = uiState.value.quizType,
                quizDescription = uiState.value.quizDescription,
                numberOfAttempts = uiState.value.numberOfAttempts,
                timeLimitSeconds = uiState.value.timeLimit,
                hasAnswerShown = uiState.value.hasAnswersShown,
                questions = questions,
                unlockDateTime = uiState.value.unlockDateTime,
                deadlineDateTime = uiState.value.deadlineDateTime,
                numberOfQuestions = questions.size,
                overallPoints = totalScore
            )

            quizRepository.createQuiz(_moduleId, quizRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, createQuizStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Quiz Creation",
                            notificationText = "You have successfully created a ${_uiState.value.quizType} QUIZ",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err,createQuizStatus = null) }
                    },
                )
            }
        }
    }

    fun clearCreateQuizStatus(){
        _uiState.update { it.copy(createQuizStatus = null) }
    }
}

data class CreateQuizUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val createQuizStatus: Result<Unit>? = null,
    val quizTitle: String = "",
    val quizType: String = "SHORT",
    val quizDescription: String = "",
    val unlockDateTime: LocalDateTime? = null,
    val deadlineDateTime: LocalDateTime? = null,
    val numberOfAttempts: Int = 1,
    val timeLimit: Int = 0, //in seconds
    val hasAnswersShown: Boolean = false,
    val questions: List<CreateQuizQuestions> = emptyList()
)

data class PendingOption(
    val tempId: String = UUID.randomUUID().toString(),
    var text: String = "",
    var isCorrect: Boolean = false
)

data class PendingQuestion(
    val tempId: String = UUID.randomUUID().toString(),
    var text: String = "",
    val options: MutableList<PendingOption> = mutableListOf(),
    var score: Int = 1
)

