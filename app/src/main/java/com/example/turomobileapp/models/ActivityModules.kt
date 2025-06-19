package com.example.turomobileapp.models

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ModuleUploadRequest(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_description") val moduleDescription: String
)

@JsonClass(generateAdapter = true)
data class ModuleResultResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class ModulesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "modules") val modules: List<ModuleResponse>
)

@JsonClass(generateAdapter = true)
data class ModuleUpdateRequest(
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_description") val moduleDescription: String
)

@JsonClass(generateAdapter = true)
data class ModuleResponse(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_picture") val modulePicture: ByteArray,
    @Json(name = "module_description") val moduleDescription: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ModuleResponse

        if (moduleId!=other.moduleId) return false
        if (moduleName!=other.moduleName) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false
        if (moduleDescription!=other.moduleDescription) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleId.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + modulePicture.contentHashCode()
        result = 31 * result + moduleDescription.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ModulesResponseStudent(
    @Json(name = "success") val success: Boolean,
    @Json(name = "modules") val modules: List<ModuleResponseStudent>
)

@JsonClass(generateAdapter = true)
data class ModuleResponseStudent(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_picture") val modulePicture: ByteArray,
    @Json(name = "module_description") val moduleDescription: String,
    @Json(name = "progress") val moduleProgress: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ModuleResponseStudent

        if (moduleProgress!=other.moduleProgress) return false
        if (moduleId!=other.moduleId) return false
        if (moduleName!=other.moduleName) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false
        if (moduleDescription!=other.moduleDescription) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleProgress.hashCode()
        result = 31 * result + moduleId.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + modulePicture.contentHashCode()
        result = 31 * result + moduleDescription.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ModuleActivitiesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "activities") val activities: List<ModuleActivityResponse>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class ModuleActivityResponse(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "activity_id") val activityId: String,
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "quiz_type_name") val quizTypeName: String? = null,
    @Json(name = "activity_description") val activityDescription: String,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime? = null,

    val isUnlocked: Boolean = false,
    val hasAnswered: Boolean = false,
    val displayOrder: Int = 0,
): Parcelable{
    val isLockedDate: Boolean @RequiresApi(Build.VERSION_CODES.O) get() = unlockDate?.isAfter(LocalDateTime.now()) == true
}

@JsonClass(generateAdapter = true)
data class ActivityActionResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class TutorialResponse(
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "activity_description") val activityDescription: String,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "video_url") val videoUrl: String
)

@JsonClass(generateAdapter = true)
data class TutorialUploadRequest(
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "activity_description") val activityDescription: String,
    @Json(name = "unlock_date") val unlockDateTime: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDateTime: LocalDateTime?,
    @Json(name = "content_type_name") val contentTypeName: String,
    @Json(name = "video_url") val videoUrl: String
)

@JsonClass(generateAdapter = true)
data class FileUploadResponse(
    @Json(name = "file_url") val fileUrl: ByteArray,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "mime_type") val mimeType: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as FileUploadResponse

        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileName!=other.fileName) return false
        if (mimeType!=other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileUrl.contentHashCode()
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class LectureUploadRequest(
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val lectureName: String,
    @Json(name = "activity_description") val lectureDescription: String?,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "content_type_name") val contentTypeName: String,
    @Json(name = "video_url") val videoUrl: String? = null,
    @Json(name = "file_url") val fileUrl: ByteArray? = null,
    @Json(name = "file_mime_type") val fileMimeType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "text_body") val textBody: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureUploadRequest

        if (activityType!=other.activityType) return false
        if (lectureName!=other.lectureName) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDate!=other.unlockDate) return false
        if (deadlineDate!=other.deadlineDate) return false
        if (contentTypeName!=other.contentTypeName) return false
        if (videoUrl!=other.videoUrl) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false
        if (textBody!=other.textBody) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activityType.hashCode()
        result = 31 * result + lectureName.hashCode()
        result = 31 * result + (lectureDescription?.hashCode() ?: 0)
        result = 31 * result + (unlockDate?.hashCode() ?: 0)
        result = 31 * result + (deadlineDate?.hashCode() ?: 0)
        result = 31 * result + contentTypeName.hashCode()
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (textBody?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class LectureResponse(
    @Json(name = "activity_name") val lectureName: String,
    @Json(name = "activity_description") val lectureDescription: String?,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "content_type_name") val contentTypeName: String,
    @Json(name = "video_url") val videoUrl: String? = null,
    @Json(name = "file_url") val fileUrl: ByteArray? = null,
    @Json(name = "file_mime_type") val fileMimeType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "text_body") val textBody: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureResponse

        if (lectureName!=other.lectureName) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDate!=other.unlockDate) return false
        if (deadlineDate!=other.deadlineDate) return false
        if (contentTypeName!=other.contentTypeName) return false
        if (videoUrl!=other.videoUrl) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false
        if (textBody!=other.textBody) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lectureName.hashCode()
        result = 31 * result + (lectureDescription?.hashCode() ?: 0)
        result = 31 * result + (unlockDate?.hashCode() ?: 0)
        result = 31 * result + (deadlineDate?.hashCode() ?: 0)
        result = 31 * result + contentTypeName.hashCode()
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (textBody?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class LectureUpdateRequest(
    @Json(name = "activity_name") val lectureName: String,
    @Json(name = "activity_description") val lectureDescription: String?,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "content_type_name") val contentTypeName: String,
    @Json(name = "video_url") val videoUrl: String? = null,
    @Json(name = "file_url") val fileUrl: ByteArray? = null,
    @Json(name = "file_mime_type") val fileMimeType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "text_body") val textBody: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureUpdateRequest

        if (lectureName!=other.lectureName) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDate!=other.unlockDate) return false
        if (deadlineDate!=other.deadlineDate) return false
        if (contentTypeName!=other.contentTypeName) return false
        if (videoUrl!=other.videoUrl) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false
        if (textBody!=other.textBody) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lectureName.hashCode()
        result = 31 * result + (lectureDescription?.hashCode() ?: 0)
        result = 31 * result + (unlockDate?.hashCode() ?: 0)
        result = 31 * result + (deadlineDate?.hashCode() ?: 0)
        result = 31 * result + contentTypeName.hashCode()
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (textBody?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class QuizzesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "quizzes") val quizzes: List<QuizResponse>
)

@Parcelize
@JsonClass(generateAdapter = true)
data class QuizResponse(
    @Json(name = "activity_id") val quizId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val quizName: String,
    @Json(name = "activity_description") val quizDescription: String,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "number_of_attempts") val numberOfAttempts: Int,
    @Json(name = "quiz_type_name") val quizTypeName: String,
    @Json(name = "time_limit") val timeLimit: Int,
    @Json(name = "number_of_questions") val numberOfQuestions: Int,
    @Json(name = "overall_points") val overallPoints: Int,
    @Json(name = "has_answers_shown") val hasAnswersShownInt: Int,

    val isUnlocked: Boolean? = false,
    val hasAnswered: Boolean? = false
) : Parcelable{
    val hasAnswersShown: Boolean get() = hasAnswersShownInt != 0
}

