package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.ActivityApiService
import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.helperfunctions.handleApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ActivityRepository @Inject constructor(private val activityApiService: ActivityApiService){

    fun createActivity(moduleId: String, activity: Activity): Flow<Result<Activity>> = flow {
        handleApiResponse(
            call = { activityApiService.createActivity(moduleId, activity) },
            errorMessage = "Failed to create activity $activity in $moduleId"
        )
    }

    fun getActivity(activityId: String): Flow<Result<Activity>> = flow {
        handleApiResponse(
            call = { activityApiService.getActivity(activityId) },
            errorMessage = "Failed to get activity $activityId"
        )
    }

    fun getAllActivities(moduleId: String): Flow<Result<List<Activity>>> = flow {
        handleApiResponse(
            call = { activityApiService.getAllActivities(moduleId) },
            errorMessage = "Failed to get all activities in $moduleId"
        )
    }

    fun updateActivity(activityId: String, activity: Activity): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { activityApiService.updateActivity(activityId, activity) },
            errorMessage = "Failed to update activity $activityId"
        )
    }

    fun deleteActivity(activityId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { activityApiService.deleteActivity(activityId) },
            errorMessage = "Failed to delete activity $activityId"
        )
    }
}