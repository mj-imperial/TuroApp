package com.example.turomobileapp.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    //authentication
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object ChangePassword : Screen("change_password/{userId}/{email}/{requiresChange}") {
        fun createRoute(userId: String,email: String,requiresChange: Boolean)
            = "change_password/$userId/$email/$requiresChange"
    }
    object ForgotPassword : Screen("forgot_password/{userId}")
    object TermsAgreement : Screen("terms_agreement_screen/{userId}") {
        fun createRoute(userId: String) = "terms_agreement_screen/$userId"
    }

    //shared
    object Dashboard: Screen("dashboard_screen")
    object Calendar: Screen("calendar_screen")
    object Profile: Screen("profile_screen")
    object Inbox: Screen("inbox_screen")
    object CreateMessage: Screen("create_message_screen")
    object CreateMessageSelectCourse: Screen("create_message_select_course_screen")
    object CreateMessageSelectRecipients: Screen("create_message_select_recipient/{courseName}"){
        fun createRoute(courseName: String) = "create_message_select_recipient/$courseName"
    }
    object InboxDetail: Screen("inbox_detail_screen/{inboxId}"){
        fun createRoute(inboxId: String) = "inbox_detail_screen/$inboxId"
    }
    object ReplyScreen: Screen("reply_screen/{inboxId}"){
        fun createRoute(inboxId: String) = "reply_screen/$inboxId"
    }
    object Notification: Screen("notification_screen")
    object Help: Screen("help_screen")

    //student
    object Leaderboard: Screen("leaderboard_screen")
    object Shop: Screen("shop_screen")
    object StudentScreening: Screen("student_screening")
    object StudentCourseDetail: Screen("course_detail/{courseId}") {
        fun createRoute(courseId: String) = "course_detail/$courseId"
    }
    object StudentModules: Screen("student_modules_screen/{courseId}"){
        fun createRoute(courseId: String) = "student_modules_screen/$courseId"
    }
    object StudentModuleActivities: Screen("student_module_activities/{courseId}/{moduleId}"){
        fun createRoute(courseId: String, moduleId: String) = "student_module_activities/$courseId/$moduleId"
    }
    object StudentActivityDetail : Screen("activity_detail/{courseId}/{moduleId}/{activityId}/{activityType}") {
        fun createRoute(courseId: String, moduleId: String, activityId: String, activityType: String) =
            "activity_detail/$courseId/$moduleId/$activityId/$activityType"
    }
    object StudentQuizAttempt: Screen("quiz_attempt_screen/{quizId}"){
        fun createRoute(quizId: String) = "quiz_attempt_screen/$quizId"
    }
    object StudentQuizResult: Screen("quiz_result_screen/{quizId}/{fromSubmit}"){
        fun createRoute(quizId: String, fromSubmit: Boolean) = "quiz_result_screen/$quizId/$fromSubmit"
    }
    object ScreeningExamDetail: Screen("screening_exam_detail_screen/{activityId}"){
        fun createRoute(activityId: String) = "screening_exam_detail_screen/$activityId"
    }
    object StudentCourseAnalytics: Screen("student_course_analytics/{courseId}"){
        fun createRoute(courseId: String) = "student_course_analytics/$courseId"
    }
    object StudentCourseIndividualAnalytics: Screen("student_course_individual_analytics/{courseId}/{moduleId}"){
        fun createRoute(courseId: String, moduleId: String) = "student_course_individual_analytics/$courseId/$moduleId"
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
    object TeacherPerformance: Screen("teacher_performance/{courseId}"){
        fun createRoute(courseId: String) = "teacher_performance/$courseId"
    }
    object TeacherPerformanceIndividual: Screen("teacher_performance_individual/{courseId}/{studentId}"){
        fun createRoute(courseId: String, studentId: String) = "teacher_performance_individual/$courseId/$studentId"
    }
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