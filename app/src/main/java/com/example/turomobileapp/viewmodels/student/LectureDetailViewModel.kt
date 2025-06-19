package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.LectureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val lectureRepository: LectureRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])

    private val _uiState = MutableStateFlow(LectureDetailUIState())
    val uiState: StateFlow<LectureDetailUIState> = _uiState.asStateFlow()

    init {
        getLecture()
    }

    fun getLecture(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            lectureRepository.getLecture(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            lectureTitle = resp.lectureName,
                            lectureDescription = resp.lectureDescription.toString(),
                            unlockDateTime = resp.unlockDate,
                            deadlineDateTime = resp.deadlineDate,
                            contentTypeName = resp.contentTypeName,
                            text = resp.textBody,
                            youtubeUrl = resp.videoUrl,
                            fileUrl = resp.fileUrl,
                            fileMimeType = resp.fileMimeType,
                            fileName = resp.fileName
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }
}

data class LectureDetailUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val lectureTitle: String = "",
    val lectureDescription: String = "",
    val unlockDateTime: LocalDateTime? = null,
    val deadlineDateTime: LocalDateTime? = null,
    val contentTypeName: String = "",
    val text: String? = null,
    val youtubeUrl: String? = null,
    val fileUrl: ByteArray? = byteArrayOf(),
    val fileMimeType: String? = null,
    val fileName: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureDetailUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (lectureTitle!=other.lectureTitle) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (unlockDateTime!=other.unlockDateTime) return false
        if (deadlineDateTime!=other.deadlineDateTime) return false
        if (contentTypeName!=other.contentTypeName) return false
        if (text!=other.text) return false
        if (youtubeUrl!=other.youtubeUrl) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false
        if (fileMimeType!=other.fileMimeType) return false
        if (fileName!=other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + lectureTitle.hashCode()
        result = 31 * result + lectureDescription.hashCode()
        result = 31 * result + (unlockDateTime?.hashCode() ?: 0)
        result = 31 * result + (deadlineDateTime?.hashCode() ?: 0)
        result = 31 * result + contentTypeName.hashCode()
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (youtubeUrl?.hashCode() ?: 0)
        result = 31 * result + fileUrl.contentHashCode()
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        return result
    }
}