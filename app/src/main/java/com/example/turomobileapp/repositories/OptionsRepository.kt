package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.OptionsApiService
import com.example.turomobileapp.models.Options
import com.example.turomobileapp.helperfunctions.handleApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OptionsRepository @Inject constructor(private val optionsApiService: OptionsApiService) {

    fun createOption(questionId: String, options: Options): Flow<Result<Options>> = flow {
        handleApiResponse(
            call = { optionsApiService.createOption(questionId, options) },
            errorMessage = "Failed to create option $options for question $questionId"
        )
    }

    fun isOptionDuplicate(questionId: String, options: Options): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { optionsApiService.isOptionDuplicate(questionId, options) },
            errorMessage = "Failed to check option $options duplication for question $questionId"
        )
    }

    fun getOption(questionId: String, optionId: String): Flow<Result<Options>> = flow {
        handleApiResponse(
            call = { optionsApiService.getOption(questionId, optionId) },
            errorMessage = "Failed to get option $optionId for question $questionId"
        )
    }

    fun getAllOptionsForQuestion(questionId: String): Flow<Result<List<Options>>> = flow {
        handleApiResponse(
            call = { optionsApiService.getAllOptionsForQuestion(questionId) },
            errorMessage = "Failed to get all options for question $questionId"
        )
    }

    fun updateOption(questionId: String, optionId: String, options: Options): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { optionsApiService.updateOption(questionId, optionId, options) },
            errorMessage = "Failed to update option $optionId in question $questionId"
        )
    }

    fun deleteOption(questionId: String, optionId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { optionsApiService.deleteOption(questionId, optionId) },
            errorMessage = "Failed to delete option $optionId"
        )
    }
}