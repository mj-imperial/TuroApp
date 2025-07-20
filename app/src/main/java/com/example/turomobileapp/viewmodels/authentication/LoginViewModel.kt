package com.example.turomobileapp.viewmodels.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.UserResponse
import com.example.turomobileapp.repositories.UserRepository
import com.example.turomobileapp.viewmodels.SessionManager
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

@Suppress("KotlinConstantConditions")
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginEvent>(
        replay = 0,
        extraBufferCapacity = 2
    )
    val eventFlow: SharedFlow<LoginEvent> = _eventFlow.asSharedFlow()

    fun updateEmail(newEmail: String){
        _uiState.update { it.copy(email = newEmail, errorMessage = null ) }
        validateLoginInput()
    }

    fun updatePassword(newPassword: String){
        _uiState.update { it.copy(password = newPassword, errorMessage = null ) }
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
        if (_uiState.value.loading || !_uiState.value.isLoginEnabled) {
            _uiState.update { it.copy(errorMessage = "Please complete all fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null, loginSuccess = null) }

            userRepository.login(_uiState.value.email.trim(), _uiState.value.password).collect { result ->
                Log.d("LoginDebug", "Login result=$result")
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
                        _eventFlow.tryEmit(LoginEvent.ShowToast("Login successful"))

                        viewModelScope.launch {
                            sessionManager.saveTokens(user.accessToken)
                            sessionManager.startSession(user)
                        }

                        Log.d("LoginDebug", "Email=${_uiState.value.email}, Password=${_uiState.value.password}")

                        if (user.data.requiresPasswordChange){
                            _eventFlow.tryEmit(LoginEvent.NavigateToChangeDefaultPassword(user.data.userId,user.data.email,true))
                        } else if(!user.data.agreedToTerms){
                            _eventFlow.tryEmit(LoginEvent.NavigateToTermsAgreement(user.data.userId, user.data.agreedToTerms,
                                user.data.roleId
                            ))
                        } else{
                            _eventFlow.tryEmit(LoginEvent.NavigateToDashboard(user.data.userId))
                        }
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

    fun clearLoginSuccess() {
        _uiState.update { it.copy(loginSuccess = null) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.update {
                it.copy(loggedInUser = null, email = "", password = "")
            }
            sessionManager.clearSession()
            _eventFlow.emit(LoginEvent.ShowToast("Logged out"))
        }
    }
}

data class LoginUiState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val email: String = "",
    val password: String = "",
    val loggedInUser: UserResponse? = null,
    val isLoginEnabled: Boolean = false,
    val loginSuccess: Boolean? = null,
    val requiresChange: Boolean? = null
)

sealed class LoginEvent {
    data class ShowToast(val message: String) : LoginEvent()
    data class NavigateToChangeDefaultPassword(val userId: String, val email: String,val requiresChange: Boolean) : LoginEvent()
    data class NavigateToTermsAgreement(val userId: String, val agreedToTerms: Boolean, val role: Int) : LoginEvent()
    data class NavigateToDashboard(val userId: String) : LoginEvent()
}

