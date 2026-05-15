package com.abplus.k2a2recorder.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.abplus.k2a2recorder.model.BloodPressure
import com.abplus.k2a2recorder.ui.screens.BloodPressureListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
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
    }
}
