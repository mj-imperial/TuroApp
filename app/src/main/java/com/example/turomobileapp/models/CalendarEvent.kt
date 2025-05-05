package com.example.turoapp.models

import com.example.turoapp.enums.EventType
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class CalendarEvent(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("date") val date: Instant,
    @SerializedName("event_type") val eventType: EventType,
    @SerializedName("is_urgent") var isUrgent: Boolean = false,
    @SerializedName("location") val location: String
)