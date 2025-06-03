package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.repositories.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService

@HiltViewModel
class ModuleListActivityActionsViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(GetModulesInCourseUIState())
    val uiState: StateFlow<GetModulesInCourseUIState> = _uiState.asStateFlow()

    init {
        getModulesInCourse()
    }

    fun getModulesInCourse(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = false, errorMessage = null) }

            moduleRepository.getModulesForCourse(_courseId).collect { result ->
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

    fun deleteModuleInCourse(moduleId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = false, errorMessage = null) }

            moduleRepository.deleteModule(moduleId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, moduleDeleteStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Module Deleted",
                            notificationText = "You have deleted a Module.",
                            route = "teacher_view_all_modules/$_courseId"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun resetModuleDeleteStatus(){
        _uiState.update { it.copy(moduleDeleteStatus = null) }
    }
}

data class GetModulesInCourseUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val modules: List<ModuleResponse> = emptyList(),
    val moduleDeleteStatus: Result<Unit>? = null
)