package com.example.turoapp.interfaces

import com.example.turoapp.models.Activity
import com.example.turoapp.models.CalendarEvent
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CalendarApiService {
    @GET("/calendar/events")
    suspend fun getAllCalendarEvents(
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
    ): Response<List<CalendarEvent>>

    @GET("/calendar/events/{eventId}")
    suspend fun getCalendarEvent(
        @Path("eventId") eventId: String
    ): Response<CalendarEvent>

    @POST("/calendar/events")
    suspend fun addCalendarEvent(
        @Body event: CalendarEvent
    ): Response<CalendarEvent>

    @PUT("/calendar/events/{eventId}")
    suspend fun updateCalendarEvent(
        @Path("eventId") eventId: String,
        @Body event: CalendarEvent
    ): Response<ResponseBody>

    @DELETE("/calendar/events/{eventId}")
    suspend fun deleteCalendarEvent(
        @Path("eventId") eventId: String
    ): Response<ResponseBody>

    @GET("/calendar/users/{userId}/events")
    suspend fun getCalendarEventsForUser(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Response<List<CalendarEvent>>

    @GET("/calendar/events")
    suspend fun getCalendarEventsByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 20
    ): Response<List<CalendarEvent>>

    @POST("/calendar/events/validateDuplication")
    suspend fun isCalendarEventDuplicate(
        @Body calendarEvent: CalendarEvent
    ): Response<Boolean>

    @POST("/calendar/events/fromActivity")
    suspend fun createCalendarEventFromActivity(
        @Body activityId: Activity,
    ): Response<CalendarEvent>
}