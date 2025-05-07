package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.UserApiService
import com.example.turomobileapp.models.Admin
import com.example.turomobileapp.models.CodeVerificationRequest
import com.example.turomobileapp.models.EmailRequest
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApiService: UserApiService) {

    fun login(userEmail: String, password: String): Flow<Result<User>> = flow {
        handleApiResponse(
            call = { userApiService.login(userEmail, password) },
            errorMessage = "Login failed"
        )
    }

    fun logout(): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { userApiService.logout() },
            errorMessage = "Logout failed"
        )
    }

    fun getUserById(userId: String): Flow<Result<User>> = flow {
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
        try{
            val response = userApiService.updateUser(userId, user)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("User update failed: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateUserProfilePic(userId: String, imageStream: InputStream, mimeType: String): Flow<Result<Unit>> = flow {
        try{
            val requestFile = imageStream.readBytes().toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", "profile_pic", requestFile)

            val response = userApiService.updateProfilePic(userId, part)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Profile picture update failed: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun requestPasswordReset(email: String): Flow<Result<Unit>> = flow {
        try{
            val emailRequest = EmailRequest(email)
            val response = userApiService.requestPasswordReset(emailRequest)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Password reset request failed: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun verifyPasswordResetCode(
        userId: String,
        code: String,
        newPassword: String?
    ): Flow<Result<Unit>> = flow {
        try {
            val request = CodeVerificationRequest(
                userId = userId,
                code = code,
                newPassword = newPassword
            )

            val response = userApiService.verifyPasswordResetCode(request)

            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Verification failed: ${response.code()} - $errorBody")))
            }

        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }


    fun getDefaultPasswordChangeStatus(userId: String): Flow<Result<Boolean>> = flow {
        try{
            val userResponse = userApiService.getUserById(userId)
            if (userResponse.isSuccessful) {
                val user = userResponse.body()
                if (user != null) {
                    emit(Result.Success(user.requiresPasswordChange))
                } else {
                    emit(Result.Failure(Exception("User not found")))
                }
            } else {
                val errorBody = userResponse.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get user: ${userResponse.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getTermsAgreementStatus(userId: String): Flow<Result<Boolean>> = flow {
        try{
            val userResponse = userApiService.getUserById(userId)
            if (userResponse.isSuccessful) {
                val user = userResponse.body()
                if (user != null) {
                    emit(Result.Success(user.agreedToTerms))
                } else {
                    emit(Result.Failure(Exception("User not found")))
                }
            } else {
                val errorBody = userResponse.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get user: ${userResponse.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun setTermsAgreementStatus(userId: String, agreed: Boolean): Flow<Result<Unit>> = flow {
        try{
            val response = userApiService.setTermsAgreementStatus(userId, agreed)
            if (response.isSuccessful) {
                emit(Result.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to set terms agreement status: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllStudents(): Flow<Result<List<Student>>> = flow {
        try{
            val response = userApiService.getAllStudents()
            if (response.isSuccessful) {
                response.body()?.let { students ->
                    emit(Result.Success(students))
                } ?: run {
                    emit(Result.Failure(Exception("No students found")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get all students: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllTeachers(): Flow<Result<List<Teacher>>> = flow {
        try{
            val response = userApiService.getAllTeachers()
            if (response.isSuccessful) {
                response.body()?.let { teachers ->
                    emit(Result.Success(teachers))
                } ?: run {
                    emit(Result.Failure(Exception("No teachers found")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get all teachers: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getAllAdmins(): Flow<Result<List<Admin>>> = flow {
        try{
            val response = userApiService.getAllAdmins()
            if (response.isSuccessful) {
                response.body()?.let { admins ->
                    emit(Result.Success(admins))
                } ?: run {
                    emit(Result.Failure(Exception("No admins found")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get all admins: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

}