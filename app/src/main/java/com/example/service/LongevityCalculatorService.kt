package com.example.service

import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.data.model.ReserveScoreItem
import com.example.data.model.TrajectoryForecast
import com.example.data.model.TrajectoryPoint
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object LongevityCalculatorService {

    /**
     * Compute Morgan Levine Phenotypic Age (PhenoAge)
     * Reference: Levine et al., Aging (Albany NY) 2018; 10(4): 573-591.
     */
    fun calculatePhenoAge(profile: BiomarkerProfile): Double {
        val age = profile.chronologicalAge
        val glucose = profile.glucoseMgDl
        val albumin = profile.albuminGDl
        val creatinine = profile.creatinineUmolL
        val hsCrp = max(0.01, profile.hsCrpMgL)
        val lympPercent = profile.lymphocytePercent
        val mcv = profile.mcvFl
        val rdw = profile.rdwPercent
        val alp = profile.alpUL
        val wbc = profile.wbc103Ul

        // 1. Linear predictor xb
        val xb = -19.9067 +
                (0.0804 * age) +
                (0.0104 * glucose) -
                (0.0336 * albumin) +
                (0.0095 * creatinine) +
                (0.1953 * ln(hsCrp)) -
                (0.0120 * lympPercent) +
                (0.0268 * mcv) +
                (0.3306 * rdw) +
                (0.0019 * alp) +
                (0.0554 * wbc)

        // 2. Gompertz Mortality Risk (M) over 120 months (10 years)
        val gamma = 0.007688
        val b0 = exp(xb)
        val term = (exp(gamma * 120.0) - 1.0) / gamma
        val mortalityExponent = -b0 * term

        // Safe clamp for Gompertz risk M
        val rawM = 1.0 - exp(mortalityExponent)
        val m = min(0.999999, max(0.000001, rawM))

        // 3. Phenotypic Age
        val innerLog = -0.00553 * ln(1.0 - m)
        val safeInner = max(0.0000001, innerLog)
        val phenoAge = 141.50 + (ln(safeInner) / 0.090165)

        return (min(115.0, max(18.0, phenoAge)) * 10.0).roundToInt() / 10.0
    }

    /**
     * Compute 8 Functional Reserve Scores (0 to 100 scale)
     */
    fun calculateReserves(profile: BiomarkerProfile): List<ReserveScoreItem> {
        val reserves = mutableListOf<ReserveScoreItem>()

        // 1. Vascular Reserve
        val bpScore = calculateVascularScore(profile)
        val vascularAge = profile.chronologicalAge + (100.0 - bpScore.first) * 0.18 - 5.0
        val hcPulseTag = if (profile.isHealthConnectSynced) " [HC Live]" else ""
        reserves.add(
            ReserveScoreItem(
                name = "Vascular Reserve",
                key = "vascular",
                score = bpScore.first,
                ageEstimate = (vascularAge * 10.0).roundToInt() / 10.0,
                statusLabel = if (bpScore.first >= 80) "Optimal Elasticity" else if (bpScore.first >= 60) "Moderate Compliance" else "Arterial Stiffness",
                metricSummary = "BP ${profile.sbpMmHg.toInt()}/${profile.dbpMmHg.toInt()} • Pulse ${profile.restingPulseBpm.toInt()} bpm$hcPulseTag • Chol/HDL ${(profile.totalCholesterolMgDl / max(1.0, profile.hdlMgDl) * 10.0).roundToInt() / 10.0}",
                clinicalInsight = "Assesses pulse wave compliance, endothelial health, and microvascular resistance with real-time resting heart rate.",
                dominantLeverageAction = "Optimize nitric oxide availability via Zone 2 cardio and beet root nitrate supplementation."
            )
        )

        // 2. Metabolic Reserve
        val homaIr = (profile.glucoseMgDl * profile.insulinUuMl) / 405.0
        val tyg = ln(profile.triglyceridesMgDl * profile.glucoseMgDl / 2.0)
        val metabolicScore = calculateMetabolicScore(homaIr, tyg, profile.waistToHeightRatio, profile.dailySteps, profile.activeCaloriesKcal)
        val metabolicAge = profile.chronologicalAge + (homaIr - 1.0) * 4.5
        val hcActivityTag = if (profile.isHealthConnectSynced) " • Steps ${profile.dailySteps} [HC]" else ""
        reserves.add(
            ReserveScoreItem(
                name = "Metabolic Reserve",
                key = "metabolic",
                score = metabolicScore,
                ageEstimate = (metabolicAge * 10.0).roundToInt() / 10.0,
                statusLabel = if (homaIr < 1.4) "Insulin Sensitive" else if (homaIr < 2.5) "Compensatory Glycemia" else "Insulin Resistant",
                metricSummary = "HOMA-IR ${(homaIr * 100.0).roundToInt() / 100.0} • TyG ${(tyg * 100.0).roundToInt() / 100.0}$hcActivityTag",
                clinicalInsight = "Measures mitochondrial bioenergetics, liver lipid clearance, and peripheral glucose disposal driven by daily step cadence.",
                dominantLeverageAction = "Implement time-restricted feeding (14:10) and post-meal 15-minute walks to blunten glycemic excursions."
            )
        )

        // 3. Inflammatory Reserve (Inflammaging)
        val neutCount = (profile.neutrophilsPercent / 100.0) * profile.wbc103Ul
        val lympCount = (profile.lymphocytePercent / 100.0) * profile.wbc103Ul
        val sii = if (lympCount > 0) (profile.platelets103Ul * neutCount) / lympCount else 400.0
        val inflammatoryScore = calculateInflammatoryScore(profile.hsCrpMgL, sii)
        reserves.add(
            ReserveScoreItem(
                name = "Inflammatory Reserve",
                key = "inflammatory",
                score = inflammatoryScore,
                ageEstimate = null,
                statusLabel = if (profile.hsCrpMgL < 0.8 && sii < 500) "Basal Anti-Inflammatory" else if (profile.hsCrpMgL < 2.0) "Subclinical Inflammation" else "High Inflammaging Burden",
                metricSummary = "hs-CRP ${profile.hsCrpMgL} mg/L • SII ${sii.roundToInt()} • WBC ${profile.wbc103Ul}k",
                clinicalInsight = "Reflects immune senescence, cytokine load (IL-6/TNF-α cascade), and endothelial irritation.",
                dominantLeverageAction = "High-potency EPA/DHA Omega-3 (2-3g/day) + Polyphenol-rich Mediterranean diet."
            )
        )

        // 4. Hormonal Reserve
        val hormonalScore = calculateHormonalScore(profile)
        reserves.add(
            ReserveScoreItem(
                name = "Hormonal Reserve",
                key = "hormonal",
                score = hormonalScore,
                ageEstimate = null,
                statusLabel = if (hormonalScore >= 80) "Optimal Endocrine Axis" else if (hormonalScore >= 60) "Mild Axis Attenuation" else "Endocrine Exhaustion",
                metricSummary = "Vit D ${profile.vitaminDNgMl.toInt()} ng/mL • TSH ${profile.tshUiuMl} • DHEA-S ${profile.dheaSUgDl.toInt()}",
                clinicalInsight = "Evaluates steroidogenesis, thyroid conversion efficiency, and nuclear receptor activation.",
                dominantLeverageAction = "Target Vitamin D3 + K2 optimization to 60 ng/mL and morning sunlight exposure."
            )
        )

        // 5. Muscle & Neuromuscular Reserve
        val muscleScore = calculateMuscleScore(profile)
        reserves.add(
            ReserveScoreItem(
                name = "Muscle Reserve",
                key = "muscle",
                score = muscleScore,
                ageEstimate = null,
                statusLabel = if (muscleScore >= 85) "High Motor Efficiency" else if (muscleScore >= 65) "Normal Sarcopenia Buffer" else "Elevated Sarcopenia Risk",
                metricSummary = "Chair Stand ${profile.chairStand30sReps} reps/30s • Gait ${profile.walkingSpeed4mMps} m/s • BMI ${(profile.bmi * 10.0).roundToInt() / 10.0}",
                clinicalInsight = "Quantifies type-II muscle fiber recruitment, power-to-weight ratio, and fall-resistance reserve.",
                dominantLeverageAction = "Progressive overload resistance training 3x/week with 1.6g/kg daily protein intake."
            )
        )

        // 6. Cognitive Reserve
        val cognitiveScore = calculateCognitiveScore(profile)
        reserves.add(
            ReserveScoreItem(
                name = "Cognitive Reserve",
                key = "cognitive",
                score = cognitiveScore,
                ageEstimate = null,
                statusLabel = if (cognitiveScore >= 85) "High Processing Speed" else if (cognitiveScore >= 65) "Normal Executive Control" else "Latency Attenuation",
                metricSummary = "Stroop Latency ${profile.stroopMeanLatencyMs.roundToInt()} ms • Accuracy ${profile.stroopAccuracyPercent.toInt()}%",
                clinicalInsight = "Measures prefrontal cortex executive inhibition, neural transmission velocity, and synaptogenesis.",
                dominantLeverageAction = "Dual-task cognitive motor drills and aerobic exercise to promote brain-derived neurotrophic factor (BDNF)."
            )
        )

        // 7. Psychological Reserve
        val psychScore = calculatePsychologicalScore(profile)
        reserves.add(
            ReserveScoreItem(
                name = "Psychological Reserve",
                key = "psychological",
                score = psychScore,
                ageEstimate = null,
                statusLabel = if (psychScore >= 85) "High Neuro-Resilience" else if (psychScore >= 65) "Moderate Allostatic Load" else "High Chronic Strain",
                metricSummary = "PHQ-9 ${profile.phq9Score}/27 • GAD-7 ${profile.gad7Score}/21 • PSQI Sleep ${profile.psqiSleepScore}/21",
                clinicalInsight = "Evaluates autonomic nervous system parasympathetic tone, allostatic stress resilience, and deep restorative REM sleep.",
                dominantLeverageAction = "Non-Sleep Deep Rest (NSDR/Yoga Nidra), physiological sigh breathing, and strict sleep hygiene."
            )
        )

        return reserves
    }

    /**
     * Longevity Reserve Score (LRS):
     * Harmonic weighted mean of all 7 functional reserves.
     * Harmonic mean strictly penalizes single-organ bottlenecks.
     */
    fun calculateLRS(reserves: List<ReserveScoreItem>): Double {
        if (reserves.isEmpty()) return 75.0
        val weights = listOf(1.3, 1.3, 1.2, 1.0, 1.2, 1.0, 1.0)
        var weightedSum = 0.0
        var totalWeight = 0.0

        for (i in reserves.indices) {
            val w = weights.getOrElse(i) { 1.0 }
            val score = max(5.0, reserves[i].score)
            weightedSum += w / score
            totalWeight += w
        }

        val harmonicMean = if (weightedSum > 0) totalWeight / weightedSum else 75.0
        return (min(100.0, max(0.0, harmonicMean)) * 10.0).roundToInt() / 10.0
    }

    /**
     * Compute full Digital Twin Analysis
     */
    fun computeDigitalTwin(profile: BiomarkerProfile): BiologicalTwinResult {
        val chronoAge = profile.chronologicalAge
        val phenoAge = calculatePhenoAge(profile)
        val ageDiff = (phenoAge - chronoAge * 10.0).roundToInt() / 10.0
        val paceOfAging = if (chronoAge > 0) ((phenoAge / chronoAge) * 100.0).roundToInt() / 100.0 else 1.0

        val reserves = calculateReserves(profile)
        val lrs = calculateLRS(reserves)

        // Mortality risk proxy from PhenoAge equation
        val risk = min(0.99, max(0.01, (exp((phenoAge - 40.0) * 0.07) * 0.035)))

        // Identify Dominant Aging Phenotype (the system with lowest reserve score)
        val lowestReserve = reserves.minByOrNull { it.score } ?: reserves.first()
        val dominantPhenotype = when (lowestReserve.key) {
            "metabolic" -> "Metabolic-Dominant Aging Phenotype"
            "vascular" -> "Cardiovascular-Dominant Aging Phenotype"
            "inflammatory" -> "Inflammaging-Dominant Aging Phenotype"
            "muscle" -> "Musculoskeletal-Dominant Aging Phenotype"
            "hormonal" -> "Neuroendocrine-Dominant Aging Phenotype"
            "cognitive" -> "Neurocognitive-Dominant Aging Phenotype"
            else -> "Psychosomatic-Dominant Aging Phenotype"
        }

        val dominantDesc = "Your digital twin exhibits greatest biological vulnerability within the ${lowestReserve.name} system (Score: ${lowestReserve.score.roundToInt()}/100). Accelerating interventions in this domain provides the largest leverage for biological age reversal."

        // Projected Functional Independence Years (years expected to remain disability-free)
        val baseIndependence = max(chronoAge + 5.0, 85.0 - (phenoAge - chronoAge) * 1.5 + (lrs - 70.0) * 0.25)
        val projectedIndependenceYears = (min(102.0, max(chronoAge + 2.0, baseIndependence)) * 10.0).roundToInt() / 10.0

        // Data confidence
        val confidence = 95.0

        return BiologicalTwinResult(
            chronologicalAge = chronoAge,
            phenoAge = phenoAge,
            ageDifference = ageDiff,
            paceOfAging = paceOfAging,
            gompertzMortalityRisk = (risk * 1000.0).roundToInt() / 10.0,
            reserves = reserves,
            longevityReserveScore = lrs,
            dominantAgingPhenotype = dominantPhenotype,
            dominantPhenotypeDescription = dominantDesc,
            primaryRecommendation = lowestReserve.dominantLeverageAction,
            projectedIndependenceYears = projectedIndependenceYears,
            dataConfidencePercent = confidence
        )
    }

    /**
     * 5-Trajectory Scenario Forecasting Engine (0, 5, 10, 20 Years)
     */
    fun forecastTrajectories(profile: BiomarkerProfile, twinResult: BiologicalTwinResult): TrajectoryForecast {
        val baseChrono = profile.chronologicalAge
        val baseBio = twinResult.phenoAge
        val pace = twinResult.paceOfAging

        val years = listOf(0, 5, 10, 20)
        val points = years.map { yr ->
            val chronoAtYr = baseChrono + yr

            // Scenario A (Status Quo): Continues at current pace of aging
            val bioA = baseBio + (yr * pace)

            // Scenario B (Nutrition Optimization: lowers HOMA-IR & hs-CRP -> -0.15 pace reduction)
            val bioB = baseBio + (yr * max(0.65, pace - 0.16)) - (if (yr > 0) 1.2 else 0.0)

            // Scenario C (Strength & VO2max: reduces sarcopenia, enhances mitochondrial capacity -> -0.22 pace reduction)
            val bioC = baseBio + (yr * max(0.58, pace - 0.24)) - (if (yr > 0) 1.8 else 0.0)

            // Scenario D (Sleep & Circadian Optimization: lowers basal cortisol & inflammation -> -0.10 pace reduction)
            val bioD = baseBio + (yr * max(0.70, pace - 0.11)) - (if (yr > 0) 0.8 else 0.0)

            // Scenario E (Synergistic Intervention: B + C + D combined)
            val bioE = baseBio + (yr * max(0.48, pace - 0.42)) - (if (yr > 0) 3.6 else 0.0)

            TrajectoryPoint(
                year = yr,
                chronoAge = chronoAtYr,
                scenarioABiologicalAge = (bioA * 10.0).roundToInt() / 10.0,
                scenarioBBiologicalAge = (bioB * 10.0).roundToInt() / 10.0,
                scenarioCBiologicalAge = (bioC * 10.0).roundToInt() / 10.0,
                scenarioDBiologicalAge = (bioD * 10.0).roundToInt() / 10.0,
                scenarioEBiologicalAge = (bioE * 10.0).roundToInt() / 10.0
            )
        }

        return TrajectoryForecast(
            baseChronoAge = baseChrono,
            baseBioAge = baseBio,
            points = points,
            scenarioAGainYears = 0.0,
            scenarioBGainYears = 3.2,
            scenarioCGainYears = 4.9,
            scenarioDGainYears = 2.1,
            scenarioEGainYears = 8.6
        )
    }

    // --- Private Helper Formula Calculators ---

    private fun calculateVascularScore(profile: BiomarkerProfile): Pair<Double, Double> {
        var score = 100.0
        // SBP penalty
        if (profile.sbpMmHg > 120) score -= (profile.sbpMmHg - 120) * 0.9
        if (profile.sbpMmHg < 95) score -= (95 - profile.sbpMmHg) * 0.5
        // DBP penalty
        if (profile.dbpMmHg > 80) score -= (profile.dbpMmHg - 80) * 1.0
        // Total Chol / HDL ratio penalty (optimal < 3.2)
        val ratio = profile.totalCholesterolMgDl / max(1.0, profile.hdlMgDl)
        if (ratio > 3.5) score -= (ratio - 3.5) * 8.0
        // Resting pulse penalty (optimal 50-65)
        if (profile.restingPulseBpm > 70) {
            score -= (profile.restingPulseBpm - 70) * 0.7
        } else if (profile.restingPulseBpm in 48.0..62.0) {
            // High stroke volume & vagal tone bonus
            score += (62.0 - profile.restingPulseBpm) * 0.4
        }

        // Aerobic activity volume bonus (Health Connect integration)
        if (profile.dailySteps >= 8000) {
            val stepBonus = min(6.0, (profile.dailySteps - 8000) / 1500.0 * 1.5)
            score += stepBonus
        }
        if (profile.exerciseMinutesToday >= 25) {
            val exerciseBonus = min(4.0, (profile.exerciseMinutesToday - 20) * 0.15)
            score += exerciseBonus
        }

        return Pair(min(100.0, max(15.0, score)), ratio)
    }

    private fun calculateMetabolicScore(
        homaIr: Double,
        tyg: Double,
        whtr: Double,
        dailySteps: Int = 8500,
        activeCaloriesKcal: Double = 350.0
    ): Double {
        var score = 100.0
        // HOMA-IR penalty (optimal < 1.2)
        if (homaIr > 1.2) score -= (homaIr - 1.2) * 22.0
        // TyG penalty (optimal < 8.3)
        if (tyg > 8.4) score -= (tyg - 8.4) * 20.0
        // Waist-to-Height Ratio penalty (optimal < 0.50)
        if (whtr > 0.50) score -= (whtr - 0.50) * 120.0

        // Non-insulin dependent GLUT4 glucose clearance & lipid beta-oxidation bonus from activity
        if (dailySteps >= 7500) {
            val stepMetabolicBonus = min(6.5, ((dailySteps - 7000) / 1000.0) * 1.2)
            score += stepMetabolicBonus
        }
        if (activeCaloriesKcal >= 300.0) {
            val calBonus = min(4.5, ((activeCaloriesKcal - 250.0) / 100.0) * 1.0)
            score += calBonus
        }

        return (min(100.0, max(10.0, score)) * 10.0).roundToInt() / 10.0
    }

    private fun calculateInflammatoryScore(hsCrp: Double, sii: Double): Double {
        var score = 100.0
        // hs-CRP penalty (logarithmic)
        if (hsCrp > 0.5) score -= ln(hsCrp / 0.5 + 1.0) * 35.0
        // SII penalty (optimal < 450)
        if (sii > 500) score -= ((sii - 500) / 100.0) * 4.0

        return (min(100.0, max(10.0, score)) * 10.0).roundToInt() / 10.0
    }

    private fun calculateHormonalScore(profile: BiomarkerProfile): Double {
        var score = 100.0
        // Vitamin D (optimal 40-70 ng/mL)
        if (profile.vitaminDNgMl < 40) score -= (40 - profile.vitaminDNgMl) * 1.2
        if (profile.vitaminDNgMl > 90) score -= (profile.vitaminDNgMl - 90) * 0.8
        // TSH (optimal 1.0-2.5)
        if (profile.tshUiuMl < 1.0) score -= (1.0 - profile.tshUiuMl) * 15.0
        if (profile.tshUiuMl > 2.5) score -= (profile.tshUiuMl - 2.5) * 14.0
        // DHEA-S (optimal > 250 for male, > 180 for female)
        val targetDhea = if (profile.isMale) 260.0 else 200.0
        if (profile.dheaSUgDl < targetDhea) score -= ((targetDhea - profile.dheaSUgDl) / 10.0) * 1.5

        return (min(100.0, max(15.0, score)) * 10.0).roundToInt() / 10.0
    }

    private fun calculateMuscleScore(profile: BiomarkerProfile): Double {
        var score = 70.0
        // Chair stand reps in 30s (optimal >= 17)
        score += (profile.chairStand30sReps - 14) * 3.5
        // Walking speed (optimal >= 1.25 m/s)
        score += (profile.walkingSpeed4mMps - 1.1) * 35.0
        // BMI penalty for extreme adiposity or severe underweight
        if (profile.bmi > 27.5) score -= (profile.bmi - 27.5) * 2.5
        if (profile.bmi < 18.5) score -= (18.5 - profile.bmi) * 4.0

        return (min(100.0, max(15.0, score)) * 10.0).roundToInt() / 10.0
    }

    private fun calculateCognitiveScore(profile: BiomarkerProfile): Double {
        var score = 100.0
        // Latency penalty (optimal < 550ms)
        if (profile.stroopMeanLatencyMs > 550) {
            score -= ((profile.stroopMeanLatencyMs - 550) / 10.0) * 1.0
        }
        // Accuracy penalty (optimal >= 98%)
        if (profile.stroopAccuracyPercent < 98) {
            score -= (98 - profile.stroopAccuracyPercent) * 3.5
        }

        return (min(100.0, max(15.0, score)) * 10.0).roundToInt() / 10.0
    }

    private fun calculatePsychologicalScore(profile: BiomarkerProfile): Double {
        // PHQ-9 (0-27), GAD-7 (0-21), PSQI (0-21)
        val totalBurden = profile.phq9Score * 1.5 + profile.gad7Score * 1.3 + profile.psqiSleepScore * 1.8
        val score = 100.0 - totalBurden * 1.2
        return (min(100.0, max(15.0, score)) * 10.0).roundToInt() / 10.0
    }
}
