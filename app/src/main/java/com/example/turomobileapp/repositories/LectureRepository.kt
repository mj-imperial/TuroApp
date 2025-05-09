package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.LectureApiService
import com.example.turomobileapp.models.Lecture
import com.example.turomobileapp.helperfunctions.handleApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LectureRepository @Inject constructor(private val lectureApiService: LectureApiService) {

    fun createLecture(moduleId: String, lecture: Lecture): Flow<Result<Lecture>> = flow {
        handleApiResponse(
            call = { lectureApiService.createLecture(moduleId, lecture) },
            errorMessage = "Failed to create lecture $lecture for module $moduleId"
        )
    }

    fun isLectureDuplicate(lecture: Lecture): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { lectureApiService.isLectureDuplicate(lecture) },
            errorMessage = "Failed to check lecture $lecture duplication"
        )
    }

    fun getLecture(lectureId: String): Flow<Result<Lecture>> = flow {
        handleApiResponse(
            call = { lectureApiService.getLecture(lectureId) },
            errorMessage = "Failed to get lecture $lectureId"
        )
    }

    fun getAllLecturesForModule(moduleId: String): Flow<Result<List<Lecture>>> = flow {
        handleApiResponse(
            call = { lectureApiService.getAllLecturesForModule(moduleId) },
            errorMessage = "Failed to get all lectures for module $moduleId"
        )
    }

    fun updateLecture(lectureId: String, lecture: Lecture): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { lectureApiService.updateLecture(lectureId, lecture) },
            errorMessage = "Failed to update lecture $lectureId"
        )
    }

    fun deleteLecture(lectureId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { lectureApiService.deleteLecture(lectureId) },
            errorMessage = "Failed to delete lecture $lectureId"
        )
    }
}