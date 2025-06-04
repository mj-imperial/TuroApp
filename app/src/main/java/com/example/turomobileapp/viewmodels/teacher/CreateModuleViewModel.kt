package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.repositories.ModuleRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateModuleViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(CourseModuleUIState())
    val uiState: StateFlow<CourseModuleUIState> = _uiState.asStateFlow()

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
                        _uiState.update { it.copy(loading = false, currentModules = modules) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun updateModuleName(newModuleName: String){
        _uiState.update { it.copy(moduleName = newModuleName) }
        validateInput()
    }

    fun updateModuleDescription(newModuleDescription: String){
        _uiState.update { it.copy(moduleDescription = newModuleDescription) }
    }

    fun validateInput(){
        _uiState.update {
            it.copy(isCreationEnabled = it.moduleName.isNotBlank())
        }
    }

    fun createModule(){
        validateInput()
        if (!_uiState.value.isCreationEnabled){
            _uiState.update { it.copy(errorMessage = "Please enter a Module Name and Module Description (optional)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.createModule(
                _courseId,
                _uiState.value.moduleName.toString(),
                _uiState.value.moduleDescription.toString()
            ).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        _uiState.update { it.copy(loading = false, moduleCreationStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Module Creation",
                            notificationText = "Module successfully created.",
                            route = "teacher_createModule"
                        )
                    },
                    onFailure = {  err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, moduleCreationStatus = null) }
                    },
                )
            }
        }
    }

    fun clearCreateModule(){
        _uiState.update { it.copy(moduleName = "", moduleDescription = "", moduleCreationStatus = null) }
    }

    fun clearCreationStatus(){
        _uiState.update { it.copy(moduleCreationStatus = null) }
    }
}

data class CourseModuleUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val moduleName: String = "",
    val moduleDescription: String = "",
    val moduleCreationStatus: Result<Unit>? = null,
    val isCreationEnabled: Boolean = false,
    val currentModules: List<ModuleResponse> = emptyList()
)