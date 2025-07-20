package com.example.turomobileapp.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ScreeningExamResultResponse
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CircularScoreProgressBar
import com.example.turomobileapp.ui.components.LinearScoreProgressBar
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.longquiz2
import com.example.turomobileapp.ui.theme.screeningBody
import com.example.turomobileapp.ui.theme.screeningBody2
import com.example.turomobileapp.ui.theme.screeningExamTitle1
import com.example.turomobileapp.ui.theme.screeningExamTitle2
import com.example.turomobileapp.ui.theme.screeningOrange
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ScreeningExamResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreeningExamResultScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ScreeningExamResultViewModel,
    screeningExamId: String
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    uiState.errorMessage?.let { message ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Something went wrong:\n$message",
                fontSize = ResponsiveFont.heading2(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = MainRed
            )
        }
        return
    }

    var openQuizDialog by remember { mutableStateOf(false) }

    if (openQuizDialog){
        PopupAlertWithActions(
            onDismissRequest = {
                openQuizDialog = false
            },
            onConfirmation = {
                navController.navigate(Screen.ScreeningExamAttempt.createRoute(screeningExamId))
            },
            icon = painterResource(R.drawable.screeningexam_icon),
            title = {
                Text(
                    text = "TAKE SCREENING EXAM",
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

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { paddingValues ->
            if (uiState.loadingInit) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }else if (uiState.result == null) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You haven’t attempted this exam yet.",
                        fontSize = ResponsiveFont.heading1(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        color = headingText
                    )
                    Spacer(Modifier.height(16.dp))
                    CapsuleButton(
                        text = {
                            Text(
                                text = "TAKE QUIZ",
                                fontSize = ResponsiveFont.heading2(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata)),
                                color = MainWhite
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { openQuizDialog = true },
                        roundedCornerShape = 10.dp,
                        buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                        contentPadding = PaddingValues(5.dp),
                        buttonColors = ButtonDefaults.buttonColors(containerColor = green, contentColor = MainWhite),
                        enabled = true
                    )
                }
            } else{
                PullToRefreshBox(
                    isRefreshing = uiState.loadingInit,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.loadResults()
                    },
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            ScreeningTitle(
                                windowInfo = windowInfo,
                                screeningName = uiState.exam?.screeningName ?: ""
                            )

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        item {
                            ScreeningMainScore(
                                windowInfo = windowInfo,
                                highestScore = uiState.result!!.earnedPoints,
                                numberOfQuestions = uiState.exam?.numberOfQuestions ?: 0
                            )

                            Spacer(modifier = Modifier.height(15.dp))
                        }

                        item {
                            ScreeningPersonalizedLearning(
                                windowInfo = windowInfo,
                                tier = uiState.result!!.tierId,
                                attemptNumber = uiState.result!!.attemptNumber,
                                concepts = uiState.result!!.data,
                                onNavigateToLearningResources = { conceptId, topicId ->
                                    if (uiState.result!!.tierId == 1){
                                        navController.navigate(Screen.ScreeningLearningResources.createRoute(screeningExamId, conceptId))
                                    }else if(uiState.result!!.tierId == 2){
                                        navController.navigate(
                                            Screen.ScreeningLearningResources.createRoute(
                                                screeningExamId,
                                                conceptId,
                                                topicId
                                            )
                                        )
                                    }
                                },
                                onNavigateToCatchUp = {
                                    viewModel.setCatchUp()
                                    navController.navigate(Screen.Dashboard.route)
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
fun ScreeningTitle(
    windowInfo: WindowInfo,
    screeningName: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(screeningOrange, RoundedCornerShape(5.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(R.drawable.additionalscreening),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = screeningName,
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = headingText
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = "Screening Exam",
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = headingText
            )
        }
    }
}

@Composable
fun ScreeningMainScore(
    windowInfo: WindowInfo,
    highestScore: Int,
    numberOfQuestions: Int,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(screeningExamTitle1, RoundedCornerShape(5.dp))
            .padding(vertical = 15.dp, horizontal = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Column {
                Text(
                    text = "BEST SCORE",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "$highestScore/$numberOfQuestions",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack
                )
            }
        }

        CircularScoreProgressBar(
            scorePercentage = (highestScore.toDouble() / numberOfQuestions.toDouble()) * 100,
            diameter = 150.dp,
            fontSize = ResponsiveFont.heading1(windowInfo)
        )
    }
}

@Composable
fun ScreeningPersonalizedLearning(
    windowInfo: WindowInfo,
    tier: Int,
    attemptNumber: Int,
    concepts: List<ScreeningExamResultResponse>,
    onNavigateToLearningResources: (conceptId: String, topicId: String?) -> Unit,
    onNavigateToCatchUp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                screeningExamTitle1,
                                screeningExamTitle2
                            )
                        )
                    )
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "SCREENING DIAGNOSTICS",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainWhite
                )
            }

            // Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                screeningBody,
                                screeningBody2
                            )
                        )
                    )
                    .padding(vertical = 10.dp, horizontal = 20.dp)
            ) {
                Row {
                    Column {
                        Text(
                            text = "Tier $tier",
                            fontSize = ResponsiveFont.heading2(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = MainWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Attempt $attemptNumber / 3",
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = MainWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "If you get a low score on a certain topic, resources will be given to you for improvement.",
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            color = MainWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                when (tier) {
                    1 -> {
                        concepts.forEach { concept ->
                            Text(
                                text = concept.conceptName,
                                fontSize = ResponsiveFont.heading2(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata)),
                                color = MainWhite
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearScoreProgressBar(
                                scorePercentage = concept.conceptScorePercentage
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            if (!concept.passed){
                                CapsuleButton(
                                    text = {
                                        Text("COURSE MATERIALS +")
                                    },
                                    onClick = {
                                        onNavigateToLearningResources(concept.conceptId, null)
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .width(windowInfo.screenWidth * 0.5f),
                                    buttonColors = ButtonDefaults.buttonColors(containerColor = screeningOrange),
                                    roundedCornerShape = 5.dp,
                                    contentPadding = PaddingValues(6.dp),
                                    buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                                    enabled = true
                                )
                            }
                        }
                    }

                    2 -> {
                        concepts.forEach { concept ->
                            Text(
                                text = concept.conceptName,
                                fontSize = ResponsiveFont.heading2(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata)),
                                color = MainWhite
                            )

                            concept.topics.forEach { topic ->
                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = topic.topicName,
                                    fontSize = ResponsiveFont.heading3(windowInfo),
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    color = MainWhite
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearScoreProgressBar(
                                    scorePercentage = topic.topicScorePercentage
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                if (!topic.passed){
                                    CapsuleButton(
                                        text = {
                                            Text("COURSE MATERIALS +")
                                        },
                                        onClick = {
                                            onNavigateToLearningResources(
                                                concept.conceptId,
                                                topic.topicId
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(bottom = 8.dp)
                                            .width(windowInfo.screenWidth * 0.5f),
                                        buttonColors = ButtonDefaults.buttonColors(containerColor = screeningOrange),
                                        roundedCornerShape = 5.dp,
                                        contentPadding = PaddingValues(6.dp),
                                        buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                                        enabled = true
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        CapsuleButton(
                            text = {
                                Text("Proceed to Catch-up")
                            },
                            onClick = onNavigateToCatchUp,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth(),
                            buttonColors = ButtonDefaults.buttonColors(containerColor = longquiz2),
                            roundedCornerShape = 10.dp,
                            contentPadding = PaddingValues(10.dp),
                            buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                            enabled = true
                        )
                    }
                }
            }
        }
    }
}


