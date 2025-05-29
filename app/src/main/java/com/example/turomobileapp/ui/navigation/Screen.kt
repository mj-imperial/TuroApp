package com.example.turomobileapp.ui.navigation

import android.net.Uri
import com.example.turomobileapp.enums.ActivityType
import com.example.turomobileapp.enums.QuizType

sealed class Screen(val route: String) {
    //authentication
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangePassword : Screen("change_password/{userId}/{email}/{requiresChange}") {
        fun createRoute(userId: String,email: String,requiresChange: Boolean)
            = "change_password/$userId/$email/$requiresChange"
    }
    object TermsAgreement: Screen("terms_agreement_screen")

    //shared
    object Dashboard: Screen("dashboard_screen")
    object Calendar: Screen("calendar_screen")
    object Profile: Screen("profile_screen")
    object Inbox: Screen("inbox_screen")
    object Notification: Screen("notification_screen")
    object Help: Screen("help_screen")

    //student
    object MiniGames: Screen("mini_games_screen")
    object Leaderboard: Screen("leaderboard_screen")
    object Shop: Screen("shop_screen")
    object StudentCourseDetail: Screen("course_detail/{courseId}/{coursePic}") {
        fun createRoute(courseId: String, coursePic: String): String {
            val encodedPic = Uri.encode(coursePic)
            return "course_detail/$courseId/$encodedPic"
        }
    }
    object StudentCourseActivity: Screen("course_tutorial_screen/{courseId}/{type}"){
        fun createRoute(courseId: String, type: ActivityType) = "course_tutorial_screen/$courseId/${type.name}"
    }
    object StudentCourseQuizzes: Screen("course_quizzes_screen/{courseId}/{type}"){
        fun createRoute(courseId: String, type: QuizType) = "course_quizzes_screen/$courseId/${type.name}"
    }
    object StudentModules: Screen("student_modules_screen")
    object StudentQuizDetail: Screen("quiz_detail/{quizId}"){
        fun createRoute(quizId: String) = "quiz_detail/$quizId"
    }
    object StudentQuizAttempt: Screen("quiz_attempt_screen/{quizId}"){
        fun createRoute(quizId: String) = "quiz_attempt_screen/$quizId"
    }
    object StudentQuizResult: Screen("quiz_result_screen/{quizId}/{fromSubmit}"){
        fun createRoute(quizId: String, fromSubmit: Boolean) = "quiz_result_screen/$quizId/$fromSubmit"
    }

    //teacher
    object TeacherCourseDetail: Screen("course_detail/{courseId}") {
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object TeacherViewAllModules: Screen("teacher_viewAllModules")
    object TeacherTutorials: Screen("teacher_tutorials")
    object TeacherCreateQuiz: Screen("teacher_createQuiz")
    object TeacherCreateScreeningQuiz: Screen("teacher_createScreeningQuiz")
    object TeacherCreateModule: Screen("teacher_createModule")
    object TeacherViewAllStudents: Screen("teacher_viewAllStudents")
}