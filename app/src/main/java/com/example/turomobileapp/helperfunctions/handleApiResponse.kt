package com.example.turomobileapp.helperfunctions

import com.example.turomobileapp.repositories.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.io.IOException

fun <T> handleApiResponse(
    call: suspend () -> Response<T>,
    errorMessage: String
): Flow<Result<T>> = flow {
    val response = call()

    if (response.isSuccessful) {
        response.body()?.let {
            emit(Result.Success(it))
        } ?: emit(
            Result.Failure(
                IOException("$errorMessage: response body was empty")
            )
        )
    } else {
        emit(
            Result.Failure(
                IOException(
                    "$errorMessage: ${response.code()} – ${
                        response.errorBody()?.string()
                            ?: "unknown error"
                    }"
                )
            )
        )
    }
}.catch { e ->
    val wrapped = if (e is IOException) e else IOException(e)
    emit(Result.Failure(wrapped))
}
