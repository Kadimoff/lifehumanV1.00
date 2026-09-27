package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D Spatial Vector in anatomical coordinates
 */
data class Vector3D(val x: Float, val y: Float, val z: Float) {
    fun rotate(yawRad: Float, pitchRad: Float): Vector3D {
        // Pitch (around X axis)
        val cosPitch = cos(pitchRad)
        val sinPitch = sin(pitchRad)
        val y1 = y * cosPitch - z * sinPitch
        val z1 = y * sinPitch + z * cosPitch

        // Yaw (around Y axis)
        val cosYaw = cos(yawRad)
        val sinYaw = sin(yawRad)
        val x2 = x * cosYaw + z1 * sinYaw
        val z2 = -x * sinYaw + z1 * cosYaw

        return Vector3D(x2, y1, z2)
    }

    fun project(centerX: Float, centerY: Float, fov: Float = 420f, scale: Float = 0.95f): Offset {
        val cameraDistance = 320f
        val depth = z + cameraDistance
        val factor = if (depth > 10f) (fov / depth) * scale else 1f
        return Offset(centerX + x * factor, centerY + y * factor)
    }
}

/**
 * Anatomical wireframe edge between two 3D vertices
 */
data class WireframeEdge(val fromIndex: Int, val toIndex: Int, val color: Color = Color(0x33CDC0E9), val strokeWidth: Float = 1.2f)

data class BiomarkerMetricDetail(
    val label: String,
    val value: String,
    val optimalRange: String,
    val isOptimal: Boolean
)

data class LongevityIntervention(
    val title: String,
    val impact: String,
    val description: String
)

/**
 * Interactive Organ / Biological System Reserve Node with Deep-Dive Data
 */
data class DigitalTwinNode(
    val id: String,
    val title: String,
    val systemName: String,
    val position: Vector3D,
    val color: Color,
    val icon: ImageVector,
    val scoreExtractor: (BiologicalTwinResult) -> Double,
    val ageExtractor: (BiologicalTwinResult, BiomarkerProfile) -> String,
    val statusExtractor: (Double) -> String,
    val clinicalSummary: String,
    val biologicalMechanism: String,
    val metrics: List<BiomarkerMetricDetail>,
    val interventions: List<LongevityIntervention>
)

