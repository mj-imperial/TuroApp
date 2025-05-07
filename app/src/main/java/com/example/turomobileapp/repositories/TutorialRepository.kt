package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.TutorialApiService
import com.example.turomobileapp.models.Tutorial
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TutorialRepository @Inject constructor(private val tutorialApiService: TutorialApiService) {

    fun createTutorial(moduleId: String, tutorial: Tutorial): Flow<Result<Tutorial>> = flow {
        handleApiResponse(
            call = { tutorialApiService.createTutorial(moduleId, tutorial) },
            errorMessage = "Failed to create tutorial $tutorial in module $moduleId"
        )
    }

    fun isTutorialDuplicate(moduleId: String, tutorial: Tutorial): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { tutorialApiService.isTutorialDuplicate(moduleId, tutorial) },
            errorMessage = "Failed to check tutorial $tutorial duplication in module $moduleId"
        )
    }

    fun getTutorial(tutorialId: String): Flow<Result<Tutorial>> = flow {
        handleApiResponse(
            call = { tutorialApiService.getTutorial(tutorialId) },
            errorMessage = "Failed to get tutorial $tutorialId"
        )
    }

    fun getAllTutorialsForModule(moduleId: String): Flow<Result<List<Tutorial>>> = flow {
        handleApiResponse(
            call = { tutorialApiService.getAllTutorialsForModule(moduleId) },
            errorMessage = "Failed to get tutorials for module $moduleId"
        )
    }

    fun updateTutorial(tutorialId: String, tutorial: Tutorial): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { tutorialApiService.updateTutorial(tutorialId, tutorial) },
            errorMessage = "Failed to update tutorial $tutorialId"
        )
    }

    fun deleteTutorial(tutorialId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { tutorialApiService.deleteTutorial(tutorialId) },
            errorMessage = "Failed to delete tutorial $tutorialId"
        )
    }
}