package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentBadgeResponse
import com.example.turomobileapp.models.StudentProfileProgress
import com.example.turomobileapp.repositories.BadgesRepository
import com.example.turomobileapp.repositories.UserRepository
import com.example.turomobileapp.viewmodels.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val badgesRepository: BadgesRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(StudentProfileUIState(
        studentName = "${sessionManager.firstName.value} ${sessionManager.lastName.value}",
        email = sessionManager.email.value.toString(),
        role = sessionManager.role.value.toString(),
        profilePic = sessionManager.profilePicUrl.value.toString()
    ))
    val uiState: StateFlow<StudentProfileUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { getStudentProgress() }
            launch { getBadgesForStudent() }
        }
    }

    fun getStudentProgress(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            userRepository.getStudentProfileProgress(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loading = false, studentProgress = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getBadgesForStudent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            badgesRepository.getAllBadgesForStudent(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { badges ->
                        _uiState.update { it.copy(loading = false, badges = badges) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class StudentProfileUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val studentName: String = "",
    val email: String = "",
    val role: String = "",
    val profilePic: String = "",
    val badges: List<StudentBadgeResponse> = emptyList(),
    val studentProgress: StudentProfileProgress? = null
)