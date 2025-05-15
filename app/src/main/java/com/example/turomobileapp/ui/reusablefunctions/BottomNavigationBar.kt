package com.example.turomobileapp.ui.reusablefunctions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.turomobileapp.ui.navigation.navigationItems
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.theme.MainOrange

@Composable
fun BottomNavigationBar(
    navController: NavController,
    barHeight: Dp,
    caption: TextUnit
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

        navigationItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState    = true
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
                modifier = Modifier.height(barHeight)
            )
        }
    }
}

//@Preview
//@Composable
//fun BottomNavigationBarPreview(){
//    BottomNavigationBar()
//}