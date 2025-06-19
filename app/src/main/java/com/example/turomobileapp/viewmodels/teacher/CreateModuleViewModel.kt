package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ModuleResponse
import com.example.turomobileapp.models.ModuleUploadRequest
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

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    fun updateModuleName(newModuleName: String){
        _uiState.update { it.copy(moduleName = newModuleName) }
        _isFormValid.value = validateInput()
    }

    fun updateModuleDescription(newModuleDescription: String){
        _uiState.update { it.copy(moduleDescription = newModuleDescription) }
        _isFormValid.value = validateInput()
    }

    fun updateSelectedImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)
            val byteArray = inputStream?.readBytes() ?: byteArrayOf()
            inputStream?.close()

            _uiState.update { it.copy(moduleImage = byteArray) }
        }
        _isFormValid.value = validateInput()
    }

    fun validateInput(): Boolean{
        val state = _uiState.value

        if (state.moduleName.isBlank()) return false

        if (state.moduleImage.isEmpty()) return false

        return true
    }

    fun createModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value
            val encodedImage = Base64.encodeToString(_uiState.value.moduleImage, Base64.NO_WRAP)

            val request = ModuleUploadRequest(
                moduleName = state.moduleName,
                moduleDescription = state.moduleDescription,
                moduleImage = encodedImage
            )

            moduleRepository.createModule(_courseId, request).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update {
                            it.copy(loading = false, moduleCreationStatus = Result.Success(Unit))
                        }

                        notificationService.showNotification(
                            notificationTitle = "Module Creation",
                            notificationText = "Module successfully created.",
                            route = "teacher_activity_modules/$_courseId"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update {
                            it.copy(loading = false, errorMessage = err, moduleCreationStatus = null)
                        }
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
    val moduleImage: ByteArray = byteArrayOf(),
    val moduleCreationStatus: Result<Unit>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as CourseModuleUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (moduleName!=other.moduleName) return false
        if (moduleDescription!=other.moduleDescription) return false
        if (!moduleImage.contentEquals(other.moduleImage)) return false
        if (moduleCreationStatus!=other.moduleCreationStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + moduleDescription.hashCode()
        result = 31 * result + moduleImage.contentHashCode()
        result = 31 * result + (moduleCreationStatus?.hashCode() ?: 0)
        return result
    }
}