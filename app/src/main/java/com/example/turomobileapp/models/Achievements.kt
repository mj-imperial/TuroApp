package com.example.turomobileapp.models

import com.example.turomobileapp.enums.AchievementConditionType
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class Achievements (
    @SerializedName("achievement_id") val achievementId: String,
    @SerializedName("achievement_name") val achievementName: String,
    @SerializedName("achievement_description") val achievementDescription: String,
    @SerializedName("achievement_image") val achievementImage: String? = null,
    @SerializedName("condition_type") val conditionType: AchievementConditionType,
    @SerializedName("condition_value") val conditionValue: String,
    @SerializedName("is_unlocked") val isUnlocked: Boolean
)

@JsonClass(generateAdapter = true)
data class Badges(
    @SerializedName("badge_id") val badgeId: String,
    @SerializedName("badge_name") val badgeName: String,
    @SerializedName("badge_description") val badgeDescription: String,
    @SerializedName("badge_image") val badgeImage: String? = null,
    @SerializedName("points_required") val pointsRequired: Int,
    @SerializedName("is_unlocked") val isUnlocked: Boolean
)

@JsonClass(generateAdapter = true)
data class Leaderboard(
    @SerializedName("course_id") val courseId: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("total_points") val totalPoints: Int,
    @SerializedName("ranking") val ranking: Int,
    @SerializedName("last_updated") val lastUpdated: Instant
)