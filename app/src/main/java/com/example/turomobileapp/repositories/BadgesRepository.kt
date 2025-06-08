package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.BadgesApiService
import com.example.turomobileapp.models.Badges
import com.example.turomobileapp.models.StudentBadgeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BadgesRepository @Inject constructor(private val badgesApiService: BadgesApiService){

    fun getAllBadgesForStudent(studentId: String): Flow<Result<List<StudentBadgeResponse>>> =
        requestAndMap(
            call = { badgesApiService.getAllBadgesForStudent(studentId) },
            mapper = { dto -> dto.badges }
        )


    fun getAllBadges(): Flow<Result<List<Badges>>> = flow {
        handleApiResponse(
            call = { badgesApiService.getAllBadges() },
            errorMessage = "Failed to get all badges"
        )
    }

    fun getBadge(badgeId: String): Flow<Result<Badges>> = flow {
        handleApiResponse(
            call = { badgesApiService.getBadge(badgeId) },
            errorMessage = "Failed to get badge $badgeId"
        )
    }

    fun assignBadgeToStudent(studentId: String, badgeId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { badgesApiService.assignBadgeToStudent(studentId, badgeId) },
            errorMessage = "Failed to assign badge $badgeId to student $studentId"
        )
    }
}