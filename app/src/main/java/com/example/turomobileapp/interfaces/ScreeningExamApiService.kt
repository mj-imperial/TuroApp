package com.example.turoapp.interfaces

import com.example.turoapp.models.ScreeningExam
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
    ): Response<ResponseBody>

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

    @PUT("/screeningExams/{screeningExamId}")
    suspend fun updateScreeningExam(
        @Path("screeningExamId") screeningExamId: String,
        @Body screeningExam: ScreeningExam
    ): Response<ResponseBody>

    @DELETE("/screeningExams/{screeningExamId}")
    suspend fun deleteScreeningExam(
        @Path("screeningExamId") screeningExamId: String
    ): Response<ResponseBody>
}