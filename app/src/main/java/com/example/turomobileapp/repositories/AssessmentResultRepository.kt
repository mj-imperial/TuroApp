package com.example.turoapp.repositories

import com.example.turoapp.interfaces.AssessmentResultApiService
import com.example.turoapp.models.AssessmentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class AssessmentResultRepository @Inject constructor(private val assessmentResultApiService: AssessmentResultApiService) {

    fun saveAssessmentResult(result: AssessmentResult): Flow<Result<Unit>> = flow {
        try {
            val response = assessmentResultApiService.saveAssessmentResult(result)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to save assessment result: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsForQuizAndStudent(studentId: String, quizId: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsForQuizAndStudent(studentId, quizId)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsForQuizAndStudent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment result for quiz and student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsForStudent(studentId: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsForStudent(studentId)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsForStudent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment result for student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResult(assessmentResultId: String): Flow<Result<AssessmentResult>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResult(assessmentResultId)
            if (response.isSuccessful){
                val assessmentResult = response.body()
                assessmentResult?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResult Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment result: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsForStudentAndModule(studentId: String, moduleId: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsForStudentAndModule(studentId, moduleId)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsForStudentAndModule Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment results for student and module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsBasedOnType(activityType: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsBasedOnType(activityType)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsBasedOnType Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment result based on type: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsForQuiz(quizId: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsForQuiz(quizId)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsForQuiz Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment results for quiz: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAssessmentResultsForModule(moduleId: String): Flow<Result<List<AssessmentResult>>> = flow {
        try {
            val response = assessmentResultApiService.getAssessmentResultsForModule(moduleId)
            if (response.isSuccessful){
                val assessmentResults = response.body()
                assessmentResults?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAssessmentResultsForModule Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get assessment results for module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}