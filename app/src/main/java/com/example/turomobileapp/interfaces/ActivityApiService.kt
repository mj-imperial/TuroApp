package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Query

interface ActivityApiService {
    @DELETE("delete_activity_in_module.php")
    suspend fun deleteActivity(
        @Query("activity_id") activityId: String
    ): Response<ActivityActionResponse>
}