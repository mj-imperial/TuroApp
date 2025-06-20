package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.QuizApiService
import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.models.QuizUploadResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizRepository @Inject constructor(private val quizApiService: QuizApiService) {

    fun createQuiz(moduleId: String, quiz: CreateQuizRequest): Flow<Result<QuizUploadResponse>> =
        handleApiResponse(
            call = { quizApiService.createQuiz(moduleId, quiz) },
            errorMessage = "Failed to create quiz"
        )

    fun getQuiz(quizId: String): Flow<Result<QuizResponse>> =
        handleApiResponse(
            call = { quizApiService.getQuiz(quizId) },
            errorMessage = "Failed to get quiz $quizId"
        )

    fun getQuizContent(quizId: String): Flow<Result<List<QuizContentResponse>>> =
        requestAndMap(
            call = { quizApiService.getQuizContent(quizId) },
            mapper = { dto -> dto.questions }
        )


    fun updateQuiz(quizId: String, moduleId: String, quiz: CreateQuizRequest): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { quizApiService.updateQuiz(quizId, moduleId, quiz) },
            errorMessage = "Failed to update quiz $quizId"
        )

    fun getQuizzesInCourse(courseId: String): Flow<Result<List<QuizResponse>>> =
        requestAndMap(
            call = { quizApiService.getQuizzesInCourse(courseId) },
            mapper = { dto -> dto.quizzes }
        )

}