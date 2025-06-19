package com.example.turomobileapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun AppScaffold(
    navController: NavController,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    windowInfo: WindowInfo,
    sessionManager: SessionManager,
    hasFloatingActionButton: Boolean = false,
    items: List<ButtonItems> = emptyList(),
    content: @Composable (PaddingValues) -> Unit
) {
    val heading2 = ResponsiveFont.heading2(windowInfo)
    val body = ResponsiveFont.body(windowInfo)
    val subtitle = ResponsiveFont.subtitle(windowInfo)
    val caption = ResponsiveFont.caption(windowInfo)

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val firstName by sessionManager.firstName.collectAsState(initial = "")
    val lastName by sessionManager.lastName.collectAsState(initial = "")
    val email by sessionManager.email.collectAsState(initial = "")
    val profilePic by sessionManager.profilePic.collectAsState(initial = null)
    val loginViewModel: LoginViewModel = hiltViewModel()

    val barHeight = when(windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.10f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.08f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.06f
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideDrawer(
                navController = navController,
                onCloseClick = { scope.launch { drawerState.close() } },
                onLogOut = {
                    loginViewModel.logout()
                },
                windowInfo = rememberWindowInfo(),
                heading2 = heading2,
                body = body,
                profilePic = profilePic,
                firstName = firstName.toString(),
                lastName = lastName.toString(),
                email = email.toString()
            )
        }) {
        Scaffold(
            floatingActionButton = {
                if (hasFloatingActionButton) CustomFloatingActionButton(items)
            },
            topBar = {
                TopNavigationBar(
                    canNavigateBack = canNavigateBack,
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route)
                    },
                    barHeight = barHeight,
                    body = body,
                    subtitle = subtitle,
                    hasNotification = false, //TODO add notification logic
                    navigateUp = navigateUp,
                    onHamburgerClick = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = modifier,
                )
        },bottomBar = {
            BottomNavigationBar(
                navController = navController,
                barHeight = barHeight,
                caption = caption
            )
        }) { innerPadding ->
            content(innerPadding)
        }
    }
}