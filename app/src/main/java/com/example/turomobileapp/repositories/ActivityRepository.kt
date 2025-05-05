package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.ActivityApiService
import com.example.turomobileapp.models.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ActivityRepository @Inject constructor(private val activityApiService: ActivityApiService){

    fun createActivity(moduleId: String, activity: Activity): Flow<Result<Activity>> = flow {
        try {
            val response = activityApiService.createActivity(moduleId, activity)
            if (response.isSuccessful) {
                val activity = response.body()
                activity?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("createActivity Response Body is Empty")))
            } else{
                val errorMessage = "Failed to create activity: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getActivity(activityId: String): Flow<Result<Activity>> = flow {
        try {
            val response = activityApiService.getActivity(activityId)
            if (response.isSuccessful){
                val activity = response.body()
                activity?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getActivity Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get activity: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllActivities(moduleId: String): Flow<Result<List<Activity>>> = flow {
        try {
            val response = activityApiService.getAllActivities(moduleId)
            if (response.isSuccessful){
                val activities = response.body()
                activities?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllActivities Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all activities: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateActivity(activityId: String, activity: Activity): Flow<Result<Unit>> = flow {
        try {
            val response = activityApiService.updateActivity(activityId, activity)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update activity: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteActivity(activityId: String): Flow<Result<Unit>> = flow {
        try {
            val response = activityApiService.deleteActivity(activityId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete activity: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}