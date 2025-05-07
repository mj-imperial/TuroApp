package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Tutorial
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TutorialApiService {
    @POST("/modules/{moduleId}/tutorials")
    suspend fun createTutorial(
        @Path("moduleId") moduleId: String,
        @Body tutorial: Tutorial
    ): Response<Tutorial>

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