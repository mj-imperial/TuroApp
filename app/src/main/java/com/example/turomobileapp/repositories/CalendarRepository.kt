package com.example.turomobileapp.repositories

import com.example.turomobileapp.helperfunctions.requestAndMap
import com.example.turomobileapp.interfaces.CalendarApiService
import com.example.turomobileapp.models.CalendarResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalendarRepository @Inject constructor(private val calendarApiService: CalendarApiService) {
    fun getCalendarEventsForStudent(userId: String): Flow<Result<List<CalendarResponse>>> =
        requestAndMap(
            call = { calendarApiService.getCalendarEventsForStudent(userId) },
            mapper = {dto -> dto.events}
        )

    fun getCalendarEventsForTeacher(userId: String): Flow<Result<List<CalendarResponse>>> =
        requestAndMap(
            call = { calendarApiService.getCalendarEventsForTeacher(userId) },
            mapper = {dto -> dto.events}
        )
}