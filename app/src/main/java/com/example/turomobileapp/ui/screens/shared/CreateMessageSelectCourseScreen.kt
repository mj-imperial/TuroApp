package com.example.turomobileapp.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.CreateMessageViewModel

@Composable
fun CreateMessageSelectCourseScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CreateMessageViewModel
) {
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    val courses = uiState.emailCourses.map { it.courseName }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(courses) { course ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, LoginText, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.updateSelectedCourse(course)
                                navController.popBackStack()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = course,
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading2(windowInfo),
                            color = TextBlack
                        )
                    }
                }
            }
        }
    )
}