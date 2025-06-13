package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import com.example.turomobileapp.repositories.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InboxDetailViewModel @Inject constructor(
    private val messageRepository: MessageRepository
): ViewModel(){

}