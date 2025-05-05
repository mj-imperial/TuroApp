package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.CourseApiService
import com.example.turomobileapp.models.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class CourseRepository @Inject constructor(private val courseApiService: CourseApiService) {
    fun getAllCourses(): Flow<Result<List<Course>>> = flow {
        try {
            val response = courseApiService.getAllCourses()
            if (response.isSuccessful){
                val courses = response.body()
                courses?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllCourses Empty Response Body")))
            }else{
                val errorMessage = "Failed to get all courses: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCourse(courseId: String): Flow<Result<Course>> = flow {
        try {
            val response = courseApiService.getCourse(courseId)
            if (response.isSuccessful){
                val course = response.body()
                course?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCourse is Empty")))
            }else{
                val errorMessage = "Failed to get course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun createCourse(course: Course): Flow<Result<Course>> = flow {
        try {
            val response = courseApiService.createCourse(course)
            if (response.isSuccessful){
                val course = response.body()
                course?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(Exception("createCourse Response Body is Empty")))
            }else{
                val errorMessage = "Failed to create course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateCourse(courseId: String, course: Course): Flow<Result<Unit>> = flow {
        try {
            val response = courseApiService.updateCourse(courseId, course)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun deleteCourse(courseId: String): Flow<Result<Unit>> = flow {
        try {
            val response = courseApiService.deleteCourse(courseId)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to delete course: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun isCourseDuplicate(course: Course): Flow<Result<Boolean>> = flow {
        try {
            val response = courseApiService.isCourseDuplicate(course)
            if (response.isSuccessful){
                val isDuplicate = response.body()
                isDuplicate?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("isCourseDuplicate Empty Response Body")))
            }else{
                val errorMessage = "Failed to check course duplicate: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
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
            val response = courseApiService.getCoursesForStudent(studentId)
            if (response.isSuccessful){
                val courses = response.body()
                courses?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getCoursesForStudent Empty Response Body")))
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
}