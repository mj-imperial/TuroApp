package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CircularScoreProgressBar
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
import com.example.turomobileapp.ui.theme.hiddenAnswers1
import com.example.turomobileapp.ui.theme.hiddenAnswers2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.AssessmentResultViewModel

@Composable
fun QuizResultScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: AssessmentResultViewModel,
    fromSubmit: Boolean
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    val quiz = uiState.quiz
    val results = uiState.results
    if (uiState.loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

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

    if (uiState.results.isEmpty()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You haven’t attempted this quiz yet.",
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
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
                modifier = Modifier.size(windowInfo.screenWidth*0.2f),
                onClick = { navController.navigate(Screen.StudentQuizDetail.route) },
                roundedCornerShape = 10.dp,
                buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                contentPadding = PaddingValues(5.dp),
                buttonColors = ButtonDefaults.buttonColors(contentColor = green),
                enabled = true
            )
        }
        return
    }

    val orderedResults = remember(uiState.results) {
        uiState.results.sortedBy { it.attemptNumber }
    }
    var selectedAttempt by rememberSaveable(orderedResults) {
        mutableStateOf(orderedResults.last().attemptNumber)
    }
    val scoreList = remember(orderedResults) {
        orderedResults.map { it.scorePercentage }
    }
    val keptScore: Double = remember(orderedResults) {
        orderedResults.first { it.isKept }.scorePercentage
    }
    val selectedResult = orderedResults.first { it.attemptNumber == selectedAttempt }

    var openAlertDialog by remember { mutableStateOf(fromSubmit) }
    if (openAlertDialog){
        PopupMinimal(
            onDismissRequest = { openAlertDialog = false },
            width = windowInfo.screenWidth * 0.2f,
            height = windowInfo.screenHeight * 0.2f,
            padding = 15.dp,
            roundedCornerShape = 10.dp,
            dialogText = "You got ${selectedResult.earnedPoints} for scoring ${selectedResult.scorePercentage} on your latest attempt.",
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = ResponsiveFont.heading3(windowInfo),
            textColor = TextBlack,
            cardColors = CardDefaults.cardColors(shortquiz1)
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
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    QuizResultHeader(
                        quizName = uiState.quiz!!.quizName,
                        attemptSize = quiz?.numberOfAttempts,
                        totalPoints = uiState.quiz!!.overallPoints,
                        scoreList = scoreList,
                        windowInfo = windowInfo,
                        onClickAttempt = { num ->
                            selectedAttempt = num
                        },
                        selectedAttempt = selectedAttempt,
                        earnedPoints = selectedResult.earnedPoints,
                        keptScore = keptScore
                    )
                }

                item {
                    QuizResultQuestions(
                        windowInfo = windowInfo,
                        showAnswers = uiState.quiz!!.hasAnswersShown,
                        resultShown = selectedResult,
                        numOfQuestion = uiState.quiz!!.numberOfQuestions,
                        content = uiState.content
                    )
                }

                item {
                    TakeQuizAgainButton(
                        windowInfo = windowInfo,
                        onClickTakeQuizAgain = {
                            navController.navigate(Screen.StudentQuizDetail.route)
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun QuizResultHeader(
    windowInfo: WindowInfo,
    quizName: String,
    attemptSize: Int?,
    totalPoints: Int,
    scoreList: List<Double>,
    selectedAttempt: Int,
    onClickAttempt: (Int) -> Unit,
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
                    text = "ATTEMPT HISTORY",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                attemptSize?.let {
                    for (i in 1..it.toInt()) {
                        val hasResult = i <= scoreInt.size
                        val scoreText = if (hasResult) "${scoreInt[i - 1]}/$totalPoints" else "--/$totalPoints"

                        Text(
                            text = "ATTEMPT $i: $scoreText",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.body(windowInfo),
                            textDecoration = if (hasResult && i == selectedAttempt) TextDecoration.Underline else TextDecoration.None,
                            modifier = if (hasResult) Modifier
                                .clickable { onClickAttempt(i) }
                                .padding(vertical = 4.dp)
                            else Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(windowInfo.screenWidth * 0.5f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularScoreProgressBar(
                    scorePercentage = scoreList[selectedAttempt - 1],
                    diameter = (windowInfo.screenWidth*0.4f) * 0.6f,
                    fontSize = ResponsiveFont.heading2(windowInfo),
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "POINTS: $earnedPoints",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "KEPT SCORE: $keptScoreInt/$totalPoints",
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
fun QuizResultQuestions(
    windowInfo: WindowInfo,
    showAnswers: Boolean,
    resultShown: AssessmentResultResponse,
    numOfQuestion: Int,
    content: List<QuizContentResponse>
){
    val score = (resultShown.scorePercentage) * numOfQuestion

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        if (!showAnswers){
            Text(
                text = "The result of the quiz is protected and is not visible to students.",
                fontSize = ResponsiveFont.body(windowInfo),
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
                                MainWhite,hiddenAnswers1,hiddenAnswers2
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ){
                Column {
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
                val chosenOptionId = resultShown.answers.firstOrNull { it.questionId == question.questionId }?.optionId
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
                                !isChosen && isCorrect -> Color(0xFFE6F4EA)
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
                                    .then(if (border != null) Modifier.border(border, RoundedCornerShape(4.dp)) else Modifier)
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
fun TakeQuizAgainButton(
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
            buttonColors = ButtonDefaults.buttonColors(green),
            enabled = true
        )
    }
}