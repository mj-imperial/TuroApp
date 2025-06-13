package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.CreateMessageResponse
import com.example.turomobileapp.models.InboxCourseUserListsResponse
import com.example.turomobileapp.models.Message
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApiService {
    @GET("get_inbox_users_in_course.php")
    suspend fun getUsersForStudent(
        @Query("action") action: String,
        @Query("user_id") userId: String
    ): Response<InboxCourseUserListsResponse>

    @GET("get_inbox_users_in_course.php")
    suspend fun getUsersForTeacher(
        @Query("action") action: String,
        @Query("user_id") userId: String
    ): Response<InboxCourseUserListsResponse>

    @POST("send_message.php")
    suspend fun sendMessage(
        @Query("user_id") userId: String,
        @Body message: CreateMessageRequest
    ): Response<CreateMessageResponse>

    @GET("/users/{userId}/messages/inbox")
    suspend fun getInboxMessages(
        @Path("userId") userId: String,
    ): Response<List<Message>>

    @GET("/messages/{messageId}")
    suspend fun getMessage(
        @Path("messageId") messageId: String
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
}