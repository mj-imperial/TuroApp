package com.example.turomobileapp.objects

import android.util.Base64
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class Base64ByteArrayAdapter {
    @FromJson
    fun fromJson(base64: String?): ByteArray? {
        return base64?.let { Base64.decode(it, Base64.DEFAULT) }
    }

    @ToJson
    fun toJson(bytes: ByteArray?): String? {
        return bytes?.let { Base64.encodeToString(it, Base64.NO_WRAP) }
    }
}