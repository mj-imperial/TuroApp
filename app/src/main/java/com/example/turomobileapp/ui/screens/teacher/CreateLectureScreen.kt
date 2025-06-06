package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.CapsuleTextField
import com.example.turomobileapp.ui.components.CustomDropDownMenu
import com.example.turomobileapp.ui.components.DropdownMenuItem
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.SoftGray
import com.example.turomobileapp.viewmodels.SessionManager

//TODO replace with viewmodel variables after ui
@Composable
fun CreateLectureScreen(
    navController: NavController,
    sessionManager: SessionManager,
    moduleId: String,
    fromTutorial: Boolean
){
    val windowInfo = rememberWindowInfo()

    var currentUploadType by remember { mutableStateOf("PDF/DOCS") }

    val menuList = listOf(
        DropdownMenuItem(
            itemName = "PDF/DOCS",
            onClick = {
                currentUploadType = "PDF/DOCS"
            }
        ),
        DropdownMenuItem(
            itemName = "YOUTUBE LINK",
            onClick = {
                currentUploadType = "YOUTUBE LINK"
            }
        ),
        DropdownMenuItem(
            itemName = "TEXT",
            onClick = {
                currentUploadType = "TEXT"
            }
        ),
    )

    var placeholderActivityName by remember { mutableStateOf("") }
    var placeholderActivityDescription by remember {mutableStateOf("")}

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        hasFloatingActionButton = false,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomDropDownMenu(
                        menuText = currentUploadType,
                        dropdownMenuItems = menuList,
                        maxWidthFloat = 0.3f
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        CapsuleTextField(
                            value = placeholderActivityName,
                            onValueChange = {
                                placeholderActivityName = it
                            },
                            placeholder = {
                                Text(
                                    text = if (fromTutorial) "TUTORIAL NAME" else "LECTURE NAME",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.heading2(windowInfo),
                                    color = LoginText
                                )
                            },
                            label = {
                                Text(
                                    text = if (fromTutorial) "TUTORIAL NAME" else "LECTURE NAME",
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
                            value = placeholderActivityDescription,
                            onValueChange = { placeholderActivityDescription = it },
                            placeholder = {
                                Text(
                                    text = if (fromTutorial) "TUTORIAL DESCRIPTION" else "LECTURE DESCRIPTION",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = ResponsiveFont.heading2(windowInfo),
                                    color = LoginText
                                )
                            },
                            label = {
                                Text(
                                    text = if (fromTutorial) "TUTORIAL DESCRIPTION" else "LECTURE DESCRIPTION",
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

                when(currentUploadType){
                    "PDF/DOCS" -> CreatePDFCard(height = windowInfo.screenHeight * 0.3f)
                    "YOUTUBE LINK" -> CreateYoutubeLink(windowInfo = windowInfo)
                    "TEXT" -> CreateText(windowInfo = windowInfo)
                }
            }
        }
    )
}

//Upload pdf
@Composable
fun CreatePDFCard(
    height: Dp
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = {/*ask for permission to access files upload docs / pdf*/ }),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(R.drawable.upload_files),
                contentDescription = "Upload Files",
                modifier = Modifier.size(height * 0.5f)
            )
        }
    }
}

//upload youtube link
@Composable
fun CreateYoutubeLink(
    windowInfo: WindowInfo
){
    var placeholderLink by remember { mutableStateOf("") }

    CapsuleTextField(
        value = placeholderLink,
        onValueChange = {
            placeholderLink = it
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
        maxLines = 3
    )
}

//upload text
@Composable
fun CreateText(
    windowInfo: WindowInfo
){
    var placeholderText by remember { mutableStateOf("") }
}