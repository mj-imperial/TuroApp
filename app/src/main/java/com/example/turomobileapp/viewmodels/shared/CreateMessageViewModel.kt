package com.example.turomobileapp.viewmodels.shared

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.InboxCourseUserListResponse
import com.example.turomobileapp.repositories.MessageRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
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
class CreateMessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sessionManager: SessionManager,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _uiState = MutableStateFlow(CreateMessageUIState())
    val uiState: StateFlow<CreateMessageUIState> = _uiState.asStateFlow()

    private val _selectedTempRecipients = mutableStateListOf<MessageRecipient>()
    val selectedTempRecipients: List<MessageRecipient> get() = _selectedTempRecipients

    init {
        getCoursesRelatedToUser()
    }

    fun getCoursesRelatedToUser(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()
            val role: String = sessionManager.role.filterNotNull().first()

            if (role == "STUDENT"){
                messageRepository.getUsersForStudent(userId).collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { resp ->
                            _uiState.update { it.copy(loading = false, emailCourses = resp) }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        }
                    )
                }
            }else{
                messageRepository.getUsersForTeacher(userId).collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = { resp ->
                            _uiState.update { it.copy(loading = false, emailCourses = resp) }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        }
                    )
                }
            }
        }
    }

    fun updateSelectedCourse(newCourse: String){
        _uiState.update { it.copy(selectedCourse = newCourse) }
    }

    fun initTempRecipientsIfEmpty() {
        if (_selectedTempRecipients.isEmpty()) {
            _selectedTempRecipients.addAll(_uiState.value.selectedRecipients)
        }
    }

    fun toggleTempRecipient(recipient: MessageRecipient) {
        if (_selectedTempRecipients.any { it.userId == recipient.userId }) {
            _selectedTempRecipients.removeAll { it.userId == recipient.userId }
        } else {
            _selectedTempRecipients.add(recipient)
        }
    }

    fun saveSelectedRecipients() {
        _uiState.update { it.copy(selectedRecipients = _selectedTempRecipients.toList()) }
    }

    fun removeRecipient(userId: String){
        _uiState.update {
            it.copy(selectedRecipients = it.selectedRecipients.filterNot { recipient -> recipient.userId == userId })
        }
        _selectedTempRecipients.removeIf { it.userId == userId }
    }

    fun updateSubject(newSubject: String){
        _uiState.update { it.copy(subject = newSubject) }
    }

    fun updateBody(newBody: String){
        _uiState.update { it.copy(body = newBody) }
    }

    fun sendMessage(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }


            val userId: String = sessionManager.userId.filterNotNull().first()
            val state = _uiState.value

            val messageRequest = CreateMessageRequest(
                senderId = userId,
                recipientIds = state.selectedRecipients.map { it.userId },
                subject = state.subject,
                body = state.body
            )

            if (state.selectedRecipients.isEmpty()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please select at least one recipient.") }
                return@launch
            }

            if (state.body.isEmpty()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please enter a message.") }
                return@launch
            }

            messageRepository.sendMessage(userId, messageRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, sendMessageStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Message Sent",
                            notificationText = "Message successfully sent!",
                            route = "inbox_screen"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, sendMessageStatus = null) }
                    }
                )
            }
        }
    }

    fun clearSendMessageStatus(){
        _uiState.update { it.copy(
            loading = false,
            subject = "",
            body = "",
            selectedCourse = "",
            selectedRecipients = emptyList(),
            sendMessageStatus = null
        ) }
        _selectedTempRecipients.clear()
    }
}

data class CreateMessageUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val sendMessageStatus: Result<Unit>? = null,
    val emailCourses: List<InboxCourseUserListResponse> = emptyList(),
    val selectedCourse: String = "",
    val selectedRecipients: List<MessageRecipient> = emptyList(),
    val subject: String = "",
    val body: String = ""
)

data class MessageRecipient(
    val userId: String,
    val name: String,
    val email: String,
    val profilePic: String? = null
)