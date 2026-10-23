package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiMorningBriefingWidget
import com.example.ui.components.HologramTwinView
import com.example.ui.components.DominantAgingPhenotypeCard
import com.example.ui.components.HealthConnectSyncCard
import com.example.ui.components.LiveEcgMonitor
import com.example.ui.components.heartbeatPulse
import com.example.ui.components.rememberEcgClock
import com.example.service.LongevityCalculatorService
import com.example.ui.components.HeroAgeCard
import com.example.ui.components.ProjectedIndependenceCard
import com.example.ui.components.RadarChart
import com.example.ui.components.ReserveItemCard
import com.example.ui.theme.AppTheme
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCyan
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LongevityViewModel
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: LongevityViewModel,
    onNavigateToInput: () -> Unit,
    onNavigateToTests: () -> Unit,
    onNavigateToForecast: () -> Unit,
    onNavigateToOcr: () -> Unit,
    onNavigateToWhitepaper: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val profile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()
    val twinResult by viewModel.twinResult.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUserProfile.collectAsStateWithLifecycle()

    var savedMessage by remember { mutableStateOf<String?>(null) }
    // Focused Dashboard View Modes: "3D" (Volumetric Twin), "AI_BRIEF" (Gemini Analysis), "MATRIX" (8-Axis Radar & Reserves)
    var dashboardTab by remember { mutableStateOf("3D") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (colors.isDark) {
                    Brush.verticalGradient(listOf(Color(0xFF090D16), Color(0xFF0F1728), Color(0xFF090D16)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFFD4E5ED), Color(0xFFC8DEE8), Color(0xFFCFE1EB), Color(0xFFC5DDE7)))
                }
            ),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. HERO AGE & VELOCITY CARD (High Contrast, Clear Metrics)
        item {
            HeroAgeCard(result = twinResult)
        }

        // 2. LIVE RESTING HEART RATE CARDIOGRAM
        item {
            RestingHeartRateCard(
                bpm = profile.restingPulseBpm,
                isLiveSynced = profile.isHealthConnectSynced
            )
        }

        // 3. AT-A-GLANCE VITALS (Longevity Reserve Score & Daily Steps)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val lrs = twinResult.longevityReserveScore
                VitalStatCard(
                    label = "LONGEVITY RESERVE",
                    icon = Icons.Default.Shield,
                    accent = colors.cyan,
                    value = "${lrs.roundToInt()}",
                    unit = "/100",
                    caption = when {
                        lrs >= 80 -> "Robust reserve"
                        lrs >= 60 -> "Moderate reserve"
                        else -> "Low reserve"
                    },
                    progress = (lrs / 100.0).toFloat(),
                    modifier = Modifier.weight(1f)
                )

                val stepGoal = currentUser.dailyStepGoal.coerceAtLeast(1)
                VitalStatCard(
                    label = "DAILY STEPS",
                    icon = Icons.Default.DirectionsWalk,
                    accent = colors.green,
                    value = String.format(Locale.US, "%,d", profile.dailySteps),
                    unit = "",
                    caption = "${profile.dailySteps * 100 / stepGoal}% of ${String.format(Locale.US, "%,d", stepGoal)} goal",
                    progress = profile.dailySteps.toFloat() / stepGoal,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. BIOLOGICAL SCENARIOS (reference profiles, deltas computed by the PhenoAge engine)
        item {
            val scenarios = remember(viewModel.presetProfiles) {
                listOf(
                    "super_ager" to "Super-Ager",
                    "population_median" to "Median",
                    "cardiometabolic" to "Cardio-Risk"
                ).mapNotNull { (id, shortTitle) ->
                    viewModel.presetProfiles.find { it.id == id }?.let { preset ->
                        Triple(preset, shortTitle, LongevityCalculatorService.computeDigitalTwin(preset.profile).ageDifference)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BIOLOGICAL SCENARIOS",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Tap to simulate",
                        color = colors.textMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scenarios.forEach { (preset, shortTitle, delta) ->
                        ScenarioCard(
                            title = shortTitle,
                            delta = delta,
                            isActive = profile == preset.profile,
                            onClick = { viewModel.applyPreset(preset.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. SEGMENTED HUB BAR (3D Twin | AI Briefing | Bio-Matrix)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (colors.isDark) Color(0xFF161F30) else colors.surface)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("3D", "3D Twin", Icons.Default.ViewInAr),
                    Triple("AI_BRIEF", "AI Briefing", Icons.Default.Lightbulb),
                    Triple("MATRIX", "Bio-Matrix", Icons.Default.AutoAwesome)
                )

                tabs.forEach { (mode, label, icon) ->
                    val isSelected = dashboardTab == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    !isSelected -> Color.Transparent
                                    colors.isDark -> Color(0xFF0F172A)
                                    else -> colors.primary.copy(alpha = 0.12f)
                                }
                            )
                            .border(
                                width = if (isSelected) 1.2.dp else 0.dp,
                                color = if (isSelected) colors.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { dashboardTab = mode }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = if (isSelected) colors.primary else colors.textMuted
                            )
                            Text(
                                text = label,
                                color = when {
                                    !isSelected -> colors.textSecondary
                                    colors.isDark -> Color.White
                                    else -> colors.primary
                                },
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 6. TABBED FOCUSED DISPLAY
        when (dashboardTab) {
            "3D" -> {
                item {
                    HologramTwinView(
                        profile = profile,
                        twinResult = twinResult
                    )
                }
            }
            "AI_BRIEF" -> {
                item {
                    AiMorningBriefingWidget(
                        viewModel = viewModel,
                        profile = profile,
                        result = twinResult
                    )
                }
            }
            "MATRIX" -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(22.dp)),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "BIOLOGICAL RESERVE MAP",
                                    color = colors.secondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "8-Axis Twin Matrix",
                                    color = colors.primary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            RadarChart(
                                reserves = twinResult.reserves,
                                longevityReserveScore = twinResult.longevityReserveScore
                            )

                            DominantAgingPhenotypeCard(result = twinResult)
                        }
                    }
                }

                items(twinResult.reserves) { reserve ->
                    ReserveItemCard(reserve = reserve)
                }
            }
        }

        // 7. HEALTH CONNECT & FUNCTIONAL INDEPENDENCE
        item {
            HealthConnectSyncCard(viewModel = viewModel)
        }

        item {
            ProjectedIndependenceCard(
                projectedYears = twinResult.projectedIndependenceYears,
                chronoAge = twinResult.chronologicalAge
            )
        }

        // 8. QUICK ACTION TILES (2x2 Clean Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "QUICK ACTIONS & MODULES",
                    color = colors.textSecondary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToOcr() },
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                            }
                            Text("AI Lab Report OCR", color = colors.textPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Scan and digitize a lab report", color = colors.textSecondary, fontSize = 10.sp, lineHeight = 13.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                            .clickable { onNavigateToWhitepaper() },
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.amber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = colors.amber, modifier = Modifier.size(16.dp))
                            }
                            Text("Whitepaper 2026", color = colors.textPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Levine PhenoAge & 8 clinical models", color = colors.textSecondary, fontSize = 10.sp, lineHeight = 13.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToInput,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surface,
                            contentColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Biomarkers", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToTests,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surface,
                            contentColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp), tint = colors.green)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Live Tests", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Snapshot feeds the "Historical Assessments" list in Forecast › Medical Report
                Button(
                    onClick = {
                        viewModel.saveCurrentAssessment("Snapshot")
                        savedMessage = "Snapshot saved · see Forecast › Medical Report"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Snapshot", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                savedMessage?.let { message ->
                    LaunchedEffect(message) {
                        delay(3000)
                        savedMessage = null
                    }
                    Text(
                        text = "✓ $message",
                        color = colors.green,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/** Resting heart rate with a live, rate-accurate cardiogram strip and a heart icon beating on each R wave. */
@Composable
private fun RestingHeartRateCard(
    bpm: Double,
    isLiveSynced: Boolean
) {
    val colors = AppTheme.colors
    val clock = rememberEcgClock()
    val traceColor = if (colors.isDark) Color(0xFFFF4D6D) else colors.rose

    val (statusText, statusColor) = when {
        bpm < 50 -> "Low · athletic range" to colors.amber
        bpm <= 65 -> "Optimal" to colors.green
        bpm <= 75 -> "Normal" to colors.primary
        bpm <= 90 -> "Elevated" to colors.amber
        else -> "High" to colors.coral
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(traceColor.copy(alpha = 0.14f))
                        .border(1.dp, traceColor.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Heartbeat",
                        tint = traceColor,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer {
                                val scale = 1f + 0.24f * heartbeatPulse(clock.floatValue, bpm)
                                scaleX = scale
                                scaleY = scale
                            }
                    )
                }
                Column {
                    Text(
                        text = "RESTING HEART RATE",
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${bpm.roundToInt()}",
                            color = colors.textPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "bpm",
                            color = colors.textMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 5.dp)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val sourceColor = if (isLiveSynced) colors.green else colors.textMuted
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(sourceColor.copy(alpha = 0.14f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .graphicsLayer { alpha = 0.3f + 0.7f * heartbeatPulse(clock.floatValue, bpm) }
                            .clip(CircleShape)
                            .background(traceColor)
                    )
                    Text(
                        text = if (isLiveSynced) "HEALTH CONNECT" else "PROFILE VALUE",
                        color = sourceColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.4.sp
                    )
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bedside-monitor strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (colors.isDark) Color(0xFF0A0F1A) else Color(0xFFFFF5F7))
                .border(1.dp, traceColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
        ) {
            LiveEcgMonitor(
                bpm = bpm,
                clock = clock,
                traceColor = traceColor,
                gridColor = traceColor.copy(alpha = if (colors.isDark) 0.3f else 0.35f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
            )
            Text(
                text = "II",
                color = traceColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 5.dp)
            )
            Text(
                text = "25 mm/s",
                color = traceColor.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 5.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "R–R interval ${String.format(Locale.US, "%.2f", 60.0 / bpm.coerceAtLeast(1.0))} s",
                color = colors.textMuted,
                fontSize = 11.sp
            )
            Text(
                text = "Optimal 50–65 bpm",
                color = colors.textMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun VitalStatCard(
    label: String,
    icon: ImageVector,
    accent: Color,
    value: String,
    unit: String,
    caption: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(14.dp))
            }
            Text(
                text = label,
                color = colors.textSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.4.sp,
                maxLines = 1
            )
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    color = colors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(CircleShape)
                .background(colors.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0.03f, 1f))
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
        }

        Text(
            text = caption,
            color = colors.textMuted,
            fontSize = 10.5.sp,
            maxLines = 1
        )
    }
}

/** Reference-profile card; the curve rises for a younger biological age and falls for an older one. */
@Composable
private fun ScenarioCard(
    title: String,
    delta: Double,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val accent = when {
        delta <= -2.0 -> colors.primary
        delta < 2.0 -> if (colors.isDark) Color(0xFF2DD4BF) else Color(0xFF0F766E)
        else -> colors.coral
    }
    val trend = (-delta / 12.0).coerceIn(-0.38, 0.38).toFloat()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) accent.copy(alpha = 0.10f) else colors.surface)
            .border(if (isActive) 2.dp else 1.2.dp, accent.copy(alpha = if (isActive) 1f else 0.55f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isActive) "✓ $title" else title,
                color = colors.textPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Canvas(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                val w = size.width
                val h = size.height
                val startY = h * 0.55f
                val endY = h * (0.55f - trend)
                val path = Path().apply {
                    moveTo(0f, startY)
                    cubicTo(w * 0.4f, startY, w * 0.6f, endY, w, endY)
                }
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(fillPath, brush = Brush.verticalGradient(listOf(accent.copy(alpha = 0.22f), Color.Transparent)))
                drawPath(path, color = accent, style = Stroke(width = 2.dp.toPx()))
                drawCircle(accent, radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w, endY))
            }
            Text(
                text = "BioAge ${if (delta > 0) "+" else ""}${String.format(Locale.US, "%.1f", delta)}y",
                color = accent,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}
