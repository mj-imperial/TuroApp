package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CoursePicture
import com.example.turomobileapp.models.CoursesResponse
import com.example.turomobileapp.models.TeacherCoursesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CourseApiService {
    @GET("get-courses")
    suspend fun getCoursesForStudent(): Response<CoursesResponse>

    @GET("get-courses-for-teacher")
    suspend fun getCoursesForTeacher(
        @Query("teacher_id") teacherId: String
    ): Response<TeacherCoursesResponse>

    @GET("get-course-image")
    suspend fun getCoursePicture(): Response<CoursePicture>
}