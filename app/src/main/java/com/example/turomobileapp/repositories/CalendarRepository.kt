package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.CalendarApiService
import com.example.turomobileapp.models.Activity
import com.example.turomobileapp.models.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class CalendarRepository @Inject constructor(private val calendarApiService: CalendarApiService) {

    fun getAllCalendarEvents(page: Int = 0, pageSize: Int = 20, startDate: String?, endDate: String?): Flow<Result<List<CalendarEvent>>> = flow {
        try {
            val response = calendarApiService.getAllCalendarEvents(page, pageSize, startDate, endDate)
            if (response.isSuccessful){
                val events = response.body()
                events?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllCalendarEvents Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all calendar events: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCalendarEvent(eventId: String): Flow<Result<CalendarEvent>> = flow {
        try {
            val response = calendarApiService.getCalendarEvent(eventId)
            if (response.isSuccessful){
                val event = response.body()
                event?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCalendarEvent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get calendar event: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun addCalendarEvent(event: CalendarEvent): Flow<Result<CalendarEvent>> = flow {
        try {
            val response = calendarApiService.addCalendarEvent(event)
            if (response.isSuccessful){
                val event = response.body()
                event?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("addCalendarEvent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to add calendar event: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateCalendarEvent(eventId: String, event: CalendarEvent): Flow<Result<Unit>> = flow {
        try {
            val response = calendarApiService.updateCalendarEvent(eventId, event)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update calendar event: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteCalendarEvent(eventId: String): Flow<Result<Unit>> = flow {
        try {
            val response = calendarApiService.deleteCalendarEvent(eventId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete calendar event: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCalendarEventsForUser(userId: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<CalendarEvent>>> = flow {
        try {
            val response = calendarApiService.getCalendarEventsForUser(userId, page, pageSize)
            if (response.isSuccessful){
                val events = response.body()
                events?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCalendarEventsForUser Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get calendar events for user: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCalendarEventsByDateRange(startDate: String, endDate: String, page: Int = 0, pageSize: Int = 20): Flow<Result<List<CalendarEvent>>> = flow {
        try {
            val response = calendarApiService.getCalendarEventsByDateRange(startDate, endDate, page, pageSize)
            if (response.isSuccessful){
                val events = response.body()
                events?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCalendarEventsByDateRange Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get calendar events by date range: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isCalendarEventDuplicate(calendarEvent: CalendarEvent): Flow<Result<Boolean>> = flow {
        try {
            val response = calendarApiService.isCalendarEventDuplicate(calendarEvent)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isCalendarEventDuplicate Response Body is Empty")))
            }else{
                val errorMessage = "Failed to check if calendar event is a duplicate: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun createCalendarEventFromActivity(activity: Activity): Flow<Result<CalendarEvent>> = flow {
        try {
            val response = calendarApiService.createCalendarEventFromActivity(activity)
            if (response.isSuccessful){
                val activity = response.body()
                activity?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isCalendarEventDuplicate Response Body is Empty")))
            }else{
                val errorMessage = "Failed to create calendar event from activity: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}