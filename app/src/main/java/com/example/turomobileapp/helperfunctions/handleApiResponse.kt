package com.example.turomobileapp.helperfunctions

import com.example.turomobileapp.repositories.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

fun <T> handleApiResponse(call: suspend () -> Response<T>, errorMessage: String): Flow<Result<T>> = flow {
    try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            body?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Failure(IOException("$errorMessage Response Body is Empty")))
        } else {
            val error = IOException("$errorMessage: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}")
            emit(Result.Failure(error))
        }
    } catch (e: IOException) {
        emit(Result.Failure(e))
    } catch (e: Exception) {
        emit(Result.Failure(e))
    }
}