package com.abplus.k2a2recorder.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class BloodPressure(
    val id: String,
    val timeInMillis: Long,
    val systolic: Int,
    val diastolic: Int,
    val dataOriginPackageName: String
) {

    companion object {

        fun newInstance(
            dateTime: Long,
            systolic: Int,
            diastolic: Int,
            id: String = "",
            dataOriginPackageName: String = ""
        ): BloodPressure = BloodPressure(id, dateTime, systolic, diastolic, dataOriginPackageName)
    }

    private val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val calendar = Calendar.getInstance().also {
        it.timeInMillis = timeInMillis
    }

    val date: String get() = dateFormatter.format(calendar.time)
    val time: String get() = timeFormatter.format(calendar.time)
}
