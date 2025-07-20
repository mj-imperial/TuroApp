package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class CalendarEventsResponse(
    @Json(name = "events") val events: List<CalendarResponse>
)

@JsonClass(generateAdapter = true)
data class CalendarResponse(
    @Json(name = "course_code") val courseCode: String,
    @Json(name = "event_id") val eventId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "date") val date: LocalDateTime,
    @Json(name = "event_type_name") val eventType: String,
    @Json(name = "is_urgent") val isUrgentInt: Int,
    @Json(name = "location") val location: String
){
    val isUrgent: Boolean get() = isUrgentInt != 0
}