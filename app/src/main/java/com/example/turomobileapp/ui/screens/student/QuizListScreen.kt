package com.example.turomobileapp.ui.screens.student

import AppScaffold
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.turomobileapp.R
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.ui.components.CapsuleButton
import com.example.turomobileapp.ui.components.ResponsiveFont
import com.example.turomobileapp.ui.components.WindowInfo
import com.example.turomobileapp.ui.components.rememberWindowInfo
import com.example.turomobileapp.ui.navigation.Screen
import com.example.turomobileapp.ui.theme.MainWhite
import com.example.turomobileapp.ui.theme.TextBlack
import com.example.turomobileapp.ui.theme.green
import com.example.turomobileapp.ui.theme.quiz1
import com.example.turomobileapp.ui.theme.quiz2
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.QuizListViewModel

@Composable
fun QuizListScreen(
    navController: NavController,
    viewModel: QuizListViewModel = hiltViewModel(),
    sessionManager: SessionManager,
    onClickQuiz: (QuizResponse) -> Unit
){
    val windowInfo = rememberWindowInfo()
    val uiState by viewModel.uiState.collectAsState()
    val quizType = viewModel.quizType

    val filteredQuizzes = uiState.quizList.filter { it.quizTypeName == quizType.toString() }
    val quizzesByModule: Map<String, List<QuizResponse>> = filteredQuizzes.groupBy { it.moduleName }

        AppScaffold(
        navController = navController,
        canNavigateBack = true,
        navigateUp = {
            navController.navigateUp()
        },
        windowInfo = windowInfo,
        sessionManager = sessionManager,
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                QuizListByModule(
                    quizzesByModule = quizzesByModule,
                    quizType = quizType.toString(),
                    windowInfo = windowInfo,
                    navController = navController,
                    onClickQuiz = onClickQuiz
                )
            }
        }
    )
}

@Composable
fun QuizListByModule(
    navController: NavController,
    quizzesByModule: Map<String, List<QuizResponse>>,
    windowInfo: WindowInfo,
    quizType: String,
    onClickQuiz: (QuizResponse) -> Unit
){
    val cardWidth = (windowInfo.screenWidth) * 0.75f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp,vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        quizzesByModule.forEach { moduleName, quizzes ->
            item{
                Text(
                    text = moduleName,
                    fontSize = ResponsiveFont.title(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            item{
                Text(
                    text = "$quizType QUIZ",
                    fontSize = ResponsiveFont.heading1(windowInfo),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(quizzes) {quiz ->
                QuizCard(
                    windowInfo = windowInfo,
                    width = cardWidth,
                    quizName = quiz.quizName,
                    onClickQuiz = { onClickQuiz(quiz) },
                    onClickStatistics = {
                        navController.navigate(Screen.StudentQuizResult.createRoute(quiz.quizId, false))
                    },
                )
            }
        }
    }
}

@Composable
fun QuizCard(
    windowInfo: WindowInfo,
    width: Dp,
    quizName: String,
    onClickQuiz: () -> Unit,
    onClickStatistics: () -> Unit
){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, contentColor = TextBlack),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .border(0.2.dp,TextBlack,RoundedCornerShape(5.dp))
            .width(width)
            .clickable(onClick = onClickQuiz)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors = listOf(quiz1,quiz2)))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = quizName.capitalize(),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = ResponsiveFont.heading3(windowInfo),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CapsuleButton(
                        text = {
                            Text(
                                text = "VIEW QUIZ",
                                fontFamily = FontFamily(Font(R.font.alata)),
                                fontSize = ResponsiveFont.body(windowInfo),
                                color = MainWhite
                            )
                        },
                        onClick = onClickQuiz,
                        roundedCornerShape = 5.dp,
                        buttonElevation = ButtonDefaults.buttonElevation(5.dp),
                        buttonColors = ButtonDefaults.buttonColors(green),
                        enabled = true,
                        contentPadding = PaddingValues(5.dp)
                        //TODO implement enabled only if user has answered the quiz before it after
                    )

                    Text(
                        text = "VIEW HISTORY",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = ResponsiveFont.subtitle(windowInfo),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable(onClick = onClickStatistics)
                    )
                }
            }
        }
    }
}