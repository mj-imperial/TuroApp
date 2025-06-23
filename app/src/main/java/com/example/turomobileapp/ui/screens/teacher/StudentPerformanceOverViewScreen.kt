@file:Suppress("KotlinConstantConditions")

package com.example.turomobileapp.ui.screens.teacher

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.CircularScoreProgressBar
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.courseInfo1
import com.example.turomobileapp.ui.theme.courseInfo2
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.pquiz1
import com.example.turomobileapp.ui.theme.pquiz2
import com.example.turomobileapp.ui.theme.quiz1
import com.example.turomobileapp.ui.theme.quiz2
import com.example.turomobileapp.ui.theme.studentProgressBox
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.StudentPerformanceViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentPerformanceOverViewScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: StudentPerformanceViewModel,
    courseId: String
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val sortedStudents = uiState.studentProgressList.sortedByDescending { it.totalPoints }
    val pullRefreshState = rememberPullToRefreshState()
    val decimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))

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
                        viewModel.getStudentListProgress()
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(15.dp)
                    ) {
                        item {
                            val highestScorePercentage = uiState.studentProgressList.maxByOrNull { it.averageScore }?.averageScore ?: 0.0
                            val gradeAverage = if (uiState.studentProgressList.isNotEmpty()) {
                                uiState.studentProgressList.map { it.averageScore }.average()
                            } else 0.0
                            val gradeAverageDisplay = "Grade Average: $gradeAverage%"
                            OverallPerformanceBoxes(
                                windowInfo = windowInfo,
                                title = "OVERALL STUDENT PERFORMANCE",
                                mainPercentage = decimalFormat.format(highestScorePercentage),
                                subtitle = gradeAverageDisplay,
                                image = painterResource(R.drawable.trophy)
                            )

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        item {
                            val courseProgress: Double = if (uiState.numberOfAssessment > 0) {
                                (uiState.studentProgressList.map { it.completedAssessments }.average() / uiState.numberOfAssessment * 100)
                            } else {
                                0.0
                            }
                            val numberOfAssessments = "${uiState.numberOfAssessment} Assessments"
                            OverallPerformanceBoxes(
                                windowInfo = windowInfo,
                                title = "COURSE PROGRESS",
                                mainPercentage = decimalFormat.format(courseProgress),
                                subtitle = numberOfAssessments,
                                image = painterResource(R.drawable.additionalscreening)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            OverallCourseModuleInfo(
                                windowInfo = windowInfo,
                                lowestModuleName = uiState.lowestScoringModuleName,
                                lowestModuleAvg = uiState.lowestScoringModuleAverage,
                                highestModuleName = uiState.highestScoringModuleName,
                                highestModuleAvg = uiState.highestScoringModuleAverage
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            OverallCourseAssessmentInfo(
                                windowInfo = windowInfo,
                                lowestAssessmentName = uiState.lowestAssessmentAverageQuizName,
                                lowestAssessmentAvg = uiState.lowestAssessmentAverage,
                                highestAssessmentName = uiState.highestAssessmentAverageQuizName,
                                highestAssessmentAvg = uiState.highestAssessmentAverage
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                        }

                        item {
                            Text(
                                text = "INDIVIDUAL STUDENT PROGRESS",
                                fontSize = ResponsiveFont.heading1(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata)),
                                color = headingText,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        item {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, LoginText)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(modifier = Modifier.width(45.dp))

                                Text(
                                    text = "Name",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.body(windowInfo),
                                    color = headingText,
                                    modifier = Modifier.weight(2.0f)
                                )

                                Text(
                                    text = "Done",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.body(windowInfo),
                                    color = headingText,
                                    modifier = Modifier.weight(1.2f)
                                )

                                Text(
                                    text = "Grade",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.body(windowInfo),
                                    color = headingText,
                                    modifier = Modifier.weight(1.2f)
                                )

                                Text(
                                    text = "Points",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.body(windowInfo),
                                    color = headingText,
                                    modifier = Modifier.weight(1.2f)
                                )

                                Spacer(modifier = Modifier.width(30.dp))
                            }
                            HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, LoginText)
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        itemsIndexed(sortedStudents) { index, student ->
                            StudentProgressRow(
                                windowInfo = windowInfo,
                                profileUrl = student.profilePic,
                                fullName = student.studentName,
                                numberOfAssessments = uiState.numberOfAssessment,
                                completedAssessments = student.completedAssessments,
                                averageGrade = student.averageScore,
                                points = student.totalPoints,
                                onClickStudent = {
                                    viewModel.updateIndividualStudentInfo(
                                        studentId = student.studentId,
                                        studentName = student.studentName,
                                        profilePic = student.profilePic,
                                        completedAssessments = student.completedAssessments,
                                        averageGrade = student.averageScore,
                                        points = student.totalPoints,
                                        rank = index + 1
                                    )
                                    navController.navigate(Screen.TeacherPerformanceIndividual.createRoute(courseId, student.studentId))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OverallPerformanceBoxes(
    windowInfo: WindowInfo,
    title: String,
    mainPercentage: String,
    subtitle: String,
    image: Painter
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
                        colors = listOf(
                            quiz1.copy(alpha = 0.4f), quiz2.copy(alpha = 0.4f)
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading2(windowInfo),
                color = headingText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Text(
                            text = mainPercentage,
                            fontSize = 50.sp,
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = green,
                            modifier = Modifier.padding(end = 5.dp)
                        )

                        Text(
                            text = "%",
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = green,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        color = headingText
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun OverallCourseModuleInfo(
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

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Highest Module Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lowest Module Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = highestModuleName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.caption(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = lowestModuleName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.caption(windowInfo),
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
                    modifier = Modifier.fillMaxHeight().weight(1f),
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
                    modifier = Modifier.fillMaxHeight().weight(1f),
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
fun OverallCourseAssessmentInfo(
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

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Highest Assessment Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Lowest Assessment Avg.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = highestAssessmentName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.caption(windowInfo),
                        color = headingText,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = lowestAssessmentName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.caption(windowInfo),
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
                    modifier = Modifier.fillMaxHeight().weight(1f),
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
                    modifier = Modifier.fillMaxHeight().weight(1f),
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

@SuppressLint("DefaultLocale")
@Composable
fun StudentProgressRow(
    windowInfo: WindowInfo,
    profileUrl: ByteArray?,
    fullName: String,
    numberOfAssessments: Int,
    completedAssessments: Int,
    averageGrade: Double,
    points: Int,
    onClickStudent: () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =  3.dp, vertical = 8.dp)
            .clickable(onClick = onClickStudent),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(studentProgressBox)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlobImage(
                byteArray = profileUrl,
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = fullName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.body(windowInfo),
                color = headingText,
                modifier = Modifier.weight(2.0f)
            )

            Text(
                text = "$completedAssessments/$numberOfAssessments",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.body(windowInfo),
                color = headingText,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center
            )

            Text(
                text = String.format("%.1f", averageGrade),
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.body(windowInfo),
                color = headingText,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center
            )

            Text(
                text = points.toString(),
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.body(windowInfo),
                color = headingText,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onClickStudent) {
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
