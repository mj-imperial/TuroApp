package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.IndividualStudentList
import com.example.turomobileapp.models.StudentLeaderboardResponses
import com.example.turomobileapp.models.StudentPerformanceListResponses
import com.example.turomobileapp.models.StudentPerformanceResponse
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

    @GET("get_individual_student_progress_course.php")
    suspend fun getIndividualStudentCourseProgress(
        @Query("student_id") studentId: String,
        @Query("course_id") courseId: String
    ): Response<IndividualStudentList>

    @GET("get_overview_student_performance.php")
    suspend fun getIndividualStudentPerformanceList(
        @Query("student_id") studentId: String,
        @Query("course_id") courseId: String
    ): Response<StudentPerformanceResponse>
}