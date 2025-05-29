package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.AssessmentResultApiService
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResult
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentScoreResponse
import com.example.turomobileapp.models.AssessmentScoresResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssessmentResultRepository @Inject constructor(private val assessmentResultApiService: AssessmentResultApiService) {

    fun saveAssessmentResult(studentId: String, activityId: String, scorePercentage: Double, earnedPoints: Int, answers: List<AnswerUploadRequest>): Flow<Result<AssessmentResultUploadResponse>> =
        handleApiResponse(
            call = { assessmentResultApiService.saveAssessmentResult(AssessmentResultUploadRequest(
                studentId = studentId,
                activityId = activityId,
                scorePercentage = scorePercentage,
                earnedPoints = earnedPoints,
                answers = answers,
            )) },
            errorMessage = "Failed to save assessment result"
        )

    fun getAssessmentResultsForQuizAndStudent(studentId: String, activityId: String): Flow<Result<List<AssessmentResultResponse>>> =
        requestAndMap(
            call = { assessmentResultApiService.getAssessmentResultsForActivityAndStudent(studentId,activityId) },
            mapper = { dto -> dto.results }
        )

    fun getScoresForStudentAndQuiz(studentId: String, activityId: String): Flow<Result<List<AssessmentScoreResponse>>> =
        requestAndMap(
            call = { assessmentResultApiService.getScoresForStudentAndQuiz(studentId, activityId) },
            mapper = { dto -> dto.scores }
        )


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