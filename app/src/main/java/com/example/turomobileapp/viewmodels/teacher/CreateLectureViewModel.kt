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
        val name = getDisplayName(context, uri)
        val mime = context.contentResolver.getType(uri)

        _uiState.update {
            it.copy(
                fileUri = uri,
                fileName = name,
                fileMimeType = mime
            )
        }

        _isFormValid.value = validateForm()
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
            "PDF/DOCS" -> !state.fileUrl?.isEmpty()!!
            "VIDEO" -> !state.youtubeUrl.isNullOrBlank()
            "TEXT" -> !state.text.isNullOrBlank()
            else -> false
        }
    }

    fun createLecture(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            if (state.uploadType == "PDF/DOCS") {
                val uri = state.fileUri
                if (uri == null) {
                    _uiState.update { it.copy(loading = false, errorMessage = "No file selected") }
                    return@launch
                }

                try {
                    val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
                    val name = getDisplayName(context, uri)
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()

                    if (bytes == null) {
                        _uiState.update { it.copy(loading = false, errorMessage = "Failed to read file bytes") }
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            fileUrl = bytes,
                            fileMimeType = mime,
                            fileName = name
                        )
                    }

                    submitLecture()

                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(loading = false, errorMessage = e.message ?: "File read error")
                    }
                }

            } else {
                submitLecture()
            }
        }
    }

    private fun submitLecture() {
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

        viewModelScope.launch {
            lectureRepository.createLecture(_moduleId, lectureRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
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
    val fileUrl: ByteArray? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null,
    val fileUri: Uri? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as CreateLectureUIState

        if (loading!=other.loading) return false
        if (loadingFile!=other.loadingFile) return false
        if (errorMessage!=other.errorMessage) return false
        if (createLectureStatus!=other.createLectureStatus) return false
        if (lectureTitle!=other.lectureTitle) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDateTime!=other.unlockDateTime) return false
        if (deadlineDateTime!=other.deadlineDateTime) return false
        if (uploadType!=other.uploadType) return false
        if (text!=other.text) return false
        if (youtubeUrl!=other.youtubeUrl) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false
        if (fileUri!=other.fileUri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + loadingFile.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (createLectureStatus?.hashCode() ?: 0)
        result = 31 * result + lectureTitle.hashCode()
        result = 31 * result + lectureDescription.hashCode()
        result = 31 * result + (unlockDateTime?.hashCode() ?: 0)
        result = 31 * result + (deadlineDateTime?.hashCode() ?: 0)
        result = 31 * result + uploadType.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (youtubeUrl?.hashCode() ?: 0)
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (fileUri?.hashCode() ?: 0)
        return result
    }
}