package com.example.turomobileapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turomobileapp.ui.screens.ChangePasswordScreen
import com.example.turomobileapp.ui.screens.DashboardScreen
import com.example.turomobileapp.ui.screens.LoginScreen
import com.example.turomobileapp.ui.screens.SplashScreen
import com.example.turomobileapp.ui.screens.TermsAgreementScreen

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
        composable(
            route = Screen.ChangePassword.route,
            arguments = listOf(
                navArgument("requiresChange") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            ChangePasswordScreen(navController)
        }
        composable(
            route = Screen.TermsAgreement.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                },
                navArgument("agreedToTerms") {
                    type = NavType.BoolType
                }
            )
        ) {
            TermsAgreementScreen(navController)
        }
        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) {
            DashboardScreen(navController)
        }
    }
}