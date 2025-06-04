package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.ReusableDatePicker
import com.example.turomobileapp.ui.components.ReusableTimeLimitPicker
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.CreateQuizViewModel
import java.time.Duration
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateQuizScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CreateQuizViewModel,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()

    var questionIndices by remember { mutableStateOf(List(1) { it }) }

    var openAlertDialog by remember { mutableStateOf(false) }

    val unlockDateState = remember { mutableStateOf<LocalDate?>(null) }
    val deadlineDateState = remember { mutableStateOf<LocalDate?>(null) }

    val timeLimitState = remember { mutableStateOf<Duration?>(null) }

    val numberOfAttemptsState = remember { mutableIntStateOf(1) }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                //TODO save quiz
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
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    CreateQuizHeader( windowInfo = windowInfo )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                item {
                    CreateQuizMoreInfo(
                        numberOfAttemptsState = numberOfAttemptsState,
                        windowInfo = windowInfo,
                        unlockDateState = unlockDateState,
                        deadlineDateState = deadlineDateState,
                        timeLimitState = timeLimitState
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(
                    items = questionIndices,
                    key = { it }
                ) { questionId ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart ) {
                                questionIndices = questionIndices.filterNot { it == questionId }
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
                        QuizQuestionCard(
                            windowInfo = windowInfo,
                            questionNumber = questionIndices.indexOf(questionId) + 1,
                        )
                    }
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = {
                                val nextIndex = (questionIndices.maxOrNull() ?: -1) + 1
                                questionIndices = questionIndices + nextIndex
                            },
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
                                    text = "CREATE QUIZ",
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
                            enabled = true
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CreateQuizHeader(
    windowInfo: WindowInfo
){
    var placeholderQuizName by remember { mutableStateOf("") }
    var placeholderQuizDescription by remember { mutableStateOf("") }
    var currentQuizType by remember { mutableStateOf("SHORT") }

    val menuList = listOf(
        DropdownMenuItem(
            itemName = "SHORT",
            onClick = {
                currentQuizType = "SHORT"
            }
        ),
        DropdownMenuItem(
            itemName = "PRACTICE",
            onClick = {
                currentQuizType = "PRACTICE"
            }
        ),
        DropdownMenuItem(
            itemName = "LONG",
            onClick = {
                currentQuizType = "LONG"
            }
        ),
        DropdownMenuItem(
            itemName = "SCREENING",
            onClick = {
                currentQuizType = "SCREENING"
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
                value = placeholderQuizName,
                onValueChange = { placeholderQuizName = it },
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
                value = placeholderQuizDescription,
                onValueChange = { placeholderQuizDescription = it },
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
fun CreateQuizMoreInfo(
    numberOfAttemptsState: MutableState<Int>,
    windowInfo: WindowInfo,
    unlockDateState: MutableState<LocalDate?>,
    deadlineDateState: MutableState<LocalDate?>,
    timeLimitState: MutableState<Duration?>
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
            ReusableDatePicker(
                selectedDate = unlockDateState,
                label = "UNLOCK DATE",
            )

            ReusableDatePicker(
                selectedDate = deadlineDateState,
                label = "DEADLINE DATE",
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
                    if (numberOfAttemptsState.value > 1) {
                        numberOfAttemptsState.value--
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
                        text = numberOfAttemptsState.value.toString(),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo)
                    )
                }

                IconButton(onClick = { numberOfAttemptsState.value++ }) {
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
                label = "Pick time limit"
            )
        }
    }
}

@Composable
fun QuizQuestionCard(
    questionNumber: Int,
    windowInfo: WindowInfo
){
    val options = remember { mutableStateListOf<String>("") }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var points by remember { mutableIntStateOf(1) }
    var questionText by remember { mutableStateOf("") }

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
                value = questionText,
                onValueChange = { questionText = it },
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
                    value = points.toString(),
                    onValueChange = { newVal ->
                        newVal.toIntOrNull()?.let { points = it }
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
                        .size(width = 80.dp, height = 40.dp)
                        .background(color = SoftGray, shape = RoundedCornerShape(6.dp))
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(6.dp), clip = false),
                    enabled = true
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEachIndexed { index, optionText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (index == selectedIndex),
                            onClick = { selectedIndex = index },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = green,
                                unselectedColor = LoginText
                            )
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        CapsuleTextField(
                            value = optionText,
                            onValueChange = { newVal ->
                                options[index] = newVal
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
                            maxLines = 3,
                            roundedCornerShape = 6.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = SoftGray, shape = RoundedCornerShape(6.dp))
                                .shadow(
                                    elevation = 1.dp, shape = RoundedCornerShape(6.dp), clip = false
                                ),
                            enabled = true
                        )
                    }
                }

                IconButton(
                    onClick = { options.add("") },
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