package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ActivityActionResponse
import com.example.turomobileapp.models.FileUploadResponse
import com.example.turomobileapp.models.Lecture
import com.example.turomobileapp.models.LectureUploadRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LectureApiService {
    @Multipart
    @POST("upload_lecture_file.php")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<FileUploadResponse>

    @POST("create_lecture.php")
    suspend fun createLecture(
        @Query("module_id") moduleId: String,
        @Body lecture: LectureUploadRequest
    ): Response<ActivityActionResponse>

    @GET("/lectures/validateDuplication")
    suspend fun isLectureDuplicate(
        @Body lecture: Lecture
    ): Response<Boolean>

    @GET("/lectures/{lectureId}")
    suspend fun getLecture(
        @Path("lectureId") lectureId: String
    ): Response<Lecture>

    @GET("/modules/{moduleId}/lectures")
    suspend fun getAllLecturesForModule(
        @Path("moduleId") moduleId: String
    ): Response<List<Lecture>>

    @PUT("/lectures/{lectureId}")
    suspend fun updateLecture(
        @Path("lectureId") lectureId: String,
        @Body lecture: Lecture
    ): Response<ResponseBody>

    @DELETE("/lectures/{lectureId}")
    suspend fun deleteLecture(
        @Path("lectureId") lectureId: String
    ): Response<ResponseBody>
}