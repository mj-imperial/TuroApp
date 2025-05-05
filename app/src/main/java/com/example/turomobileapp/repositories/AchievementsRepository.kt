package com.example.turoapp.repositories

import com.example.turoapp.interfaces.AchievementsApiService
import com.example.turoapp.models.Achievements
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class AchievementsRepository @Inject constructor(private val achievementsApiService: AchievementsApiService) {

    fun getAllAchievements(): Flow<Result<List<Achievements>>> = flow {
        try {
            val response = achievementsApiService.getAllAchievements()
            if (response.isSuccessful){
                val achievements = response.body()
                achievements?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllAchievements Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all achievements: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAchievement(achievementId: String): Flow<Result<Achievements>> = flow {
        try {
            val response = achievementsApiService.getAchievement(achievementId)
            if (response.isSuccessful){
                val achievement = response.body()
                achievement?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAchievement Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get achievement: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllAchievementsForStudent(studentId: String): Flow<Result<List<Achievements>>> = flow {
        try {
            val response = achievementsApiService.getAllAchievementsForStudent(studentId)
            if (response.isSuccessful){
                val achievements = response.body()
                achievements?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllAchievementsForStudent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all achievements for student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun assignAchievementToStudent(studentId: String, achievementId: String): Flow<Result<Unit>> = flow {
        try {
            val response = achievementsApiService.assignAchievementToStudent(studentId, achievementId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to get assign achievement for student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
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