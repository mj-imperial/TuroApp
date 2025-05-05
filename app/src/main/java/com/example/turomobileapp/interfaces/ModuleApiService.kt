package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.Module
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ModuleApiService {
    @GET("/modules")
    suspend fun getAllModules(): Response<List<Module>>

    @GET("/courses/{courseId}/modules")
    suspend fun getModulesForCourse(
        @Path("courseId") courseId: String
    ): Response<List<Module>>

    @GET("/modules/{moduleId}")
    suspend fun getModule(
        @Path("moduleId") moduleId: String
    ): Response<Module>

    @GET("/modules/{moduleId}/activities")
    suspend fun getActivitiesInModule(
        @Path("moduleId") moduleId: String
    ): Response<List<Activity>>

    @POST("/course/{courseId}/modules")
    suspend fun createModule(
        @Path("courseId") courseId: String,
        @Body module: Module
    ): Response<Module>

    @POST("/course/{courseId}/modules/validateDuplication")
    suspend fun isModuleDuplicate(
        @Path("courseId") courseId: String,
        @Body module: Module
    ): Response<Boolean>

    @PUT("/modules/{moduleId}")
    suspend fun updateModule(
        @Path("moduleId") moduleId: String,
        @Body module: Module
    ): Response<ResponseBody>

    @DELETE("/modules/{moduleId}")
    suspend fun deleteModule(
        @Path("moduleId") moduleId: String,
    ): Response<ResponseBody>
}