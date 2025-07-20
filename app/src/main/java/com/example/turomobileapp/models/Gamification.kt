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
    @Json(name = "success") val success: Boolean,
    @Json(name = "achievements") val achievements: List<StudentAchievementResponse>
)

@JsonClass(generateAdapter = true)
data class StudentAchievementResponse(
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

@JsonClass(generateAdapter = true)
data class GamifiedElementsResponse(
    @Json(name = "student_name") val studentName: String,
    @Json(name = "overall_points") val overallPoints: Int,
    @Json(name = "leaderboard_ranking") val leaderboardRanking: Int,
    @Json(name = "section") val section: String,
    @Json(name = "leaderboards") val leaderboards: List<LeaderboardEntry>,
    @Json(name = "achievements") val achievements: List<Achievement>,
    @Json(name = "achievements_retrieved") val achievementsRetrieved: List<AchievementRetrieved>,
    @Json(name = "badges") val badges: List<Badge>,
    @Json(name = "badges_retrieved") val badgesRetrieved: List<BadgeRetrieved>
)

@JsonClass(generateAdapter = true)
data class LeaderboardEntry(
    @Json(name = "student_name") val studentName: String,
    @Json(name = "student_ranking") val studentRanking: Int,
    @Json(name = "student_points") val studentPoints: Int,
    @Json(name = "student_image") val studentImage: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LeaderboardEntry

        if (studentRanking!=other.studentRanking) return false
        if (studentPoints!=other.studentPoints) return false
        if (studentName!=other.studentName) return false
        if (!studentImage.contentEquals(other.studentImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = studentRanking
        result = 31 * result + studentPoints
        result = 31 * result + studentName.hashCode()
        result = 31 * result + (studentImage?.contentHashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class Achievement(
    @Json(name = "achievement_id") val achievementId: Int,
    @Json(name = "achievement_name") val achievementName: String,
    @Json(name = "achievement_description") val achievementDescription: String,
    @Json(name = "image_name") val imageName: String
)

@JsonClass(generateAdapter = true)
data class AchievementRetrieved(
    @Json(name = "achievement_id") val achievementId: Int,
    @Json(name = "unlocked_at") val unlockedAt: String
)

@JsonClass(generateAdapter = true)
data class Badge(
    @Json(name = "badge_id") val badgeId: Int,
    @Json(name = "badge_name") val badgeName: String,
    @Json(name = "badge_description") val badgeDescription: String,
    @Json(name = "image_name") val imageName: String
)

@JsonClass(generateAdapter = true)
data class BadgeRetrieved(
    @Json(name = "badge_id") val badgeId: Int,
    @Json(name = "unlocked_at") val unlockedAt: String
)
