package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AgreementTermsViewModel @Inject constructor(
    private val sessionManager: SessionManager
): ViewModel(){
    private val _userId: StateFlow<String?> = sessionManager.userId
}