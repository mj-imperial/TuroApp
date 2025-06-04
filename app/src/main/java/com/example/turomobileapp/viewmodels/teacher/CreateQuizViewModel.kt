package com.example.turomobileapp.viewmodels.teacher

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.turomobileapp.repositories.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _moduleId: String = checkNotNull(savedStateHandle["moduleId"])


}

data class CreateQuizUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val quizTitle: String = "",
    val quizType: String = "",
    val createQuizStatus: Result<Unit>? = null
)

