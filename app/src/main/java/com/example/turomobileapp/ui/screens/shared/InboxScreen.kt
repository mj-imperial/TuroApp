package com.example.turomobileapp.ui.screens.shared

import AppScaffold
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.models.UserInfo
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InboxScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: InboxViewModel
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()

    var inboxToDelete by remember { mutableStateOf<String?>(null) }
    var openDeleteDialog by remember { mutableStateOf(false) }

    if (openDeleteDialog){
        PopupAlertWithActions(
            onDismissRequest = {
                openDeleteDialog = false
                inboxToDelete = null
            },
            onConfirmation = {
                viewModel.deleteInbox(inboxToDelete.toString())
                openDeleteDialog = false
                inboxToDelete = null
            },
            icon = painterResource(R.drawable.delete_vector_icon),
            title = {
                Text(
                    text = "Delete Message?",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.title(windowInfo)
                )
            },
            dialogText = {
                Text(
                    text = "Are you sure you want to delete?\n You can't reverse this action.",
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
            if (uiState.loading){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }else{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(uiState.inboxList) { inbox ->
                            InboxItem(
                                windowInfo = windowInfo,
                                subjectPreview = inbox.lastMessageSubject.toString(),
                                messagePreview = inbox.lastMessagePreview,
                                timestamp = inbox.lastMessageTimestamp,
                                latestParticipant = inbox.participants.lastOrNull() ?: UserInfo("?", "Unknown", null),
                                isRead = inbox.unreadCount == 0,
                                onNavigateToInboxItem = {
                                    viewModel.markMessageAsRead(inbox.latestMessageId.toString())
                                    navController.navigate(Screen.InboxDetail.createRoute(inbox.inboxId))
                                },
                                onDeleteMessage = {
                                    inboxToDelete = inbox.inboxId
                                    openDeleteDialog = true
                                }
                            )
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(Screen.CreateMessage.route) },
                        icon = { Icon(Icons.Filled.Edit, "Edit.", tint = MainWhite) },
                        text = { Text("Create Message", fontFamily = FontFamily(Font(R.font.alata))) },
                        containerColor = MainOrange,
                        contentColor = MainWhite,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        },
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
            .padding(vertical = 6.dp)
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = latestParticipant.profileImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = latestParticipant.name,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.title(windowInfo),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subjectPreview,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading2(windowInfo),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = messagePreview,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        color = LoginText,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = formattedTime,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
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
