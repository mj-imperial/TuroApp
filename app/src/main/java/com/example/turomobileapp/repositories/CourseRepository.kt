package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.CoursePicture
import com.example.turomobileapp.models.CourseResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseApiService: CourseApiService) {
    fun getCoursesForUser(): Flow<Result<List<CourseResponse>>> =
        requestAndMap(
            call = { courseApiService.getCoursesForUser() },
            mapper = { dto -> dto.courses }
        )

    fun getCoursePicture(): Flow<Result<CoursePicture>> =
        handleApiResponse(
            call = { courseApiService.getCoursePicture() },
            errorMessage = "Failed to get course picture"
        )
}