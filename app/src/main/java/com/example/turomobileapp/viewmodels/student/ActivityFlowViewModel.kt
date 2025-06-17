package com.example.turomobileapp.viewmodels.student

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.turomobileapp.models.ModuleActivityResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ActivityFlowUIState(
    val activityList: List<ModuleActivityResponse> = emptyList(),
    val currentActivityId: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ActivityFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ActivityFlowUIState(
            activityList = savedStateHandle.get("activityList") ?: emptyList(),
            currentActivityId = savedStateHandle.get("currentActivityId")
        )
    )
    val uiState: StateFlow<ActivityFlowUIState> = _uiState.asStateFlow()

    fun setActivityList(list: List<ModuleActivityResponse>) {
        _uiState.update { it.copy(activityList = list) }
    }

    fun setCurrentActivityId(activityId: String) {
        _uiState.update { it.copy(currentActivityId = activityId) }
    }

    fun goToPrevious(): ModuleActivityResponse? {
        val list = _uiState.value.activityList.filter {
            it.isUnlocked && !it.isLockedDate
        }
        val index = list.indexOfFirst { it.activityId == _uiState.value.currentActivityId }
        return if (index > 0) list[index - 1] else null
    }

    fun goToNext(): ModuleActivityResponse? {
        val list = _uiState.value.activityList.filter {
            it.isUnlocked && !it.isLockedDate
        }
        val index = list.indexOfFirst { it.activityId == _uiState.value.currentActivityId }
        return if (index >= 0 && index < list.lastIndex) list[index + 1] else null
    }
}

