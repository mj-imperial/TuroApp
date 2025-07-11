package com.example.turomobileapp.ui.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.turomobileapp.R
import java.io.File

@Composable
fun BlobFileViewer(
    windowInfo: WindowInfo,
    fileBytes: ByteArray?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (fileBytes?.isNotEmpty() == true) {
        val tempFile = remember(fileBytes) {
            File.createTempFile("file_preview_", ".pdf", context.cacheDir).apply {
                writeBytes(fileBytes)
            }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )

        val intent = remember(uri, "application/pdf") {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        Text(
            text = "Open File",
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            textDecoration = TextDecoration.Underline,
            color = Color.Blue,
            modifier = Modifier
                .padding(8.dp)
                .clickable { context.startActivity(intent) }
        )
    } else {
        Text("No file available", modifier = modifier.padding(8.dp))
    }
}
