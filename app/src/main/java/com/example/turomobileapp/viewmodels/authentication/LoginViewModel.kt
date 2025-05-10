package com.example.turomobileapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.User
import com.example.turomobileapp.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventFlow: SharedFlow<LoginEvent> = _eventFlow.asSharedFlow()

    fun updateEmail(newEmail: String){
        _uiState.update { it.copy(email = newEmail) }
        validateLoginInput()
    }

    fun updatePassword(newPassword: String){
        _uiState.update { it.copy(password = newPassword) }
        validateLoginInput()
    }

    private fun validateLoginInput() {
        _uiState.update { state ->
            state.copy(
                isLoginEnabled = state.email.isNotBlank() && state.password.isNotBlank()
            )
        }
    }

    fun login(){
        if (_uiState.value.loading || !_uiState.value.isLoginEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null, loginSuccess = null) }

            userRepository.login(_uiState.value.email.trim(), _uiState.value.password).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { user ->
                        _uiState.update {
                            it.copy(
                                loading = false,
                                loggedInUser = user,
                                password = "",
                                loginSuccess = true
                            )
                        }
                        savedStateHandle["userId"] = user.userId
                        _eventFlow.tryEmit(LoginEvent.ShowToast("Login successful"))
                        _eventFlow.tryEmit(LoginEvent.NavigateToChangePassword(user.requiresPasswordChange))
                    },
                    onFailure = { error ->
                        _uiState.update { old ->
                            old.copy(
                                loading      = false,
                                errorMessage = error,
                                loginSuccess = false,
                                password     = ""
                            )
                        }
                        _eventFlow.tryEmit(LoginEvent.ShowToast(error.toString()))
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }

    fun resetNavigation() {
        viewModelScope.launch {
            _eventFlow.emit(LoginEvent.NavigateToChangePassword(null))
        }
    }

    fun clearLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = null) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.update {
                it.copy(loggedInUser = null, email = "", password = "")
            }
            savedStateHandle["userId"] = ""
            _eventFlow.emit(LoginEvent.ShowToast("Logged out"))
        }
    }
}

data class LoginUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val loggedInUser: User? = null,
    val isLoginEnabled: Boolean = false,
    val loginSuccess: Boolean? = null
)

sealed class LoginEvent {
    data class ShowToast(val message: String) : LoginEvent()
    data class NavigateToChangePassword(val requireChange: Boolean?) : LoginEvent()
}

