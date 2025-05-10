package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseApiService: CourseApiService) {

    fun getAllCourses(): Flow<Result<List<Course>>> = flow {
        handleApiResponse(
            call = { courseApiService.getAllCourses() },
            errorMessage = "Failed to get all courses"
        )
    }

    fun getCourse(courseId: String): Flow<Result<Course>> = flow {
        handleApiResponse(
            call = { courseApiService.getCourse(courseId) },
            errorMessage = "Failed to get course $courseId"
        )
    }

    fun createCourse(course: Course): Flow<Result<Course>> = flow {
        handleApiResponse(
            call = { courseApiService.createCourse(course) },
            errorMessage = "Failed to create course $course"
        )
    }

    fun updateCourse(courseId: String, course: Course): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { courseApiService.updateCourse(courseId, course) },
            errorMessage = "Failed to update course $courseId"
        )
    }

    fun deleteCourse(courseId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { courseApiService.deleteCourse(courseId) },
            errorMessage = "Failed to delete course $courseId"
        )
    }

    fun isCourseDuplicate(course: Course): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { courseApiService.isCourseDuplicate(course) },
            errorMessage = "Failed to check course $course duplicate"
        )
    }

    fun getCoursesForStudent(studentId: String): Flow<Result<List<Course>>> = flow {
        handleApiResponse(
            call = { courseApiService.getCoursesForStudent(studentId) },
            errorMessage = "Failed to get courses for student $studentId"
        )
    }
}