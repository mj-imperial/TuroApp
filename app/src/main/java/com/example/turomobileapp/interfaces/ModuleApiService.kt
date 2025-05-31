package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.Module
import com.example.turomobileapp.models.ModuleResultUploadResponse
import com.example.turomobileapp.models.ModuleUploadRequest
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

    @POST("create_module.php")
    suspend fun createModule(
        @Body request: ModuleUploadRequest
    ): Response<ModuleResultUploadResponse>

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