package com.example.turomobileapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turomobileapp.helperfunctions.handleResult
import com.example.turomobileapp.repositories.Result
import com.example.turomobileapp.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgreementTermsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _hasAgreed = MutableStateFlow<Boolean>(savedStateHandle.get<Boolean>("agreedToTerms") != false)

    private val _uiState = MutableStateFlow(
        AgreementTermsUIState(hasAgreed = _hasAgreed.value)
    )
    val uiState: StateFlow<AgreementTermsUIState> = _uiState.asStateFlow()

    fun setAgreed(agreement: Boolean) {
        _uiState.update { it.copy(hasAgreed = agreement) }
    }

    fun saveAgreement(){
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }
            
            userRepository.setTermsAgreementStatus(_userId.value, _uiState.value.hasAgreed).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.update { it.copy(loading = false, isAgreementSaved = Result.Success(Unit)) }

                    },
                    onFailure = { err ->
                        _uiState.update { it.copy(loading = false, errorMessage = err, isAgreementSaved = null) }
                    },
                    onLoading = {
                        _uiState.update { it.copy(loading = true) }
                    }
                )
            }
        }
    }
}

data class AgreementTermsUIState(
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val hasAgreed: Boolean = false,
    val isAgreementSaved: Result<Unit>? = null
)