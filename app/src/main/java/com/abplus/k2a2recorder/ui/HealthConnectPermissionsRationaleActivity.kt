package com.abplus.k2a2recorder.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class HealthConnectPermissionsRationaleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            TextView(this).apply {
                setPadding(48, 48, 48, 48)
                text = "Health Connect is used to read and write blood pressure records only when you grant permission."
            }
        )
    }
}
