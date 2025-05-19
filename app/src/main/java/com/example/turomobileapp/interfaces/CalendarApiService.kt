package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CalendarEvent
import com.example.turomobileapp.models.CalendarEventsResponse
import com.example.turomobileapp.models.CalendarResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate
import java.util.Date

interface CalendarApiService {
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

    @GET("get_calendar_events.php")
    suspend fun getCalendarEventsByDate(
        @Query("user_id") userId: String,
        @Query("date") date: LocalDate
    ): Response<CalendarEventsResponse>

    @POST("/calendar/events/validateDuplication")
    suspend fun isCalendarEventDuplicate(
        @Body calendarEvent: CalendarEvent
    ): Response<Boolean>
}