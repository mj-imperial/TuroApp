package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.models.StudentPerformanceListResponses
import com.example.turomobileapp.models.StudentPerformanceModuleList
import com.example.turomobileapp.models.StudentPerformanceResponse
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

    fun getIndividualStudentCourseProgress(studentId: String, courseId: String): Flow<Result<List<StudentPerformanceModuleList>>> =
        requestAndMap(
            call = { studentProgressApiService.getIndividualStudentCourseProgress(studentId, courseId) },
            mapper = { dto -> dto.modules }
        )

    fun getIndividualStudentPerformanceList(studentId: String, courseId: String): Flow<Result<StudentPerformanceResponse>> =
        handleApiResponse(
            call = { studentProgressApiService.getIndividualStudentPerformanceList(studentId, courseId) },
            errorMessage = "Failed to get individual student performance list for $studentId"
        )
}