package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.StudentProgress
import com.example.turomobileapp.models.UpdateStudentCourseProgressRequest
import com.example.turomobileapp.models.UpdateStudentModuleProgressRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudentProgressApiService {
    @GET("/students/{studentId}/progress/courses")
    suspend fun getAllStudentCourseProgress(
        @Path("studentId") studentId: String
    ): Response<List<StudentProgress>>

    @GET("/students/{studentId}/progress/courses/{courseId}")
    suspend fun getStudentCourseProgress(
        @Path("studentId") studentId: String,
        @Path("courseId") courseId: String
    ): Response<StudentProgress>

    @GET("/courses/{courseId}/progress")
    suspend fun getCourseProgressForAllStudents(
        @Path("courseId") courseId: String
    ): Response<List<StudentProgress>>

    @PUT("/students/{studentId}/modules/{moduleId}/progress")
    suspend fun updateStudentModuleProgress(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String,
        @Body request: UpdateStudentModuleProgressRequest
    ): Response<ResponseBody>

    @PUT("/students/{studentId}/courses/{courseId}/progress")
    suspend fun updateStudentCourseProgress(
        @Path("studentId") studentId: String,
        @Path("courseId") courseId: String,
        @Body request: UpdateStudentCourseProgressRequest
    ): Response<ResponseBody>
}