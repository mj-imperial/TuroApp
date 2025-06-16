package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Module
import com.example.turomobileapp.models.ModuleActivitiesResponse
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.ModuleUpdateRequest
import com.example.turomobileapp.models.ModuleUploadRequest
import com.example.turomobileapp.models.ModulesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ModuleApiService {
    @GET("/modulesScores")
    suspend fun getAllModules(): Response<List<Module>>

    @GET("get_course_modules.php")
    suspend fun getModulesForCourse(
        @Query("course_id") courseId: String
    ): Response<ModulesResponse>

    @GET("get_activities_in_module.php")
    suspend fun getActivitiesInModule(
        @Query("module_id") moduleId: String,
    ): Response<ModuleActivitiesResponse>

    @DELETE("delete_module_in_course.php")
    suspend fun deleteModule(
        @Query("module_id") moduleId: String
    ): Response<ModuleResultResponse>

    @POST("create_module.php")
    suspend fun createModule(
        @Body request: ModuleUploadRequest
    ): Response<ModuleResultResponse>

    @GET("get_module.php")
    suspend fun getModule(
        @Query("module_id") moduleId: String
    ): Response<ModuleUploadRequest>

    @POST("update_module.php")
    suspend fun updateModule(
        @Query("module_id") moduleId: String,
        @Body module: ModuleUpdateRequest
    ): Response<ModuleResultResponse>
}