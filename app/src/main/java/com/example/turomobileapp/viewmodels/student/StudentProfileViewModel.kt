package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.GamificationRepository
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
    private val gamificationRepository: GamificationRepository,
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
            getGamificationElements()
        }
    }

    fun getGamificationElements(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            gamificationRepository.getGamifiedElements(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update {
                            it.copy(
                                loading = false,
                                section = resp.section,
                                overallPoints = resp.overallPoints,
                                leaderboardRank = resp.leaderboardRanking
                            )
                        }
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
    val profilePic: ByteArray? = null,
    val overallPoints: Int = 0,
    val section: String = "",
    val leaderboardRank: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as StudentProfileUIState

        if (loading!=other.loading) return false
        if (studentName!=other.studentName) return false
        if (email!=other.email) return false
        if (role!=other.role) return false
        if (!profilePic.contentEquals(other.profilePic)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + studentName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        return result
    }
}