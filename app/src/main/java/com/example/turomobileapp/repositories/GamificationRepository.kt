package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.GamificationApiService
import com.example.turomobileapp.models.GamifiedElementsResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GamificationRepository @Inject constructor(private val gamificationApiService: GamificationApiService) {

    fun getGamifiedElements(studentId: String): Flow<Result<GamifiedElementsResponse>> =
        handleApiResponse(
            call = { gamificationApiService.getGamifiedElements(studentId) },
            errorMessage = "Failed to get gamified elements"
        )
}