package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CoursePicture
import com.example.turomobileapp.models.CoursesResponse
import retrofit2.Response
import retrofit2.http.GET

interface CourseApiService {
    @GET("get-courses")
    suspend fun getCoursesForUser(): Response<CoursesResponse>

    @GET("get-course-image")
    suspend fun getCoursePicture(): Response<CoursePicture>
}