package com.example.turomobileapp.ui.navigation

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangeDefaultPassword: Screen("change_default_password_screen?requiresChange={requiresChange}")
    object Home: Screen("home_screen")
}