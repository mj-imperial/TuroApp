package com.example.turomobileapp.objects

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
object LocalDateTimeAdapter {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return dateTime.format(fmt)
    }

    @FromJson
    fun fromJson(json: String): LocalDateTime {
        return LocalDateTime.parse(json, fmt)
    }
}
