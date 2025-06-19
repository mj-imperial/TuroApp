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
        profilePic = sessionManager.profilePic.value
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
            _uiState.update { it.copy(loadingProgress = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            userRepository.getStudentProfileProgress(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(loadingProgress = false, studentProgress = resp) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingProgress = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getBadgesForStudent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingBadges = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            badgesRepository.getAllBadgesForStudent(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { badges ->
                        _uiState.update { it.copy(loadingBadges = false, badges = badges) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingBadges = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class StudentProfileUIState(
    val loadingProgress: Boolean = false,
    val loadingBadges: Boolean = false,
    val errorMessage: String? = null,
    val studentName: String = "",
    val email: String = "",
    val role: String = "",
    val profilePic: ByteArray? = null,
    val badges: List<StudentBadgeResponse> = emptyList(),
    val studentProgress: StudentProfileProgress? = null
){
    val loading: Boolean get() = loadingProgress || loadingBadges
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as StudentProfileUIState

        if (loadingProgress!=other.loadingProgress) return false
        if (loadingBadges!=other.loadingBadges) return false
        if (errorMessage!=other.errorMessage) return false
        if (studentName!=other.studentName) return false
        if (email!=other.email) return false
        if (role!=other.role) return false
        if (!profilePic.contentEquals(other.profilePic)) return false
        if (badges!=other.badges) return false
        if (studentProgress!=other.studentProgress) return false
        if (loading!=other.loading) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loadingProgress.hashCode()
        result = 31 * result + loadingBadges.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + studentName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        result = 31 * result + badges.hashCode()
        result = 31 * result + (studentProgress?.hashCode() ?: 0)
        result = 31 * result + loading.hashCode()
        return result
    }
}