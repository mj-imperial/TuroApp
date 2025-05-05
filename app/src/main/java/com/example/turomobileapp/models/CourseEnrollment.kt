package com.example.turomobileapp.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Course(
    @SerializedName("course_id") val courseId: String,
    @SerializedName("course_code") val courseCode: String,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("teacher_id") val teacherId: String,
    @SerializedName("course_description") val courseDescription: String,
    @SerializedName("start_date") val startDate: Date? = null,
    @SerializedName("end_date") val endDate: Date?,
)

@JsonClass(generateAdapter = true)
data class Enrollment(
    @SerializedName("enrollment_id") val enrollmentId: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("enrollment_date") val enrollmentDate: Date,
    @SerializedName("enrollment_status") val isEnrolled: Boolean = false,
    @SerializedName("final_grade") val finalGrade: Double = 0.0
)