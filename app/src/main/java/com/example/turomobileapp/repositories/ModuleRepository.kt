package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.models.Module
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.models.ModuleResultResponse
import com.example.turomobileapp.models.ModuleUpdateRequest
import com.example.turomobileapp.models.ModuleUploadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ModuleRepository @Inject constructor(private val moduleApiService: ModuleApiService) {

    fun createModule(courseId:String, moduleName: String, moduleDescription: String): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.createModule(ModuleUploadRequest(courseId, moduleName, moduleDescription)) },
            errorMessage = "Failed to create module"
        )

    fun getModulesForCourse(courseId: String): Flow<Result<List<ModuleResponse>>> =
        requestAndMap(
            call = { moduleApiService.getModulesForCourse(courseId) },
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

    fun getModule(moduleId: String): Flow<Result<ModuleUploadRequest>> =
        handleApiResponse(
            call = { moduleApiService.getModule(moduleId) },
            errorMessage = "Failed to get module $moduleId"
        )

    fun updateModule(moduleId: String, module: ModuleUpdateRequest): Flow<Result<ModuleResultResponse>> =
        handleApiResponse(
            call = { moduleApiService.updateModule(moduleId, module) },
            errorMessage = "Failed to update module $moduleId"
        )

    fun getAllModules(): Flow<Result<List<Module>>> = flow {
        handleApiResponse(
            call = { moduleApiService.getAllModules() },
            errorMessage = "Failed to get all modules"
        )
    }

}