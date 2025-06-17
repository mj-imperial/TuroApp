package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.StudentBadgesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BadgesApiService {
    @GET("get_student_badges.php")
    suspend fun getAllBadgesForStudent(
        @Query("student_id") studentId: String
    ): Response<StudentBadgesResponse>

    @POST("/students/{studentId}/badges/{badgeId}")
    suspend fun assignBadgeToStudent(
        @Path("studentId") studentId: String,
        @Path("badgeId") badgeId: String
    ): Response<ResponseBody>
}