package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.models.ModuleResponseStudent
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.ModuleUpdateRequest
import com.example.turomobileapp.models.ModuleUploadRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ModuleRepository @Inject constructor(private val moduleApiService: ModuleApiService) {

    fun createModule(courseId:String, request: ModuleUploadRequest): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.createModule(courseId, request) },
            errorMessage = "Failed to create module"
        )

    fun getModulesForCourseTeacher(courseId: String): Flow<Result<List<ModuleResponse>>> =
        requestAndMap(
            call = { moduleApiService.getModulesForCourseTeacher(courseId) },
            mapper = { dto -> dto.modules }
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

    fun getModule(moduleId: String, courseId: String): Flow<Result<ModuleResponse>> =
        handleApiResponse(
            call = { moduleApiService.getModule(moduleId, courseId) },
            errorMessage = "Failed to get module $moduleId"
        )

    fun updateModule(moduleId: String, module: ModuleUpdateRequest): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.updateModule(moduleId, module) },
            errorMessage = "Failed to update module $moduleId"
        )

    fun getCurrentModule(studentId: String, courseId: String): Flow<Result<ModuleResponseStudent>> =
        handleApiResponse(
            call = { moduleApiService.getCurrentModule(studentId, courseId) },
            errorMessage = "Failed to get current module for $studentId"
        )

}