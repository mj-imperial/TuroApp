package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.IndividualStudentList
import com.example.turomobileapp.models.StudentLeaderboardResponses
import com.example.turomobileapp.models.StudentPerformanceListResponses
import com.example.turomobileapp.models.UpdateStudentCourseProgressRequest
import com.example.turomobileapp.models.UpdateStudentModuleProgressRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudentProgressApiService {
    @GET("get_leaderboard.php")
    suspend fun getLeaderboardCourse(
        @Query("student_id") studentId: String
    ): Response<StudentLeaderboardResponses>

    @GET("get_student_progress_list_for_course.php")
    suspend fun getCourseProgressForAllStudents(
        @Query("course_id") courseId: String
    ): Response<StudentPerformanceListResponses>

    @GET("get_individual_student_progress_course.php")
    suspend fun getIndividualStudentCourseProgress(
        @Query("student_id") studentId: String,
        @Query("course_id") courseId: String
    ): Response<IndividualStudentList>

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