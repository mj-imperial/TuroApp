package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Leaderboard
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LeaderboardApiService {
    @GET("/leaderboard/{courseId}")
    suspend fun getLeaderboardByCourse(
        @Path("courseId") courseId: String
    ): Response<List<Leaderboard>>

    @GET("/leaderboard/{courseId}/Top3")
    suspend fun getTopThreeStudents(
        @Path("courseId") courseId: String
    ): Response<List<Leaderboard>>
}