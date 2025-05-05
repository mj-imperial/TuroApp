package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Options
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OptionsApiService {
    @POST("/questions/{questionId}/options")
    suspend fun createOption(
        @Path("questionId") questionId: String,
        @Body options: Options
    ): Response<Options>

    @POST("/questions/{questionId}/options/validateDuplication")
    suspend fun isOptionDuplicate(
        @Path("questionId") questionId: String,
        @Body options: Options
    ): Response<Boolean>

    @GET("/questions/{questionId}/options/{optionId}")
    suspend fun getOption(
        @Path("questionId") questionId: String,
        @Path("optionId") optionId: String
    ): Response<Options>

    @GET("/questions/{questionId}/options")
    suspend fun getAllOptionsForQuestion(
        @Path("questionId") questionId: String
    ): Response<List<Options>>

    @PUT("/questions/{questionId}/options/{optionId}")
    suspend fun updateOption(
        @Path("questionId") questionId: String,
        @Path("optionId") optionId: String,
        @Body options: Options
    ): Response<ResponseBody>

    @DELETE("/questions/{questionId}/options/{optionId}")
    suspend fun deleteOption(
        @Path("questionId") questionId: String,
        @Path("optionId") optionId: String
    ): Response<ResponseBody>
}