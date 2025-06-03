package com.example.turomobileapp.models

import android.os.Parcelable
import com.example.turomobileapp.enums.ActivityType
import com.example.turomobileapp.enums.ContentType
import com.example.turomobileapp.enums.QuizType
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.Date

@JsonClass(generateAdapter = true)
open class Activity (
    open @SerializedName("activity_id") val activityId: String,
    open @SerializedName("module_id") val moduleId: String,
    open @SerializedName("activity_type") val activityType: ActivityType,
    open @SerializedName("activity_name") val activityName: String,
    open @SerializedName("activity_description") val activityDescription: String,
    open @SerializedName("unlock_date") val unlockDate: Date,
    open @SerializedName("deadline_date") val deadlineDate: Date?,
    open @SerializedName("event_id") val eventId: String?
)

@JsonClass(generateAdapter = true)
data class Quiz(
    @SerializedName("quiz_id") override val activityId: String,
    @SerializedName("module_id") override val moduleId: String,
    @SerializedName("activity_type") override val activityType: ActivityType = ActivityType.QUIZ,
    @SerializedName("quiz_name") override val activityName: String,
    @SerializedName("quiz_description") override val activityDescription: String,
    @SerializedName("unlock_date") override val unlockDate: Date,
    @SerializedName("deadline_date") override val deadlineDate: Date?,
    @SerializedName("event_id") override val eventId: String?,
    @SerializedName("number_of_attempts") val numberOfAttempts: Int,
    @SerializedName("quiz_type") val quizType: QuizType,
    @SerializedName("time_limit") val timeLimit: Int,
    @SerializedName("questions") val questions: List<Question> = emptyList(),
    @SerializedName("is_passed") val isPassed: Boolean
): Activity(activityId,moduleId, activityType, activityName, activityDescription, unlockDate, deadlineDate, eventId)

@JsonClass(generateAdapter = true)
data class Tutorial(
    @SerializedName("tutorial_id") override val activityId: String,
    @SerializedName("module_id") override val moduleId: String,
    @SerializedName("activity_type") override val activityType: ActivityType = ActivityType.TUTORIAL,
    @SerializedName("tutorial_name") override val activityName: String,
    @SerializedName("tutorial_description") override val activityDescription: String,
    @SerializedName("unlock_date") override val unlockDate: Date,
    @SerializedName("deadline_date") override val deadlineDate: Date?,
    @SerializedName("event_id") override val eventId: String?,
    @SerializedName("content_type") val contentType: ContentType,
    @SerializedName("video_url") val videoUrl: String? = null,
): Activity(activityId,moduleId, activityType, activityName, activityDescription, unlockDate, deadlineDate, eventId)

@JsonClass(generateAdapter = true)
data class Lecture(
    @SerializedName("lecture_id") override val activityId: String,
    @SerializedName("module_id") override val moduleId: String,
    @SerializedName("activity_type") override val activityType: ActivityType = ActivityType.LECTURE,
    @SerializedName("lecture_name") override val activityName: String,
    @SerializedName("lecture_description") override val activityDescription: String,
    @SerializedName("unlock_date") override val unlockDate: Date,
    @SerializedName("deadline_date") override val deadlineDate: Date?,
    @SerializedName("event_id") override val eventId: String?,
    @SerializedName("content_type") val contentType: ContentType,
    @SerializedName("video_url") val videoUrl: String? = null,
): Activity(activityId,moduleId, activityType, activityName, activityDescription, unlockDate, deadlineDate, eventId)

@JsonClass(generateAdapter = true)
data class Module(
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("module_name") val moduleName: String,
    @SerializedName("module_description") val moduleDescription: String,
    @SerializedName("activities") val activities: List<Activity> = emptyList()
)

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
data class ModuleResponse(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "module_description") val moduleDescription: String,
)

@JsonClass(generateAdapter = true)
data class ModuleActivitiesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "activities") val activities: List<ModuleActivityResponse>
)

@JsonClass(generateAdapter = true)
data class ModuleActivityResponse(
    @Json(name = "activity_id") val activityId: String,
    @Json(name = "activity_type") val activityType: String,
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "activity_description") val activityDescription: String,
    @Json(name = "unlock_date") val unlockDate: String,
    @Json(name = "deadline_date") val deadlineDate: String? = null
)

@JsonClass(generateAdapter = true)
data class ActivityDeleteResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String
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
    @Json(name = "unlock_date") val unlockDate: String,
    @Json(name = "deadline_date") val deadlineDate: String?,
    @Json(name = "number_of_attempts") val numberOfAttempts: Int,
    @Json(name = "quiz_type_name") val quizTypeName: String,
    @Json(name = "time_limit") val timeLimit: Int,
    @Json(name = "number_of_questions") val numberOfQuestions: Int,
    @Json(name = "overall_points") val overallPoints: Int,
    @Json(name = "has_answers_shown") val hasAnswersShownInt: Int
) : Parcelable{
    val hasAnswersShown: Boolean get() = hasAnswersShownInt != 0
}

