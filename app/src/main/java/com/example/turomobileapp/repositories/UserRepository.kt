package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.UserApiService
import com.example.turomobileapp.models.Admin
import com.example.turomobileapp.models.CodeVerificationRequest
import com.example.turomobileapp.models.EmailRequest
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject

class UserRepository @Inject constructor(private val userApiService: UserApiService) {

    fun login(userEmail: String, password: String): Flow<Result<User>> = flow {
        try {
            val hashedPassword = hashPassword(password)
            val response = userApiService.login(userEmail, hashedPassword)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    emit(Result.Success(user))
                } ?: run {
                    emit(Result.Failure(Exception("Login successful, but user data is null")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Login failed: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun logout(): Flow<Result<Unit>> = flow {
        try{
            val response = userApiService.logout()
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    emit(Result.Failure(Exception("Logout failed: ${response.code()} - $errorBody")))
                }
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getUserById(userId: String): Flow<Result<User>> = flow {
        try {
            val response = userApiService.getUserById(userId)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    emit(Result.Success(user))
                } ?: emit(Result.Failure(Exception("User not found")))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get user by ID: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }


    fun getUserByEmail(email: String): Flow<Result<User>> = flow {
        try{
            val response = userApiService.getUserByEmail(email)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    emit(Result.Success(user))
                } ?: run {
                    emit(Result.Failure(Exception("User not found")))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.Failure(Exception("Failed to get user by email: ${response.code()} - $errorBody")))
            }
        } catch (e: IOException) {
            emit(Result.Failure(e))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
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

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun hashPassword(password: String): String = withContext(Dispatchers.IO) {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        digest.toHexString()
    }

    private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }

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