package com.example.turomobileapp.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.turomobileapp.ui.screens.student.LeaderboardScreen
import com.example.turomobileapp.ui.screens.student.QuizAttemptScreen
import com.example.turomobileapp.ui.screens.student.QuizDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizListScreen
import com.example.turomobileapp.ui.screens.student.QuizResultScreen
import com.example.turomobileapp.ui.screens.student.StudentModulesScreen
import com.example.turomobileapp.ui.screens.teacher.CreateModuleScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherCourseScreen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.student.AssessmentResultViewModel
import com.example.turomobileapp.viewmodels.student.QuizAttemptViewModel
import com.example.turomobileapp.viewmodels.student.QuizDetailViewModel
import com.example.turomobileapp.viewmodels.student.QuizListViewModel
import com.example.turomobileapp.viewmodels.teacher.CourseActionsViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val roleStr by sessionManager.role.collectAsState(initial = null)
    val role = roleStr?.let { UserRole.valueOf(it) }

    when (role) {
        null -> AuthNavHost()
        UserRole.STUDENT -> StudentNavHost(sessionManager)
        UserRole.TEACHER -> TeacherNavHost(sessionManager)
    }
}

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Splash.route) {
        authNavGraph(navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudentNavHost(sessionManager: SessionManager) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        commonNavGraph(navController, sessionManager)
        studentNavGraph(navController, sessionManager)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeacherNavHost(sessionManager: SessionManager) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Dashboard.route) {
        commonNavGraph(navController, sessionManager)
        teacherNavGraph(navController, sessionManager)
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
    composable(
        route = Screen.ChangePassword.route,
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
            navArgument("email")  { type = NavType.StringType },
            navArgument("requiresChange") { type = NavType.BoolType }
        )
    ) { backStack ->
        ChangePasswordScreen(navController)
    }
    composable(Screen.ForgotPassword.route) {
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
    composable(Screen.Profile.route) {
        ProfileScreen(navController, sessionManager)
    }
    composable(Screen.Calendar.route) {
        CalendarScreen(navController, sessionManager)
    }
    composable(route = Screen.Dashboard.route) {
        DashboardScreen(navController, sessionManager)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.studentNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    commonNavGraph(navController, sessionManager)

    composable(
        route = Screen.StudentCourseDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("coursePic") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val coursePic = backStackEntry.arguments?.getString("coursePic")
        CourseDetailScreen(navController,courseId.toString(), sessionManager,coursePic.toString())
    }
    composable(Screen.StudentModules.route) {
        StudentModulesScreen(navController, sessionManager)
    }
    composable(
        route = Screen.StudentCourseQuizzes.route,
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
                navController.navigate(Screen.StudentQuizDetail.createRoute(quiz.quizId))
            }
        )
    }
    composable(
        route = Screen.StudentQuizDetail.route,
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
                navController.navigate(Screen.StudentQuizAttempt.createRoute(quiz.quizId))
            }
        )
    }
    composable(
        route = Screen.StudentQuizAttempt.route,
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
        route = Screen.StudentQuizResult.route,
        arguments = listOf(
            navArgument("quizId")    { type = NavType.StringType },
            navArgument("fromSubmit"){ type = NavType.BoolType    }
        )
    ) { backStackEntry ->
        val fromSubmit = backStackEntry.arguments?.getBoolean("fromSubmit") ?: false

        val viewModel: AssessmentResultViewModel = hiltViewModel()
        QuizResultScreen(navController, sessionManager, viewModel, fromSubmit)
    }

    composable(Screen.Leaderboard.route) {
        LeaderboardScreen(navController, sessionManager)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.teacherNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    commonNavGraph(navController, sessionManager)

    composable(
        route = Screen.TeacherCourseDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("coursePic") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val coursePic = backStackEntry.arguments?.getString("coursePic")
        TeacherCourseScreen(navController,courseId.toString(), sessionManager,coursePic.toString())
    }

    composable(
        route = Screen.TeacherViewAllModules.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) {
        val viewModel: CourseActionsViewModel = hiltViewModel()
        CreateModuleScreen(navController, sessionManager, viewModel)
    }
}