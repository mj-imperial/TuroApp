package com.example.turomobileapp.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentProgress(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("module_progress") val moduleProgress: List<ModuleProgress>,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("average_score") val averageScore: Double,
    @SerializedName("leaderboard_rank") val leaderboardRank: Int
)

data class UpdateStudentModuleProgressRequest(
    val averageScore: Double? = null,
)

data class UpdateStudentCourseProgressRequest(
    val averageScore: Double? = null,
    val totalPoints: Int? = null,
    val leaderboardRank: Int? = null
)

@JsonClass(generateAdapter = true)
data class ModuleProgress(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("is_completed") val isCompleted: Boolean,
    @SerializedName("average_score") val averageScore: Double,
    @SerializedName("tier_passed") val tierPassed: String? = null,
    @SerializedName("screening_exam_attempts") val screeningExamAttempts: Int = 0,
    @SerializedName("screening_exam_failed_count") val screeningExamFailedCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class UpdateModuleProgressRequest(
    val isCompleted: Boolean? = null,
    val averageScore: Double? = null,
    val tierPassed: String? = null,
    val screeningExamAttempts: Int? = null,
    val screeningExamFailedCount: Int? = null
)