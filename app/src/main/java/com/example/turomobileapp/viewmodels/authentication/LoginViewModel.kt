package com.example.turomobileapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.repositories.UserRepository
import com.example.turomobileapp.viewmodels.LoginUiState
import com.example.turomobileapp.viewmodels.higherorderfunctions.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigateToChangePassword = Channel<Boolean?>()
    val navigateToChangePassword = _navigateToChangePassword.receiveAsFlow()

    private val _toastChannel = Channel<String>()
    val toastFlow = _toastChannel.receiveAsFlow()

    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
        validateLoginInput()
    }

    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
        validateLoginInput()
    }

    private fun validateLoginInput() {
        _uiState.value = _uiState.value.copy(isLoginEnabled = _uiState.value.email.isNotBlank() && _uiState.value.password.isNotBlank())
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, loginSuccess = null)

            userRepository.login(_uiState.value.email, _uiState.value.password).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(loading = false, loggedInUser = user, loginSuccess = true)
                        savedStateHandle["userId"] = user.userId

                        launch {
                            _navigateToChangePassword.send(user.requiresPasswordChange)
                            _toastChannel.send("Login Successful")
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(loading = false, errorMessage = error, loginSuccess = false)
                    }
                )
            }
        }
    }

    fun resetNavigation() {
        viewModelScope.launch {
            _navigateToChangePassword.send(null)
        }
    }

    fun clearLoginSuccess() {
        _uiState.value = _uiState.value.copy(loginSuccess = null)
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = _uiState.value.copy(loggedInUser = null)
            savedStateHandle["userId"] = ""
            _toastChannel.send("Logging Out")
        }
    }
}