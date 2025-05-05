package com.example.turomobileapp.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    @SerializedName("message_id") val messageId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("recipient_ids") val recipientIds: List<String>,
    @SerializedName("cc_recipient_ids") val ccRecipientIds: List<String>,
    @SerializedName("subject") val subject: String,
    @SerializedName("body") val body: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("attachment_ids") val attachmentIds: List<String>,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("is_mass") val isMass: Boolean,
    @SerializedName("delete_for_user") var deleteForUser: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Attachment(
    @SerializedName("attachment_id") val attachmentId: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("file_size") val fileSize: Long,
    @SerializedName("mime_type") val mimeType: String,
)

@JsonClass(generateAdapter = true)
data class Inbox(
    @SerializedName("inbox_id") val inboxId: String,
    @SerializedName("participant_ids") val participantIds: List<String>,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("last_message") val lastMessage: Message?,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("timestamp") val timestamp: Long
)