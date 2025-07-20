package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InboxItems(
    @Json(name = "messages") val messages: Messages
)

@JsonClass(generateAdapter = true)
data class Messages(
    @Json(name = "incoming") val incoming: List<MessageItem>,
    @Json(name = "sent") val sent: List<MessageItem>
)

@JsonClass(generateAdapter = true)
data class MessageItem(
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "sender_name") val senderName: String,
    @Json(name = "image_blob") val imageBlob: ByteArray,
    @Json(name = "subject") val subject: String?,
    @Json(name = "message") val message: String,
    @Json(name = "date") val date: String,
    @Json(name = "unread") val unread: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as MessageItem

        if (unread!=other.unread) return false
        if (senderId!=other.senderId) return false
        if (senderName!=other.senderName) return false
        if (!imageBlob.contentEquals(other.imageBlob)) return false
        if (subject!=other.subject) return false
        if (message!=other.message) return false
        if (date!=other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = unread.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + imageBlob.contentHashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "user_id") val userId: String,
    @Json(name = "name") val name: String,
    @Json(name = "image") val profileImage: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as UserInfo

        if (userId!=other.userId) return false
        if (name!=other.name) return false
        if (!profileImage.contentEquals(other.profileImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (profileImage?.contentHashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class CreateMessageRequest(
    @Json(name = "sender_id") val senderId: String,
    @Json(name = "recipient_ids") val recipientIds: List<String>,
    @Json(name = "subject") val subject: String,
    @Json(name = "body") val body: String
)

@JsonClass(generateAdapter = true)
data class MessageActionResponse(
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
    @Json(name = "teacher_pic") val teacherPic: ByteArray,
    @Json(name = "teacher_email") val teacherEmail: String,
    @Json(name = "teacher_name") val teacherName: String,
    @Json(name = "students") val students: List<InboxCourseStudent>
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as InboxCourseUserListResponse

        if (courseName!=other.courseName) return false
        if (teacherId!=other.teacherId) return false
        if (!teacherPic.contentEquals(other.teacherPic)) return false
        if (teacherEmail!=other.teacherEmail) return false
        if (teacherName!=other.teacherName) return false
        if (students!=other.students) return false

        return true
    }

    override fun hashCode(): Int {
        var result = courseName.hashCode()
        result = 31 * result + teacherId.hashCode()
        result = 31 * result + teacherPic.contentHashCode()
        result = 31 * result + teacherEmail.hashCode()
        result = 31 * result + teacherName.hashCode()
        result = 31 * result + students.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class InboxCourseStudent(
    @Json(name = "user_id") val userId: String,
    @Json(name = "student_pic") val studentPic: ByteArray,
    @Json(name = "student_name") val studentName: String,
    @Json(name = "student_email") val email: String
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as InboxCourseStudent

        if (userId!=other.userId) return false
        if (!studentPic.contentEquals(other.studentPic)) return false
        if (studentName!=other.studentName) return false
        if (email!=other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + studentPic.contentHashCode()
        result = 31 * result + studentName.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }
}

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
    @Json(name = "sender_pic") val senderPic: ByteArray?,
    @Json(name = "recipient_id") val recipientId: String,
    @Json(name = "recipient_name") val recipientName: String,
    @Json(name = "recipient_pic") val recipientPic: ByteArray?,
    @Json(name = "subject") val subject: String,
    @Json(name = "body") val body: String,
    @Json(name = "timestamp") val timestamp: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as InboxDetail

        if (timestamp!=other.timestamp) return false
        if (messageId!=other.messageId) return false
        if (senderId!=other.senderId) return false
        if (senderName!=other.senderName) return false
        if (senderPic!=other.senderPic) return false
        if (recipientId!=other.recipientId) return false
        if (recipientName!=other.recipientName) return false
        if (!recipientPic.contentEquals(other.recipientPic)) return false
        if (subject!=other.subject) return false
        if (body!=other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + messageId.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + senderName.hashCode()
        result = 31 * result + (senderPic?.hashCode() ?: 0)
        result = 31 * result + recipientId.hashCode()
        result = 31 * result + recipientName.hashCode()
        result = 31 * result + (recipientPic?.contentHashCode() ?: 0)
        result = 31 * result + subject.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }
}