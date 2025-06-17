package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.AchievementsApiService
import com.example.turomobileapp.models.StudentAchievementResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AchievementsRepository @Inject constructor(private val achievementsApiService: AchievementsApiService) {

    fun getAchievementsForStudent(studentId: String): Flow<Result<List<StudentAchievementResponse>>> =
        requestAndMap(
            call = { achievementsApiService.getAchievementsForStudent(studentId) },
            mapper = { dto -> dto.achievements }
        )
}