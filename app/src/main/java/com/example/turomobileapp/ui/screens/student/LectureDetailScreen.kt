package com.example.turomobileapp.ui.screens.student

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobFileViewer
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.MainRed
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ActivityFlowViewModel
import com.example.turomobileapp.viewmodels.student.LectureDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LectureDetailScreen(
    viewModel: LectureDetailViewModel,
    navController: NavController,
    sessionManager: SessionManager,
    activityId: String,
    courseId: String,
    activityFlowViewModel: ActivityFlowViewModel
){
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(activityId) {
        activityFlowViewModel.setCurrentActivityId(activityId)
    }

    val previous = activityFlowViewModel.goToPrevious()
    val next = activityFlowViewModel.goToNext()
    val hasPrevious = previous != null
    val hasNext = next != null

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
                PullToRefreshBox(
                    isRefreshing = uiState.loading,
                    state = pullRefreshState,
                    onRefresh = {
                        viewModel.getLecture()
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            LectureTitle(
                                height = windowInfo.screenHeight * 0.17f,
                                windowInfo = windowInfo,
                                lectureName = uiState.lectureTitle
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp),
                                thickness =  1.dp,
                                color = LoginText
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.lectureDescription,
                                    fontSize = ResponsiveFont.heading3(windowInfo),
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    minLines = 1,
                                    softWrap = true,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                BlobFileViewer(
                                    windowInfo = windowInfo,
                                    fileBytes = uiState.fileUrl
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 15.dp),
                                thickness =  1.dp,
                                color = LoginText
                            )
                        }

                        item {
                            PreviousNextButtonLecture(
                                windowInfo = windowInfo,
                                onClickPrevious = {
                                    previous?.let {
                                        activityFlowViewModel.setCurrentActivityId(it.activityId)
                                        val route = Screen.StudentActivityDetail.createRoute(
                                            moduleId = it.moduleId,
                                            activityId = it.activityId,
                                            activityType = it.activityType,
                                            courseId = courseId
                                        )
                                        navController.navigate(route)
                                    }
                                },
                                onClickNext = {
                                    next?.let {
                                        activityFlowViewModel.setCurrentActivityId(it.activityId)
                                        val route = Screen.StudentActivityDetail.createRoute(
                                            moduleId = it.moduleId,
                                            activityId = it.activityId,
                                            activityType = it.activityType,
                                            courseId = courseId
                                        )
                                        navController.navigate(route)
                                    }
                                },
                                isPreviousEnabled = hasPrevious,
                                isNextEnabled = hasNext
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun LectureTitle(
    height: Dp,
    windowInfo: WindowInfo,
    lectureName: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.shortquiz_icon),
            contentDescription = null,
            modifier = Modifier.size(height)
        )

        Text(
            text = lectureName,
            fontSize = ResponsiveFont.title(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            color = headingText
        )
    }
}

@Composable
fun PreviousNextButtonLecture(
    windowInfo: WindowInfo,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
    isPreviousEnabled: Boolean,
    isNextEnabled: Boolean
){
    val buttonWidth = (windowInfo.screenWidth) * 0.3f
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CapsuleButton(
            text = {
                Text(
                    text = "PREVIOUS",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainRed
                )
            },
            onClick = onClickPrevious,
            roundedCornerShape = 5.dp,
            modifier = Modifier
                .width(buttonWidth)
                .border(1.dp, MainRed,RoundedCornerShape(5.dp)),
            buttonElevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(vertical = 10.dp),
            buttonColors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = isPreviousEnabled
        )

        CapsuleButton(
            text = {
                Text(
                    text = "NEXT",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = MainRed
                )
            },
            onClick = onClickNext,
            roundedCornerShape = 5.dp,
            modifier = Modifier
                .width(buttonWidth)
                .border(1.dp,MainRed,RoundedCornerShape(5.dp)),
            buttonElevation = ButtonDefaults.buttonElevation(0.dp),
            contentPadding = PaddingValues(10.dp),
            buttonColors = ButtonDefaults.buttonColors(Color.Transparent),
            enabled = isNextEnabled
        )
    }
}