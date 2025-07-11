package com.example.turomobileapp.viewmodels.student

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LongQuizListViewModel @Inject constructor(

): ViewModel(){

}

data class LongQuizListUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
)