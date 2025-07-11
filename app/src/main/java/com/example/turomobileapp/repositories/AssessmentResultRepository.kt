package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.AssessmentResultApiService
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentScoreResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssessmentResultRepository @Inject constructor(private val assessmentResultApiService: AssessmentResultApiService) {

    fun saveAssessmentResult(moduleId: String, studentId: String, activityId: String, scorePercentage: Double, earnedPoints: Int, answers: List<AnswerUploadRequest>): Flow<Result<AssessmentResultUploadResponse>> =
        handleApiResponse(
            call = { assessmentResultApiService.saveAssessmentResult(
                moduleId = moduleId,
                AssessmentResultUploadRequest(
                    studentId = studentId,
                    activityId = activityId,
                    scorePercentage = scorePercentage,
                    earnedPoints = earnedPoints,
                    answers = answers
                )
            ) },
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
}