package com.example.turomobileapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.green

@Composable
fun CircularScoreProgressBar(
    scorePercentage: Double,
    animationDuration: Int = 1000,
    diameter: Dp,
    fontSize: TextUnit
) {
    val gradient = Brush.linearGradient(listOf(MainRed, MainOrange, green))
    var animFinished by remember { mutableStateOf(false) }
    val targetFraction: Float = (scorePercentage.toFloat() / 100f).coerceIn(0f, 1f)
    val currentFraction by animateFloatAsState(
        targetValue = if (animFinished) targetFraction else 0f,
        animationSpec = tween(durationMillis = animationDuration)
    )
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = "${scorePercentage.toInt()}%",
        style = TextStyle(
            fontSize = fontSize,
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Medium
        )
    )
    val textSize = textLayoutResult.size
    LaunchedEffect(Unit) { animFinished = true }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(diameter)) {
        Canvas(modifier = Modifier.size(diameter)) {
            drawArc(
                brush = gradient,
                startAngle = -90f,
                sweepAngle = 360f * currentFraction,
                useCenter = false,
                style = Stroke(7.dp.toPx(), cap = StrokeCap.Round)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = size.width / 2 - textSize.width / 2f,
                    y = size.height / 2 - textSize.height / 2f
                )
            )
        }
    }
}
