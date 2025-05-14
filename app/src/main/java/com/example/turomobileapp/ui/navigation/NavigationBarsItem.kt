package com.example.turomobileapp.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.turomobileapp.R

data class NavigationBarsItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
)

val navigationItems = listOf(
    NavigationBarsItem(
        title = R.string.Dashboard,
        icon = R.drawable.dashboard_icon,
        route = Screen.Dashboard.route
    ),
    NavigationBarsItem(
        title = R.string.Calendar,
        icon = R.drawable.calendar_icon,
        route = Screen.Calendar.route
    ),
    NavigationBarsItem(
        title = R.string.MiniGames,
        icon = R.drawable.minigames_icon,
        route = Screen.MiniGames.route
    ),
    NavigationBarsItem(
        title = R.string.Leaderboard,
        icon = R.drawable.leaderboard_icon,
        route = Screen.Leaderboard.route
    ),
    NavigationBarsItem(
        title = R.string.Inbox,
        icon = R.drawable.inbox_icon,
        route = Screen.Inbox.route
    )
)