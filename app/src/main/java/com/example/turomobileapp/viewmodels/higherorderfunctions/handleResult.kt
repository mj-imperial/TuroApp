package com.example.turomobileapp.viewmodels.higherorderfunctions

import com.example.turomobileapp.repositories.Result

fun <T> handleResult(
    result: Result<T>,
    onSuccess: (T) -> Unit,
    onFailure: (String?) -> Unit,
    onLoading: () -> Unit = {}
) {
    when (result) {
        is Result.Success -> {
            onSuccess(result.data)
        }
        is Result.Failure -> {
            onFailure(result.exception.message)
        }
        is Result.Loading -> onLoading()
    }
}