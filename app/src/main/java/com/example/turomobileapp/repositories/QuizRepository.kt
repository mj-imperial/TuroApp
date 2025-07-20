package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.QuizApiService
import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.CatchUpStudentResponse
import com.example.turomobileapp.models.CreateQuizRequest
import com.example.turomobileapp.models.LongQuizListResponse
import com.example.turomobileapp.models.LongQuizResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.models.QuizUploadResponse
import com.example.turomobileapp.models.ScreeningExamContent
import com.example.turomobileapp.models.ScreeningExamLearningResources
import com.example.turomobileapp.models.ScreeningExamListResponse
import com.example.turomobileapp.models.ScreeningExamResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuizRepository @Inject constructor(private val quizApiService: QuizApiService) {

    fun createQuiz(moduleId: String, quiz: CreateQuizRequest): Flow<Result<QuizUploadResponse>> =
        handleApiResponse(
            call = { quizApiService.createQuiz(moduleId, quiz) },
            errorMessage = "Failed to create exam"
        )

    fun getQuiz(quizId: String): Flow<Result<QuizResponse>> =
        handleApiResponse(
            call = { quizApiService.getQuiz(quizId) },
            errorMessage = "Failed to get exam $quizId"
        )

    fun getQuizContent(quizId: String): Flow<Result<List<QuizContentResponse>>> =
        requestAndMap(
            call = { quizApiService.getQuizContent(quizId) },
            mapper = { dto -> dto.questions }
        )


    fun updateQuiz(quizId: String, moduleId: String, quiz: CreateQuizRequest): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { quizApiService.updateQuiz(quizId, moduleId, quiz) },
            errorMessage = "Failed to update exam $quizId"
        )

    fun getLongQuizList(courseId: String): Flow<Result<List<LongQuizListResponse>>> =
        requestAndMap(
            call = { quizApiService.getLongQuizList(courseId) },
            mapper = { dto -> dto.longQuizzes }
        )

    fun getLongQuiz(longQuizId: String): Flow<Result<LongQuizResponse>> =
        handleApiResponse(
            call = { quizApiService.getLongQuiz(longQuizId) },
            errorMessage = "Failed to get long exam"
        )

    fun getLongQuizContent(longQuizId: String): Flow<Result<List<QuizContentResponse>>> =
        requestAndMap(
            call = { quizApiService.getLongQuizContent(longQuizId) },
            mapper = { dto -> dto.questions }
        )

    fun getScreeningExamList(courseId: String): Flow<Result<List<ScreeningExamListResponse>>> =
        requestAndMap(
            call = { quizApiService.getScreeningExamList(courseId) },
            mapper = { dto -> dto.data }
        )

    fun getScreeningExam(screeningId: String): Flow<Result<ScreeningExamResponse>> =
        handleApiResponse(
            call = { quizApiService.getScreeningExam(screeningId) },
            errorMessage = "Failed to get screening exam"
        )

    fun getScreeningExamContent(screeningId: String): Flow<Result<List<ScreeningExamContent>>> =
        requestAndMap(
            call = { quizApiService.getScreeningExamContent(screeningId) },
            mapper = { dto -> dto.data }
        )

    fun getLearningResources(conceptId: String, topicId: String?): Flow<Result<List<ScreeningExamLearningResources>>> =
        requestAndMap(
            call = { quizApiService.getLearningResources(conceptId, topicId) },
            mapper = { dto -> dto.resources }
        )

    fun setCatchUp(studentId: String): Flow<Result<CatchUpStudentResponse>> =
        handleApiResponse(
            call = { quizApiService.setCatchUp(studentId) },
            errorMessage = "Failed to set Catch up"
        )
}