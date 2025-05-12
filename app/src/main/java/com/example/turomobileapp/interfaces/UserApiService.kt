package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Admin
import com.example.turomobileapp.models.LoginResponse
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") userEmail: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("/logout")
    suspend fun logout(): Response<ResponseBody>

    @GET("/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): Response<User>

    @GET("/users/{userEmail}")
    suspend fun getUserByEmail(
        @Path("userEmail") userEmail: String
    ): Response<User>

    @GET("/teachers")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    @GET("/students")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("/admins")
    suspend fun getAllAdmins(): Response<List<Admin>>

    @FormUrlEncoded
    @POST("request_password_reset")
    suspend fun requestPasswordReset(
        @Path("userId") userId: String,
        @Field("email") email: String?
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("verify_password_reset_code")
    suspend fun verifyPasswordResetCode(
        @Field("email") email: String,
        @Field("code") code: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @PATCH("change_password")
    suspend fun resetPassword(
        @Path("email") email: String,
        @Field("oldPassword") oldPassword: String?,
        @Field("newPassword") newPassword: String
    ): Response<ResponseBody>

    @PUT("/users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: String,
        @Body user: User
    ): Response<ResponseBody>

    @PATCH("/users/{userId}/profilePic")
    @Multipart
    suspend fun updateProfilePic(
        @Path("userId") userId: String,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @PATCH("/users/{userId}/termsAgreement")
    suspend fun setTermsAgreementStatus(
        @Path("userId") userId: String,
        @Query("agreedToTerms") agreedToTerms: Boolean
    ): Response<ResponseBody>

}