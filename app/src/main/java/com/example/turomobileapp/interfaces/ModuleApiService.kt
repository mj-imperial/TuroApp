package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ModuleActivitiesResponse
import com.example.turomobileapp.models.ModuleResponseStudent
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.ModuleUpdateRequest
import com.example.turomobileapp.models.ModuleUploadRequest
import com.example.turomobileapp.models.ModulesResponse
import com.example.turomobileapp.models.ModulesResponseStudent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ModuleApiService {
    @GET("get_course_modules_for_teacher.php")
    suspend fun getModulesForCourseTeacher(
        @Query("course_id") courseId: String
    ): Response<ModulesResponse>

    @GET("get_course_modules_for_student.php")
    suspend fun getModulesForCourseStudent(
        @Query("course_id") courseId: String,
        @Query("student_id") studentId: String
    ): Response<ModulesResponseStudent>

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

    @GET("get_current_module_for_student.php")
    suspend fun getCurrentModule(
        @Query("student_id") studentId: String,
        @Query("course_id") courseId: String
    ): Response<ModuleResponseStudent>
}