package com.example.turomobileapp.ui.screens.teacher

import com.example.turomobileapp.ui.components.AppScaffold
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.ReusableDateTimePicker
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.EditTutorialViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTutorialScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: EditTutorialViewModel,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()

    var openAlertDialog by remember { mutableStateOf(false) }
    var openErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.editTutorialStatus) {
        if (uiState.editTutorialStatus is Result.Success){
            Toast.makeText(context, "Tutorial successfully updated.",Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.TeacherCreateEditActivitiesInModule.createRoute(moduleId))
            viewModel.clearTutorialStatus()
        }else if(uiState.editTutorialStatus is Result.Failure){
            val msg = uiState.errorMessage
                ?: "Unknown error"
            Toast.makeText(context, "Failed to update Tutorial: $msg", Toast.LENGTH_LONG).show()
            viewModel.clearTutorialStatus()
        }
    }

    if (openErrorDialog){
        PopupMinimal(
            onDismissRequest = { openErrorDialog = false },
            width = windowInfo.screenWidth * 0.9f,
            height = windowInfo.screenHeight * 0.45f,
            padding = 10.dp,
            roundedCornerShape = 10.dp,
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
                viewModel.saveTutorial()
                if (uiState.errorMessage != null){
                    openErrorDialog = true
                }
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.upload_files_color),
            title = {
                Text(
                    text = "UPDATE TUTORIAL",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    color = LoginText
                )
            },
            dialogText = {
                Text(
                    text = "Are you satisfied with the changes?\n You cannot reverse the changes.",
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
                    color = LoginText
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "Update Tutorial",
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
                        EditTutorialHeader(
                            windowInfo = windowInfo,
                            tutorialName = uiState.tutorialName,
                            onUpdateTutorialName = viewModel::updateTutorialName,
                            tutorialDescription = uiState.originalTutorialDescription,
                            onUpdateTutorialDescription = viewModel::updateTutorialDescription,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    item {
                        EditTutorialMoreInfo(
                            unlockDateState = uiState.unlockDate,
                            onUpdateUnlockDateState = viewModel::updateUnlockDate,
                            deadlineDateState = uiState.deadlineDate,
                            onDeadlineDateState = viewModel::updateDeadlineDate
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ){
                                CapsuleTextField(
                                    value = uiState.youtubeLink,
                                    onValueChange = { viewModel.updateYoutubeLink(it) },
                                    placeholder = {
                                        Text(
                                            text = "TYPE YOUTUBE LINK",
                                            fontFamily = FontFamily(Font(R.font.alata)),
                                            fontSize = ResponsiveFont.heading2(windowInfo),
                                            color = LoginText
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "YOUTUBE LINK",
                                            fontFamily = FontFamily(Font(R.font.alata)),
                                            fontSize = ResponsiveFont.heading2(windowInfo),
                                            color = LoginText
                                        )
                                    },
                                    isSingleLine = false,
                                    roundedCornerShape = 10.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 3.dp,
                                            shape = RoundedCornerShape(10.dp),
                                            clip = false
                                        )
                                        .background(
                                            color = SoftGray,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    enabled = true,
                                    maxLines = 3
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    item {
                        CapsuleButton(
                            text = {
                                Text(
                                    text = "UPDATE TUTORIAL",
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
                        if (!isFormValid){
                            Text(
                                text = "To enable the edit button, change the URL Link",
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                color = LoginText
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun EditTutorialHeader(
    windowInfo: WindowInfo,
    tutorialName: String,
    onUpdateTutorialName: (String) -> Unit,
    tutorialDescription: String,
    onUpdateTutorialDescription: (String) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CapsuleTextField(
                value = tutorialName,
                onValueChange = {
                    onUpdateTutorialName(it)
                },
                placeholder = {
                    Text(
                        text = "TYPE TUTORIAL NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                label = {
                    Text(
                        text = "TUTORIAL NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                isSingleLine = true,
                roundedCornerShape = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp), clip = false)
                    .background(color = SoftGray, shape = RoundedCornerShape(10.dp)),
                enabled = true,
            )

            Spacer(modifier = Modifier.height(10.dp))

            CapsuleTextField(
                value = tutorialDescription,
                onValueChange = {
                    onUpdateTutorialDescription(it)
                },
                placeholder = {
                    Text(
                        text = "TYPE TUTORIAL DESCRIPTION",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                label = {
                    Text(
                        text = "TUTORIAL DESCRIPTION",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                isSingleLine = false,
                roundedCornerShape = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp), clip = false)
                    .background(color = SoftGray, shape = RoundedCornerShape(10.dp)),
                enabled = true,
                maxLines = 6
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTutorialMoreInfo(
    unlockDateState: LocalDateTime?,
    onUpdateUnlockDateState: (LocalDateTime?) -> Unit,
    deadlineDateState: LocalDateTime?,
    onDeadlineDateState: (LocalDateTime?) -> Unit
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
                    onUpdateUnlockDateState(it)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ReusableDateTimePicker(
                selectedDateTime = deadlineDateState,
                label = "DEADLINE DATE & TIME",
                onUpdateDateTime = {
                    onDeadlineDateState(it)
                }
            )
        }
    }
}