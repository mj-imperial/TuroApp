package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class AssessmentResultsResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "results") val results: List<AssessmentResultResponse>
)

@JsonClass(generateAdapter = true)
data class AssessmentResultResponse(
    @Json(name = "result_id") val resultId: String,
    @Json(name = "module_id") val moduleId: String,
    @Json(name = "score_percentage") val scorePercentage: Double,
    @Json(name = "date_taken") val dateTaken: String,
    @Json(name = "attempt_number") val attemptNumber: Int,
    @Json(name = "tier_name") val tierName: String?,
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "answers") val answers: List<AnswerUploadRequest>,
    @Json(name = "is_kept") val isKeptInt: Int
){
    val isKept: Boolean get() = isKeptInt != 0
}

@JsonClass(generateAdapter = true)
data class AssessmentScoresResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "scores") val scores: List<AssessmentScoreResponse>
)

@JsonClass(generateAdapter = true)
data class AssessmentScoreResponse(
    @Json(name = "result_id") val resultId: String,
    @Json(name = "attempt_number") val attemptNumber: Int,
    @Json(name = "score_percentage") val scorePercentage: Double
)

@JsonClass(generateAdapter = true)
data class AssessmentResultUploadResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class AssessmentResultUploadRequest(
    @Json(name = "student_id") val studentId: String,
    @Json(name = "activity_id") val activityId: String,
    @Json(name = "score_percentage") val scorePercentage: Double,
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "answers") val answers: List<AnswerUploadRequest>,
    @Json(name= "tier_level_id") val tierLevelId: Int? = null
)

@JsonClass(generateAdapter = true)
data class AnswerUploadRequest(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "option_id") val optionId: String,
    @Json(name = "is_correct") val isCorrect: Boolean
)

@JsonClass(generateAdapter = true)
data class QuizContentResponses(
    @Json(name = "success") val success: Boolean,
    @Json(name = "questions") val questions: List<QuizContentResponse>
)

@JsonClass(generateAdapter = true)
data class QuizContentResponse(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "question_text") val questionText: String,
    @Json(name = "type_name") val questionTypeName: String,
    @Json(name = "score") val score: Int,
    @Json(name = "options") val options: List<QuestionResponse>
)

@JsonClass(generateAdapter = true)
data class QuestionResponse(
    @Json(name = "option_id") val optionId: String,
    @Json(name = "option_text") val optionText: String,
    @Json(name = "is_correct") val isCorrectInt: Int
){
    val isCorrect: Boolean get() = isCorrectInt != 0
}

@JsonClass(generateAdapter = true)
data class QuizUploadResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
)

@JsonClass(generateAdapter = true)
data class CreateQuizRequest(
    @Json(name = "activity_name") val quizTitle: String,
    @Json(name = "activity_description") val quizDescription: String,
    @Json(name = "unlock_date") val unlockDateTime: LocalDateTime?,
    @Json(name = "deadline_date") val deadlineDateTime: LocalDateTime?,
    @Json(name = "number_of_attempts") val numberOfAttempts: Int,
    @Json(name = "quiz_type_name") val quizType: String,
    @Json(name = "time_limit") val timeLimitSeconds: Int,
    @Json(name = "has_answers_shown") val hasAnswerShown: Boolean,
    @Json(name = "number_of_questions") val numberOfQuestions: Int,
    @Json(name = "overall_points") val overallPoints: Int,
    @Json(name = "questions") val questions: List<CreateQuizQuestions>
)

@JsonClass(generateAdapter = true)
data class CreateQuizQuestions(
    @Json(name = "question_id") val questionId: String? = null,
    @Json(name = "question_text") val questionText: String = "",
    @Json(name = "type_name") val questionType: String = "",
    @Json(name = "score") val score: Int = 0,
    @Json(name = "options") val options: List<CreateQuizOptions>
)

@JsonClass(generateAdapter = true)
data class CreateQuizOptions(
    @Json(name = "option_id") val optionId: String? = null,
    @Json(name = "option_text") val optionText: String = "",
    @Json(name = "is_correct") val isCorrect: Boolean = false
)