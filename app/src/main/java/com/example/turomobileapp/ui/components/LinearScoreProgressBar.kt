package com.example.turomobileapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green

@Composable
fun LinearScoreProgressBar(
    scorePercentage: Double,
    animationDuration: Int = 1000
) {
    val targetFraction = (scorePercentage.toFloat() / 100f).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = animationDuration),
        label = "progress"
    )

    val backgroundColor = TextBlack
    val progressColor = when {
        scorePercentage <= 30 -> MainRed
        scorePercentage < 60 -> MainOrange
        else -> green
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(backgroundColor)
            .fillMaxWidth()
            .height(40.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(progressColor)
                .clip(RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "${scorePercentage.toInt()}%",
                color = MainWhite,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
