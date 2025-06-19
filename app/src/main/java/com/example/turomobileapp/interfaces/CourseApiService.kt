package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CoursePicture
import com.example.turomobileapp.models.CoursesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseApiService {
    @GET("get_courses.php")
    suspend fun getCoursesForStudent(
        @Query("action") action: String,
        @Query("user_id") userId: String
    ): Response<CoursesResponse>

    @GET("get_courses.php")
    suspend fun getCoursesForTeacher(
        @Query("action") action: String,
        @Query("user_id") userId: String
    ): Response<CoursesResponse>

    @GET("get_course_picture.php")
    suspend fun getCoursePicture(
        @Query("course_id") courseId: String
    ): Response<CoursePicture>
}