package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseActionsViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(CourseModuleUIState())
    val uiState: StateFlow<CourseModuleUIState> = _uiState.asStateFlow()

    fun updateModuleName(newModuleName: String){
        _uiState.update { it.copy(moduleName = newModuleName) }
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
                        _uiState.update { it.copy(loading = false, moduleCreationStatus = response.success) }
                    },
                    onFailure = {  err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, moduleCreationStatus = false) }
                    },
                )
            }
        }
    }

    fun clearCreateModule(){
        _uiState.update { it.copy(moduleName = "", moduleDescription = "", moduleCreationStatus = null) }
    }
}

data class CourseModuleUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val moduleName: String = "",
    val moduleDescription: String = "",
    val moduleCreationStatus: Boolean? = null,
    val isCreationEnabled: Boolean = false
)