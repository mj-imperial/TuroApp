package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginText
import com.example.turomobileapp.ui.theme.screeningExam1
import com.example.turomobileapp.ui.theme.screeningExam11
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ActivityFlowViewModel
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel

@Composable
fun ViewAllModulesScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ViewAllModulesViewModel,
    courseId: String,
    activityFlowViewModel: ActivityFlowViewModel,
){
    val windowInfo = rememberWindowInfo()

    val uiState by viewModel.uiState.collectAsState()
    val modules = uiState.modules

    val expandedModules = remember { mutableStateOf(setOf<String>()) }

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(20.dp)
            ) {
                items(modules) { module ->
                    val activities = uiState.activities.filter { it.moduleId == module.moduleId }
                    val isExpanded = expandedModules.value.contains(module.moduleId)

                    val screeningExam = activities.filter { it.quizTypeName == "SCREENING_EXAM" }

                    screeningExam.forEach {
                        ScreeningExamCard(
                            windowInfo = windowInfo,
                            screeningExam = it,
                            onNavigateActivity = {
                                //TODO screening navigation
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    ModuleContentCollapsable(
                        windowInfo = windowInfo,
                        moduleName = module.moduleName,
                        moduleDescription = module.moduleDescription,
                        activities = activities,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedModules.value = if (expandedModules.value.contains(module.moduleId)) {
                                expandedModules.value - module.moduleId
                            } else {
                                expandedModules.value + module.moduleId
                            }
                        },
                        navController = navController,
                        courseId = courseId,
                        viewModel = activityFlowViewModel
                    )
                }
            }
        }
    )
}

@Composable
fun ScreeningExamCard(
    windowInfo: WindowInfo,
    screeningExam: ModuleActivityResponse,
    onNavigateActivity: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onNavigateActivity),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(colors = listOf(screeningExam1, screeningExam11)),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.additionalscreening),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = screeningExam.activityName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo)
            )
        }
    }
}


@Composable
fun ModuleContentCollapsable(
    windowInfo: WindowInfo,
    moduleName: String,
    moduleDescription: String,
    activities: List<ModuleActivityResponse>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    navController: NavController,
    courseId: String,
    viewModel: ActivityFlowViewModel
){
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // or themed bg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .animateContentSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if (isExpanded) painterResource(R.drawable.expand_module) else painterResource(R.drawable.hide_module),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = moduleName,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading1(windowInfo)
                    )
                    Text(
                        text = moduleDescription,
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.heading3(windowInfo)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(15.dp)
            ) {
                if (isExpanded){
                    val allPassed = activities
                        .filter { it.activityType == "QUIZ" }
                        .all { it.isUnlocked && it.hasAnswered == true }

                    if (allPassed) {
                        Text(
                            text = "✅ Module Complete!",
                            color = Color(0xFF4CAF50),
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.body(windowInfo),
                            modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                        )
                    }

                    val lectures = activities.filter { it.activityType == "LECTURE" }
                    val tutorials = activities.filter { it.activityType == "TUTORIAL" }
                    val quizzes = activities.filter { it.activityType == "QUIZ" && it.quizTypeName != "SCREENING_EXAM" }

                    val onNavigateToActivity: (ModuleActivityResponse) -> Unit = { activity ->
                        viewModel.setActivityList(activities)
                        viewModel.setCurrentActivityId(activity.activityId)

                        val route = Screen.StudentActivityDetail.createRoute(
                            moduleId = activity.moduleId,
                            activityId = activity.activityId,
                            activityType = activity.activityType,
                            courseId = courseId
                        )

                        navController.navigate(route)
                    }

                    if (lectures.isNotEmpty()){
                        Text(
                            text = "LECTURES",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        lectures.forEach {
                            ActivitiesCard(
                                windowInfo = windowInfo,
                                isUnlocked = it.isUnlocked,
                                onNavigateActivity = { onNavigateToActivity(it) },
                                activityName = it.activityName,
                                colors = listOf(Color(0xFFFFF59D), Color(0xFFFFD54F))
                            )
                        }
                    }

                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (tutorials.isNotEmpty()){
                        Text(
                            text = "TUTORIALS",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        tutorials.forEach {
                            ActivitiesCard(
                                windowInfo = windowInfo,
                                isUnlocked = it.isUnlocked,
                                onNavigateActivity = { onNavigateToActivity(it) },
                                activityName = it.activityName,
                                colors = listOf(Color(0xFFB2EBF2), Color(0xFF4DD0E1))
                            )
                        }
                    }

                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (quizzes.isNotEmpty()){
                        Text(
                            text = "QUIZZES",
                            fontFamily = FontFamily(Font(R.font.alata)),
                            fontSize = ResponsiveFont.heading3(windowInfo),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        quizzes.forEach {
                            ActivitiesCard(
                                windowInfo = windowInfo,
                                isUnlocked = it.isUnlocked,
                                onNavigateActivity = { onNavigateToActivity(it) },
                                activityName = it.activityName,
                                colors = listOf(shortquiz1, shortquiz2)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivitiesCard(
    windowInfo: WindowInfo,
    isUnlocked: Boolean,
    onNavigateActivity: () -> Unit,
    activityName: String,
    colors: List<Color>
){
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .background(brush = Brush.linearGradient(colors = colors), shape = RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = if (isUnlocked)
                    painterResource(R.drawable.outline_check_circle_24)
                else
                    painterResource(R.drawable.outline_circle_24),
                contentDescription = null,
                tint = LoginText
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = activityName,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontSize = ResponsiveFont.heading3(windowInfo)
            )
        }
    }
}