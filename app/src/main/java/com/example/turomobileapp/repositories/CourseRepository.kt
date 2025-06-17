package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.CourseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseApiService: CourseApiService) {
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