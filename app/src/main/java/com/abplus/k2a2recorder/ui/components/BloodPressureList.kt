package com.abplus.k2a2recorder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abplus.k2a2recorder.model.BloodPressure
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun BloodPressureListBox(
    bloodPressures: List<BloodPressure>,
    modifier: Modifier = Modifier,
    isLoadingMore: Boolean = false,
    onBloodPressureClick: (BloodPressure) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, bloodPressures.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { lastVisibleIndex ->
                val lastDataIndex = bloodPressures.lastIndex
                lastVisibleIndex != null && lastDataIndex >= 0 && lastVisibleIndex >= lastDataIndex - 8
            }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface
    ) {
        if (bloodPressures.isEmpty()) {
            Text(
                text = "No blood pressure records",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = bloodPressures,
                    key = { index, bloodPressure ->
                        "${bloodPressure.id}-${bloodPressure.timeInMillis}-$index"
                    }
                ) { _, bloodPressure ->
                    BloodPressureItem(
                        bloodPressure = bloodPressure,
                        onClick = { onBloodPressureClick(bloodPressure) }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Text(
                            text = "Loading more blood pressure records...",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        BloodPressureInputPanel(
            systolic = 150,
            diastolic = 100
        )
    }
}

@Composable
fun BloodPressureItem(
    bloodPressure: BloodPressure,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = bloodPressure.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = bloodPressure.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BloodPressureValue(
                    label = "SYS",
                    value = bloodPressure.systolic
                )
                Text(
                    text = "/",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BloodPressureValue(
                    label = "DIA",
                    value = bloodPressure.diastolic
                )
            }
        }
    }
}

@Composable
private fun BloodPressureValue(
    label: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureListBoxPreview() {
    MaterialTheme {
        BloodPressureListBox(
            bloodPressures = listOf(
                BloodPressure.newInstance(1_717_200_000_000, 128, 82),
                BloodPressure.newInstance(1_717_286_400_000, 121, 78),
                BloodPressure.newInstance(1_717_372_800_000, 134, 86)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureItemPreview() {
    MaterialTheme {
        BloodPressureItem(
            bloodPressure = BloodPressure.newInstance(1_717_200_000_000, 128, 82),
            modifier = Modifier.padding(16.dp)
        )
    }
}
