package com.example.turomobileapp.ui.screens.teacher

import AppScaffold
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.longquiz1
import com.example.turomobileapp.ui.theme.longquiz2
import com.example.turomobileapp.ui.theme.practice1
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.ui.theme.quiz1
import com.example.turomobileapp.ui.theme.quiz2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.ui.theme.tutorial1
import com.example.turomobileapp.ui.theme.tutorial2
import com.example.turomobileapp.viewmodels.SessionManager

@Composable
fun TeacherCourseScreen(
    navController: NavController,
    courseId: String,
    sessionManager: SessionManager,
    coursePic: String
){
    val windowInfo = rememberWindowInfo()
    
    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = stringResource(R.string.ViewModules),
                    fontFamily = FontFamily(Font(R.font.alexandria)),
                    fontSize = ResponsiveFont.body(windowInfo),
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable(onClick = {
                            navController.navigate(Screen.TeacherCreateModule.route)
                        }),
                )

                CourseHeader(
                    height = windowInfo.screenHeight,
                    coursePic = coursePic
                )

                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ) {
                    CourseActivities(
                        navController = navController,
                        windowInfo = windowInfo,
                        width = windowInfo.screenWidth,
                        height = windowInfo.screenHeight,
                        courseId = courseId
                    )
                }
            }
        }
    )
}

@Composable
fun CourseHeader(
    height: Dp,
    coursePic: String
){
    val modulePlaceholderImage = "https://images.pexels.com/photos/27409729/pexels-photo-27409729/free-photo-of-dice-game-on-black-and-white-background.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height * 0.20f)
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            AsyncImage(
                model = if (coursePic.isNotEmpty()) coursePic else modulePlaceholderImage,
                contentDescription = "Module Image",
                contentScale = ContentScale.Crop,
                clipToBounds = true,
                alpha = 0.6f,
                modifier = Modifier.clip(RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp)).fillMaxWidth()
            )
        }
    }
}

@Composable
fun CourseActivities(
    navController: NavController,
    windowInfo: WindowInfo,
    width: Dp,
    height: Dp,
    courseId: String
){
    val cardWidth = width * 0.7f
    val columnHeight = height * 0.5f
    val cardHeight = columnHeight * 0.2f
    val iconSize = cardWidth * 0.4f

    val activitiesList = listOf(
        Activities(
            name = R.string.TeacherModules,
            icon = R.drawable.viewallmodules,
            route = Screen.TeacherViewAllModules.createRoute(courseId),
            colors = listOf(shortquiz1,shortquiz2)
        ),
        Activities(
            name = R.string.TeacherTutorials,
            icon = R.drawable.tutorial_icon,
            route = Screen.TeacherTutorials.route,
            colors = listOf(tutorial1,tutorial2)
        ),
        Activities(
            name = R.string.TeacherQuiz,
            icon = R.drawable.shortquiz_icon,
            route = Screen.TeacherCreateQuiz.route,
            colors = listOf(practice1,practice2)
        ),
        Activities(
            name = R.string.TeacherScreeningExam,
            icon = R.drawable.longquiz_icon,
            route = Screen.TeacherCreateScreeningQuiz.route,
            colors = listOf(longquiz1,longquiz2)
        ),
        Activities(
            name = R.string.TeacherStudents,
            icon = R.drawable.practicequiz_icon,
            route = Screen.TeacherViewAllStudents.route,
            colors = listOf(quiz1,quiz2)
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
                            maxLines = 2
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