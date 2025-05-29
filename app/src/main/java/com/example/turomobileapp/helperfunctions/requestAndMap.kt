package com.example.turomobileapp.helperfunctions

import com.example.turomobileapp.repositories.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

fun <Dto : Any, Domain : Any> requestAndMap(
    call: suspend () -> Response<Dto>,
    mapper: (Dto) -> Domain
): Flow<Result<Domain>> = flow {
    val response = call()
    if (response.isSuccessful) {
        response.body()?.let { dto ->
            emit(Result.Success(mapper(dto)))
        } ?: emit(Result.Failure(IOException("Empty response body")))
    } else {
        val msg = "Error ${response.code()}: ${response.errorBody()?.string() ?: "Unknown"}"
        emit(Result.Failure(IOException(msg)))
    }
}.catch { e ->
        emit(Result.Failure(e as Exception))
    }
