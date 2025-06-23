package com.example.turomobileapp.ui.screens.shared

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.enums.UserRole
import com.example.turomobileapp.models.CourseResponse
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: DashboardViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()

    val context = LocalContext.current
    var hasRequestedNotification by remember { mutableStateOf(false) }
    var openAlertDialog by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            openAlertDialog = true
        }
    }

    LaunchedEffect(Unit) {
        if (!hasRequestedNotification) {
            hasRequestedNotification = true
            val alreadyGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!alreadyGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    
    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                openAlertDialog = false
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            icon = painterResource(R.drawable.notification),
            title = {
                Text(
                    text = "Notifications Disabled",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo)
                )
            },
            dialogText = {
                Text(
                    text = "We need notification permission so we can alert you \nabout important updates. Please enable it in settings.",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo)
                )
            },
            confirmText = {
                Text(
                    text = "YES",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "NO",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = MainRed
                )
            }
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    val userId by sessionManager.userId.collectAsState(initial = "")
    val roleStr by sessionManager.role.collectAsState(initial = "")
    val role = if (roleStr == "STUDENT") UserRole.STUDENT else UserRole.TEACHER

    LaunchedEffect(Unit) {
        if (!userId.isNullOrBlank() && !roleStr.isNullOrBlank()) {
            val id = userId!!
            Log.d("DashboardScreen", "Loading courses for $id / $role")
            viewModel.loadCourses()
        }
    }

    AppScaffold(
        navController = navController,
        modifier = Modifier,
        canNavigateBack = false,
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            when {
                uiState.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${uiState.errorMessage}")
                    }
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.loading,
                        state = pullRefreshState,
                        onRefresh = {
                            viewModel.loadCourses()
                        },
                    ) {
                        DashboardContent(
                            innerPadding = innerPadding,
                            coursesList = uiState.courses,
                            body = ResponsiveFont.body(windowInfo),
                            subtitle = ResponsiveFont.subtitle(windowInfo),
                            navController = navController,
                            role = role
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DashboardContent(
    innerPadding: PaddingValues,
    coursesList: List<CourseResponse>,
    body: TextUnit,
    subtitle: TextUnit,
    navController: NavController,
    role: UserRole
){
    LazyVerticalGrid(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = coursesList, key = {it.courseId}){ course ->
            DashboardItem(
                courseName = course.courseName,
                courseCode = course.courseCode,
                coursePic = course.coursePicture,
                onCardClick = {
                    if (role == UserRole.STUDENT){
                        navController.navigate(Screen.StudentCourseDetail.createRoute(course.courseId))
                    }else if (role ==UserRole.TEACHER){
                        navController.navigate(Screen.TeacherCourseDetail.createRoute(course.courseId))
                    }
                },
                body = body,
                subtitle = subtitle
            )
        }
    }
}

@Composable
fun DashboardItem(
    courseName: String,
    courseCode: String,
    coursePic: ByteArray?,
    onCardClick: () -> Unit,
    body: TextUnit,
    subtitle: TextUnit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            coursePic?.let {
                if (it.isNotEmpty()) {
                    BlobImage(
                        byteArray = coursePic,
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                    )
                }else{
                    AsyncImage(
                        model = "https://img.freepik.com/free-photo/blackboard-inscribed-with-scientific-formulas-calculations_1150-19413.jpg?semt=ais_hybrid&w=740",
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = courseName,
                    color = TextBlack,
                    fontSize = body,
                    fontFamily = FontFamily(Font(R.font.alata)),
                )

                Text(
                    text = courseCode,
                    color = TextBlack,
                    fontSize = subtitle,
                    fontFamily = FontFamily(Font(R.font.alata)),
                )
            }
        }
    }
}



