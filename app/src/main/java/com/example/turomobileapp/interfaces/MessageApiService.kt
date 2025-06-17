package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.InboxCourseUserListsResponse
import com.example.turomobileapp.models.InboxDetails
import com.example.turomobileapp.models.InboxItems
import com.example.turomobileapp.models.MessageActionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
    ): Response<MessageActionResponse>

    @POST("send_reply.php")
    suspend fun sendReply(
        @Query("user_id") userId: String,
        @Body message: CreateMessageRequest
    ): Response<MessageActionResponse>

    @GET("get_inbox_for_user.php")
    suspend fun getInboxMessages(
        @Query("user_id") userId: String
    ): Response<InboxItems>

    @DELETE("delete_inbox.php")
    suspend fun deleteInboxMessage(
        @Query("inbox_id") inboxId: String,
        @Query("user_id") userId: String
    ): Response<MessageActionResponse>

    @POST("mark_message_as_read.php")
    suspend fun markMessageAsRead(
        @Query("user_id") userId: String,
        @Query("message_id") messageId: String,
    ): Response<MessageActionResponse>

    @GET("get_inbox_details.php")
    suspend fun getInboxDetails(
        @Query("inbox_id") inboxId: String,
        @Query("user_id") userId: String
    ): Response<InboxDetails>
}