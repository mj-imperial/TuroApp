package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CalendarApiService
import com.example.turomobileapp.models.CalendarEvent
import com.example.turomobileapp.models.CalendarResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class CalendarRepository @Inject constructor(private val calendarApiService: CalendarApiService) {

    fun getCalendarEvent(eventId: String): Flow<Result<CalendarEvent>> = flow {
        handleApiResponse(
            call = { calendarApiService.getCalendarEvent(eventId) },
            errorMessage = "Failed to get calendar event $eventId"
        )
    }

    fun addCalendarEvent(event: CalendarEvent): Flow<Result<CalendarEvent>> = flow {
        handleApiResponse(
            call = { calendarApiService.addCalendarEvent(event) },
            errorMessage = "Failed to add calendar event $event"
        )
    }

    fun updateCalendarEvent(eventId: String, event: CalendarEvent): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { calendarApiService.updateCalendarEvent(eventId, event) },
            errorMessage = "Failed to update calendar event $eventId"
        )
    }

    fun deleteCalendarEvent(eventId: String): Flow<Result<Unit>> = flow {
        handleApiResponse(
            call = { calendarApiService.deleteCalendarEvent(eventId) },
            errorMessage = "Failed to delete calendar event $eventId"
        )
    }

    fun getCalendarEventsForUser(userId: String): Flow<Result<List<CalendarResponse>>> =
        requestAndMap(
            call = { calendarApiService.getCalendarEventsForUser(userId) },
            mapper = {dto -> dto.events}
        )


    fun getCalendarEventsByDate(userId: String, date: LocalDate): Flow<Result<List<CalendarResponse>>> =
        requestAndMap(
            call = { calendarApiService.getCalendarEventsByDate(userId,date) },
            mapper = { dto -> dto.events },
        )

    fun isCalendarEventDuplicate(calendarEvent: CalendarEvent): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { calendarApiService.isCalendarEventDuplicate(calendarEvent) },
            errorMessage = "Failed to check if calendar event $calendarEvent is a duplicate"
        )
    }
}