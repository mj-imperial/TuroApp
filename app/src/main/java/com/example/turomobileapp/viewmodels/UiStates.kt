package com.example.turomobileapp.viewmodels

import android.net.Uri
import com.example.turomobileapp.models.CalendarEvent
import com.example.turomobileapp.models.User
import com.example.turomobileapp.repositories.Result

open class BaseUiState(
    open val loading: Boolean = false,
    open val errorMessage: String? = null
)

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val loggedInUser: User? = null,
    val isLoginEnabled: Boolean = false,
    val loginSuccess: Boolean? = null
): BaseUiState(loading, errorMessage)