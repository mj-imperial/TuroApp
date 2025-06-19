package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.StudentBadgesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BadgesApiService {
    @GET("get_student_badges.php")
    suspend fun getAllBadgesForStudent(
        @Query("student_id") studentId: String
    ): Response<StudentBadgesResponse>
}