package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.GamifiedElementsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GamificationApiService {
    @GET("get-gamified-elements")
    suspend fun getGamifiedElements(
        @Query("student_id") studentId: String
    ): Response<GamifiedElementsResponse>
}