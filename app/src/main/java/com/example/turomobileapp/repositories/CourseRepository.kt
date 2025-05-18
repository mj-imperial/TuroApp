package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.Course
import com.example.turomobileapp.models.CourseResponse
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

    fun getCoursesForStudent(userId: String): Flow<Result<List<CourseResponse>>> =
        requestAndMap(
            call = { courseApiService.getCoursesForStudent(action = "studentCourses", userId = userId) },
            mapper = { dto -> dto.courses }
        )

    fun getCoursesForTeacher(userId: String): Flow<Result<List<CourseResponse>>> =
        requestAndMap(
            call = { courseApiService.getCoursesForTeacher(action = "teacherCourses", userId = userId) },
            mapper = { dto -> dto.courses }
        )
}