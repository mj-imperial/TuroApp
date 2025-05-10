package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.AchievementsApiService
import com.example.turomobileapp.models.Achievements
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AchievementsRepository @Inject constructor(private val achievementsApiService: AchievementsApiService) {

    fun getAllAchievements(): Flow<Result<List<Achievements>>> = flow {
        handleApiResponse(
            call = { achievementsApiService.getAllAchievements() },
            errorMessage = "Failed to get all achievements"
        )
    }

    fun getAchievement(achievementId: String): Flow<Result<Achievements>> = flow {
        handleApiResponse(
            call = { achievementsApiService.getAchievement(achievementId) },
            errorMessage = "Failed to get achievement $achievementId"
        )
    }

    fun getAllAchievementsForStudent(studentId: String): Flow<Result<List<Achievements>>> = flow {
        handleApiResponse(
            call = { achievementsApiService.getAllAchievementsForStudent(studentId) },
            errorMessage = "Failed to get all achievements for student $studentId"
        )
    }

    fun assignAchievementToStudent(studentId: String, achievementId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { achievementsApiService.assignAchievementToStudent(studentId, achievementId) },
            errorMessage = "Failed to get assign achievement $achievementId for student $studentId"
        )
    }
}