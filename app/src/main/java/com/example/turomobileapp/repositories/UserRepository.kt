package com.example.turomobileapp.repositories

import com.example.turomobileapp.enums.UserRole
import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.UserApiService
import com.example.turomobileapp.models.ApiResponse
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.UploadResponse
import com.example.turomobileapp.models.User
import com.example.turomobileapp.models.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApiService: UserApiService) {

    fun login(email: String, password: String): Flow<Result<User>> =
        requestAndMap(
            call   = { userApiService.login(email, password) },
            mapper = { dto ->
                User(
                    userId = dto.userId,
                    email = dto.email,
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                    role = UserRole.fromId(dto.roleId),
                    requiresPasswordChange  = dto.requiresPasswordChange,
                    agreedToTerms = dto.agreedToTerms,
                    profilePic = dto.profilePic
                )
            }
        )

    fun logout(): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.logout() },
            errorMessage = "Logout failed"
        )
    }

    fun getUserById(userId: String): Flow<Result<UserResponse>> = flow {
        handleApiResponse(
            call = { userApiService.getUserById(userId) },
            errorMessage = "Failed to get user by ID $userId"
        )
    }


    fun getUserByEmail(email: String): Flow<Result<User>> = flow {
        handleApiResponse(
            call = { userApiService.getUserByEmail(email) },
            errorMessage = "Failed to get user by email $email"
        )
    }

    fun updateUser(userId: String, user: User): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.updateUser(userId, user) },
            errorMessage = "User $userId update failed"
        )
    }

    fun updateUserProfilePic(userId: String, imageBytes: ByteArray, mimeType: String): Flow<Result<UploadResponse>> = flow {
        try {
            val userIdBody = userId.toRequestBody("text/plain".toMediaType())

            val requestFile = imageBytes.toRequestBody(mimeType.toMediaTypeOrNull())

            val part = MultipartBody.Part.createFormData(
                name = "file",
                filename = "profile_pic.jpg",
                body = requestFile
            )

            emitAll(
                handleApiResponse(
                    call = { userApiService.updateProfilePic(userIdBody, part) },
                    errorMessage = "Failed to change profile pic"
                )
            )
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }


    fun requestPasswordReset(email: String): Flow<Result<ApiResponse>> =
        handleApiResponse(
            call = { userApiService.requestPasswordReset(email) },
            errorMessage = "Password reset request failed"
        )

    fun verifyPasswordResetCode(email: String, code: String): Flow<Result<ApiResponse>> =
        handleApiResponse(
            call = { userApiService.verifyPasswordResetCode(email, code) },
            errorMessage = "Failed to verify password reset code"
        )

    fun resetPassword(email: String, newPassword: String): Flow<Result<ApiResponse>> =
        handleApiResponse(
            call = {userApiService.resetPassword(action = "resetPassword", email = email, newPassword = newPassword)},
            errorMessage = "Failed to reset password"
        )

    fun changeDefaultPassword(email: String, oldPassword: String, newPassword: String): Flow<Result<ApiResponse>> =
        handleApiResponse(
            call = {userApiService.changeDefaultPassword(action = "changeDefaultPassword", email = email, oldPassword =  oldPassword, newPassword = newPassword)},
            errorMessage = "Failed to reset password"
        )

    fun setTermsAgreementStatus(userId: String, agreed: Boolean): Flow<Result<ApiResponse>> =
        handleApiResponse(
            call = { userApiService.setTermsAgreementStatus(userId, agreed) },
            errorMessage = "Failed to set terms agreement status for $userId"
        )

    fun getDefaultPasswordChangeStatus(userId: String): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { userApiService.getUserById(userId) },
            errorMessage = "Failed to get user $userId"
        )
    }

    fun getTermsAgreementStatus(userId: String): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { userApiService.getUserById(userId) },
            errorMessage = "Failed to get user $userId"
        )
    }

    fun getAllStudents(): Flow<Result<List<Student>>> = flow {
        handleApiResponse(
            call = { userApiService.getAllStudents() },
            errorMessage = "Failed to get all students"
        )
    }

    fun getAllTeachers(): Flow<Result<List<Teacher>>> = flow {
        handleApiResponse(
            call = { userApiService.getAllTeachers() },
            errorMessage = "Failed to get all teachers"
        )
    }

}