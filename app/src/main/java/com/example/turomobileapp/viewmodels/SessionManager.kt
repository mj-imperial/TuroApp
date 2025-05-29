package com.example.turomobileapp.viewmodels

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
    private val _firstName = MutableStateFlow<String?>(null)
    private val _lastName = MutableStateFlow<String?>(null)
    private val _profilePicUrl = MutableStateFlow<String?>(null)
    private val _role = MutableStateFlow<String?>(null)

    val userId: StateFlow<String?> = _userId
    val agreedToTerms: StateFlow<Boolean?> = _agreedToTerms
    val requiresPasswordChange: StateFlow<Boolean?> = _requiresPasswordChange
    val email: StateFlow<String?> = _email
    val firstName: StateFlow<String?> = _firstName
    val lastName: StateFlow<String?> = _lastName
    val profilePicUrl: StateFlow<String?> = _profilePicUrl
    val role: StateFlow<String?> = _role

    fun startSession(
        userId: String,
        agreedToTerms: Boolean,
        requiresPasswordChange: Boolean,
        email: String,
        firstName: String,
        lastName: String,
        profilePicUrl: String,
        role: String
    ) {
        _userId.value = userId
        _agreedToTerms.value = agreedToTerms
        _requiresPasswordChange.value = requiresPasswordChange
        _email.value = email
        _firstName.value = firstName
        _lastName.value = lastName
        _profilePicUrl.value = profilePicUrl
        _role.value = role
    }

    fun clearSession() {
        _userId.value = null
        _agreedToTerms.value = null
        _requiresPasswordChange.value = null
        _email.value = null
        _firstName.value = null
        _lastName.value = null
        _profilePicUrl.value = null
        _role.value = null
    }

    fun updateProfilePicUrl(newPic: String){
        _profilePicUrl.value = newPic
    }

    fun setRole(role: String){
        _role.value = role
    }
}