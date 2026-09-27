package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Health Connect Availability Status
 */
enum class HealthConnectStatus {
    AVAILABLE,
    PROVIDER_UPDATE_REQUIRED,
    NOT_SUPPORTED,
    NOT_INSTALLED
}

/**
 * Aggregated telemetry fetched from Health Connect
 */
data class HealthConnectData(
    val restingHeartRateBpm: Double? = null,
    val avgHeartRateBpm: Double? = null,
    val minHeartRateBpm: Double? = null,
    val maxHeartRateBpm: Double? = null,
    val totalStepsToday: Long = 0L,
    val activeCaloriesKcal: Double = 0.0,
    val totalCaloriesKcal: Double = 0.0,
    val distanceMeters: Double = 0.0,
    val exerciseMinutesToday: Int = 0,
    val exerciseSessionCount: Int = 0,
    val isLiveSynced: Boolean = false,
    val syncTimestamp: Long = System.currentTimeMillis(),
    val sourceSummary: String = "Health Connect Engine"
)

class HealthConnectManager(private val context: Context) {

    private val tag = "HealthConnectManager"

    // Supported permissions set
    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class)
    )

    /**
     * Check if Health Connect is available on the current device
     */
    fun checkAvailability(): HealthConnectStatus {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            when (status) {
                HealthConnectClient.SDK_AVAILABLE -> HealthConnectStatus.AVAILABLE
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectStatus.PROVIDER_UPDATE_REQUIRED
                else -> HealthConnectStatus.NOT_INSTALLED
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to check Health Connect SDK status", e)
            HealthConnectStatus.NOT_SUPPORTED
        }
    }

    /**
     * Get or create the HealthConnectClient instance
     */
    fun getClient(): HealthConnectClient? {
        return try {
            if (checkAvailability() == HealthConnectStatus.AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error obtaining HealthConnectClient", e)
            null
        }
    }

    /**
     * Verify if all necessary read permissions are granted
     */
    suspend fun hasAllPermissions(): Boolean {
        val client = getClient() ?: return false
        return try {
            val granted = client.permissionController.getGrantedPermissions()
            permissions.all { it in granted }
        } catch (e: Exception) {
            Log.e(tag, "Error checking granted permissions", e)
            false
        }
    }

    /**
     * Read actual heart rate and activity metrics from Health Connect for the past 24 hours
     */
    suspend fun fetchHealthData(): HealthConnectData {
        val client = getClient()
        if (client == null) {
            Log.w(tag, "HealthConnectClient not available, returning empty data")
            return HealthConnectData()
        }

        return try {
            val now = Instant.now()
            val startTime = now.minus(24, ChronoUnit.HOURS)
            val timeRangeFilter = TimeRangeFilter.between(startTime, now)

            // 1. Fetch Resting Heart Rate records
            var restingHeartRate: Double? = null
            try {
                val rhrResponse = client.readRecords(
                    ReadRecordsRequest(
                        recordType = RestingHeartRateRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                val latestRhr = rhrResponse.records.maxByOrNull { it.time }
                if (latestRhr != null) {
                    restingHeartRate = latestRhr.beatsPerMinute.toDouble()
                }
            } catch (e: Exception) {
                Log.w(tag, "Error reading RestingHeartRateRecord", e)
            }

            // 2. Fetch Heart Rate Series (BPM values)
            var avgBpm: Double? = null
            var minBpm: Double? = null
            var maxBpm: Double? = null
            try {
                val hrResponse = client.readRecords(
                    ReadRecordsRequest(
                        recordType = HeartRateRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                val allSamples = hrResponse.records.flatMap { it.samples }
                if (allSamples.isNotEmpty()) {
                    val bpms = allSamples.map { it.beatsPerMinute.toDouble() }
                    avgBpm = bpms.average()
                    minBpm = bpms.minOrNull()
                    maxBpm = bpms.maxOrNull()

                    // If resting HR was null, approximate from lowest 10th percentile
                    if (restingHeartRate == null && bpms.size >= 5) {
                        val sorted = bpms.sorted()
                        restingHeartRate = sorted.take(maxOf(1, sorted.size / 10)).average()
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Error reading HeartRateRecord", e)
            }

            // 3. Aggregate Steps, Active Energy, Total Energy, and Distance
            var stepsTotal = 0L
            var activeCaloriesTotal = 0.0
            var totalCaloriesTotal = 0.0
            var distanceTotal = 0.0

            try {
                val aggregateResponse = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(
                            StepsRecord.COUNT_TOTAL,
                            ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
                            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
                            DistanceRecord.DISTANCE_TOTAL
                        ),
                        timeRangeFilter = timeRangeFilter
                    )
                )

                stepsTotal = aggregateResponse[StepsRecord.COUNT_TOTAL] ?: 0L
                activeCaloriesTotal = aggregateResponse[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0
                totalCaloriesTotal = aggregateResponse[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
                distanceTotal = aggregateResponse[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
            } catch (e: Exception) {
                Log.w(tag, "Error aggregating activity metrics", e)
            }

            // 4. Fetch Exercise Sessions
            var exerciseSessionCount = 0
            var exerciseMinutes = 0
            try {
                val exerciseResponse = client.readRecords(
                    ReadRecordsRequest(
                        recordType = ExerciseSessionRecord::class,
                        timeRangeFilter = timeRangeFilter
                    )
                )
                exerciseSessionCount = exerciseResponse.records.size
                exerciseMinutes = exerciseResponse.records.sumOf {
                    ChronoUnit.MINUTES.between(it.startTime, it.endTime).toInt()
                }
            } catch (e: Exception) {
                Log.w(tag, "Error reading ExerciseSessionRecord", e)
            }

            HealthConnectData(
                restingHeartRateBpm = restingHeartRate,
                avgHeartRateBpm = avgBpm,
                minHeartRateBpm = minBpm,
                maxHeartRateBpm = maxBpm,
                totalStepsToday = stepsTotal,
                activeCaloriesKcal = activeCaloriesTotal,
                totalCaloriesKcal = totalCaloriesTotal,
                distanceMeters = distanceTotal,
                exerciseMinutesToday = exerciseMinutes,
                exerciseSessionCount = exerciseSessionCount,
                isLiveSynced = true,
                syncTimestamp = System.currentTimeMillis(),
                sourceSummary = "Health Connect Android API"
            )
        } catch (e: Exception) {
            Log.e(tag, "Fatal error querying Health Connect data", e)
            HealthConnectData(isLiveSynced = false)
        }
    }

    /**
     * Build an Intent to prompt the user to install or update Health Connect from the Google Play Store
     */
    fun createInstallIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding")
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
}
