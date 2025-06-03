package com.example.turomobileapp.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green

@Composable
fun PopupWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    uri: Uri,
    imageDescription: String,
    height: Dp,
    width: Dp,
    padding: Dp,
    roundedCornerShape: Dp,
    errorMessage: String?
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .width(width)
                .height(height)
                .padding(padding),
            shape = RoundedCornerShape(roundedCornerShape),
            colors = CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MainWhite),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = uri,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(height * 0.3f)
                        .clip(CircleShape),
                    contentDescription = "Preview"
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = imageDescription,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = "Dismiss",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = TextBlack
                        )
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = "Confirm",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = green
                        )
                    }
                }

                errorMessage?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text("Error: $err",color = Color.Red)
                }
            }
        }
    }
}