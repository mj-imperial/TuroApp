@file:Suppress("DEPRECATION")

package com.example.turomobileapp.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.turomobileapp.models.UserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("UseKtx")
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = EncryptedSharedPreferences.create(
        "secure_session",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /* ---------- TOKEN FLOWS ---------- */
    private val _accessToken = MutableStateFlow(prefs.getString("access", null))
    val accessToken: StateFlow<String?> = _accessToken

    suspend fun saveTokens(access: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString("access", access).apply()
        _accessToken.value = access
    }

    fun clearTokens() {
        prefs.edit().remove("access").apply()
        _accessToken.value = null
    }

    private val _userId = MutableStateFlow(prefs.getString("uid", null))
    private val _agreedToTerms = MutableStateFlow<Boolean?>(
        prefs.takeIf { it.contains("terms") }?.getBoolean("terms", false)
    )
    private val _requiresPasswordChange = MutableStateFlow<Boolean?>(
        prefs.takeIf { it.contains("pwchange") }?.getBoolean("pwchange", false)
    )
    private val _email = MutableStateFlow(prefs.getString("email", null))
    private val _firstName = MutableStateFlow(prefs.getString("fname", null))
    private val _lastName = MutableStateFlow(prefs.getString("lname", null))
    private val _profilePic = MutableStateFlow(prefs.getString("pic", null)?.let { Base64.decode(it, 0) })
    private val _roleId = MutableStateFlow(prefs.getInt("roleId", 0).takeIf { it != 0 })

    private val _role = MutableStateFlow<String?>(null)

    val userId: StateFlow<String?> = _userId
    val agreedToTerms: StateFlow<Boolean?> = _agreedToTerms
    val requiresPasswordChange: StateFlow<Boolean?> = _requiresPasswordChange
    val email: StateFlow<String?> = _email
    val firstName: StateFlow<String?> = _firstName
    val lastName: StateFlow<String?> = _lastName
    val profilePic: StateFlow<ByteArray?> = _profilePic.asStateFlow()
    val role: StateFlow<String?> = _role

    fun startSession(u: UserResponse) {
        prefs.edit().apply {
            putString("uid", u.data.userId)
            putBoolean("terms", u.data.agreedToTerms)
            putBoolean("pwchange", u.data.requiresPasswordChange)
            putString("email", u.data.email)
            putString("fname", u.data.firstName)
            putString("lname", u.data.lastName)
            putString("pic", u.data.profilePic?.let { Base64.encodeToString(it, 0) })
            putInt("roleId", u.data.roleId)
        }.apply()

        _userId.value = u.data.userId
        _agreedToTerms.value = u.data.agreedToTerms
        _requiresPasswordChange.value = u.data.requiresPasswordChange
        _email.value = u.data.email
        _firstName.value = u.data.firstName
        _lastName.value = u.data.lastName
        _profilePic.value = u.data.profilePic
        _roleId.value = u.data.roleId

        if (_roleId.value == 1){
            _role.value = "STUDENT"
        }else if(_roleId.value == 2){
            _role.value = "TEACHER"
        }else{
            _role.value = "ADMIN"
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        clearTokens()
        _userId.value = null
        _agreedToTerms.value = null
        _requiresPasswordChange.value = null
        _email.value = null
        _firstName.value = null
        _lastName.value = null
        _profilePic.value = null
        _roleId.value = null
    }

    fun setAgreedToTerms(newTerms: Boolean) {
        prefs.edit().putBoolean("terms", newTerms).apply()
        _agreedToTerms.value = newTerms
    }
}