package com.abplus.k2a2recorder.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

enum class BloodPressureInputMode {
    NORMAL,
    ADD,
    EDIT
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BloodPressureList(
    bloodPressures: List<BloodPressure>,
    ownPackageName: String,
    modifier: Modifier = Modifier,
    inputMode: BloodPressureInputMode = BloodPressureInputMode.NORMAL,
    inputSystolic: Int? = 150,
    inputDiastolic: Int? = 100,
    isRefreshing: Boolean = false,
    isLoadingMore: Boolean = false,
    onRefresh: () -> Unit = {},
    onInputSystolicChange: (Int?) -> Unit = {},
    onInputDiastolicChange: (Int?) -> Unit = {},
    onInputMicClick: () -> Unit = {},
    onInputCancelClick: () -> Unit = {},
    onInputSaveClick: () -> Unit = {},
    onEditClick: (BloodPressure) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
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
                            isOwnRecord = bloodPressure.dataOriginPackageName == ownPackageName,
                            isEditEnabled = inputMode == BloodPressureInputMode.NORMAL,
                            onEditClick = { onEditClick(bloodPressure) }
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
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        if (inputMode == BloodPressureInputMode.ADD || inputMode == BloodPressureInputMode.EDIT) {
            BloodPressureInputPanel(
                systolic = inputSystolic,
                diastolic = inputDiastolic,
                inputMode = inputMode,
                onSystolicChange = onInputSystolicChange,
                onDiastolicChange = onInputDiastolicChange,
                onMicClick = onInputMicClick,
                onCancelClick = onInputCancelClick,
                onSaveClick = onInputSaveClick
            )
        }
    }
}

@Composable
fun BloodPressureItem(
    bloodPressure: BloodPressure,
    modifier: Modifier = Modifier,
    isOwnRecord: Boolean = false,
    isEditEnabled: Boolean = true,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isOwnRecord) {
                    IconButton(
                        onClick = onEditClick,
                        enabled = isEditEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "編集",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = bloodPressure.systolic.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "/",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = bloodPressure.diastolic.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "mmHg",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureListPreview() {
    MaterialTheme {
        BloodPressureList(
            bloodPressures = listOf(
                BloodPressure.newInstance(1_717_200_000_000, 128, 82),
                BloodPressure.newInstance(1_717_286_400_000, 121, 78),
                BloodPressure.newInstance(1_717_372_800_000, 134, 86)
            ),
            ownPackageName = "com.abplus.k2a2recorder",
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
