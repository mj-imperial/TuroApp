package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.CalendarEventsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CalendarApiService {
    @GET("get_calendar_events_for_student.php")
    suspend fun getCalendarEventsForStudent(
        @Query("user_id") userId: String
    ): Response<CalendarEventsResponse>

    @GET("get_calendar_events_for_teacher.php")
    suspend fun getCalendarEventsForTeacher(
        @Query("user_id") userId: String
    ): Response<CalendarEventsResponse>
}