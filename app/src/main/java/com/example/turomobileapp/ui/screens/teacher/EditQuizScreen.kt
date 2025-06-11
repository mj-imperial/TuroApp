package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.QuizContentResponse
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.ReusableDateTimePicker
import com.example.turomobileapp.ui.components.ReusableTimeLimitPicker
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.EditQuizViewModel
import com.example.turomobileapp.viewmodels.teacher.PendingQuestion
import java.time.Duration
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditQuizScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: EditQuizViewModel,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    val oldQuestions = uiState.questions
    val newQuestions by viewModel.pendingQuestions.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    var openAlertDialog by remember { mutableStateOf(false) }
    var openErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.editQuizStatus) {
        if (uiState.editQuizStatus is Result.Success){
            Toast.makeText(context, "Quiz successfully created.",Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.TeacherCreateEditActivitiesInModule.createRoute(moduleId))
            viewModel.clearEditStatus()
        }else if(uiState.editQuizStatus is Result.Failure){
            val msg = uiState.errorMessage
                ?: "Unknown error"
            Toast.makeText(context, "Failed to create quiz: $msg", Toast.LENGTH_LONG).show()
            viewModel.clearEditStatus()
        }
    }

    if (openErrorDialog){
        PopupMinimal(
            onDismissRequest = { openErrorDialog = false },
            width = windowInfo.screenWidth * 0.7f,
            height = windowInfo.screenHeight * 0.3f,
            padding = 15.dp,
            roundedCornerShape = 15.dp,
            dialogText = uiState.errorMessage.toString(),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = ResponsiveFont.heading3(windowInfo),
            textColor = TextBlack
        )
    }
    
    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                viewModel.updateQuiz()
                if (uiState.errorMessage != null){
                    openErrorDialog = true
                }
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.create_quiz),
            title = {
                Text(
                    text = "QUIZ CREATION",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    color = LoginText
                )
            },
            dialogText = {
                Text(
                    text = "Are you satisfied with the information inputted?",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = LoginText
                )
            },
            confirmText = {
                Text(
                    text = "YES",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "NO",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = MainRed
                )
            }
        )
    }

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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    item {
                        Text(
                            text = "Update Quiz",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading1(windowInfo),
                            color = TextBlack,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }

                    item {
                        EditQuizHeader(
                            windowInfo = windowInfo,
                            quizType = uiState.quizType,
                            onUpdateQuizType = viewModel::updateQuizType,
                            quizName = uiState.quizTitle,
                            onUpdateName = viewModel::updateQuizTitle,
                            quizDescription = uiState.quizDescription,
                            onUpdateQuizDescription = viewModel::updateQuizDescription
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    item {
                        EditQuizMoreInfo(
                            windowInfo = windowInfo,
                            unlockDateState = uiState.unlockDateTime,
                            onUpdateUnlockDate = viewModel::updateUnlockDateTime,
                            deadlineDateState = uiState.deadlineDateTime,
                            onUpdateDeadlineDate = viewModel::updateDeadlineDateTime,
                            numberOfAttempts = uiState.numberOfAttempts,
                            onUpdateNumberOfAttempts = viewModel::updateNumberOfAttempts,
                            timeLimitState = remember { mutableStateOf(Duration.ofSeconds(uiState.timeLimit.toLong())) },
                            onUpdateDuration = viewModel::updateTimeLimit,
                            hasAnswersShown = uiState.hasAnswersShown,
                            onUpdateShowAnswers = viewModel::updateHasAnswersShown
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(
                        items = oldQuestions,
                        key = { it.questionId }
                    ) { question ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart ) {
                                    viewModel.removeOldQuestion(question.questionId)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.fillMaxWidth(),
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            gesturesEnabled = true,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled)
                                    Color.Transparent else MainRed
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            },
                        ) {
                            EditQuizQuestionCard(
                                windowInfo = windowInfo,
                                questionNumber = oldQuestions.indexOf(question) + 1,
                                isOldQuestion = true,
                                oldQuestion = question,
                                questionId = question.questionId,
                                onQuestionTextChanged = viewModel::updateOldQuestionText,
                                onAddOption = viewModel::addOptionToOldQuestion,
                                onUpdateOptionText = viewModel::updateOldOptionText,
                                onSetSingleCorrectOption = viewModel::setSingleCorrectOldOption,
                                onRemoveOption = viewModel::removeOptionFromOldQuestion,
                                onUpdateQuestionScore = viewModel::updateOldQuestionScore
                            )
                        }
                    }
                    
                    items(
                        items = newQuestions,
                        key = { it.tempId }
                    ) {question ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart ) {
                                    viewModel.removeNewQuestion(question.tempId)
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier.fillMaxWidth(),
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = true,
                            gesturesEnabled = true,
                            backgroundContent = {
                                val color = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled)
                                    Color.Transparent else MainRed
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                }
                            },
                        ) {
                            EditQuizQuestionCard(
                                windowInfo = windowInfo,
                                questionNumber = oldQuestions.size + (newQuestions.indexOf(question) + 1),
                                isOldQuestion = false,
                                newQuestion = question,
                                questionId = question.tempId,
                                onQuestionTextChanged = viewModel::updateNewQuestionText,
                                onAddOption = viewModel::addOptionToNewQuestion,
                                onUpdateOptionText = viewModel::updateNewOptionText,
                                onSetSingleCorrectOption = viewModel::setSingleCorrectNewOption,
                                onRemoveOption = viewModel::removeOptionFromNewQuestion,
                                onUpdateQuestionScore = viewModel::updateNewQuestionScore
                            )
                        }
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            IconButton(
                                onClick = { viewModel.addQuestion() },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_circle),
                                    contentDescription = "Add Question",
                                    modifier = Modifier.size(40.dp),
                                    tint = MainOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))
                    }
                    
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            CapsuleButton(
                                text = {
                                    Text(
                                        text = "UPDATE QUIZ",
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = ResponsiveFont.heading1(windowInfo),
                                        color = TextBlack
                                    )
                                },
                                onClick = { openAlertDialog = true },
                                roundedCornerShape = 10.dp,
                                buttonElevation = ButtonDefaults.buttonElevation(5.dp),
                                contentPadding = PaddingValues(10.dp),
                                buttonColors = ButtonDefaults.buttonColors(MainOrange),
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isFormValid
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun EditQuizHeader(
    windowInfo: WindowInfo,
    quizType: String,
    onUpdateQuizType: (String) -> Unit,
    quizName: String,
    onUpdateName: (String) -> Unit,
    quizDescription: String,
    onUpdateQuizDescription: (String) -> Unit
){
    var currentQuizType by remember { mutableStateOf(quizType) }

    val menuList = listOf(
        DropdownMenuItem(
            itemName = "SHORT",
            onClick = {
                onUpdateQuizType("SHORT")
            }
        ),
        DropdownMenuItem(
            itemName = "PRACTICE",
            onClick = {
                onUpdateQuizType("PRACTICE")
            }
        ),
        DropdownMenuItem(
            itemName = "LONG",
            onClick = {
                onUpdateQuizType("LONG")
            }
        ),
        DropdownMenuItem(
            itemName = "SCREENING",
            onClick = {
                onUpdateQuizType("SCREENING")
            }
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CustomDropDownMenu(
            menuText = currentQuizType,
            dropdownMenuItems = menuList,
            maxWidthFloat = 0.3f
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            CapsuleTextField(
                value = quizName,
                onValueChange = {
                    onUpdateName(it)
                },
                placeholder = {
                    Text(
                        text = "SET QUIZ NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = LoginText
                    )
                },
                label = {
                    Text(
                        text = "SET QUIZ NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = LoginText
                    )
                },
                isSingleLine = true,
                roundedCornerShape = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 3.dp, shape = RoundedCornerShape(5.dp), clip = false
                    )
                    .background(
                        color = SoftGray, shape = RoundedCornerShape(5.dp)
                    ),
                enabled = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            CapsuleTextField(
                value = quizDescription,
                onValueChange = { onUpdateQuizDescription(it) },
                placeholder = {
                    Text(
                        text = "SET QUIZ DESCRIPTION / INSTRUCTION",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = LoginText
                    )
                },
                isSingleLine = false,
                roundedCornerShape = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 3.dp, shape = RoundedCornerShape(5.dp), clip = false
                    )
                    .background(
                        color = SoftGray, shape = RoundedCornerShape(5.dp)
                    ),
                enabled = true,
                maxLines = 7
            )
        }
    }

    HorizontalDivider(color = Color(0xFFDDDDDD), thickness = 1.dp)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditQuizMoreInfo(
    windowInfo: WindowInfo,
    unlockDateState: LocalDateTime?,
    onUpdateUnlockDate: (LocalDateTime?) -> Unit,
    deadlineDateState: LocalDateTime?,
    onUpdateDeadlineDate: (LocalDateTime?) -> Unit,
    numberOfAttempts: Int,
    onUpdateNumberOfAttempts: (Int) -> Unit,
    timeLimitState: MutableState<Duration?>,
    onUpdateDuration: (Int) -> Unit,
    hasAnswersShown: Boolean,
    onUpdateShowAnswers: (Boolean) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReusableDateTimePicker(
                selectedDateTime = unlockDateState,
                label = "UNLOCK DATE & TIME",
                onUpdateDateTime = {
                    onUpdateUnlockDate(it)
                }
            )

            ReusableDateTimePicker(
                selectedDateTime = deadlineDateState,
                label = "DEADLINE DATE & TIME",
                onUpdateDateTime = {
                    onUpdateDeadlineDate(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "NUM. OF ATTEMPTS",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = LoginText
                )

                IconButton(onClick = {
                    if (numberOfAttempts > 1) {
                        onUpdateNumberOfAttempts(numberOfAttempts - 1)
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.subtract_circle),
                        contentDescription = null
                    )
                }

                Box(
                    modifier = Modifier.padding(5.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = numberOfAttempts.toString(),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo)
                    )
                }

                IconButton(onClick = { onUpdateNumberOfAttempts(numberOfAttempts + 1) }) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ReusableTimeLimitPicker(
                windowInfo = windowInfo,
                selectedDuration = timeLimitState,
                label = "Pick time limit",
                onDurationSelected = {
                    onUpdateDuration(it?.seconds?.toInt() ?: 0)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "SHOW ANSWERS: ",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = LoginText
                )
                Spacer(modifier = Modifier.width(8.dp))

                RadioButton(
                    selected = hasAnswersShown == true,
                    onClick = { onUpdateShowAnswers(true) },
                    colors = RadioButtonDefaults.colors(selectedColor = green)
                )
                Text(
                    text = "YES",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = LoginText
                )

                Spacer(modifier = Modifier.width(12.dp))

                RadioButton(
                    selected = hasAnswersShown == false,
                    onClick = { onUpdateShowAnswers(false) },
                    colors = RadioButtonDefaults.colors(selectedColor = green)
                )
                Text(
                    text = "NO",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = LoginText
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = Color(0xFFDDDDDD), thickness = 1.dp)
}

@Composable
fun EditQuizQuestionCard(
    windowInfo: WindowInfo,
    questionNumber: Int,
    isOldQuestion: Boolean,
    oldQuestion: QuizContentResponse? = null,
    newQuestion: PendingQuestion? = null,
    questionId: String,
    onQuestionTextChanged: (String, String) -> Unit,
    onAddOption: (String) -> Unit,
    onUpdateOptionText: (String, String, String) -> Unit,
    onSetSingleCorrectOption: (String, String) -> Unit,
    onRemoveOption: (String, String) -> Unit,
    onUpdateQuestionScore: (String, Int) -> Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(4.dp, RoundedCornerShape(10.dp), clip = false),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Question $questionNumber",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading2(windowInfo),
                textAlign = TextAlign.Start
            )

            CapsuleTextField(
                value = if (isOldQuestion) oldQuestion!!.questionText else newQuestion!!.text,
                onValueChange = { onQuestionTextChanged(questionId,it) },
                placeholder = {
                    Text(
                        text = "Type the question here...",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = LoginText
                    )
                },
                isSingleLine = false,
                roundedCornerShape = 5.dp,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SoftGray, shape = RoundedCornerShape(6.dp))
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(6.dp), clip = false),
                enabled = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Points:",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo)
                )

                Spacer(modifier = Modifier.size(12.dp))

                CapsuleTextField(
                    value = if (isOldQuestion) oldQuestion!!.score.toString() else newQuestion!!.score.toString(),
                    onValueChange = {
                        val intVal = it.toIntOrNull() ?: 0
                        onUpdateQuestionScore(questionId, intVal)
                    },
                    placeholder = {
                        Text(
                            text = "0",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            color = LoginText
                        )
                    },
                    isSingleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    roundedCornerShape = 6.dp,
                    modifier = Modifier
                        .padding(10.dp)
                        .width(70.dp)
                        .background(color = SoftGray, shape = RoundedCornerShape(6.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(6.dp), clip = false),
                    enabled = true
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isOldQuestion){
                    oldQuestion!!.options.forEach { oldOption ->
                        EditOptionRow(
                            windowInfo = windowInfo,
                            isCorrect = oldOption.isCorrect,
                            onSetSingleCorrectOption = onSetSingleCorrectOption,
                            questionId = questionId,
                            optionId = oldOption.optionId,
                            optionText = oldOption.optionText,
                            onUpdateOptionText = onUpdateOptionText,
                            onRemoveOption = onRemoveOption
                        )
                    }
                }else{
                    newQuestion!!.options.forEach { newOption ->
                        EditOptionRow(
                            windowInfo = windowInfo,
                            isCorrect = newOption.isCorrect,
                            onSetSingleCorrectOption = onSetSingleCorrectOption,
                            questionId = questionId,
                            optionId = newOption.tempId,
                            optionText = newOption.text,
                            onUpdateOptionText = onUpdateOptionText,
                            onRemoveOption = onRemoveOption
                        )
                    }
                }

                IconButton(
                    onClick = { onAddOption(questionId) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle),
                        contentDescription = "Add Option",
                        modifier = Modifier.size(32.dp),
                        tint = MainOrange
                    )
                }
            }
        }
    }
}

