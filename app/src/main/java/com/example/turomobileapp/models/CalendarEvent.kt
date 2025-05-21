package com.example.turomobileapp.models

import com.example.turomobileapp.enums.EventType
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class CalendarEvent(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("date") val date: Date,
    @SerializedName("event_type") val eventType: EventType,
    @SerializedName("is_urgent") var isUrgent: Boolean = false,
    @SerializedName("location") val location: String
)

@JsonClass(generateAdapter = true)
data class CalendarEventsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "events") val events: List<CalendarResponse>
)

@JsonClass(generateAdapter = true)
data class CalendarResponse(
    @Json(name = "event_id") val eventId: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "date") val date: Date,
    @Json(name = "event_type") val eventType: EventType,
    @Json(name = "is_urgent") var isUrgent: Boolean,
    @Json(name = "location") val location: String
)