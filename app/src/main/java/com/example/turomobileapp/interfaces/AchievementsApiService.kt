package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Achievements
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AchievementsApiService {
    @GET("/achievements")
    suspend fun getAllAchievements(): Response<List<Achievements>>

    @GET("/students/{studentId}/achievements")
    suspend fun getAllAchievementsForStudent(
        @Path("studentId") studentId: String
    ): Response<List<Achievements>>

    @GET("/achievements/{achievementId}")
    suspend fun getAchievement(
        @Path("achievementId") achievementId: String
    ): Response<Achievements>

    @POST("/students/{studentId}/achievements/{achievementId}")
    suspend fun assignAchievementToStudent(
        @Path("studentId") studentId: String,
        @Path("achievementId") achievementId: String
    ): Response<ResponseBody>
}