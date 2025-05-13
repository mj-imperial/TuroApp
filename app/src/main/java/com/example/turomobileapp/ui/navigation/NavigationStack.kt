package com.example.turomobileapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turomobileapp.ui.screens.ChangeDefaultPasswordScreen
import com.example.turomobileapp.ui.screens.HomeScreen
import com.example.turomobileapp.ui.screens.LoginScreen
import com.example.turomobileapp.ui.screens.SplashScreen

@Composable
fun NavigationStack(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ChangeDefaultPassword.route) {
            ChangeDefaultPasswordScreen(navController = navController)
        }
        composable(route = Screen.Home.route){
            HomeScreen()
        }
    }
}