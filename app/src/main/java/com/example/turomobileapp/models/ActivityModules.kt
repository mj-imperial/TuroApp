package com.example.turoapp.models

import com.example.turoapp.enums.ActivityType
import com.example.turoapp.enums.ContentType
import com.example.turoapp.enums.QuizType
import com.example.turoapp.enums.ScreeningTier
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
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
data class ScreeningExam(
    @SerializedName("screening_id") override val activityId: String,
    @SerializedName("module_id") override val moduleId: String,
    @SerializedName("activity_type") override val activityType: ActivityType = ActivityType.SCREENING_EXAM,
    @SerializedName("screening_name") override val activityName: String,
    @SerializedName("screening_description") override val activityDescription: String,
    @SerializedName("unlock_date") override val unlockDate: Date,
    @SerializedName("deadline_date") override val deadlineDate: Date?,
    @SerializedName("event_id") override val eventId: String?,
    @SerializedName("tier_passed") var tierPassed: ScreeningTier,
    @SerializedName("is_Passed") var isPassed: Boolean,
): Activity(activityId,moduleId, activityType, activityName, activityDescription, unlockDate, deadlineDate, eventId)

@JsonClass(generateAdapter = true)
data class Module(
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("module_name") val moduleName: String,
    @SerializedName("module_description") val moduleDescription: String,
    @SerializedName("activities") val activities: List<Activity> = emptyList()
)