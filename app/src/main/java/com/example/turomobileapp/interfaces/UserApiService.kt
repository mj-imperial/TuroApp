package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.Admin
import com.example.turomobileapp.models.ApiResponse
import com.example.turomobileapp.models.Student
import com.example.turomobileapp.models.Teacher
import com.example.turomobileapp.models.User
import com.example.turomobileapp.models.UserResponse
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
    @POST("login.php")
    suspend fun login(
        @Field("email") userEmail: String,
        @Field("password") password: String
    ): Response<UserResponse>

    @POST("/logout")
    suspend fun logout(): Response<ResponseBody>

    @GET("get_user_by_id.php")
    suspend fun getUserById(
        @Query("user_id") userId: String
    ): Response<UserResponse>

    @GET("/users/{userEmail}")
    suspend fun getUserByEmail(
        @Path("userEmail") userEmail: String
    ): Response<UserResponse>

    @GET("/teachers")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    @GET("/students")
    suspend fun getAllStudents(): Response<List<Student>>

    @GET("/admins")
    suspend fun getAllAdmins(): Response<List<Admin>>

    @FormUrlEncoded
    @POST("request_password_reset.php")
    suspend fun requestPasswordReset(
        @Field("email") email: String?
    ): Response<ApiResponse>

    @FormUrlEncoded
    @POST("verify_password_reset_code.php")
    suspend fun verifyPasswordResetCode(
        @Field("email") email: String,
        @Field("code") code: String
    ): Response<ApiResponse>

    @FormUrlEncoded
    @POST("change_password.php")
    suspend fun resetPassword(
        @Field("action") action: String,
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
    ): Response<ApiResponse>

    @FormUrlEncoded
    @POST("change_password.php")
    suspend fun changeDefaultPassword(
        @Field("action") action: String,
        @Field("email") email: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String
    ): Response<ApiResponse>

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

    @FormUrlEncoded
    @POST("terms_and_agreement.php")
    suspend fun setTermsAgreementStatus(
        @Field("user_id") userId: String,
        @Field("agreed_to_terms") agreedToTerms: Boolean
    ): Response<ApiResponse>

}