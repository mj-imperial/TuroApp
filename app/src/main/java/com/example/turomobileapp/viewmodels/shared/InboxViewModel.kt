package com.example.turomobileapp.viewmodels.shared

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.turomobileapp.models.Message
import com.example.turomobileapp.repositories.MessageRepository
import com.example.turomobileapp.viewmodels.BaseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _userId = MutableStateFlow<String>(savedStateHandle["userId"] ?: "")
    val userId: StateFlow<String> = _userId.asStateFlow()


}

data class InboxUiState(
    override val loading: Boolean = false,
    override val errorMessage: String? = null,
    val message: List<Message> = emptyList(),
    val sortOrder: SortOrder? = null,
    val filterCriteria: FilterCriteria? = null,
    val PaginationInfo: PaginationInfo? = null
): BaseUiState(loading, errorMessage)

enum class SortOrder {
    DATE_DESCENDING,
    DATE_ASCENDING,
    SENDER_A_TO_Z,
    SENDER_Z_TO_A,
    SUBJECT_A_TO_Z,
    SUBJECT_Z_TO_A
}

data class FilterCriteria(
    val showUnread: Boolean = false,
    val showFlagged: Boolean = false,
    val sender: String? = null,
    val dateRange: DateRange? = null
)

data class DateRange(
    val startDate: Date,
    val endDate: Date
)

data class PaginationInfo(
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalItems: Int = 0,
    val totalPages: Int = 0
)