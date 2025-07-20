package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.models.ActivityItem
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.models.ModuleResponseStudent
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.TeacherGetModuleResponse
import com.example.turomobileapp.models.TeacherModuleResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ModuleRepository @Inject constructor(private val moduleApiService: ModuleApiService) {

    fun getModulesForCourseTeacher(courseId: String, sectionId: String): Flow<Result<List<TeacherModuleResponse>>> =
        requestAndMap(
            call = { moduleApiService.getModulesForCourseTeacher(courseId, sectionId) },
            mapper = { dto -> dto.modules }
        )

    fun getActivitiesInModuleForTeacher(moduleId: String, sectionId: String): Flow<Result<List<ActivityItem>>> =
        requestAndMap(
            call = { moduleApiService.getActivitiesInModuleForTeacher(moduleId, sectionId) },
            mapper = { dto -> dto.data }
        )

    fun createModule(courseId: RequestBody, moduleName: RequestBody, moduleDescription: RequestBody, imageBlob: MultipartBody.Part?): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.createModule(courseId, moduleName, moduleDescription, imageBlob) },
            errorMessage = "Failed to create module"
        )

    fun updateModule(moduleId: RequestBody, moduleName: RequestBody, moduleDescription: RequestBody, imageBlob: MultipartBody.Part?): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.updateModule(moduleId, moduleName, moduleDescription, imageBlob) },
            errorMessage = "Failed to update module $moduleId"
        )

    fun getModulesForCourseStudent(courseId: String, studentId: String): Flow<Result<List<ModuleResponseStudent>>> =
        requestAndMap(
            call = { moduleApiService.getModulesForCourseStudent(courseId, studentId) },
            mapper = { dto -> dto.modules }
        )

    fun getActivitiesForModule(moduleId: String): Flow<Result<List<ModuleActivityResponse>>> =
        requestAndMap(
            call = { moduleApiService.getActivitiesInModule(moduleId) },
            mapper = { dto -> dto.activities }
        )

    fun deleteModule(moduleId: String): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.deleteModule(moduleId) },
            errorMessage = "Failed to delete module $moduleId"
        )

    fun getModule(moduleId: String): Flow<Result<TeacherGetModuleResponse>> =
        handleApiResponse(
            call = { moduleApiService.getModule(moduleId) },
            errorMessage = "Failed to get module $moduleId"
        )
}