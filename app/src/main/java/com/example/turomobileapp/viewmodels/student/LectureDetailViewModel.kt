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
                            fileUrl = resp.fileUrl
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
    val fileUrl: ByteArray? = byteArrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this===other) return true
        if (javaClass!=other?.javaClass) return false

        other as LectureDetailUIState

        if (loading!=other.loading) return false
        if (errorMessage!=other.errorMessage) return false
        if (lectureTitle!=other.lectureTitle) return false
        if (lectureDescription!=other.lectureDescription) return false
        if (!fileUrl.contentEquals(other.fileUrl)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + lectureTitle.hashCode()
        result = 31 * result + lectureDescription.hashCode()
        result = 31 * result + (fileUrl?.contentHashCode() ?: 0)
        return result
    }
}