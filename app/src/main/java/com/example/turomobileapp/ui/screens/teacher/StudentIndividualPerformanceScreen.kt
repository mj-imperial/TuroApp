package com.example.turomobileapp.ui.screens.teacher

import com.example.turomobileapp.ui.components.AppScaffold
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.models.QuizScore
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.quiz1
import com.example.turomobileapp.ui.theme.quiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.StudentPerformanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentIndividualPerformanceScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: StudentPerformanceViewModel,
    studentId: String
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getIndividualStudentProgress(studentId)
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            viewModel.clearCurrentStudentInfo()
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            if (uiState.loading){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getIndividualStudentProgress(studentId)
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(15.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))

                            uiState.currentStudentInfo?.let { student ->
                                IndividualPerformanceBox(
                                    windowInfo = windowInfo,
                                    studentPic = student.profilePic,
                                    studentName = student.studentName,
                                    completedAssessments = student.completedAssessments,
                                    averageGrade = student.averageGrade,
                                    points = student.points,
                                    rank = student.rank
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        items(uiState.currentStudentScores) {
                            ScoresList(
                                windowInfo = windowInfo,
                                moduleName = it.moduleName,
                                activityList = it.quizScores
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun IndividualPerformanceBox(
    windowInfo: WindowInfo,
    studentPic: String,
    studentName: String,
    completedAssessments: Int,
    averageGrade: Double,
    points: Int,
    rank: Int
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            quiz1.copy(alpha = 0.4f), quiz2.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = studentPic,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = studentName,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = headingText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "DONE: $completedAssessments",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo),
                color = headingText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AVERAGE GRADE: $averageGrade",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo),
                color = headingText
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Points: $points",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo),
                color = headingText
            )

            Spacer(modifier = Modifier.height(10.dp))

            val rankEnd = when(rank){
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }

            Text(
                text = "Rank: $rank$rankEnd",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo),
                color = headingText
            )
        }
    }
}

@Composable
fun ScoresList(
    windowInfo: WindowInfo,
    moduleName: String,
    activityList: List<QuizScore>
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.folder_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 10.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = moduleName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading1(windowInfo),
                color = headingText
            )
        }

        activityList.forEach {
            ActivityListItem(
                windowInfo = windowInfo,
                activityName = it.activityName,
                highestScorePercentage = it.highestScorePercentage,
                lowestScorePercentage = it.lowestScorePercentage,
                latestScorePercentage = it.latestScorePercentage
            )
        }
    }
}

@Composable
fun ActivityListItem(
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