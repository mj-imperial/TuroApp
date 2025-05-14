package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.reusablefunctions.BottomNavigationBar
import com.example.turomobileapp.ui.reusablefunctions.ResponsiveFont
import com.example.turomobileapp.ui.reusablefunctions.TopNavigationBar
import com.example.turomobileapp.ui.reusablefunctions.WindowInfo
import com.example.turomobileapp.ui.reusablefunctions.rememberWindowInfo
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavController = rememberNavController(),
){
    val windowInfo = rememberWindowInfo()
    val body = ResponsiveFont.body(windowInfo)
    val subtitle = ResponsiveFont.subtitle(windowInfo)
    val barHeight = when(windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.10f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.08f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.06f
    }
    Scaffold(
        topBar = {
            TopNavigationBar(
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = {navController.navigate(Screen.Notification.route)}) {
                        Icon(
                            painter = painterResource(R.drawable.notifications_icon),
                            tint = MainWhite,
                            contentDescription = "Notifications"
                        )
                    }
                },
                body = body,
                subtitle = subtitle
            )
        },
        bottomBar = { BottomNavigationBar(navController, barHeight, subtitle) },
        containerColor = MainWhite
    ) {

    }
}

@Preview
@Composable
fun DashboardPreview(){
    DashboardScreen()
}