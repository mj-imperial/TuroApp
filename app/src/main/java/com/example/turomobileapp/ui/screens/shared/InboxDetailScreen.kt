package com.example.turomobileapp.ui.screens.shared

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.InboxDetailViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxDetailScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: InboxDetailViewModel,
    inboxId: String
){
    val windowInfo = rememberWindowInfo()

    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

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
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getInbox()
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(uiState.messages) { message ->
                            SubjectHeader(
                                windowInfo = windowInfo,
                                subject = message.subject
                            )

                            val currentUserId = sessionManager.userId.collectAsState().value

                            val displayName = if (message.senderId == currentUserId) {
                                "Me to ${message.recipientName}"
                            } else {
                                "${message.senderName} to Me"
                            }

                            MessageHeader(
                                windowInfo = windowInfo,
                                profilePic = message.senderPic,
                                timestamp = message.timestamp,
                                onClickReply = {
                                    val (recipientId, recipientName, recipientPic) = if (message.senderId == currentUserId) {
                                        Triple(message.recipientId, message.recipientName, message.recipientPic ?: byteArrayOf())
                                    } else {
                                        Triple(message.senderId, message.senderName, message.senderPic ?: byteArrayOf())
                                    }

                                    viewModel.setReplyInfo(recipientId, recipientName, recipientPic)
                                    navController.navigate(Screen.ReplyScreen.createRoute(inboxId))
                                },
                                displayName = displayName
                            )

                            MessageBody(
                                windowInfo = windowInfo,
                                body = message.body,
                                onClickReply = {
                                    val (recipientId, recipientName, recipientPic) = if (message.senderId == currentUserId) {
                                        Triple(message.recipientId, message.recipientName, message.recipientPic ?: byteArrayOf())
                                    } else {
                                        Triple(message.senderId, message.senderName, message.senderPic ?: byteArrayOf())
                                    }

                                    viewModel.setReplyInfo(recipientId, recipientName, recipientPic)
                                    navController.navigate(Screen.ReplyScreen.createRoute(inboxId))
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SubjectHeader(
    windowInfo: WindowInfo,
    subject: String
){
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Subject: $subject",
            fontSize = ResponsiveFont.title(windowInfo),
            fontFamily = FontFamily(Font((R.font.alata))),
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }

    HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, LoginText)
    Spacer(modifier = Modifier.height(15.dp))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageHeader(
    windowInfo: WindowInfo,
    profilePic: ByteArray?,
    timestamp: Long,
    onClickReply: () -> Unit,
    displayName: String
){
    val formattedTime = remember(timestamp) {
        Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BlobImage(
            byteArray = profilePic,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                fontSize = ResponsiveFont.heading2(windowInfo),
                fontFamily = FontFamily(Font((R.font.alata)))
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedTime,
                fontSize = ResponsiveFont.heading3(windowInfo),
                fontFamily = FontFamily(Font((R.font.alata))),
                color = LoginText
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onClickReply) {
                Icon(
                    painter = painterResource(R.drawable.reply_icon),
                    contentDescription = null,
                    tint = LoginText
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))
}


@Composable
fun MessageBody(
    windowInfo: WindowInfo,
    body: String,
    onClickReply: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = body,
            fontFamily = FontFamily(Font(R.font.alata)),
            fontSize = ResponsiveFont.heading3(windowInfo),
            minLines = 1
        )

        TextButton(onClick = onClickReply) {
            Text(
                text = "Reply",
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo),
                color = MainOrange
            )
        }
    }
}