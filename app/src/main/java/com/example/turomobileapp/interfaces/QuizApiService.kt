package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.QuizContentResponses
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.models.QuizUploadResponse
import com.example.turomobileapp.models.QuizzesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface QuizApiService {
    @POST("create_quiz.php")
    suspend fun createQuiz(
        @Query("module_id") moduleId: String,
        @Body quiz: CreateQuizRequest
    ): Response<QuizUploadResponse>

    @GET("get_course_quizzes.php")
    suspend fun getQuizzesInCourse(
        @Query("course_id") courseId: String
    ): Response<QuizzesResponse>

    @GET("get_quiz.php")
    suspend fun getQuiz(
        @Query("activity_id") quizId: String
    ): Response<QuizResponse>

    @GET("get_quiz_content.php")
    suspend fun getQuizContent(
        @Query("activity_id") quizId: String
    ): Response<QuizContentResponses>

    @POST("update_quiz.php")
    suspend fun updateQuiz(
        @Query("activity_id") quizId: String,
        @Query("module_id") moduleId: String,
        @Body quiz: CreateQuizRequest
    ): Response<ActivityActionResponse>
}