package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.CoursePicture
import com.example.turomobileapp.models.CourseResponse
import com.example.turomobileapp.models.TeacherCourseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseApiService: CourseApiService) {
    fun getCoursesForStudent(): Flow<Result<List<CourseResponse>>> =
        requestAndMap(
            call = { courseApiService.getCoursesForStudent() },
            mapper = { dto -> dto.courses }
        )

    fun getCoursesForTeacher(teacherId: String): Flow<Result<List<TeacherCourseResponse>>> =
        requestAndMap(
            call = { courseApiService.getCoursesForTeacher(teacherId) },
            mapper = { dto -> dto.courses }
        )

    fun getCoursePicture(): Flow<Result<CoursePicture>> =
        handleApiResponse(
            call = { courseApiService.getCoursePicture() },
            errorMessage = "Failed to get course picture"
        )
}