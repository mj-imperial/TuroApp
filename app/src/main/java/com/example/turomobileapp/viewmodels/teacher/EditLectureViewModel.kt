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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditLectureViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])
    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(EditLectureUIState())
    val uiState: StateFlow<EditLectureUIState> = _uiState.asStateFlow()

    init {
        getLecture()
    }

    fun getLecture(){
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
                                lectureDescription = lecture.lectureDescription.toString(),
                                unlockDate = lecture.unlockDate,
                                deadlineDate = lecture.deadlineDate,
                                originalContentTypeName = lecture.contentTypeName,
                                contentTypeName = lecture.contentTypeName,
                                videoUrl = lecture.videoUrl,
                                fileUrl = lecture.fileUrl,
                                fileName = lecture.fileName,
                                fileMimeType = lecture.fileMimeType,
                                textBody = lecture.textBody
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

    fun updateLectureTitle(newTitle: String){
        _uiState.update { it.copy(lectureTitle = newTitle) }
    }

    fun updateLectureDescription(newDescription: String){
        _uiState.update { it.copy(lectureDescription = newDescription) }
    }

    fun updateUnlockDate(newUnlockDate: LocalDateTime?){
        _uiState.update { it.copy(unlockDate = newUnlockDate) }
    }

    fun updateDeadlineDate(newDeadlineDate: LocalDateTime?){
        _uiState.update { it.copy(deadlineDate = newDeadlineDate) }
    }

    fun updateContentTypeName(newContentType: String){
        _uiState.update { it.copy(contentTypeName = newContentType) }
    }

    fun updateVideoUrl(newUrl: String){
        _uiState.update { it.copy(videoUrl = newUrl) }
    }

    fun updateText(newText: String){
        _uiState.update { it.copy(textBody = newText) }
    }

    fun onFilePicked(context: Context, uri: Uri) {
        val mime = context.contentResolver.getType(uri)
        val name = getDisplayName(context, uri)

        _uiState.update {
            it.copy(
                fileUri = uri,
                fileName = name,
                fileMimeType = mime
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
            var uploadedFileUrl = state.fileUrl

            if (state.contentTypeName == "PDF/DOCS" && state.fileUri != null) {
                try {
                    val mime = state.fileMimeType ?: "application/octet-stream"
                    val bytes = context.contentResolver.openInputStream(state.fileUri)!!.readBytes()
                    val req = bytes.toRequestBody(mime.toMediaType())
                    val part = MultipartBody.Part.createFormData("file", state.fileName ?: "lecture", req)

                    lectureRepository.uploadLectureFile(part).collect { result ->
                        handleResult(result,
                            onSuccess = { response ->
                                uploadedFileUrl = response.fileUrl
                                _uiState.update {
                                    it.copy(fileUrl = response.fileUrl)
                                }
                                resetFieldsIfContentTypeChanged()
                                sendEditRequest()
                            },
                            onFailure = { error ->
                                _uiState.update { it.copy(loading = false, errorMessage = error) }
                            }
                        )
                    }

                    return@launch
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
                textBody = if (state.contentTypeName != "TEXT") null else state.textBody,
                videoUrl = if (state.contentTypeName != "VIDEO") null else state.videoUrl
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
                videoUrl = state.videoUrl,
                fileUrl = state.fileUrl,
                fileMimeType = state.fileMimeType,
                fileName = state.fileName,
                textBody = state.textBody
            )

            lectureRepository.updateLecture(_activityId, _moduleId, updateLecture).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, editLectureStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle =  "LECTURE UPDATED",
                            notificationText =  "The lecture has been successfully updated.",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = {err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, editLectureStatus = null) }
                    }
                )
            }
        }
    }

    fun clearLectureStatus(){
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
    val videoUrl: String? = null,
    val fileUrl: String? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null,
    val textBody: String? = null,
    val fileUri: Uri? = null
)