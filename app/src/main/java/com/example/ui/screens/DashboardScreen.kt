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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiMorningBriefingWidget
import com.example.ui.components.DigitalTwin3DVisualizer
import com.example.ui.components.DominantAgingPhenotypeCard
import com.example.ui.components.HealthConnectSyncCard
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

        // 2. AT-A-GLANCE VITALS ROW (Glassmorphism 3 Key Metrics with Waveforms & 3D Glowing Orbs)
        item {
            val vitalCardBg = if (colors.isDark) colors.surface.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.88f)
            val vitalCardBorder = if (colors.isDark) colors.cardBorder else Color.White.copy(alpha = 0.95f)
            val vitalLabelColor = if (colors.isDark) colors.textSecondary else Color(0xFF334155)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Longevity Reserve Score Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(vitalCardBg)
                        .border(1.2.dp, vitalCardBorder, RoundedCornerShape(18.dp))
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF00B4D8), modifier = Modifier.size(13.dp))
                            Text("LRS SKORU", color = vitalLabelColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini equalizer bars canvas
                            Canvas(modifier = Modifier.size(width = 38.dp, height = 20.dp)) {
                                val barWidth = 3.dp.toPx()
                                val heights = listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 0.5f)
                                heights.forEachIndexed { i, hRatio ->
                                    val left = i * (barWidth + 2.dp.toPx())
                                    val top = size.height * (1f - hRatio)
                                    drawRoundRect(
                                        color = Color(0xFF00B4D8).copy(alpha = 0.9f),
                                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                        size = androidx.compose.ui.geometry.Size(barWidth, size.height * hRatio),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                                    )
                                }
                            }

                            // Glowing 3D circular orb with shield
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFF00E5FF).copy(alpha = 0.45f),
                                                Color(0xFF0077B6).copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.75f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (colors.isDark) Color(0xFF00E5FF) else Color(0xFF0096C7),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Resting Heart Rate Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(vitalCardBg)
                        .border(1.2.dp, vitalCardBorder, RoundedCornerShape(18.dp))
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(13.dp))
                            Text("DİNLENİK NABIZ", color = vitalLabelColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini red ECG pulse wave
                            Canvas(modifier = Modifier.size(width = 38.dp, height = 20.dp)) {
                                val w = size.width
                                val h = size.height
                                val p = Path().apply {
                                    moveTo(0f, h * 0.5f)
                                    lineTo(w * 0.25f, h * 0.5f)
                                    lineTo(w * 0.35f, h * 0.2f)
                                    lineTo(w * 0.5f, h * 0.9f)
                                    lineTo(w * 0.65f, h * 0.1f)
                                    lineTo(w * 0.75f, h * 0.5f)
                                    lineTo(w, h * 0.5f)
                                }
                                drawPath(path = p, color = Color(0xFFFF5252).copy(alpha = 0.85f), style = Stroke(width = 2.dp.toPx()))
                            }

                            // Glowing 3D circular orb with heart
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFFF5252).copy(alpha = 0.45f),
                                                Color(0xFFDC2626).copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.75f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5252),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Daily Steps Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(vitalCardBg)
                        .border(1.2.dp, vitalCardBorder, RoundedCornerShape(18.dp))
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                            Text("GÜNLÜK ADIM", color = vitalLabelColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mini step histogram bars
                            Canvas(modifier = Modifier.size(width = 38.dp, height = 20.dp)) {
                                val barWidth = 3.dp.toPx()
                                val heights = listOf(0.3f, 0.5f, 0.8f, 0.6f, 0.9f, 0.7f, 0.85f)
                                heights.forEachIndexed { i, hRatio ->
                                    val left = i * (barWidth + 2.dp.toPx())
                                    val top = size.height * (1f - hRatio)
                                    drawRoundRect(
                                        color = Color(0xFF10B981).copy(alpha = 0.88f),
                                        topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                        size = androidx.compose.ui.geometry.Size(barWidth, size.height * hRatio),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                                    )
                                }
                            }

                            // Glowing 3D circular orb with walking icon
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFF10B981).copy(alpha = 0.45f),
                                                Color(0xFF047857).copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.75f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. BİYOLOJİK SENARYOLAR (3 Side-by-Side Glassmorphic Wave Cards)
        item {
            val scenarioCardBg = if (colors.isDark) colors.surface.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.88f)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BİYOLOJİK SENARYOLAR",
                    color = if (colors.isDark) colors.textSecondary else Color(0xFF334155),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Scenario 1: Optimal Youth (-8.4 yrs)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(scenarioCardBg)
                            .border(1.4.dp, Color(0xFF00E5FF), RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.presetProfiles.find { it.id == "super_ager" }?.let {
                                    viewModel.applyPreset(it.id)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Wave Canvas
                            Canvas(modifier = Modifier.fillMaxWidth().height(36.dp)) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.75f)
                                    cubicTo(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.25f, w, h * 0.25f)
                                }
                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(w, h)
                                    lineTo(0f, h)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFF00E5FF).copy(alpha = 0.25f), Color.Transparent)
                                    )
                                )
                                drawPath(
                                    path = path,
                                    color = if (colors.isDark) Color(0xFF00E5FF) else Color(0xFF00B4D8),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                            Text(
                                text = "BioAge: -8.4 yrs",
                                color = if (colors.isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }

                    // Scenario 2: Baseline (+0.2 yrs)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(scenarioCardBg)
                            .border(1.2.dp, if (colors.isDark) Color(0xFF2DD4BF).copy(alpha = 0.7f) else Color(0xFF5EEAD4), RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.presetProfiles.find { it.id == "population_median" }?.let {
                                    viewModel.applyPreset(it.id)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Wave Canvas with central indicator dot
                            Canvas(modifier = Modifier.fillMaxWidth().height(36.dp)) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.55f)
                                    cubicTo(w * 0.3f, h * 0.45f, w * 0.7f, h * 0.55f, w, h * 0.45f)
                                }
                                drawPath(
                                    path = path,
                                    color = if (colors.isDark) Color(0xFF2DD4BF) else Color(0xFF0D9488),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = if (colors.isDark) Color(0xFF2DD4BF) else Color(0xFF0D9488),
                                    radius = 3.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
                                )
                            }
                            Text(
                                text = "BioAge: +0.2 yrs",
                                color = if (colors.isDark) Color(0xFF2DD4BF) else Color(0xFF0F766E),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }

                    // Scenario 3: High Risk (+6.8 yrs)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(scenarioCardBg)
                            .border(1.4.dp, Color(0xFFFF6B6B), RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.presetProfiles.find { it.id == "cardiometabolic" }?.let {
                                    viewModel.applyPreset(it.id)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Warning Wave Canvas
                            Canvas(modifier = Modifier.fillMaxWidth().height(36.dp)) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.85f)
                                    cubicTo(w * 0.35f, h * 0.8f, w * 0.65f, h * 0.2f, w, h * 0.15f)
                                }
                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(w, h)
                                    lineTo(0f, h)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFFF5252).copy(alpha = 0.25f), Color.Transparent)
                                    )
                                )
                                drawPath(
                                    path = path,
                                    color = if (colors.isDark) Color(0xFFFB7185) else Color(0xFFEA580C),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                            Text(
                                text = "BioAge: +6.8 yrs",
                                color = if (colors.isDark) Color(0xFFFB7185) else Color(0xFFEA580C),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 4. SEGMENTED HUB BAR (3D İkiz | AI Brifing | Biyo-Matris)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (colors.isDark) Color(0xFF161F30) else Color(0xFF2C3E50))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("3D", "3D İkiz", Icons.Default.ViewInAr),
                    Triple("AI_BRIEF", "AI Brifing", Icons.Default.Lightbulb),
                    Triple("MATRIX", "Biyo-Matris", Icons.Default.AutoAwesome)
                )

                tabs.forEach { (mode, label, icon) ->
                    val isSelected = dashboardTab == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF0F172A) else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.dp,
                                color = if (isSelected) Color(0xFF00E5FF) else Color.Transparent,
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
                                tint = if (isSelected) Color(0xFF00E5FF) else Color(0xFF94A3B8)
                            )
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 5. TABBED FOCUSED DISPLAY
        when (dashboardTab) {
            "3D" -> {
                item {
                    DigitalTwin3DVisualizer(
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
                                    text = "BİYOLOJİK REZERV HARİTASI",
                                    color = colors.secondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "8-Eksenli İkiz Matrisi",
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

        // 6. HEALTH CONNECT & FUNCTIONAL INDEPENDENCE
        item {
            HealthConnectSyncCard(viewModel = viewModel)
        }

        item {
            ProjectedIndependenceCard(
                projectedYears = twinResult.projectedIndependenceYears,
                chronoAge = twinResult.chronologicalAge
            )
        }

        // 7. QUICK ACTION TILES (2x2 Clean Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "HIZLI EYLEMLER & MODÜLLER",
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
                            Text("AI Tahlil OCR", color = colors.textPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Laboratuvar raporunu tara ve dijitalleştir", color = colors.textSecondary, fontSize = 10.sp, lineHeight = 13.sp)
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
                            Text("Levine PhenoAge ve 8 klinik model", color = colors.textSecondary, fontSize = 10.sp, lineHeight = 13.sp)
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
                        Text("Biyobelirteçler", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                        Text("Canlı Testler", color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
