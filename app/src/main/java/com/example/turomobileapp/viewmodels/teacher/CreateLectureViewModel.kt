package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.LectureUploadRequest
import com.example.turomobileapp.repositories.LectureRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateLectureViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(CreateLectureUIState())
    val uiState: StateFlow<CreateLectureUIState> = _uiState.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    fun updateLectureTitle(newLectureTitle: String){
        _uiState.update { it.copy(lectureTitle = newLectureTitle) }
        _isFormValid.value = validateForm()
    }

    fun updateLectureDescription(newLectureDescription: String){
        _uiState.update { it.copy(lectureDescription = newLectureDescription) }
        _isFormValid.value = validateForm()
    }

    fun updateUnlockDateTime(newLocalUnlockDateTime: LocalDateTime?){
        _uiState.update { it.copy(unlockDateTime = newLocalUnlockDateTime) }
        _isFormValid.value = validateForm()
    }

    fun updateDeadlineDateTime(newLocalDeadlineDateTime: LocalDateTime?){
        _uiState.update { it.copy(deadlineDateTime = newLocalDeadlineDateTime) }
        _isFormValid.value = validateForm()
    }

    fun updateUploadType(newUploadType: String){
        _uiState.update { it.copy(uploadType = newUploadType) }
        _isFormValid.value = validateForm()
    }

    fun updateText(newText: String){
        _uiState.update { it.copy(text = newText) }
        _isFormValid.value = validateForm()
    }

    fun updateYoutubeUrl(newYoutubeUrl: String){
        _uiState.update { it.copy(youtubeUrl = newYoutubeUrl) }
        _isFormValid.value = validateForm()
    }

    fun onFilePicked(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingFile = true, errorMessage = null) }

            val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val name = getDisplayName(context, uri)
            val bytes = context.contentResolver.openInputStream(uri)!!.readBytes()
            val req = bytes.toRequestBody(mime.toMediaType())
            val part = MultipartBody.Part.createFormData("file", name, req)

            lectureRepository.uploadLectureFile(part).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        _uiState.update { it.copy(
                            loadingFile = false,
                            fileUrl = response.fileUrl,
                            fileMimeType = response.mimeType,
                            fileName = getDisplayName(context, uri)
                        ) }
                        _isFormValid.value = validateForm()
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loadingFile = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    private fun getDisplayName(context: Context, uri: Uri): String =
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(idx)
        } ?: uri.lastPathSegment.orEmpty()

    fun validateForm(): Boolean{
        val state = _uiState.value

        if (state.lectureTitle.isBlank()) return false

        val unlock = state.unlockDateTime
        if (unlock == null) return false

        return when (state.uploadType) {
            "PDF/DOCS" -> !state.fileUrl.isNullOrBlank()
            "VIDEO" -> !state.youtubeUrl.isNullOrBlank()
            "TEXT" -> !state.text.isNullOrBlank()
            else -> false
        }
    }

    fun createLecture(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            val lectureRequest = LectureUploadRequest(
                activityType = "LECTURE",
                lectureName = state.lectureTitle,
                lectureDescription = state.lectureDescription,
                unlockDate = state.unlockDateTime,
                deadlineDate = state.deadlineDateTime,
                contentTypeName = state.uploadType,
                videoUrl = state.youtubeUrl,
                fileUrl = state.fileUrl,
                fileMimeType = state.fileMimeType,
                fileName = state.fileName,
                textBody = state.text
            )

            lectureRepository.createLecture(_moduleId, lectureRequest).collect {  result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        _uiState.update { it.copy(loading = false, createLectureStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "LECTURE CREATION",
                            notificationText = "You have successfully created a lecture.",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun clearCreateLectureStatus(){
        _uiState.update { it.copy(createLectureStatus = null) }
    }
}

data class CreateLectureUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val loadingFile: Boolean = false,
    val createLectureStatus: Result<Unit>? = null,
    val lectureTitle: String = "",
    val lectureDescription: String = "",
    val unlockDateTime: LocalDateTime? = null,
    val deadlineDateTime: LocalDateTime? = null,
    val uploadType: String = "PDF/DOCS",
    val text: String? = null,
    val youtubeUrl: String? = null,
    val fileUrl: String? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null
)