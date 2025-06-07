package com.example.turomobileapp.viewmodels.teacher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.TutorialUploadRequest
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.repositories.TutorialRepository
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CreateTutorialViewModel @Inject constructor(
    private val tutorialRepository: TutorialRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(CreateTutorialUIState())
    val uiState: StateFlow<CreateTutorialUIState> = _uiState.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    fun updateActivityName(newTutorialName: String){
        _uiState.update { it.copy(activityName = newTutorialName) }
        _isFormValid.value = validateForm()
    }

    fun updateActivityDescription(newTutorialDescription: String){
        _uiState.update { it.copy(activityDescription = newTutorialDescription) }
        _isFormValid.value = validateForm()
    }

    fun updateUnlockDateTime(newUnlockDateTime: LocalDateTime?){
        _uiState.update { it.copy(unlockDateTime = newUnlockDateTime) }
        _isFormValid.value = validateForm()
    }

    fun updateDeadlineDateTime(newDeadlineDateTime: LocalDateTime?){
        _uiState.update { it.copy(deadlineDateTime = newDeadlineDateTime) }
        _isFormValid.value = validateForm()
    }

    fun updateVideoUrl(newVideoURL: String){
        _uiState.update { it.copy(videoUrl = newVideoURL) }
        _isFormValid.value = validateForm()
    }

    fun validateForm(): Boolean{
        val state = _uiState.value

        if (state.activityName.isBlank()) return false

        if (state.videoUrl.isBlank()) return false

        val unlock = state.unlockDateTime
        if (unlock == null) return false

        val deadline = state.deadlineDateTime

        deadline?.let { if (it.isBefore(unlock)) return false }

        return true
    }

    fun saveTutorial(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val tutorialRequest = TutorialUploadRequest(
                activityName = _uiState.value.activityName,
                activityDescription = _uiState.value.activityDescription,
                unlockDateTime = _uiState.value.unlockDateTime,
                deadlineDateTime = _uiState.value.deadlineDateTime,
                contentTypeName = _uiState.value.contentTypeName,
                videoUrl = _uiState.value.videoUrl
            )

            tutorialRepository.createTutorial(_moduleId, tutorialRequest).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { response ->
                        _uiState.update { it.copy(loading = false, tutorialUploadResult = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Tutorial Creation",
                            notificationText = "You have successfully uploaded a Tutorial Link",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, tutorialUploadResult = null) }
                    }
                )
            }
        }
    }

    fun clearTutorialUploadResult(){
        _uiState.update { it.copy(tutorialUploadResult = null) }
    }
}

data class CreateTutorialUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val tutorialUploadResult: Result<Unit>? = null,
    val activityName: String = "",
    val activityDescription: String = "",
    val unlockDateTime: LocalDateTime? = null,
    val deadlineDateTime: LocalDateTime? = null,
    val contentTypeName: String = "VIDEO",
    val videoUrl: String = "",
)