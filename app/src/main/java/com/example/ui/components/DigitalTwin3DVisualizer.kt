package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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

    fun project(centerX: Float, centerY: Float, fov: Float = 420f, scale: Float = 1.0f): Offset {
        val cameraDistance = 340f
        val depth = z + cameraDistance
        val factor = if (depth > 10f) (fov / depth) * scale else 1f
        return Offset(centerX + x * factor, centerY + y * factor)
    }
}

/**
 * Anatomical volumetric segment between two 3D vertices
 */
data class VolumetricEdge(
    val fromIndex: Int,
    val toIndex: Int,
    val isPrimaryContour: Boolean = false,
    val isVascularConduit: Boolean = false
)

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
    var yawDeg by remember { mutableFloatStateOf(24f) }
    var pitchDeg by remember { mutableFloatStateOf(6f) }
    var isAutoRotating by remember { mutableStateOf(true) }
    var selectedNode by remember { mutableStateOf<DigitalTwinNode?>(null) }
    var showFullDeepDiveDialog by remember { mutableStateOf(false) }

    // Heartbeat pulse animation
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        pulseAnim.animateTo(
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(750, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Infinite transition for medical laser tomography scanner & circulatory energy pulses
    val infiniteTransition = rememberInfiniteTransition(label = "HologramScanner")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanProgress"
    )

    val vascularPulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "VascularPulse"
    )

    // Auto-orbit ticker loop
    LaunchedEffect(isAutoRotating) {
        while (isAutoRotating) {
            yawDeg = (yawDeg + 0.5f) % 360f
            kotlinx.coroutines.delay(16)
        }
    }

    // =========================================================================
    // VOLUMETRIC ANATOMICAL MESH (Head, Ribcage, Spine, Dual-rail Arms & Legs)
    // =========================================================================
    val meshVertices = remember {
        listOf(
            // CRANIUM & HEAD (0..9)
            Vector3D(0f, -145f, 0f),     // 0: Crown top
            Vector3D(0f, -134f, 16f),    // 1: Forehead (anterior)
            Vector3D(0f, -134f, -16f),   // 2: Occipital (posterior)
            Vector3D(-18f, -134f, 0f),   // 3: Left Temporal
            Vector3D(18f, -134f, 0f),    // 4: Right Temporal
            Vector3D(0f, -120f, 14f),    // 5: Eye-line / Nasal
            Vector3D(0f, -106f, 10f),    // 6: Chin / Mandible
            Vector3D(-14f, -112f, 4f),   // 7: Left Jaw angle
            Vector3D(14f, -112f, 4f),    // 8: Right Jaw angle
            Vector3D(0f, -96f, -2f),     // 9: Cervical Base / Neck core

            // THORACIC SPINAL COLUMN & STERNUM (10..15)
            Vector3D(0f, -86f, 14f),     // 10: Superior Sternum / Manubrium
            Vector3D(0f, -60f, 16f),     // 11: Mid Sternum
            Vector3D(0f, -38f, 14f),     // 12: Xiphoid Process
            Vector3D(0f, -86f, -10f),    // 13: High Thoracic Spine
            Vector3D(0f, -60f, -12f),    // 14: Mid Thoracic Spine
            Vector3D(0f, -38f, -10f),    // 15: Lower Thoracic Spine

            // THORACIC RIBCAGE BASKET (16..27)
            // Upper Chest Ring
            Vector3D(-34f, -82f, 8f),    // 16: Left Upper Clavicle
            Vector3D(34f, -82f, 8f),     // 17: Right Upper Clavicle
            Vector3D(-32f, -68f, 10f),   // 18: Left Mid Pectoral Rib
            Vector3D(32f, -68f, 10f),    // 19: Right Mid Pectoral Rib
            Vector3D(-30f, -68f, -8f),   // 20: Left Upper Back Rib
            Vector3D(30f, -68f, -8f),    // 21: Right Upper Back Rib
            // Lower Costal Margin Ring
            Vector3D(-26f, -42f, 8f),    // 22: Left Flank Rib
            Vector3D(26f, -42f, 8f),     // 23: Right Flank Rib
            Vector3D(-24f, -42f, -8f),   // 24: Left Lower Back Rib
            Vector3D(24f, -42f, -8f),    // 25: Right Lower Back Rib

            // WAIST CORE & PELVIC GIRDLE (26..33)
            Vector3D(0f, -18f, 10f),     // 26: Navel / Core Anterior
            Vector3D(0f, -18f, -8f),     // 27: Lumbar Spine L3
            Vector3D(-22f, -18f, 2f),    // 28: Left Waist
            Vector3D(22f, -18f, 2f),     // 29: Right Waist
            Vector3D(-26f, 8f, 4f),      // 30: Left Iliac Crest (Hip bone)
            Vector3D(26f, 8f, 4f),       // 31: Right Iliac Crest (Hip bone)
            Vector3D(0f, 16f, 8f),       // 32: Pubic Arch
            Vector3D(0f, 10f, -8f),      // 33: Sacrum base

            // UPPER LIMBS: LEFT ARM (34..41)
            Vector3D(-42f, -80f, 0f),    // 34: Left Deltoid Acromion
            Vector3D(-48f, -56f, 4f),    // 35: Left Bicep Lateral
            Vector3D(-40f, -56f, -4f),   // 36: Left Bicep Medial
            Vector3D(-50f, -34f, 0f),    // 37: Left Elbow joint
            Vector3D(-54f, -12f, 3f),    // 38: Left Forearm Lateral
            Vector3D(-46f, -12f, -3f),   // 39: Left Forearm Medial
            Vector3D(-56f, 10f, 0f),     // 40: Left Wrist
            Vector3D(-60f, 24f, 0f),     // 41: Left Hand palm

            // UPPER LIMBS: RIGHT ARM (42..49)
            Vector3D(42f, -80f, 0f),     // 42: Right Deltoid Acromion
            Vector3D(48f, -56f, 4f),     // 43: Right Bicep Lateral
            Vector3D(40f, -56f, -4f),    // 44: Right Bicep Medial
            Vector3D(50f, -34f, 0f),     // 45: Right Elbow joint
            Vector3D(54f, -12f, 3f),     // 46: Right Forearm Lateral
            Vector3D(46f, -12f, -3f),    // 47: Right Forearm Medial
            Vector3D(56f, 10f, 0f),      // 48: Right Wrist
            Vector3D(60f, 24f, 0f),      // 49: Right Hand palm

            // LOWER LIMBS: LEFT LEG (50..57)
            Vector3D(-22f, 20f, 0f),     // 50: Left Femoral Head (Hip joint)
            Vector3D(-26f, 48f, 6f),     // 51: Left Quad Lateral
            Vector3D(-16f, 48f, -4f),    // 52: Left Quad Medial
            Vector3D(-20f, 76f, 2f),     // 53: Left Patella / Knee
            Vector3D(-22f, 104f, 4f),    // 54: Left Calf Lateral
            Vector3D(-14f, 104f, -4f),   // 55: Left Shin Medial
            Vector3D(-18f, 130f, 0f),    // 56: Left Ankle joint
            Vector3D(-17f, 138f, 14f),   // 57: Left Foot toe tip

            // LOWER LIMBS: RIGHT LEG (58..65)
            Vector3D(22f, 20f, 0f),      // 58: Right Femoral Head (Hip joint)
            Vector3D(26f, 48f, 6f),      // 59: Right Quad Lateral
            Vector3D(16f, 48f, -4f),     // 60: Right Quad Medial
            Vector3D(20f, 76f, 2f),      // 61: Right Patella / Knee
            Vector3D(22f, 104f, 4f),     // 62: Right Calf Lateral
            Vector3D(14f, 104f, -4f),    // 63: Right Shin Medial
            Vector3D(18f, 130f, 0f),     // 64: Right Ankle joint
            Vector3D(17f, 138f, 14f)     // 65: Right Foot toe tip
        )
    }

    val meshEdges = remember {
        listOf(
            // Cranial Volumetric Mesh
            VolumetricEdge(0, 1, true), VolumetricEdge(0, 2, true),
            VolumetricEdge(0, 3, true), VolumetricEdge(0, 4, true),
            VolumetricEdge(1, 3), VolumetricEdge(1, 4),
            VolumetricEdge(2, 3), VolumetricEdge(2, 4),
            VolumetricEdge(1, 5, true), VolumetricEdge(5, 6, true),
            VolumetricEdge(3, 7), VolumetricEdge(4, 8),
            VolumetricEdge(7, 6), VolumetricEdge(8, 6),
            VolumetricEdge(6, 9, true), VolumetricEdge(2, 9),

            // Cervical & Spine Axis
            VolumetricEdge(9, 13, true), VolumetricEdge(13, 14, true),
            VolumetricEdge(14, 15, true), VolumetricEdge(15, 27, true),
            VolumetricEdge(27, 33, true),

            // Sternum Axis
            VolumetricEdge(9, 10, true), VolumetricEdge(10, 11, true),
            VolumetricEdge(11, 12, true), VolumetricEdge(12, 26, true),

            // Ribcage Volumetric Basket
            VolumetricEdge(10, 16), VolumetricEdge(10, 17),
            VolumetricEdge(13, 20), VolumetricEdge(13, 21),
            VolumetricEdge(16, 18), VolumetricEdge(17, 19),
            VolumetricEdge(18, 11), VolumetricEdge(19, 11),
            VolumetricEdge(18, 20), VolumetricEdge(19, 21),
            VolumetricEdge(11, 22), VolumetricEdge(11, 23),
            VolumetricEdge(14, 24), VolumetricEdge(14, 25),
            VolumetricEdge(22, 24), VolumetricEdge(23, 25),
            VolumetricEdge(12, 22), VolumetricEdge(12, 23),
            VolumetricEdge(15, 24), VolumetricEdge(15, 25),

            // Abdominal Core & Pelvis
            VolumetricEdge(26, 28), VolumetricEdge(26, 29),
            VolumetricEdge(27, 28), VolumetricEdge(27, 29),
            VolumetricEdge(28, 30, true), VolumetricEdge(29, 31, true),
            VolumetricEdge(30, 32, true), VolumetricEdge(31, 32, true),
            VolumetricEdge(30, 33), VolumetricEdge(31, 33),
            VolumetricEdge(32, 50), VolumetricEdge(32, 58),

            // Left Arm Dual-Rail Volume
            VolumetricEdge(16, 34, true), VolumetricEdge(20, 34),
            VolumetricEdge(34, 35, true), VolumetricEdge(34, 36),
            VolumetricEdge(35, 37, true), VolumetricEdge(36, 37),
            VolumetricEdge(37, 38, true), VolumetricEdge(37, 39),
            VolumetricEdge(38, 40, true), VolumetricEdge(39, 40),
            VolumetricEdge(40, 41, true),

            // Right Arm Dual-Rail Volume
            VolumetricEdge(17, 42, true), VolumetricEdge(21, 42),
            VolumetricEdge(42, 43, true), VolumetricEdge(42, 44),
            VolumetricEdge(43, 45, true), VolumetricEdge(44, 45),
            VolumetricEdge(45, 46, true), VolumetricEdge(45, 47),
            VolumetricEdge(46, 48, true), VolumetricEdge(47, 48),
            VolumetricEdge(48, 49, true),

            // Left Leg Dual-Rail Volume
            VolumetricEdge(30, 50, true), VolumetricEdge(33, 50),
            VolumetricEdge(50, 51, true), VolumetricEdge(50, 52),
            VolumetricEdge(51, 53, true), VolumetricEdge(52, 53),
            VolumetricEdge(53, 54, true), VolumetricEdge(53, 55),
            VolumetricEdge(54, 56, true), VolumetricEdge(55, 56),
            VolumetricEdge(56, 57, true),

            // Right Leg Dual-Rail Volume
            VolumetricEdge(31, 58, true), VolumetricEdge(33, 58),
            VolumetricEdge(58, 59, true), VolumetricEdge(58, 60),
            VolumetricEdge(59, 61, true), VolumetricEdge(60, 61),
            VolumetricEdge(61, 62, true), VolumetricEdge(61, 63),
            VolumetricEdge(62, 64, true), VolumetricEdge(63, 64),
            VolumetricEdge(64, 65, true)
        )
    }

    // Organ Reserve Nodes with Rich Clinical Telemetry
    val organNodes = remember(profile, twinResult) {
        listOf(
            DigitalTwinNode(
                id = "cognitive",
                title = "Beyin & Bilişsel",
                systemName = "Merkezi Sinir Sistemi & Nöroplastisite",
                position = Vector3D(0f, -125f, 0f),
                color = SleekCyan,
                icon = Icons.Default.Psychology,
                scoreExtractor = { res -> res.reserves.find { it.key == "cognitive" }?.score ?: 85.0 },
                ageExtractor = { _, p -> "${p.stroopMeanLatencyMs.toInt()} ms reaksiyon hızı" },
                statusExtractor = { score -> if (score >= 80) "Optimal Nöro-Rezerv" else "Reaksiyon Gecikmesi" },
                clinicalSummary = "Yürütücü işlevler, nöral senkronizasyon ve sinaptik elastikiyet skoru.",
                biologicalMechanism = "Prefrontal korteks yürütücü kontrolü ve dorsolateral yolların uyarılma hızını ölçer. Yüksek rezerv, nörofibriler tau agregasyonuna direnç ve sağlam nörovasküler eşleşmeyi gösterir.",
                metrics = listOf(
                    BiomarkerMetricDetail("Stroop Hızı", "${profile.stroopMeanLatencyMs.toInt()} ms", "< 650 ms", profile.stroopMeanLatencyMs < 650),
                    BiomarkerMetricDetail("Doğruluk", "%${profile.stroopAccuracyPercent.toInt()}", "> %92", profile.stroopAccuracyPercent >= 92),
                    BiomarkerMetricDetail("Bilişsel Esneklik", "${(profile.stroopAccuracyPercent * 0.9).toInt()}/100", "> 80", profile.stroopAccuracyPercent >= 88)
                ),
                interventions = listOf(
                    LongevityIntervention("Dual N-Back & Zihinsel Egzersiz", "+1.2 Yıl", "Haftada 3 gün sinaptik elastikiyeti korumak için nöromusküler ve stratejik bulmacalar."),
                    LongevityIntervention("Aerobik BDNF Protokolü", "+0.8 Yıl", "Beyin türevli nörotrofik faktörü (BDNF) artırmak için 30 dk Zone 2 yürüyüş.")
                )
            ),
            DigitalTwinNode(
                id = "vascular",
                title = "Kalp & Damar",
                systemName = "Kardiyovasküler & Endotel Esnekliği",
                position = Vector3D(-8f, -62f, 10f),
                color = SleekRose,
                icon = Icons.Default.Favorite,
                scoreExtractor = { res -> res.reserves.find { it.key == "vascular" }?.score ?: 82.0 },
                ageExtractor = { res, p -> "Damar Yaşı: ${res.reserves.find { it.key == "vascular" }?.ageEstimate?.toInt() ?: p.chronologicalAge.toInt()} yaş" },
                statusExtractor = { score -> if (score >= 80) "Optimal Elastisite" else "Hafif Arteriyel Sertlik" },
                clinicalSummary = "SCORE2 arteriyel elastisite, nabız dalgası ve lipid fraksiyon dengesi.",
                biologicalMechanism = "Arteriyel uyum, sol ventrikül sonrası yükü ve mikrovasküler perfüzyonu belirler. Sistolik/diyastolik oran ve nabız dalga hızı aterosklerotik yükü gösterir.",
                metrics = listOf(
                    BiomarkerMetricDetail("Tansiyon", "${profile.sbpMmHg.toInt()}/${profile.dbpMmHg.toInt()} mmHg", "< 120/80", profile.sbpMmHg <= 120 && profile.dbpMmHg <= 80),
                    BiomarkerMetricDetail("Dinlenik Nabız", "${profile.restingPulseBpm.toInt()} bpm", "50-68 bpm", profile.restingPulseBpm in 50.0..70.0),
                    BiomarkerMetricDetail("Kolesterol", "${profile.totalCholesterolMgDl.toInt()} mg/dL", "< 190", profile.totalCholesterolMgDl < 190),
                    BiomarkerMetricDetail("HDL Koruyucu", "${profile.hdlMgDl.toInt()} mg/dL", "> 50", profile.hdlMgDl >= 50)
                ),
                interventions = listOf(
                    LongevityIntervention("Zone 2 Kardiyo Antrenmanı", "+2.8 Yıl", "Haftada 150 dk mitokondriyal yoğunluğu ve nitrik oksit sentezini artıran hafif tempo kardiyo."),
                    LongevityIntervention("Omega-3 & Polifenol Desteği", "+1.9 Yıl", "Endotel koruması için yüksek EPA/DHA ve zeytinyağı polifenolleri.")
                )
            ),
            DigitalTwinNode(
                id = "metabolic",
                title = "Metabolizma",
                systemName = "Hepatik & İnsülin Duyarlılığı Ekseni",
                position = Vector3D(10f, -22f, 8f),
                color = SleekAmber,
                icon = Icons.Default.LocalFireDepartment,
                scoreExtractor = { res -> res.reserves.find { it.key == "metabolic" }?.score ?: 78.0 },
                ageExtractor = { _, p -> "HOMA-IR: ${String.format("%.2f", (p.glucoseMgDl * p.insulinUuMl) / 405.0)}" },
                statusExtractor = { score -> if (score >= 80) "Yüksek İnsülin Duyarlılığı" else "Hafif Glikasyon Yükü" },
                clinicalSummary = "HOMA-IR insülin direnci ve glukoz metabolizması kinetiği.",
                biologicalMechanism = "Glukoz ve açlık insülini çarpımı ile hesaplanan HOMA-IR, karaciğer ve kas dokusundaki glikojen depolama etkinliğini temsil eder.",
                metrics = listOf(
                    BiomarkerMetricDetail("Açlık Glukozu", "${profile.glucoseMgDl.toInt()} mg/dL", "70-90", profile.glucoseMgDl in 70.0..95.0),
                    BiomarkerMetricDetail("Açlık İnsülini", "${profile.insulinUuMl.toInt()} μU/mL", "< 6.0", profile.insulinUuMl <= 6.0),
                    BiomarkerMetricDetail("HbA1c", "%${profile.hba1cPercent}", "< %5.4", profile.hba1cPercent < 5.5)
                ),
                interventions = listOf(
                    LongevityIntervention("Yemek Sonrası 10 Dk Yürüyüş", "+1.5 Yıl", "Postprandiyal glukoz tepe noktasını %30 düşürür."),
                    LongevityIntervention("Aralıklı Oruç & Sirkadiyen Beslenme", "+1.8 Yıl", "14/10 beslenme penceresi ile otofaji aktivasyonu.")
                )
            ),
            DigitalTwinNode(
                id = "immune",
                title = "Bağışıklık",
                systemName = "İnflamasyon & Sistemik Biyolojik Direnç",
                position = Vector3D(0f, -44f, 6f),
                color = SleekCoral,
                icon = Icons.Default.Security,
                scoreExtractor = { res -> res.reserves.find { it.key == "immune" }?.score ?: 76.0 },
                ageExtractor = { _, p -> "hs-CRP: ${p.hsCrpMgL} mg/L" },
                statusExtractor = { score -> if (score >= 80) "Düşük İnflamasyon" else "Sistemik İnflamasyon Riski" },
                clinicalSummary = "hs-CRP, lökosit fraksiyonu ve sistemik immünosenesans.",
                biologicalMechanism = "Hassas C-reaktif protein (hs-CRP), damar duvarı ve organlardaki düşük dereceli kronik mikroyangıyı (inflammaging) yansıtır.",
                metrics = listOf(
                    BiomarkerMetricDetail("hs-CRP", "${profile.hsCrpMgL} mg/L", "< 0.8", profile.hsCrpMgL < 0.8),
                    BiomarkerMetricDetail("Beyaz Küre (WBC)", "${String.format("%.1f", profile.whiteBloodCellCount)} k/μL", "4.0-6.5", profile.whiteBloodCellCount in 4.0..7.0)
                ),
                interventions = listOf(
                    LongevityIntervention("Anti-İnflamatuar Fitonütrientler", "+1.6 Yıl", "Kurkumin, kuersetin ve sülforafan ile sitokin supresyonu."),
                    LongevityIntervention("Soğuk/Sıcak Termal Maruziyet", "+1.1 Yıl", "Haftada 2-3 sauna seansı ile ısı şoku proteinleri (HSP70) uyarımı.")
                )
            ),
            DigitalTwinNode(
                id = "musculoskeletal",
                title = "Kas & İskelet",
                systemName = "Sarkopeni Direnci & Kemik Mineral Dengesi",
                position = Vector3D(-16f, 64f, 2f),
                color = SleekGreen,
                icon = Icons.Default.FitnessCenter,
                scoreExtractor = { res -> res.reserves.find { it.key == "musculoskeletal" }?.score ?: 88.0 },
                ageExtractor = { _, p -> "Kavrama Gücü: ${p.gripStrengthKg.toInt()} kg" },
                statusExtractor = { score -> if (score >= 80) "Güçlü Kas Kütlesi" else "Kas Kaybı (Sarkopeni) Riski" },
                clinicalSummary = "Kavrama kuvveti ve fonksiyonel fiziksel bağımsızlık kapasitesi.",
                biologicalMechanism = "Kavrama kuvveti tüm nedenlere bağlı mortalite riskinin en güçlü bağımsız fiziksel belirtecidir.",
                metrics = listOf(
                    BiomarkerMetricDetail("Kavrama Gücü", "${profile.gripStrengthKg.toInt()} kg", "> 45 kg", profile.gripStrengthKg >= 45),
                    BiomarkerMetricDetail("Günlük Adım", "${profile.dailySteps} adım", "> 8,000", profile.dailySteps >= 8000)
                ),
                interventions = listOf(
                    LongevityIntervention("Progresif Direnç Antrenmanı", "+3.2 Yıl", "Büyük kas grupları için haftada 3 seans ağırlık egzersizi."),
                    LongevityIntervention("Lösin Zengini Protein (1.6g/kg)", "+1.4 Yıl", "Kas protein sentezi (mTOR) optimizasyonu.")
                )
            ),
            DigitalTwinNode(
                id = "circadian",
                title = "Uyku & Ritüel",
                systemName = "Glimfatik Sistem & Derin Rejenerasyon",
                position = Vector3D(0f, -14f, -8f),
                color = SleekIndigo,
                icon = Icons.Default.NightlightRound,
                scoreExtractor = { res -> res.reserves.find { it.key == "circadian" }?.score ?: 84.0 },
                ageExtractor = { _, p -> "Uyku İndeksi: ${p.psqiSleepScore}/21" },
                statusExtractor = { score -> if (score >= 80) "Optimal Glimfatik Temizlik" else "Yetersiz Derin Uyku" },
                clinicalSummary = "PSQI skoru, sirkadiyen ritim senkronizasyonu ve REM verimliliği.",
                biologicalMechanism = "Derin yavaş dalga uykusu sırasında glimfatik sistem genişleyerek beyindeki amiloid-beta ve hücresel atıkları temizler.",
                metrics = listOf(
                    BiomarkerMetricDetail("PSQI İndeksi", "${profile.psqiSleepScore}/21", "< 5", profile.psqiSleepScore <= 5),
                    BiomarkerMetricDetail("Anksiyete (GAD-7)", "${profile.gad7Score}/21", "< 4", profile.gad7Score <= 4)
                ),
                interventions = listOf(
                    LongevityIntervention("Karanlık ve Soğuk Oda (18°C)", "+2.2 Yıl", "Yatmadan 90 dk önce mavi ışık engeli ve tam karanlık."),
                    LongevityIntervention("Rezonans Nefes Egzersizi", "+1.2 Yıl", "HRV artışı için dakikada 5.5 nefes kutu solunumu.")
                )
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.2.dp, CardBorder, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. TOP HEADER & TELEMETRY
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
                        text = "3D BİYOLOJİK İKİZ",
                        color = SleekPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                // Orbit & Reset Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isAutoRotating) SleekPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant)
                            .border(1.dp, if (isAutoRotating) SleekPrimary else CardBorder, RoundedCornerShape(8.dp))
                            .clickable { isAutoRotating = !isAutoRotating }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isAutoRotating) "Dönüş: AÇIK" else "Dönüş: DURDU",
                            color = if (isAutoRotating) SleekPrimary else TextMuted,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = {
                            yawDeg = 24f
                            pitchDeg = 6f
                            selectedNode = null
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Sıfırla",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 2. INTERACTIVE 3D HOLOGRAPHIC CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF080B11))
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f + 8f
                            val yawRad = (yawDeg * PI / 180f).toFloat()
                            val pitchRad = (pitchDeg * PI / 180f).toFloat()

                            var closestNode: DigitalTwinNode? = null
                            var minDistance = Float.MAX_VALUE

                            organNodes.forEach { node ->
                                val rot = node.position.rotate(yawRad, pitchRad)
                                val proj = rot.project(centerX, centerY)
                                val dx = tapOffset.x - proj.x
                                val dy = tapOffset.y - proj.y
                                val dist = sqrt(dx * dx + dy * dy)
                                if (dist < 46f && dist < minDistance) {
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
                            pitchDeg = (pitchDeg - dragAmount.y * 0.35f).coerceIn(-40f, 40f)
                        }
                    }
            ) {
                // The Drawing Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f + 8f
                    val yawRad = (yawDeg * PI / 180f).toFloat()
                    val pitchRad = (pitchDeg * PI / 180f).toFloat()

                    // A) Draw Holographic Ground Grid & Rotating Rings
                    drawHolographicPlatform(centerX, centerY + 148f, yawRad)

                    // B) Project All Vertices to 2D Screen Space
                    val projectedVertices = meshVertices.map { v ->
                        val rot = v.rotate(yawRad, pitchRad)
                        Pair(rot, rot.project(centerX, centerY))
                    }

                    // C) Draw Anatomical Volumetric Torso & Head Translucent Polygons (Body Mass)
                    drawVolumetricBodySilhouette(projectedVertices)

                    // D) Draw Wireframe Volumetric Edges with Depth Shading
                    meshEdges.forEach { edge ->
                        if (edge.fromIndex < projectedVertices.size && edge.toIndex < projectedVertices.size) {
                            val (v1Rot, p1) = projectedVertices[edge.fromIndex]
                            val (v2Rot, p2) = projectedVertices[edge.toIndex]
                            val avgZ = (v1Rot.z + v2Rot.z) / 2f

                            // Dynamic depth shading: front edges glow vibrant cyan; back edges stay deep indigo/slate
                            val depthFactor = ((avgZ + 50f) / 100f).coerceIn(0.18f, 1.0f)
                            val edgeColor = if (edge.isPrimaryContour) {
                                SleekPrimary.copy(alpha = 0.35f + depthFactor * 0.55f)
                            } else {
                                Color(0xFF38BDF8).copy(alpha = 0.12f + depthFactor * 0.30f)
                            }
                            val edgeWidth = if (edge.isPrimaryContour) 1.8f * depthFactor + 0.8f else 1.0f

                            drawLine(
                                color = edgeColor,
                                start = p1,
                                end = p2,
                                strokeWidth = edgeWidth,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // E) Animated Circulatory Energy Pulses (Flowing Light Packets)
                    drawCirculatoryPulses(projectedVertices, vascularPulseProgress)

                    // F) Medical Tomography Laser Scan Sweep Line
                    drawMedicalScanBeam(centerX, centerY, scanProgress, size.width)

                    // G) Depth-Sorted Interactive Organ Nodes
                    val sortedOrganNodes = organNodes.map { node ->
                        val rot = node.position.rotate(yawRad, pitchRad)
                        Pair(node, rot)
                    }.sortedBy { it.second.z }

                    sortedOrganNodes.forEach { (node, rotPos) ->
                        val proj = rotPos.project(centerX, centerY)
                        val isSelected = selectedNode?.id == node.id
                        val depthAlpha = ((rotPos.z + 60f) / 120f).coerceIn(0.4f, 1.0f)

                        // Central spine connection beam
                        val spineRot = Vector3D(0f, node.position.y, 0f).rotate(yawRad, pitchRad)
                        val spineProj = spineRot.project(centerX, centerY)
                        drawLine(
                            color = node.color.copy(alpha = if (isSelected) 0.85f else 0.25f * depthAlpha),
                            start = spineProj,
                            end = proj,
                            strokeWidth = if (isSelected) 2.2f else 1.0f
                        )

                        val pulseScale = if (node.id == "vascular") pulseAnim.value else 1.0f
                        val auraRadius = (if (isSelected) 18f else 10f) * pulseScale

                        // Glowing Aura Ring
                        drawCircle(
                            color = node.color.copy(alpha = if (isSelected) 0.45f else 0.22f * depthAlpha),
                            radius = auraRadius,
                            center = proj
                        )

                        // Selected Focus Reticle
                        if (isSelected) {
                            drawCircle(
                                color = Color.White,
                                radius = auraRadius + 6f,
                                center = proj,
                                style = Stroke(
                                    width = 1.4f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                )
                            )
                        }

                        // Core Organ Sphere
                        drawCircle(
                            color = node.color,
                            radius = if (isSelected) 7.5f else 5f,
                            center = proj
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.0f,
                            center = proj
                        )
                    }
                }

                // HUD Overlay: LRS Badge & Active Scan Status
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xD90F131C))
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SleekPrimary))
                            Text(
                                text = "LRS: ${twinResult.longevityReserveScore.toInt()}/100 • PhenoAge: ${twinResult.phenoAge} Yaş",
                                color = SleekPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Camera Angle Quick Buttons (Ön, Yan, 3D)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val angles = listOf(
                        Triple("ÖN", 0f, 0f),
                        Triple("YAN", 90f, 0f),
                        Triple("3D", 35f, 15f)
                    )
                    angles.forEach { (label, y, p) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xB3111520))
                                .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    yawDeg = y
                                    pitchDeg = p
                                    isAutoRotating = false
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = label, color = TextSecondary, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Inspect Callout Button
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
                                    text = "Klinik Raporu İncele",
                                    color = node.color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = "Genişlet",
                                    tint = node.color,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. ORGAN SYSTEM FILTER CHIPS (Tümü, Kalp, Beyin, Metabolizma vs.)
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
                                color = if (isSel) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 4. SELECTED ORGAN COMPACT HUD CARD (Clean, High Contrast, Readable)
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
                                        onClick = { selectedNode = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = TextMuted, modifier = Modifier.size(16.dp))
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
                                onClick = { showFullDeepDiveDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = node.color.copy(alpha = 0.15f),
                                    contentColor = node.color
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Ayrıntılı Klinik Rapor & Müdahaleler", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // 5. FULL CLINICAL DEEP DIVE DIALOG
    if (showFullDeepDiveDialog && selectedNode != null) {
        val node = selectedNode!!
        val score = node.scoreExtractor(twinResult)

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
                            IconButton(onClick = { showFullDeepDiveDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = TextMuted)
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
                                Text("BİYOLOJİK MEKANİZMA", color = node.color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                Text(node.biologicalMechanism, color = TextSecondary, fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }

                    item {
                        Text("KLİNİK BİYOBELİRTEÇLER", color = TextMuted, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
                                Text("Hedef: ${m.optimalRange}", color = TextMuted, fontSize = 10.sp)
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
                        Text("LONGEVITY MÜDAHALE PROTOKOLLERİ", color = TextMuted, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
                            Text("Modele Geri Dön", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// DRAWING HELPER FUNCTIONS (Volumetric silhouette, laser scan, pulses, platform)
// =========================================================================

private fun DrawScope.drawHolographicPlatform(centerX: Float, centerY: Float, yawRad: Float) {
    for (r in 1..3) {
        drawOval(
            color = SleekPrimary.copy(alpha = 0.04f * r),
            topLeft = Offset(centerX - r * 48f, centerY - r * 15f),
            size = Size(r * 96f, r * 30f),
            style = Stroke(width = 1.0f)
        )
    }

    for (i in 0 until 8) {
        val angle = yawRad + (i * PI.toFloat() / 4f)
        val dx = cos(angle) * 115f
        val dy = sin(angle) * 36f
        drawLine(
            color = SleekPrimary.copy(alpha = 0.05f),
            start = Offset(centerX, centerY),
            end = Offset(centerX + dx, centerY + dy),
            strokeWidth = 0.8f
        )
    }
}

private fun DrawScope.drawVolumetricBodySilhouette(projectedVertices: List<Pair<Vector3D, Offset>>) {
    if (projectedVertices.size < 66) return

    // Cranium head volumetric mass
    val headPath = Path().apply {
        val pCrown = projectedVertices[0].second
        val pLeft = projectedVertices[3].second
        val pChin = projectedVertices[6].second
        val pRight = projectedVertices[4].second

        moveTo(pCrown.x, pCrown.y)
        lineTo(pLeft.x, pLeft.y)
        lineTo(pChin.x, pChin.y)
        lineTo(pRight.x, pRight.y)
        close()
    }
    drawPath(headPath, color = SleekPrimary.copy(alpha = 0.06f))

    // Torso volumetric chest mass
    val torsoPath = Path().apply {
        val pClavLeft = projectedVertices[16].second
        val pClavRight = projectedVertices[17].second
        val pHipRight = projectedVertices[31].second
        val pPelvis = projectedVertices[32].second
        val pHipLeft = projectedVertices[30].second

        moveTo(pClavLeft.x, pClavLeft.y)
        lineTo(pClavRight.x, pClavRight.y)
        lineTo(pHipRight.x, pHipRight.y)
        lineTo(pPelvis.x, pPelvis.y)
        lineTo(pHipLeft.x, pHipLeft.y)
        close()
    }
    drawPath(torsoPath, color = SleekIndigo.copy(alpha = 0.05f))
}

private fun DrawScope.drawCirculatoryPulses(
    projectedVertices: List<Pair<Vector3D, Offset>>,
    pulseProgress: Float
) {
    if (projectedVertices.size < 66) return

    // Cardiac center is around mid-sternum
    val heartPos = projectedVertices[11].second

    // Pulse paths: Heart -> Head, Heart -> Left Hand, Heart -> Right Hand, Heart -> Left Foot, Heart -> Right Foot
    val targetPoints = listOf(
        projectedVertices[0].second,  // Head
        projectedVertices[41].second, // Left Hand
        projectedVertices[49].second, // Right Hand
        projectedVertices[57].second, // Left Foot
        projectedVertices[65].second  // Right Foot
    )

    targetPoints.forEach { target ->
        val pulseX = heartPos.x + (target.x - heartPos.x) * pulseProgress
        val pulseY = heartPos.y + (target.y - heartPos.y) * pulseProgress

        drawCircle(
            color = SleekRose.copy(alpha = 0.8f * (1f - pulseProgress * 0.5f)),
            radius = 2.4f,
            center = Offset(pulseX, pulseY)
        )
    }
}

private fun DrawScope.drawMedicalScanBeam(
    centerX: Float,
    centerY: Float,
    scanProgress: Float,
    canvasWidth: Float
) {
    // Scan sweeps vertically across height
    val scanY = (centerY - 130f) + scanProgress * 260f

    // Glowing laser beam line
    drawLine(
        brush = Brush.horizontalGradient(
            listOf(
                Color.Transparent,
                SleekCyan.copy(alpha = 0.65f),
                Color.White.copy(alpha = 0.9f),
                SleekCyan.copy(alpha = 0.65f),
                Color.Transparent
            )
        ),
        start = Offset(centerX - 95f, scanY),
        end = Offset(centerX + 95f, scanY),
        strokeWidth = 2.0f
    )

    // Soft laser scan halo
    drawOval(
        brush = Brush.radialGradient(
            listOf(SleekCyan.copy(alpha = 0.20f), Color.Transparent),
            center = Offset(centerX, scanY),
            radius = 65f
        ),
        topLeft = Offset(centerX - 85f, scanY - 8f),
        size = Size(170f, 16f)
    )
}
