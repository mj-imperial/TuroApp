package com.example.turoapp.interfaces

import com.example.turoapp.models.Course
import com.example.turoapp.models.Enrollment
import com.example.turoapp.models.Student
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EnrollmentApiService {
    @GET("/enrollment")
    suspend fun getAllEnrollments(): Response<List<Enrollment>>

    @POST("/enrollment/students/{studentId}/courses/{courseId}")
    suspend fun enrollStudentInCourse(
        @Body enrollment: Enrollment,
        @Path("studentId") studentId: String,
        @Path("courseId") courseId: String
    ): Response<Enrollment>

    @GET("/enrollment/{enrollmentId}")
    suspend fun getEnrollmentId(
        @Path("enrollmentId") enrollmentId: String
    ): Response<Enrollment>

    @GET("/enrollment/students/{studentId}/courses")
    suspend fun getCoursesForStudent(
        @Path("studentId") studentId: String
    ): Response<List<Course>>

    @GET("enrollment/courses/{courseId}/students")
    suspend fun getStudentsinCourse(
        @Path("courseId") courseId: String
    ): Response<List<Student>>

    @DELETE("enrollment/courses/{courseId}/students/{studentId}")
    suspend fun unenrollStudentFromCourse(
        @Path("courseId") courseId: String,
        @Path("studentId") studentId: String
    ): Response<ResponseBody>
}