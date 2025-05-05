package com.example.turoapp.repositories

import com.example.turoapp.interfaces.BadgesApiService
import com.example.turoapp.models.Badges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class BadgesRepository @Inject constructor(private val badgesApiService: BadgesApiService){

    fun getAllBadges(): Flow<Result<List<Badges>>> = flow {
        try {
            val response = badgesApiService.getAllBadges()
            if (response.isSuccessful){
                val badges = response.body()
                badges?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllBadges Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all badges: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getBadge(badgeId: String): Flow<Result<Badges>> = flow {
        try {
            val response = badgesApiService.getBadge(badgeId)
            if (response.isSuccessful){
                val badge = response.body()
                badge?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getBadge Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get badge: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllBadgesForStudent(studentId: String): Flow<Result<List<Badges>>> = flow {
        try {
            val response = badgesApiService.getAllBadgesForStudent(studentId)
            if (response.isSuccessful){
                val badges = response.body()
                badges?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllBadgesForStudent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all badges for student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun assignBadgeToStudent(studentId: String, badgeId: String): Flow<Result<Unit>> = flow {
        try {
            val response = badgesApiService.assignBadgeToStudent(studentId, badgeId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to assign badge to student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
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