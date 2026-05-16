package com.abplus.k2a2recorder.health

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.response.ReadRecordsResponse
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Pressure
import com.abplus.k2a2recorder.model.BloodPressure
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    val availabilityStatus: Int
        get() = HealthConnectClient.getSdkStatus(context)

    val isAvailable: Boolean
        get() = availabilityStatus == HealthConnectClient.SDK_AVAILABLE

    val permissionContract: ActivityResultContract<Set<String>, Set<String>>
        get() = PermissionController.createRequestPermissionResultContract()

    private val client: HealthConnectClient
        get() = HealthConnectClient.getOrCreate(context)

    suspend fun hasBloodPressurePermissions(): Boolean {
        if (!isAvailable) return false

        return client.permissionController
            .getGrantedPermissions()
            .containsAll(BLOOD_PRESSURE_PERMISSIONS)
    }

    suspend fun hasReadBloodPressurePermission(): Boolean {
        if (!isAvailable) return false

        return client.permissionController
            .getGrantedPermissions()
            .contains(READ_BLOOD_PRESSURE_PERMISSION)
    }

    suspend fun writeBloodPressure(bloodPressure: BloodPressure) {
        client.insertRecords(listOf(bloodPressure.toHealthConnectRecord()))
    }

    suspend fun readBloodPressures(startTime: Instant, endTime: Instant): List<BloodPressure> {
        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        return response.records.map { it.toBloodPressure() }
    }

    suspend fun readLatestBloodPressures(limit: Int = 50): List<BloodPressure> {
        return readLatestBloodPressuresPage(limit = limit).bloodPressures
    }

    suspend fun readLatestBloodPressuresPage(
        limit: Int = 50,
        pageToken: String? = null
    ): BloodPressurePage {
        val response: ReadRecordsResponse<BloodPressureRecord> = client.readRecords(
            ReadRecordsRequest(
                recordType = BloodPressureRecord::class,
                timeRangeFilter = TimeRangeFilter.before(Instant.now()),
                ascendingOrder = false,
                pageSize = limit,
                pageToken = pageToken
            )
        )

        return BloodPressurePage(
            bloodPressures = response.records.map { it.toBloodPressure() },
            nextPageToken = response.pageToken
        )
    }

    private fun BloodPressure.toHealthConnectRecord(): BloodPressureRecord {
        val instant = Instant.ofEpochMilli(timeInMillis)
        val zoneOffset = ZoneId.systemDefault().rules.getOffset(instant)

        return BloodPressureRecord(
            time = instant,
            zoneOffset = zoneOffset,
            metadata = Metadata.manualEntry(),
            systolic = Pressure.millimetersOfMercury(systolic.toDouble()),
            diastolic = Pressure.millimetersOfMercury(diastolic.toDouble())
        )
    }

    private fun BloodPressureRecord.toBloodPressure(): BloodPressure =
        BloodPressure.newInstance(
            dateTime = time.toEpochMilli(),
            systolic = systolic.inMillimetersOfMercury.toInt(),
            diastolic = diastolic.inMillimetersOfMercury.toInt(),
            id = metadata.id
        )

    companion object {
        val READ_BLOOD_PRESSURE_PERMISSION: String =
            HealthPermission.getReadPermission(BloodPressureRecord::class)

        val READ_BLOOD_PRESSURE_PERMISSIONS: Set<String> = setOf(
            READ_BLOOD_PRESSURE_PERMISSION
        )

        val BLOOD_PRESSURE_PERMISSIONS: Set<String> = setOf(
            READ_BLOOD_PRESSURE_PERMISSION,
            HealthPermission.getWritePermission(BloodPressureRecord::class)
        )
    }
}

data class BloodPressurePage(
    val bloodPressures: List<BloodPressure>,
    val nextPageToken: String?
)
