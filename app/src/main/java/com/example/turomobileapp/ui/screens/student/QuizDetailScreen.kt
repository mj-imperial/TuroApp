package com.example.turomobileapp.ui.screens.student

import com.example.turomobileapp.ui.components.AppScaffold
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.AssessmentScoreResponse
import com.example.turomobileapp.models.QuizResponse
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
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ActivityFlowViewModel
import com.example.turomobileapp.viewmodels.student.QuizDetailViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizDetailScreen(
    navController: NavController,
    sessionManager: SessionManager,
    onClickTakeQuiz: (QuizResponse) -> Unit,
    viewModel: QuizDetailViewModel,
    activityId: String,
    courseId: String,
    activityFlowViewModel: ActivityFlowViewModel
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val quiz = uiState.quiz
    val scoresList = uiState.scores.sortedBy { it.attemptNumber }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(activityId) {
        activityFlowViewModel.setCurrentActivityId(activityId)
    }

    val previous = activityFlowViewModel.goToPrevious()
    val next = activityFlowViewModel.goToNext()
    val hasPrevious = previous != null
    val hasNext = next != null

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
                        viewModel.loadQuizMetadata()
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

                        QuizBody(
                            windowInfo = windowInfo,
                            description = quiz?.quizDescription,
                            onTakeQuizClick = { quiz?.let { onClickTakeQuiz(it) } },
                            onViewStatistics = {
                                navController.navigate(Screen.StudentQuizResult.createRoute(quiz?.quizId ?: "", false))
                            },
                            scoresList = scoresList,
                            attemptNumber = quiz?.numberOfAttempts ?: 0,
                            deadline = quiz?.deadlineDate,
                            unlockDate = quiz?.unlockDate
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 30.dp),
                            thickness =  1.dp,
                            color = LoginText
                        )

                        PreviousNextButton(
                            windowInfo = windowInfo,
                            onClickPrevious = {
                                previous?.let {
                                    activityFlowViewModel.setCurrentActivityId(it.activityId)
                                    val route = Screen.StudentActivityDetail.createRoute(
                                        moduleId = it.moduleId,
                                        activityId = it.activityId,
                                        activityType = it.activityType,
                                        courseId = courseId
                                    )
                                    navController.navigate(route)
                                }
                            },
                            onClickNext = {
                                next?.let {
                                    activityFlowViewModel.setCurrentActivityId(it.activityId)
                                    val route = Screen.StudentActivityDetail.createRoute(
                                        moduleId = it.moduleId,
                                        activityId = it.activityId,
                                        activityType = it.activityType,
                                        courseId = courseId
                                    )
                                    navController.navigate(route)
                                }
                            },
                            isPreviousEnabled = hasPrevious,
                            isNextEnabled = hasNext,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizBody(
    windowInfo: WindowInfo,
    description: String?,
    onTakeQuizClick: () -> Unit,
    onViewStatistics: () -> Unit,
    scoresList: List<AssessmentScoreResponse>,
    attemptNumber: Int,
    unlockDate: LocalDateTime?,
    deadline: LocalDateTime?
){
    var openAlertDialog by remember { mutableStateOf(false) }

    val today = LocalDateTime.now()
    val beforeDeadline = deadline?.let { today <= it } ?: true
    val afterUnlock = unlockDate?.let { today >= it } ?: true

    val hasAttemptsLeft = scoresList.size < attemptNumber
    val canTake = hasAttemptsLeft && beforeDeadline && afterUnlock

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

            Text(
                text = "${it.scorePercentage}%",
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
    }

    Spacer(Modifier.height(30.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
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
            buttonColors = ButtonDefaults.buttonColors(green),
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
            buttonColors = ButtonDefaults.buttonColors(green),
            enabled = true
        )
    }

    if (!beforeDeadline){
        Text(
            text = "Deadline date has passed.",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata))
        )
    }else if (!hasAttemptsLeft){
        Text(
            text = "No more attempts available.",
            fontSize = ResponsiveFont.body(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata))
        )
    }else if(!afterUnlock){
        Text(
            text = "Quiz hasn't been unlocked yet. Please check the date.",
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

@Composable
fun PreviousNextButton(
    windowInfo: WindowInfo,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean
){
    val buttonWidth = (windowInfo.screenWidth) * 0.3f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CapsuleButton(
            text = {
                Text(
                    text = "PREVIOUS",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = green
                )
            },
            onClick = onClickPrevious,
            roundedCornerShape = 5.dp,
            modifier = Modifier
                .width(buttonWidth)
                .border(1.dp,green,RoundedCornerShape(5.dp)),
            buttonElevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            buttonColors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = isPreviousEnabled
        )

        CapsuleButton(
            text = {
                Text(
                    text = "NEXT",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = green
                )
            },
            onClick = onClickNext,
            roundedCornerShape = 5.dp,
            modifier = Modifier
                .width(buttonWidth)
                .border(1.dp,green,RoundedCornerShape(5.dp)),
            buttonElevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(10.dp),
            buttonColors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = isNextEnabled
        )
    }
}
