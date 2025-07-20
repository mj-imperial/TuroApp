package com.example.turomobileapp.ui.screens.shared

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.UserInfo
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.PopupAlertWithActions
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.InboxViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//TODO only delete sent
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: InboxViewModel
) {
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    var showSent by remember { mutableStateOf(false) }
    var inboxToDelete by remember { mutableStateOf<String?>(null) }
    var openDeleteDialog by remember { mutableStateOf(false) }

    val messageList = if (showSent) uiState.inboxList?.sent else uiState.inboxList?.incoming

    if (openDeleteDialog) {
        PopupAlertWithActions(
            onDismissRequest = {
                openDeleteDialog = false
                inboxToDelete = null
            },
            onConfirmation = {
                inboxToDelete?.let { viewModel.deleteInbox(it) }
                openDeleteDialog = false
                inboxToDelete = null
            },
            icon = painterResource(R.drawable.delete_vector_icon),
            title = {
                Text("Delete Message?", fontFamily = FontFamily(Font(R.font.alata)), fontSize = ResponsiveFont.title(windowInfo))
            },
            dialogText = {
                Text("Are you sure you want to delete?\nYou can't reverse this action.",
                    fontFamily = FontFamily(Font(R.font.alata)), fontSize = ResponsiveFont.heading3(windowInfo))
            },
            confirmText = {
                Text("Yes", fontFamily = FontFamily(Font(R.font.alata)), fontSize = ResponsiveFont.heading3(windowInfo), color = green)
            },
            dismissText = {
                Text("No", fontFamily = FontFamily(Font(R.font.alata)), fontSize = ResponsiveFont.heading3(windowInfo), color = LoginText)
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
            if (uiState.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = { viewModel.getInboxes() },
                    modifier = Modifier.fillMaxSize().padding(it)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INBOX",
                                modifier = Modifier
                                    .background(
                                        if (!showSent) MainOrange else Color.Transparent,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .clickable { showSent = false }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (!showSent) Color.White else Color.Black,
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "SENT",
                                modifier = Modifier
                                    .background(
                                        if (showSent) MainOrange else Color.Transparent,
                                        RoundedCornerShape(5.dp)
                                    )
                                    .clickable { showSent = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                color = if (showSent) Color.White else Color.Black,
                                fontFamily = FontFamily(Font(R.font.alata))
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            messageList?.let {
                                items(it.size) { index ->
                                    val message = it[index]
                                    InboxItem(
                                        windowInfo = windowInfo,
                                        subjectPreview = message.subject ?: "Blank Subject",
                                        messagePreview = message.message,
                                        timestamp = try {
                                            message.date.toLong()
                                        } catch (e: Exception) {
                                            0L
                                        },
                                        latestParticipant = UserInfo(
                                            userId = message.senderId,
                                            name = message.senderName,
                                            profileImage = message.imageBlob
                                        ),
                                        isRead = !message.unread,
                                        onNavigateToInboxItem = {
                                            viewModel.markMessageAsRead(message.message)
                                            // TODO: Navigate to detail screen
                                        },
                                        onDeleteMessage = {
                                            if (showSent) {
                                                inboxToDelete = message.message
                                                openDeleteDialog = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.CreateMessage.route) },
                        icon = { Icon(Icons.Filled.Edit, "Edit", tint = MainWhite) },
                        text = { Text("Create Message", fontFamily = FontFamily(Font(R.font.alata))) },
                        containerColor = MainOrange,
                        contentColor = MainWhite,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxItem(
    windowInfo: WindowInfo,
    subjectPreview: String,
    messagePreview: String,
    timestamp: Long,
    latestParticipant: UserInfo,
    isRead: Boolean,
    onNavigateToInboxItem: () -> Unit,
    onDeleteMessage: () -> Unit
) {
    val formattedTime = remember(timestamp) {
        Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable(onClick = onNavigateToInboxItem),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MainWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlobImage(
                    byteArray = latestParticipant.profileImage,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth()){
                        Text(
                            text = latestParticipant.name,
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading3(windowInfo),
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subjectPreview,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = messagePreview,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = LoginText,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = formattedTime,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.caption(windowInfo),
                        color = LoginText
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Top
                ) {
                    IconButton(onClick = onDeleteMessage) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = "Delete",
                            tint = MainRed
                        )
                    }
                }
            }

            if (!isRead) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 10.dp, y = 10.dp)
                        .background(color = Color.Red, shape = CircleShape)
                )
            }
        }
    }
}
