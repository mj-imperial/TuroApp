package com.example.turomobileapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.enums.ResetStep
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.turomobileapp.repositories.Result

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    init {
        checkIfRequiresPasswordChange()
    }

    fun checkIfRequiresPasswordChange(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            userRepository.getDefaultPasswordChangeStatus(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { status ->
                        _uiState.update { it.copy(
                            requiresPasswordChangeStatus = status,
                            loading = false,
                            resetStep = if (status)
                                    ResetStep.CODE_INPUT
                                else
                                    ResetStep.EMAIL_INPUT)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(errorMessage = error, loading = false) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }

    fun updateRequiresPasswordChangeStatus(defaultPasswordChangeStatus: Boolean) {
        _uiState.update {  it.copy(requiresPasswordChangeStatus = defaultPasswordChangeStatus) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun updateVerificationCode(code: String) {
        _uiState.update { it.copy(verificationCode = code) }
    }

    private fun validatePassword(): Boolean {
        return _uiState.value.newPassword.isNotBlank() &&
                _uiState.value.newPassword == _uiState.value.confirmPassword &&
                _uiState.value.newPassword.length >= 8 &&
                _uiState.value.newPassword.any { it.isDigit() } &&
                _uiState.value.newPassword.any { it.isUpperCase() }
    }

    private fun getPasswordValidationError(): String? {
        val pwd     = _uiState.value.newPassword
        val confirm = _uiState.value.confirmPassword

        return when {
            pwd.isBlank() -> "Password cannot be blank."
            pwd != confirm -> "Passwords do not match."
            pwd.length < 8 -> "Password must be at least 8 characters long."
            !pwd.any { it.isDigit() } -> "Password must contain at least one digit."
            !pwd.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter."
            else -> null
        }
    }

    fun resetPasswordChangeResult() {
        _uiState.value = _uiState.value.copy(passwordChangeResult = null)
    }

    fun getEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            userRepository.getUserById(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { user ->
                        user?.let {
                            _uiState.value = _uiState.value.copy(email = it.email, loading = false)
                        } ?: run {
                            _uiState.value = _uiState.value.copy(errorMessage = "User  not found", loading = false)
                        }
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

    fun sendRequestCode(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val emailParam = _uiState.value.email.takeIf { it.isNotBlank() }
                ?: return@launch _uiState.update {
                    it.copy(
                        loading      = false,
                        errorMessage = "Please enter your email"
                    )
                }

            userRepository.requestPasswordReset(_userId.value, emailParam.toString()).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, resetStep = ResetStep.CODE_INPUT) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(loading = false, errorMessage = error) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }

    fun verifyResetCode(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            if (state.verificationCode.isBlank()){
                _uiState.update { it.copy(errorMessage = "Please enter the code") }
                return@launch
            }

            userRepository.verifyPasswordResetCode(state.email, state.verificationCode).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, resetStep = ResetStep.PASSWORD_INPUT) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(loading = false, errorMessage = error) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            if (!validatePassword()){
                _uiState.update { it.copy(errorMessage = getPasswordValidationError()) }
                return@launch
            }

            userRepository.resetPassword(state.email, state.oldPassword, state.newPassword).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(passwordChangeResult = Result.Success(Unit), resetStep = ResetStep.DONE, loading = false) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(errorMessage = error, loading = false, passwordChangeResult = result) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }
}

data class ChangePasswordUiState(
    val email: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val verificationCode: String = "",
    val requiresPasswordChangeStatus: Boolean = false,
    val passwordChangeResult: Result<Unit>? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val resetStep: ResetStep = ResetStep.LOADING
)