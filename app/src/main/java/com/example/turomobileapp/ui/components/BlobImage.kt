package com.example.turomobileapp.ui.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale

@Composable
fun BlobImage(
    byteArray: ByteArray?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alpha: Float = 1f
) {
    val bitmap = remember(byteArray) {
        try {
            byteArray?.takeIf { it.isNotEmpty() }?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size)?.asImageBitmap()
            }
        } catch (e: Exception) {
            Log.e("BlobImage", "Image decode failed: ${e.localizedMessage}")
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            alpha = alpha
        )
    } else {
        Log.w("BlobImage", "BlobImage bitmap is null — nothing rendered")
    }
}