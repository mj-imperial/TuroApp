package com.example.turomobileapp.models

import com.example.turomobileapp.enums.QuestionType
import com.example.turomobileapp.enums.ScreeningTier
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Question(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("question_image") val questionImage: String? = null,
    @SerializedName("question_type") val questionType: QuestionType,
    @SerializedName("options") val options: List<Options>? = null,
    @SerializedName("correct_answer") val correctAnswer: String? = null,
    @SerializedName("score") val score: Int
)

@JsonClass(generateAdapter = true)
data class Options(
    @SerializedName("option_id") val optionId: String,
    @SerializedName("question_id") val questionId: String,
    @SerializedName("option_text") val optionText: String,
    @SerializedName("is_correct") val isCorrect: Boolean,
)

@JsonClass(generateAdapter = true)
data class Answers(
    @SerializedName("question_id") val questionId: String,
    @SerializedName("question_type") val questionType: QuestionType,
    @SerializedName("option_id") val optionId: String?, //if multiple choice
    @SerializedName("selected_answer") val selectedAnswer: String? //option_id or text
)

@JsonClass(generateAdapter = true)
data class AssessmentResult(
    @SerializedName("result_id") val resultId: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("activity_id") val activityId: String,
    @SerializedName("score_percentage") val scorePercentage: Double,
    @SerializedName("date_taken") val dateTaken: Date,
    @SerializedName("attempt_number") val attemptNumber: Int,
    @SerializedName("tier_level") val tierLevel: ScreeningTier?,
    @SerializedName("earned_points") val earnedPoints: Int,
    @SerializedName("correct_answers") val correctAnswers: List<Answers>,
    @SerializedName("wrong_answers") val wrongAnswers: List<Answers>
)

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
    @Json(name = "answers") val answers: List<AnswerUploadRequest>
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
    @Json(name = "question_image") val questionImage: String?,
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