package com.abplus.k2a2recorder.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
    bloodPressures: List<BloodPressure>,
    modifier: Modifier = Modifier,
    onBloodPressureClick: (BloodPressure) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Blood Pressure") }
            )
        }
    ) { innerPadding ->
        BloodPressureListScreenBody(
            bloodPressures = bloodPressures,
            contentPadding = innerPadding,
            onBloodPressureClick = onBloodPressureClick
        )
    }
}

@Composable
private fun BloodPressureListScreenBody(
    bloodPressures: List<BloodPressure>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onBloodPressureClick: (BloodPressure) -> Unit = {}
) {
    BloodPressureListBox(
        bloodPressures = bloodPressures,
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        onBloodPressureClick = onBloodPressureClick
    )
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureListScreenPreview() {
    MaterialTheme {
        BloodPressureListScreen(
            bloodPressures = listOf(
                BloodPressure.newInstance(1_717_200_000_000, 128, 82),
                BloodPressure.newInstance(1_717_286_400_000, 121, 78),
                BloodPressure.newInstance(1_717_372_800_000, 134, 86)
            )
        )
    }
}
