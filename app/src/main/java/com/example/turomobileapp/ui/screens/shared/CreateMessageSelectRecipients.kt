package com.example.turomobileapp.ui.screens.shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.shared.CreateMessageViewModel
import com.example.turomobileapp.viewmodels.shared.MessageRecipient

@Composable
fun CreateMessageSelectRecipients(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: CreateMessageViewModel,
    courseName: String
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val selected = viewModel.selectedTempRecipients

    LaunchedEffect(Unit) {
        viewModel.initTempRecipientsIfEmpty()
    }

    val course = uiState.emailCourses.find { it.courseName == courseName }

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
            ){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    course?.let { selectedCourse ->
                        item {
                            Text(
                                text = "TEACHER",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                modifier = Modifier.padding(10.dp)
                            )

                            val teacher = MessageRecipient(
                                userId = selectedCourse.teacherId,
                                name = selectedCourse.teacherName,
                                email = selectedCourse.teacherEmail,
                                profilePic = selectedCourse.teacherPic
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                RecipientCheckItem(
                                    recipient = teacher,
                                    isSelected = selected.any{ it.userId == teacher.userId },
                                    onToggle = { viewModel.toggleTempRecipient(teacher) },
                                    windowInfo = windowInfo
                                )
                            }

                            HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, LoginText)
                        }

                        item {
                            Text(
                                text = "STUDENTS",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.heading3(windowInfo),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        items(selectedCourse.students) { student ->
                            val recipient = MessageRecipient(
                                userId = student.userId,
                                name = student.studentName,
                                email = student.email,
                                profilePic = student.studentPic
                            )

                            RecipientCheckItem(
                                recipient = recipient,
                                isSelected = selected.any { it.userId == recipient.userId },
                                onToggle = { viewModel.toggleTempRecipient(recipient) },
                                windowInfo = windowInfo
                            )

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.saveSelectedRecipients()
                        navController.popBackStack()
                    },
                    icon = {
                        Icon(Icons.Filled.Save, "Save", tint = MainWhite)
                    },
                    text = {
                        Text(
                            text = "DONE",
                            fontFamily = FontFamily(Font(R.font.alata))
                        )
                    },
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
fun RecipientCheckItem(
    windowInfo: WindowInfo,
    recipient: MessageRecipient,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            recipient.profilePic?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = recipient.name,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading2(windowInfo),
            )
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
    }
}
