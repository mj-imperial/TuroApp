package com.example.turomobileapp.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.turomobileapp.ui.screens.shared.CreateMessageScreen
import com.example.turomobileapp.ui.screens.shared.CreateMessageSelectCourseScreen
import com.example.turomobileapp.ui.screens.shared.CreateMessageSelectRecipients
import com.example.turomobileapp.ui.screens.shared.DashboardScreen
import com.example.turomobileapp.ui.screens.shared.InboxDetailScreen
import com.example.turomobileapp.ui.screens.shared.InboxScreen
import com.example.turomobileapp.ui.screens.shared.NotificationScreen
import com.example.turomobileapp.ui.screens.shared.ReplyScreen
import com.example.turomobileapp.ui.screens.student.CourseDetailScreen
import com.example.turomobileapp.ui.screens.student.LeaderboardScreen
import com.example.turomobileapp.ui.screens.student.LectureDetailScreen
import com.example.turomobileapp.ui.screens.student.LongQuizDetailScreen
import com.example.turomobileapp.ui.screens.student.LongQuizListScreen
import com.example.turomobileapp.ui.screens.student.LongQuizResultScreen
import com.example.turomobileapp.ui.screens.student.LongquizAttemptScreen
import com.example.turomobileapp.ui.screens.student.QuizAttemptScreen
import com.example.turomobileapp.ui.screens.student.QuizDetailScreen
import com.example.turomobileapp.ui.screens.student.QuizResultScreen
import com.example.turomobileapp.ui.screens.student.ScreeningExamAttemptScreen
import com.example.turomobileapp.ui.screens.student.ScreeningExamDetailScreen
import com.example.turomobileapp.ui.screens.student.ScreeningExamLearningResourcesScreen
import com.example.turomobileapp.ui.screens.student.ScreeningExamListScreen
import com.example.turomobileapp.ui.screens.student.ScreeningExamResultScreen
import com.example.turomobileapp.ui.screens.student.StudentCourseAnalyticsScreen
import com.example.turomobileapp.ui.screens.student.StudentModuleActivitiesScreen
import com.example.turomobileapp.ui.screens.student.StudentModulesScreen
import com.example.turomobileapp.ui.screens.student.StudentProfileScreen
import com.example.turomobileapp.ui.screens.student.TutorialDetailScreen
import com.example.turomobileapp.ui.screens.teacher.CreateEditActivitiesInModuleScreen
import com.example.turomobileapp.ui.screens.teacher.CreateLectureScreen
import com.example.turomobileapp.ui.screens.teacher.CreateModuleScreen
import com.example.turomobileapp.ui.screens.teacher.CreateQuizScreen
import com.example.turomobileapp.ui.screens.teacher.CreateTutorialScreen
import com.example.turomobileapp.ui.screens.teacher.EditLectureScreen
import com.example.turomobileapp.ui.screens.teacher.EditModuleScreen
import com.example.turomobileapp.ui.screens.teacher.EditQuizScreen
import com.example.turomobileapp.ui.screens.teacher.EditTutorialScreen
import com.example.turomobileapp.ui.screens.teacher.ModuleFoldersScreen
import com.example.turomobileapp.ui.screens.teacher.StudentIndividualPerformanceScreen
import com.example.turomobileapp.ui.screens.teacher.StudentPerformanceOverViewScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherCourseScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherLectureDetailScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherProfileScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherQuizDetailScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherTutorialDetailScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherViewAllActivitiesScreen
import com.example.turomobileapp.ui.screens.teacher.TeacherViewAllModulesScreen
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.authentication.AgreementTermsViewModel
import com.example.turomobileapp.viewmodels.shared.CreateMessageViewModel
import com.example.turomobileapp.viewmodels.shared.InboxDetailViewModel
import com.example.turomobileapp.viewmodels.shared.InboxViewModel
import com.example.turomobileapp.viewmodels.student.ActivityFlowViewModel
import com.example.turomobileapp.viewmodels.student.AssessmentResultViewModel
import com.example.turomobileapp.viewmodels.student.LectureDetailViewModel
import com.example.turomobileapp.viewmodels.student.LongQuizAttemptViewModel
import com.example.turomobileapp.viewmodels.student.LongQuizDetailViewModel
import com.example.turomobileapp.viewmodels.student.LongQuizListViewModel
import com.example.turomobileapp.viewmodels.student.LongQuizResultViewModel
import com.example.turomobileapp.viewmodels.student.QuizAttemptViewModel
import com.example.turomobileapp.viewmodels.student.QuizDetailViewModel
import com.example.turomobileapp.viewmodels.student.ScreeningExamAttemptViewModel
import com.example.turomobileapp.viewmodels.student.ScreeningExamDetailViewModel
import com.example.turomobileapp.viewmodels.student.ScreeningExamListViewModel
import com.example.turomobileapp.viewmodels.student.ScreeningExamResultViewModel
import com.example.turomobileapp.viewmodels.student.StudentCourseAnalyticsViewModel
import com.example.turomobileapp.viewmodels.student.TutorialDetailViewModel
import com.example.turomobileapp.viewmodels.student.ViewAllModulesViewModel
import com.example.turomobileapp.viewmodels.teacher.ActivityActionsViewModel
import com.example.turomobileapp.viewmodels.teacher.CreateLectureViewModel
import com.example.turomobileapp.viewmodels.teacher.CreateModuleViewModel
import com.example.turomobileapp.viewmodels.teacher.CreateQuizViewModel
import com.example.turomobileapp.viewmodels.teacher.CreateTutorialViewModel
import com.example.turomobileapp.viewmodels.teacher.EditLectureViewModel
import com.example.turomobileapp.viewmodels.teacher.EditModuleViewModel
import com.example.turomobileapp.viewmodels.teacher.EditQuizViewModel
import com.example.turomobileapp.viewmodels.teacher.EditTutorialViewModel
import com.example.turomobileapp.viewmodels.teacher.ModuleListActivityActionsViewModel
import com.example.turomobileapp.viewmodels.teacher.StudentPerformanceViewModel
import com.example.turomobileapp.viewmodels.teacher.TeacherCourseViewModel
import com.example.turomobileapp.viewmodels.teacher.TeacherQuizViewModel
import com.example.turomobileapp.viewmodels.teacher.TeacherViewAllModulesViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavigationStack(
    modifier: Modifier = Modifier,
    sessionManager: SessionManager
) {
    val roleStr by sessionManager.role.collectAsState(initial = null)
    val agreedToTerms by sessionManager.agreedToTerms.collectAsState(initial = false)
    val userId by sessionManager.userId.collectAsState()
    val role = roleStr?.let { UserRole.valueOf(it) }

    when {
        role == null -> {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.Splash.route) {
                authNavGraph(navController)
            }
        }
        !agreedToTerms!! -> {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.TermsAgreement.createRoute(userId.toString())) {
                authNavGraph(navController)
            }
        }
        role == UserRole.STUDENT && agreedToTerms == true -> {
            Log.d("NavDebug", "Showing Student Dashboard NavHost")
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.Dashboard.route) {
                commonNavGraph(role, navController, sessionManager)
                studentNavGraph(role, navController, sessionManager)
            }
        }
        role == UserRole.TEACHER && agreedToTerms == true -> {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.Dashboard.route) {
                commonNavGraph(role, navController, sessionManager)
                teacherNavGraph(role, navController, sessionManager)
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
    composable(
        route = Screen.TermsAgreement.route,
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType }
        )
    ) {
        val viewModel: AgreementTermsViewModel = hiltViewModel()
        TermsAgreementScreen(navController, viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.commonNavGraph(
    role: UserRole,
    navController: NavHostController,
    sessionManager: SessionManager
) {
    composable(Screen.Profile.route) {
        if (role == UserRole.STUDENT){
            StudentProfileScreen(navController, sessionManager)
        }else{
            TeacherProfileScreen(navController, sessionManager)
        }
    }
    composable(
        route = Screen.Calendar.route
    ) {
        CalendarScreen(navController, sessionManager)
    }
    composable(Screen.Dashboard.route) {
        DashboardScreen(navController, sessionManager)
    }
    composable(Screen.Notification.route) {
        NotificationScreen(navController, sessionManager)
    }
    composable(Screen.Inbox.route) {
        val viewModel: InboxViewModel = hiltViewModel()
        InboxScreen(navController, sessionManager, viewModel)
    }
    composable(Screen.CreateMessage.route) {navBackStackEntry ->
        val viewModel: CreateMessageViewModel = hiltViewModel(navBackStackEntry)
        CreateMessageScreen(navController, sessionManager, viewModel)
    }
    composable(Screen.CreateMessageSelectCourse.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("create_message_screen")
        }
        val viewModel: CreateMessageViewModel = hiltViewModel(parentEntry)
        CreateMessageSelectCourseScreen(navController, sessionManager, viewModel)
    }
    composable(
        route = Screen.CreateMessageSelectRecipients.route,
        arguments = listOf(
            navArgument("courseName") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("create_message_screen")
        }
        val viewModel: CreateMessageViewModel = hiltViewModel(parentEntry)
        val courseName = backStackEntry.arguments?.getString("courseName")
        CreateMessageSelectRecipients(navController, sessionManager, viewModel, courseName.toString())
    }
    composable(
        route = Screen.InboxDetail.route,
        arguments = listOf(
            navArgument("inboxId") { type = NavType.StringType }
        )
    ) { navBackStackEntry ->
        val inboxId = navBackStackEntry.arguments?.getString("inboxId")!!
        val viewModel: InboxDetailViewModel = hiltViewModel(navBackStackEntry)

        InboxDetailScreen(navController, sessionManager, viewModel, inboxId.toString())
    }
    composable(
        route = Screen.ReplyScreen.route,
        arguments = listOf(
            navArgument("inboxId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val inboxId = backStackEntry.arguments?.getString("inboxId")!!
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("inbox_detail_screen/$inboxId")
        }
        val viewModel: InboxDetailViewModel = hiltViewModel(parentEntry)
        ReplyScreen(navController, sessionManager, viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.studentNavGraph(
    role: UserRole,
    navController: NavHostController,
    sessionManager: SessionManager
) {
    commonNavGraph(role, navController, sessionManager)

    composable(
        route = Screen.StudentCourseDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val viewModel: ViewAllModulesViewModel = hiltViewModel(backStackEntry)
        CourseDetailScreen(navController,courseId.toString(), sessionManager,viewModel)
    }
    composable(
        route = Screen.StudentModules.route,
        arguments = listOf(
            navArgument(name = "courseId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("course_detail/$courseId")
        }
        val viewModel: ViewAllModulesViewModel = hiltViewModel(parentEntry)

        StudentModulesScreen(navController, sessionManager, viewModel, courseId.toString())
    }

    composable(
        route = Screen.StudentModuleActivities.route,
        arguments = listOf(
            navArgument(name = "courseId") { type = NavType.StringType },
            navArgument(name = "moduleId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val moduleId = backStackEntry.arguments?.getString("moduleId")!!
        val activityFlowViewModel: ActivityFlowViewModel = hiltViewModel(backStackEntry)
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("course_detail/$courseId")
        }
        val viewModel: ViewAllModulesViewModel = hiltViewModel(parentEntry)
        StudentModuleActivitiesScreen(navController, sessionManager, viewModel, courseId, moduleId, activityFlowViewModel)
    }
    composable(
        route = Screen.StudentActivityDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("activityId") { type = NavType.StringType },
            navArgument("activityType") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val moduleId = backStackEntry.arguments?.getString("moduleId")!!
        val activityId = backStackEntry.arguments?.getString("activityId")!!
        val activityType = backStackEntry.arguments?.getString("activityType")!!

        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("student_module_activities/$courseId/$moduleId")
        }
        val activityFlowViewModel: ActivityFlowViewModel = hiltViewModel(parentEntry)

        when(activityType.toUpperCase()){
            "LECTURE" -> {
                val viewModel: LectureDetailViewModel = hiltViewModel()
                LectureDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager,
                    activityId = activityId,
                    courseId = courseId,
                    activityFlowViewModel = activityFlowViewModel
                )
            }
            "TUTORIAL" -> {
                val viewModel: TutorialDetailViewModel = hiltViewModel()
                TutorialDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager,
                    activityId = activityId,
                    courseId = courseId,
                    activityFlowViewModel = activityFlowViewModel
                )
            }
            "QUIZ" -> {
                val viewModel: QuizDetailViewModel = hiltViewModel()
                QuizDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager,
                    onClickTakeQuiz = { quiz ->
                        navController.navigate(Screen.StudentQuizAttempt.createRoute(moduleId, quiz.quizId))
                    },
                    activityId = activityId,
                    courseId = courseId,
                    activityFlowViewModel = activityFlowViewModel,
                    moduleId = moduleId
                )
            }
        }
    }
    composable(
        route = Screen.StudentQuizAttempt.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("quizId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments!!.getString("quizId")!!
        val moduleId = backStackEntry.arguments?.getString("moduleId")!!
        val viewModel: QuizAttemptViewModel = hiltViewModel(backStackEntry)

        QuizAttemptScreen(
            navController = navController,
            sessionManager = sessionManager,
            viewModel = viewModel,
            quizId = quizId,
            moduleId = moduleId
        )
    }
    composable(
        route = Screen.StudentQuizResult.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("quizId") { type = NavType.StringType },
            navArgument("fromSubmit"){ type = NavType.BoolType }
        )
    ) { backStackEntry ->
        val quizId = backStackEntry.arguments!!.getString("quizId")!!
        val moduleId = backStackEntry.arguments?.getString("moduleId")!!
        val fromSubmit = backStackEntry.arguments?.getBoolean("fromSubmit") ?: false

        val viewModel: AssessmentResultViewModel = hiltViewModel()
        QuizResultScreen(navController, sessionManager, viewModel, fromSubmit, quizId, moduleId)
    }

    composable(Screen.Leaderboard.route) {
        LeaderboardScreen(navController, sessionManager)
    }
    composable(
        route = Screen.StudentCourseAnalytics.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) { navBackStackEntry ->
        val courseId = navBackStackEntry.arguments?.getString("courseId")!!
        val viewModel: StudentCourseAnalyticsViewModel = hiltViewModel(navBackStackEntry)

        StudentCourseAnalyticsScreen(navController, sessionManager, viewModel, courseId)
    }
    composable(
        route = Screen.StudentLongQuizList.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val viewModel: LongQuizListViewModel = hiltViewModel()
        LongQuizListScreen(navController, sessionManager, viewModel, courseId)
    }
    composable(
        route = Screen.StudentLongQuizDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("longQuizId") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val longQuizId = backStackEntry.arguments?.getString("longQuizId")!!

        val viewModel: LongQuizDetailViewModel = hiltViewModel()
        LongQuizDetailScreen(navController, sessionManager, viewModel, courseId, longQuizId)
    }
    composable(
        route = Screen.StudentLongQuizAttempt.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("longQuizId") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val longQuizId = backStackEntry.arguments?.getString("longQuizId")!!

        val viewModel: LongQuizAttemptViewModel = hiltViewModel()
        LongquizAttemptScreen(navController, sessionManager, viewModel, courseId, longQuizId)
    }
    composable(
        route = Screen.StudentLongQuizResult.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("longQuizId") { type = NavType.StringType },
            navArgument("fromSubmit"){ type = NavType.BoolType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val longQuizId = backStackEntry.arguments?.getString("longQuizId")!!
        val fromSubmit = backStackEntry.arguments?.getBoolean("fromSubmit")!!

        val viewModel: LongQuizResultViewModel = hiltViewModel()
        LongQuizResultScreen(navController, sessionManager, viewModel, courseId, longQuizId, fromSubmit)
    }

    composable(
        route = Screen.StudentScreeningList.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val viewModel: ScreeningExamListViewModel = hiltViewModel()
        ScreeningExamListScreen(navController, sessionManager, viewModel)
    }
    composable(
        route = Screen.ScreeningExamDetail.route,
        arguments = listOf(
            navArgument("screeningExamId") { type = NavType.StringType },
        )
    ) {backStackEntry ->
        val screeningExamId = backStackEntry.arguments?.getString("screeningExamId")!!
        val viewModel: ScreeningExamDetailViewModel = hiltViewModel()
        ScreeningExamDetailScreen(navController, sessionManager, viewModel, screeningExamId)
    }
    composable(
        route = Screen.ScreeningExamAttempt.route,
        arguments = listOf(
            navArgument("screeningExamId") { type = NavType.StringType },
        )
    ) { backStackEntry ->
        val screeningExamId = backStackEntry.arguments?.getString("screeningExamId")!!
        val viewModel: ScreeningExamAttemptViewModel = hiltViewModel()
        ScreeningExamAttemptScreen(navController, sessionManager, viewModel, screeningExamId)
    }
    composable(
        route = Screen.ScreeningExamResult.route,
        arguments = listOf(
            navArgument("screeningExamId") { type = NavType.StringType },
        )
    ) {navBackStackEntry ->
        val screeningExamId = navBackStackEntry.arguments?.getString("screeningExamId")!!
        val viewModel: ScreeningExamResultViewModel = hiltViewModel(navBackStackEntry)

        ScreeningExamResultScreen(navController, sessionManager, viewModel, screeningExamId)
    }
    composable(
        route = "screening_learning_resources/{screeningExamId}/{conceptId}",
        arguments = listOf(
            navArgument("screeningExamId") { type = NavType.StringType },
            navArgument("conceptId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val screeningExamId = backStackEntry.arguments?.getString("screeningExamId")!!
        val conceptId = backStackEntry.arguments?.getString("conceptId")!!
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("screening_exam_result/$screeningExamId")
        }
        val viewModel: ScreeningExamResultViewModel = hiltViewModel(parentEntry)
        ScreeningExamLearningResourcesScreen(navController, sessionManager, viewModel, conceptId, null)
    }
    composable(
        route = "screening_learning_resources/{screeningExamId}/{conceptId}/{topicId}",
        arguments = listOf(
            navArgument("screeningExamId") { type = NavType.StringType },
            navArgument("conceptId") { type = NavType.StringType },
            navArgument("topicId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        )
    ) { backStackEntry ->
        val screeningExamId = backStackEntry.arguments?.getString("screeningExamId")!!
        val conceptId = backStackEntry.arguments?.getString("conceptId")!!
        val topicId = backStackEntry.arguments?.getString("topicId")
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("screening_exam_result/$screeningExamId")
        }
        val viewModel: ScreeningExamResultViewModel = hiltViewModel(parentEntry)
        ScreeningExamLearningResourcesScreen(navController, sessionManager, viewModel, conceptId,
            topicId.toString()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.teacherNavGraph(
    role: UserRole,
    navController: NavHostController,
    sessionManager: SessionManager
) {
    commonNavGraph(role, navController, sessionManager)

    composable(
        route = Screen.TeacherCourseDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: TeacherCourseViewModel = hiltViewModel()
        TeacherCourseScreen(navController,courseId.toString(), sessionManager, viewModel,
            sectionId.toString()
        )
    }

    composable(
        route = Screen.TeacherCreateModule.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) {
        val viewModel: CreateModuleViewModel = hiltViewModel()
        CreateModuleScreen(navController, sessionManager, viewModel)
    }

    composable(
        route = Screen.TeacherActivityModules.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: ModuleListActivityActionsViewModel = hiltViewModel()
        ModuleFoldersScreen(navController, sessionManager, viewModel, courseId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherCreateEditActivitiesInModule.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: ActivityActionsViewModel = hiltViewModel()
        CreateEditActivitiesInModuleScreen(navController, sessionManager, viewModel, moduleId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherCreateQuiz.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: CreateQuizViewModel = hiltViewModel()
        CreateQuizScreen(navController, sessionManager, viewModel, moduleId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherCreateTutorial.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: CreateTutorialViewModel = hiltViewModel()

        CreateTutorialScreen(navController, sessionManager, moduleId.toString(), viewModel, sectionId.toString())
    }

    composable(
        route = Screen.TeacherCreateLecture.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: CreateLectureViewModel = hiltViewModel()

        CreateLectureScreen(navController, sessionManager, moduleId.toString(), viewModel, sectionId.toString())
    }

    composable(
        route = Screen.TeacherEditModule.route,
        arguments = listOf(
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("courseId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: EditModuleViewModel = hiltViewModel()

        EditModuleScreen(navController, sessionManager, viewModel, courseId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherEditTutorial.route,
        arguments = listOf(
            navArgument("activityId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: EditTutorialViewModel = hiltViewModel()

        EditTutorialScreen(navController, sessionManager, viewModel, moduleId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherEditLecture.route,
        arguments = listOf(
            navArgument("activityId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) {backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: EditLectureViewModel = hiltViewModel()

        EditLectureScreen(navController, sessionManager, viewModel, moduleId.toString(), sectionId.toString())
    }

    composable(
        route = Screen.TeacherEditQuiz.route,
        arguments = listOf(
            navArgument("activityId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val viewModel: EditQuizViewModel = hiltViewModel()

        EditQuizScreen(navController, sessionManager, viewModel, moduleId.toString(), sectionId.toString())
    }
    composable(
        route = Screen.TeacherPerformance.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType }
        )
    ) {navBackStackEntry ->
        val courseId = navBackStackEntry.arguments?.getString("courseId")!!
        val viewModel: StudentPerformanceViewModel = hiltViewModel(navBackStackEntry)
        StudentPerformanceOverViewScreen(navController, sessionManager, viewModel, courseId)
    }
    composable(
        route = Screen.TeacherPerformanceIndividual.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("studentId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")!!
        val studentId = backStackEntry.arguments?.getString("studentId")!!
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("teacher_performance/$courseId")
        }
        val viewModel: StudentPerformanceViewModel = hiltViewModel(parentEntry)
        StudentIndividualPerformanceScreen(navController, sessionManager, viewModel, studentId)
    }
    composable(
        route = Screen.TeacherViewAllModules.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { navBackStackEntry ->
        val sectionId = navBackStackEntry.arguments?.getString("sectionId")
        val courseId = navBackStackEntry.arguments?.getString("courseId")
        val viewModel: TeacherViewAllModulesViewModel = hiltViewModel(navBackStackEntry)
        TeacherViewAllModulesScreen(navController, sessionManager, viewModel, sectionId.toString(), courseId.toString())
    }
    composable(
        route = Screen.TeacherViewAllActivities.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry("teacher_view_all_modules/$courseId/$sectionId")
        }
        val viewModel: TeacherViewAllModulesViewModel = hiltViewModel(parentEntry)

        TeacherViewAllActivitiesScreen(navController, sessionManager, viewModel, sectionId.toString(), moduleId.toString(),
            courseId.toString()
        )
    }
    composable(
        route = Screen.TeacherActivityDetail.route,
        arguments = listOf(
            navArgument("courseId") { type = NavType.StringType },
            navArgument("activityId") { type = NavType.StringType },
            navArgument("activityType") { type = NavType.StringType },
            navArgument("sectionId") { type = NavType.StringType },
            navArgument("moduleId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val activityId = backStackEntry.arguments?.getString("activityId")!!
        val activityType = backStackEntry.arguments?.getString("activityType")!!
        when(activityType.toUpperCase()){
            "LECTURE" -> {
                val viewModel: LectureDetailViewModel = hiltViewModel()
                TeacherLectureDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager
                )
            }
            "TUTORIAL" -> {
                val viewModel: TutorialDetailViewModel = hiltViewModel()
                TeacherTutorialDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager
                )
            }
            "QUIZ" -> {
                val viewModel: TeacherQuizViewModel = hiltViewModel()
                TeacherQuizDetailScreen(
                    viewModel = viewModel,
                    navController = navController,
                    sessionManager = sessionManager,
                    activityId = activityId,
                    courseId = courseId.toString(),
                    moduleId = moduleId.toString()
                )
            }
        }
    }
}