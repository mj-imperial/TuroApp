package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.models.ModuleActivityResponse
import com.example.turomobileapp.repositories.ActivityRepository
import com.example.turomobileapp.repositories.ModuleRepository
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.ui.notifications.TuroNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityActionsViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
    private val activityRepository: ActivityRepository,
    private val savedStateHandle: SavedStateHandle,
    private val notificationService: TuroNotificationService
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])

    private val _uiState = MutableStateFlow(ActivityActionsUIState())
    val uiState: StateFlow<ActivityActionsUIState> = _uiState.asStateFlow()

    init {
        getActivitiesInModule()
    }

    fun getActivitiesInModule(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            moduleRepository.getActivitiesForModule(_moduleId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { activities ->
                        _uiState.update { it.copy(loading = false, activities = activities) }
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err) }
                    }
                )
            }
        }
    }

    fun deleteActivity(activityId: String){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            activityRepository.deleteActivity(activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { success ->
                        _uiState.update { it.copy(loading = false, deleteStatusResult = Result.Success(Unit)) }
                        notificationService.showNotification(
                            notificationTitle = "Activity Deleted",
                            notificationText = "You have deleted an activity.",
                            route = "teacher_create_edit_activity_in_module/${_moduleId}"
                        )
                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, deleteStatusResult = null, errorMessage = err) }
                    },
                )
            }
        }
    }

    fun resetDeleteStatusResult(){
        _uiState.update { it.copy(deleteStatusResult = null) }
    }
}

data class ActivityActionsUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val activities: List<ModuleActivityResponse> = emptyList(),
    val deleteStatusResult: Result<Unit>? = null
)