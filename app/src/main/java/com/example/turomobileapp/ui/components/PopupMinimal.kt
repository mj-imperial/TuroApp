package com.example.turomobileapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog

@Composable
fun PopupMinimal(
    onDismissRequest: () -> Unit,
    width: Dp,
    height: Dp,
    padding: Dp,
    roundedCornerShape: Dp,
    dialogText: String,
    fontFamily: FontFamily,
    fontSize: TextUnit,
    textColor: Color,
    cardColors: CardColors
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .width(width)
                .height(height)
                .padding(padding),
            shape = RoundedCornerShape(roundedCornerShape),
            colors = cardColors
        ) {
            Text(
                text = dialogText,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
                color = textColor,
                fontFamily = fontFamily,
                fontSize = fontSize
            )
        }
    }
}