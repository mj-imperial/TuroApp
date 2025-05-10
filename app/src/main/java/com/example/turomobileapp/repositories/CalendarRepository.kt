package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.handleApiResponse
import com.example.turomobileapp.interfaces.CalendarApiService
import com.example.turomobileapp.models.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CalendarRepository @Inject constructor(private val calendarApiService: CalendarApiService) {

    fun getAllCalendarEvents(page: Int = 0, pageSize: Int = 20, startDate: String?, endDate: String?): Flow<Result<List<CalendarEvent>>> = flow {
        handleApiResponse(
            call = { calendarApiService.getAllCalendarEvents(page, pageSize, startDate, endDate) },
            errorMessage = "Failed to get all calendar events"
        )
    }

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

    fun getCalendarEventsForUser(userId: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<CalendarEvent>>> = flow {
        handleApiResponse(
            call = { calendarApiService.getCalendarEventsForUser(userId, page, pageSize) },
            errorMessage = "Failed to get calendar events for user $userId"
        )
    }

    fun getCalendarEventsByDateRange(userId: String, startDate: String, endDate: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<CalendarEvent>>> = flow {
        handleApiResponse(
            call = {
                calendarApiService.getCalendarEventsByDateRange(
                    userId,
                    startDate,
                    endDate,
                    page,
                    pageSize
                )
            },
            errorMessage = "Failed to get calendar events by date range for user $userId"
        )
    }

    fun isCalendarEventDuplicate(calendarEvent: CalendarEvent): Flow<Result<Boolean>> = flow {
        handleApiResponse(
            call = { calendarApiService.isCalendarEventDuplicate(calendarEvent) },
            errorMessage = "Failed to check if calendar event $calendarEvent is a duplicate"
        )
    }
}