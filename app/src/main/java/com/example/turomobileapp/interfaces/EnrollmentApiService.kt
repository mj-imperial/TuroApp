package com.example.turomobileapp.interfaces

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface EnrollmentApiService {
    @DELETE("enrollment/courses/{courseId}/students/{studentId}")
    suspend fun unenrollStudentFromCourse(
        @Path("courseId") courseId: String,
        @Path("studentId") studentId: String
    ): Response<ResponseBody>
}