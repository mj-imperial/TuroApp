package com.example.turomobileapp.ui.screens.student

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.turomobileapp.R
import com.example.turomobileapp.enums.QuizType
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.longquiz1
import com.example.turomobileapp.ui.theme.longquiz2
import com.example.turomobileapp.ui.theme.practice1
import com.example.turomobileapp.ui.theme.practice2
import com.example.turomobileapp.ui.theme.screeningExam1
import com.example.turomobileapp.ui.theme.screeningExam2
import com.example.turomobileapp.ui.theme.shortquiz1
import com.example.turomobileapp.ui.theme.shortquiz2
import com.example.turomobileapp.ui.theme.tutorial1
import com.example.turomobileapp.ui.theme.tutorial2
import com.example.turomobileapp.viewmodels.SessionManager

//TODO current module logic
@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseId: String,
    sessionManager: SessionManager,
    coursePic: String
){
    val windowInfo = rememberWindowInfo()
    val width = windowInfo.screenWidth
    val height = windowInfo.screenHeight

    AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = { navController.navigateUp() },
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
                CourseViewModules(
                    windowInfo = windowInfo,
                    onClickViewAllModules = {
                        navController.navigate(Screen.StudentModules.route)
                    }
                )

                CourseHeader(
                    windowInfo = windowInfo,
                    height = height,
                    onClickCurrentModule = { TODO() },
                    coursePic = coursePic
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
    )
}

@Composable
fun CourseViewModules(
    windowInfo: WindowInfo,
    onClickViewAllModules: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(top = 10.dp,bottom = 2.dp)
            .fillMaxWidth()
            .clickable(onClick = onClickViewAllModules)
    ) {
        Text(
            text = stringResource(R.string.ViewModules),
            fontFamily = FontFamily(Font(R.font.alexandria)),
            fontSize = ResponsiveFont.body(windowInfo),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(end = 5.dp),
            textAlign = TextAlign.End
        )

        Icon(
            painter = painterResource(R.drawable.rightarrow_icon),
            contentDescription = stringResource(R.string.ViewModules),
            tint = TextBlack
        )
    }

    Text(
        text = stringResource(R.string.Continue),
        fontFamily = FontFamily(Font(R.font.alexandria)),
        fontSize = ResponsiveFont.body(windowInfo),
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(start = 10.dp)
    )
}

/*
* TODO add logic for progress percentage and module name placeholder
* */
@Composable
fun CourseHeader(
    windowInfo: WindowInfo,
    height: Dp,
    onClickCurrentModule: () -> Unit,
    coursePic: String
){
    val modulePlaceholderImage = "https://images.pexels.com/photos/27409729/pexels-photo-27409729/free-photo-of-dice-game-on-black-and-white-background.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
    val progressPercentage = "67%"
    val moduleNamePlaceholder = "MODULE 6: STATISTICS & PROBABILITIES"
    val columnHeight = height * 0.20f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(columnHeight)
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClickCurrentModule)
        ){
            AsyncImage(
                model = if (coursePic.isNotEmpty()) coursePic else modulePlaceholderImage,
                contentDescription = "Module Image",
                contentScale = ContentScale.Crop,
                clipToBounds = true,
                alpha = 0.6f,
                modifier = Modifier.clip(RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp)).fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Progress: $progressPercentage",
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alexandria)),
                    color = MainWhite
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = moduleNamePlaceholder,
                        fontSize = ResponsiveFont.heading3(windowInfo),
                        fontFamily = FontFamily(Font(R.font.alexandria)),
                        color = MainWhite,
                        maxLines = 2,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(
                        onClick = onClickCurrentModule,
                        enabled = true,
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
            name = R.string.Tutorials,
            icon = R.drawable.tutorial_floating_icon,
            route = Screen.StudentCourseActivity.route,
            colors = listOf(tutorial1,tutorial2)
        ),
        Activities(
            name = R.string.ShortQuiz,
            icon = R.drawable.shortquiz_icon,
            route = Screen.StudentCourseQuizzes.createRoute(courseId = courseId, type = QuizType.SHORT),
            colors = listOf(shortquiz1,shortquiz2)
        ),
        Activities(
            name = R.string.PracticeQuiz,
            icon = R.drawable.practicequiz_icon,
            route = Screen.StudentCourseQuizzes.createRoute(courseId = courseId, type = QuizType.PRACTICE),
            colors = listOf(practice1,practice2)
        ),
        Activities(
            name = R.string.LongQuiz,
            icon = R.drawable.longquiz_icon,
            route = Screen.StudentCourseQuizzes.createRoute(courseId = courseId, type = QuizType.LONG),
            colors = listOf(longquiz1,longquiz2)
        ),
        Activities(
            name = R.string.ScreeningExam,
            icon = R.drawable.screeningexam_icon,
            route = Screen.StudentCourseActivity.createRoute(courseId = courseId, type = QuizType.SCREENING_EXAM),
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

