package com.example.turomobileapp.models

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ModuleResultResponse(
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class TeacherModulesResponse(
    @Json(name = "section_id") val sectionId: String,
    @Json(name = "section_name") val sectionName: String,
    @Json(name = "data") val modules: List<TeacherModuleResponse>
)

@JsonClass(generateAdapter = true)
data class TeacherModuleResponse(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "image_blob") val modulePicture: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as TeacherModuleResponse

        if (moduleId!=other.moduleId) return false
        if (moduleName!=other.moduleName) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleId.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + (modulePicture?.contentHashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class TeacherGetModuleResponse(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_description") val moduleDescription: String? = null,
    @Json(name = "image_blob") val modulePicture: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as TeacherGetModuleResponse

        if (moduleId!=other.moduleId) return false
        if (moduleName!=other.moduleName) return false
        if (moduleDescription!=other.moduleDescription) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = moduleId.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + (moduleDescription?.hashCode() ?: 0)
        result = 31 * result + (modulePicture?.contentHashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ModulesResponseStudent(
    @Json(name = "data") val modules: List<ModuleResponseStudent>
)

@JsonClass(generateAdapter = true)
data class ModuleResponseStudent(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_picture") val modulePicture: ByteArray?,
    @Json(name = "module_description") val moduleDescription: String?,
    @Json(name = "progress") val moduleProgress: Double
){
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
data class ActivitySectionResponse(
    @Json(name = "section_id") val sectionId: String,
    @Json(name = "section_name") val sectionName: String,
    @Json(name = "data") val data: List<ActivityItem>
)

@JsonClass(generateAdapter = true)
data class ActivityItem(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "activity_id") val activityId: String,
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "quiz_type_name") val quizTypeName: String?,
    @Json(name = "activity_description") val activityDescription: String,
    @Json(name = "unlock_date") val unlockDate: String,
    @Json(name = "deadline_date") val deadlineDate: String
)

@JsonClass(generateAdapter = true)
data class ModuleActivitiesResponse(
    @Json(name = "data") val activities: List<ModuleActivityResponse>
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
data class LectureUploadRequest(
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val lectureName: String,
    @Json(name = "activity_description") val lectureDescription: String?,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime?,
    @Json(name = "content_type_name") val contentTypeName: String,
    @Json(name = "file_url") val fileUrl: String?,
    @Json(name = "file_mime_type") val fileMimeType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
)

@JsonClass(generateAdapter = true)
data class LectureResponse(
    @Json(name = "activity_name") val lectureName: String,
    @Json(name = "activity_description") val lectureDescription: String?,
    @Json(name = "file_url") val fileUrl: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureResponse

        if (lectureName!=other.lectureName) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lectureName.hashCode()
        result = 31 * result + (lectureDescription?.hashCode() ?: 0)
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
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
    @Json(name = "file_url") val fileUrl: String? = null,
    @Json(name = "file_mime_type") val fileMimeType: String? = null,
    @Json(name = "file_name") val fileName: String? = null,
    @Json(name = "text_body") val textBody: String? = null
)

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

@JsonClass(generateAdapter = true)
data class LongQuizzesListResponse(
    @Json(name = "data") val longQuizzes: List<LongQuizListResponse>
)

@JsonClass(generateAdapter = true)
data class LongQuizListResponse(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "long_quiz_id") val longQuizId: String,
    @Json(name = "long_quiz_name") val longQuizName: String,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime
)

@JsonClass(generateAdapter = true)
data class LongQuizResponse(
    @Json(name = "long_quiz_id") val longQuizId: String,
    @Json(name = "long_quiz_name") val longQuizName: String,
    @Json(name = "long_quiz_instructions") val longQuizInstructions: String,
    @Json(name = "unlock_date") val unlockDate: LocalDateTime,
    @Json(name = "deadline_date") val deadlineDate: LocalDateTime,
    @Json(name = "number_of_attempts") val numberOfAttempts: Int,
    @Json(name = "time_limit") val timeLimit: Int,
    @Json(name = "number_of_questions") val numberOfQuestions: Int,
    @Json(name = "overall_points") val overallPoints: Int,
    @Json(name = "has_answers_shown") val hasAnswersShownInt: Int
){
    val hasAnswersShown: Boolean get() = hasAnswersShownInt != 0
}





