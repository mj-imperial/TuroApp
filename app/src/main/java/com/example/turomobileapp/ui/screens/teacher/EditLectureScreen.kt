package com.example.turomobileapp.ui.screens.teacher

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.ReusableDateTimePicker
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
import com.example.turomobileapp.viewmodels.teacher.EditLectureViewModel
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditLectureScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: EditLectureViewModel,
    moduleId: String
){
    val windowInfo = rememberWindowInfo()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    var openAlertDialog by remember { mutableStateOf(false) }
    var openErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.editLectureStatus) {
        if (uiState.editLectureStatus is Result.Success){
            navController.navigate(Screen.TeacherCreateEditActivitiesInModule.createRoute(moduleId))
            viewModel.clearLectureStatus()
        }
    }

    if (openErrorDialog){
        PopupMinimal(
            onDismissRequest = { openErrorDialog = false },
            width = windowInfo.screenWidth * 0.95f,
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
                viewModel.updateLecture(context)
                if (uiState.errorMessage != null){
                    openErrorDialog = true
                }
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.upload_files_color),
            title = {
                Text(
                    text = "LECTURE UPDATE",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    color = LoginText
                )
            },
            dialogText = {
                Text(
                    text = "Are you satisfied with the information uploaded/updated?",
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
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
                            text = "UPDATE LECTURE",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading1(windowInfo),
                            color = TextBlack,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        EditLectureHeader(
                            windowInfo = windowInfo,
                            currentUploadType = uiState.contentTypeName,
                            onUploadTypeChange = viewModel::updateContentTypeName,
                            lectureName = uiState.lectureTitle,
                            onUpdateLectureName = viewModel::updateLectureTitle,
                            lectureDescription = uiState.lectureDescription,
                            onUpdateLectureDescription = viewModel::updateLectureDescription
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        EditLectureMoreInfo(
                            unlockDate = uiState.unlockDate,
                            onUpdateUnlockDate = viewModel::updateUnlockDate,
                            deadlineDate = uiState.deadlineDate,
                            onUpdateDeadlineDate = viewModel::updateDeadlineDate
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    item {
                        when(uiState.contentTypeName){
                            "PDF/DOCS" -> EditPDFCard(
                                height = windowInfo.screenHeight * 0.3f,
                                onPickFile = { uri ->
                                    viewModel.onFilePicked(context, uri)
                                },
                                windowInfo = windowInfo,
                                fileName = uiState.fileName,
                                isLoading = uiState.loading,
                            )
                            "YOUTUBE LINK" -> EditYoutubeLink(
                                windowInfo = windowInfo,
                                youtubeLink = uiState.videoUrl.toString(),
                                onUpdateYoutubeLink = viewModel::updateVideoUrl,
                            )
                            "TEXT" -> EditText(
                                windowInfo = windowInfo,
                                text = uiState.textBody.toString(),
                                onUpdateText = viewModel::updateText,
                            )
                        }
                        Spacer(modifier = Modifier.height(35.dp))
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            CapsuleButton(
                                text = {
                                    Text(
                                        text = "UPDATE LECTURE",
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
        }
    )
}

@Composable
fun EditLectureHeader(
    windowInfo: WindowInfo,
    currentUploadType: String,
    onUploadTypeChange: (String) -> Unit,
    lectureName: String,
    onUpdateLectureName: (String) -> Unit,
    lectureDescription: String,
    onUpdateLectureDescription: (String) -> Unit
){
    val menuList = listOf(
        DropdownMenuItem(
            itemName = "PDF/DOCS",
            onClick = {
                onUploadTypeChange("PDF/DOCS")
            }
        ),
        DropdownMenuItem(
            itemName = "VIDEO",
            onClick = {
                onUploadTypeChange("VIDEO")
            }
        ),
        DropdownMenuItem(
            itemName = "TEXT",
            onClick = {
                onUploadTypeChange("TEXT")
            }
        ),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CustomDropDownMenu(
            menuText = currentUploadType,
            dropdownMenuItems = menuList,
            maxWidthFloat = 0.3f
        )

        Spacer(modifier = Modifier.size(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            CapsuleTextField(
                value = lectureName,
                onValueChange = {
                    onUpdateLectureName(it)
                },
                placeholder = {
                    Text(
                        text = "LECTURE NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                label = {
                    Text(
                        text = "LECTURE NAME",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
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

            CapsuleTextField(
                value = lectureDescription,
                onValueChange = { onUpdateLectureDescription(it) },
                placeholder = {
                    Text(
                        text = "LECTURE DESCRIPTION",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = LoginText
                    )
                },
                label = {
                    Text(
                        text = "LECTURE DESCRIPTION",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
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
                maxLines = 5
            )
        }
    }

    Spacer(modifier = Modifier.height(15.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditLectureMoreInfo(
    unlockDate: LocalDateTime?,
    onUpdateUnlockDate: (LocalDateTime?) -> Unit,
    deadlineDate: LocalDateTime?,
    onUpdateDeadlineDate: (LocalDateTime?) -> Unit
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
                selectedDateTime = unlockDate,
                label = "UNLOCK DATE & TIME",
                onUpdateDateTime = {
                    onUpdateUnlockDate(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReusableDateTimePicker(
                selectedDateTime = deadlineDate,
                label = "DEADLINE DATE & TIME",
                onUpdateDateTime = {
                    onUpdateDeadlineDate(it)
                }
            )
        }
    }
}

@Composable
fun EditPDFCard(
    windowInfo: WindowInfo,
    height: Dp,
    fileName: String?,
    isLoading: Boolean,
    onPickFile: (Uri) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let(onPickFile)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = {
                filePicker.launch(
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                )
            }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SoftGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()

                fileName != null ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_file_present_24),
                            contentDescription = "$fileName present",
                            tint = LoginText
                        )

                        Spacer(modifier = Modifier.size(5.dp))

                        Text(
                            text = "Filename: $fileName",
                            fontSize = ResponsiveFont.heading2(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                else -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.upload_files),
                        contentDescription = "Pick a PDF or DOC",
                        modifier = Modifier.size(height * 0.4f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tap to select PDF/DOC",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo)
                    )
                }
            }
        }
    }
}

@Composable
fun EditYoutubeLink(
    windowInfo: WindowInfo,
    youtubeLink: String,
    onUpdateYoutubeLink: (String) -> Unit
){
    CapsuleTextField(
        value = youtubeLink,
        onValueChange = {
            onUpdateYoutubeLink(it)
        },
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
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(10.dp), clip = false)
            .background(color = SoftGray, shape = RoundedCornerShape(10.dp)),
        enabled = true,
        maxLines = 3
    )
}

@Composable
fun EditText(
    windowInfo: WindowInfo,
    text: String,
    onUpdateText: (String) -> Unit
){
    CapsuleTextField(
        value = text,
        onValueChange = { onUpdateText(it) },
        placeholder = {
            Text(
                text = "TYPE CONTENT",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading2(windowInfo),
                color = LoginText
            )
        },
        label = {
            Text(
                text = "TYPE CONTENT",
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
        maxLines = 8
    )
}

