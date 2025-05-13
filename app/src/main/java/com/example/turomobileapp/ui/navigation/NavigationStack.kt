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
            LoginScreen(
                onLoginSuccess = { userId: String, requiresChange: Boolean ->
                    navController.navigate(
                        "${Screen.ChangeDefaultPassword.route}/$userId" +
                                "?requiresChange=$requiresChange"
                    ) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                navController = navController,
            )
        }
        composable(
            route = Screen.ChangeDefaultPassword.route + "/{userId}?requiresChange={requiresChange}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("requiresChange") {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) { backStackEntry ->
            val requiresChange = backStackEntry
                .arguments!!
                .getBoolean("requiresChange")

            ChangeDefaultPasswordScreen(
                navController          = navController,
                requiresPasswordChange = requiresChange
            )
        }
        composable(route = Screen.Home.route){
            HomeScreen()
        }
    }
}