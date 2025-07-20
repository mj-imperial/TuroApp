package com.example.turomobileapp.ui.navigation

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
    object StudentScreeningList: Screen("student_screening_list/{courseId}"){
        fun createRoute(courseId: String) = "student_screening_list/$courseId"
    }
    object ScreeningExamDetail: Screen("screening_exam_detail_screen/{screeningExamId}"){
        fun createRoute(screeningExamId: String) = "screening_exam_detail_screen/$screeningExamId"
    }
    object ScreeningExamAttempt: Screen("screening_exam_attempt/{screeningExamId}"){
        fun createRoute(screeningExamId: String) = "screening_exam_attempt/$screeningExamId"
    }
    object ScreeningExamResult: Screen("screening_exam_result/{screeningExamId}"){
        fun createRoute(screeningExamId: String) = "screening_exam_result/$screeningExamId"
    }
    object ScreeningLearningResources : Screen("screening_learning_resources/{screeningExamId}/{conceptId}/{topicId?}") {
        fun createRoute(screeningExamId: String, conceptId: String, topicId: String? = null): String {
            return if (topicId != null)
                "screening_learning_resources/$screeningExamId/$conceptId/$topicId"
            else
                "screening_learning_resources/$screeningExamId/$conceptId"
        }
    }
    object StudentLongQuizList: Screen("student_long_quiz_list/{courseId}"){
        fun createRoute(courseId: String) = "student_long_quiz_list/$courseId"
    }
    object StudentLongQuizDetail: Screen("student_long_quiz_detail/{courseId}/{longQuizId}"){
        fun createRoute(courseId: String, longQuizId: String) = "student_long_quiz_detail/$courseId/$longQuizId"
    }
    object StudentLongQuizAttempt: Screen("student_long_quiz_attempt/{courseId}/{longQuizId}"){
        fun createRoute(courseId: String, longQuizId: String) = "student_long_quiz_attempt/$courseId/$longQuizId"
    }
    object StudentLongQuizResult: Screen("student_long_quiz_result_screen/{courseId}/{longQuizId}/{fromSubmit}"){
        fun createRoute(courseId: String, longQuizId: String, fromSubmit: Boolean) = "student_long_quiz_result_screen/$courseId/$longQuizId/$fromSubmit"
    }
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
    object StudentQuizAttempt: Screen("quiz_attempt_screen/{moduleId}/{quizId}"){
        fun createRoute(moduleId: String, quizId: String) = "quiz_attempt_screen/$moduleId/$quizId"
    }
    object StudentQuizResult: Screen("quiz_result_screen/{moduleId}/{quizId}/{fromSubmit}"){
        fun createRoute(moduleId: String, quizId: String, fromSubmit: Boolean) = "quiz_result_screen/$moduleId/$quizId/$fromSubmit"
    }
    object StudentCourseAnalytics: Screen("student_course_analytics/{courseId}"){
        fun createRoute(courseId: String) = "student_course_analytics/$courseId"
    }

    //teacher
    object TeacherCourseDetail: Screen("course_detail/{courseId}/{sectionId}") {
        fun createRoute(courseId: String, sectionId: String) = "course_detail/$courseId/$sectionId"
    }
    object TeacherActivityModules: Screen("teacher_activity_modules/{courseId}/{sectionId}"){
        fun createRoute(courseId: String, sectionId: String) = "teacher_activity_modules/$courseId/$sectionId"
    }
    object TeacherCreateEditActivitiesInModule: Screen("teacher_create_edit_activity_in_module/{moduleId}/{sectionId}"){
        fun createRoute(moduleId: String, sectionId: String) = "teacher_create_edit_activity_in_module/$moduleId/$sectionId"
    }
    object TeacherCreateModule: Screen("teacher_createModule/{courseId}"){
        fun createRoute(courseId: String) = "teacher_createModule/$courseId"
    }
    object TeacherCreateLongQuiz: Screen("teacher_create_long_quiz")
    object TeacherCreateScreeningExam: Screen("teacher_create_screening_exam")
    object TeacherPerformance: Screen("teacher_performance/{courseId}"){
        fun createRoute(courseId: String) = "teacher_performance/$courseId"
    }
    object TeacherPerformanceIndividual: Screen("teacher_performance_individual/{courseId}/{studentId}"){
        fun createRoute(courseId: String, studentId: String) = "teacher_performance_individual/$courseId/$studentId"
    }
    object TeacherCreateQuiz: Screen("teacher_create_quiz/{sectionId}/{moduleId}"){
        fun createRoute(sectionId: String, moduleId: String) = "teacher_create_quiz/$sectionId/$moduleId"
    }
    object TeacherCreateTutorial: Screen("teacher_create_tutorial/{sectionId}/{moduleId}"){
        fun createRoute(sectionId: String, moduleId: String) = "teacher_create_tutorial/$sectionId/$moduleId"
    }
    object TeacherCreateLecture: Screen("teacher_create_lecture/{sectionId}/{moduleId}"){
        fun createRoute(sectionId: String, moduleId: String) = "teacher_create_lecture/$sectionId/$moduleId"
    }
    object TeacherEditModule: Screen("teacher_edit_module/{courseId}/{moduleId}/{sectionId}"){
        fun createRoute(courseId: String, moduleId: String, sectionId: String) = "teacher_edit_module/$courseId/$moduleId/$sectionId"
    }
    object TeacherEditTutorial: Screen("teacher_edit_tutorial/{sectionId}/{moduleId}/{activityId}"){
        fun createRoute(sectionId: String, moduleId: String, activityId: String) = "teacher_edit_tutorial/$sectionId/$moduleId/$activityId"
    }
    object TeacherEditLecture: Screen("teacher_edit_lecture/{sectionId}/{moduleId}/{activityId}"){
        fun createRoute(sectionId: String, moduleId: String, activityId: String) = "teacher_edit_lecture/$sectionId/$moduleId/$activityId"
    }
    object TeacherEditQuiz: Screen("teacher_edit_quiz/{sectionId}/{moduleId}/{activityId}"){
        fun createRoute(sectionId: String, moduleId: String, activityId: String) = "teacher_edit_quiz/$sectionId/$moduleId/$activityId"
    }
    object TeacherViewAllModules: Screen("teacher_view_all_modules/{courseId}/{sectionId}"){
        fun createRoute(courseId: String, sectionId: String) = "teacher_view_all_modules/$courseId/$sectionId"
    }
    object TeacherViewAllActivities: Screen("teacher_view_all_activities/{courseId}/{sectionId}/{moduleId}"){
        fun createRoute(courseId: String, sectionId: String, moduleId: String) = "teacher_view_all_activities/$courseId/$sectionId/$moduleId"
    }
    object TeacherActivityDetail : Screen("teacher_activity_detail/{courseId}/{sectionId}/{activityId}/{activityType}/{moduleId}") {
        fun createRoute(courseId: String, sectionId: String, activityId: String, activityType: String, moduleId: String) =
            "teacher_activity_detail/$courseId/$sectionId/$activityId/$activityType/$moduleId"
    }
}