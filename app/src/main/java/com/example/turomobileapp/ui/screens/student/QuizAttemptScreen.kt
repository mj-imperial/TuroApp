package com.example.turomobileapp.ui.screens.student

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.enums.QuestionType
import com.example.turomobileapp.models.QuestionResponse
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.courseInfo2
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.quizDetail1
import com.example.turomobileapp.ui.theme.quizDetail2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.Answer
import com.example.turomobileapp.viewmodels.student.QuizAttemptEvent
import com.example.turomobileapp.viewmodels.student.QuizAttemptUIState
import com.example.turomobileapp.viewmodels.student.QuizAttemptViewModel

@SuppressLint("DefaultLocale")
@Composable
fun QuizAttemptScreen(
    navController: NavController,
    sessionManager: SessionManager,
    quizId: String,
    viewModel: QuizAttemptViewModel,
    moduleId: String
) {
    val ctx = LocalContext.current
    BackHandler {
        Toast.makeText(ctx,"You can’t go back during the exam",Toast.LENGTH_SHORT).show()
    }

    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = {
                openAlertDialog = false
            },
            onConfirmation = {
                openAlertDialog = false
                viewModel.onSubmitClicked()
            },
            icon = painterResource(R.drawable.quiz),
            title = {
                Text(
                    text = "SUBMIT QUIZ",
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = TextBlack
                )
            },
            dialogText = {
                Text(
                    text = "Are you sure you want to submit the Quiz?",
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

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { ev ->
            when (ev) {
                is QuizAttemptEvent.SubmitSuccess -> {
                    navController.navigate(Screen.StudentQuizResult.createRoute(moduleId, quizId, true)) {
                        popUpTo(Screen.StudentQuizAttempt.route) { inclusive = true }
                    }
                }
                is QuizAttemptEvent.SubmitError -> {
                    Toast.makeText(ctx,ev.errorMessage,Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    if (uiState.loading) {
        Box(
            Modifier.fillMaxSize(),contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.content.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),contentAlignment = Alignment.Center
        ) {
            Text(
                "No questions available",style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LaunchedEffect(uiState.timeLimit) {
        if (uiState.timeLimit > 0) {
            viewModel.startTimer(uiState.timeLimit)
        }
    }

    val minutes = uiState.timeRemaining / 60
    val seconds = uiState.timeRemaining % 60
    val timerText = String.format("%02d:%02d",minutes,seconds)

    AppScaffold(
        navController = navController,
        canNavigateBack = false,
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start
                ) {
                    val question = uiState.shuffledQuestions.getOrNull(uiState.currentIndex)
                    if (question == null) {
                        Text("Invalid question index.")
                        return@AppScaffold
                    }
                    val options = question.options

                    QuizAttemptHeader(
                        height = windowInfo.screenHeight * 0.15f,
                        windowInfo = windowInfo,
                        quizType = uiState.quizType,
                        quizName = uiState.quizName
                    )

                    QuestionBox(
                        uiState = uiState,
                        windowInfo = windowInfo,
                        questionNumber = uiState.currentIndex + 1,
                        questionText = question.questionText,
                        questionType = QuestionType.valueOf(question.questionTypeName),
                        options = options,
                        onShortAnswerEntered = viewModel::onShortAnswerEntered,
                        onOptionSelected = viewModel::onOptionSelected,
                        onNextClicked = viewModel::onNextClicked,
                        onOpenAlertDialog = {
                            openAlertDialog = true
                        },
                        questionImage = question.questionImage
                    )

                    Text(
                        text = timerText,
                        color = LoginText,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    )
                }

                if (uiState.loadingSubmit) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = "Submitting Quiz.....",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading2(windowInfo),
                                color = TextBlack,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                            )
                            CircularProgressIndicator(color = MainWhite)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun QuizAttemptHeader(
    height: Dp,
    windowInfo: WindowInfo,
    quizType: String,
    quizName: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(vertical = 30.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.quiz_detail_icon),
            contentDescription = "Quiz Icon",
            modifier = Modifier.size(height * 0.8f)
        )

        Column {
            Text(
                text = "$quizType QUIZ",
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )

            Text(
                text = quizName,
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata))
            )
        }
    }
}

@Composable
fun QuestionBox(
    uiState: QuizAttemptUIState,
    windowInfo: WindowInfo,
    questionNumber: Int,
    questionImage: ByteArray?,
    questionText: String,
    questionType: QuestionType,
    options: List<QuestionResponse>,
    onShortAnswerEntered: (String) -> Unit,
    onOptionSelected: (QuestionResponse) -> Unit,
    onNextClicked: () -> Unit,
    onOpenAlertDialog: () -> Unit
){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = TextBlack),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            quizDetail1, quizDetail2
                        )
                    )
                )
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Q$questionNumber of ${uiState.shuffledQuestions.size}",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.albert_sans_thin)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 5.dp)
                )

                Text(
                    text = "QUESTION $questionNumber",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.albert_sans_thin)),
                    fontWeight = FontWeight.Bold
                )

                questionImage?.let {
                    if (it.isNotEmpty()){
                        BlobImage(
                            byteArray = questionImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(vertical = 10.dp)
                        )
                    }
                }

                Text(
                    text = questionText,
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.albert_sans_thin)),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (questionType == QuestionType.SHORT_ANSWER){
                    CapsuleTextField(
                        value = (uiState.answers[uiState.currentIndex] as? Answer.TextAnswer)?.text.orEmpty(),
                        onValueChange = {
                            onShortAnswerEntered(it)
                        },
                        isSingleLine = true,
                        roundedCornerShape = 3.dp,
                        modifier = Modifier.width(windowInfo.screenWidth * 0.4f),
                        enabled = true,
                        textStyle = LocalTextStyle.current
                    )
                }else{
                    val index = uiState.currentIndex
                    val selAnswer = uiState.answers[index] as? Answer.OptionAnswer
                    val selectedOptionId = selAnswer?.optionId

                    options.forEachIndexed { optionIndex, option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = { onOptionSelected(option) }),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = option.optionId == selectedOptionId,
                                onClick = {
                                    onOptionSelected(option)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = green,
                                    unselectedColor = LoginText
                                )
                            )
                            Text(
                                text = option.optionText,
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {

        if (uiState.currentIndex < uiState.shuffledQuestions.size - 1){
            CapsuleButton(
                text = {
                    Text(
                        text = "NEXT",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = MainWhite
                    )
                },
                onClick = onNextClicked,
                modifier = Modifier.width(windowInfo.screenWidth*0.35f),
                roundedCornerShape = 5.dp,
                buttonElevation = ButtonDefaults.buttonElevation(5.dp),
                contentPadding = PaddingValues(5.dp),
                buttonColors = ButtonDefaults.buttonColors(green),
                enabled = true
            )
        }
        CapsuleButton(
            text = {
                Text(
                    text = "SUBMIT",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = TextBlack,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            },
            onClick = onOpenAlertDialog,
            modifier = Modifier
                .width(windowInfo.screenWidth * 0.45f)
                .height(52.dp)
                .padding(bottom = 10.dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color.White, courseInfo2)),
                    shape = RoundedCornerShape(5.dp)
                )
                .shadow(4.dp, RoundedCornerShape(5.dp)),
            roundedCornerShape = 5.dp,
            buttonElevation = ButtonDefaults.buttonElevation(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            buttonColors = ButtonDefaults.buttonColors(
                containerColor = courseInfo2,
                contentColor = TextBlack
            ),
            enabled = !uiState.hasSubmitted && !uiState.loadingSubmit
        )
    }
}


