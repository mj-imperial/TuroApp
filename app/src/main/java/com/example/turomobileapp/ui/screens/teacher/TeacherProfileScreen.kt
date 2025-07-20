package com.example.turomobileapp.ui.screens.teacher

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.screens.student.ProfileCardItems
import com.example.turomobileapp.ui.theme.MainOrange
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.authentication.LoginViewModel
import com.example.turomobileapp.viewmodels.teacher.TeacherProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(
    navController: NavController,
    sessionManager: SessionManager,
    loginViewModel: LoginViewModel = hiltViewModel(),
    viewModel: TeacherProfileViewModel = hiltViewModel()
){
    val windowInfo = rememberWindowInfo()
    val screenHeight = windowInfo.screenHeight
    val headerHeight = screenHeight * 0.18f
    val imageSize = windowInfo.screenWidth * 0.3f
    val imageOverlap = imageSize / 2f
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(true) {
        viewModel.getTeacherCourses()
    }

    val uiState by viewModel.uiState.collectAsState()

    val userInfo = listOf(
        ProfileCardItems("NAME", R.drawable.fullname_icon, uiState.teacherName),
        ProfileCardItems("EMAIL", R.drawable.mail_icon, uiState.email),
        ProfileCardItems("ROLE", R.drawable.role_icon, uiState.role),
    )

    AppScaffold(
        navController = navController,
        modifier = Modifier,
        canNavigateBack = true,
        navigateUp = {navController.navigateUp()},
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { innerPadding ->
            if (uiState.loading){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }else {
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getTeacherCourses()
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(headerHeight)
                                    .background(MainOrange)
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(imageSize)
                                        .offset(y = (-imageOverlap * 1.9f).coerceAtLeast((-40).dp))
                                        .align(Alignment.CenterHorizontally)
                                        .border(4.dp, MainOrange, CircleShape)
                                        .clip(CircleShape)
                                ) {
                                    uiState.profilePic?.let {
                                        if (it.isEmpty()) AsyncImage(
                                            model = uiState.profilePic,
                                            placeholder = painterResource(R.drawable.default_account),
                                            error = painterResource(R.drawable.default_account),
                                            contentDescription = "Profile picture",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )else{
                                            BlobImage(
                                                byteArray = uiState.profilePic!!,
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(-imageOverlap))

                                userInfo.forEach { item ->
                                    ProfileField(
                                        nameRes = item.name,
                                        iconRes = item.icon,
                                        value = item.value
                                    )
                                }
                            }
                        }

                        items(uiState.sectionsTaught) { entry ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 31.dp, bottom = 15.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.course_profile),
                                        contentDescription = null
                                    )

                                    Spacer(modifier = Modifier.width(25.dp))

                                    Text(
                                        text = entry
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            CapsuleButton(
                                text = {
                                    Text(
                                        text = stringResource(R.string.Logout),
                                        color = MainRed,
                                        fontSize = ResponsiveFont.heading3(windowInfo),
                                        fontFamily = FontFamily(Font(R.font.alata))
                                    )
                                },
                                onClick = {
                                    loginViewModel.logout()
                                    navController.navigate(Screen.Login.route)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                                    .border(2.dp, MainRed, RoundedCornerShape(28.dp)),
                                roundedCornerShape = 28.dp,
                                buttonElevation = ButtonDefaults.buttonElevation(8.dp),
                                buttonColors = ButtonDefaults.buttonColors(
                                    containerColor = MainWhite,
                                    contentColor = MainRed,
                                    disabledContainerColor = Color.DarkGray,
                                    disabledContentColor = Color.DarkGray,
                                ),
                                enabled = true
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun ProfileField(
    nameRes: String,
    @DrawableRes iconRes: Int,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(25.dp))
        Column {
            Text(
                text = nameRes,
                fontSize = ResponsiveFont.heading3(rememberWindowInfo()),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = ResponsiveFont.body(rememberWindowInfo()),
                fontFamily = FontFamily(Font(R.font.alata)),
                color = Color.Black
            )
        }
    }
}