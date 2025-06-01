package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentProgressResponse
import com.example.turomobileapp.repositories.StudentProgressRepository
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
class LeaderboardViewModel @Inject constructor(
    private val studentProgressRepository: StudentProgressRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(LeaderboardUIState())
    val uiState: StateFlow<LeaderboardUIState> = _uiState.asStateFlow()

    init {
        getLeaderboard()
    }

    fun getLeaderboard(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            studentProgressRepository.getLeaderboardCourse(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { progresses ->
                        _uiState.update { it.copy(loading = false, progresses = progresses) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }
}

data class LeaderboardUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val progresses: List<StudentProgressResponse> = emptyList()
)