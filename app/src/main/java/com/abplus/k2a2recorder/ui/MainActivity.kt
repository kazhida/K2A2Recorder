package com.abplus.k2a2recorder.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.health.connect.client.PermissionController
import com.abplus.k2a2recorder.health.HealthConnectManager
import com.abplus.k2a2recorder.ui.components.BloodPressureInputMode
import com.abplus.k2a2recorder.ui.screens.BloodPressureListScreen
import com.abplus.k2a2recorder.ui.screens.BloodPressureListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var healthConnectManager: HealthConnectManager

    private val viewModel: BloodPressureListViewModel by viewModels()

    private val healthConnectPermissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) {
        viewModel.loadLatestBloodPressures()
    }

    private val speechRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            return@registerForActivityResult
        }

        val inputMode = viewModel.uiState.value.inputMode != BloodPressureInputMode.NORMAL
        if (inputMode) {
            val recognizedText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.joinToString(separator = " ")
                .orEmpty()
            val values = recognizedText.extractIntegers()
            if (values.size >= 2) {
                viewModel.updateInputBloodPressure(
                    systolic = values[0],
                    diastolic = values[1]
                )
            }
            Log.d(TAG, "Recognized blood pressure input: $recognizedText")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MaterialTheme {
                BloodPressureListScreen(
                    uiState = uiState,
                    onRefresh = viewModel::loadLatestBloodPressures,
                    onLoadMore = viewModel::loadNextBloodPressures,
                    onInputSystolicChange = viewModel::updateInputSystolic,
                    onInputDiastolicChange = viewModel::updateInputDiastolic,
                    onInputMicClick = ::startSpeechRecognition,
                    onInputCancelClick = viewModel::hideInput,
                    onInputSaveClick = viewModel::saveInputBloodPressure,
                    onEditClick = viewModel::onEditClick,
                    onAddClick = viewModel::onAddClick
                )
            }
        }

        if (healthConnectManager.isAvailable) {
            healthConnectPermissionLauncher.launch(
                HealthConnectManager.BLOOD_PRESSURE_PERMISSIONS
            )
        } else {
            viewModel.loadLatestBloodPressures()
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "血圧を入力")
        }

        try {
            speechRecognitionLauncher.launch(intent)
        } catch (exception: ActivityNotFoundException) {
            Log.w(TAG, "Speech recognition is not available.", exception)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

private fun String.extractIntegers(): List<Int> {
    val normalizedText = map { char ->
        when (char) {
            in '０'..'９' -> '0' + (char - '０')
            else -> char
        }
    }.joinToString(separator = "")

    return Regex("""\d+""")
        .findAll(normalizedText)
        .mapNotNull { it.value.toIntOrNull() }
        .toList()
}
