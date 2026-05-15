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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.k2a2recorder.model.BloodPressure
import com.abplus.k2a2recorder.ui.components.BloodPressureListBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureListScreen(
    uiState: BloodPressureListUiState,
    modifier: Modifier = Modifier,
    onBloodPressureClick: (BloodPressure) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Blood Pressure") }
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
            onBloodPressureClick = onBloodPressureClick,
            onLoadMore = onLoadMore
        )
    }
}

@Composable
private fun BloodPressureListScreenBody(
    uiState: BloodPressureListUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onBloodPressureClick: (BloodPressure) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        uiState.message?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.isLoading) {
            Text(
                text = "Loading blood pressure records...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BloodPressureListBox(
            bloodPressures = uiState.bloodPressures,
            isLoadingMore = uiState.isLoadingMore,
            modifier = Modifier.fillMaxSize(),
            onBloodPressureClick = onBloodPressureClick,
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
                bloodPressures = listOf(
                    BloodPressure.newInstance(1_717_200_000_000, 128, 82),
                    BloodPressure.newInstance(1_717_286_400_000, 121, 78),
                    BloodPressure.newInstance(1_717_372_800_000, 134, 86)
                )
            )
        )
    }
}
