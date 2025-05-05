package com.example.turoapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turoapp.enums.ResetStep
import com.example.turoapp.models.ChangePasswordUiState
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
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    init {
        checkIfRequiresPasswordChange()
    }

    fun checkIfRequiresPasswordChange(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            userRepository.getDefaultPasswordChangeStatus(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { requiresPasswordChange ->
                        _uiState.value = _uiState.value.copy(requiresPasswordChangeStatus = requiresPasswordChange, loading = false)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(errorMessage = error, loading = false)
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    fun updateRequiresPasswordChangeStatus(defaultPasswordChangeStatus: Boolean) {
        _uiState.value = _uiState.value.copy(requiresPasswordChangeStatus = defaultPasswordChangeStatus)
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
    }

    fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password)
    }

    fun updateVerificationCode(code: String) {
        _uiState.value = _uiState.value.copy(verificationCode = code)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun getEmail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            userRepository.getUserById(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(email = user.email, loading = false)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(errorMessage = error, loading = false)
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    fun requestPasswordReset() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            getEmail()
            userRepository.requestPasswordReset(_uiState.value.email).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(resetStep = ResetStep.CODE_INPUT, loading = false)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(errorMessage = error, loading = false)
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    fun verifyCodeAndSetPassword() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, passwordChangeResult = null)

            if (_uiState.value.verificationCode.isBlank()) {
                val errorMessage = "Verification code is required"
                _uiState.value = _uiState.value.copy(
                    errorMessage = errorMessage,
                    loading = false,
                    passwordChangeResult = Result.Failure(Exception(errorMessage))
                )
                return@launch
            }

            if (!validatePassword()) {
                val errorMessage = "Passwords do not match or are invalid"
                _uiState.value = _uiState.value.copy(
                    errorMessage = errorMessage,
                    loading = false,
                    passwordChangeResult = Result.Failure(Exception(errorMessage))
                )
                return@launch
            }

            userRepository.verifyPasswordResetCode(
                _userId.value,
                _uiState.value.verificationCode,
                _uiState.value.newPassword
            ).collect { result ->
                _uiState.value = _uiState.value.copy(passwordChangeResult = result, loading = false)
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(resetStep = ResetStep.DONE)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(errorMessage = error)
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    private fun validatePassword(): Boolean {
        return _uiState.value.newPassword.isNotBlank() &&
                _uiState.value.newPassword == _uiState.value.confirmPassword &&
                _uiState.value.newPassword.length >= 8
    }

    fun resetPasswordChangeResult() {
        _uiState.value = _uiState.value.copy(passwordChangeResult = null)
    }
}