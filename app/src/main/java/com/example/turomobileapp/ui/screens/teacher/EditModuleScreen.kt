package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.EditModuleViewModel

@Composable
fun EditModuleScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: EditModuleViewModel,
    courseId: String
){
    val windowInfo = rememberWindowInfo()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    var openAlertDialog by remember { mutableStateOf(false) }
    var openErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.editModuleStatus) {
        if (uiState.editModuleStatus is Result.Success){
            Toast.makeText(context, "Module successfully updated.",Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.TeacherActivityModules.createRoute(courseId))
            viewModel.clearEditStatus()
        }else if(uiState.editModuleStatus is Result.Failure){
            val msg = uiState.errorMessage
                ?: "Unknown error"
            Toast.makeText(context, "Failed to update module: $msg", Toast.LENGTH_LONG).show()
            viewModel.clearEditStatus()
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
                viewModel.saveEditedModule()
                if (uiState.errorMessage != null){
                    openErrorDialog = true
                }
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.edit_icon_color),
            title = {
                Text(
                    text = "MODULE EDIT",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontWeight = FontWeight.Medium
                )
            },
            dialogText = {
                Text(
                    text = "Are you satisfied with the information you inputted?\n Changes cannot be reversed.",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo)
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }else{
                Column(
                    modifier = Modifier
                        .padding(it)
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EDIT MODULE",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading1(windowInfo),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 15.dp)
                    )

                    CapsuleTextField(
                        value = uiState.moduleName,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp, shape = RoundedCornerShape(28.dp), clip = false
                            )
                            .background(
                                color = SoftGray, shape = RoundedCornerShape(28.dp)
                            ),
                        enabled = true,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CapsuleTextField(
                        value = uiState.moduleDescription,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(windowInfo.screenHeight * 0.2f)
                            .shadow(
                                elevation = 10.dp, shape = RoundedCornerShape(28.dp), clip = false
                            )
                            .background(
                                color = SoftGray, shape = RoundedCornerShape(28.dp)
                            ),
                        enabled = true,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CapsuleButton(
                        text = {
                            Text(
                                text = "UPDATE MODULE",
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
                        enabled = isFormValid
                    )
                }
            }
        }
    )
}