@Composable
fun DigitalTwin3DVisualizer(
    profile: BiomarkerProfile,
    twinResult: BiologicalTwinResult,
    modifier: Modifier = Modifier
) {
    var yawDeg by remember { mutableFloatStateOf(20f) }
    var pitchDeg by remember { mutableFloatStateOf(4f) }
    var isAutoRotating by remember { mutableStateOf(true) }
    var selectedNode by remember { mutableStateOf<DigitalTwinNode?>(null) }
    var showFullDeepDiveDialog by remember { mutableStateOf(false) }

    // Heartbeat pulse animation
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Auto-orbit ticker loop
    LaunchedEffect(isAutoRotating) {
        while (isAutoRotating) {
            yawDeg = (yawDeg + 0.6f) % 360f
            kotlinx.coroutines.delay(16)
        }
    }

    // Holographic mesh vertices (Head, Torso, Spine, Arms, Legs)
    val meshVertices = remember {
        listOf(
            Vector3D(0f, -135f, 0f),    // 0: Crown
            Vector3D(0f, -118f, 0f),    // 1: Brain Center
            Vector3D(0f, -100f, 0f),    // 2: Chin
            Vector3D(-16f, -118f, 0f),  // 3: Left Cranium
            Vector3D(16f, -118f, 0f),   // 4: Right Cranium
            Vector3D(0f, -88f, 0f),     // 5: Cervical Base
            Vector3D(-38f, -78f, 0f),   // 6: Left Shoulder
            Vector3D(38f, -78f, 0f),    // 7: Right Shoulder
            Vector3D(-10f, -62f, 10f),  // 8: Cardiac Center
            Vector3D(0f, -38f, 0f),     // 9: Mid Thoracic Spine
            Vector3D(10f, -18f, 8f),    // 10: Hepatic Center
            Vector3D(-24f, -22f, 0f),   // 11: Left Ribcage
            Vector3D(24f, -22f, 0f),    // 12: Right Ribcage
            Vector3D(0f, 6f, 0f),       // 13: Lumbar / Pelvis Core
            Vector3D(-22f, 16f, 0f),    // 14: Left Hip
            Vector3D(22f, 16f, 0f),     // 15: Right Hip
            Vector3D(-48f, -38f, 0f),   // 16: Left Elbow
            Vector3D(-56f, 2f, 0f),     // 17: Left Wrist
            Vector3D(48f, -38f, 0f),    // 18: Right Elbow
            Vector3D(56f, 2f, 0f),      // 19: Right Wrist
            Vector3D(-18f, 62f, 4f),    // 20: Left Knee
            Vector3D(-16f, 115f, 0f),   // 21: Left Ankle
            Vector3D(18f, 62f, 4f),     // 22: Right Knee
            Vector3D(16f, 115f, 0f)     // 23: Right Ankle
        )
    }

    val meshEdges = remember {
        listOf(
            WireframeEdge(0, 3), WireframeEdge(0, 4), WireframeEdge(3, 2), WireframeEdge(4, 2),
            WireframeEdge(0, 1), WireframeEdge(1, 2), WireframeEdge(2, 5),
            WireframeEdge(5, 6), WireframeEdge(5, 7), WireframeEdge(6, 7),
            WireframeEdge(5, 9), WireframeEdge(9, 13),
            WireframeEdge(6, 11), WireframeEdge(7, 12), WireframeEdge(11, 12),
            WireframeEdge(8, 9), WireframeEdge(10, 9),
            WireframeEdge(13, 14), WireframeEdge(13, 15), WireframeEdge(14, 15),
            WireframeEdge(6, 16), WireframeEdge(16, 17),
            WireframeEdge(7, 18), WireframeEdge(18, 19),
            WireframeEdge(14, 20), WireframeEdge(20, 21),
            WireframeEdge(15, 22), WireframeEdge(22, 23)
        )
    }

    val organNodes = remember(profile, twinResult) {
        listOf(
            DigitalTwinNode(
                id = "cognitive",
                title = "Cognitive Reserve",
                systemName = "Central Nervous System & Neuroplasticity",
                position = Vector3D(0f, -118f, 0f),
                color = SleekPrimary,
                icon = Icons.Default.Psychology,
                scoreExtractor = { res -> res.reserves.find { it.key == "cognitive" }?.score ?: 85.0 },
                ageExtractor = { _, p -> "Reaction Latency: ${p.stroopMeanLatencyMs.toInt()} ms (${p.stroopAccuracyPercent.toInt()}% accuracy)" },
                statusExtractor = { score -> if (score >= 85) "Optimal Neuro-reserve" else if (score >= 70) "Preserved Capacity" else "Processing Latency Alert" },
                clinicalSummary = "Executive inhibition control, processing speed, synaptic plasticity, and working memory integrity.",
                biologicalMechanism = "The Stroop Color-Word interference paradigm measures prefrontal cortex executive control and dorsolateral pathway activation. Higher scores reflect robust dendritic spine density, resistance to neurofibrillary tau aggregation, and intact neurovascular coupling.",
                metrics = listOf(
                    BiomarkerMetricDetail("Stroop Response Time", "${profile.stroopMeanLatencyMs.toInt()} ms", "< 650 ms", profile.stroopMeanLatencyMs < 650),
                    BiomarkerMetricDetail("Stroop Accuracy", "${profile.stroopAccuracyPercent.toInt()} %", "> 92 %", profile.stroopAccuracyPercent >= 92),
                    BiomarkerMetricDetail("Cognitive Resilience", "${(profile.stroopAccuracyPercent * 0.9).toInt()}/100", "> 80", profile.stroopAccuracyPercent >= 88)
                ),
                interventions = listOf(
                    LongevityIntervention("Dual N-Back & Visual Puzzles", "+1.2 Yrs", "Engage in progressive executive inhibition drills 3x weekly to maintain synaptic elasticity."),
                    LongevityIntervention("Methylene Blue & BDNF Enhancers", "+0.8 Yrs", "Support mitochondrial respiration and cerebral oxygen consumption via high-intensity interval training.")
                )
            ),
            DigitalTwinNode(
                id = "vascular",
                title = "Vascular & Heart",
                systemName = "Cardiovascular & Endothelial Contour",
                position = Vector3D(-10f, -62f, 10f),
                color = SleekRose,
                icon = Icons.Default.Favorite,
                scoreExtractor = { res -> res.reserves.find { it.key == "vascular" }?.score ?: 82.0 },
                ageExtractor = { res, p -> "Estimated Vascular Age: ${res.reserves.find { it.key == "vascular" }?.ageEstimate?.toInt() ?: p.chronologicalAge.toInt()} yrs" },
                statusExtractor = { score -> if (score >= 80) "Optimal Endothelial Elasticity" else if (score >= 65) "Mild Arterial Stiffness" else "Elevated Atherogenic Load" },
                clinicalSummary = "SCORE2 arterial stiffness modeling, pulse pressure wave integration, and lipid fraction balance.",
                biologicalMechanism = "Arterial compliance dictates left ventricular afterload and cerebral microvascular perfusion. SCORE2 integrates systolic/diastolic ratios and Total/HDL atherogenic particles to quantify plaque progression risks.",
                metrics = listOf(
                    BiomarkerMetricDetail("Systolic / Diastolic BP", "${profile.sbpMmHg.toInt()}/${profile.dbpMmHg.toInt()} mmHg", "< 120/80 mmHg", profile.sbpMmHg <= 120 && profile.dbpMmHg <= 80),
                    BiomarkerMetricDetail("Total Cholesterol", "${profile.totalCholesterolMgDl.toInt()} mg/dL", "< 190 mg/dL", profile.totalCholesterolMgDl < 190),
                    BiomarkerMetricDetail("HDL Protective Factor", "${profile.hdlMgDl.toInt()} mg/dL", "> 50 mg/dL", profile.hdlMgDl >= 50),
                    BiomarkerMetricDetail("Resting Pulse", "${profile.restingPulseBpm.toInt()} bpm", "50 - 68 bpm", profile.restingPulseBpm in 50.0..70.0)
                ),
                interventions = listOf(
                    LongevityIntervention("Zone 2 Aerobic Conditioning", "+2.8 Yrs", "150-180 min/week in Zone 2 heart rate to elevate capillary density and nitric oxide synthesis."),
                    LongevityIntervention("ApoB & Triglyceride Reduction", "+1.9 Yrs", "Optimize dietary polyphenol intake and omega-3 EPA/DHA to reduce subendothelial lipid deposition.")
                )
            ),
            DigitalTwinNode(
                id = "metabolic",
                title = "Metabolic Reserve",
                systemName = "Hepatic & Insulin Sensitivity Axis",
                position = Vector3D(10f, -18f, 8f),
                color = SleekGreen,
                icon = Icons.Default.LocalFireDepartment,
                scoreExtractor = { res -> res.reserves.find { it.key == "metabolic" }?.score ?: 78.0 },
                ageExtractor = { res, p -> "Estimated Metabolic Age: ${res.reserves.find { it.key == "metabolic" }?.ageEstimate?.toInt() ?: p.chronologicalAge.toInt()} yrs" },
                statusExtractor = { score -> if (score >= 80) "Insulin-Sensitive Homeostasis" else if (score >= 65) "Subclinical Glycemic Shift" else "Insulin Resistance Risk" },
                clinicalSummary = "HOMA-IR insulin sensitivity calculation & TyG atherogenic index with hepatic transaminases.",
                biologicalMechanism = "Insulin resistance accelerates cellular senescence via advanced glycation end-products (AGEs) and mTOR overactivation. HOMA-IR evaluates basal pancreatic beta-cell workload against fasting hepatic glucose output.",
                metrics = listOf(
                    BiomarkerMetricDetail("Fasting Glucose", "${profile.glucoseMgDl.toInt()} mg/dL", "70 - 92 mg/dL", profile.glucoseMgDl in 70.0..95.0),
                    BiomarkerMetricDetail("Fasting Insulin", "${profile.insulinUuMl} μU/mL", "< 6.0 μU/mL", profile.insulinUuMl < 6.0),
                    BiomarkerMetricDetail("HOMA-IR Index", String.format("%.2f", (profile.glucoseMgDl * profile.insulinUuMl) / 405.0), "< 1.40", ((profile.glucoseMgDl * profile.insulinUuMl) / 405.0) < 1.5),
                    BiomarkerMetricDetail("Triglycerides", "${profile.triglyceridesMgDl.toInt()} mg/dL", "< 100 mg/dL", profile.triglyceridesMgDl < 100)
                ),
                interventions = listOf(
                    LongevityIntervention("Time-Restricted Feeding (16:8)", "+2.1 Yrs", "Promotes AMPK activation and hepatic glycogen clearance to restore baseline insulin receptor sensitivity."),
                    LongevityIntervention("Postprandial Walking Protocol", "+1.1 Yrs", "15-minute gentle walk following carbohydrate intake increases GLUT4 translocation without insulin surge.")
                )
            ),
            DigitalTwinNode(
                id = "inflammation",
                title = "Inflammaging (Immune)",
                systemName = "Systemic Immune System & Cytokine Tone",
                position = Vector3D(0f, -48f, 0f),
                color = SleekSecondary,
                icon = Icons.Default.Security,
                scoreExtractor = { res -> res.reserves.find { it.key == "inflammation" }?.score ?: 88.0 },
                ageExtractor = { _, p -> "hs-CRP: ${p.hsCrpMgL} mg/L (${if (p.hsCrpMgL < 0.8) "Low Inflammatory Risk" else "Active Inflammation"})" },
                statusExtractor = { score -> if (score >= 82) "Low Inflammatory Burden" else if (score >= 65) "Low-Grade Chronic Stress" else "Hyper-Inflammatory State" },
                clinicalSummary = "Systemic Immune-Inflammation Index (SII) with high-sensitivity CRP logarithmic penalization.",
                biologicalMechanism = "Inflammaging is the chronic, sterile low-grade inflammation driving multi-organ deterioration. Elevated hs-CRP triggers endothelial dysfunction, telomere shortening, and stem cell exhaustion.",
                metrics = listOf(
                    BiomarkerMetricDetail("hs-CRP (High-Sens)", "${profile.hsCrpMgL} mg/L", "< 0.50 mg/L", profile.hsCrpMgL < 0.6),
                    BiomarkerMetricDetail("Serum Albumin", "${profile.albuminGDl} g/dL", "4.4 - 5.2 g/dL", profile.albuminGDl >= 4.4),
                    BiomarkerMetricDetail("White Blood Cells (WBC)", "${profile.wbc103Ul} 10³/μL", "4.0 - 7.0 10³/μL", profile.wbc103Ul in 4.0..7.5),
                    BiomarkerMetricDetail("Lymphocyte Fraction", "${profile.lymphocytePercent}%", "28 - 42%", profile.lymphocytePercent in 28.0..42.0)
                ),
                interventions = listOf(
                    LongevityIntervention("Anti-Inflammatory Phytonutrients", "+1.7 Yrs", "High-potency curcumin, sulforaphane, and polyphenols to downregulate NF-kB transcription."),
                    LongevityIntervention("Gut Barrier Restoration", "+1.4 Yrs", "Fermented foods and soluble prebiotic fibers to reduce systemic lipopolysaccharide (LPS) translocation.")
                )
            ),
            DigitalTwinNode(
                id = "hormonal",
                title = "Hormonal & Thyroid",
                systemName = "Neuroendocrine & Anabolic Cascade",
                position = Vector3D(0f, -80f, 4f),
                color = SleekCoral,
                icon = Icons.Default.WaterDrop,
                scoreExtractor = { res -> res.reserves.find { it.key == "hormonal" }?.score ?: 80.0 },
                ageExtractor = { _, _ -> "Z-Score: +0.28 SD (Balanced)" },
                statusExtractor = { score -> if (score >= 80) "Robust Endocrine Signaling" else if (score >= 65) "Suboptimal Adrenal/Thyroid" else "Endocrine Axis Fatigue" },
                clinicalSummary = "Sex hormone balance, thyroid axis regulation (TSH), adrenal DHEA-S, and 25(OH) Vitamin D.",
                biologicalMechanism = "The endocrine axis regulates cellular metabolism, DNA repair, and protein turnover. DHEA-S and Testosterone support lean mass preservation, while TSH ensures basal mitochondrial oxidative phosphorylation.",
                metrics = listOf(
                    BiomarkerMetricDetail("Total Testosterone", "${profile.testosteroneNgDl.toInt()} ng/dL", "500 - 850 ng/dL", profile.testosteroneNgDl >= 450),
                    BiomarkerMetricDetail("DHEA-Sulfate", "${profile.dheaSUgDl.toInt()} μg/dL", "250 - 450 μg/dL", profile.dheaSUgDl >= 220),
                    BiomarkerMetricDetail("TSH (Thyrotropin)", "${profile.tshUiuMl} μIU/mL", "1.0 - 2.2 μIU/mL", profile.tshUiuMl in 0.8..2.5),
                    BiomarkerMetricDetail("Vitamin D3 (25-OH)", "${profile.vitaminDNgMl.toInt()} ng/mL", "50 - 80 ng/mL", profile.vitaminDNgMl >= 45)
                ),
                interventions = listOf(
                    LongevityIntervention("Circadian Light Optimization", "+1.3 Yrs", "10,000+ lux morning sunlight exposure within 30 min of waking to synchronize pituitary LH/FSH pulsatility."),
                    LongevityIntervention("Micronutrient Cofactors (Zinc/Mag/D3)", "+1.0 Yrs", "Daily supplementation with Zinc glycinate, Magnesium taurate, and Vitamin D3+K2.")
                )
            ),
            DigitalTwinNode(
                id = "muscle",
                title = "Muscle & Locomotor",
                systemName = "Musculoskeletal & Functional Longevity",
                position = Vector3D(-16f, 62f, 4f),
                color = SleekAmber,
                icon = Icons.Default.FitnessCenter,
                scoreExtractor = { res -> res.reserves.find { it.key == "muscle" }?.score ?: 84.0 },
                ageExtractor = { _, p -> "Chair Stand: ${p.chairStand30sReps} reps • Gait: ${p.walkingSpeed4mMps} m/s" },
                statusExtractor = { score -> if (score >= 82) "High Sarcopenia Resistance" else if (score >= 68) "Moderate Muscular Power" else "Sarcopenia Vulnerability" },
                clinicalSummary = "Lower-extremity power, 4m gait velocity, and EWGSOP2 sarcopenia resistance index.",
                biologicalMechanism = "Skeletal muscle is a primary metabolic organ and endocrine secretory tissue producing longevity myokines (IL-15, Irisin). The 30s chair stand test assesses fast-twitch Type II fiber recruitment and dynapenia risk.",
                metrics = listOf(
                    BiomarkerMetricDetail("30s Chair Stand Reps", "${profile.chairStand30sReps} reps", "> 20 reps", profile.chairStand30sReps >= 18),
                    BiomarkerMetricDetail("4m Gait Velocity", "${profile.walkingSpeed4mMps} m/s", "> 1.35 m/s", profile.walkingSpeed4mMps >= 1.25),
                    BiomarkerMetricDetail("Body Mass Index (BMI)", String.format("%.1f", profile.bmi), "20.5 - 24.5", profile.bmi in 20.0..25.0)
                ),
                interventions = listOf(
                    LongevityIntervention("Hypertrophy & Power Lifting", "+3.2 Yrs", "Heavy compound resistance training 3x weekly targeting major locomotive kinetic chains."),
                    LongevityIntervention("Optimized Leucine & Protein Bolus", "+1.5 Yrs", "Consume 1.6-2.2g/kg/day of high-quality protein with 3g leucine thresholds per meal.")
                )
            ),
            DigitalTwinNode(
                id = "sleep",
                title = "Sleep & Autonomic",
                systemName = "Autonomic Nervous System & Recovery",
                position = Vector3D(0f, -100f, 0f),
                color = Color(0xFF80CBC4),
                icon = Icons.Default.NightlightRound,
                scoreExtractor = { res -> res.reserves.find { it.key == "psychological" }?.score ?: 81.0 },
                ageExtractor = { _, p -> "PSQI Score: ${p.psqiSleepScore}/21 (Lower is better)" },
                statusExtractor = { score -> if (score >= 80) "Restorative Sleep Architecture" else if (score >= 65) "Mild Autonomic Strain" else "Sleep Debt & Stress" },
                clinicalSummary = "Pittsburgh Sleep Quality Index, circadian synchrony, and parasympathetic autonomic recovery.",
                biologicalMechanism = "During Slow-Wave Sleep (SWS), the glymphatic system clears beta-amyloid metabolites and interstitial waste. High PSQI scores reflect disrupted sleep stages and elevated nocturnal sympathetic tone.",
                metrics = listOf(
                    BiomarkerMetricDetail("PSQI Sleep Index", "${profile.psqiSleepScore}/21", "< 5", profile.psqiSleepScore <= 5),
                    BiomarkerMetricDetail("GAD-7 Anxiety Scale", "${profile.gad7Score}/21", "< 4", profile.gad7Score <= 4),
                    BiomarkerMetricDetail("PHQ-9 Mood Scale", "${profile.phq9Score}/27", "< 4", profile.phq9Score <= 4)
                ),
                interventions = listOf(
                    LongevityIntervention("Glymphatic Sleep Protocol", "+2.2 Yrs", "Cool bedroom (18°C), total darkness, and zero screen exposure 90 min before bedtime."),
                    LongevityIntervention("Resonance Frequency Breathing", "+1.2 Yrs", "5.5 breaths per minute box breathing for 10 minutes to enhance heart rate variability (HRV).")
                )
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SleekPrimary)
                    )
                    Text(
                        text = "3D DIGITAL TWIN",
                        color = SleekPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-orbit Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isAutoRotating) SleekOnPrimary else DarkSurfaceVariant)
                            .border(1.dp, if (isAutoRotating) SleekPrimary else BorderSubtle, RoundedCornerShape(10.dp))
                            .clickable { isAutoRotating = !isAutoRotating }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isAutoRotating) "3D Orbit: ON" else "3D Orbit: PAUSE",
                            color = if (isAutoRotating) SleekPrimary else TextMuted,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reset 3D Angle
                    IconButton(
                        onClick = {
                            yawDeg = 20f
                            pitchDeg = 4f
                            selectedNode = null
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Angle",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 2. 3D Canvas Box with Interactive Direct Touch & Callout Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0D0E12))
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f + 10f
                            val yawRad = (yawDeg * PI / 180f).toFloat()
                            val pitchRad = (pitchDeg * PI / 180f).toFloat()
                            val scale = 0.96f

                            // Hit-test against projected 3D nodes
                            var closestNode: DigitalTwinNode? = null
                            var minDistance = Float.MAX_VALUE

                            organNodes.forEach { node ->
                                val rot = node.position.rotate(yawRad, pitchRad)
                                val proj = rot.project(centerX, centerY, scale = scale)
                                val dx = tapOffset.x - proj.x
                                val dy = tapOffset.y - proj.y
                                val dist = sqrt(dx * dx + dy * dy)
                                if (dist < 42f && dist < minDistance) {
                                    minDistance = dist
                                    closestNode = node
                                }
                            }

                            if (closestNode != null) {
                                selectedNode = if (selectedNode?.id == closestNode?.id) null else closestNode
                                isAutoRotating = false
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            isAutoRotating = false
                            yawDeg = (yawDeg + dragAmount.x * 0.45f) % 360f
                            pitchDeg = (pitchDeg - dragAmount.y * 0.35f).coerceIn(-35f, 35f)
                        }
                    }
            ) {
                // Interactive 3D Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f + 10f
                    val yawRad = (yawDeg * PI / 180f).toFloat()
                    val pitchRad = (pitchDeg * PI / 180f).toFloat()
                    val scale = 0.96f

                    // 1. Draw Concentric Holographic Platform
                    drawHolographicPlatform(centerX, centerY + 130f, yawRad)

                    // 2. Project mesh vertices
                    val projectedVertices = meshVertices.map { v ->
                        v.rotate(yawRad, pitchRad).project(centerX, centerY, scale = scale)
                    }

                    // 3. Draw Body Wireframe Edges
                    meshEdges.forEach { edge ->
                        if (edge.fromIndex < projectedVertices.size && edge.toIndex < projectedVertices.size) {
                            val p1 = projectedVertices[edge.fromIndex]
                            val p2 = projectedVertices[edge.toIndex]
                            drawLine(
                                color = edge.color,
                                start = p1,
                                end = p2,
                                strokeWidth = edge.strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // 4. Draw Joint Nodes
                    projectedVertices.forEach { p ->
                        drawCircle(
                            color = Color(0x44CDC0E9),
                            radius = 2.2f,
                            center = p
                        )
                    }

                    // 5. Draw Depth-Sorted Organ Reserve Nodes
                    val sortedNodes = organNodes.map { node ->
                        val rot = node.position.rotate(yawRad, pitchRad)
                        Pair(node, rot)
                    }.sortedBy { it.second.z }

                    sortedNodes.forEach { (node, rotPos) ->
                        val proj = rotPos.project(centerX, centerY, scale = scale)
                        val isSelected = selectedNode?.id == node.id

                        // Central spine project connection
                        val spineRot = Vector3D(0f, node.position.y, 0f).rotate(yawRad, pitchRad)
                        val spineProj = spineRot.project(centerX, centerY, scale = scale)

                        drawLine(
                            color = node.color.copy(alpha = if (isSelected) 0.85f else 0.22f),
                            start = spineProj,
                            end = proj,
                            strokeWidth = if (isSelected) 2.2f else 1.0f
                        )

                        val pulseMultiplier = if (node.id == "vascular") pulseAnim.value else 1f
                        val auraRadius = (if (isSelected) 15f else 8.5f) * pulseMultiplier

                        // Glowing Aura
                        drawCircle(
                            color = node.color.copy(alpha = if (isSelected) 0.50f else 0.20f),
                            radius = auraRadius,
                            center = proj
                        )

                        // Outer Selection Reticle
                        if (isSelected) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.8f),
                                radius = auraRadius + 5f,
                                center = proj,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 1.2f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                )
                            )
                        }

                        // Core Node
                        drawCircle(
                            color = node.color,
                            radius = if (isSelected) 7.0f else 4.5f,
                            center = proj
                        )

                        // White Center Spot
                        drawCircle(
                            color = Color.White,
                            radius = 1.8f,
                            center = proj
                        )
                    }
                }

                // Top Live Telemetry Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xD916171E))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SleekPrimary))
                            Text(
                                text = "LRS: ${twinResult.longevityReserveScore.toInt()}/100 • PhenoAge: ${twinResult.phenoAge} yrs",
                                color = SleekPrimary,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Spatial Interactive Callout Tag for Selected Organ Node
                selectedNode?.let { node ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(node.color.copy(alpha = 0.20f))
                                .border(1.dp, node.color, RoundedCornerShape(8.dp))
                                .clickable { showFullDeepDiveDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Inspect Deep-Dive",
                                    color = node.color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = "Expand",
                                    tint = node.color,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Quick Organ System Chips Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(organNodes) { node ->
                    val isSel = selectedNode?.id == node.id
                    val score = node.scoreExtractor(twinResult).toInt()

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) node.color.copy(alpha = 0.25f) else DarkSurfaceVariant)
                            .border(1.dp, if (isSel) node.color else BorderSubtle, RoundedCornerShape(10.dp))
                            .clickable {
                                selectedNode = if (isSel) null else node
                                isAutoRotating = false
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = node.icon,
                                contentDescription = null,
                                tint = if (isSel) node.color else TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${node.title} ($score)",
                                color = if (isSel) node.color else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 4. Interactive Slide-up Deep-Dive Telemetry Overlay Card
            AnimatedVisibility(
                visible = selectedNode != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                selectedNode?.let { node ->
                    val score = node.scoreExtractor(twinResult)
                    val ageInfo = node.ageExtractor(twinResult, profile)
                    val statusText = node.statusExtractor(score)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, node.color.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(node.color.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = node.icon, contentDescription = null, tint = node.color, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(node.title, color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                        Text(node.systemName, color = node.color, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(node.color.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("${score.toInt()}/100", color = node.color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                    IconButton(
                                        onClick = { selectedNode = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            // Score Progress Indicator & Status Pill
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(statusText, color = node.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(ageInfo, color = SleekPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                                }
                                LinearProgressIndicator(
                                    progress = { (score / 100.0).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = node.color,
                                    trackColor = DarkSurface
                                )
                            }

                            Text(node.clinicalSummary, color = TextSecondary, fontSize = 11.5.sp, lineHeight = 16.sp)

                            // Biomarker Key Value Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                node.metrics.take(3).forEach { m ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF111216))
                                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 6.dp, vertical = 5.dp)
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(m.label, color = TextMuted, fontSize = 9.sp, maxLines = 1)
                                            Text(
                                                text = m.value,
                                                color = if (m.isOptimal) SleekGreen else SleekCoral,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Action Button: View Full Clinical Deep-Dive
                            Button(
                                onClick = { showFullDeepDiveDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = node.color.copy(alpha = 0.22f),
                                    contentColor = node.color
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Full Clinical Deep-Dive & Action Plan", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // 5. Full Clinical Deep-Dive Modal Dialog
    if (showFullDeepDiveDialog && selectedNode != null) {
        val node = selectedNode!!
        val score = node.scoreExtractor(twinResult)
        val statusText = node.statusExtractor(score)

        Dialog(
            onDismissRequest = { showFullDeepDiveDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = DarkBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, node.color.copy(alpha = 0.6f))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Bar
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(node.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = node.icon,
                                        contentDescription = null,
                                        tint = node.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = node.title,
                                        color = TextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = node.systemName,
                                        color = node.color,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showFullDeepDiveDialog = false },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Score Card with Radial/Linear Overview
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "SYSTEM RESERVE SCORE",
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        )
                                        Text(
                                            text = "${score.toInt()} / 100",
                                            color = node.color,
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(node.color.copy(alpha = 0.15f))
                                            .border(1.dp, node.color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = statusText,
                                            color = node.color,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { (score / 100.0).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = node.color,
                                    trackColor = DarkSurfaceVariant
                                )

                                Text(
                                    text = "Calculated in harmonic synthesis with the PhenoAge Gompertz mortality engine.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Biological Mechanism Deep Dive
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "PHYSIOLOGICAL MECHANISM & AGING IMPACT",
                                        color = SleekPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = node.biologicalMechanism,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Associated Biomarker Metrics Table
                    item {
                        Text(
                            text = "CLINICAL BIOMARKERS & REFERENCE RANGES",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    items(node.metrics) { metric ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(metric.label, color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    Text("Clinical Target: ${metric.optimalRange}", color = TextMuted, fontSize = 10.5.sp)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = metric.value,
                                        color = if (metric.isOptimal) SleekGreen else SleekCoral,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = if (metric.isOptimal) Icons.Default.CheckCircle else Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (metric.isOptimal) SleekGreen else SleekCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Targeted Longevity Interventions
                    item {
                        Text(
                            text = "EVIDENCE-BASED LONGEVITY PROTOCOLS",
                            color = SleekPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    items(node.interventions) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SleekPrimary.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SleekGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(item.impact, color = SleekGreen, fontSize = 10.5.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Text(item.description, color = TextSecondary, fontSize = 11.5.sp, lineHeight = 16.sp)
                            }
                        }
                    }

                    // Done Button
                    item {
                        Button(
                            onClick = { showFullDeepDiveDialog = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekPrimary,
                                contentColor = SleekOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back to 3D Digital Twin", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

/**
 * Draw concentric 3D perspective rings and rotating radial spokes
 */
private fun DrawScope.drawHolographicPlatform(centerX: Float, centerY: Float, yawRad: Float) {
    for (r in 1..3) {
        drawOval(
            color = SleekPrimary.copy(alpha = 0.05f * r),
            topLeft = Offset(centerX - r * 45f, centerY - r * 14f),
            size = androidx.compose.ui.geometry.Size(r * 90f, r * 28f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.0f)
        )
    }

    for (i in 0 until 8) {
        val angle = yawRad + (i * PI.toFloat() / 4f)
        val dx = cos(angle) * 110f
        val dy = sin(angle) * 34f
        drawLine(
            color = SleekPrimary.copy(alpha = 0.06f),
            start = Offset(centerX, centerY),
            end = Offset(centerX + dx, centerY + dy),
            strokeWidth = 0.8f
        )
    }
}
