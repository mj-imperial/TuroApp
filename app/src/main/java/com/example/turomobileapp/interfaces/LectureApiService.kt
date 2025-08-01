package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.LectureResponse
import com.example.turomobileapp.models.LectureUpdateRequest
import com.example.turomobileapp.models.LectureUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LectureApiService {
    @POST("create_lecture.php")
    suspend fun createLecture(
        @Query("module_id") moduleId: String,
        @Body lecture: LectureUploadRequest
    ): Response<ActivityActionResponse>

    @GET("get-lecture")
    suspend fun getLecture(
        @Query("activity_id") lectureId: String,
    ): Response<LectureResponse>

    @POST("update_lecture.php")
    suspend fun updateLecture(
        @Query("activity_id") lectureId: String,
        @Query("module_id") moduleId: String,
        @Body lecture: LectureUpdateRequest
    ): Response<ActivityActionResponse>
}