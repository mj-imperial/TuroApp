package com.example.turomobileapp.models

import com.example.turomobileapp.enums.UserRole
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class User(
    open @SerializedName("user_id") val userId: String,
    open @SerializedName("first_name") val firstName: String,
    open @SerializedName("last_name") val lastName: String,
    open @SerializedName("email") val email: String,
    open @SerializedName("role") val role: UserRole,
    open @SerializedName("profile_pic") val profilePic: String? = null,
    open @SerializedName("agreed_to_terms") var agreedToTerms: Boolean = false,
    open @SerializedName("requires_password_change") var requiresPasswordChange: Boolean = true,
    open @SerializedName("calendar_events") val calendarEvents: List<CalendarEvent> = emptyList()
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

data class LoginResponse(
    @SerializedName("user_id") val userId: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("role_id") val roleId: Int,
    @SerializedName("role") val roleName: String,
    @SerializedName("requires_password_change") val requiresPasswordChange: Boolean,
    val success: Boolean,
    val email: String,
)

