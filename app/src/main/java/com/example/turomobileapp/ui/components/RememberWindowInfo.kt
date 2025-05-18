package com.example.turomobileapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberWindowInfo(): WindowInfo{
    val configuration = LocalConfiguration.current
    return WindowInfo(
        screenWidthInfo = when {
            configuration.screenWidthDp < 600 -> WindowInfo.WindowType.Compact
            configuration.screenWidthDp < 840 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenHeightInfo = when{
            configuration.screenHeightDp < 480 -> WindowInfo.WindowType.Compact
            configuration.screenHeightDp < 900 -> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenWidth = configuration.screenWidthDp.dp,
        screenHeight = configuration.screenHeightDp.dp
    )
}

data class WindowInfo(
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType,
    val screenWidth: Dp,
    val screenHeight: Dp
){
    sealed class WindowType{
        object Compact: WindowType()
        object Medium: WindowType()
        object Expanded: WindowType()
    }
}

object ResponsiveFont {
    @Composable fun title(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 28.sp
        WindowInfo.WindowType.Medium   -> 32.sp
        WindowInfo.WindowType.Expanded -> 36.sp
    }
    @Composable fun heading1(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 24.sp
        WindowInfo.WindowType.Medium   -> 28.sp
        WindowInfo.WindowType.Expanded -> 32.sp
    }
    @Composable fun heading2(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 20.sp
        WindowInfo.WindowType.Medium   -> 24.sp
        WindowInfo.WindowType.Expanded -> 28.sp
    }
    @Composable fun heading3(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 18.sp
        WindowInfo.WindowType.Medium   -> 20.sp
        WindowInfo.WindowType.Expanded -> 24.sp
    }
    @Composable fun subheading(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 16.sp
        WindowInfo.WindowType.Medium   -> 18.sp
        WindowInfo.WindowType.Expanded -> 20.sp
    }
    @Composable fun body(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 14.sp
        WindowInfo.WindowType.Medium   -> 16.sp
        WindowInfo.WindowType.Expanded -> 18.sp
    }
    @Composable fun subtitle(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 12.sp
        WindowInfo.WindowType.Medium   -> 14.sp
        WindowInfo.WindowType.Expanded -> 16.sp
    }
    @Composable fun caption(window: WindowInfo): TextUnit = when (window.screenWidthInfo) {
        WindowInfo.WindowType.Compact  -> 8.sp
        WindowInfo.WindowType.Medium   -> 10.sp
        WindowInfo.WindowType.Expanded -> 12.sp
    }
}


