package com.example.turomobileapp.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoursesResponse(
    @Json(name = "data") val courses: List<CourseResponse>
)

@JsonClass(generateAdapter = true)
data class CourseResponse(
    @Json(name = "course_id") val courseId: String,
    @Json(name = "course_code") val courseCode: String,
    @Json(name = "course_name") val courseName: String,
    @Json(name = "course_description") val description: String,
    @Json(name = "start_date") val startDate: String,
    @Json(name = "end_date") val endDate: String,
    @Json(name = "image") val coursePicture: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as CourseResponse

        if (courseId!=other.courseId) return false
        if (courseCode!=other.courseCode) return false
        if (courseName!=other.courseName) return false
        if (description!=other.description) return false
        if (startDate!=other.startDate) return false
        if (endDate!=other.endDate) return false
        if (!coursePicture.contentEquals(other.coursePicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = courseId.hashCode()
        result = 31 * result + courseCode.hashCode()
        result = 31 * result + courseName.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + startDate.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + coursePicture.contentHashCode()
        return result
    }
}

@JsonClass(generateAdapter = true)
data class CoursePicture(
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