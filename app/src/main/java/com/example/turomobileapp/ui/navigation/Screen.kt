package com.example.turomobileapp.ui.navigation

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
    object CourseTutorials: Screen("course_tutorial/{courseId}"){
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object CourseShortQuizzes: Screen("course_shortQuizzes/{courseId}"){
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object CoursePracticeQuizzes: Screen("course_practiceQuizzes/{courseId}"){
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object CourseLongQuizzes: Screen("course_longQuizzes/{courseId}"){
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object CourseScreeningQuizzes: Screen("course_screeningQuizzes/{courseId}"){
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
}