package com.example.turomobileapp.ui.screens.student

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.BlobImage
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.LoginTextLight
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.headingText
import com.example.turomobileapp.ui.theme.practice1
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.ui.theme.screeningExam1
import com.example.turomobileapp.ui.theme.screeningExam2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseId: String,
    sessionManager: SessionManager,
    viewModel: ViewAllModulesViewModel
){
    val windowInfo = rememberWindowInfo()
    val width = windowInfo.screenWidth
    val height = windowInfo.screenHeight
    val pullRefreshState = rememberPullToRefreshState()

    val uiState by viewModel.uiState.collectAsState()

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
                        viewModel.getCurrentModule()
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        CourseHeader(
                            windowInfo = windowInfo,
                            height = height,
                            onClickCurrentModule = {
                                navController.navigate(Screen.StudentModuleActivities.createRoute(courseId, uiState.moduleId))
                            },
                            moduleImage = uiState.modulePicture,
                            moduleProgress = uiState.moduleProgress,
                            moduleName = uiState.moduleName,
                        )

                        CourseActivities(
                            windowInfo = windowInfo,
                            height = height,
                            navController = navController,
                            width = width,
                            courseId = courseId
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CourseHeader(
    windowInfo: WindowInfo,
    height: Dp,
    onClickCurrentModule: () -> Unit,
    moduleImage: ByteArray,
    moduleProgress: Double,
    moduleName: String
) {
    val columnHeight = max(height * 0.25f, 180.dp)
    val progressText = "Progress: ${"%.0f".format(moduleProgress)}%"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(columnHeight)
            .padding(bottom = 30.dp)
            .clickable(onClick = onClickCurrentModule)
    ) {
        BlobImage(
            byteArray = moduleImage,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)),
            alpha = 0.6f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp)
                .background(LoginTextLight.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .shadow(4.dp, RoundedCornerShape(10.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = progressText,
                fontSize = ResponsiveFont.heading1(windowInfo),
                fontFamily = FontFamily(Font(R.font.alexandria)),
                color = headingText,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = moduleName,
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alexandria)),
                    color = headingText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onClickCurrentModule,
                    shape = CircleShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MainWhite,
                        contentColor = TextBlack
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.readmore_icon),
                        contentDescription = "Read More",
                        tint = TextBlack
                    )
                }
            }
        }
    }
}


@Composable
fun CourseActivities(
    navController: NavController,
    windowInfo: WindowInfo,
    width: Dp,
    height: Dp,
    courseId: String,
){
    val cardWidth = width * 0.7f
    val columnHeight = height * 0.5f
    val cardHeight = columnHeight * 0.2f
    val iconSize = cardWidth * 0.4f

    val activitiesList = listOf(
        Activities(
            name = R.string.ViewModules,
            icon = R.drawable.shortquiz_icon,
            route = Screen.StudentModules.createRoute(courseId),
            colors = listOf(shortquiz1,shortquiz2)
        ),
        Activities(
            name = R.string.Analytics,
            icon = R.drawable.practicequiz_icon,
            route = Screen.StudentCourseAnalytics.createRoute(courseId),
            colors = listOf(practice1,practice2)
        ),
        Activities(
            name = R.string.ScreeningExam,
            icon = R.drawable.screeningexam_icon,
            route = Screen.StudentScreening.route,
            colors = listOf(screeningExam1,screeningExam2)
        ),
        Activities(
            name = R.string.LongQuizExam,
            icon = R.drawable.longquiz_icon,
            route = Screen.StudentLongQuiz.route,
            colors = listOf(screeningExam1,screeningExam2)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Activities",
            fontSize = ResponsiveFont.heading2(windowInfo),
            fontFamily = FontFamily(Font(R.font.alexandria)),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp)
        )

        activitiesList.forEach { activity ->
            Card(
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
                    .clickable(onClick = {
                        navController.navigate(activity.route)
                    }),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(activity.colors))
                        .padding(horizontal = 5.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(activity.icon),
                            contentDescription = stringResource(activity.name),
                            modifier = Modifier.size(iconSize),
                        )

                        Text(
                            text = stringResource(activity.name),
                            fontSize = ResponsiveFont.body(windowInfo),
                            fontFamily = FontFamily(Font(R.font.alexandria)),
                            color = TextBlack,
                            textAlign = TextAlign.End,
                            maxLines = 2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class Activities(
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    val route: String,
    val colors: List<Color>
)

