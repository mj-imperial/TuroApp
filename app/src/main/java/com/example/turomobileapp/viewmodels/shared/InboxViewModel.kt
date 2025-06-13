package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import com.example.turomobileapp.repositories.MessageRepository
import javax.inject.Inject

class CreateInboxViewModel @Inject constructor(
    private val messageRepository: MessageRepository
): ViewModel(){
    
}

data class CreateInboxUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null
)