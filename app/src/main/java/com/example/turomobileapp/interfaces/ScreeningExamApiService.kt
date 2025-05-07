package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Answers
import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.Question
import com.example.turomobileapp.models.ScreeningExam
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ScreeningExamApiService {
    @POST("/modules/{moduleId}/screeningExams")
    suspend fun createScreeningExam(
        @Path("moduleId") moduleId: String,
        @Body screeningExam: ScreeningExam
    ): Response<ScreeningExam>

    @POST("/modules/{moduleId}/screeningExams/validateDuplication")
    suspend fun isScreeningExamDuplicate(
        @Path("moduleId") moduleId: String,
        @Body screeningExam: ScreeningExam
    ): Response<Boolean>

    @GET("/screeningExams/{screeningExamId}")
    suspend fun getScreeningExam(
        @Path("screeningExamId") screeningExamId: String
    ): Response<ScreeningExam>

    @GET("/modules/{moduleId}/screeningExams")
    suspend fun getAllScreeningExamsForModule(
        @Path("moduleId") moduleId: String
    ): Response<List<ScreeningExam>>

    @GET("/screeningExams/{screeningExamId}/questions")
    suspend fun getQuestionsForScreeningExam(
        @Path("screeningExamId") screeningExamId: String
    ): Response<List<Question>>

    @PUT("/screeningExams/{screeningExamId}")
    suspend fun updateScreeningExam(
        @Path("screeningExamId") screeningExamId: String,
        @Body screeningExam: ScreeningExam
    ): Response<ResponseBody>

    @DELETE("/screeningExams/{screeningExamId}")
    suspend fun deleteScreeningExam(
        @Path("screeningExamId") screeningExamId: String
    ): Response<ResponseBody>

    @POST("/students/{studentId}/screeningExams/{screeningExamId}/submit")
    suspend fun submitScreeningExam(
        @Path("studentId") studentId: String,
        @Path("screeningExamId") screeningExamId: String,
        @Body answers: List<Answers>
    ): Response<AssessmentResult>
}