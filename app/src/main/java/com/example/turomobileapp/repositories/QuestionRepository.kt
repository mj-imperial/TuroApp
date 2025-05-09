package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.QuestionApiService
import com.example.turomobileapp.models.Question
import com.example.turomobileapp.helperfunctions.handleApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val questionApiService: QuestionApiService) {

    fun createQuestion(quizId: String, question: Question): Flow<Result<Question>> = flow {
        handleApiResponse(
            call = { questionApiService.createQuestion(quizId, question) },
            errorMessage = "Failed to create question $question in $quizId"
        )
    }

    fun isQuestionDuplicate(quizId: String, question: Question): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { questionApiService.isQuestionDuplicate(quizId, question) },
            errorMessage = "Failed to check question $question duplication in quiz $quizId"
        )
    }

    fun getQuestion(quizId: String, questionId: String): Flow<Result<Question>> = flow {
        handleApiResponse(
            call = { questionApiService.getQuestion(quizId, questionId) },
            errorMessage = "Failed to get question $questionId in quiz $quizId"
        )
    }

    fun getAllQuestionsForQuiz(quizId: String): Flow<Result<List<Question>>> = flow {
        handleApiResponse(
            call = { questionApiService.getAllQuestionsForQuiz(quizId) },
            errorMessage = "Failed to get all questions for quiz $quizId"
        )
    }

    fun updateQuestion(quizId: String, questionId: String, question: Question): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { questionApiService.updateQuestion(quizId, questionId, question) },
            errorMessage = "Failed to update question $questionId"
        )
    }

    fun deleteQuestion(quizId: String, questionId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { questionApiService.deleteQuestion(quizId, questionId) },
            errorMessage = "Failed to delete question $questionId"
        )
    }
}