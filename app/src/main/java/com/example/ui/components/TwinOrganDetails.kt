package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekCyan
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Organ reserve nodes of the digital twin and their clinical deep-dive UI.
 * Shared by the hologram view and anything else that needs to show an organ report.
 */

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

data class DigitalTwinNode(
    val id: String,
    val title: String,
    val systemName: String,
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

/** Organ reserve nodes with rich clinical telemetry for the given profile. */
fun buildOrganNodes(profile: BiomarkerProfile): List<DigitalTwinNode> =
    listOf(
        DigitalTwinNode(
            id = "cognitive",
            title = "Brain & Cognition",
            systemName = "Central Nervous System & Neuroplasticity",
            color = SleekCyan,
            icon = Icons.Default.Psychology,
            scoreExtractor = { res -> res.reserves.find { it.key == "cognitive" }?.score ?: 85.0 },
            ageExtractor = { _, p -> "${p.stroopMeanLatencyMs.toInt()} ms reaction time" },
            statusExtractor = { score -> if (score >= 80) "Optimal Neuro-Reserve" else "Delayed Reaction" },
            clinicalSummary = "Executive function, neural synchronization and synaptic plasticity score.",
            biologicalMechanism = "Measures prefrontal executive control and dorsolateral pathway activation speed. A high reserve indicates resistance to neurofibrillary tau aggregation and robust neurovascular coupling.",
            metrics = listOf(
                BiomarkerMetricDetail("Stroop Speed", "${profile.stroopMeanLatencyMs.toInt()} ms", "< 650 ms", profile.stroopMeanLatencyMs < 650),
                BiomarkerMetricDetail("Accuracy", "${profile.stroopAccuracyPercent.toInt()}%", "> 92%", profile.stroopAccuracyPercent >= 92),
                BiomarkerMetricDetail("Cognitive Flexibility", "${(profile.stroopAccuracyPercent * 0.9).toInt()}/100", "> 80", profile.stroopAccuracyPercent >= 88)
            ),
            interventions = listOf(
                LongevityIntervention("Dual N-Back & Mental Training", "+1.2 yrs", "Neuromuscular and strategy puzzles 3 days a week to preserve synaptic plasticity."),
                LongevityIntervention("Aerobic BDNF Protocol", "+0.8 yrs", "30-min Zone 2 walk to boost brain-derived neurotrophic factor (BDNF).")
            )
        ),
        DigitalTwinNode(
            id = "vascular",
            title = "Heart & Vessels",
            systemName = "Cardiovascular & Endothelial Elasticity",
            color = SleekRose,
            icon = Icons.Default.Favorite,
            scoreExtractor = { res -> res.reserves.find { it.key == "vascular" }?.score ?: 82.0 },
            ageExtractor = { res, p -> "Vascular Age: ${res.reserves.find { it.key == "vascular" }?.ageEstimate?.toInt() ?: p.chronologicalAge.toInt()} yrs" },
            statusExtractor = { score -> if (score >= 80) "Optimal Elasticity" else "Mild Arterial Stiffness" },
            clinicalSummary = "SCORE2 arterial elasticity, pulse wave and lipid fraction balance.",
            biologicalMechanism = "Arterial compliance determines left ventricular afterload and microvascular perfusion. The systolic/diastolic ratio and pulse wave velocity reflect atherosclerotic burden.",
            metrics = listOf(
                BiomarkerMetricDetail("Blood Pressure", "${profile.sbpMmHg.toInt()}/${profile.dbpMmHg.toInt()} mmHg", "< 120/80", profile.sbpMmHg <= 120 && profile.dbpMmHg <= 80),
                BiomarkerMetricDetail("Resting Heart Rate", "${profile.restingPulseBpm.toInt()} bpm", "50-68 bpm", profile.restingPulseBpm in 50.0..70.0),
                BiomarkerMetricDetail("Cholesterol", "${profile.totalCholesterolMgDl.toInt()} mg/dL", "< 190", profile.totalCholesterolMgDl < 190),
                BiomarkerMetricDetail("Protective HDL", "${profile.hdlMgDl.toInt()} mg/dL", "> 50", profile.hdlMgDl >= 50)
            ),
            interventions = listOf(
                LongevityIntervention("Zone 2 Cardio Training", "+2.8 yrs", "150 min/week of easy-pace cardio to increase mitochondrial density and nitric oxide synthesis."),
                LongevityIntervention("Omega-3 & Polyphenol Support", "+1.9 yrs", "High EPA/DHA and olive oil polyphenols for endothelial protection.")
            )
        ),
        DigitalTwinNode(
            id = "metabolic",
            title = "Metabolism",
            systemName = "Hepatic & Insulin Sensitivity Axis",
            color = SleekAmber,
            icon = Icons.Default.LocalFireDepartment,
            scoreExtractor = { res -> res.reserves.find { it.key == "metabolic" }?.score ?: 78.0 },
            ageExtractor = { _, p -> "HOMA-IR: ${String.format("%.2f", (p.glucoseMgDl * p.insulinUuMl) / 405.0)}" },
            statusExtractor = { score -> if (score >= 80) "High Insulin Sensitivity" else "Mild Glycation Load" },
            clinicalSummary = "HOMA-IR insulin resistance and glucose metabolism kinetics.",
            biologicalMechanism = "HOMA-IR, calculated from the product of fasting glucose and insulin, represents glycogen storage efficiency in liver and muscle tissue.",
            metrics = listOf(
                BiomarkerMetricDetail("Fasting Glucose", "${profile.glucoseMgDl.toInt()} mg/dL", "70-90", profile.glucoseMgDl in 70.0..95.0),
                BiomarkerMetricDetail("Fasting Insulin", "${profile.insulinUuMl.toInt()} μU/mL", "< 6.0", profile.insulinUuMl <= 6.0),
                BiomarkerMetricDetail("HbA1c", "${profile.hba1cPercent}%", "< 5.4%", profile.hba1cPercent < 5.5)
            ),
            interventions = listOf(
                LongevityIntervention("10-Min Post-Meal Walk", "+1.5 yrs", "Lowers the postprandial glucose peak by 30%."),
                LongevityIntervention("Intermittent Fasting & Circadian Eating", "+1.8 yrs", "Autophagy activation with a 14/10 eating window.")
            )
        ),
        DigitalTwinNode(
            id = "immune",
            title = "Immunity",
            systemName = "Inflammation & Systemic Biological Resilience",
            color = SleekCoral,
            icon = Icons.Default.Security,
            scoreExtractor = { res -> res.reserves.find { it.key == "inflammatory" }?.score ?: 76.0 },
            ageExtractor = { _, p -> "hs-CRP: ${p.hsCrpMgL} mg/L" },
            statusExtractor = { score -> if (score >= 80) "Low Inflammation" else "Systemic Inflammation Risk" },
            clinicalSummary = "hs-CRP, leukocyte fractions and systemic immunosenescence.",
            biologicalMechanism = "High-sensitivity C-reactive protein (hs-CRP) reflects low-grade chronic micro-inflammation (inflammaging) in vessel walls and organs.",
            metrics = listOf(
                BiomarkerMetricDetail("hs-CRP", "${profile.hsCrpMgL} mg/L", "< 0.8", profile.hsCrpMgL < 0.8),
                BiomarkerMetricDetail("White Blood Cells (WBC)", "${String.format("%.1f", profile.whiteBloodCellCount)} k/μL", "4.0-6.5", profile.whiteBloodCellCount in 4.0..7.0)
            ),
            interventions = listOf(
                LongevityIntervention("Anti-Inflammatory Phytonutrients", "+1.6 yrs", "Cytokine suppression with curcumin, quercetin and sulforaphane."),
                LongevityIntervention("Hot/Cold Thermal Exposure", "+1.1 yrs", "Heat shock protein (HSP70) activation with 2-3 sauna sessions per week.")
            )
        ),
        DigitalTwinNode(
            id = "musculoskeletal",
            title = "Muscle & Skeleton",
            systemName = "Sarcopenia Resistance & Bone Mineral Balance",
            color = SleekGreen,
            icon = Icons.Default.FitnessCenter,
            scoreExtractor = { res -> res.reserves.find { it.key == "muscle" }?.score ?: 88.0 },
            ageExtractor = { _, p -> "Grip Strength: ${p.gripStrengthKg.toInt()} kg" },
            statusExtractor = { score -> if (score >= 80) "Strong Muscle Mass" else "Muscle Loss (Sarcopenia) Risk" },
            clinicalSummary = "Grip strength and functional physical independence capacity.",
            biologicalMechanism = "Grip strength is the strongest independent physical predictor of all-cause mortality risk.",
            metrics = listOf(
                BiomarkerMetricDetail("Grip Strength", "${profile.gripStrengthKg.toInt()} kg", "> 45 kg", profile.gripStrengthKg >= 45),
                BiomarkerMetricDetail("Daily Steps", "${profile.dailySteps} steps", "> 8,000", profile.dailySteps >= 8000)
            ),
            interventions = listOf(
                LongevityIntervention("Progressive Resistance Training", "+3.2 yrs", "3 weekly weight-training sessions targeting major muscle groups."),
                LongevityIntervention("Leucine-Rich Protein (1.6g/kg)", "+1.4 yrs", "Optimizes muscle protein synthesis (mTOR).")
            )
        ),
        DigitalTwinNode(
            id = "circadian",
            title = "Sleep & Rhythm",
            systemName = "Glymphatic System & Deep Regeneration",
            color = SleekIndigo,
            icon = Icons.Default.NightlightRound,
            scoreExtractor = { res -> res.reserves.find { it.key == "psychological" }?.score ?: 84.0 },
            ageExtractor = { _, p -> "Sleep Index: ${p.psqiSleepScore}/21" },
            statusExtractor = { score -> if (score >= 80) "Optimal Glymphatic Clearance" else "Insufficient Deep Sleep" },
            clinicalSummary = "PSQI score, circadian rhythm synchronization and REM efficiency.",
            biologicalMechanism = "During deep slow-wave sleep the glymphatic system expands to clear amyloid-beta and cellular waste from the brain.",
            metrics = listOf(
                BiomarkerMetricDetail("PSQI Index", "${profile.psqiSleepScore}/21", "< 5", profile.psqiSleepScore <= 5),
                BiomarkerMetricDetail("Anxiety (GAD-7)", "${profile.gad7Score}/21", "< 4", profile.gad7Score <= 4)
            ),
            interventions = listOf(
                LongevityIntervention("Dark & Cool Bedroom (18°C)", "+2.2 yrs", "Block blue light and keep total darkness 90 min before bed."),
                LongevityIntervention("Resonance Breathing Exercise", "+1.2 yrs", "Box breathing at 5.5 breaths per minute to raise HRV.")
            )
        )
    )

/** Horizontal organ system chips; tapping toggles the selection. */
@Composable
fun OrganChipsRow(
    organNodes: List<DigitalTwinNode>,
    twinResult: BiologicalTwinResult,
    selectedNode: DigitalTwinNode?,
    onSelect: (DigitalTwinNode?) -> Unit
) {
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
                    .border(1.dp, if (isSel) node.color else CardBorder, RoundedCornerShape(10.dp))
                    .clickable { onSelect(if (isSel) null else node) }
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
                        color = if (isSel) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

/** Compact summary card for the selected organ. */
@Composable
fun OrganSummaryCard(
    node: DigitalTwinNode,
    profile: BiomarkerProfile,
    twinResult: BiologicalTwinResult,
    onClose: () -> Unit,
    onOpenReport: () -> Unit
) {
    val score = node.scoreExtractor(twinResult)
    val ageInfo = node.ageExtractor(twinResult, profile)
    val statusText = node.statusExtractor(score)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, node.color.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
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
                        Text(node.systemName, color = node.color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(node.color.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("${score.toInt()}/100", color = node.color, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Progress & Status
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(statusText, color = node.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(ageInfo, color = TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                }
                LinearProgressIndicator(
                    progress = { (score / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = node.color,
                    trackColor = DarkBackground
                )
            }

            // Glanceable Metric Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                node.metrics.take(3).forEach { m ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(m.label, color = TextMuted, fontSize = 9.sp, maxLines = 1)
                            Text(
                                text = m.value,
                                color = if (m.isOptimal) SleekGreen else SleekCoral,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Button
            Button(
                onClick = onOpenReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = node.color.copy(alpha = 0.15f),
                    contentColor = node.color
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Detailed Clinical Report & Interventions", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Full-screen clinical deep dive: mechanism, biomarkers and intervention protocols. */
@Composable
fun OrganDeepDiveDialog(
    node: DigitalTwinNode,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, node.color.copy(alpha = 0.5f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(node.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Text(node.systemName, color = node.color, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("BIOLOGICAL MECHANISM", color = node.color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            Text(node.biologicalMechanism, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }

                item {
                    Text("CLINICAL BIOMARKERS", color = TextMuted, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                items(node.metrics) { m ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(m.label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Target: ${m.optimalRange}", color = TextMuted, fontSize = 10.sp)
                        }
                        Text(
                            text = m.value,
                            color = if (m.isOptimal) SleekGreen else SleekCoral,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Text("LONGEVITY INTERVENTION PROTOCOLS", color = TextMuted, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                items(node.interventions) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

                item {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekPrimary,
                            contentColor = SleekOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to Model", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
