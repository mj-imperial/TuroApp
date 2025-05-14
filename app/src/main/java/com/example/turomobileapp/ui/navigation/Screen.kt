package com.example.turomobileapp.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangePassword: Screen("changePassword?requiresChange={requiresChange}&email={email}"){
        fun createRoute(requiresChange: Boolean) =
            "changePassword?requiresChange=$requiresChange"
        fun createRoute(requiresChange: Boolean, email: String) =
            "changePassword?requiresChange=$requiresChange&email=${Uri.encode(email)}"
    }
    object Dashboard: Screen("dashboard_screen/{userId}")
    object Calendar: Screen("calendar_screen")
    object MiniGames: Screen("mini_games_screen")
    object Leaderboard: Screen("leaderboard_screen")
    object Inbox: Screen("inbox_screen")
    object Notification: Screen("notification_screen")
}