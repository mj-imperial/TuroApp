package com.example.turoapp.interfaces

import com.example.turoapp.models.ModuleProgress
import com.example.turoapp.models.UpdateModuleProgressRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ModuleProgressApiService {
    @GET("/students/{studentId}/modules/{moduleId}/progress")
    suspend fun getModuleProgress(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String
    ): Response<ModuleProgress>

    @GET("/students/{studentId}/modules")
    suspend fun getAllModuleProgressForStudent(
        @Path("studentId") studentId: String
    ): Response<List<ModuleProgress>>

    @PUT("/students/{studentId}/modules/{moduleId}/progress")
    suspend fun updateModuleProgress(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String,
        @Body request: UpdateModuleProgressRequest // Use the new request class
    ): Response<ResponseBody>

    @PUT("/students/{studentId}/modules/{moduleId}/progress/completed")
    suspend fun updateIsCompleted(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String,
        @Query("isCompleted") isCompleted: Boolean
    ): Response<ResponseBody>

    @PUT("/students/{studentId}/modules/{moduleId}/progress/tier-passed")
    suspend fun updateTierPassed(
        @Path("studentId") studentId: String,
        @Path("moduleId") moduleId: String,
        @Query("tierPassed") tierPassed: String?
    ): Response<ResponseBody>
}