package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.StudentAchievementsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AchievementsApiService {
    @GET("get_achievements_for_student.php")
    suspend fun getAchievementsForStudent(
        @Query("student_id") studentId: String
    ): Response<StudentAchievementsResponse>
}