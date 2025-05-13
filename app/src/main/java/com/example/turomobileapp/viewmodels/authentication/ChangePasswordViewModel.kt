package com.example.turomobileapp.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.enums.ResetStep
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.User
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    //Read the nav‐arg into a simple Boolean
    private val _requiresChange = MutableStateFlow<Boolean>(savedStateHandle.get<Boolean>("requiresChange") != false)
    val requiresChange: StateFlow<Boolean> = _requiresChange.asStateFlow()

    private val _initialEmail = MutableStateFlow<String>(savedStateHandle["email"] ?: "")

    // Seed UI state’s first screen
    private val _uiState = MutableStateFlow(
        ChangePasswordUiState(
            resetStep = if (_requiresChange.value) {
                ResetStep.PASSWORD_INPUT    // old→new→confirm
            } else {
                ResetStep.EMAIL_INPUT       // email→code→new→confirm
            }
        )
    )
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    init {
        if (_requiresChange.value) {
            _uiState.update { it.copy(email = _initialEmail.value) }
        }
    }

    fun updateEmail(email: String){
        _uiState.update { it.copy(email = email) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateOldPassword(password: String) {
        _uiState.update { it.copy(oldPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun updateVerificationCode(code: String) {
        _uiState.update { it.copy(verificationCode = code) }
    }

    private fun getPasswordValidationError(): String? {
        val pwd     = _uiState.value.newPassword
        val confirm = _uiState.value.confirmPassword
        return when {
            pwd.isBlank()      -> "Password cannot be blank."
            pwd != confirm     -> "Passwords do not match."
            pwd.length < 8     -> "Password must be at least 8 characters long."
            !pwd.any { it.isDigit() }    -> "Password must contain at least one digit."
            !pwd.any { it.isUpperCase() }-> "Password must contain at least one uppercase letter."
            else -> null
        }
    }

    fun sendRequestCode() {
        // Only used when resetStep == EMAIL_INPUT
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            val email = _uiState.value.email
            if (email.isBlank()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please enter your email") }
                return@launch
            }
            //changed to just email since its what the user inputs
            userRepository.requestPasswordReset(email)
                .collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = {
                            _uiState.update { it.copy( loading = false, resetStep = ResetStep.CODE_INPUT) }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        },
                        onLoading = {
                            _uiState.update { it.copy(loading = true) }
                        }
                    )
                }
        }
    }

    fun verifyResetCode() {
        // Only used when resetStep == CODE_INPUT
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            val code = _uiState.value.verificationCode
            if (code.isBlank()) {
                _uiState.update { it.copy(loading = false, errorMessage = "Please enter the code") }
                return@launch
            }
            userRepository.verifyPasswordResetCode(_uiState.value.email, code)
                .collect { result ->
                    handleResult(
                        result = result,
                        onSuccess = {
                            _uiState.update { it.copy(loading = false, resetStep = ResetStep.PASSWORD_INPUT) }
                        },
                        onFailure = { err ->
                            _uiState.update { it.copy(loading = false, errorMessage = err) }
                        },
                        onLoading = {
                            _uiState.update { it.copy(loading = true) }
                        }
                    )
                }
        }
    }

    fun changePassword() {
        // Used on the final PASSWORD_INPUT screen
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            if (_uiState.value.resetStep != ResetStep.PASSWORD_INPUT) return@launch

            getPasswordValidationError()?.let { err ->
                _uiState.update { it.copy(errorMessage = err, loading = false) }
                return@launch
            }

            //if user is require change is true
            if (_requiresChange.value && _uiState.value.oldPassword.isBlank()){
                _uiState.update { it.copy(errorMessage = "Please enter your current password", loading = false) }
                return@launch
            }

            userRepository.resetPassword(
                email = _uiState.value.email,
                oldPassword = if (_requiresChange.value) _uiState.value.oldPassword else "",
                newPassword = _uiState.value.newPassword
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, resetStep = ResetStep.DONE, passwordChangeResult = Result.Success(Unit)) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, passwordChangeResult = null) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }

    fun resetPasswordChangeResult() {
        // call after navigation so the DONE flag is cleared
        _uiState.update { it.copy(passwordChangeResult = null) }
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
    val resetStep: ResetStep = ResetStep.PASSWORD_INPUT
)
