package com.example.turoapp.repositories

import com.example.turoapp.interfaces.QuizApiService
import com.example.turoapp.models.Answers
import com.example.turoapp.models.AssessmentResult
import com.example.turoapp.models.Question
import com.example.turoapp.models.Quiz
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class QuizRepository @Inject constructor(private val quizApiService: QuizApiService) {

    fun createQuiz(moduleId: String, activityId: String, quiz: Quiz): Flow<Result<Quiz>> = flow {
        try {
            val response = quizApiService.createQuiz(moduleId, activityId, quiz)
            if (response.isSuccessful){
                val quiz = response.body()
                quiz?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("createQuiz Response body is empty")))
            }else{
                val errorMessage = "Failed to create quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isQuizDuplicate(moduleId: String, quiz: Quiz): Flow<Result<Boolean>> = flow {
        try {
            val response = quizApiService.isQuizDuplicate(moduleId, quiz)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isQuizDuplicate Response body is empty")))
            }else{
                val errorMessage = "Failed to check quiz duplication: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getQuiz(quizId: String): Flow<Result<Quiz>> = flow {
        try {
            val response = quizApiService.getQuiz(quizId)
            if (response.isSuccessful){
                val quiz = response.body()
                quiz?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getQuiz Response body is empty")))
            }else{
                val errorMessage = "Failed to get quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getQuestionsForQuiz(quizId: String): Flow<Result<List<Question>>> = flow {
        try {
            val response = quizApiService.getQuestionsForQuiz(quizId)
            if (response.isSuccessful){
                val questions = response.body()
                questions?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getQuestionsForQuiz Response body is empty")))
            }else{
                val errorMessage = "Failed to get questions for quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateQuiz(quizId: String, quiz: Quiz): Flow<Result<Unit>> = flow {
        try {
            val response = quizApiService.updateQuiz(quizId, quiz)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteQuiz(quizId: String): Flow<Result<Unit>> = flow {
        try {
            val response = quizApiService.deleteQuiz(quizId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun submitQuiz(studentId: String, quizId: String, answers: List<Answers>): Flow<Result<AssessmentResult>> = flow {
        try{
            val response = quizApiService.submitQuiz(studentId, quizId, answers)
            if (response.isSuccessful){
                val submitQuiz = response.body()
                submitQuiz?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("submitQuiz Response body is empty")))
            }else{
                val errorMessage = "Failed to submit quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getQuizzesInModule(moduleId: String): Flow<Result<List<Quiz>>> = flow {
        try {
            val response = quizApiService.getQuizzesInModule(moduleId)
            if (response.isSuccessful){
                val quizzes = response.body()
                quizzes?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getQuizzesInModule Response body is empty")))
            }else{
                val errorMessage = "Failed to get quizzes in module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}