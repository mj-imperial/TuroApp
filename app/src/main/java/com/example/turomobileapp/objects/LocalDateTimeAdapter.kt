package com.example.turomobileapp.objects

import android.os.Build
import androidx.annotation.RequiresApi
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
object LocalDateTimeAdapter {
    private val isoFmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val dbFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @ToJson fun toJson(dt: LocalDateTime): String = dt.format(isoFmt)

    @FromJson fun fromJson(raw: String): LocalDateTime = runCatching {
        LocalDateTime.parse(raw, isoFmt)
    }.getOrElse {
        LocalDateTime.parse(raw, dbFmt)
    }
}
