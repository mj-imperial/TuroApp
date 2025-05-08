package com.example.turomobileapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import com.example.turomobileapp.repositories.UserRepository
import com.example.turomobileapp.viewmodels.LoginUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigateToChangePassword = Channel<Boolean?>()
    val navigateToChangePassword = _navigateToChangePassword.receiveAsFlow()
}