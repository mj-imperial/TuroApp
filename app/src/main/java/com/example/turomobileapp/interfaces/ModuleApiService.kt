package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivitySectionResponse
import com.example.turomobileapp.models.ModuleActivitiesResponse
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.ModulesResponseStudent
import com.example.turomobileapp.models.TeacherGetModuleResponse
import com.example.turomobileapp.models.TeacherModulesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ModuleApiService {
    @GET("get-modules-for-teacher")
    suspend fun getModulesForCourseTeacher(
        @Query("course_id") courseId: String,
        @Query("section_id") sectionId: String
    ): Response<TeacherModulesResponse>

    @GET("get-course_modules-for-student")
    suspend fun getModulesForCourseStudent(
        @Query("course_id") courseId: String,
        @Query("student_id") studentId: String
    ): Response<ModulesResponseStudent>

    @GET("get-activities-in-module")
    suspend fun getActivitiesInModule(
        @Query("module_id") moduleId: String,
    ): Response<ModuleActivitiesResponse>

    @GET("get-activities-for-teacher")
    suspend fun getActivitiesInModuleForTeacher(
        @Query("module_id") moduleId: String,
        @Query("section_id") sectionId: String
    ): Response<ActivitySectionResponse>

    @DELETE("delete_module_in_course.php")
    suspend fun deleteModule(
        @Query("module_id") moduleId: String
    ): Response<ModuleResultResponse>

    @Multipart
    @POST("create-module")
    suspend fun createModule(
        @Part("course_id") courseId: RequestBody,
        @Part("module_name") moduleName: RequestBody,
        @Part("module_description") moduleDescription: RequestBody,
        @Part imageBlob: MultipartBody.Part?
    ): Response<ModuleResultResponse>

    @Multipart
    @POST("update-module")
    suspend fun updateModule(
        @Part("module_id") moduleId: RequestBody,
        @Part("module_name") moduleName: RequestBody,
        @Part("module_description") moduleDescription: RequestBody,
        @Part imageBlob: MultipartBody.Part?
    ): Response<ModuleResultResponse>

    @GET("get-module")
    suspend fun getModule(
        @Query("module_id") moduleId: String,
    ): Response<TeacherGetModuleResponse>
}