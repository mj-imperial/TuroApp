package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.MessageApiService
import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.InboxCourseUserListResponse
import com.example.turomobileapp.models.InboxItem
import com.example.turomobileapp.models.Message
import com.example.turomobileapp.models.MessageActionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageApiService: MessageApiService){

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

    fun deleteInboxMessage(inboxId: String, userId: String): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.deleteInboxMessage(inboxId, userId) },
            errorMessage = "Failed to delete $inboxId"
        )

    fun getInboxMessages(userId: String): Flow<Result<List<InboxItem>>> =
        requestAndMap(
            call = { messageApiService.getInboxMessages(userId) },
            mapper = { dto -> dto.inboxes }
        )

    fun markMessageAsRead(userId: String, messageId: String): Flow<Result<MessageActionResponse>> =
        handleApiResponse(
            call = { messageApiService.markMessageAsRead(userId,messageId) },
            errorMessage = "Failed to mark message $messageId as read"
        )

    fun getMessage(messageId: String): Flow<Result<Message>> = flow {
        handleApiResponse(
            call = { messageApiService.getMessage(messageId) },
            errorMessage = "Failed to get message $messageId"
        )
    }

    fun deleteMessage(messageId: String, deleteForUser: Boolean = true): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { messageApiService.deleteMessage(messageId, deleteForUser) },
            errorMessage = "Failed to delete message $messageId"
        )
    }
}