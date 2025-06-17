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
    @Json(name = "course_picture") val coursePicture: String,
    @Json(name = "enrollment_id") val enrollmentId: String?,
    @Json(name = "student_id") val studentId: String?,
    @Json(name = "enrollment_date") val enrollmentDate: String?,
    @Json(name = "isEnrolled") val isEnrolled: Int?,
    @Json(name = "finalGrade") val finalGrade: Int?
)