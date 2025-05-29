package com.example.turomobileapp.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turomobileapp.enums.UserRole
import com.example.turomobileapp.ui.screens.authentication.ChangePasswordScreen
import com.example.turomobileapp.ui.screens.authentication.LoginScreen
import com.example.turomobileapp.ui.screens.authentication.SplashScreen
import com.example.turomobileapp.ui.screens.authentication.TermsAgreementScreen
import com.example.turomobileapp.ui.screens.shared.CalendarScreen
import com.example.turomobileapp.ui.screens.shared.DashboardScreen
import com.example.turomobileapp.ui.screens.shared.ProfileScreen
import com.example.turomobileapp.ui.screens.student.CourseDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizAttemptScreen
import com.example.turomobileapp.ui.screens.student.QuizDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizListScreen
import com.example.turomobileapp.ui.screens.student.QuizResultScreen
import com.example.turomobileapp.ui.screens.student.StudentModulesScreen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.AssessmentResultViewModel
import com.example.turomobileapp.viewmodels.student.QuizAttemptViewModel
import com.example.turomobileapp.viewmodels.student.QuizDetailViewModel
import com.example.turomobileapp.viewmodels.student.QuizListViewModel
import androidx.compose.runtime.getValue

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val roleState by sessionManager.role.collectAsState(initial = null)
    val roleString = roleState

    val userRole: UserRole? = roleString?.let { runCatching { UserRole.valueOf(it) }.getOrNull() }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        authNavGraph(navController)

        commonNavGraph(navController, sessionManager)

        userRole?.let { role ->
            when (role) {
                UserRole.STUDENT -> studentNavGraph(navController, sessionManager)
                UserRole.TEACHER -> teacherNavGraph(navController, sessionManager)
            }
        }
    }
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
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
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.commonNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    composable(Screen.Dashboard.route) {
        DashboardScreen(navController, sessionManager)
    }
    composable(Screen.Profile.route) {
        ProfileScreen(navController, sessionManager)
    }

    composable(Screen.Calendar.route) {
        CalendarScreen(navController, sessionManager)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.studentNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
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
                navController.navigate(Screen.QuizDetail.createRoute(quiz.quizId))
            }
        )
    }
    composable(
        route = Screen.QuizDetail.route,
        arguments = listOf(
            navArgument("quizId"){ type = NavType.StringType }
        )
    ) {  backStackEntry ->
        val viewModel: QuizDetailViewModel = hiltViewModel()

        QuizDetailScreen(
            viewModel       = viewModel,
            navController   = navController,
            sessionManager  = sessionManager,
            onClickTakeQuiz = { quiz ->
                navController.navigate(Screen.QuizAttempt.createRoute(quiz.quizId))
            }
        )
    }
    composable(
        route = Screen.QuizAttempt.route,
        arguments = listOf(
            navArgument("quizId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments!!.getString("quizId")!!
        val viewModel: QuizAttemptViewModel = hiltViewModel(backStackEntry)

        QuizAttemptScreen(
            navController = navController,
            sessionManager = sessionManager,
            viewModel = viewModel,
            quizId = quizId
        )
    }
    composable(
        route = Screen.QuizResult.route,
        arguments = listOf(
            navArgument("quizId")    { type = NavType.StringType },
            navArgument("fromSubmit"){ type = NavType.BoolType    }
        )
    ) { backStackEntry ->
        val fromSubmit = backStackEntry.arguments?.getBoolean("fromSubmit") ?: false

        val viewModel: AssessmentResultViewModel = hiltViewModel()
        QuizResultScreen(navController, sessionManager, viewModel, fromSubmit)
    }
}

fun NavGraphBuilder.teacherNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {

}