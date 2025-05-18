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
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.ui.components.BottomNavigationBar
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.SideDrawer
import com.example.turomobileapp.ui.components.TopNavigationBar
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun AppScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    windowInfo: WindowInfo,
    sessionManager: SessionManager
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
    val profilePic by sessionManager.profilePicUrl.collectAsState(initial = "")
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
                    loginViewModel::logout
                },
                windowInfo = rememberWindowInfo(),
                heading2 = heading2,
                body = body,
                profilePicUrl = profilePic.toString(),
                firstName = firstName.toString(),
                lastName = lastName.toString(),
                email = email.toString()
            )
        }) {
        Scaffold(topBar = {
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