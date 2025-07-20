package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentResultsResponse
import com.example.turomobileapp.models.AssessmentScoresResponse
import com.example.turomobileapp.models.LongQuizAssessmentResultUploadRequest
import com.example.turomobileapp.models.LongQuizAssessmentResultsResponse
import com.example.turomobileapp.models.ScreeningExamAssessmentResults
import com.example.turomobileapp.models.ScreeningExamAssessmentUpload
import com.example.turomobileapp.models.ScreeningExamResultsResponse
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

    @GET("get-long-exam-assessment-result-for-student-and-exam")
    suspend fun getLongQuizResultsForStudent(
        @Query("long_quiz_id") longQuizId: String,
        @Query("student_id") studentId: String
    ): Response<LongQuizAssessmentResultsResponse>

    @POST("save-long-exam-assessment-result")
    suspend fun saveLongQuizAssessmentResult(
        @Body request: LongQuizAssessmentResultUploadRequest
    ): Response<AssessmentResultUploadResponse>

    @GET("get-screening-exam-assessment-result-for-student")
    suspend fun getScreeningExamAssessmentResultForStudent(
        @Query("student_id") studentId: String,
        @Query("screening_id") screeningId: String
    ): Response<ScreeningExamAssessmentResults>

    @POST("save-screening-exam-result")
    suspend fun saveScreeningExamResult(
        @Body request: ScreeningExamAssessmentUpload
    ): Response<AssessmentResultUploadResponse>

    @GET("get-screening-exam-result")
    suspend fun getScreeningExamResult(
        @Query("student_id") studentId: String,
        @Query("screening_id") screeningId: String
    ): Response<ScreeningExamResultsResponse>
}