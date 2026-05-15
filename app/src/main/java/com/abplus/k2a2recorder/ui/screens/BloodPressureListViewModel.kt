package com.abplus.k2a2recorder.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abplus.k2a2recorder.health.HealthConnectManager
import com.abplus.k2a2recorder.model.BloodPressure
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BloodPressureListViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BloodPressureListUiState())
    val uiState: StateFlow<BloodPressureListUiState> = _uiState.asStateFlow()

    fun loadLatestBloodPressures() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isLoadingMore = false,
                    canLoadMore = true,
                    nextPageToken = null,
                    message = null
                )
            }

            if (!healthConnectManager.isAvailable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bloodPressures = emptyList(),
                        canLoadMore = false,
                        message = "Health Connect is not available."
                    )
                }
                return@launch
            }

            if (!healthConnectManager.hasReadBloodPressurePermission()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bloodPressures = emptyList(),
                        canLoadMore = false,
                        message = "Blood pressure permission is required."
                    )
                }
                return@launch
            }

            runCatching {
                healthConnectManager.readLatestBloodPressuresPage(limit = PAGE_SIZE)
            }.onSuccess { page ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bloodPressures = page.bloodPressures,
                        nextPageToken = page.nextPageToken,
                        canLoadMore = page.nextPageToken != null,
                        message = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        bloodPressures = emptyList(),
                        canLoadMore = false,
                        message = throwable.message ?: "Failed to load blood pressure records."
                    )
                }
            }
        }
    }

    fun loadNextBloodPressures() {
        val state = _uiState.value
        val pageToken = state.nextPageToken

        if (state.isLoading || state.isLoadingMore || !state.canLoadMore || pageToken == null) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, message = null) }

            runCatching {
                healthConnectManager.readLatestBloodPressuresPage(
                    limit = PAGE_SIZE,
                    pageToken = pageToken
                )
            }.onSuccess { page ->
                _uiState.update {
                    it.copy(
                        bloodPressures = it.bloodPressures + page.bloodPressures,
                        isLoadingMore = false,
                        nextPageToken = page.nextPageToken,
                        canLoadMore = page.nextPageToken != null,
                        message = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        message = throwable.message ?: "Failed to load more blood pressure records."
                    )
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}

data class BloodPressureListUiState(
    val bloodPressures: List<BloodPressure> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val nextPageToken: String? = null,
    val message: String? = null
)
