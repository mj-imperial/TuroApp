package com.example.turomobileapp.ui.screens

import AppScaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.viewmodels.SessionManager

private const val CALENDAR_ROWS = 5
private const val CALENDAR_COLUMNS = 7

@Composable
fun CalendarScreen(
    navController: NavController,
    sessionManager: SessionManager
){
    val windowInfo = rememberWindowInfo()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = TODO()
    )
}