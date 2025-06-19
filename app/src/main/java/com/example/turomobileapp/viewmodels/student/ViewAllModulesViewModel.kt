package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.AssessmentResultResponse
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.models.ModuleResponseStudent
import com.example.turomobileapp.repositories.AssessmentResultRepository
import com.example.turomobileapp.repositories.ModuleRepository
import com.example.turomobileapp.viewmodels.SessionManager
import com.example.turomobileapp.viewmodels.utils.unlockModuleQuizzesInOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewAllModulesViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val assessmentResultRepository: AssessmentResultRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(ViewAllModulesUIState())
    val uiState: StateFlow<ViewAllModulesUIState> = _uiState.asStateFlow()

    init {
        getCurrentModule()
    }

    fun getCurrentModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            moduleRepository.getCurrentModule(studentId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            moduleId = resp.moduleId,
                            moduleName = resp.moduleName,
                            modulePicture = resp.modulePicture,
                            moduleDescription = resp.moduleDescription,
                            moduleProgress = resp.moduleProgress
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getModules(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val studentId: String = sessionManager.userId.filterNotNull().first()

            moduleRepository.getModulesForCourseStudent(_courseId, studentId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { modules ->
                        _uiState.update { it.copy(loading = false, modules = modules) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun getActivitiesInModule(moduleId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.getActivitiesForModule(moduleId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        viewModelScope.launch {
                            val quizOrder = listOf("PRACTICE", "SHORT", "LONG")
                            val lectures = resp.filter { it.activityType == "LECTURE" }
                            val tutorials = resp.filter { it.activityType == "TUTORIAL" }
                            val quizzes = resp.filter { it.activityType == "QUIZ" && it.quizTypeName != "SCREENING_EXAM" }

                            val studentId: String = sessionManager.userId.filterNotNull().first()

                            val enrichedQuizzes = quizzes.map { quiz ->
                                val attempts = withContext(Dispatchers.IO) {
                                    getAssessmentResults(studentId, quiz.activityId)
                                }

                                val hasAnswered = attempts.isNotEmpty()
                                val passed = attempts.any { it.scorePercentage >= 75 }

                                quiz.copy(
                                    hasAnswered = hasAnswered,
                                    isUnlocked = passed
                                )
                            }

                            val unlockedQuizzes = unlockModuleQuizzesInOrder(
                                activities = enrichedQuizzes,
                                quizTypeOrder = quizOrder
                            )

                            val finalActivities = mutableListOf<ModuleActivityResponse>()
                            finalActivities += lectures.map { it.copy(isUnlocked = true) }
                            finalActivities += tutorials.map { it.copy(isUnlocked = true) }
                            finalActivities += unlockedQuizzes

                            val sortedActivities = finalActivities.sortedBy { it.displayOrder }

                            _uiState.update {
                                it.copy(loading = false, activities = sortedActivities)
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

    suspend fun getAssessmentResults(studentId: String, activityId: String): List<AssessmentResultResponse> {
        var resultList: List<AssessmentResultResponse> = emptyList()

        assessmentResultRepository.getAssessmentResultsForQuizAndStudent(studentId, activityId).collect { result ->
            handleResult(
                result = result,
                onSuccess = { resultList = it },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err) }
                }
            )
        }

        return resultList
    }
}

data class ViewAllModulesUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val modules: List<ModuleResponseStudent> = emptyList(),
    val activities: List<ModuleActivityResponse> = emptyList(),
    val moduleName: String = "",
    val moduleId: String = "",
    val modulePicture: ByteArray = byteArrayOf(),
    val moduleDescription: String = "",
    val moduleProgress: Double = 0.0
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as ViewAllModulesUIState

        if (loading!=other.loading) return false
        if (moduleProgress!=other.moduleProgress) return false
        if (errorMessage!=other.errorMessage) return false
        if (modules!=other.modules) return false
        if (activities!=other.activities) return false
        if (moduleName!=other.moduleName) return false
        if (moduleId!=other.moduleId) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false
        if (moduleDescription!=other.moduleDescription) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + moduleProgress.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + modules.hashCode()
        result = 31 * result + activities.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + moduleId.hashCode()
        result = 31 * result + modulePicture.contentHashCode()
        result = 31 * result + moduleDescription.hashCode()
        return result
    }
}