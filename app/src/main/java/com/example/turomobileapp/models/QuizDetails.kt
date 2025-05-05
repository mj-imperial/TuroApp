package com.example.turoapp.models

import com.example.turoapp.enums.QuestionType
import com.example.turoapp.enums.ScreeningTier
import com.google.gson.annotations.SerializedName
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
    @SerializedName("quiz_id") val quizId: String,
    @SerializedName("score_percentage") val scorePercentage: Double,
    @SerializedName("date_taken") val dateTaken: Date,
    @SerializedName("attempt_number") val attemptNumber: Int,
    @SerializedName("tier_level") val tierLevel: ScreeningTier?,
    @SerializedName("earned_points") val earnedPoints: Int,
    @SerializedName("correct_answers") val correctAnswers: List<Answers>,
    @SerializedName("wrong_answers") val wrongAnswers: List<Answers>
)