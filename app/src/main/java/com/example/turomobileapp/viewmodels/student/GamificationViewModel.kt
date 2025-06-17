package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.StudentAchievementResponse
import com.example.turomobileapp.models.StudentBadgeResponse
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.repositories.AchievementsRepository
import com.example.turomobileapp.repositories.BadgesRepository
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
class GamificationViewModel @Inject constructor(
    private val studentProgressRepository: StudentProgressRepository,
    private val badgesRepository: BadgesRepository,
    private val achievementsRepository: AchievementsRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(LeaderboardUIState())
    val uiState: StateFlow<LeaderboardUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch { getLeaderboard() }
            launch { getBadgesForStudent() }
            launch { getAchievementsForStudent() }
        }
    }

    fun getLeaderboard(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingLeaderboard = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            studentProgressRepository.getLeaderboardCourse(studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { progresses ->
                        _uiState.update { it.copy(loadingLeaderboard = false, progresses = progresses) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingLeaderboard = false, errorMessage = err) }
                    },
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
                        _uiState.update { it.copy(loadingBadges = false, studentBadges = badges) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingBadges = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getAchievementsForStudent(){
        viewModelScope.launch {
            _uiState.update { it.copy(loadingAchievements = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

             achievementsRepository.getAchievementsForStudent(studentId).collect { result ->
                 handleResult(
                     result = result,
                     onSuccess = { resp ->
                         _uiState.update { it.copy(loadingAchievements = false, studentAchievements = resp) }
                     },
                     onFailure = { err ->
                         _uiState.update { it.copy(loadingAchievements = false, errorMessage = err) }
                     }
                 )
             }
        }
    }
}

data class LeaderboardUIState(
    val loadingLeaderboard: Boolean = false,
    val loadingBadges: Boolean = false,
    val loadingAchievements: Boolean = false,
    val errorMessage: String? = null,
    val progresses: List<StudentLeaderboardResponse> = emptyList(),
    val studentBadges: List<StudentBadgeResponse> = emptyList(),
    val studentAchievements: List<StudentAchievementResponse> = emptyList()
){
    val loading: Boolean get() = loadingLeaderboard || loadingBadges || loadingAchievements
}