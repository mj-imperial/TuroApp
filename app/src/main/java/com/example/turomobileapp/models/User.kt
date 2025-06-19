package com.example.turomobileapp.models

import com.example.turomobileapp.enums.UserRole
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    @Json(name = "image") val profilePic: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as UserResponse

        if (roleId!=other.roleId) return false
        if (requiresPasswordChange!=other.requiresPasswordChange) return false
        if (success!=other.success) return false
        if (agreedToTerms!=other.agreedToTerms) return false
        if (userId!=other.userId) return false
        if (firstName!=other.firstName) return false
        if (lastName!=other.lastName) return false
        if (roleName!=other.roleName) return false
        if (email!=other.email) return false
        if (!profilePic.contentEquals(other.profilePic)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roleId
        result = 31 * result + requiresPasswordChange.hashCode()
        result = 31 * result + success.hashCode()
        result = 31 * result + agreedToTerms.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + roleName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message")val message: String
)