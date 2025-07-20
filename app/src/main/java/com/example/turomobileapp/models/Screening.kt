package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ScreeningExamsListResponse(
    @Json(name = "data") val data: List<ScreeningExamListResponse>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamListResponse(
    @Json(name = "screening_id") val screeningId: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "screening_name") val screeningName: String,
    @Json(name = "screening_image") val screeningImage: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ScreeningExamListResponse

        if (screeningId!=other.screeningId) return false
        if (courseId!=other.courseId) return false
        if (screeningName!=other.screeningName) return false
        if (!screeningImage.contentEquals(other.screeningImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = screeningId.hashCode()
        result = 31 * result + courseId.hashCode()
        result = 31 * result + screeningName.hashCode()
        result = 31 * result + screeningImage.contentHashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ScreeningExamResponse(
    @Json(name = "screening_id") val screeningId: String,
    @Json(name = "course_id") val courseId: String,
    @Json(name = "screening_name") val screeningName: String,
    @Json(name = "screening_instructions") val screeningInstructions: String,
    @Json(name = "time_limit") val timeLimit: Int,
    @Json(name = "number_of_questions") val numberOfQuestions: Int
)

@JsonClass(generateAdapter = true)
data class ScreeningExamContents(
    @Json(name = "concepts") val data: List<ScreeningExamContent>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamContent(
    @Json(name = "concept_id") val conceptId: String,
    @Json(name = "concept_name") val conceptName: String,
    @Json(name = "passing") val passing: Int,
    @Json(name = "topics") val topics: List<ScreeningExamTopic>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamTopic(
    @Json(name = "topic_id") val topicId: String,
    @Json(name = "topic_name") val topicName: String,
    @Json(name = "questions") val questions: List<ScreeningExamQuestion>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamQuestion(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "question_text") val questionText: String,
    @Json(name = "question_image") val questionImage: ByteArray?,
    @Json(name = "type_name") val typeName: String,
    @Json(name = "score") val score: Int,
    @Json(name = "options") val options: List<ScreeningExamOption>
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ScreeningExamQuestion

        if (score!=other.score) return false
        if (questionId!=other.questionId) return false
        if (questionText!=other.questionText) return false
        if (!questionImage.contentEquals(other.questionImage)) return false
        if (typeName!=other.typeName) return false
        if (options!=other.options) return false

        return true
    }

    override fun hashCode(): Int {
        var result = score
        result = 31 * result + questionId.hashCode()
        result = 31 * result + questionText.hashCode()
        result = 31 * result + (questionImage?.contentHashCode() ?: 0)
        result = 31 * result + typeName.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class ScreeningExamOption(
    @Json(name = "option_id") val optionId: String,
    @Json(name = "option_txt") val optionText: String,
    @Json(name = "is_correct") val isCorrectInt: Int
){
    val isCorrect: Boolean get() = isCorrectInt != 0
}

@JsonClass(generateAdapter = true)
data class ScreeningExamAssessmentResults(
    @Json(name = "data") val result: ScreeningExamAssessmentResult
)

@JsonClass(generateAdapter = true)
data class ScreeningExamAssessmentResult(
    @Json(name = "result_id") val resultId: String,
    @Json(name = "screening_id") val screeningId: String,
    @Json(name = "student_id") val studentId: String,
    @Json(name = "tier_id") val tierId: Int,
    @Json(name = "score_percentage") val scorePercentage: Double,
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "attempt_number") val attemptNumber: Int,
    @Json(name = "date_taken") val dateTaken: LocalDateTime,
    @Json(name = "is_kept") val isKeptInt: Int
){
    val isKept: Boolean get() = isKeptInt != 0
}

@JsonClass(generateAdapter = true)
data class ScreeningExamAssessmentUpload(
    @Json(name = "student_id") val studentId: String,
    @Json(name = "screening_id") val screeningExamId: String,
    @Json(name = "score_percentage") val scorePercentage: Double,
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "answers") val answers: List<AnswerUploadRequest>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamResultsResponse(
    @Json(name = "earned_points") val earnedPoints: Int,
    @Json(name = "number_of_questions") val numberOfQuestions: Int,
    @Json(name = "tier_id") val tierId: Int,
    @Json(name = "attempt_number") val attemptNumber: Int,
    @Json(name = "data") val data: List<ScreeningExamResultResponse>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamResultResponse(
    @Json(name = "concept_id") val conceptId: String,
    @Json(name = "concept_name") val conceptName: String,
    @Json(name = "concept_score_percentage") val conceptScorePercentage: Double,
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "topics") val topics: List<ScreeningExamResultTopic>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamResultTopic(
    @Json(name = "topic_id") val topicId: String,
    @Json(name = "topic_name") val topicName: String,
    @Json(name = "topic_score_percentage") val topicScorePercentage: Double,
    @Json(name = "passed") val passed: Boolean
)

@JsonClass(generateAdapter = true)
data class ScreeningExamLearningResourcesResponse(
    @Json(name = "resources") val resources: List<ScreeningExamLearningResources>
)

@JsonClass(generateAdapter = true)
data class ScreeningExamLearningResources(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String?,
    @Json(name = "video_url") val videoUrl: String?,
    @Json(name = "pdf_blob") val pdfBlob: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ScreeningExamLearningResources

        if (title!=other.title) return false
        if (description!=other.description) return false
        if (videoUrl!=other.videoUrl) return false
        if (!pdfBlob.contentEquals(other.pdfBlob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (videoUrl?.hashCode() ?: 0)
        result = 31 * result + (pdfBlob?.contentHashCode() ?: 0)
        return result
    }
}


@JsonClass(generateAdapter = true)
data class CatchUpStudentResponse(
    @Json(name = "message") val message: String
)
