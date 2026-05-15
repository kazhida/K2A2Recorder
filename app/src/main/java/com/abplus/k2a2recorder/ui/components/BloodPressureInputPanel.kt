package com.abplus.k2a2recorder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun BloodPressureInputPanel(
    systolic: Int,
    diastolic: Int,
    modifier: Modifier = Modifier,
    systolicRange: IntRange = 70..250,
    diastolicRange: IntRange = 40..150,
    onSystolicChange: (Int) -> Unit = {},
    onDiastolicChange: (Int) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),
                            Color(0xFFFFFFFF),
                        )
                    )
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BloodPressureDrumPicker(
                    label = "SYS",
                    value = systolic,
                    values = systolicRange.toList(),
                    modifier = Modifier.weight(1f),
                    onValueChange = onSystolicChange
                )
                BloodPressureDrumPicker(
                    label = "DIA",
                    value = diastolic,
                    values = diastolicRange.toList(),
                    modifier = Modifier.weight(1f),
                    onValueChange = onDiastolicChange
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0x00FFFFFF),
                        )
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BloodPressureDrumPicker(
    label: String,
    value: Int,
    values: List<Int>,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {}
) {
    val selectedIndex = values.indexOf(value).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(value, values) {
        val nextIndex = values.indexOf(value)
        if (nextIndex >= 0 && nextIndex != listState.firstVisibleItemIndex) {
            listState.animateScrollToItem(nextIndex)
        }
    }

    LaunchedEffect(listState, values) {
        snapshotFlow { listState.centerVisibleIndex() }
            .map { index -> values.getOrNull(index) }
            .distinctUntilChanged()
            .collect { selectedValue ->
                if (selectedValue != null) {
                    onValueChange(selectedValue)
                }
            }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    itemsIndexed(values) { index, pickerValue ->
                        val isSelected = index == listState.centerVisibleIndex()

                        Text(
                            text = pickerValue.toString(),
                            modifier = Modifier
                                .height(56.dp)
                                .alpha(if (isSelected) 1f else 0.45f)
                                .padding(vertical = 10.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    content = {}
                )
            }
        }

        Text(
            text = "mmHg",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun LazyListState.centerVisibleIndex(): Int {
    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return firstVisibleItemIndex

    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    return visibleItems.minBy { item ->
        kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
    }.index
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureInputPanelPreview() {
    MaterialTheme {
        BloodPressureInputPanel(
            systolic = 128,
            diastolic = 82,
            modifier = Modifier.padding(16.dp)
        )
    }
}
