package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.MessageApiService
import com.example.turomobileapp.models.CreateMessageRequest
import com.example.turomobileapp.models.CreateMessageResponse
import com.example.turomobileapp.models.InboxCourseUserListResponse
import com.example.turomobileapp.models.Message
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

    fun sendMessage(userId: String, message: CreateMessageRequest): Flow<Result<CreateMessageResponse>> =
        handleApiResponse(
            call = { messageApiService.sendMessage(userId, message) },
            errorMessage = "Failed to send message"
        )

    fun getMessage(messageId: String): Flow<Result<Message>> = flow {
        handleApiResponse(
            call = { messageApiService.getMessage(messageId) },
            errorMessage = "Failed to get message $messageId"
        )
    }

    fun getInboxMessages(userId: String): Flow<Result<List<Message>>> = flow {
        handleApiResponse(
            call = { messageApiService.getInboxMessages(userId) },
            errorMessage = "Failed to get inbox messages for user $userId"
        )
    }

    fun markMessageAsRead(messageId: String, isRead: Boolean): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { messageApiService.markMessageAsRead(messageId, isRead) },
            errorMessage = "Failed to mark message $messageId as read"
        )
    }

    fun deleteMessage(messageId: String, deleteForUser: Boolean = true): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { messageApiService.deleteMessage(messageId, deleteForUser) },
            errorMessage = "Failed to delete message $messageId"
        )
    }
}