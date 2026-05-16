package com.abplus.k2a2recorder.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.k2a2recorder.model.BloodPressure
import com.abplus.k2a2recorder.ui.components.BloodPressureInputMode
import com.abplus.k2a2recorder.ui.components.BloodPressureList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureListScreen(
    uiState: BloodPressureListUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onInputSystolicChange: (Int?) -> Unit = {},
    onInputDiastolicChange: (Int?) -> Unit = {},
    onInputMicClick: () -> Unit = {},
    onInputCancelClick: () -> Unit = {},
    onInputSaveClick: () -> Unit = {},
    onEditClick: (BloodPressure) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "血圧記録") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    titleContentColor = MaterialTheme.colorScheme.onError
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add blood pressure"
                )
            }
        }
    ) { innerPadding ->
        BloodPressureListScreenBody(
            uiState = uiState,
            contentPadding = innerPadding,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
            onInputSystolicChange = onInputSystolicChange,
            onInputDiastolicChange = onInputDiastolicChange,
            onInputMicClick = onInputMicClick,
            onInputCancelClick = onInputCancelClick,
            onInputSaveClick = onInputSaveClick,
            onEditClick = onEditClick
        )
    }
}

@Composable
private fun BloodPressureListScreenBody(
    uiState: BloodPressureListUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
    onInputSystolicChange: (Int?) -> Unit = {},
    onInputDiastolicChange: (Int?) -> Unit = {},
    onInputMicClick: () -> Unit = {},
    onInputCancelClick: () -> Unit = {},
    onInputSaveClick: () -> Unit = {},
    onEditClick: (BloodPressure) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val packageName = LocalContext.current.packageName

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        uiState.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BloodPressureList(
            bloodPressures = uiState.bloodPressures,
            ownPackageName = packageName,
            inputMode = uiState.inputMode,
            inputSystolic = uiState.inputSystolic,
            inputDiastolic = uiState.inputDiastolic,
            isRefreshing = uiState.isLoading,
            isLoadingMore = uiState.isLoadingMore,
            modifier = Modifier.fillMaxSize(),
            onRefresh = onRefresh,
            onInputSystolicChange = onInputSystolicChange,
            onInputDiastolicChange = onInputDiastolicChange,
            onInputMicClick = onInputMicClick,
            onInputCancelClick = onInputCancelClick,
            onInputSaveClick = onInputSaveClick,
            onEditClick = onEditClick,
            onLoadMore = onLoadMore
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureListScreenPreview() {
    MaterialTheme {
        BloodPressureListScreen(
            uiState = BloodPressureListUiState(
                inputMode = BloodPressureInputMode.ADD,
                bloodPressures = listOf(
                    BloodPressure.newInstance(1_717_200_000_000, 128, 82),
                    BloodPressure.newInstance(1_717_286_400_000, 121, 78),
                    BloodPressure.newInstance(1_717_372_800_000, 134, 86)
                )
            )
        )
    }
}
