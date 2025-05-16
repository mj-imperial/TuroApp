package com.example.turomobileapp.viewmodels.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _userId  = MutableStateFlow<String?>(null)
    private val _agreedToTerms = MutableStateFlow<Boolean?>(null)
    private val _requiresPasswordChange = MutableStateFlow<Boolean?>(null)
    private val _email = MutableStateFlow<String?>(null)

    val userId: StateFlow<String?> = _userId
    val agreedToTerms: StateFlow<Boolean?> = _agreedToTerms
    val requiresPasswordChange: StateFlow<Boolean?> = _requiresPasswordChange
    val email: StateFlow<String?> = _email

    fun startSession(
        userId: String,
        agreedToTerms: Boolean,
        requiresPasswordChange: Boolean,
        email: String
    ) {
        _userId.value = userId
        _agreedToTerms.value = agreedToTerms
        _requiresPasswordChange.value = requiresPasswordChange
        _email.value = email
    }

    fun clearSession() {
        _userId.value = null
        _agreedToTerms.value = null
        _requiresPasswordChange.value = null
        _email.value = null
    }
}