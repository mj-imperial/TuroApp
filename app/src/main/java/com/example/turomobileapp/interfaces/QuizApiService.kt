package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.CatchUpStudentResponse
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.LongQuizResponse
import com.example.turomobileapp.models.LongQuizzesListResponse
import com.example.turomobileapp.models.QuizContentResponses
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.models.QuizUploadResponse
import com.example.turomobileapp.models.ScreeningExamContents
import com.example.turomobileapp.models.ScreeningExamLearningResourcesResponse
import com.example.turomobileapp.models.ScreeningExamResponse
import com.example.turomobileapp.models.ScreeningExamsListResponse
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

    @GET("get-quiz")
    suspend fun getQuiz(
        @Query("activity_id") quizId: String
    ): Response<QuizResponse>

    @GET("get-quiz-content")
    suspend fun getQuizContent(
        @Query("activity_id") quizId: String
    ): Response<QuizContentResponses>

    @POST("update_quiz.php")
    suspend fun updateQuiz(
        @Query("activity_id") quizId: String,
        @Query("module_id") moduleId: String,
        @Body quiz: CreateQuizRequest
    ): Response<ActivityActionResponse>

    @GET("get-long-exam-list")
    suspend fun getLongQuizList(
        @Query("course_id") courseId: String
    ): Response<LongQuizzesListResponse>

    @GET("get-long-exam")
    suspend fun getLongQuiz(
        @Query("long_quiz_id") longQuizId: String
    ): Response<LongQuizResponse>

    @GET("get-long-exam-content")
    suspend fun getLongQuizContent(
        @Query("long_quiz_id") longQuizId: String
    ): Response<QuizContentResponses>

    @GET("get-screening-exam-list")
    suspend fun getScreeningExamList(
        @Query("course_id") courseId: String
    ): Response<ScreeningExamsListResponse>

    @GET("get-screening-exam")
    suspend fun getScreeningExam(
        @Query("screening_id") screeningId: String
    ): Response<ScreeningExamResponse>

    @GET("get-screening-exam-content")
    suspend fun getScreeningExamContent(
        @Query("screening_id") screeningId: String
    ): Response<ScreeningExamContents>

    @GET("get-learning-resources")
    suspend fun getLearningResources(
        @Query("concept_id") conceptId: String,
        @Query("topic_id") topicId: String? = null
    ): Response<ScreeningExamLearningResourcesResponse>

    @POST("set-catch-up")
    suspend fun setCatchUp(
        @Query("student_id") studentId: String
    ): Response<CatchUpStudentResponse>
}