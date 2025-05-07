package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.EnrollmentApiService
import com.example.turomobileapp.models.Course
import com.example.turomobileapp.models.Enrollment
import com.example.turomobileapp.models.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EnrollmentRepository @Inject constructor(private val enrollmentApiService: EnrollmentApiService) {

    fun enrollStudentInCourse(enrollment: Enrollment, studentId: String, courseId: String): Flow<Result<Enrollment>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.enrollStudentInCourse(enrollment, studentId, courseId) },
            errorMessage = "Failed to enroll student $studentId in course $courseId"
        )
    }

    fun getEnrollment(enrollmentId: String): Flow<Result<Enrollment>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.getEnrollmentId(enrollmentId) },
            errorMessage = "Failed to get enrollment $enrollmentId"
        )
    }

    fun getCoursesForStudent(studentId: String): Flow<Result<List<Course>>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.getCoursesForStudent(studentId) },
            errorMessage = "Failed to get courses for student $studentId"
        )
    }

    fun getStudentsInCourse(courseId: String): Flow<Result<List<Student>>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.getStudentsinCourse(courseId) },
            errorMessage = "Failed to get students in course $courseId"
        )
    }

    fun unenrollStudentFromCourse(studentId: String, courseId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { enrollmentApiService.unenrollStudentFromCourse(courseId, studentId) },
            errorMessage = "Failed to unenroll student $studentId from course $courseId"
        )
    }
}