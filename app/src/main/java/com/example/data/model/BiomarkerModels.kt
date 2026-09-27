package com.example.data.model

/**
 * Complete set of biological inputs for the Digital Twin.
 * Defaults are initialized to standard clinical healthy population medians.
 */
data class BiomarkerProfile(
    val chronologicalAge: Double = 42.0,
    val isMale: Boolean = true,

    // Blood Biomarkers (Morgan Levine PhenoAge & Organ Reserves)
    val glucoseMgDl: Double = 92.0,            // mg/dL (70-99 optimal)
    val insulinUuMl: Double = 6.0,             // μU/mL (2-10 optimal)
    val hsCrpMgL: Double = 0.6,                // mg/L (<0.5 optimal)
    val albuminGDl: Double = 4.5,              // g/dL (4.0-5.0 optimal)
    val creatinineUmolL: Double = 78.0,        // μmol/L (60-95 optimal)
    val lymphocytePercent: Double = 32.0,      // % (20-40 optimal)
    val mcvFl: Double = 88.0,                  // fL (80-96 optimal)
    val rdwPercent: Double = 12.5,             // % (11.5-14.5 optimal)
    val alpUL: Double = 64.0,                  // U/L (44-120 optimal)
    val wbc103Ul: Double = 5.8,                // 10^3/μL (4.5-10.0 optimal)
    val platelets103Ul: Double = 240.0,        // 10^3/μL (150-400 optimal)
    val neutrophilsPercent: Double = 58.0,     // % (40-70 optimal)

    // Vitals & Cardiovascular Panel
    val sbpMmHg: Double = 118.0,               // mmHg (<120 optimal)
    val dbpMmHg: Double = 76.0,                // mmHg (<80 optimal)
    val restingPulseBpm: Double = 62.0,        // bpm (50-65 optimal)
    val totalCholesterolMgDl: Double = 175.0,  // mg/dL (<200)
    val hdlMgDl: Double = 58.0,                // mg/dL (>50 optimal)
    val triglyceridesMgDl: Double = 95.0,      // mg/dL (<150 optimal)

    // Anthropometrics
    val heightCm: Double = 176.0,
    val weightKg: Double = 72.0,
    val waistCircumferenceCm: Double = 81.0,

    // Hormonal & Micronutrient Panel
    val testosteroneNgDl: Double = 650.0,      // ng/dL (optimal male 500-900, female 20-70)
    val dheaSUgDl: Double = 280.0,             // μg/dL (optimal 200-450)
    val tshUiuMl: Double = 1.8,                // μIU/mL (optimal 1.0-2.5)
    val vitaminDNgMl: Double = 52.0,           // ng/mL (optimal 40-70)

    // Physical Functional Performance
    val chairStand30sReps: Int = 18,           // Reps in 30 seconds (>15 optimal)
    val walkingSpeed4mMps: Double = 1.25,      // m/s (>1.2 optimal)

    // Cognitive Performance
    val stroopMeanLatencyMs: Double = 540.0,   // ms (<600 optimal)
    val stroopAccuracyPercent: Double = 98.0,  // % (>95 optimal)

    // Psychological & Sleep
    val phq9Score: Int = 2,                    // 0-27 (0-4 minimal)
    val gad7Score: Int = 2,                    // 0-21 (0-4 minimal)
    val psqiSleepScore: Int = 3,               // 0-21 (<5 good sleep)

    // Health Connect Device Telemetry
    val dailySteps: Int = 8500,                // steps/day (optimal >8000)
    val activeCaloriesKcal: Double = 350.0,    // kcal/day
    val exerciseMinutesToday: Int = 30,        // minutes of active exercise
    val isHealthConnectSynced: Boolean = false,
    val healthConnectSyncTimestamp: Long? = null
) {
    val bmi: Double
        get() {
            val hM = heightCm / 100.0
            return if (hM > 0) weightKg / (hM * hM) else 22.5
        }

    val waistToHeightRatio: Double
        get() = if (heightCm > 0) waistCircumferenceCm / heightCm else 0.46
}

data class ReserveScoreItem(
    val name: String,
    val key: String,
    val score: Double, // 0 to 100
    val ageEstimate: Double? = null,
    val statusLabel: String,
    val metricSummary: String,
    val clinicalInsight: String,
    val dominantLeverageAction: String
)

