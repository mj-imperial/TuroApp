package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.StudentAnalyticsResponse
import com.example.turomobileapp.models.StudentLeaderboardResponses
import com.example.turomobileapp.models.StudentPerformanceListResponses
import retrofit2.Response
import retrofit2.http.GET
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

    @GET("get-student-analysis")
    suspend fun getStudentAnalysis(
        @Query("student_id") studentId: String,
        @Query("course_id") courseId: String
    ): Response<StudentAnalyticsResponse>
}