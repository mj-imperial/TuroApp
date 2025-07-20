package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.models.StudentAnalyticsResponse
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.models.StudentPerformanceListResponses
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class  StudentProgressRepository @Inject constructor(private val studentProgressApiService: StudentProgressApiService) {

    fun getLeaderboardCourse(studentId: String): Flow<Result<List<StudentLeaderboardResponse>>> =
        requestAndMap(
            call = { studentProgressApiService.getLeaderboardCourse(studentId) },
            mapper = { dto -> dto.progresses }
        )

    fun getCourseProgressForAllStudents(courseId: String): Flow<Result<StudentPerformanceListResponses>> =
        handleApiResponse(
            call = { studentProgressApiService.getCourseProgressForAllStudents(courseId) },
            errorMessage = "Failed to get course $courseId progress"
        )

    fun getStudentAnalysis(studentId: String, courseId: String): Flow<Result<StudentAnalyticsResponse>> =
        handleApiResponse(
            call = { studentProgressApiService.getStudentAnalysis(studentId, courseId) },
            errorMessage = "Failed to get student progress"
        )
}