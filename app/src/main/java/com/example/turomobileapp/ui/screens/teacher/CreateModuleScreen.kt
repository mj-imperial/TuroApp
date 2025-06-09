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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.teacher.CreateModuleViewModel

@Composable
fun CreateModuleScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CreateModuleViewModel
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    var openAlertDialog by remember { mutableStateOf(false) }

    val currentModules = uiState.currentModules.sortedBy { it.moduleName }

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
        if (uiState.moduleCreationStatus == Result.Success(Unit)) {
            Toast.makeText(context, "Module created!", Toast.LENGTH_LONG).show()
        }
        viewModel.clearCreateModule()
        viewModel.clearCreationStatus()
        viewModel.getModulesInCourse()
    }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                viewModel.createModule()
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.module_create),
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
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
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
                    enabled = uiState.isCreationEnabled
                )

                Spacer(modifier = Modifier.height(20.dp))

                uiState.errorMessage?.let {
                    Text(
                        text = "Something went wrong:\n$it",
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        color = MainRed
                    )
                }

                Text(
                    text = "Current Modules",
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(currentModules) {
                        CurrentModulesList(
                            windowInfo = windowInfo,
                            moduleName = it.moduleName,
                            moduleDescription = it.moduleDescription
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CurrentModulesList(
    windowInfo: WindowInfo,
    moduleName: String,
    moduleDescription: String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = moduleName,
                fontSize = ResponsiveFont.heading2(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = TextBlack
            )
            Text(
                text = moduleDescription,
                fontSize = ResponsiveFont.body(windowInfo),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = TextBlack
            )
        }
    }
}