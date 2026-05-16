package com.abplus.k2a2recorder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun BloodPressureInputPanel(
    systolic: Int,
    diastolic: Int,
    inputMode: BloodPressureInputMode,
    modifier: Modifier = Modifier,
    systolicRange: IntRange = 70..250,
    diastolicRange: IntRange = 40..150,
    onSystolicChange: (Int) -> Unit = {},
    onDiastolicChange: (Int) -> Unit = {},
    onMicClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val saveButtonText = when (inputMode) {
        BloodPressureInputMode.EDIT -> "更新"
        BloodPressureInputMode.ADD,
        BloodPressureInputMode.NORMAL -> "保存"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onCancelClick)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x66FFFFFF),
                            Color(0xAAFFFFFF),
                            Color(0xDDFFFFFF),
                            Color(0xFFFFFFFF),
                        )
                    )
                )
            )
            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White).weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BloodPressureTextField(
                        label = "最高血圧",
                        value = systolic,
                        valueRange = systolicRange,
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        onValueChange = onSystolicChange
                    )
                    Text("/", modifier = Modifier.align(Alignment.CenterVertically))
                    BloodPressureTextField(
                        label = "最低血圧",
                        value = diastolic,
                        valueRange = diastolicRange,
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        onValueChange = onDiastolicChange
                    )
                    Text("mmHg", modifier = Modifier.align(Alignment.CenterVertically))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMicClick) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "音声入力"
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancelClick) {
                        Text(text = "キャンセル")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = onSaveClick) {
                        Text(text = saveButtonText)
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onCancelClick)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xDDFFFFFF),
                            Color(0xAAFFFFFF),
                            Color(0x66FFFFFF),
                        )
                    )
                )
            )
        }
    }
}

@Composable
private fun BloodPressureTextField(
    label: String,
    value: Int,
    valueRange: IntRange,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {}
) {
    var text by rememberSaveable(label) { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        if (text.toIntOrNull() != value) {
            text = value.toString()
        }
    }

    val parsedValue = text.toIntOrNull()
    val isError = parsedValue != null && parsedValue !in valueRange

    OutlinedTextField(
        value = text,
        onValueChange = { nextText ->
            val filteredText = nextText
                .filter { it.isDigit() }
                .take(valueRange.last.toString().length)
            text = filteredText

            val nextValue = filteredText.toIntOrNull()
            if (nextValue != null && nextValue in valueRange) {
                onValueChange(nextValue)
            }
        },
        label = { Text(text = label) },
        modifier = modifier.width(120.dp),
        textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.End),
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true)
@Composable
private fun BloodPressureInputPanelPreview() {
    MaterialTheme {
        BloodPressureInputPanel(
            systolic = 128,
            diastolic = 82,
            inputMode = BloodPressureInputMode.ADD,
            modifier = Modifier.padding(16.dp)
        )
    }
}
