package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Attachment
import com.example.turomobileapp.models.Message
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApiService {

    @GET("/messages/{messageId}")
    suspend fun getMessage(
        @Path("messageId") messageId: String
    ): Response<Message>

    @GET("/users/{userId}/messages/inbox")
    suspend fun getInboxMessages(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Response<List<Message>>

    @POST("/messages")
    suspend fun sendMessage(
        @Body messageWithAttachments: Message
    ): Response<Message>

    @PATCH("/messages/{messageId}/isRead")
    suspend fun markMessageAsRead(
        @Path("messageId") messageId: String,
        @Query("isRead") isRead: Boolean
    ): Response<ResponseBody>

    @DELETE("/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("messageId") messageId: String,
        @Query("deleteForUser") deleteForUser: Boolean
    ): Response<ResponseBody>

    @Multipart
    @POST("/attachments")
    suspend fun uploadAttachment(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("/attachments/{attachmentId}")
    suspend fun getAttachment(
        @Path("attachmentId") attachmentId: String
    ): Response<Attachment>

    @GET("/messages/{messageId}/attachments")
    suspend fun getAllAttachments(
        @Path("messageId") messageId: String,
        @Query("attachmentIds") attachmentIds: List<String>
    ): Response<List<Attachment>>

    @PUT("/attachments/{attachmentId}")
    suspend fun updateAttachment(
        @Path("attachmentId") attachmentId: String
    ): Response<ResponseBody>

    @DELETE("/attachments/{attachmentId}")
    suspend fun deleteAttachment(
        @Path("attachmentId") attachmentId: String
    ): Response<ResponseBody>
}