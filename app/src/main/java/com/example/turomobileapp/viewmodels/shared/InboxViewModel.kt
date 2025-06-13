package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.InboxItem
import com.example.turomobileapp.repositories.MessageRepository
import com.example.turomobileapp.repositories.Result
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
class InboxViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(InboxUIState())
    val uiState: StateFlow<InboxUIState> = _uiState.asStateFlow()

    init {
        getInboxes()
    }

    fun getInboxes(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()

            messageRepository.getInboxMessages(userId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, inboxList = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun deleteInbox(inboxId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()

            messageRepository.deleteInboxMessage(inboxId, userId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, deleteInboxStatus = Result.Success(Unit)) }
                        getInboxes()
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun markMessageAsRead(messageId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val userId: String = sessionManager.userId.filterNotNull().first()

            messageRepository.markMessageAsRead(userId, messageId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, markMessageStatus = Result.Success(Unit)) }
                        getInboxes()
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class InboxUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val inboxList: List<InboxItem> = emptyList(),
    val deleteInboxStatus: Result<Unit>? = null,
    val markMessageStatus: Result<Unit>? = null
)