package com.example.turomobileapp.models

import com.example.turomobileapp.enums.UserRole
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class User(
    open @Json(name = "user_id") val userId: String,
    open @Json(name = "first_name") val firstName: String,
    open @Json(name = "last_name") val lastName: String,
    open @Json(name = "email") val email: String,
    open @Json(name = "role") val role: UserRole,
    open @Json(name = "profile_pic") val profilePic: String? = null,
    open @Json(name = "agreed_to_terms") var agreedToTerms: Boolean = false,
    open @Json(name = "requires_password_change") var requiresPasswordChange: Boolean = true,
    open @Json(name = "calendar_events") val calendarEvents: List<CalendarEvent> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Student(
    @SerializedName("user_id") override val userId: String,
    @SerializedName("first_name") override val firstName: String,
    @SerializedName("last_name") override val lastName: String,
    @SerializedName("email") override val email: String,
    @SerializedName("role") override val role: UserRole = UserRole.STUDENT,
    @SerializedName("profile_pic") override val profilePic: String? = null,
    @SerializedName("agreedToTerms") override var agreedToTerms: Boolean,
    @SerializedName("requiresPasswordChange") override var requiresPasswordChange: Boolean = true,
    @SerializedName("calendar_events") override val calendarEvents: List<CalendarEvent> = emptyList(),
    @SerializedName("averageScore") var averageScore: Double = 0.0,
    @SerializedName("isCatchUp") var isCatchUp: Boolean = false,
    @SerializedName("totalPoints") var totalPoints: Int = 0,
    @SerializedName("achievements") var achievements: List<Achievements> = emptyList(),
    @SerializedName("badges") var badges: List<Badges> = emptyList(),
    @SerializedName("boughtItems") var boughtItems: List<ShopItem> = emptyList()
): User(userId, firstName, lastName, email, role, profilePic, agreedToTerms, requiresPasswordChange, calendarEvents)

@JsonClass(generateAdapter = true)
data class Teacher(
    @SerializedName("user_id") override val userId: String,
    @SerializedName("first_name") override val firstName: String,
    @SerializedName("last_name") override val lastName: String,
    @SerializedName("email") override val email: String,
    @SerializedName("role") override val role: UserRole = UserRole.TEACHER,
    @SerializedName("profile_pic") override val profilePic: String? = null,
    @SerializedName("agreedToTerms") override var agreedToTerms: Boolean = false,
    @SerializedName("requiresPasswordChange") override var requiresPasswordChange: Boolean = true,
    @SerializedName("calendar_events") override val calendarEvents: List<CalendarEvent> = emptyList(),
    @SerializedName("courses") var courses: List<Course> = emptyList()
): User(userId, firstName, lastName, email, role, profilePic, agreedToTerms, requiresPasswordChange, calendarEvents)

@JsonClass(generateAdapter = true)
data class Admin(
    @SerializedName("user_id") override val userId: String,
    @SerializedName("first_name") override val firstName: String,
    @SerializedName("last_name") override val lastName: String,
    @SerializedName("email") override val email: String,
    @SerializedName("role") override val role: UserRole = UserRole.ADMIN,
    @SerializedName("profile_pic") override val profilePic: String? = null,
    @SerializedName("agreedToTerms") override var agreedToTerms: Boolean = false,
    @SerializedName("requiresPasswordChange") override var requiresPasswordChange: Boolean = true,
    @SerializedName("calendar_events") override val calendarEvents: List<CalendarEvent> = emptyList(),
): User(userId, firstName, lastName, email, role, profilePic, agreedToTerms, requiresPasswordChange, calendarEvents)

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "user_id") val userId: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "role_id") val roleId: Int,
    @Json(name = "role") val roleName: String,
    @Json(name = "requires_password_change") val requiresPasswordChange: Boolean,
    @Json(name = "success") val success: Boolean,
    @Json(name = "email") val email: String,
    @Json(name = "agreed_to_terms") val agreedToTerms: Boolean
)

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name="success") val success: Boolean,
    @Json(name="message")val message: String
)

