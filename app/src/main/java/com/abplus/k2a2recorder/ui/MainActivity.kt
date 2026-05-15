package com.abplus.k2a2recorder.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.health.connect.client.PermissionController
import com.abplus.k2a2recorder.health.HealthConnectManager
import com.abplus.k2a2recorder.ui.screens.BloodPressureListScreen
import com.abplus.k2a2recorder.ui.screens.BloodPressureListViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MaterialTheme {
                BloodPressureListScreen(
                    uiState = uiState,
                    onLoadMore = viewModel::loadNextBloodPressures,
                    onAddClick = {}
                )
            }
        }

        if (healthConnectManager.isAvailable) {
            healthConnectPermissionLauncher.launch(
                HealthConnectManager.READ_BLOOD_PRESSURE_PERMISSIONS
            )
        } else {
            viewModel.loadLatestBloodPressures()
        }
    }
}
