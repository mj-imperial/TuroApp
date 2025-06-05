package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Answers
import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.Quiz
import com.example.turomobileapp.models.QuizContentResponses
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.models.QuizUploadResponse
import com.example.turomobileapp.models.QuizzesResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApiService {
    @POST("create_quiz.php")
    suspend fun createQuiz(
        @Query("module_id") moduleId: String,
        @Body quiz: CreateQuizRequest
    ): Response<QuizUploadResponse>

    @POST("/modules/{moduleId}/quizzes/validateDuplication")
    suspend fun isQuizDuplicate(
        @Path("moduleId") moduleId: String,
        @Body quiz: Quiz
    ): Response<Boolean>

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

    @PUT("/quizzes/{quizId}")
    suspend fun updateQuiz(
        @Path("quizId") quizId: String,
        @Body quiz: Quiz
    ): Response<ResponseBody>

    @DELETE("/quizzes/{quizId}")
    suspend fun deleteQuiz(
        @Path("quizId") quizId: String
    ): Response<ResponseBody>

    @POST("/students/{studentId}/quizzes/{quizId}/submit")
    suspend fun submitQuiz(
        @Path("studentId") studentId: String,
        @Path("quizId") quizId: String,
        @Body answers: List<Answers>
    ): Response<AssessmentResult>
}