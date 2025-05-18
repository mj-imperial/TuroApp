package com.example.turomobileapp.viewmodels.shared

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.UserRepository
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val session: SessionManager
): ViewModel() {

    private val _events = MutableSharedFlow<ProfileUiEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(
        ProfileUIState(currentPicUrl = "")
    )
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            session.profilePicUrl
                .filter { it!!.isNotBlank() }
                .collect { url ->
                    _uiState.update { it.copy(currentPicUrl = url.toString()) }
                }
        }
    }

    fun onEditClick() {
        viewModelScope.launch {
            _events.emit(ProfileUiEvent.RequestImagePermission)
        }
    }

    fun onImagePicked(uri: Uri) {
        _uiState.update { it.copy(pickedImageUri = uri, errorMessage = null) }
    }

    fun uploadImage(imageBytes: ByteArray, mimeType: String) {
        val uri = _uiState.value.pickedImageUri ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            userRepository.updateUserProfilePic(
                userId = session.userId.value.toString(),
                imageBytes = imageBytes,
                mimeType = mimeType
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { currentPic ->
                        _uiState.update { it.copy(loading = false, pickedImageUri = null, currentPicUrl = currentPic.profilePicUrl.orEmpty()) }
                        session.updateProfilePicUrl(currentPic.profilePicUrl.toString())
                    },
                    onFailure = { err ->
                        _uiState.update {it.copy(loading = false, errorMessage = err)}
                    },
                )
            }
        }
    }

    fun clearPickedImageUri() {
        _uiState.update { it.copy(pickedImageUri = null) }
    }
}

data class ProfileUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val currentPicUrl: String = "",
    val pickedImageUri: Uri? = null,
)

sealed class ProfileUiEvent {
    object RequestImagePermission : ProfileUiEvent()
}