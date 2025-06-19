package com.example.turomobileapp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale


//BlobImage(module.modulePicture, modifier = Modifier.size(100.dp))
@Composable
fun BlobImage(
    byteArray: ByteArray?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alpha: Float = 1f
) {
    val bitmap = remember(byteArray) {
        byteArray?.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }?.asImageBitmap()
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            alpha = alpha
        )
    }
}