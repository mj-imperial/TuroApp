package com.example.turomobileapp.ui.screens.student

import com.example.turomobileapp.ui.components.AppScaffold
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.StudentCourseAnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCourseIndividualModuleScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: StudentCourseAnalyticsViewModel,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(moduleId) {
        viewModel.updateModuleInfo(moduleId)
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            viewModel.clearModuleInfo()
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            PullToRefreshBox(
                isRefreshing = uiState.loading,
                state = pullRefreshState,
                onRefresh = {
                    viewModel.getStudentScoreList()
                },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(it).padding(15.dp)
                ) {
                    uiState.currentStudentModule?.let { module ->
                        item {
                            Text(
                                text = module.moduleName,
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading1(windowInfo),
                                color = headingText
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(module.quizScores) { quiz ->
                            ScoreBox(
                                windowInfo = windowInfo,
                                activityName = quiz.activityName,
                                highestScorePercentage = quiz.highestScorePercentage,
                                lowestScorePercentage = quiz.lowestScorePercentage,
                                latestScorePercentage = quiz.latestScorePercentage
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ScoreBox(
    windowInfo: WindowInfo,
    activityName: String,
    highestScorePercentage: Double,
    lowestScorePercentage: Double,
    latestScorePercentage: Double
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCF5E5)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.quiz_detail_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 15.dp)
                )

                Text(
                    text = activityName,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = headingText,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = "Highest Score Percentage: $highestScorePercentage%",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = headingText
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Lowest Score Percentage: $lowestScorePercentage%",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = headingText
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Latest Score Percentage: $latestScorePercentage%",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = headingText
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}