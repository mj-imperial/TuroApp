package com.example.turoapp.viewmodels.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.turoapp.models.TermsAndConditionsUiState
import com.example.turoapp.repositories.Result
import com.example.turoapp.repositories.UserRepository
import com.example.turoapp.viewmodels.higherorderfunctions.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _uiState = MutableStateFlow(TermsAndConditionsUiState())
    val uiState: StateFlow<TermsAndConditionsUiState> = _uiState.asStateFlow()

    init {
        checkAgreementStatus()
    }

    fun checkAgreementStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            userRepository.getTermsAgreementStatus(_userId.value).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = { agreementStatus ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            agreementStatus = agreementStatus
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            errorMessage = error
                        )
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    fun updateAgreementStatus(agreed: Boolean) {
        _uiState.value = _uiState.value.copy(agreementStatus = agreed)
    }

    fun saveAgreement() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null, isAgreementSaved = null)
            userRepository.setTermsAgreementStatus(_userId.value, _uiState.value.agreementStatus).collect { result ->
                handleResult(
                    result = result,
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            isAgreementSaved = Result.Success(Unit)
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            errorMessage = error,
                            isAgreementSaved = result
                        )
                    },
                    onLoading = {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                )
            }
        }
    }

    fun resetSaveAgreementStatus() {
        _uiState.value = _uiState.value.copy(isAgreementSaved = null)
    }
}