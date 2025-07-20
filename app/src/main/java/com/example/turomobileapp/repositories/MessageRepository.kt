package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.MessageApiService
import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.InboxCourseUserListResponse
import com.example.turomobileapp.models.InboxDetails
import com.example.turomobileapp.models.MessageActionResponse
import com.example.turomobileapp.models.Messages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageApiService: MessageApiService){

    fun getInboxMessages(userId: String): Flow<Result<Messages>> =
        requestAndMap(
            call = { messageApiService.getInboxMessages(userId) },
            mapper = { dto -> dto.messages }
        )

    fun getUsersForStudent(userId: String): Flow<Result<List<InboxCourseUserListResponse>>> =
        requestAndMap(
            call = { messageApiService.getUsersForStudent(action = "student", userId) },
            mapper = { dto -> dto.courses }
        )

    fun getUsersForTeacher(userId: String): Flow<Result<List<InboxCourseUserListResponse>>> =
        requestAndMap(
            call = { messageApiService.getUsersForTeacher(action = "teacher", userId) },
            mapper = { dto -> dto.courses }
        )

    fun sendMessage(userId: String, message: CreateMessageRequest): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.sendMessage(userId, message) },
            errorMessage = "Failed to send message"
        )

    fun sendReply(userId: String, message: CreateMessageRequest): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.sendReply(userId, message) },
            errorMessage = "Failed to reply message"
        )

    fun deleteInboxMessage(inboxId: String, userId: String): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.deleteInboxMessage(inboxId, userId) },
            errorMessage = "Failed to delete $inboxId"
        )

    fun markMessageAsRead(userId: String, messageId: String): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.markMessageAsRead(userId,messageId) },
            errorMessage = "Failed to mark message $messageId as read"
        )

    fun getInboxDetails(inboxId: String, userId: String): Flow<Result<InboxDetails>> =
        requestAndMap(
            call = { messageApiService.getInboxDetails(inboxId, userId) },
            mapper = { dto ->
                InboxDetails(
                    success = dto.success,
                    messages = dto.messages,
                )
            }
        )
}