package com.example.turomobileapp.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.LongQuizAssessmentResultResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CircularScoreProgressBar
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.hiddenAnswers1
import com.example.turomobileapp.ui.theme.hiddenAnswers2
import com.example.turomobileapp.ui.theme.longquiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.LongQuizResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LongQuizResultScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: LongQuizResultViewModel,
    courseId: String,
    longQuizId: String,
    fromSubmit: Boolean
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
                navController.navigate(Screen.StudentLongQuizAttempt.createRoute(courseId, longQuizId))
            },
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

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { paddingValues ->
            if (uiState.loading) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }else if (uiState.results.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                val orderedResults = remember(uiState.results) {
                    uiState.results.sortedBy { it.attemptNumber }
                }
                val defaultAttempt = orderedResults.lastOrNull()?.attemptNumber ?: 1
                var selectedAttempt by rememberSaveable(defaultAttempt) {
                    mutableIntStateOf(defaultAttempt)
                }
                val scoreList = remember(orderedResults) {
                    orderedResults.map { it.scorePercentage }
                }
                val selectedResult = remember(orderedResults) {
                    orderedResults.firstOrNull { it.isKept } ?: orderedResults.lastOrNull()
                }
                val keptScore: Double = selectedResult?.scorePercentage ?: 0.0

                var openAlertDialog by remember { mutableStateOf(fromSubmit) }
                if (openAlertDialog){
                    PopupMinimal(
                        onDismissRequest = { openAlertDialog = false },
                        width = windowInfo.screenWidth * 0.95f,
                        height = windowInfo.screenHeight * 0.2f,
                        padding = 15.dp,
                        roundedCornerShape = 10.dp,
                        dialogText = "You got ${selectedResult!!.earnedPoints} pts. for scoring ${selectedResult.scorePercentage}% on your latest attempt.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        textColor = TextBlack
                    )
                }

                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.loadMetadata()
                        viewModel.loadAssessmentResults()
                    },
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            LongQuizResultHeader(
                                quizName = uiState.quiz!!.longQuizName,
                                totalPoints = uiState.quiz!!.overallPoints,
                                scoreList = scoreList,
                                windowInfo = windowInfo,
                                selectedAttempt = selectedAttempt,
                                earnedPoints = selectedResult!!.earnedPoints,
                                keptScore = keptScore
                            )
                        }

                        item {
                            LongQuizResultQuestions(
                                windowInfo = windowInfo,
                                showAnswers = uiState.quiz!!.hasAnswersShown,
                                resultShown = selectedResult,
                                numOfQuestion = uiState.quiz!!.numberOfQuestions,
                                content = uiState.content
                            )
                        }

                        item {
                            LongTakeQuizAgainButton(
                                windowInfo = windowInfo,
                                onClickTakeQuizAgain = {
                                    openQuizDialog = true
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
fun LongQuizResultHeader(
    windowInfo: WindowInfo,
    quizName: String,
    totalPoints: Int,
    scoreList: List<Double>,
    selectedAttempt: Int,
    earnedPoints: Int,
    keptScore: Double
){
    val scoreInt: List<Int> = scoreList.map { it -> ((it/100) * totalPoints).toInt() }
    val keptScoreInt = ((keptScore/100) * totalPoints).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LoginText)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quizName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading1(windowInfo),
                color = TextBlack
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(windowInfo.screenWidth * 0.5f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "KEPT SCORE",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(windowInfo.screenWidth * 0.5f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularScoreProgressBar(
                    scorePercentage = scoreList[(selectedAttempt - 1).coerceIn(0, scoreList.lastIndex)],
                    diameter = (windowInfo.screenWidth*0.4f) * 0.6f,
                    fontSize = ResponsiveFont.heading2(windowInfo),
                )
                Spacer(Modifier.height(5.dp))
                //TODO check logic
                Text(
                    text = "POINTS: ${earnedPoints * 100}",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "$keptScoreInt/$totalPoints",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), 2.dp, LoginText)
    }
}

@Composable
fun LongQuizResultQuestions(
    windowInfo: WindowInfo,
    showAnswers: Boolean,
    resultShown: LongQuizAssessmentResultResponse?,
    numOfQuestion: Int,
    content: List<QuizContentResponse>
){
    val score = (resultShown!!.scorePercentage/100) * numOfQuestion

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        if (!showAnswers){
            Text(
                text = "The result of the exam is protected and is not visible to students.",
                fontSize = ResponsiveFont.heading3(windowInfo),
                minLines = 1,
                fontFamily = FontFamily(Font(R.font.alata)),
                softWrap = true,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MainWhite, hiddenAnswers1, hiddenAnswers2
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ){
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "CORRECT ANSWERS ARE HIDDEN",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = MainRed
                    )

                    Text(
                        text = "You scored $score/$numOfQuestion.",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }else{
            content.forEachIndexed { index, question ->
                val chosenOptionId = resultShown.answers?.firstOrNull { it.questionId == question.questionId }?.optionId
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Question ${index + 1}",
                            fontSize = ResponsiveFont.heading2(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata))
                        )
                        question.questionImage?.let {
                            if (it.isNotEmpty()){
                                BlobImage(
                                    byteArray = question.questionImage,
                                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 10.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = question.questionText,
                            fontSize = ResponsiveFont.body(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata))
                        )
                        Spacer(Modifier.height(12.dp))
                        question.options.forEach { option ->
                            val isChosen = option.optionId == chosenOptionId
                            val isCorrect = option.isCorrect
                            val bg = when {
                                !showAnswers -> Color.Transparent
                                isChosen && isCorrect -> Color(0xFFE6F4EA)
                                isChosen && !isCorrect -> Color(0xFFFDECEA)
                                !isChosen && isCorrect -> Color(0xFFE6F4EA) // always show correct
                                else -> Color.Transparent
                            }
                            val border = if (showAnswers && (isChosen || isCorrect)) {
                                BorderStroke(1.dp, if (isCorrect) green else MainRed)
                            } else null
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bg, RoundedCornerShape(4.dp))
                                    .then(
                                        if (border!=null) Modifier.border(
                                            border,
                                            RoundedCornerShape(4.dp)
                                        ) else Modifier
                                    )
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = isChosen,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = if (isCorrect) green else MainRed,
                                        unselectedColor = Color.Gray
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = option.optionText,
                                    fontSize = ResponsiveFont.body(windowInfo),
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    color = when {
                                        showAnswers && isCorrect -> green
                                        showAnswers && isChosen && !isCorrect -> MainRed
                                        else -> TextBlack
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LongTakeQuizAgainButton(
    windowInfo: WindowInfo,
    onClickTakeQuizAgain: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CapsuleButton(
            text = {
                Text(
                    text = "TAKE QUIZ AGAIN",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = MainWhite
                )
            },
            onClick = onClickTakeQuizAgain,
            roundedCornerShape = 5.dp,
            buttonElevation = ButtonDefaults.buttonElevation(8.dp),
            contentPadding = PaddingValues(5.dp),
            buttonColors = ButtonDefaults.buttonColors(longquiz2),
            enabled = true
        )
    }
}