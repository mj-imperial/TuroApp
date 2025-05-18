package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import kotlinx.coroutines.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SplashScreen(
    navController: NavController
){
    var visible by remember { mutableStateOf(true) }
    val windowInfo = rememberWindowInfo()

    AnimatedVisibility(
        visible = visible,
        enter   = EnterTransition.None,
        exit  = fadeOut(animationSpec = tween(durationMillis = 300))
    ){
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MainRed)
                .systemBarsPadding()
        ){
            val logoSize = when (windowInfo.screenWidthInfo) {
                WindowInfo.WindowType.Compact  -> windowInfo.screenWidth * 0.3f
                WindowInfo.WindowType.Medium   -> windowInfo.screenWidth * 0.4f
                WindowInfo.WindowType.Expanded -> windowInfo.screenWidth * 0.5f
            }

            val headingSize    = ResponsiveFont.title(windowInfo)
            val subheadingSize = ResponsiveFont.heading2(windowInfo)

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(R.drawable.book_open),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize),
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    text = stringResource(R.string.TURO),
                    fontSize = headingSize,
                    fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                    color = MainWhite,
                    modifier = Modifier
                        .width(logoSize * 0.9f),
                    textAlign = TextAlign.Start
                )

                Text(
                    text = stringResource(R.string.byGSCS),
                    fontSize = subheadingSize,
                    fontFamily = FontFamily(Font(R.font.alexandria_bold)),
                    color = MainOrange
                )
            }
        }
    }


    LaunchedEffect(Unit) {
        delay(800)
        visible = false
        delay(300)
        navController.navigate(Screen.Login.route) {
            popUpTo("splash") { inclusive = true }
        }
    }
}

//@Preview
//@Composable
//fun SplashScreenPreview() {
//    SplashScreen()
//}