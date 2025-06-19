package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.InboxDetail
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
class InboxDetailViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val notificationService: TuroNotificationService,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _inboxId: String = checkNotNull(savedStateHandle["inboxId"])

    private val _uiState = MutableStateFlow(InboxDetailUIState())
    val uiState: StateFlow<InboxDetailUIState> = _uiState.asStateFlow()

    init {
        getInbox()
    }

    fun getInbox(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()

            messageRepository.getInboxDetails(_inboxId, userId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            messages = resp.messages,
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun setReplyInfo(recipientId: String, recipientName: String, recipientPic: ByteArray){
        _uiState.update { it.copy(
            recipientId = recipientId,
            recipientName = recipientName,
            recipientPic = recipientPic
        ) }
    }

    fun updateSubject(newSubject: String){
        _uiState.update { it.copy(subject = newSubject) }
    }

    fun updateBody(newBody: String){
        _uiState.update { it.copy(body = newBody) }
    }

    fun sendReply(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()
            val state = _uiState.value

            val messageRequest = CreateMessageRequest(
                senderId = userId,
                recipientIds = listOf(state.recipientId),
                subject = state.subject,
                body = state.body
            )

            if (state.recipientId.isEmpty()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please select at least one recipient.") }
                return@launch
            }

            if (state.body.isEmpty()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please enter a message.") }
                return@launch
            }

            messageRepository.sendReply(userId, messageRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, replyMessageStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Reply Sent",
                            notificationText = "Reply successfully sent!",
                            route = "inbox_detail_screen/$_inboxId"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, replyMessageStatus = null) }
                    }
                )
            }
        }
    }

    fun clearReplyMessage(){
        _uiState.update { it.copy(
            loading = false,
            subject = "",
            body = "",
            recipientId = "",
            recipientPic = byteArrayOf(),
            recipientName = "",
            replyMessageStatus = null
        ) }
    }
}

data class InboxDetailUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<InboxDetail> = emptyList(),
    val recipientId: String = "",
    val recipientName: String = "",
    val recipientPic: ByteArray = byteArrayOf(),
    val subject: String = "",
    val body: String = "",
    val replyMessageStatus: Result<Unit>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as InboxDetailUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (messages!=other.messages) return false
        if (recipientId!=other.recipientId) return false
        if (recipientName!=other.recipientName) return false
        if (!recipientPic.contentEquals(other.recipientPic)) return false
        if (subject!=other.subject) return false
        if (body!=other.body) return false
        if (replyMessageStatus!=other.replyMessageStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + messages.hashCode()
        result = 31 * result + recipientId.hashCode()
        result = 31 * result + recipientName.hashCode()
        result = 31 * result + recipientPic.contentHashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + (replyMessageStatus?.hashCode() ?: 0)
        return result
    }
}