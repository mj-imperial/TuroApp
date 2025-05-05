package com.example.turoapp.interfaces

import com.example.turoapp.models.Badges
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BadgesApiService {
    @GET("/badges")
    suspend fun getAllBadges(): Response<List<Badges>>

    @GET("/students/{studentId}/badges")
    suspend fun getAllBadgesForStudent(
        @Path("studentId") studentId: String
    ): Response<List<Badges>>

    @GET("/badges/{badgeId}")
    suspend fun getBadge(
        @Path("badgeId") badgeId: String
    ): Response<Badges>

    @POST("/students/{studentId}/badges/{badgeId}")
    suspend fun assignBadgeToStudent(
        @Path("studentId") studentId: String,
        @Path("badgeId") badgeId: String
    ): Response<ResponseBody>
}