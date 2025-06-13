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
    val fileUrl: String? = null,
    val fileMimeType: String? = null,
    val fileName: String? = null,
)