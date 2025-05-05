package com.example.turoapp.repositories

import com.example.turoapp.interfaces.QuestionApiService
import com.example.turoapp.models.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val questionApiService: QuestionApiService) {

    fun createQuestion(quizId: String, question: Question): Flow<Result<Question>> = flow {
        try{
            val response = questionApiService.createQuestion(quizId, question)
            if (response.isSuccessful){
                val question = response.body()
                question?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("createQuestion Response Body is empty")))
            } else{
                val errorMessage = "Failed to create question: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isQuestionDuplicate(quizId: String, question: Question): Flow<Result<Boolean>> = flow {
        try {
            val response = questionApiService.isQuestionDuplicate(quizId, question)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isQuestionDuplicate Response Body is empty")))
            } else{
                val errorMessage = "Failed to check question duplication: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getQuestion(quizId: String, questionId: String): Flow<Result<Question>> = flow {
        try {
            val response = questionApiService.getQuestion(quizId, questionId)
            if (response.isSuccessful){
                val question = response.body()
                question?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getQuestion Response Body is empty")))
            } else{
                val errorMessage = "Failed to get question: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllQuestionsForQuiz(quizId: String): Flow<Result<List<Question>>> = flow {
        try {
            val response = questionApiService.getAllQuestionsForQuiz(quizId)
            if (response.isSuccessful){
                val questions = response.body()
                questions?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllQuestionsForQuiz Response Body is empty")))
            } else{
                val errorMessage = "Failed to get all questions for quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateQuestion(quizId: String, questionId: String, question: Question): Flow<Result<Unit>> = flow {
        try {
            val response = questionApiService.updateQuestion(quizId, questionId, question)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update question: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteQuestion(quizId: String, questionId: String): Flow<Result<Unit>> = flow {
        try {
            val response = questionApiService.deleteQuestion(quizId, questionId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete question: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}