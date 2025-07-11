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
    @POST("save-assessment-result")
    suspend fun saveAssessmentResult(
        @Query("module_id") moduleId: String,
        @Body request: AssessmentResultUploadRequest
    ): Response<AssessmentResultUploadResponse>

    @GET("get-assessment-result-for-student-and-quiz")
    suspend fun getAssessmentResultsForActivityAndStudent(
        @Query("student_id") studentId: String,
        @Query("activity_id") activityId: String
    ): Response<AssessmentResultsResponse>

    @GET("get-scores-for-student-and-quiz")
    suspend fun getScoresForStudentAndQuiz(
        @Query("student_id") studentId: String,
        @Query("activity_id") activityId: String
    ): Response<AssessmentScoresResponse>
}