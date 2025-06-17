package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StudentLeaderboardResponses(
    @Json(name = "success") val success: Boolean,
    @Json(name = "progresses") val progresses: List<StudentLeaderboardResponse>
)

@JsonClass(generateAdapter = true)
data class StudentLeaderboardResponse(
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "course_name") val courseName: String,
    @Json(name = "profile_pic") val profilePic: String?,
    @Json(name = "total_points") val totalPoints: Int,
    @Json(name = "average_score") val averageScore: Double
)

@JsonClass(generateAdapter = true)
data class StudentProfileProgress(
    @Json(name = "course_name") val courseName: String,
    @Json(name = "total_points") val totalPoints: Int,
    @Json(name = "average_score") val averageScore: Double
)

@JsonClass(generateAdapter = true)
data class StudentPerformanceListResponses(
    @Json(name = "success") val success: Boolean,
    @Json(name = "overall_number_of_assessments") val overallNumberOfAssessments: Int,
    @Json(name = "lowest_assessment_average_quiz_name") val lowestAssessmentAverageQuizName: String,
    @Json(name = "lowest_assessment_average") val lowestAssessmentAverage: Double,
    @Json(name = "highest_assessment_average_quiz_name") val highestAssessmentAverageQuizName: String,
    @Json(name = "highest_assessment_average") val highestAssessmentAverage: Double,
    @Json(name = "lowest_scoring_module_name") val lowestScoringModuleName: String,
    @Json(name = "lowest_scoring_module_average") val lowestScoringModuleAverage: Double,
    @Json(name = "highest_scoring_module_name") val highestScoringModuleName: String,
    @Json(name = "highest_scoring_module_average") val highestScoringModuleAverage: Double,
    @Json(name = "progresses") val progresses: List<StudentPerformanceListResponse>
)

@JsonClass(generateAdapter = true)
data class StudentPerformanceListResponse(
    @Json(name = "student_id") val studentId: String,
    @Json(name = "student_name") val studentName: String,
    @Json(name = "course_name") val courseName: String,
    @Json(name = "profile_pic") val profilePic: String?,
    @Json(name = "completed_assessments") val completedAssessments: Int,
    @Json(name = "total_points") val totalPoints: Int,
    @Json(name = "average_score") val averageScore: Double
)

@JsonClass(generateAdapter = true)
data class StudentPerformanceResponse(
    @Json(name = "overall_number_of_assessments") val overallNumberOfAssessments: Int,
    @Json(name = "completed_assessments") val completedAssessments: Int,
    @Json(name = "total_points") val totalPoints: Int,
    @Json(name = "average_score") val averageScore: Double,
    @Json(name = "lowest_assessment_quiz_name") val lowestAssessmentQuizName: String,
    @Json(name = "lowest_assessment_scorePercentage") val lowestAssessmentScorePercentage: Double,
    @Json(name = "highest_assessment_quiz_name") val highestAssessmentQuizName: String,
    @Json(name = "highest_assessment_scorePercentage") val highestAssessmentScorePercentage: Double,
    @Json(name = "lowest_scoring_module_name") val lowestScoringModuleName: String,
    @Json(name = "lowest_scoring_module_average") val lowestScoringModuleAverage: Double,
    @Json(name = "highest_scoring_module_name") val highestScoringModuleName: String,
    @Json(name = "highest_scoring_module_average") val highestScoringModuleAverage: Double,
)

@JsonClass(generateAdapter = true)
data class IndividualStudentList(
    @Json(name = "success") val success: Boolean,
    @Json(name = "modules") val modules: List<StudentPerformanceModuleList>
)

@JsonClass(generateAdapter = true)
data class StudentPerformanceModuleList(
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "module_name") val moduleName: String,
    @Json(name = "quiz_scores") val quizScores: List<QuizScore>
)

@JsonClass(generateAdapter = true)
data class QuizScore(
    @Json(name = "activity_id") val activityId: String,
    @Json(name = "activity_name") val activityName: String,
    @Json(name = "highest_score_percentage") val highestScorePercentage: Double,
    @Json(name = "lowest_score_percentage") val lowestScorePercentage: Double,
    @Json(name = "latest_score_percentage") val latestScorePercentage: Double
)