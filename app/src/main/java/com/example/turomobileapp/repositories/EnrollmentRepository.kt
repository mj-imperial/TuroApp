package com.example.turoapp.repositories

import com.example.turoapp.interfaces.EnrollmentApiService
import com.example.turoapp.models.Course
import com.example.turoapp.models.Enrollment
import com.example.turoapp.models.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class EnrollmentRepository @Inject constructor(private val enrollmentApiService: EnrollmentApiService) {

    fun enrollStudentInCourse(enrollment: Enrollment, studentId: String, courseId: String): Flow<Result<Enrollment>> = flow {
        try {
            val response = enrollmentApiService.enrollStudentInCourse(enrollment, studentId, courseId)
            if (response.isSuccessful){
                val enrollmentResult = response.body()
                enrollmentResult?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("enrollStudentInCourse Response Body is Empty")))
            }else{
                val errorMessage = "Failed to enroll student in course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getEnrollment(enrollmentId: String): Flow<Result<Enrollment>> = flow {
        try {
            val response = enrollmentApiService.getEnrollmentId(enrollmentId)
            if (response.isSuccessful){
                val enrollmentResult = response.body()
                enrollmentResult?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getEnrollment Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get enrollment: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCoursesForStudent(studentId: String): Flow<Result<List<Course>>> = flow {
        try {
            val response = enrollmentApiService.getCoursesForStudent(studentId)
            if (response.isSuccessful){
                val courses = response.body()
                courses?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCoursesForStudent Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get courses for student: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getStudentsInCourse(courseId: String): Flow<Result<List<Student>>> = flow {
        try {
            val response = enrollmentApiService.getStudentsinCourse(courseId)
            if (response.isSuccessful){
                val students = response.body()
                students?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getStudentsInCourse Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get students in course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun unenrollStudentFromCourse(studentId: String, courseId: String): Flow<Result<Unit>> = flow {
        try {
            val response = enrollmentApiService.unenrollStudentFromCourse(courseId, studentId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to unenroll student from course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }
}