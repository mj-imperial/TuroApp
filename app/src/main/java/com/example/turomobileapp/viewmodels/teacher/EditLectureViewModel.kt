package com.example.turomobileapp.viewmodels.teacher

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.LectureUpdateRequest
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
class EditLectureViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
) : ViewModel() {

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])
    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(EditLectureUIState())
    val uiState: StateFlow<EditLectureUIState> = _uiState.asStateFlow()

    init {
        getLecture()
    }

    fun getLecture() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            lectureRepository.getLecture(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { lecture ->
                        _uiState.update {
                            it.copy(
                                loading = false,
                                lectureTitle = lecture.lectureName,
                                lectureDescription = lecture.lectureDescription.orEmpty(),
                                fileUrl = lecture.fileUrl,
                            )
                        }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun updateLectureTitle(newTitle: String) {
        _uiState.update { it.copy(lectureTitle = newTitle) }
    }

    fun updateLectureDescription(newDescription: String) {
        _uiState.update { it.copy(lectureDescription = newDescription) }
    }

    fun updateUnlockDate(newUnlockDate: LocalDateTime?) {
        _uiState.update { it.copy(unlockDate = newUnlockDate) }
    }

    fun updateDeadlineDate(newDeadlineDate: LocalDateTime?) {
        _uiState.update { it.copy(deadlineDate = newDeadlineDate) }
    }

    fun updateContentTypeName(newContentType: String) {
        _uiState.update { it.copy(contentTypeName = newContentType) }
    }

    fun onFilePicked(context: Context, uri: Uri) {
        val mime = context.contentResolver.getType(uri)
        val name = getDisplayName(context, uri)
        val byteArray = context.contentResolver.openInputStream(uri)?.readBytes()

        _uiState.update {
            it.copy(
                fileUri = uri,
                fileName = name,
                fileMimeType = mime,
                fileUrl = byteArray
            )
        }
    }

    private fun getDisplayName(context: Context, uri: Uri): String =
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(idx)
        } ?: uri.lastPathSegment.orEmpty()

    fun updateLecture(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            if (state.contentTypeName == "PDF/DOCS" && state.fileUri != null) {
                try {
                    val mime = state.fileMimeType ?: "application/octet-stream"
                    val name = state.fileName ?: "lecture"
                    val bytes = context.contentResolver.openInputStream(state.fileUri)?.readBytes()

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

                } catch (e: Exception) {
                    _uiState.update { it.copy(loading = false, errorMessage = e.message ?: "Upload error") }
                    return@launch
                }
            }

            resetFieldsIfContentTypeChanged()
            sendEditRequest()
        }
    }

    private fun resetFieldsIfContentTypeChanged() {
        val state = _uiState.value

        _uiState.update {
            it.copy(
                fileUrl = if (state.contentTypeName != "PDF/DOCS") null else state.fileUrl,
                fileMimeType = if (state.contentTypeName != "PDF/DOCS") null else state.fileMimeType,
                fileName = if (state.contentTypeName != "PDF/DOCS") null else state.fileName,
                fileUri = if (state.contentTypeName != "PDF/DOCS") null else state.fileUri,
            )
        }
    }

    private fun sendEditRequest() {
        viewModelScope.launch {
            val state = _uiState.value

            val updateLecture = LectureUpdateRequest(
                lectureName = state.lectureTitle,
                lectureDescription = state.lectureDescription,
                unlockDate = state.unlockDate,
                deadlineDate = state.deadlineDate,
                contentTypeName = state.contentTypeName,
                fileUrl = state.fileUrl?.let { android.util.Base64.encodeToString(it, android.util.Base64.NO_WRAP) },
                fileMimeType = state.fileMimeType,
                fileName = state.fileName,
            )

            lectureRepository.updateLecture(_activityId, _moduleId, updateLecture).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, editLectureStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "LECTURE UPDATED",
                            notificationText = "The lecture has been successfully updated.",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, editLectureStatus = null) }
                    }
                )
            }
        }
    }

    fun clearLectureStatus() {
        _uiState.update { it.copy(editLectureStatus = null) }
    }
}

data class EditLectureUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val editLectureStatus: Result<Unit>? = null,
    val lectureTitle: String = "",
    val lectureDescription: String = "",
    val unlockDate: LocalDateTime? = null,
    val deadlineDate: LocalDateTime? = null,
    val originalContentTypeName: String = "",
    val contentTypeName: String = "",
    val fileUrl: ByteArray? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null,
    val fileUri: Uri? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as EditLectureUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (editLectureStatus!=other.editLectureStatus) return false
        if (lectureTitle!=other.lectureTitle) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDate!=other.unlockDate) return false
        if (deadlineDate!=other.deadlineDate) return false
        if (originalContentTypeName!=other.originalContentTypeName) return false
        if (contentTypeName!=other.contentTypeName) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false
        if (fileUri!=other.fileUri) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (editLectureStatus?.hashCode() ?: 0)
        result = 31 * result + lectureTitle.hashCode()
        result = 31 * result + lectureDescription.hashCode()
        result = 31 * result + (unlockDate?.hashCode() ?: 0)
        result = 31 * result + (deadlineDate?.hashCode() ?: 0)
        result = 31 * result + originalContentTypeName.hashCode()
        result = 31 * result + contentTypeName.hashCode()
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (fileUri?.hashCode() ?: 0)
        return result
    }
}