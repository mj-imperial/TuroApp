package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentResultsResponse
import com.example.turomobileapp.models.AssessmentScoresResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AssessmentResultApiService {
    @POST("save_assessment_result.php")
    suspend fun saveAssessmentResult(
        @Body request: AssessmentResultUploadRequest
    ): Response<AssessmentResultUploadResponse>

    @GET("get_assessment_result_for_student_and_quiz.php")
    suspend fun getAssessmentResultsForActivityAndStudent(
        @Query("student_id") studentId: String,
        @Query("activity_id") activityId: String
    ): Response<AssessmentResultsResponse>

    @GET("get_scores_for_student_and_quiz.php")
    suspend fun getScoresForStudentAndQuiz(
        @Query("student_id") studentId: String,
        @Query("activity_id") activityId: String
    ): Response<AssessmentScoresResponse>
}