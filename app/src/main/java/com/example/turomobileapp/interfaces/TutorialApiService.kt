package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.Tutorial
import com.example.turomobileapp.models.TutorialUploadRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TutorialApiService {
    @POST("create_tutorial.php")
    suspend fun createTutorial(
        @Query("module_id") moduleId: String,
        @Body tutorial: TutorialUploadRequest
    ): Response<ActivityActionResponse>

    @POST("/modules/{moduleId}/tutorials/validateDuplication")
    suspend fun isTutorialDuplicate(
        @Path("moduleId") moduleId: String,
        @Body tutorial: Tutorial
    ): Response<Boolean>

    @GET("/tutorials/{tutorialId}")
    suspend fun getTutorial(
        @Path("tutorialId") tutorialId: String
    ): Response<Tutorial>

    @GET("/modules/{moduleId}/tutorials")
    suspend fun getAllTutorialsForModule(
        @Path("moduleId") moduleId: String
    ): Response<List<Tutorial>>

    @PUT("/tutorials/{tutorialId}")
    suspend fun updateTutorial(
        @Path("tutorialId") tutorialId: String,
        @Body tutorial: Tutorial
    ): Response<ResponseBody>

    @DELETE("/tutorials/{tutorialId}")
    suspend fun deleteTutorial(
        @Path("tutorialId") tutorialId: String
    ): Response<ResponseBody>
}