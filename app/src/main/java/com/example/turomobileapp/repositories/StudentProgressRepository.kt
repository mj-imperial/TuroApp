package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.StudentProgressApiService
import com.example.turomobileapp.models.StudentProgress
import com.example.turomobileapp.models.UpdateStudentCourseProgressRequest
import com.example.turomobileapp.models.UpdateStudentModuleProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class  StudentProgressRepository @Inject constructor(private val studentProgressApiService: StudentProgressApiService) {

    fun getAllStudentCourseProgress(studentId: String): Flow<Result<List<StudentProgress>>> = flow {
        try {
            val response = studentProgressApiService.getAllStudentCourseProgress(studentId)
            if (response.isSuccessful){
                val studentProgresses = response.body()
                studentProgresses?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getAllStudentCourseProgress Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all student course progress: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getStudentCourseProgress(studentId: String, courseId: String): Flow<Result<StudentProgress>> = flow {
        try {
            val response = studentProgressApiService.getStudentCourseProgress(studentId, courseId)
            if (response.isSuccessful){
                val studentProgress = response.body()
                studentProgress?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(IOException("getStudentCourseProgress Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get student course progress: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun getCourseProgressForAllStudents(courseId: String): Flow<Result<List<StudentProgress>>> = flow {
        try {
            val response = studentProgressApiService.getCourseProgressForAllStudents(courseId)
            if (response.isSuccessful){
                val studentProgresses = response.body()
                studentProgresses?.let {
                    emit(Result.Success(it))
                } ?: emit(Result.Failure(Exception("getCourseProgressForAllStudents Response Body is Empty")))
            }else{
                val errorMessage = "Failed to get all course progress for all students: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                emit(Result.Failure(IOException(errorMessage)))
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateStudentProgress(studentId: String, moduleId: String, request: UpdateStudentModuleProgressRequest): Flow<Result<Unit>> = flow {
        try {
            val response = studentProgressApiService.updateStudentModuleProgress(studentId, moduleId, request)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update student progress: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    emit(Result.Failure(IOException(errorMessage)))
                }
            }
        }catch (e: IOException) {
            emit(Result.Failure(e))
        }catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }

    fun updateStudentCourseProgress(studentId: String, courseId: String, request: UpdateStudentCourseProgressRequest): Flow<Result<Unit>> = flow {
        try {
            val response = studentProgressApiService.updateStudentCourseProgress(studentId, courseId, request)
            when{
                response.isSuccessful -> emit(Result.Success(Unit))
                else -> {
                    val errorMessage = "Failed to update student course progress: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
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