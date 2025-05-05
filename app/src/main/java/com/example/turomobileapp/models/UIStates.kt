package com.example.turoapp.models

import android.net.Uri
import com.example.turoapp.enums.ResetStep
import com.example.turoapp.repositories.Result

open class BaseUiState(
    open val loading: Boolean = false,
    open val errorMessage: String? = null
)

data class ChangePasswordUiState(
    val email: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val verificationCode: String = "",
    val requiresPasswordChangeStatus: Boolean = false,
    val passwordChangeResult: Result<Unit>? = null,
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val resetStep: ResetStep = ResetStep.EMAIL_INPUT
): BaseUiState(loading, errorMessage)

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val loggedInUser: User? = null,
    val isLoginEnabled: Boolean = false,
    val loginSuccess: Boolean? = null
): BaseUiState(loading, errorMessage)

data class TermsAndConditionsUiState(
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val agreementStatus: Boolean = false,
    val isAgreementSaved: Result<Unit>? = null
): BaseUiState(loading, errorMessage)

data class ProfileUiState(
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val user: User? = null,
    val selectedImageUri: Uri? = null,
    val profilePicUpdateResult: Result<Unit>? = null
): BaseUiState(loading, errorMessage)

data class CalendarUiState(
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val eventList: List<CalendarEvent> = emptyList()
): BaseUiState(loading, errorMessage)




