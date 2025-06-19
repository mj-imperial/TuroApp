package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.LectureApiService
import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.FileUploadResponse
import com.example.turomobileapp.models.LectureResponse
import com.example.turomobileapp.models.LectureUpdateRequest
import com.example.turomobileapp.models.LectureUploadRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class LectureRepository @Inject constructor(private val lectureApiService: LectureApiService) {

    fun uploadLectureFile(part: MultipartBody.Part): Flow<Result<FileUploadResponse>> =
        handleApiResponse(
            call = { lectureApiService.uploadFile(part) },
            errorMessage = "Failed file upload"
        )

    fun createLecture(moduleId: String, lecture: LectureUploadRequest): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { lectureApiService.createLecture(moduleId, lecture) },
            errorMessage = "Failed to create lecture ${lecture.lectureName} for module $moduleId"
        )

    fun getLecture(lectureId: String): Flow<Result<LectureResponse>> =
        handleApiResponse(
            call = { lectureApiService.getLecture(lectureId) },
            errorMessage = "Failed to get lecture $lectureId"
        )

    fun updateLecture(lectureId: String, moduleId: String, lecture: LectureUpdateRequest): Flow<Result<ActivityActionResponse>> =
        handleApiResponse(
            call = { lectureApiService.updateLecture(lectureId, moduleId, lecture) },
            errorMessage = "Failed to update lecture $lectureId"
        )
}