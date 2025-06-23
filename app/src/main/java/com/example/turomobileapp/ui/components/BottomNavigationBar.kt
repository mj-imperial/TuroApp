package com.example.turomobileapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.navigation.navigationItemsStudent
import com.example.turomobileapp.ui.navigation.navigationItemsTeacher
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite

@Composable
fun BottomNavigationBar(
    navController: NavController,
    barHeight: Dp,
    caption: TextUnit,
    isStudent: Boolean
){
    NavigationBar(
        containerColor = MainRed,
        contentColor = MainWhite,
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val iconSize = barHeight * 0.5f

        val navigationItems = if (isStudent) navigationItemsStudent else navigationItemsTeacher

        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.route == Screen.Dashboard.route) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else if (!isSelected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        tint = if (isSelected) MainOrange else MainWhite,
                        contentDescription = stringResource(item.title),
                        modifier = Modifier.size(iconSize)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.title),
                        fontSize = caption,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        color = if (isSelected) MainOrange else MainWhite
                    )
                },
                alwaysShowLabel = true,
                modifier = Modifier.height(barHeight),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MainOrange,
                    unselectedIconColor = MainWhite,
                    selectedTextColor   = MainOrange,
                    unselectedTextColor = MainWhite,
                    indicatorColor      = Color.Transparent
                )
            )
        }
    }
}
