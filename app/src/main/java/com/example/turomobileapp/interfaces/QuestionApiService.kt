package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Question
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QuestionApiService {
    @POST("/quizzes/{quizId}/questions")
    suspend fun createQuestion(
        @Path("quizId") quizId: String,
        @Body question: Question
    ): Response<Question>

    @POST("/quizzes/{quizId}/questions/validateDuplication")
    suspend fun isQuestionDuplicate(
        @Path("quizId") quizId: String,
        @Body question: Question
    ): Response<Boolean>

    @GET("/quizzes/{quizId}/questions/{questionId}")
    suspend fun getQuestion(
        @Path("quizId") quizId: String,
        @Path("questionId") questionId: String
    ): Response<Question>

    @GET("/quizzes/{quizId}/questions")
    suspend fun getAllQuestionsForQuiz(
        @Path("quizId") quizId: String
    ): Response<List<Question>>

    @PUT("/quizzes/{quizId}/questions/{questionId}")
    suspend fun updateQuestion(
        @Path("quizId") quizId: String,
        @Path("questionId") questionId: String,
        @Body question: Question
    ): Response<ResponseBody>

    @DELETE("/quizzes/{quizId}/questions/{questionId}")
    suspend fun deleteQuestion(
        @Path("quizId") quizId: String,
        @Path("questionId") questionId: String
    ): Response<ResponseBody>
}