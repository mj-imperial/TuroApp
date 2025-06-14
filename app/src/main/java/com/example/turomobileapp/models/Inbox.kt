package com.example.turomobileapp.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
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
data class Inbox(
    @SerializedName("inbox_id") val inboxId: String,
    @SerializedName("participant_ids") val participantIds: List<String>,
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("last_message") val lastMessage: Message?,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("timestamp") val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class InboxItems(
    @Json(name = "success") val success: Boolean,
    @Json(name = "inboxes") val inboxes: List<InboxItem>
)

@JsonClass(generateAdapter = true)
data class InboxItem(
    @Json(name = "inbox_id") val inboxId: String,
    @Json(name = "latest_message_id") val latestMessageId: String?,
    @Json(name = "last_message_subject") val lastMessageSubject: String?,
    @Json(name = "last_message_preview") val lastMessagePreview: String,
    @Json(name = "inbox_timestamp") val lastMessageTimestamp: Long,
    @Json(name = "unread_count") val unreadCount: Int,
    @Json(name = "participants") val participants: List<UserInfo>
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "user_id") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "profile_pic") val profileImageUrl: String?
)

@JsonClass(generateAdapter = true)
data class CreateMessageRequest(
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "recipient_ids") val recipientIds: List<String>,
    @Json(name = "subject") val subject: String,
    @Json(name = "body") val body: String
)

@JsonClass(generateAdapter = true)
data class MessageActionResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class InboxCourseUserListsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "courses") val courses: List<InboxCourseUserListResponse>
)

@JsonClass(generateAdapter = true)
data class InboxCourseUserListResponse(
    @Json(name = "course_name") val courseName: String,
    @Json(name = "teacher_id") val teacherId: String,
    @Json(name = "teacher_pic") val teacherPic: String,
    @Json(name = "teacher_email") val teacherEmail: String,
    @Json(name = "teacher_name") val teacherName: String,
    @Json(name = "students") val students: List<InboxCourseStudent>
)

@JsonClass(generateAdapter = true)
data class InboxCourseStudent(
    @Json(name = "user_id") val userId: String,
    @Json(name = "student_pic") val studentPic: String,
    @Json(name = "student_name") val studentName: String,
    @Json(name = "student_email") val email: String
)

@JsonClass(generateAdapter = true)
data class InboxDetails(
    @Json(name = "success") val success: Boolean,
    @Json(name = "messages") val messages: List<InboxDetail>,
)

@JsonClass(generateAdapter = true)
data class InboxDetail(
    @Json(name = "message_id") val messageId: String,
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "sender_name") val senderName: String,
    @Json(name = "sender_pic") val senderPic: String?,
    @Json(name = "recipient_id") val recipientId: String,
    @Json(name = "recipient_name") val recipientName: String,
    @Json(name = "recipient_pic") val recipientPic: String?,
    @Json(name = "subject") val subject: String,
    @Json(name = "body") val body: String,
    @Json(name = "timestamp") val timestamp: Long
)