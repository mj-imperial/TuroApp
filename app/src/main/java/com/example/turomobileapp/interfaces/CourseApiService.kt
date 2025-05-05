package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Course
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CourseApiService {
    @GET("/courses")
    suspend fun getAllCourses(): Response<List<Course>>

    @GET("/students/{studentId}/courses")
    suspend fun getCoursesForStudent(
        @Path("studentId") studentId: String
    ): Response<List<Course>>

    @GET("/courses/{courseId}")
    suspend fun getCourse(
        @Path("courseId") courseId: String
    ): Response<Course>

    @POST("/courses")
    suspend fun createCourse(
        @Body course: Course
    ): Response<Course>

    @PUT("/courses/{courseId}")
    suspend fun updateCourse(
        @Path("courseId") courseId: String,
        @Body course: Course
    ): Response<ResponseBody>

    @DELETE("/courses/{courseId}")
    suspend fun deleteCourse(
        @Path("courseId") courseId: String
    ): Response<ResponseBody>

    @POST("/courses/validateDuplication")
    suspend fun isCourseDuplicate(
        @Body course: Course
    ): Response<Boolean>
}