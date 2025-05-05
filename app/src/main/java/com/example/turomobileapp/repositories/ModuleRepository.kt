package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.ModuleApiService
import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ModuleRepository @Inject constructor(private val moduleApiService: ModuleApiService) {

    fun createModule(courseId:String, module: Module): Flow<Result<Module>> = flow {
        try {
            val response = moduleApiService.createModule(courseId, module)
            if (response.isSuccessful){
                val module = response.body()
                module?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("createModule Response Body is empty")))
            }else{
                val errorMessage = "Failed to create module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isModuleDuplicate(courseId: String, module: Module): Flow<Result<Boolean>> = flow {
        try {
            val response = moduleApiService.isModuleDuplicate(courseId, module)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isModuleDuplicate Response Body is empty")))
            }else{
                val errorMessage = "Failed to check is module duplication: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getModule(moduleId: String): Flow<Result<Module>> = flow {
        try {
            val response = moduleApiService.getModule(moduleId)
            if (response.isSuccessful){
                val module = response.body()
                module?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getModule Response Body is empty")))
            }else{
                val errorMessage = "Failed to get module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllModules(): Flow<Result<List<Module>>> = flow {
        try {
            val response = moduleApiService.getAllModules()
            if (response.isSuccessful){
                val modules = response.body()
                modules?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllModules Response Body is empty")))
            }else{
                val errorMessage = "Failed to get all modules: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getActivitiesForModule(moduleId: String): Flow<Result<List<Activity>>> = flow {
        try {
            val response = moduleApiService.getActivitiesInModule(moduleId)
            if (response.isSuccessful){
                val activities = response.body()
                activities?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getActivitiesForModule Response Body is empty")))
            }else{
                val errorMessage = "Failed to get activities for module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateModule(moduleId: String, module: Module): Flow<Result<Unit>> = flow {
        try {
            val response = moduleApiService.updateModule(moduleId, module)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteModule(moduleId: String): Flow<Result<Unit>> = flow {
        try {
            val response = moduleApiService.deleteModule(moduleId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete module: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getModulesForCourse(courseId: String): Flow<Result<List<Module>>> = flow {
        try {
            val response = moduleApiService.getModulesForCourse(courseId)
            if (response.isSuccessful){
                val modules = response.body()
                modules?.let {
                    emit(Result.Success(it))
                }?: emit(Result.Failure(IOException("getModulesForCourse Response Body is empty")))
            }else{
                val errorMessage = "Failed to get modules for course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}