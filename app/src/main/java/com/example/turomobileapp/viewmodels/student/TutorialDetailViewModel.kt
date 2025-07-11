package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.TutorialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialDetailViewModel @Inject constructor(
    private val tutorialRepository: TutorialRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _activityId: String = checkNotNull(savedStateHandle["activityId"])

    private val _uiState = MutableStateFlow(TutorialDetailUIState())
    val uiState: StateFlow<TutorialDetailUIState> = _uiState.asStateFlow()

    init {
        getTutorial()
    }

    fun getTutorial(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            tutorialRepository.getTutorial(_activityId).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { resp ->
                        _uiState.update { it.copy(
                            loading = false,
                            tutorialName = resp.activityName,
                            tutorialDescription = resp.activityDescription,
                            videoUrl = resp.videoUrl
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

data class TutorialDetailUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val tutorialName: String = "",
    val tutorialDescription: String = "",
    val videoUrl: String = ""
)