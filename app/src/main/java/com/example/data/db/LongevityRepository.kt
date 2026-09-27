package com.example.data.db

import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import kotlinx.coroutines.flow.Flow

class LongevityRepository(private val dao: LongevityRecordDao) {
    val allRecords: Flow<List<LongevityRecordEntity>> = dao.getAllRecords()
    val latestRecord: Flow<LongevityRecordEntity?> = dao.getLatestRecord()

    suspend fun saveAssessment(
        profile: BiomarkerProfile,
        result: BiologicalTwinResult,
        label: String = "Assessment"
    ): Long {
        val entity = LongevityRecordEntity(
            label = label,
            chronologicalAge = result.chronologicalAge,
            phenoAge = result.phenoAge,
            longevityReserveScore = result.longevityReserveScore,
            dominantAgingPhenotype = result.dominantAgingPhenotype,
            projectedIndependenceYears = result.projectedIndependenceYears,
            vascularScore = result.reserves.find { it.key == "vascular" }?.score ?: 75.0,
            metabolicScore = result.reserves.find { it.key == "metabolic" }?.score ?: 75.0,
            inflammatoryScore = result.reserves.find { it.key == "inflammatory" }?.score ?: 75.0,
            hormonalScore = result.reserves.find { it.key == "hormonal" }?.score ?: 75.0,
            muscleScore = result.reserves.find { it.key == "muscle" }?.score ?: 75.0,
            cognitiveScore = result.reserves.find { it.key == "cognitive" }?.score ?: 75.0,
            psychologicalScore = result.reserves.find { it.key == "psychological" }?.score ?: 75.0,
            glucoseMgDl = profile.glucoseMgDl,
            insulinUuMl = profile.insulinUuMl,
            hsCrpMgL = profile.hsCrpMgL,
            albuminGDl = profile.albuminGDl,
            creatinineUmolL = profile.creatinineUmolL,
            sbpMmHg = profile.sbpMmHg,
            dbpMmHg = profile.dbpMmHg,
            restingPulseBpm = profile.restingPulseBpm,
            chairStand30sReps = profile.chairStand30sReps,
            stroopMeanLatencyMs = profile.stroopMeanLatencyMs,
            phq9Score = profile.phq9Score,
            gad7Score = profile.gad7Score,
            psqiSleepScore = profile.psqiSleepScore
        )
        return dao.insertRecord(entity)
    }

    suspend fun deleteRecord(id: Long) = dao.deleteRecordById(id)
    suspend fun clearHistory() = dao.clearAll()
}
