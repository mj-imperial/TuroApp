package com.example.turoapp.repositories

import com.example.turoapp.interfaces.OptionsApiService
import com.example.turoapp.models.Options
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class OptionsRepository @Inject constructor(private val optionsApiService: OptionsApiService) {

    fun createOption(questionId: String, options: Options): Flow<Result<Options>> = flow {
        try {
            val response = optionsApiService.createOption(questionId, options)
            if (response.isSuccessful){
                val option = response.body()
                option?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("createOption Response Body is empty")))
            }else{
                val errorMessage = "Failed to create option: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isOptionDuplicate(questionId: String, options: Options): Flow<Result<Boolean>> = flow {
        try {
            val response = optionsApiService.isOptionDuplicate(questionId, options)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isOptionDuplicate Response Body is empty")))
            }else{
                val errorMessage = "Failed to check option duplication: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getOption(questionId: String, optionId: String): Flow<Result<Options>> = flow {
        try {
            val response = optionsApiService.getOption(questionId, optionId)
            if (response.isSuccessful){
                val option = response.body()
                option?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getOption Response Body is empty")))
            }else{
                val errorMessage = "Failed to get option: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllOptionsForQuestion(questionId: String): Flow<Result<List<Options>>> = flow {
        try {
            val response = optionsApiService.getAllOptionsForQuestion(questionId)
            if (response.isSuccessful){
                val options = response.body()
                options?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllOptionsForQuestion Response Body is empty")))
            }else{
                val errorMessage = "Failed to get all options for question: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateOption(questionId: String, optionId: String, options: Options): Flow<Result<Unit>> = flow {
        try {
            val response = optionsApiService.updateOption(questionId, optionId, options)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update option: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteOption(questionId: String, optionId: String): Flow<Result<Unit>> = flow {
        try {
            val response = optionsApiService.deleteOption(questionId, optionId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete option: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
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