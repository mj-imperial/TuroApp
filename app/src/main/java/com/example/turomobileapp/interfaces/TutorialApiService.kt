package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.TutorialResponse
import com.example.turomobileapp.models.TutorialUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TutorialApiService {
    @POST("create_tutorial.php")
    suspend fun createTutorial(
        @Query("module_id") moduleId: String,
        @Body tutorial: TutorialUploadRequest
    ): Response<ActivityActionResponse>

    @GET("get_tutorial.php")
    suspend fun getTutorial(
        @Query("activity_id") tutorialId: String
    ): Response<TutorialResponse>

    @POST("update_tutorial.php")
    suspend fun updateTutorial(
        @Query("activity_id") activityId: String,
        @Query("module_id") moduleId: String,
        @Body tutorial: TutorialResponse
    ): Response<ActivityActionResponse>
}