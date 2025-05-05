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
        try {
            val response = messageApiService.getMessage(messageId)
            if (response.isSuccessful){
                val message = response.body()
                message?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getMessage Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get message: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getInboxMessages(userId: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<Message>>> = flow {
        try {
            val response = messageApiService.getInboxMessages(userId, page, pageSize)
            if (response.isSuccessful){
                val messages = response.body()
                messages?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getInboxMessages Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get inbox messages: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun sendMessage(message: Message, attachments: List<File>): Flow<Result<Message>> = flow {
        try {
            val attachmentIds = mutableListOf<String>()
            if (attachments.isNotEmpty()){
                for (attachment in attachments){
                    val requestFile = attachment.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", attachment.name, requestFile)
                    val uploadResponse = messageApiService.uploadAttachment(body)
                    if (uploadResponse.isSuccessful) {
                        val attachmentId = uploadResponse.body()?.string()
                            ?: throw Exception("Attachment upload failed: No ID returned")
                        attachmentIds.add(attachmentId)
                    } else {
                        emit(Result.Failure(Exception("Failed to upload attachment: ${uploadResponse.errorBody()?.string()}")))
                        return@flow
                    }
                }
            }

            val messageWithAttachments = message.copy(attachmentIds = attachmentIds)
            val response = messageApiService.sendMessage(messageWithAttachments)
            when{
                response.isSuccessful -> {
                    val sentMessage = response.body() ?: throw Exception("Failed to send message")
                    emit(Result.Success(sentMessage))
                }
                else -> {
                    val errorMessage = "Failed to send message: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    private fun File.asRequestBody(contentType: String): RequestBody {
        return asRequestBody(contentType.toMediaTypeOrNull())
    }

    fun markMessageAsRead(messageId: String, isRead: Boolean): Flow<Result<Unit>> = flow {
        try {
            val response = messageApiService.markMessageAsRead(messageId, isRead)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to mark message as read: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteMessage(messageId: String, deleteForUser: Boolean = true): Flow<Result<Unit>> = flow {
        try {
            val response = messageApiService.deleteMessage(messageId, deleteForUser)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete message: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAttachment(attachmentId: String): Flow<Result<Attachment>> = flow {
        try {
            val response = messageApiService.getAttachment(attachmentId)
            if (response.isSuccessful){
                val attachment = response.body()
                attachment?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAttachment Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get attachment: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllAttachments(messageId: String, attachmentIds: List<String>): Flow<Result<List<Attachment>>> = flow {
        try {
            val response = messageApiService.getAllAttachments(messageId, attachmentIds)
            if (response.isSuccessful){
                val attachments = response.body()
                attachments?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllAttachments Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all attachments: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateAttachment(attachmentId: String): Flow<Result<Unit>> = flow {
        try {
            val response = messageApiService.updateAttachment(attachmentId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update attachment: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteAttachment(attachmentId: String): Flow<Result<Unit>> = flow {
        try {
            val response = messageApiService.deleteAttachment(attachmentId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete attachment: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}