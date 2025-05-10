package com.example.turomobileapp.helperfunctions

import com.example.turomobileapp.repositories.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

fun <Dto : Any, Domain : Any> requestAndMap(
    call: suspend () -> Response<Dto>,
    mapper: (Dto) -> Domain
): Flow<Result<Domain>> = flow {
    try {
        val response = call()
        if (response.isSuccessful) {
            val dto = response.body()
            if (dto != null) {
                emit((Result.Success(mapper(dto))))
            } else {
                emit(Result.Failure(IOException("Empty response body")))
            }
        } else {
            val msg = "Error ${response.code()}: ${response.errorBody()?.string() ?: "Unknown"}"
            emit(Result.Failure(IOException(msg)))
        }
    } catch (e: IOException) {
        emit(Result.Failure(e))
    } catch (e: Exception) {
        emit(Result.Failure(e))
    }
}