@Composable
fun EditOptionRow(
    windowInfo: WindowInfo,
    isCorrect: Boolean,
    onSetSingleCorrectOption: (String, String) -> Unit,
    questionId: String,
    optionId: String,
    optionText: String,
    onUpdateOptionText: (String, String, String) -> Unit,
    onRemoveOption: (String, String) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isCorrect,
            onClick = { onSetSingleCorrectOption(questionId, optionId) },
            colors = RadioButtonDefaults.colors(
                selectedColor = green,
                unselectedColor = LoginText
            )
        )

        Spacer(modifier = Modifier.size(7.dp))

        CapsuleTextField(
            value = optionText,
            onValueChange = {
                onUpdateOptionText(questionId, optionId, it)
            },
            placeholder = {
                Text(
                    text = "Option text...",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    color = LoginText
                )
            },
            isSingleLine = false,
            maxLines = 5,
            roundedCornerShape = 6.dp,
            modifier = Modifier
                .weight(1f)
                .background(color = SoftGray, shape = RoundedCornerShape(6.dp))
                .shadow(
                    elevation = 1.dp, shape = RoundedCornerShape(6.dp), clip = false
                ),
            enabled = true
        )

        IconButton(onClick = { onRemoveOption(questionId,optionId) }) {
            Icon(
                painter = painterResource(R.drawable.baseline_delete_24),
                contentDescription = null,
                tint = MainRed
            )
        }
    }
}