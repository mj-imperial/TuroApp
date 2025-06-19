package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoursesResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "courses") val courses: List<CourseResponse>
)

@JsonClass(generateAdapter = true)
data class CourseResponse(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "course_code") val courseCode: String,
    @Json(name = "course_name") val courseName: String,
    @Json(name = "teacher_id") val teacherId: String,
    @Json(name = "course_description") val description: String,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String,
    @Json(name = "image") val coursePicture: ByteArray,
    @Json(name = "enrollment_id") val enrollmentId: String?,
    @Json(name = "student_id") val studentId: String?,
    @Json(name = "enrollment_date") val enrollmentDate: String?,
    @Json(name = "isEnrolled") val isEnrolled: Int?,
    @Json(name = "finalGrade") val finalGrade: Int?
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as CourseResponse

        if (isEnrolled!=other.isEnrolled) return false
        if (finalGrade!=other.finalGrade) return false
        if (courseId!=other.courseId) return false
        if (courseCode!=other.courseCode) return false
        if (courseName!=other.courseName) return false
        if (teacherId!=other.teacherId) return false
        if (description!=other.description) return false
        if (startDate!=other.startDate) return false
        if (endDate!=other.endDate) return false
        if (!coursePicture.contentEquals(other.coursePicture)) return false
        if (enrollmentId!=other.enrollmentId) return false
        if (studentId!=other.studentId) return false
        if (enrollmentDate!=other.enrollmentDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isEnrolled ?: 0
        result = 31 * result + (finalGrade ?: 0)
        result = 31 * result + courseId.hashCode()
        result = 31 * result + courseCode.hashCode()
        result = 31 * result + courseName.hashCode()
        result = 31 * result + teacherId.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + coursePicture.contentHashCode()
        result = 31 * result + (enrollmentId?.hashCode() ?: 0)
        result = 31 * result + (studentId?.hashCode() ?: 0)
        result = 31 * result + (enrollmentDate?.hashCode() ?: 0)
        return result
    }
}

@JsonClass(generateAdapter = true)
data class CoursePicture(
    @Json(name = "success") val success: Boolean,
    @Json(name = "image") val coursePic: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as CoursePicture

        if (!coursePic.contentEquals(other.coursePic)) return false

        return true
    }

    override fun hashCode(): Int {
        return coursePic.contentHashCode()
    }
}