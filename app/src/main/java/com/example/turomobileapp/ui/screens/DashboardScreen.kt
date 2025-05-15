package com.example.turomobileapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
    val caption = ResponsiveFont.caption(windowInfo)
    val barHeight = when(windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.10f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.08f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.06f
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

        }
    ) {
        Scaffold(
            topBar = {
                TopNavigationBar(
                    canNavigateBack = false,
                    body = body,
                    subtitle = subtitle,
                    barHeight = barHeight,
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    hasNotification = false //TODO add notification logic
                )
            },
            bottomBar = { BottomNavigationBar(navController, barHeight, caption) },
            containerColor = MainWhite,
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {  }
        }
    }


}

@Preview
@Composable
fun DashboardPreview(){
    DashboardScreen()
}