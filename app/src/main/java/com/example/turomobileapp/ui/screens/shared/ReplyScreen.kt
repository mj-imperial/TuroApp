package com.example.turomobileapp.ui.screens.shared

import com.example.turomobileapp.ui.components.AppScaffold
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.PopupMinimal
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.InboxDetailViewModel

@Composable
fun ReplyScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: InboxDetailViewModel
){
    val windowInfo = rememberWindowInfo()
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    var openAlertDialog by remember { mutableStateOf(false) }
    var openErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.replyMessageStatus) {
        if (uiState.replyMessageStatus is Result.Success){
            Toast.makeText(context, "Message successfully sent.",Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Inbox.route)
            viewModel.clearReplyMessage()
        }
    }

    if (openErrorDialog){
        PopupMinimal(
            onDismissRequest = { openErrorDialog = false },
            width = windowInfo.screenWidth * 0.9f,
            height = windowInfo.screenHeight * 0.5f,
            padding = 10.dp,
            roundedCornerShape = 10.dp,
            dialogText = uiState.errorMessage.toString(),
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = ResponsiveFont.heading3(windowInfo),
            textColor = LoginText
        )
    }

    if (openAlertDialog){
        PopupAlertWithActions(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = {
                viewModel.sendReply()
                if (uiState.errorMessage != null){
                    openErrorDialog = true
                }
                openAlertDialog = false
            },
            icon = painterResource(R.drawable.send_message),
            title = {
                Text(
                    text = "Send Message",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo)
                )
            },
            dialogText = {
                Text(
                    text = "Are you sure you want to send this message?",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo)
                )
            },
            confirmText = {
                Text(
                    text = "Yes",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = green
                )
            },
            dismissText = {
                Text(
                    text = "No",
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .background(MainWhite)
                ) {
                    item {
                        RecipientHeader(
                            windowInfo = windowInfo,
                            recipientName = uiState.recipientName,
                            recipientPic = uiState.recipientPic
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                        HorizontalDivider(modifier = Modifier.fillMaxSize(), 1.dp, LoginText)
                    }

                    item {
                        Spacer(modifier = Modifier.height(15.dp))
                        ReplySubject(
                            subject = uiState.subject,
                            onChange = viewModel::updateSubject
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                        HorizontalDivider(modifier = Modifier.fillMaxSize(), 1.dp, LoginText)
                    }

                    item {
                        ReplyBody(
                            value = uiState.body,
                            onValueChange = viewModel::updateBody,
                        )
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = { openAlertDialog = true },
                    icon = { Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = MainWhite) },
                    text = { Text("DONE", fontFamily = FontFamily(Font(R.font.alata))) },
                    containerColor = MainOrange,
                    contentColor = MainWhite,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    )
}

@Composable
fun RecipientHeader(
    windowInfo: WindowInfo,
    recipientName: String,
    recipientPic: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "To:",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .border(1.dp, LoginText, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                AsyncImage(
                    model = recipientPic,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = recipientName,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.body(windowInfo)
                )
            }
        }
    }
}

@Composable
fun ReplySubject(
    subject: String,
    onChange: (String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Subject:",
            fontFamily = FontFamily(Font(R.font.alata)),
            color = TextBlack,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        TextField(
            value = subject,
            onValueChange = onChange,
            singleLine = true,
            placeholder = { Text("Enter subject") },
            modifier = Modifier
                .fillMaxWidth()
                .background(MainWhite),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ReplyBody(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Message...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(MainWhite),
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}