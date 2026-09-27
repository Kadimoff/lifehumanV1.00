package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "longevity_records")
data class LongevityRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val label: String = "Assessment",
    
    // Core summary results
    val chronologicalAge: Double,
    val phenoAge: Double,
    val longevityReserveScore: Double,
    val dominantAgingPhenotype: String,
    val projectedIndependenceYears: Double,

    // 8 Reserve Scores
    val vascularScore: Double,
    val metabolicScore: Double,
    val inflammatoryScore: Double,
    val hormonalScore: Double,
    val muscleScore: Double,
    val cognitiveScore: Double,
    val psychologicalScore: Double,

    // Biomarker inputs snapshot
    val glucoseMgDl: Double,
    val insulinUuMl: Double,
    val hsCrpMgL: Double,
    val albuminGDl: Double,
    val creatinineUmolL: Double,
    val sbpMmHg: Double,
    val dbpMmHg: Double,
    val restingPulseBpm: Double,
    val chairStand30sReps: Int,
    val stroopMeanLatencyMs: Double,
    val phq9Score: Int,
    val gad7Score: Int,
    val psqiSleepScore: Int
)
