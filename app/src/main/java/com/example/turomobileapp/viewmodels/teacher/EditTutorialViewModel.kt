package com.example.turomobileapp.viewmodels.teacher

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.TutorialResponse
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
class EditTutorialViewModel @Inject constructor(
    private val tutorialRepository: TutorialRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])
    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(EditTutorialUIState())
    val uiState: StateFlow<EditTutorialUIState> = _uiState.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    init {
        getTutorial()
    }

    fun updateTutorialName(newTutorialName: String){
        _uiState.update { it.copy(tutorialName = newTutorialName) }
        _isFormValid.value = validateForm()
    }

    fun updateTutorialDescription(newTutorialDescription: String){
        _uiState.update { it.copy(originalTutorialDescription = newTutorialDescription) }
        _isFormValid.value = validateForm()
    }

    fun updateUnlockDate(newUnlockDate: LocalDateTime?){
        _uiState.update { it.copy(unlockDate = newUnlockDate) }
        _isFormValid.value = validateForm()
    }

    fun updateDeadlineDate(newDeadlineDate: LocalDateTime?){
        _uiState.update { it.copy(deadlineDate = newDeadlineDate) }
        _isFormValid.value = validateForm()
    }

    fun updateYoutubeLink(newYoutubeLink: String){
        _uiState.update { it.copy(youtubeLink = newYoutubeLink) }
        _isFormValid.value = validateForm()
    }

    fun validateForm(): Boolean{
        val state = _uiState.value

        if (state.youtubeLink == state.originalYoutubeLink) return false

        if (state.unlockDate == null) return false

        val deadline = state.deadlineDate

        deadline?.let { if (it.isBefore(state.unlockDate)) return false }

        return true
    }

    fun getTutorial(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            tutorialRepository.getTutorial(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { tutorial ->
                        _uiState.update { it.copy(
                            loading = false,
                            originalTutorialName = tutorial.activityName,
                            tutorialName = tutorial.activityName,
                            originalTutorialDescription = tutorial.activityDescription,
                            unlockDate = tutorial.unlockDate,
                            deadlineDate = tutorial.deadlineDate,
                            originalYoutubeLink = tutorial.videoUrl,
                            youtubeLink = tutorial.videoUrl
                        ) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun saveTutorial(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            val state = _uiState.value

            val request = TutorialResponse(
                activityName = state.tutorialName,
                activityDescription = state.originalTutorialDescription,
                unlockDate = state.unlockDate,
                deadlineDate = state.deadlineDate,
                videoUrl = state.youtubeLink
            )

            tutorialRepository.updateTutorial(_activityId, _moduleId, request).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, editTutorialStatus = Result.Success(Unit)) }

                        notificationService.showNotification(
                            notificationTitle = "Edited Tutorial",
                            notificationText = "Tutorial ${state.tutorialName} successfully edited",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, editTutorialStatus = null, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun clearTutorialStatus(){
        _uiState.update { it.copy(editTutorialStatus = null) }
    }
}

data class EditTutorialUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val editTutorialStatus: Result<Unit>? = null,
    val originalTutorialName: String = "",
    val tutorialName: String = "",
    val originalTutorialDescription: String = "",
    val unlockDate: LocalDateTime? = null,
    val deadlineDate: LocalDateTime? = null,
    val originalYoutubeLink: String = "",
    val youtubeLink: String = ""
)