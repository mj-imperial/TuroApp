package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentBadgesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "badges") val badges: List<StudentBadgeResponse>
)

@JsonClass(generateAdapter = true)
data class StudentBadgeResponse(
    @Json(name = "badge_id") val badgeId: String,
    @Json(name = "is_unlocked") val isUnlockedInt: Int,
    @Json(name = "badge_name") val badgeName: String,
    @Json(name = "badge_description") val badgeDescription: String,
    @Json(name = "badge_image") val badgeImage: String,
    @Json(name = "points_required") val pointsRequired: Int,
    @Json(name = "total_points") val studentTotalPoints: Int
){
    val isUnlocked: Boolean get() = isUnlockedInt != 0
}

@JsonClass(generateAdapter = true)
data class StudentAchievementsResponse(
    @Json(name = "achievement_id") val achievementId: String,
    @Json(name = "achievement_name") val achievementName: String,
    @Json(name = "achievement_description") val achievementDescription: String,
    @Json(name = "achievement_image") val achievementImage: String,
    @Json(name = "condition_name") val conditionName: String,
    @Json(name = "condition_value") val conditionValue: String,
    @Json(name = "is_unlocked") val isUnlockedInt: Int
){
    val isUnlocked: Boolean get() = isUnlockedInt != 0
}