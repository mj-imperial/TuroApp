package com.example.turomobileapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turomobileapp.ui.screens.CalendarScreen
import com.example.turomobileapp.ui.screens.ChangePasswordScreen
import com.example.turomobileapp.ui.screens.DashboardScreen
import com.example.turomobileapp.ui.screens.LoginScreen
import com.example.turomobileapp.ui.screens.ProfileScreen
import com.example.turomobileapp.ui.screens.SplashScreen
import com.example.turomobileapp.ui.screens.TermsAgreementScreen
import com.example.turomobileapp.viewmodels.SessionManager

@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(navController)
        }
        composable(Screen.TermsAgreement.route) {
            TermsAgreementScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, sessionManager)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, sessionManager)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
    }
}