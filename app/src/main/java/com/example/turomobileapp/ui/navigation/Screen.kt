package com.example.turomobileapp.ui.navigation

import android.net.Uri
import com.example.turomobileapp.enums.QuizType

sealed class Screen(val route: String) {
    //authentication
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangePassword : Screen("change_password/{userId}/{email}/{requiresChange}") {
        fun createRoute(userId: String,email: String,requiresChange: Boolean)
            = "change_password/$userId/$email/$requiresChange"
    }
    object ForgotPassword : Screen("forgot_password/{userId}")
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
        fun createRoute(courseId: String,type: QuizType) = "course_tutorial_screen/$courseId/${type.name}"
    }
    object StudentCourseQuizzes: Screen("course_quizzes_screen/{courseId}/{type}"){
        fun createRoute(courseId: String, type: QuizType) = "course_quizzes_screen/$courseId/${type.name}"
    }
    object StudentModules: Screen("student_modules_screen/{courseId}"){
        fun createRoute(courseId: String) = "student_modules_screen/$courseId"
    }
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
    object TeacherCourseDetail: Screen("course_detail/{courseId}/{coursePic}") {
        fun createRoute(courseId: String, coursePic: String): String {
            val encodedPic = Uri.encode(coursePic)
            return "course_detail/$courseId/$encodedPic"
        }
    }
    object TeacherViewAllModules: Screen("teacher_view_all_modules/{courseId}"){
        fun createRoute(courseId: String) = "teacher_view_all_modules/$courseId"
    }
    object TeacherActivityModules: Screen("teacher_activity_modules/{courseId}"){
        fun createRoute(courseId: String) = "teacher_activity_modules/$courseId"
    }
    object TeacherCreateEditActivitiesInModule: Screen("teacher_create_edit_activity_in_module/{moduleId}"){
        fun createRoute(moduleId: String) = "teacher_create_edit_activity_in_module/$moduleId"
    }
    object TeacherCreateModule: Screen("teacher_createModule")
    object TeacherViewAllStudents: Screen("teacher_viewAllStudents")
    object TeacherPerformance: Screen("teacher_performance")
    object TeacherCreateQuiz: Screen("teacher_create_quiz/{moduleId}"){
        fun createRoute(moduleId: String) = "teacher_create_quiz/$moduleId"
    }
    object TeacherCreateTutorial: Screen("teacher_create_tutorial/{moduleId}"){
        fun createRoute(moduleId: String) = "teacher_create_tutorial/$moduleId"
    }
    object TeacherCreateLecture: Screen("teacher_create_lecture/{moduleId}"){
        fun createRoute(moduleId: String) = "teacher_create_lecture/$moduleId"
    }
    object TeacherEditModule: Screen("teacher_edit_module/{courseId}/{moduleId}"){
        fun createRoute(courseId: String, moduleId: String) = "teacher_edit_module/$courseId/$moduleId"
    }
    object TeacherEditTutorial: Screen("teacher_edit_tutorial/{moduleId}/{activityId}"){
        fun createRoute(moduleId: String, activityId: String) = "teacher_edit_tutorial/$moduleId/$activityId"
    }
    object TeacherEditLecture: Screen("teacher_edit_lecture/{moduleId}/{activityId}"){
        fun createRoute(moduleId: String, activityId: String) = "teacher_edit_lecture/$moduleId/$activityId"
    }
    object TeacherEditQuiz: Screen("teacher_edit_quiz/{moduleId}/{activityId}"){
        fun createRoute(activityId: String, moduleId: String) = "teacher_edit_quiz/$moduleId/$activityId"
    }
}