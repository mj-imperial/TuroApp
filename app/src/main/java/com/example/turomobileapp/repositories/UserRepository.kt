package com.example.turomobileapp.repositories

import com.example.turomobileapp.enums.UserRole
import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.UserApiService
import com.example.turomobileapp.models.Admin
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.User
import com.example.turomobileapp.models.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream
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
                    requiresPasswordChange  = dto.requiresPasswordChange
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

    fun updateUserProfilePic(userId: String, imageStream: InputStream, mimeType: String): Flow<Result<Unit>> = flow {
        try {
            val requestFile = imageStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "profile_pic", requestFile)

            handleApiResponse(
                call = { userApiService.updateProfilePic(userId, part) },
                errorMessage = "Profile picture update failed"
            )

        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun requestPasswordReset(email: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.requestPasswordReset(email) },
            errorMessage = "Password reset request failed"
        )
    }

    fun verifyPasswordResetCode(email: String, code: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.verifyPasswordResetCode(email, code) },
            errorMessage = "Failed to verify password reset code"
        )
    }

    fun resetPassword(email: String, oldPassword: String?, newPassword: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = {userApiService.resetPassword(email, oldPassword, newPassword)},
            errorMessage = "Failed to reset password"
        )
    }

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

    fun setTermsAgreementStatus(userId: String, agreed: Boolean): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.setTermsAgreementStatus(userId, agreed) },
            errorMessage = "Failed to set terms agreement status for $userId"
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

    fun getAllAdmins(): Flow<Result<List<Admin>>> = flow {
        handleApiResponse(
            call = { userApiService.getAllAdmins() },
            errorMessage = "Failed to get all admins"
        )
    }

}