package com.example.turomobileapp.ui.screens

import AppScaffold
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.viewmodels.SessionManager

@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    sessionManager: SessionManager
){
    val windowInfo = rememberWindowInfo()
    val barHeight = when(windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.10f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.08f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.06f
    }
    val height = windo

    AppScaffold(
        navController = navController,
        barHeight = barHeight,
        modifier = Modifier,
        canNavigateBack = true,
        navigateUp = {
            //TODO navigate back
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = TODO(),
    )
}

@Composable
fun ProfileContent(){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}