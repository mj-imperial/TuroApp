package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.ScreeningExamApiService
import com.example.turomobileapp.models.Answers
import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.Question
import com.example.turomobileapp.models.ScreeningExam
import com.example.turomobileapp.helperfunctions.handleApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ScreeningExamRepository @Inject constructor(private val screeningExamApiService: ScreeningExamApiService){

    fun createScreeningExam(moduleId: String, screeningExam: ScreeningExam): Flow<Result<ScreeningExam>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.createScreeningExam(moduleId, screeningExam) },
            errorMessage = "Failed to create screening exam $screeningExam for $moduleId"
        )
    }

    fun isScreeningExamDuplicate(moduleId: String, screeningExam: ScreeningExam): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.isScreeningExamDuplicate(moduleId, screeningExam) },
            errorMessage = "Failed to check screening exam $screeningExam duplication"
        )
    }

    fun getScreeningExam(screeningExamId: String): Flow<Result<ScreeningExam>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.getScreeningExam(screeningExamId) },
            errorMessage = "Failed to get screening exam $screeningExamId"
        )
    }

    fun getAllScreeningExamsForModule(moduleId: String): Flow<Result<List<ScreeningExam>>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.getAllScreeningExamsForModule(moduleId) },
            errorMessage = "Failed to get screening exams for module $moduleId"
        )
    }

    fun getQuestionsForScreeningExam(screeningExamId: String): Flow<Result<List<Question>>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.getQuestionsForScreeningExam(screeningExamId) },
            errorMessage = "Failed to get questions for screening exam $screeningExamId"
        )
    }

    fun updateScreeningExam(screeningExamId: String, screeningExam: ScreeningExam): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { screeningExamApiService.updateScreeningExam(screeningExamId, screeningExam) },
            errorMessage = "Failed to update screening exam $screeningExamId"
        )
    }

    fun deleteScreeningExam(screeningExamId: String): Flow<Result<Unit>> = flow {
        try {
            val response = screeningExamApiService.deleteScreeningExam(screeningExamId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete screening exam: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun submitScreeningExam(studentId: String, screeningExamId: String, answers: List<Answers>): Flow<Result<AssessmentResult>> = flow {
        handleApiResponse(
            call = {
                screeningExamApiService.submitScreeningExam(
                    studentId,
                    screeningExamId,
                    answers
                )
            },
            errorMessage = "Failed to submit screening exam $screeningExamId"
        )
    }
}