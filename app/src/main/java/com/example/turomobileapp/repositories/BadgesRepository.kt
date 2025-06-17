package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.BadgesApiService
import com.example.turomobileapp.models.StudentBadgeResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BadgesRepository @Inject constructor(private val badgesApiService: BadgesApiService){

    fun getAllBadgesForStudent(studentId: String): Flow<Result<List<StudentBadgeResponse>>> =
        requestAndMap(
            call = { badgesApiService.getAllBadgesForStudent(studentId) },
            mapper = { dto -> dto.badges }
        )

}