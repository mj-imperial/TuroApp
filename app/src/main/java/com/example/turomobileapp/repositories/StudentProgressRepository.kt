package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.models.StudentLeaderboardResponse
import com.example.turomobileapp.models.StudentPerformanceListResponses
import com.example.turomobileapp.models.StudentProgress
import com.example.turomobileapp.models.UpdateStudentModuleProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun getStudentCourseProgress(studentId: String, courseId: String): Flow<Result<StudentProgress>> = flow {
        handleApiResponse(
            call = { studentProgressApiService.getStudentCourseProgress(studentId, courseId) },
            errorMessage = "Failed to get student $studentId course $courseId progress"
        )
    }

    fun updateStudentProgress(studentId: String, moduleId: String, request: UpdateStudentModuleProgressRequest): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = {
                studentProgressApiService.updateStudentModuleProgress(
                    studentId,
                    moduleId,
                    request
                )
            },
            errorMessage = "Failed to update student $studentId progress"
        )
    }
}