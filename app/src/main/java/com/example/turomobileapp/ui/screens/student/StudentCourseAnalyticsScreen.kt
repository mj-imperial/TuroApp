package com.example.turomobileapp.ui.screens.student

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ModuleQuiz
import com.example.turomobileapp.models.QuizItem
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.analysis1
import com.example.turomobileapp.ui.theme.headerQuizColor
import com.example.turomobileapp.ui.theme.headerQuizColor1
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.lonquizgrades
import com.example.turomobileapp.ui.theme.moduleColor
import com.example.turomobileapp.ui.theme.scorequizcolor
import com.example.turomobileapp.ui.theme.screeningExamgrades
import com.example.turomobileapp.ui.theme.screeninglonganalysis
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
                        viewModel.getStudentAnalysis()
                    },
                    modifier = Modifier.padding(it).padding(15.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            text = uiState.section,
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontWeight = FontWeight.Bold,
                            color = headingText
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyColumn(
                            modifier = Modifier
                                .background(headerQuizColor1, RoundedCornerShape(5.dp))
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            item {
                                CourseRow(
                                    windowInfo = windowInfo,
                                    headingName = uiState.courseName,
                                    overallGrade = uiState.overallGrade,
                                    decimalFormat = decimalFormat
                                )
                            }

                            items(uiState.moduleNames) { module ->
                                ModuleBlock(
                                    windowInfo = windowInfo,
                                    moduleName = module,
                                    decimalFormat = decimalFormat,
                                    practiceQuizAverage = uiState.practiceQuizzes?.takeIf { it.modules.any { mod -> mod.moduleName==module } }?.average,
                                    practiceQuizzesByModule = uiState.practiceQuizzes?.takeIf { it.modules.any { mod -> mod.moduleName==module } }?.modules,
                                    shortQuizAverage = uiState.shortQuizzes?.takeIf { it.modules.any { mod -> mod.moduleName==module } }?.average,
                                    shortQuizzesByModule = uiState.shortQuizzes?.takeIf { it.modules.any { mod -> mod.moduleName==module } }?.modules,
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .background(screeninglonganalysis, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Long Quizzes",
                                        fontSize = ResponsiveFont.body(windowInfo),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        color = headingText
                                    )

                                    Text(
                                        text = uiState.longQuizzes?.average?.toDouble()?.let {
                                            if (it.isNaN()) {
                                                "0.0%"
                                            } else {
                                                "${decimalFormat.format(it)}%"
                                            }
                                        } ?: "0.0%",
                                        fontSize = ResponsiveFont.body(windowInfo),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        color = headingText
                                    )
                                }
                            }

                            uiState.longQuizzes?.let { it1 ->
                                items(it1.quiz) {
                                    LongQuizRow(
                                        windowInfo = windowInfo,
                                        quiz = it
                                    )
                                }
                            }

                            items(uiState.screeningExam) {
                                Row(
                                    modifier = Modifier
                                        .background(screeningExamgrades, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = it.screeningName,
                                        fontSize = ResponsiveFont.body(windowInfo),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontWeight = FontWeight.Bold,
                                        color = headingText,
                                    )

                                    Text(
                                        text = it.percentage.toDouble().let {
                                            if (it.isNaN()) {
                                                "0.0%"
                                            } else {
                                                "${decimalFormat.format(it)}%"
                                            }
                                        },
                                        fontSize = ResponsiveFont.body(windowInfo),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontWeight = FontWeight.Bold,
                                        color = headingText
                                    )
                                }
                            }

                            item {
                                PointsRow(
                                    windowInfo = windowInfo,
                                    headingName = "POINTS GATHERED",
                                    points = uiState.points,
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CourseRow(
    windowInfo: WindowInfo,
    headingName: String?,
    overallGrade: Double,
    decimalFormat: DecimalFormat
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = analysis1, shape = RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = headingName ?: "",
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Bold,
            color = MainWhite,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = "${decimalFormat.format(overallGrade)}%",
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}


@Composable
fun PointsRow(
    windowInfo: WindowInfo,
    headingName: String?,
    points: Int,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = analysis1, shape = RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = headingName ?: "",
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Bold,
            color = MainWhite,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = points.toString(),
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun ModuleBlock(
    windowInfo: WindowInfo,
    moduleName: String,
    decimalFormat: DecimalFormat,
    practiceQuizAverage: Double?,
    practiceQuizzesByModule: List<ModuleQuiz>?,
    shortQuizAverage: Double?,
    shortQuizzesByModule: List<ModuleQuiz>?
){
    Text(
        text = moduleName,
        fontSize = ResponsiveFont.heading3(windowInfo),
        fontFamily = FontFamily(Font(R.font.alata)),
        fontWeight = FontWeight.Bold,
        color = headingText,
        modifier = Modifier
            .fillMaxWidth()
            .background(moduleColor, RoundedCornerShape(2.dp))
            .padding(top = 20.dp, bottom = 4.dp)
    )


    Row(
        modifier = Modifier
            .background(headerQuizColor, RoundedCornerShape(2.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Practices",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText
        )

        Text(
            text = practiceQuizAverage?.toDouble()?.let {
                if (it.isNaN()) {
                    "0.0%"
                } else {
                    "${decimalFormat.format(it)}%"
                }
            } ?: "0.0%",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText
        )
    }

    practiceQuizzesByModule?.let { quizzes ->
        val quiz = quizzes.filter { it.moduleName == moduleName }
        quiz.forEach {
            it.quiz.forEach {
                QuizRow(
                    windowInfo = windowInfo,
                    quiz = it
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .background(headerQuizColor, RoundedCornerShape(2.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Short Quizzes",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText
        )

        Text(
            text = shortQuizAverage?.toDouble()?.let {
                if (it.isNaN()) {
                    "0.0%"
                } else {
                    "${decimalFormat.format(it)}%"
                }
            } ?: "0.0%",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText
        )
    }

    shortQuizzesByModule?.let { quizzes ->
        val quiz = quizzes.filter { it.moduleName == moduleName }
        quiz.forEach {
            it.quiz.forEach {
                QuizRow(
                    windowInfo = windowInfo,
                    quiz = it
                )
            }
        }
    }
}

@Composable
fun QuizRow(
    windowInfo: WindowInfo,
    quiz: QuizItem
) {
    Row(
        modifier = Modifier
            .background(scorequizcolor, RoundedCornerShape(2.dp))
            .padding(start = 32.dp, top = 4.dp, bottom = 4.dp) // more indent
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "- ${quiz.quizName}",
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "${quiz.percentage.toInt()}%",
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText,
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
fun LongQuizRow(
    windowInfo: WindowInfo,
    quiz: QuizItem
){
    Row(
        modifier = Modifier
            .background(lonquizgrades, RoundedCornerShape(2.dp))
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "- ${quiz.quizName}",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Thin,
            color = headingText,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "${quiz.percentage.toInt()}%",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontWeight = FontWeight.Thin,
            color = headingText,
            fontStyle = FontStyle.Italic
        )
    }
}