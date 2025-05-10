package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.AssessmentResultApiService
import com.example.turomobileapp.models.AssessmentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssessmentResultRepository @Inject constructor(private val assessmentResultApiService: AssessmentResultApiService) {

    fun saveAssessmentResult(result: AssessmentResult): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.saveAssessmentResult(result) },
            errorMessage = "Failed to save assessment result $result"
        )
    }

    fun getAssessmentResultsForQuizAndStudent(studentId: String, quizId: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = {
                assessmentResultApiService.getAssessmentResultsForQuizAndStudent(
                    studentId,
                    quizId
                )
            },
            errorMessage = "Failed to get assessment result for quiz $quizId and student $studentId"
        )
    }

    fun getAssessmentResultsForStudent(studentId: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.getAssessmentResultsForStudent(studentId) },
            errorMessage = "Failed to get assessment result for student $studentId"
        )
    }

    fun getAssessmentResult(assessmentResultId: String): Flow<Result<AssessmentResult>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.getAssessmentResult(assessmentResultId) },
            errorMessage = "Failed to get assessment result $assessmentResultId"
        )
    }

    fun getAssessmentResultsForStudentAndModule(studentId: String, moduleId: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = {
                assessmentResultApiService.getAssessmentResultsForStudentAndModule(
                    studentId,
                    moduleId
                )
            },
            errorMessage = "Failed to get assessment results for student $studentId and module $moduleId"
        )
    }

    fun getAssessmentResultsBasedOnType(activityType: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.getAssessmentResultsBasedOnType(activityType) },
            errorMessage = "Failed to get assessment result based on type $activityType"
        )
    }

    fun getAssessmentResultsForQuiz(quizId: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.getAssessmentResultsForQuiz(quizId) },
            errorMessage = "Failed to get assessment results for quiz $quizId"
        )
    }

    fun getAssessmentResultsForModule(moduleId: String): Flow<Result<List<AssessmentResult>>> = flow {
        handleApiResponse(
            call = { assessmentResultApiService.getAssessmentResultsForModule(moduleId) },
            errorMessage = "Failed to get assessment results for module $moduleId"
        )
    }
}