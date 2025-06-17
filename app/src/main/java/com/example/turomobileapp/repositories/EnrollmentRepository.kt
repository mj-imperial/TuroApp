package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.EnrollmentApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EnrollmentRepository @Inject constructor(private val enrollmentApiService: EnrollmentApiService) {

    fun unenrollStudentFromCourse(studentId: String, courseId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.unenrollStudentFromCourse(courseId, studentId) },
            errorMessage = "Failed to unenroll student $studentId from course $courseId"
        )
    }
}