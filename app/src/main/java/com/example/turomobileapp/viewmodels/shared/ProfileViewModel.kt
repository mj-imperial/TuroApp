package com.example.turoapp.viewmodels.shared

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turoapp.models.ProfileUiState
import com.example.turoapp.repositories.Result
import com.example.turoapp.repositories.UserRepository
import com.example.turoapp.viewmodels.higherorderfunctions.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val context: Context
) : ViewModel() {

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)

            userRepository.getUserById(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            user = user
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            errorMessage = error
                        )
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }
                )
            }
        }
    }

    fun updateSelectedImageUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun updateProfilePicture() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, profilePicUpdateResult = null)

            val imageUri = _uiState.value.selectedImageUri
            if (imageUri == null) {
                val errorMessage = "Image is required"
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = errorMessage,
                    profilePicUpdateResult = com.example.turoapp.repositories.Result.Failure(Exception(errorMessage))
                )
                return@launch
            }

            try {
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"
                    userRepository.updateUserProfilePic(_userId.value, inputStream, mimeType).collect { result ->
                        _uiState.value = _uiState.value.copy(profilePicUpdateResult = result, loading = false)
                    }
                } ?: run { // Handle case where openInputStream returns null
                    val errorMessage = "Failed to open image stream"
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        errorMessage = errorMessage,
                        profilePicUpdateResult = Result.Failure(Exception(errorMessage))
                    )
                    return@launch
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = e.message,
                    profilePicUpdateResult = Result.Failure(e)
                )
            }
        }
    }

    fun resetProfilePicUpdateResult() {
        _uiState.value = _uiState.value.copy(profilePicUpdateResult = null)
    }
}