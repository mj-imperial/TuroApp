package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ActivityItem
import com.example.turomobileapp.models.TeacherGetModuleResponse
import com.example.turomobileapp.models.TeacherModuleResponse
import com.example.turomobileapp.repositories.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewAllModulesViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val _sectionId: String = checkNotNull(savedStateHandle["sectionId"])
    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(TeacherViewAllModulesUIState())
    val uiState: StateFlow<TeacherViewAllModulesUIState> = _uiState.asStateFlow()

    init {
        getModulesInCourse()
    }

    fun getModulesInCourse(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = false, errorMessage = null) }

            moduleRepository.getModulesForCourseTeacher(_courseId, _sectionId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {  modules ->
                        _uiState.update { it.copy(loading = false, modules = modules) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun getActivitiesInModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.getActivitiesInModuleForTeacher(_moduleId, _sectionId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        viewModelScope.launch {
                            val quizOrder = listOf("PRACTICE", "SHORT")
                            val lectures = resp.filter { it.activityType == "LECTURE" }
                            val tutorials = resp.filter { it.activityType == "TUTORIAL" }
                            val quizzes = resp
                                .filter { it.activityType == "QUIZ" }
                                .sortedWith(compareBy { quizItem ->
                                    val subtype = quizItem.quizTypeName ?: ""
                                    quizOrder.indexOf(subtype).takeIf { it >= 0 } ?: Int.MAX_VALUE
                                })
                            val finalActivities = mutableListOf<ActivityItem>()
                            finalActivities += lectures
                            finalActivities += tutorials
                            finalActivities += quizzes

                            _uiState.update {
                                it.copy(loading = false, activities = finalActivities)
                            }
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun updateModuleName(moduleName: String){
        _uiState.update { it.copy(moduleName = moduleName) }
    }

    fun clearModuleName(){
        _uiState.update { it.copy(moduleName = "") }
    }
}

data class TeacherViewAllModulesUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val modules: List<TeacherModuleResponse> = emptyList(),
    val activities: List<ActivityItem> = emptyList(),
    val moduleName: String = ""
)