package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.TutorialApiService
import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.Tutorial
import com.example.turomobileapp.models.TutorialResponse
import com.example.turomobileapp.models.TutorialUploadRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TutorialRepository @Inject constructor(private val tutorialApiService: TutorialApiService) {

    fun createTutorial(moduleId: String, tutorial: TutorialUploadRequest): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { tutorialApiService.createTutorial(moduleId, tutorial) },
            errorMessage = "Failed to create tutorial $tutorial in module $moduleId"
        )


    fun getTutorial(tutorialId: String): Flow<Result<TutorialResponse>> =
        handleApiResponse(
            call = { tutorialApiService.getTutorial(tutorialId) },
            errorMessage = "Failed to get tutorial $tutorialId"
        )

    fun getAllTutorialsForModule(moduleId: String): Flow<Result<List<Tutorial>>> = flow {
        handleApiResponse(
            call = { tutorialApiService.getAllTutorialsForModule(moduleId) },
            errorMessage = "Failed to get tutorials for module $moduleId"
        )
    }

    fun updateTutorial(activityId: String, moduleId: String, tutorial: TutorialResponse): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { tutorialApiService.updateTutorial(activityId, moduleId, tutorial) },
            errorMessage = "Failed to update tutorial $activityId"
        )

}