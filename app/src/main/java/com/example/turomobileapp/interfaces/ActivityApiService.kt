package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.ActivityActionResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityApiService {
    @POST("/modules/{moduleId}/activities")
    suspend fun createActivity(
        @Path("moduleId") moduleId: String,
        @Body activity: Activity
    ): Response<Activity>

    @GET("/activities/{activityId}")
    suspend fun getActivity(
        @Path("activityId") activityId: String
    ): Response<Activity>

    @GET("/modules/{moduleId}/activities")
    suspend fun getAllActivities(
        @Path("moduleId") moduleId: String
    ): Response<List<Activity>>

    @PUT("/activities/{activityId}")
    suspend fun updateActivity(
        @Path("activityId") activityId: String,
        @Body activity: Activity
    ): Response<ResponseBody>

    @DELETE("delete_activity_in_module.php")
    suspend fun deleteActivity(
        @Query("activity_id") activityId: String
    ): Response<ActivityActionResponse>
}