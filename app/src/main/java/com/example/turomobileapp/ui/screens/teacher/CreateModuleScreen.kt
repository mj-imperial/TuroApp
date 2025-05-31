package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.colors
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.CourseActionsViewModel

@Composable
fun CreateModuleScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CourseActionsViewModel
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    var openAlertDialog by remember { mutableStateOf(false) }

    if (uiState.loading) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val context = LocalContext.current
    LaunchedEffect(uiState.moduleCreationStatus) {
        if (uiState.moduleCreationStatus == true) {
            Toast.makeText(context, "Module created!", Toast.LENGTH_LONG).show()
            viewModel.clearCreateModule()
        }
    }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                viewModel::createModule
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.save_icon),
            title = {
                Text(
                    text = "CREATE MODULE",
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            },
            dialogText = {
                Text(
                    text = "Are you satisfied with the information inputted?",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            },
            confirmText = {
                Text(
                    text = "Yes",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "No",
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
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CapsuleTextField(
                        value = uiState.moduleName.toString(),
                        onValueChange = {
                            viewModel.updateModuleName(it)
                        },
                        placeholder = {
                            Text(
                                text = "MODULE NAME",
                                color = LoginText,
                                fontSize = ResponsiveFont.body(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        },
                        label = {
                            Text(
                                text = "MODULE NAME",
                                color = LoginText,
                                fontSize = ResponsiveFont.body(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        },
                        isSingleLine = true,
                        roundedCornerShape = 5.dp,
                        colors = colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = SoftGray,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp,shape = RoundedCornerShape(28.dp),clip = false
                            )
                            .background(
                                color = SoftGray,shape = RoundedCornerShape(28.dp)
                            ),
                        enabled = true,
                    )

                    CapsuleTextField(
                        value = uiState.moduleDescription.toString(),
                        onValueChange = {
                            viewModel.updateModuleDescription(it)
                        },
                        placeholder = {
                            Text(
                                text = "MODULE DESCRIPTION (OPTIONAL)",
                                color = LoginText,
                                fontSize = ResponsiveFont.body(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        },
                        label = {
                            Text(
                                text = "MODULE DESCRIPTION",
                                color = LoginText,
                                fontSize = ResponsiveFont.body(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        },
                        isSingleLine = false,
                        roundedCornerShape = 5.dp,
                        colors = colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = SoftGray,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(windowInfo.screenHeight * 0.2f)
                            .shadow(
                                elevation = 10.dp,shape = RoundedCornerShape(28.dp),clip = false
                            )
                            .background(
                                color = SoftGray,shape = RoundedCornerShape(28.dp)
                            ),
                        enabled = true,
                    )

                    CapsuleButton(
                        text = {
                            Text(
                                text = "CREATE MODULE",
                                color = TextBlack,
                                fontSize = ResponsiveFont.body(windowInfo),
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        },
                        onClick = {
                            openAlertDialog = true
                        },
                        modifier = Modifier.width(windowInfo.screenWidth * 0.7f),
                        roundedCornerShape = 10.dp,
                        buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                        contentPadding = PaddingValues(10.dp),
                        buttonColors = ButtonDefaults.buttonColors(MainOrange),
                        enabled = true
                    )
                }

                uiState.errorMessage?.let {
                    Text(
                        text = "Something went wrong:\n$it",
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        color = MainRed
                    )
                }
            }
        }
    )
}