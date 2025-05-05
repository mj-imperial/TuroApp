package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.AssessmentResult
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AssessmentResultApiService {
    @POST("/assessmentResults")
    suspend fun saveAssessmentResult(
        @Body result: AssessmentResult
    ): Response<ResponseBody>

    @GET("/students/{studentId}/assessmentResults")
    suspend fun getAssessmentResultsForStudent(
        @Path("studentId") studentId: String
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

    @GET("/students/{studentId}/quizzes/{quizId}/assessmentResults")
    suspend fun getAssessmentResultsForQuizAndStudent(
        @Path("studentId") studentId: String,
        @Query("quizId") quizId: String
    ): Response<List<AssessmentResult>>

    @GET("/modules/{moduleId}/assessmentResults")
    suspend fun getAssessmentResultsForModule(
        @Path("moduleId") moduleId: String
    ): Response<List<AssessmentResult>>
}