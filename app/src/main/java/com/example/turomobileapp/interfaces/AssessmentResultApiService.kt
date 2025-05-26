package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentResultsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("")
    suspend fun getAssessmentResultsForStudent(
        @Query("student_id") studentId: String
    ): Response<List<AssessmentResult>>

    @GET("/assessmentResults/{assessmentResultId}")
    suspend fun getAssessmentResult(
        @Path("assessmentResultId") assessmentResultId: String
    ): Response<AssessmentResult>

    @GET("/students/{studentId}/modules/{moduleId}/assessmentResults")
    suspend fun getAssessmentResultsForStudentAndModule(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String
    ): Response<List<AssessmentResult>>

    @GET("/assessmentResults")
    suspend fun getAssessmentResultsBasedOnType(
        @Query("activityType") activityType: String
    ): Response<List<AssessmentResult>>

    @GET("/assessmentResults")
    suspend fun getAssessmentResultsForQuiz(
        @Query("quizId") quizId: String
    ): Response<List<AssessmentResult>>



    @GET("/modules/{moduleId}/assessmentResults")
    suspend fun getAssessmentResultsForModule(
        @Path("moduleId") moduleId: String
    ): Response<List<AssessmentResult>>
}