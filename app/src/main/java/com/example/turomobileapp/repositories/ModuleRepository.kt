package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ModuleRepository @Inject constructor(private val moduleApiService: ModuleApiService) {

    fun createModule(courseId:String, module: Module): Flow<Result<Module>> = flow {
        handleApiResponse(
            call = { moduleApiService.createModule(courseId, module) },
            errorMessage = "Failed to create module $module for course $courseId"
        )
    }

    fun isModuleDuplicate(courseId: String, module: Module): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { moduleApiService.isModuleDuplicate(courseId, module) },
            errorMessage = "Failed to check is module $module duplication for course $courseId"
        )
    }

    fun getModule(moduleId: String): Flow<Result<Module>> = flow {
        handleApiResponse(
            call = { moduleApiService.getModule(moduleId) },
            errorMessage = "Failed to get module $moduleId"
        )
    }

    fun getAllModules(): Flow<Result<List<Module>>> = flow {
        handleApiResponse(
            call = { moduleApiService.getAllModules() },
            errorMessage = "Failed to get all modules"
        )
    }

    fun getActivitiesForModule(moduleId: String): Flow<Result<List<Activity>>> = flow {
        handleApiResponse(
            call = { moduleApiService.getActivitiesInModule(moduleId) },
            errorMessage = "Failed to get activities for module $moduleId"
        )
    }

    fun updateModule(moduleId: String, module: Module): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { moduleApiService.updateModule(moduleId, module) },
            errorMessage = "Failed to update module $moduleId"
        )
    }

    fun deleteModule(moduleId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { moduleApiService.deleteModule(moduleId) },
            errorMessage = "Failed to delete module $moduleId"
        )
    }

    fun getModulesForCourse(courseId: String): Flow<Result<List<Module>>> = flow {
        handleApiResponse(
            call = { moduleApiService.getModulesForCourse(courseId) },
            errorMessage = "Failed to get modules for course $courseId"
        )
    }
}