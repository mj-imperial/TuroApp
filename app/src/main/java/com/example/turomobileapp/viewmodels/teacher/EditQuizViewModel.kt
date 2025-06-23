package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CreateQuizOptions
import com.example.turomobileapp.models.CreateQuizQuestions
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.QuestionResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.repositories.QuizRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EditQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])
    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(EditQuizUIState())
    val uiState: StateFlow<EditQuizUIState> = _uiState.asStateFlow()

    private val _pendingQuestions = MutableStateFlow(listOf(PendingQuestion(options = mutableListOf(PendingOption()))))
    val pendingQuestions: StateFlow<List<PendingQuestion>> = _pendingQuestions.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    init {
        viewModelScope.launch {
            launch { getQuiz() }
            launch { getQuizContent() }
        }
    }

    fun getQuiz(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMetadata = true, errorMessage = null) }

            quizRepository.getQuiz(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { quiz ->
                        _uiState.update { it.copy(
                            loadingMetadata = false,
                            quizTitle = quiz.quizName,
                            quizType = quiz.quizTypeName,
                            quizDescription = quiz.quizDescription,
                            unlockDateTime = quiz.unlockDate,
                            deadlineDateTime = quiz.deadlineDate,
                            numberOfAttempts = quiz.numberOfAttempts,
                            timeLimit = quiz.timeLimit,
                            hasAnswersShown = quiz.hasAnswersShown
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingMetadata = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getQuizContent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingContent = true, errorMessage = null) }

            quizRepository.getQuizContent(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { content ->
                        _uiState.update { it.copy(loadingContent = false, questions = content) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingContent = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun updateQuizTitle(newQuizTitle: String){
        _uiState.update { it.copy(quizTitle = newQuizTitle) }
        _isFormValid.value = isFormValid()
    }

    fun updateQuizType(newQuizType: String){
        _uiState.update { it.copy(quizType = newQuizType) }
        _isFormValid.value = isFormValid()
    }

    fun updateQuizDescription(newQuizDescription: String){
        _uiState.update { it.copy(quizDescription = newQuizDescription) }
        _isFormValid.value = isFormValid()
    }

    fun updateUnlockDateTime(newUnlockDate: LocalDateTime?){
        _uiState.update { it.copy(unlockDateTime = newUnlockDate) }
        _isFormValid.value = isFormValid()
    }

    fun updateDeadlineDateTime(newDeadlineDateTime: LocalDateTime?){
        _uiState.update { it.copy(deadlineDateTime = newDeadlineDateTime) }
        _isFormValid.value = isFormValid()
    }

    fun updateNumberOfAttempts(newNumberOfAttempts: Int){
        _uiState.update { it.copy(numberOfAttempts = newNumberOfAttempts) }
        _isFormValid.value = isFormValid()
    }

    fun updateTimeLimit(newTimeLimit: Int){
        _uiState.update { it.copy(timeLimit = newTimeLimit) }
        _isFormValid.value = isFormValid()
    }

    fun updateHasAnswersShown(newHasAnswersShown: Boolean){
        _uiState.update { it.copy(hasAnswersShown = newHasAnswersShown) }
        _isFormValid.value = isFormValid()
    }

    fun addQuestion(){
        val newList = _pendingQuestions.value.toMutableList()
        newList.add(PendingQuestion())
        _pendingQuestions.value = newList
        _isFormValid.value = isFormValid()
    }

    fun removeOldQuestion(questionId: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.filterNot { it.questionId == questionId }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun removeNewQuestion(tempQuestionId: String) {
        _pendingQuestions.update { list ->
            list.filterNot { it.tempId == tempQuestionId }
        }
        _isFormValid.value = isFormValid()
    }

    fun updateOldImage(questionId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            val compressedBytes = compressImageUriToByteArray(context, uri)
            _uiState.update { state ->
                state.copy(
                    questions = state.questions.map { q ->
                        if (q.questionId == questionId) {
                            q.copy(questionImage = compressedBytes ?: byteArrayOf())
                        } else q
                    }
                )
            }
        }
    }

    fun deleteOldImage(questionId: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId){
                        q.copy(questionImage = byteArrayOf())
                    } else q
                }
            )
        }
    }

    fun addImage(tempQuestionId: String, uri: Uri, context: Context) {
        viewModelScope.launch {
            val compressedBytes = compressImageUriToByteArray(context, uri)

            _pendingQuestions.update { list ->
                list.map { q ->
                    if (q.tempId == tempQuestionId) {
                        q.copy(tempImage = compressedBytes ?: byteArrayOf())
                    } else q
                }
            }
        }
    }

    fun removeNewImage(tempQuestionId: String){
        _pendingQuestions.update {
                list ->
            list.map { q ->
                if (q.tempId == tempQuestionId){
                    q.copy(tempImage = byteArrayOf())
                } else q
            }
        }
    }

    fun updateOldQuestionText(questionId: String, newText: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId) {
                        q.copy(questionText = newText)
                    } else q
                }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun updateNewQuestionText(tempQuestionId: String, newText: String){
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    q.copy(text = newText, options = q.options)
                } else q
            }
        }
        _isFormValid.value = isFormValid()
    }

    fun updateOldQuestionScore(questionId: String, newScore: Int){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId) {
                        q.copy(score = newScore)
                    } else q
                }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun updateNewQuestionScore(tempQuestionId: String, newScore: Int){
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    q.copy(text = q.text, options = q.options, score = newScore)
                } else q
            }
        }
        _isFormValid.value = isFormValid()
    }

    fun addOptionToOldQuestion(questionId: String) {
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { question ->
                    if (question.questionId == questionId) {
                        val updatedOptions = question.options.toMutableList().apply {
                            add(
                                QuestionResponse(
                                    optionId = UUID.randomUUID().toString(),
                                    optionText = "",
                                    isCorrectInt = 0
                                )
                            )
                        }
                        question.copy(options = updatedOptions)
                    } else question
                }
            )
        }
        _isFormValid.value = isFormValid()
    }


    fun addOptionToNewQuestion(tempQuestionId: String){
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
        _isFormValid.value = isFormValid()
    }

    fun updateOldOptionText(questionId: String, optionId: String, newText: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId) {
                        q.copy(
                            options = q.options.map { opt ->
                                if (opt.optionId == optionId)
                                    opt.copy(optionText = newText)
                                else opt
                            }
                        )
                    } else q
                }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun updateNewOptionText(
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
        _isFormValid.value = isFormValid()
    }

    fun setSingleCorrectOldOption(questionId: String, optionId: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId) {
                        q.copy(
                            options = q.options.map { opt ->
                                if (opt.optionId == optionId)
                                    opt.copy(isCorrectInt = 1)
                                else opt.copy(isCorrectInt = 0)
                            }
                        )
                    } else q
                }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun setSingleCorrectNewOption(
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
        _isFormValid.value = isFormValid()
    }

    fun removeOptionFromOldQuestion(questionId: String, optionId: String){
        _uiState.update { state ->
            state.copy(
                questions = state.questions.map { q ->
                    if (q.questionId == questionId) {
                        val filteredOpts = q.options.filterNot { it.optionId == optionId }
                        q.copy(options = filteredOpts)
                    } else q
                }
            )
        }
        _isFormValid.value = isFormValid()
    }

    fun removeOptionFromNewQuestion(tempQuestionId: String, tempOptionId: String) {
        _pendingQuestions.update { list ->
            list.map { q ->
                if (q.tempId == tempQuestionId) {
                    val filteredOpts = q.options.filterNot { it.tempId == tempOptionId }
                    q.copy(options = filteredOpts.toMutableList())
                } else q
            }
        }
        _isFormValid.value = isFormValid()
    }

    fun isFormValid(): Boolean {
        val state = uiState.value

        if (state.quizTitle.isBlank()) return false

        if (state.questions.isEmpty()) return false

        state.questions.forEach { question ->
            if (question.questionText.isEmpty()) return false

            if (question.options.isEmpty()) return false

            var foundCorrect = 0
            question.options.forEach { opt ->
                if (opt.optionText.isEmpty()) return false
                if (opt.isCorrect) foundCorrect += 1
            }
            if (foundCorrect != 1) return false

            if (question.score <= 0) return false
        }

        if (pendingQuestions.value.isNotEmpty()){
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
        }

        val unlock = state.unlockDateTime
        if (unlock == null) return false

        val deadline = state.deadlineDateTime

        deadline?.let { if (it.isBefore(unlock)) return false }
        return true
    }

    fun updateQuiz(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingUpdate = true, errorMessage = null) }

            val state = _uiState.value
            val newQuestions = _pendingQuestions.value

            val totalScore = (state.questions.sumOf { it.score } + newQuestions.sumOf { it.score })

            val existingQuestions = state.questions.map { question ->
                val options = question.options.map { option ->
                    CreateQuizOptions(
                        optionId = option.optionId,
                        optionText = option.optionText,
                        isCorrect = option.isCorrect
                    )
                }

                val encodedImage = question.questionImage?.let {
                    if (!it.isEmpty()) {
                        Base64.encodeToString(question.questionImage, Base64.NO_WRAP)
                    } else null
                }

                CreateQuizQuestions(
                    questionId = question.questionId,
                    questionText = question.questionText,
                    questionType = if (question.options.size > 1) "MULTIPLE_CHOICE" else "SHORT_ANSWER",
                    questionImage = encodedImage,
                    score = question.score,
                    options = options
                )
            }

            val formattedQuestions = newQuestions.map { question ->
                val questionType = if (question.options.size > 1) "MULTIPLE_CHOICE" else "SHORT_ANSWER"

                val options = question.options.map { option ->
                    CreateQuizOptions(
                        optionText = option.text,
                        isCorrect = option.isCorrect
                    )
                }

                val encodedImage = question.tempImage?.let {
                    if (!it.isEmpty()) {
                        Base64.encodeToString(question.tempImage, Base64.NO_WRAP)
                    } else null
                }

                CreateQuizQuestions(
                    questionText = question.text,
                    questionType = questionType,
                    questionImage = encodedImage,
                    score = question.score,
                    options = options
                )
            }

            val allQuestions = existingQuestions + formattedQuestions

            if (uiState.value.quizType == "SCREENING"){
                _uiState.update { it.copy(quizType = "SCREENING_EXAM") }
            }else{
                uiState.value.quizType
            }

            val quizRequest = CreateQuizRequest(
                quizTitle = uiState.value.quizTitle,
                quizType = uiState.value.quizType,
                quizDescription = uiState.value.quizDescription,
                numberOfAttempts = uiState.value.numberOfAttempts,
                timeLimitSeconds = uiState.value.timeLimit,
                hasAnswerShown = uiState.value.hasAnswersShown,
                questions = allQuestions,
                unlockDateTime = uiState.value.unlockDateTime,
                deadlineDateTime = uiState.value.deadlineDateTime,
                numberOfQuestions = allQuestions.size,
                overallPoints = totalScore
            )

            quizRepository.updateQuiz(_activityId, _moduleId, quizRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loadingUpdate = false, editQuizStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "QUIZ UPDATED",
                            notificationText = "Quiz successfully updated.",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingUpdate = false, errorMessage = err, editQuizStatus = null) }
                    }
                )
            }
        }
    }

    fun clearEditStatus(){
        _uiState.update { it.copy(editQuizStatus = null) }
    }

    private fun compressImageUriToByteArray(
        context: Context,
        uri: Uri,
        quality: Int = 70,
        maxWidth: Int = 600,
        maxHeight: Int = 600
    ): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val ratio: Float = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val (newWidth, newHeight) = if (ratio > 1) {
                maxWidth to (maxWidth / ratio).toInt()
            } else {
                (maxHeight * ratio).toInt() to maxHeight
            }

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class EditQuizUIState(
    val loadingMetadata: Boolean = false,
    val loadingContent: Boolean = false,
    val loadingUpdate: Boolean = false,
    val errorMessage: String? = null,
    val editQuizStatus: Result<Unit>? = null,
    val quizTitle: String = "",
    val quizType: String = "",
    val quizDescription: String = "",
    val unlockDateTime: LocalDateTime? = null,
    val deadlineDateTime: LocalDateTime? = null,
    val numberOfAttempts: Int = 1,
    val timeLimit: Int = 0,
    val hasAnswersShown: Boolean = false,
    val questions: List<QuizContentResponse> = emptyList()
){
    val loading: Boolean get() = loadingMetadata || loadingContent
}