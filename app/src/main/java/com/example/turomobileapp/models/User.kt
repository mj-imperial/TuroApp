package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "token") val accessToken: String,
    @Json(name = "data") val data: UserInfoResponse
)

@JsonClass(generateAdapter = true)
data class UserInfoResponse(
    @Json(name = "user_id") val userId: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "role_id") val roleId: Int,
    @Json(name = "requires_password_change") val requiresPasswordChangeInt: Int,
    @Json(name = "email") val email: String,
    @Json(name = "agreed_to_terms") val agreedToTermsInt: Int,
    @Json(name = "image") val profilePic: ByteArray?
){
    val agreedToTerms: Boolean get() = agreedToTermsInt != 0
    val requiresPasswordChange: Boolean get() = requiresPasswordChangeInt != 0
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as UserInfoResponse

        if (roleId!=other.roleId) return false
        if (requiresPasswordChangeInt!=other.requiresPasswordChangeInt) return false
        if (agreedToTermsInt!=other.agreedToTermsInt) return false
        if (userId!=other.userId) return false
        if (firstName!=other.firstName) return false
        if (lastName!=other.lastName) return false
        if (email!=other.email) return false
        if (!profilePic.contentEquals(other.profilePic)) return false
        if (agreedToTerms!=other.agreedToTerms) return false
        if (requiresPasswordChange!=other.requiresPasswordChange) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roleId
        result = 31 * result + requiresPasswordChangeInt
        result = 31 * result + agreedToTermsInt
        result = 31 * result + userId.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        result = 31 * result + agreedToTerms.hashCode()
        result = 31 * result + requiresPasswordChange.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ApiResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message")val message: String
)

@JsonClass(generateAdapter = true)
data class CatchUpClassResponse(
    @Json(name = "is_catch_up") val isCatchUpInt: Int
){
    val isCatchUp: Boolean get() = isCatchUpInt != 0
}