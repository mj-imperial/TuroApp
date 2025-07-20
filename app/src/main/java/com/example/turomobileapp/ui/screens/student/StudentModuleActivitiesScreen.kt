package com.example.turomobileapp.ui.screens.student

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.LoginTextLight
import com.example.turomobileapp.ui.theme.LoginTextLight2
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.performance1
import com.example.turomobileapp.ui.theme.performance2
import com.example.turomobileapp.ui.theme.practice1
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ActivityFlowViewModel
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentModuleActivitiesScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ViewAllModulesViewModel,
    courseId: String,
    moduleId: String,
    activityFlowViewModel: ActivityFlowViewModel
) {
    val windowInfo = rememberWindowInfo()
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.getActivitiesInModule(moduleId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val lectures = uiState.activities.filter { it.activityType == "LECTURE" }
    val tutorials = uiState.activities.filter { it.activityType == "TUTORIAL" }
    val tutorialQuizzes = uiState.activities.filter { it.activityType == "QUIZ" && it.quizTypeName?.uppercase() == "PRACTICE" }
    val shortQuizzes = uiState.activities.filter { it.activityType == "QUIZ" && it.quizTypeName?.uppercase() == "SHORT" }

    val onNavigateToActivity: (ModuleActivityResponse) -> Unit = { activity ->
        activityFlowViewModel.setActivityList(uiState.activities)
        activityFlowViewModel.setCurrentActivityId(activity.activityId)

        val route = Screen.StudentActivityDetail.createRoute(
            moduleId = activity.moduleId,
            activityId = activity.activityId,
            activityType = activity.activityType,
            courseId = courseId
        )

        navController.navigate(route)
    }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            viewModel.clearModuleName()
            navController.navigateUp()
        },
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
                    onRefresh = { viewModel.getActivitiesInModule(moduleId) },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 15.dp)
                                    .background(LoginTextLight)
                            ) {
                                Text(
                                    text = uiState.moduleName,
                                    fontSize = ResponsiveFont.title(windowInfo),
                                    color = headingText,
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    modifier = Modifier.padding(20.dp).clip(RoundedCornerShape(10.dp))
                                )
                            }
                        }

                        activitySectionContent(
                            title = "LECTURES AND VIDEOS",
                            subtitle = "Read and learn with the resources and watch tutorials",
                            activities = lectures + tutorials,
                            image1 = R.drawable.video2,
                            image2 = R.drawable.video,
                            windowInfo = windowInfo,
                            onNavigateToActivity = onNavigateToActivity,
                            colors = listOf(performance1, performance2)
                        )

                        activitySectionContent(
                            title = "SKILL-HONING TUTORIALS",
                            subtitle = "Learn how to solve problems and hone your skills.",
                            activities = tutorialQuizzes,
                            image1 = R.drawable.practicequiz_icon,
                            windowInfo = windowInfo,
                            onNavigateToActivity = onNavigateToActivity,
                            colors = listOf(practice1, practice2)
                        )

                        activitySectionContent(
                            title = "SHORT QUIZZES",
                            subtitle = "Test your skills. You can do as many attempts as placed.",
                            activities = shortQuizzes,
                            image1 = R.drawable.quiz_detail_icon,
                            windowInfo = windowInfo,
                            onNavigateToActivity = onNavigateToActivity,
                            colors = listOf(shortquiz1, shortquiz2)
                        )
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun LazyListScope.activitySectionContent(
    title: String,
    subtitle: String,
    activities: List<ModuleActivityResponse>,
    @DrawableRes image1: Int,
    @DrawableRes image2: Int? = null,
    windowInfo: WindowInfo,
    onNavigateToActivity: (ModuleActivityResponse) -> Unit,
    colors: List<Color>
) {
    item {
        HorizontalDivider(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), 0.7.dp, LoginText)
        ActivityHeaders(
            windowInfo = windowInfo,
            image1 = painterResource(image1),
            image2 = image2?.let { painterResource(it) },
            title = title,
            subtitle = subtitle
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), 0.7.dp, LoginText)
        Spacer(Modifier.height(10.dp))
    }
    items(activities, key = { it.activityId }) {
        val painter = when (it.activityType) {
            "TUTORIAL" -> image2?.let { painterResource(it) } ?: painterResource(image1)
            else -> painterResource(image1)
        }
        ActivitiesCard(
            windowInfo = windowInfo,
            activityLogo = painter,
            isUnlocked = it.isUnlocked,
            onNavigateActivity = { onNavigateToActivity(it) },
            activityName = it.activityName,
            colors = colors,
            unlockDate = it.unlockDate,
            isLockedDate = it.isLockedDate
        )
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), 0.7.dp, LoginText)
    }
}

@Composable
fun ActivityHeaders(
    windowInfo: WindowInfo,
    image1: Painter,
    image2: Painter? = null,
    title: String,
    subtitle: String
){
    val hasImage2 = image2 != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp, horizontal = 15.dp)
            .background(LoginTextLight2)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {
            if (hasImage2){
                Image(
                    painter = image1,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).padding(end = 2.dp)
                )

                Image(
                    painter = image2,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).padding(end = 6.dp)
                )
            }else{
                Image(
                    painter = image1,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).padding(end = 6.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    fontSize = ResponsiveFont.heading2(windowInfo),
                    color = headingText,
                    fontFamily = FontFamily(Font(R.font.alata))
                )

                Text(
                    text = subtitle,
                    fontSize = ResponsiveFont.caption(windowInfo),
                    color = headingText,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivitiesCard(
    windowInfo: WindowInfo,
    activityLogo: Painter,
    isUnlocked: Boolean,
    isLockedDate: Boolean,
    onNavigateActivity: () -> Unit,
    activityName: String,
    colors: List<Color>,
    unlockDate: LocalDateTime?
){
    val dateFormatterOut = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
    val unlockDateFormatted = unlockDate?.format(dateFormatterOut) ?: "—"

    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .alpha(if (isUnlocked) 1f else 0.5f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onNavigateActivity
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .background(brush = Brush.linearGradient(colors = colors), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = if (isUnlocked)
                    painterResource(R.drawable.outline_check_circle_24)
                else
                    painterResource(R.drawable.outline_circle_24),
                contentDescription = null,
                tint = MainWhite
            )

            Spacer(modifier = Modifier.width(20.dp))

            Image(
                painter = activityLogo,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isLockedDate) "LOCKED" else activityName,
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    color = MainWhite
                )

                if (isLockedDate){
                    Text(
                        text = "Will unlock on: $unlockDateFormatted",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.body(windowInfo),
                        color = MainWhite
                    )
                }
            }
        }
    }
}