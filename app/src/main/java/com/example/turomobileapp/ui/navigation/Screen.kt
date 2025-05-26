package com.example.turomobileapp.ui.navigation

import com.example.turomobileapp.enums.ActivityType
import com.example.turomobileapp.enums.QuizType

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangePassword: Screen("change_password_screen")
    object TermsAgreement: Screen("terms_agreement_screen")
    object Dashboard: Screen("dashboard_screen")
    object Calendar: Screen("calendar_screen")
    object MiniGames: Screen("mini_games_screen")
    object Leaderboard: Screen("leaderboard_screen")
    object Inbox: Screen("inbox_screen")
    object Notification: Screen("notification_screen")
    object Profile: Screen("profile_screen")
    object Help: Screen("help_screen")
    object Shop: Screen("shop_screen")
    object CourseDetail: Screen("course_detail/{courseId}") {
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object CourseActivity: Screen("course_tutorial_screen/{courseId}/{type}"){
        fun createRoute(courseId: String, type: ActivityType) = "course_tutorial_screen/$courseId/${type.name}"
    }
    object CourseQuizzes: Screen("course_quizzes_screen/{courseId}/{type}"){
        fun createRoute(courseId: String, type: QuizType) = "course_quizzes_screen/$courseId/${type.name}"
    }
    object StudentModules: Screen("student_modules_screen")
    object QuizDetail: Screen("quiz_detail_screen")
    object QuizAttempt: Screen("quiz_attempt_screen/{quizId}"){
        fun createRoute(quizId: String) = "quiz_attempt_screen/$quizId"
    }
    object QuizResult: Screen("quiz_result_screen/{quizId}"){
        fun createRoute(quizId: String) = "quiz_result_screen/$quizId"
    }
}