data class BiologicalTwinResult(
    val chronologicalAge: Double,
    val phenoAge: Double,
    val ageDifference: Double, // phenoAge - chronologicalAge (negative is good)
    val paceOfAging: Double, // PhenoAge / ChronoAge
    val gompertzMortalityRisk: Double, // 10-year projected mortality probability
    val reserves: List<ReserveScoreItem>,
    val longevityReserveScore: Double, // Harmonic weighted mean of 7 reserves
    val dominantAgingPhenotype: String,
    val dominantPhenotypeDescription: String,
    val primaryRecommendation: String,
    val projectedIndependenceYears: Double,
    val dataConfidencePercent: Double
)

data class TrajectoryPoint(
    val year: Int, // 0, 5, 10, 20
    val chronoAge: Double,
    val scenarioABiologicalAge: Double, // Status Quo
    val scenarioBBiologicalAge: Double, // Nutrition (+3.1 yrs healthy)
    val scenarioCBiologicalAge: Double, // Strength (+4.8 yrs healthy)
    val scenarioDBiologicalAge: Double, // Sleep (+2.0 yrs healthy)
    val scenarioEBiologicalAge: Double  // Synergistic (+8.4 yrs healthy)
)

data class TrajectoryForecast(
    val baseChronoAge: Double,
    val baseBioAge: Double,
    val points: List<TrajectoryPoint>,
    val scenarioAGainYears: Double = 0.0,
    val scenarioBGainYears: Double = 3.1,
    val scenarioCGainYears: Double = 4.8,
    val scenarioDGainYears: Double = 2.0,
    val scenarioEGainYears: Double = 8.4
)

data class PresetProfile(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,
    val profile: BiomarkerProfile
)

// Stroop Interactive Test Trial
data class StroopWordItem(
    val displayedWord: String,
    val inkColorHex: Long, // Color the word is displayed in
    val inkColorName: String,
    val correctColorIndex: Int
)

data class StroopTrialResult(
    val stimulusIndex: Int,
    val latencyMs: Long,
    val isCorrect: Boolean
)

data class MorningLongevityBriefing(
    val headline: String,
    val biologicalAgeAnalysis: String,
    val rateOfAgingSummary: String,
    val topHealthPriority: String,
    val priorityRationale: String,
    val actionProtocol: String,
    val wearableTelemetryImpact: String,
    val keyBiomarkerToTarget: String,
    val isAiGenerated: Boolean = true,
    val generatedAtTimestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = "user_main",
    val name: String = "Dr. Alexander Vance",
    val title: String = "Longevity Biohacker",
    val email: String = "alexander.vance@lifemap.ai",
    val avatarEmoji: String = "🧬",
    val avatarBadgeTitle: String = "Epigenetic Pioneer",
    val chronologicalAge: Double = 42.0,
    val biologicalSex: String = "Male", // "Male", "Female", "Other"
    val bloodType: String = "O (Rh+)",
    val heightCm: Double = 176.0,
    val weightKg: Double = 72.0,
    val targetPhenoAgeDelta: Double = -6.0, // e.g. -6.0 years younger
    val primaryLongevityGoal: String = "Reverse Biological Age by 6 Years & Maximize VO2Max",
    val isMetricUnits: Boolean = true,
    val dailyStepGoal: Int = 10000,
    val activeStreakDays: Int = 28,
    val bio: String = "Targeting cellular autophagy, epigenetic clock deceleration, and vascular elasticity through precision gerontology.",
    val unlockedBadges: List<String> = listOf("🧬", "🫀", "⚡", "🧠", "🛡️", "🏆", "💎", "🔥")
)

data class LongevityEmojiItem(
    val id: String,
    val emoji: String,
    val title: String,
    val titleTr: String,
    val category: String,
    val categoryTr: String,
    val meaning: String,
    val meaningTr: String,
    val isUnlocked: Boolean = true
)

data class LongevityBadge(
    val id: String,
    val emoji: String,
    val title: String,
    val titleTr: String,
    val description: String,
    val descriptionTr: String,
    val requirement: String,
    val isUnlocked: Boolean = true
)


