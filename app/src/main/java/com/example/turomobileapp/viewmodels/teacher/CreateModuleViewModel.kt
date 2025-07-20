package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.graphics.scale
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
class CreateModuleViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService,
    @ApplicationContext private val context: Context
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

    fun updateImageBytes(image: ByteArray) {
        _uiState.update { it.copy(moduleImage = image) }
    }

    fun validateInput(): Boolean{
        val state = _uiState.value

        return state.moduleName.isNotBlank()
    }

    fun createModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value
            val courseIdPart = _courseId.toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = state.moduleName.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = (state.moduleDescription ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = state.moduleImage?.let { imageBytes ->
                val file = createTempImageFile(context, imageBytes)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image_blob", file.name, requestFile)
            }

            moduleRepository.createModule(courseIdPart, namePart, descPart, imagePart).collect { result ->
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

    private fun createTempImageFile(context: Context, imageBytes: ByteArray): File {
        val file = File.createTempFile("module_image", ".jpg", context.cacheDir)
        file.writeBytes(imageBytes)
        return file
    }

    fun clearCreateModule(){
        _uiState.update { it.copy(moduleName = "", moduleDescription = "", moduleCreationStatus = null, moduleImage = null) }
    }

    fun clearCreationStatus(){
        _uiState.update { it.copy(moduleCreationStatus = null) }
    }
}

data class CourseModuleUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val moduleName: String = "",
    val moduleDescription: String? = null,
    val moduleImage: ByteArray? = null,
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
        result = 31 * result + (moduleDescription?.hashCode() ?: 0)
        result = 31 * result + (moduleImage?.contentHashCode() ?: 0)
        result = 31 * result + (moduleCreationStatus?.hashCode() ?: 0)
        return result
    }
}