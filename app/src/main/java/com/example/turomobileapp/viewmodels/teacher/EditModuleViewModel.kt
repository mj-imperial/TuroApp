package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.models.ModuleUpdateRequest
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
class EditModuleViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])
    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(EditModuleUIState())
    val uiState: StateFlow<EditModuleUIState> = _uiState.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    init {
        getModule()
    }

    fun updateModuleName(newModuleName: String){
        _uiState.update { it.copy(moduleName = newModuleName) }
        _isFormValid.value = validateForm()
    }

    fun updateModuleDescription(newModuleDescription: String){
        _uiState.update { it.copy(moduleDescription = newModuleDescription) }
        _isFormValid.value = validateForm()
    }

    fun validateForm(): Boolean{
        val state = _uiState

        if (state.value.originalModuleName == state.value.moduleName) return false

        return true
    }

    fun getModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.getModule(_moduleId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { module ->
                        _uiState.update { it.copy(
                            loading = false,
                            originalModuleName = module.moduleName,
                            moduleName = module.moduleName,
                            originalModuleDescription = module.moduleDescription,
                            moduleDescription = module.moduleDescription
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun saveEditedModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val request = ModuleUpdateRequest(
                moduleName = _uiState.value.moduleName,
                moduleDescription = _uiState.value.moduleDescription
            )

            moduleRepository.updateModule(_moduleId, request).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, editModuleStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Module Edited",
                            notificationText = "Successfully edited ${_uiState.value.moduleName}",
                            route = "teacher_activity_modules/${_courseId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, editModuleStatus = null, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun clearEditStatus() {
        _uiState.update { it.copy(editModuleStatus = null) }
    }
}

data class EditModuleUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val originalModuleName: String = "",
    val moduleName: String = "",
    val originalModuleDescription: String = "",
    val moduleDescription: String = "",
    val editModuleStatus: Result<Unit>? = null
)