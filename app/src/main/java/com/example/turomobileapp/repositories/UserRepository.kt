package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.UserApiService
import com.example.turomobileapp.models.ApiResponse
import com.example.turomobileapp.models.CatchUpClassResponse
import com.example.turomobileapp.models.StudentProfileProgress
import com.example.turomobileapp.models.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApiService: UserApiService) {

    fun login(email: String, password: String): Flow<Result<UserResponse>> =
        handleApiResponse(
            call   = { userApiService.login(email, password) },
            errorMessage = "Login failed"
        )

    fun logout(): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.logout() },
            errorMessage = "Logout failed"
        )
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

    fun getStudentProfileProgress(studentId: String): Flow<Result<StudentProfileProgress>> =
        handleApiResponse(
            call = { userApiService.getStudentProfileProgress(studentId) },
            errorMessage = "Failed to get student profile progress for $studentId"
        )

    fun checkIfStudentIsCatchUp(studentId: String): Flow<Result<CatchUpClassResponse>> =
        handleApiResponse(
            call = { userApiService.checkIfStudentIsCatchUp(studentId) },
            errorMessage = "Failed to check if student $studentId is in the Catch-up class"
        )
}