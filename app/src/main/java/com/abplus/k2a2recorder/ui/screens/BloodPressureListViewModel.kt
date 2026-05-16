package com.abplus.k2a2recorder.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abplus.k2a2recorder.health.HealthConnectManager
import com.abplus.k2a2recorder.model.BloodPressure
import com.abplus.k2a2recorder.ui.components.BloodPressureInputMode
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

    fun onAddClick() {
        _uiState.update {
            it.copy(
                inputMode = BloodPressureInputMode.ADD,
                editingBloodPressure = null
            )
        }
    }

    fun showEditInput(bloodPressure: BloodPressure) {
        _uiState.update {
            it.copy(
                inputMode = BloodPressureInputMode.EDIT,
                inputSystolic = bloodPressure.systolic,
                inputDiastolic = bloodPressure.diastolic,
                editingBloodPressure = bloodPressure
            )
        }
    }

    fun hideInput() {
        _uiState.update {
            it.copy(
                inputMode = BloodPressureInputMode.NORMAL,
                editingBloodPressure = null
            )
        }
    }

    fun updateInputSystolic(systolic: Int) {
        _uiState.update { it.copy(inputSystolic = systolic) }
    }

    fun updateInputDiastolic(diastolic: Int) {
        _uiState.update { it.copy(inputDiastolic = diastolic) }
    }

    fun updateInputBloodPressure(systolic: Int, diastolic: Int) {
        _uiState.update {
            it.copy(
                inputSystolic = systolic,
                inputDiastolic = diastolic
            )
        }
    }

    fun saveInputBloodPressure() {
        val state = _uiState.value

        if (state.inputMode == BloodPressureInputMode.NORMAL) {
            return
        }

        viewModelScope.launch {
            if (!healthConnectManager.isAvailable) {
                _uiState.update { it.copy(message = "Health Connect is not available.") }
                return@launch
            }

            if (!healthConnectManager.hasBloodPressurePermissions()) {
                _uiState.update { it.copy(message = "Blood pressure read/write permission is required.") }
                return@launch
            }

            val bloodPressure = when (state.inputMode) {
                BloodPressureInputMode.ADD -> BloodPressure.newInstance(
                    dateTime = System.currentTimeMillis(),
                    systolic = state.inputSystolic,
                    diastolic = state.inputDiastolic
                )
                BloodPressureInputMode.EDIT -> state.editingBloodPressure?.copy(
                    systolic = state.inputSystolic,
                    diastolic = state.inputDiastolic
                )
                BloodPressureInputMode.NORMAL -> null
            } ?: return@launch

            runCatching {
                when (state.inputMode) {
                    BloodPressureInputMode.ADD -> healthConnectManager.writeBloodPressure(bloodPressure)
                    BloodPressureInputMode.EDIT -> healthConnectManager.updateBloodPressure(bloodPressure)
                    BloodPressureInputMode.NORMAL -> Unit
                }
                healthConnectManager.readLatestBloodPressuresPage(limit = PAGE_SIZE)
            }.onSuccess { page ->
                _uiState.update {
                    it.copy(
                        bloodPressures = page.bloodPressures,
                        inputMode = BloodPressureInputMode.NORMAL,
                        editingBloodPressure = null,
                        nextPageToken = page.nextPageToken,
                        canLoadMore = page.nextPageToken != null,
                        message = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(message = throwable.message ?: "Failed to save blood pressure record.")
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
    val inputMode: BloodPressureInputMode = BloodPressureInputMode.NORMAL,
    val inputSystolic: Int = 150,
    val inputDiastolic: Int = 100,
    val editingBloodPressure: BloodPressure? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true,
    val nextPageToken: String? = null,
    val message: String? = null
)
