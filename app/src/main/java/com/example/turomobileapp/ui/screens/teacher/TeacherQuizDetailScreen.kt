package com.example.turomobileapp.ui.screens.teacher

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.TeacherQuizViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherQuizDetailScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: TeacherQuizViewModel,
    activityId: String,
    courseId: String,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val quiz = uiState.quiz
    val pullRefreshState = rememberPullToRefreshState()

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { contentPadding ->
            if (uiState.loading){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }else{
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {

                    },
                ) {
                    Column(
                        modifier = Modifier
                            .padding(contentPadding)
                            .padding(windowInfo.screenWidth * 0.09f)
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.Start
                    ) {
                        QuizTitle(
                            height = windowInfo.screenHeight * 0.2f,
                            windowInfo = windowInfo,
                            quizTypeName = quiz?.quizTypeName.toString(),
                            quizName = quiz?.quizName.toString()
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 15.dp),
                            thickness =  1.dp,
                            color = LoginText
                        )

                        QuizHeader(
                            windowInfo = windowInfo,
                            dueDate = quiz?.deadlineDate,
                            unlockDate = quiz?.unlockDate,
                            timeLimit = quiz?.timeLimit,
                            allowedAttempts = quiz?.numberOfAttempts,
                            questionSize = quiz?.numberOfQuestions,
                            points = quiz?.overallPoints,
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 15.dp),
                            thickness =  1.dp,
                            color = LoginText
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun QuizTitle(
    height: Dp,
    windowInfo: WindowInfo,
    quizTypeName: String,
    quizName: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.quiz_detail_icon),
            contentDescription = "Quiz Icon",
            modifier = Modifier.size(height)
        )

        Column{
            Text(
                text = quizName,
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )

            Text(
                text = "$quizTypeName QUIZ",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizHeader(
    windowInfo: WindowInfo,
    dueDate: LocalDateTime?,
    unlockDate: LocalDateTime?,
    timeLimit: Int?,
    allowedAttempts: Int?,
    questionSize: Int?,
    points: Int?
){
    val timeLimitMinutes = timeLimit?.div(60)
    val dateFormatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    val dueDateFormatted = dueDate?.format(dateFormatterOut) ?: "—"
    val unlockDateFormatted = unlockDate?.format(dateFormatterOut) ?: "—"

    val textList = mapOf(
        "Unlocks At" to unlockDateFormatted,
        "Deadline Date At" to dueDateFormatted,
        "Time Limit (Minutes)" to timeLimitMinutes,
        "Allowed Attempts" to allowedAttempts,
        "Questions" to questionSize,
        "Points" to points
    )

    textList.forEach { (name, value) ->
        Text(
            text = "$name: $value",
            fontSize = ResponsiveFont.heading3(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}
