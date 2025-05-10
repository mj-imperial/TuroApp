package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.models.StudentProgress
import com.example.turomobileapp.models.UpdateStudentModuleProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class  StudentProgressRepository @Inject constructor(private val studentProgressApiService: StudentProgressApiService) {

    fun getAllStudentCourseProgress(studentId: String): Flow<Result<List<StudentProgress>>> = flow {
        handleApiResponse(
            call = { studentProgressApiService.getAllStudentCourseProgress(studentId) },
            errorMessage = "Failed to get all student course progress for student $studentId"
        )
    }

    fun getStudentCourseProgress(studentId: String, courseId: String): Flow<Result<StudentProgress>> = flow {
        handleApiResponse(
            call = { studentProgressApiService.getStudentCourseProgress(studentId, courseId) },
            errorMessage = "Failed to get student $studentId course $courseId progress"
        )
    }

    fun getCourseProgressForAllStudents(courseId: String): Flow<Result<List<StudentProgress>>> = flow {
        handleApiResponse(
            call = { studentProgressApiService.getCourseProgressForAllStudents(courseId) },
            errorMessage = "Failed to get all course $courseId progress for all students"
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