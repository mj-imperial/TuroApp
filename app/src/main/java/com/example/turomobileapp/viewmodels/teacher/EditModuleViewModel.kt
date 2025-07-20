package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.ModuleRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditModuleViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService,
    @ApplicationContext private val context: Context
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

    fun updateImageBytes(image: ByteArray) {
        _uiState.update { it.copy(moduleImage = image) }
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
                            moduleDescription = module.moduleDescription,
                            moduleName = module.moduleName,
                            moduleImage = module.modulePicture
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

            val state = _uiState.value
            val moduleIdPart = _moduleId.toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = state.moduleName.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = (state.moduleDescription ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = state.moduleImage?.let { imageBytes ->
                val file = createTempImageFile(context, imageBytes)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image_blob", file.name, requestFile)
            }

            moduleRepository.updateModule(moduleIdPart, namePart, descPart, imagePart).collect { result ->
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

    private fun createTempImageFile(context: Context, imageBytes: ByteArray): File {
        val file = File.createTempFile("module_image", ".jpg", context.cacheDir)
        file.writeBytes(imageBytes)
        return file
    }
}

data class EditModuleUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val moduleName: String = "",
    val moduleDescription: String? = null,
    val moduleImage: ByteArray? = null,
    val editModuleStatus: Result<Unit>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as EditModuleUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (moduleName!=other.moduleName) return false
        if (moduleDescription!=other.moduleDescription) return false
        if (!moduleImage.contentEquals(other.moduleImage)) return false
        if (editModuleStatus!=other.editModuleStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + moduleName.hashCode()
        result = 31 * result + (moduleDescription?.hashCode() ?: 0)
        result = 31 * result + (moduleImage?.contentHashCode() ?: 0)
        result = 31 * result + (editModuleStatus?.hashCode() ?: 0)
        return result
    }
}