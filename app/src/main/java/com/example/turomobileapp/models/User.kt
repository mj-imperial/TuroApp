package com.example.turomobileapp.models

import com.example.turomobileapp.enums.UserRole
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
)

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
    @Json(name = "agreed_to_terms") val agreedToTerms: Boolean,
    @Json(name = "profile_pic") val profilePic: String?
)

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name="success") val success: Boolean,
    @Json(name="message")val message: String
)