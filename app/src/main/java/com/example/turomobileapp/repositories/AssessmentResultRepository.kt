package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.AssessmentResultApiService
import com.example.turomobileapp.models.AnswerUploadRequest
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.AssessmentResultUploadRequest
import com.example.turomobileapp.models.AssessmentResultUploadResponse
import com.example.turomobileapp.models.AssessmentScoreResponse
import com.example.turomobileapp.models.LongQuizAssessmentResultResponse
import com.example.turomobileapp.models.LongQuizAssessmentResultUploadRequest
import com.example.turomobileapp.models.ScreeningExamAssessmentResult
import com.example.turomobileapp.models.ScreeningExamAssessmentUpload
import com.example.turomobileapp.models.ScreeningExamResultsResponse
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

    fun getLongQuizResultsForStudent(longQuizId: String, studentId: String): Flow<Result<List<LongQuizAssessmentResultResponse>>> =
        requestAndMap(
            call = { assessmentResultApiService.getLongQuizResultsForStudent(longQuizId, studentId) },
            mapper = { dto -> dto.results }
        )

    fun saveLongQuizAssessmentResult(studentId: String, courseId: String, longQuizId: String, scorePercentage: Double, earnedPoints: Int, answers: List<AnswerUploadRequest>): Flow<Result<AssessmentResultUploadResponse>> =
        handleApiResponse(
            call = { assessmentResultApiService.saveLongQuizAssessmentResult(
                LongQuizAssessmentResultUploadRequest(
                    studentId = studentId,
                    courseId = courseId,
                    longQuizId = longQuizId,
                    scorePercentage = scorePercentage,
                    earnedPoints = earnedPoints,
                    answers = answers
                )
            ) },
            errorMessage = "Failed to save assessment result"
        )

    fun getScreeningExamAssessmentResultForStudent(studentId: String, screeningId: String): Flow<Result<List<ScreeningExamAssessmentResult>>> =
        requestAndMap(
            call = { assessmentResultApiService.getScreeningExamAssessmentResultForStudent(studentId, screeningId) },
            mapper = { dto -> listOf(dto.result) }
        )

    fun saveScreeningExamResult(studentId: String, screeningExamId: String, scorePercentage: Double, earnedPoints: Int, answers: List<AnswerUploadRequest>): Flow<Result<AssessmentResultUploadResponse>> =
        handleApiResponse(
            call = { assessmentResultApiService.saveScreeningExamResult(
                ScreeningExamAssessmentUpload(
                    studentId = studentId,
                    screeningExamId = screeningExamId,
                    scorePercentage = scorePercentage,
                    earnedPoints = earnedPoints,
                    answers = answers
                )
            ) },
            errorMessage = "Failed to save assessment results"
        )

    fun getScreeningExamResult(studentId: String, screeningExamId: String): Flow<Result<ScreeningExamResultsResponse>> =
        handleApiResponse(
            call = { assessmentResultApiService.getScreeningExamResult(studentId, screeningExamId) },
            errorMessage = "Failed to get Screening Exam result"
        )
}