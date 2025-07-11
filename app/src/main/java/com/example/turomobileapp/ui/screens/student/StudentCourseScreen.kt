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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.AppScaffold
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.practice1
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.ui.theme.screeningExam1
import com.example.turomobileapp.ui.theme.screeningExam2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel

//TODO check if student is catch-up
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                ) {

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
    )
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
            fontSize = ResponsiveFont.title(windowInfo),
            fontFamily = FontFamily(Font(R.font.alata)),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 20.dp, top = 25.dp)
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

