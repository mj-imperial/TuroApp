package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.ActivityApiService
import com.example.turomobileapp.models.ActivityActionResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityRepository @Inject constructor(private val activityApiService: ActivityApiService){

    fun deleteActivity(activityId: String): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { activityApiService.deleteActivity(activityId) },
            errorMessage = "Failed to delete activity $activityId"
        )
}