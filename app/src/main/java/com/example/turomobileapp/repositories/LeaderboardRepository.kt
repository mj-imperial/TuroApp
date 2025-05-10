package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.LeaderboardApiService
import com.example.turomobileapp.models.Leaderboard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(private val leaderboardApiService: LeaderboardApiService) {

    fun getLeaderboardByCourse(courseId: String): Flow<Result<List<Leaderboard>>> = flow {
        handleApiResponse(
            call = { leaderboardApiService.getLeaderboardByCourse(courseId) },
            errorMessage = "Failed to get leaderboard by course $courseId"
        )
    }

    fun getTopThreeStudents(courseId: String): Flow<Result<List<Leaderboard>>> = flow {
        handleApiResponse(
            call = { leaderboardApiService.getTopThreeStudents(courseId) },
            errorMessage = "Failed to get top 3 leaderboard for course $courseId"
        )
    }
}