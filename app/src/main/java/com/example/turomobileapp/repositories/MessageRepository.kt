package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.MessageApiService
import com.example.turomobileapp.models.Attachment
import com.example.turomobileapp.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageApiService: MessageApiService){

    fun getMessage(messageId: String): Flow<Result<Message>> = flow {
        handleApiResponse(
            call = { messageApiService.getMessage(messageId) },
            errorMessage = "Failed to get message $messageId"
        )
    }

    fun getInboxMessages(userId: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<Message>>> = flow {
        handleApiResponse(
            call = { messageApiService.getInboxMessages(userId, page, pageSize) },
            errorMessage = "Failed to get inbox messages for user $userId"
        )
    }

    fun sendMessage(message: Message, attachments: List<File>): Flow<Result<Message>> = flow {
        try {
            val attachmentIds = mutableListOf<String>()
            if (attachments.isNotEmpty()) {
                for (attachment in attachments) {
                    val requestFile = attachment.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", attachment.name, requestFile)

                    val uploadResponse = messageApiService.uploadAttachment(body)
                    if (uploadResponse.isSuccessful) {
                        val idBody = uploadResponse.body()
                        val attachmentId = idBody?.string() ?: throw IOException("Attachment upload failed: No ID returned")
                        attachmentIds.add(attachmentId)
                    } else {
                        val errorBodyString = uploadResponse.errorBody()?.string() ?: "Unknown error"
                        emit(Result.Failure(IOException("Failed to upload attachment: ${uploadResponse.code()} - $errorBodyString")))
                        return@flow
                    }
                }
            }

            val messageWithAttachments = message.copy(attachmentIds = attachmentIds)
            handleApiResponse(
                call = { messageApiService.sendMessage(messageWithAttachments) },
                errorMessage = "Failed to send message"
            )
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    private fun File.asRequestBody(contentType: String): RequestBody {
        return asRequestBody(contentType.toMediaTypeOrNull())
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

    fun getAttachment(attachmentId: String): Flow<Result<Attachment>> = flow {
        handleApiResponse(
            call = { messageApiService.getAttachment(attachmentId) },
            errorMessage = "Failed to get attachment $attachmentId"
        )
    }

    fun getAllAttachments(messageId: String, attachmentIds: List<String>): Flow<Result<List<Attachment>>> = flow {
        handleApiResponse(
            call = { messageApiService.getAllAttachments(messageId, attachmentIds) },
            errorMessage = "Failed to get all attachments $attachmentIds for message $messageId"
        )
    }

    fun updateAttachment(attachmentId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { messageApiService.updateAttachment(attachmentId) },
            errorMessage = "Failed to update attachment $attachmentId"
        )
    }

    fun deleteAttachment(attachmentId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { messageApiService.deleteAttachment(attachmentId) },
            errorMessage = "Failed to delete attachment $attachmentId"
        )
    }
}