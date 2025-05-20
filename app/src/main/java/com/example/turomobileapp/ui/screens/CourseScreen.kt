package com.example.turomobileapp.ui.screens

import AppScaffold
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
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
import com.example.turomobileapp.ui.components.CapsuleButton
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

@Composable
fun CourseDetailScreen(
    navController: NavController,
    courseId: String,
    sessionManager: SessionManager
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
                CourseContent(
                    windowInfo = windowInfo,
                    width = width,
                    height = height,
                    navController = navController
                )
            }
        }
    )
}

@Composable
fun CourseContent(
    navController: NavController,
    windowInfo: WindowInfo,
    width: Dp,
    height: Dp
){
    CourseViewModules(
        windowInfo = windowInfo,
    )

    CourseHeader(
        windowInfo = windowInfo,
        height = height,
        width = width
    )

    CourseActivities(
        windowInfo = windowInfo,
        height = height,
        navController = navController,
        width = width
    )
}

@Composable
fun CourseViewModules(
    windowInfo: WindowInfo
){
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .padding(top = 10.dp,bottom = 2.dp)
            .fillMaxWidth()
//            .clickable() TODO add logic
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
    SHOWS CURRENT MODULE STUDENT IS CURRENTLY ON
    TODO add current progress logic
*/
@Composable
fun CourseHeader(
    windowInfo: WindowInfo,
    height: Dp,
    width: Dp
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
//                .clickable() TODO add logic
        ){
            AsyncImage(
                model = modulePlaceholderImage,
                contentDescription = "Module Image",
                contentScale = ContentScale.Crop,
                clipToBounds = true,
                alpha = 0.9f,
                modifier = Modifier.clip(RoundedCornerShape(bottomEnd = 5.dp, bottomStart = 5.dp))
            )

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Progress: ${progressPercentage}",
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
                        softWrap = true
                    )

                    IconButton(
                        onClick = {/*TODO*/},
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
    height: Dp
){
    val cardWidth = width * 0.7f
    val columnHeight = height * 0.5f
    val cardHeight = columnHeight * 0.2f
    val iconSize = cardWidth * 0.4f

    val activitiesList = listOf(
        Activities(
            name = R.string.Tutorials,
            icon = R.drawable.tutorial_icon,
            route = Screen.CourseTutorials.route,
            colors = listOf(tutorial1,tutorial2)
        ),
        Activities(
            name = R.string.ShortQuiz,
            icon = R.drawable.shortquiz_icon,
            route = Screen.CourseShortQuizzes.route,
            colors = listOf(shortquiz1,shortquiz2)
        ),
        Activities(
            name = R.string.PracticeQuiz,
            icon = R.drawable.practicequiz_icon,
            route = Screen.CoursePracticeQuizzes.route,
            colors = listOf(practice1,practice2)
        ),
        Activities(
            name = R.string.LongQuiz,
            icon = R.drawable.longquiz_icon,
            route = Screen.CourseLongQuizzes.route,
            colors = listOf(longquiz1,longquiz2)
        ),
        Activities(
            name = R.string.ScreeningExam,
            icon = R.drawable.screeningexam_icon,
            route = Screen.CourseScreeningQuizzes.route,
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
                    .height(cardHeight),
//                    .clickable() TODO add logic
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

