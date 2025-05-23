package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.QuizApiService
import com.example.turomobileapp.models.Answers
import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.Question
import com.example.turomobileapp.models.Quiz
import com.example.turomobileapp.models.QuizResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuizRepository @Inject constructor(private val quizApiService: QuizApiService) {

    fun createQuiz(moduleId: String, activityId: String, quiz: Quiz): Flow<Result<Quiz>> = flow {
        handleApiResponse(
            call = { quizApiService.createQuiz(moduleId, activityId, quiz) },
            errorMessage = "Failed to create quiz $quiz in $moduleId"
        )
    }

    fun isQuizDuplicate(moduleId: String, quiz: Quiz): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { quizApiService.isQuizDuplicate(moduleId, quiz) },
            errorMessage = "Failed to check quiz $quiz duplication in module $moduleId"
        )
    }

    fun getQuiz(quizId: String): Flow<Result<Quiz>> = flow {
        handleApiResponse(
            call = { quizApiService.getQuiz(quizId) },
            errorMessage = "Failed to get quiz $quizId"
        )
    }

    fun getQuestionsForQuiz(quizId: String): Flow<Result<List<Question>>> = flow {
        handleApiResponse(
            call = { quizApiService.getQuestionsForQuiz(quizId) },
            errorMessage = "Failed to get questions for quiz $quizId"
        )
    }

    fun updateQuiz(quizId: String, quiz: Quiz): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { quizApiService.updateQuiz(quizId, quiz) },
            errorMessage = "Failed to update quiz $quizId"
        )
    }

    fun deleteQuiz(quizId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { quizApiService.deleteQuiz(quizId) },
            errorMessage = "Failed to delete quiz $quizId"
        )
    }

    fun submitQuiz(studentId: String, quizId: String, answers: List<Answers>): Flow<Result<AssessmentResult>> = flow {
        handleApiResponse(
            call = { quizApiService.submitQuiz(studentId, quizId, answers) },
            errorMessage = "Failed to submit quiz $quizId"
        )
    }

    fun getQuizzesInCourse(courseId: String): Flow<Result<List<QuizResponse>>> =
        requestAndMap(
            call = { quizApiService.getQuizzesInCourse(courseId) },
            mapper = { dto -> dto.quizzes }
        )

}