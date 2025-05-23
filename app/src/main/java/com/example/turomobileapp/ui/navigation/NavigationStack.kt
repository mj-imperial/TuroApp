package com.example.turomobileapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turomobileapp.models.QuizResponse
import com.example.turomobileapp.ui.screens.authentication.ChangePasswordScreen
import com.example.turomobileapp.ui.screens.authentication.LoginScreen
import com.example.turomobileapp.ui.screens.authentication.SplashScreen
import com.example.turomobileapp.ui.screens.authentication.TermsAgreementScreen
import com.example.turomobileapp.ui.screens.shared.CalendarScreen
import com.example.turomobileapp.ui.screens.shared.DashboardScreen
import com.example.turomobileapp.ui.screens.shared.ProfileScreen
import com.example.turomobileapp.ui.screens.shared.StudentModulesScreen
import com.example.turomobileapp.ui.screens.student.CourseDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizListScreen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.QuizListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(navController)
        }
        composable(Screen.TermsAgreement.route) {
            TermsAgreementScreen(navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController, sessionManager)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, sessionManager)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(navController, sessionManager)
        }
        composable(
            route = Screen.CourseDetail.route
        ) {backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            CourseDetailScreen(navController,courseId.toString(), sessionManager)
        }
        composable(Screen.StudentModules.route) {
            StudentModulesScreen(navController, sessionManager)
        }
        composable(
            route = Screen.CourseQuizzes.route,
            arguments = listOf(
                navArgument(name = "courseId") { type = NavType.StringType },
                navArgument(name = "type") { type = NavType.StringType }
            )
        ) {backStackEntry ->
            val viewModel: QuizListViewModel = hiltViewModel()
            QuizListScreen(
                navController = navController,
                viewModel = viewModel,
                sessionManager = sessionManager,
                onClickQuiz = { quiz ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("quizResponse", quiz)
                    navController.navigate(Screen.QuizDetail.route)
                }
            )
        }
        composable(
            route = Screen.QuizDetail.route,
        ) {  backStackEntry ->
            val quiz: QuizResponse = remember {
                navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<QuizResponse>("quizResponse")
                    ?: error("No QuizResponse passed!")
            }

            LaunchedEffect(Unit) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.remove<QuizResponse>("quizResponse")
            }

            QuizDetailScreen(navController, sessionManager, quiz)
        }
    }
}