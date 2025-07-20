package com.example.turomobileapp.ui.screens.student

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ScreeningExamAssessmentResult
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.screeningExam2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ScreeningExamDetailViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningExamDetailScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ScreeningExamDetailViewModel,
    screeningExamId: String
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

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
            }else {
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.loadQuiz()
                        viewModel.loadAttemptHistory()
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
                        ScreeningExamTitle(
                            height = windowInfo.screenHeight * 0.2f,
                            windowInfo = windowInfo,
                            quizName = uiState.exam?.screeningName ?: ""
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 15.dp),
                            thickness =  1.dp,
                            color = LoginText
                        )

                        ScreeningExamHeader(
                            windowInfo = windowInfo,
                            timeLimit = uiState.exam?.timeLimit,
                            allowedAttempts = 3,
                            questionSize = uiState.exam?.numberOfQuestions,
                            //placeholderPoints
                            points = 30
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 15.dp),
                            thickness =  1.dp,
                            color = LoginText
                        )

                        ScreeningExamBody(
                            windowInfo = windowInfo,
                            description = uiState.exam?.screeningInstructions,
                            onTakeQuizClick = {
                                navController.navigate(Screen.ScreeningExamAttempt.createRoute(screeningExamId))
                            },
                            onViewStatistics = {
                                navController.navigate(Screen.ScreeningExamResult.createRoute(screeningExamId))
                            },
                            scoresList = uiState.scores,
                            attemptNumber = 3
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ScreeningExamTitle(
    height: Dp,
    windowInfo: WindowInfo,
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
            painter = painterResource(R.drawable.screeningexam_icon),
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
                text = "LONG QUIZ",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreeningExamHeader(
    windowInfo: WindowInfo,
    timeLimit: Int?,
    allowedAttempts: Int?,
    questionSize: Int?,
    points: Int?
){
    val timeLimitMinutes = timeLimit?.div(60)

    val textList = mapOf(
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScreeningExamBody(
    windowInfo: WindowInfo,
    description: String?,
    onTakeQuizClick: () -> Unit,
    onViewStatistics: () -> Unit,
    scoresList: List<ScreeningExamAssessmentResult>,
    attemptNumber: Int,
){
    var openAlertDialog by remember { mutableStateOf(false) }

    val hasAttemptsLeft = scoresList.size < attemptNumber
    val canTake = hasAttemptsLeft

    val dateFormatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

    Text(
        text = "General Instructions",
        fontSize = ResponsiveFont.heading1(windowInfo),
        fontFamily = FontFamily(Font(R.font.alata)),
        modifier = Modifier.padding(bottom = 15.dp)
    )

    Text(
        text = description.toString(),
        fontSize = ResponsiveFont.body(windowInfo),
        fontFamily = FontFamily(Font(R.font.alata)),
        minLines = 1,
        softWrap = true,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    Text(
        text = "Attempt History",
        fontSize = ResponsiveFont.heading3(windowInfo),
        fontFamily = FontFamily(Font(R.font.alata)),
    )

    scoresList.forEach {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Attempt ${it.attemptNumber}:  ",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "${it.scorePercentage}%",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                modifier = Modifier.padding(end = 15.dp)
            )

            Text(
                text = it.dateTaken.format(dateFormatterOut),
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
    }

    Spacer(Modifier.height(30.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CapsuleButton(
            text = {
                Text(
                    text = "Take Quiz",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainWhite
                )
            },
            onClick = { openAlertDialog = true },
            roundedCornerShape = 10.dp,
            buttonElevation = ButtonDefaults.buttonElevation(8.dp),
            contentPadding = PaddingValues(10.dp),
            buttonColors = ButtonDefaults.buttonColors(screeningExam2),
            enabled = canTake
        )

        CapsuleButton(
            text = {
                Text(
                    text = "View History",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainWhite
                )
            },
            onClick = onViewStatistics,
            roundedCornerShape = 10.dp,
            buttonElevation = ButtonDefaults.buttonElevation(8.dp),
            contentPadding = PaddingValues(10.dp),
            buttonColors = ButtonDefaults.buttonColors(screeningExam2),
            enabled = true
        )
    }

    if (!hasAttemptsLeft) {
        Text(
            text = "No more attempts available.",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata))
        )
    }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = {
                openAlertDialog = false
            },
            onConfirmation = onTakeQuizClick,
            icon = painterResource(R.drawable.screeningexam_icon),
            title = {
                Text(
                    text = "TAKE QUIZ",
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack
                )
            },
            dialogText = {
                Text(
                    text = "Are you sure you want to take the Quiz?",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack
                )
            },
            confirmText = {
                Text(
                    text = "CONFIRM",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "DISMISS",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainRed
                )
            }
        )
    }
}