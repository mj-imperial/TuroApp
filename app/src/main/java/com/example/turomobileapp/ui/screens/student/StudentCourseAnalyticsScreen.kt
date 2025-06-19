package com.example.turomobileapp.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.CircularScoreProgressBar
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.courseInfo1
import com.example.turomobileapp.ui.theme.courseInfo2
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.moduleStudent
import com.example.turomobileapp.ui.theme.pquiz1
import com.example.turomobileapp.ui.theme.pquiz2
import com.example.turomobileapp.ui.theme.quiz1
import com.example.turomobileapp.ui.theme.quiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.StudentCourseAnalyticsViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCourseAnalyticsScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: StudentCourseAnalyticsViewModel,
    courseId: String
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()
    val decimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
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
                        viewModel.getOverviewInfo()
                        viewModel.getStudentScoreList()
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

                            IndividualPerformanceBox(
                                windowInfo = windowInfo,
                                overallNumberOfAssessments = uiState.overviewInfo!!.overallNumberOfAssessments,
                                completedAssessments = uiState.overviewInfo!!.completedAssessments,
                                averageGrade = uiState.overviewInfo!!.averageScore,
                                points = uiState.overviewInfo!!.totalPoints
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            IndividualCourseModuleInfo(
                                windowInfo = windowInfo,
                                lowestModuleName = uiState.overviewInfo!!.lowestScoringModuleName,
                                lowestModuleAvg = uiState.overviewInfo!!.lowestScoringModuleAverage,
                                highestModuleName = uiState.overviewInfo!!.highestScoringModuleName,
                                highestModuleAvg = uiState.overviewInfo!!.highestScoringModuleAverage
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            IndividualCourseAssessmentInfo(
                                windowInfo = windowInfo,
                                lowestAssessmentName = uiState.overviewInfo!!.lowestAssessmentQuizName,
                                lowestAssessmentAvg = uiState.overviewInfo!!.lowestAssessmentScorePercentage,
                                highestAssessmentName = uiState.overviewInfo!!.highestAssessmentQuizName,
                                highestAssessmentAvg = uiState.overviewInfo!!.highestAssessmentScorePercentage
                            )
                        }

                        item {
                            Text(
                                text = "MODULES",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading2(windowInfo),
                                color = headingText
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        items(uiState.modulesScores) {
                            ModuleRow(
                                windowInfo = windowInfo,
                                moduleName = it.moduleName,
                                onClickModule = {
                                    navController.navigate(Screen.StudentCourseIndividualAnalytics.createRoute(courseId, it.moduleId))
                                },
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
    overallNumberOfAssessments: Int,
    completedAssessments: Int,
    averageGrade: Double,
    points: Int,
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
            Text(
                text = "OVERALL INFORMATION",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading1(windowInfo),
                color = headingText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(
                        text = "DONE: $completedAssessments/$overallNumberOfAssessments",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GRADE: $averageGrade%",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "POINTS: $points pts",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText
                    )
                }

                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.summary_student),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun IndividualCourseModuleInfo(
    windowInfo: WindowInfo,
    lowestModuleName: String,
    lowestModuleAvg: Double,
    highestModuleName: String,
    highestModuleAvg: Double
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(courseInfo1, courseInfo2)
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.modulestat),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = "MODULE STATISTICS",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    color = headingText
                )
            }

            Spacer(modifier = Modifier.width(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Highest Module Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lowest Module Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = highestModuleName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = lowestModuleName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularScoreProgressBar(
                        scorePercentage = highestModuleAvg,
                        diameter = 80.dp,
                        fontSize = ResponsiveFont.heading2(windowInfo)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularScoreProgressBar(
                        scorePercentage = lowestModuleAvg,
                        diameter = 80.dp,
                        fontSize = ResponsiveFont.heading2(windowInfo)
                    )
                }
            }
        }
    }
}

@Composable
fun IndividualCourseAssessmentInfo(
    windowInfo: WindowInfo,
    lowestAssessmentName: String,
    lowestAssessmentAvg: Double,
    highestAssessmentName: String,
    highestAssessmentAvg: Double
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(pquiz1, pquiz2)
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.modulestat),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = "ASSESSMENT STATISTICS",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    color = headingText
                )
            }

            Spacer(modifier = Modifier.width(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Highest Assessment Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lowest Assessment Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = highestAssessmentName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = lowestAssessmentName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularScoreProgressBar(
                        scorePercentage = highestAssessmentAvg,
                        diameter = 80.dp,
                        fontSize = ResponsiveFont.heading2(windowInfo)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularScoreProgressBar(
                        scorePercentage = lowestAssessmentAvg,
                        diameter = 80.dp,
                        fontSize = ResponsiveFont.heading2(windowInfo)
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleRow(
    windowInfo: WindowInfo,
    moduleName: String,
    onClickModule: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp, vertical = 8.dp)
            .clickable(onClick = onClickModule),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(moduleStudent)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Image(
                 painter = painterResource(R.drawable.folder_icon),
                 contentDescription = null,
                 contentScale = ContentScale.Crop,
                 modifier = Modifier.size(40.dp)
             )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = moduleName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading2(windowInfo),
                color = headingText,
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onClickModule) {
                    Icon(
                        painter = painterResource(R.drawable.rightarrow_icon),
                        contentDescription = null,
                        tint = headingText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}