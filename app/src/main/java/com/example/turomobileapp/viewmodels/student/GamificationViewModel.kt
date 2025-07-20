package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.Achievement
import com.example.turomobileapp.models.AchievementRetrieved
import com.example.turomobileapp.models.Badge
import com.example.turomobileapp.models.BadgeRetrieved
import com.example.turomobileapp.models.LeaderboardEntry
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
class GamificationViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val sessionManager: SessionManager
): ViewModel(){

    private val _uiState = MutableStateFlow(LeaderboardUIState())
    val uiState: StateFlow<LeaderboardUIState> = _uiState.asStateFlow()

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
                                currentUserName = resp.studentName,
                                leaderboards = resp.leaderboards,
                                achievements = resp.achievements,
                                achievementsRetrieved = mergeAchievements(resp.achievements, resp.achievementsRetrieved),
                                badges = resp.badges,
                                badgesRetrieved = mergeBadges(resp.badges, resp.badgesRetrieved)
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

data class LeaderboardUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserName: String = "",
    val section: String = "",
    val leaderboards: List<LeaderboardEntry> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val achievementsRetrieved: List<StudentAchievementResponseCompose> = emptyList(),
    val badges: List<Badge> = emptyList(),
    val badgesRetrieved: List<StudentBadgeResponseCompose> = emptyList()
)

data class StudentAchievementResponseCompose(
    val achievementId: Int,
    val achievementName: String,
    val achievementDescription: String,
    val achievementImage: String,
    val isUnlocked: Boolean
)

data class StudentBadgeResponseCompose(
    val badgeId: Int,
    val badgeName: String,
    val badgeDescription: String,
    val badgeImage: String,
    val isUnlocked: Boolean
)

fun mergeAchievements(
    all: List<Achievement>,
    unlocked: List<AchievementRetrieved>
): List<StudentAchievementResponseCompose> {
    val unlockedIds = unlocked.map { it.achievementId }.toSet()
    return all.map { ach ->
        StudentAchievementResponseCompose(
            achievementId = ach.achievementId,
            achievementName = ach.achievementName,
            achievementDescription = ach.achievementDescription,
            achievementImage = ach.imageName,
            isUnlocked = unlockedIds.contains(ach.achievementId)
        )
    }
}

fun mergeBadges(
    all: List<Badge>,
    unlocked: List<BadgeRetrieved>
): List<StudentBadgeResponseCompose> {
    val unlockedIds = unlocked.map { it.badgeId }.toSet()
    return all.map { badge ->
        StudentBadgeResponseCompose(
            badgeId = badge.badgeId,
            badgeName = badge.badgeName,
            badgeDescription = badge.badgeDescription,
            badgeImage = badge.imageName,
            isUnlocked = unlockedIds.contains(badge.badgeId)
        )
    }
}