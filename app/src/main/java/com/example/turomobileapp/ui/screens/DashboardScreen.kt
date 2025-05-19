package com.example.turomobileapp.ui.screens

import AppScaffold
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.enums.UserRole
import com.example.turomobileapp.models.CourseResponse
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.DashboardViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: DashboardViewModel = hiltViewModel()
){
    val userId by sessionManager.userId.collectAsState(initial = "")
    val roleStr by sessionManager.role.collectAsState(initial = "")
    val role = if (roleStr == "STUDENT") UserRole.STUDENT else UserRole.TEACHER

    LaunchedEffect(userId, roleStr) {
        if (!userId.isNullOrBlank() && !roleStr.isNullOrBlank()) {
            val id = userId!!
            Log.d("DashboardScreen", "Loading courses for $id / $role")
            viewModel.loadCourses(id, role)
        }
    }

    val windowInfo = rememberWindowInfo()
    val cardHeight = when (windowInfo.screenHeightInfo) {
        WindowInfo.WindowType.Compact  -> windowInfo.screenHeight * 0.25f
        WindowInfo.WindowType.Medium   -> windowInfo.screenHeight * 0.20f
        WindowInfo.WindowType.Expanded -> windowInfo.screenHeight * 0.15f
    }
    val uiState by viewModel.uiState.collectAsState()
    val body = ResponsiveFont.body(windowInfo)
    val subtitle = ResponsiveFont.subtitle(windowInfo)

    AppScaffold(
        navController = navController,
        modifier = Modifier,
        canNavigateBack = false,
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            DashboardContent(
                innerPadding = innerPadding,
                cardHeight = cardHeight,
                coursesList = uiState.courses,
                body = body,
                subtitle = subtitle,
                navController = navController
            )
        }
    )
}

@Composable
fun DashboardContent(
    innerPadding: PaddingValues,
    cardHeight: Dp,
    coursesList: List<CourseResponse>,
    body: TextUnit,
    subtitle: TextUnit,
    navController: NavController
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
                cardHeight = cardHeight,
                onCardClick = {
                    navController.navigate(Screen.CourseDetail.createRoute(course.courseId))
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
    coursePic: String,
    cardHeight: Dp,
    onCardClick: () -> Unit,
    body: TextUnit,
    subtitle: TextUnit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        AsyncImage(
            model = coursePic,
            contentDescription = "Course Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(6f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
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


