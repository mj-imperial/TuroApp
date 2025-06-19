package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
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
    savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])
    private val _courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow(EditModuleUIState())
    val uiState: StateFlow<EditModuleUIState> = _uiState.asStateFlow()

    init {
        getModule()
    }

    fun updateModuleName(newModuleName: String){
        _uiState.update { it.copy(moduleName = newModuleName) }
    }

    fun updateModuleDescription(newModuleDescription: String){
        _uiState.update { it.copy(moduleDescription = newModuleDescription) }
    }

    fun updateSelectedImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes() ?: byteArrayOf()
            inputStream?.close()

            _uiState.update { it.copy(modulePicture = byteArray) }
        }
    }

    fun getModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.getModule(_moduleId, _courseId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { module ->
                        _uiState.update { it.copy(
                            loading = false,
                            originalModuleName = module.moduleName,
                            moduleName = module.moduleName,
                            originalModuleDescription = module.moduleDescription,
                            moduleDescription = module.moduleDescription,
                            modulePicture = module.modulePicture
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

            val encodedImage = Base64.encodeToString(_uiState.value.modulePicture, Base64.NO_WRAP)

            val request = ModuleUpdateRequest(
                moduleName = _uiState.value.moduleName,
                moduleDescription = _uiState.value.moduleDescription,
                modulePicture = encodedImage,
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
    val modulePicture: ByteArray = byteArrayOf(),
    val editModuleStatus: Result<Unit>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as EditModuleUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (originalModuleName!=other.originalModuleName) return false
        if (moduleName!=other.moduleName) return false
        if (originalModuleDescription!=other.originalModuleDescription) return false
        if (moduleDescription!=other.moduleDescription) return false
        if (!modulePicture.contentEquals(other.modulePicture)) return false
        if (editModuleStatus!=other.editModuleStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + originalModuleName.hashCode()
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + originalModuleDescription.hashCode()
        result = 31 * result + moduleDescription.hashCode()
        result = 31 * result + modulePicture.contentHashCode()
        result = 31 * result + (editModuleStatus?.hashCode() ?: 0)
        return result
    }
}