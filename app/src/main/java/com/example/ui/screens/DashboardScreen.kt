package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
            .background(colors.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. HERO AGE & VELOCITY CARD (High Contrast, Clear Metrics)
        item {
            HeroAgeCard(result = twinResult)
        }

        // 2. AT-A-GLANCE VITALS ROW (Glanceable 3 key metrics)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Longevity Reserve Score
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(12.dp))
                            Text("LRS SKORU", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${twinResult.longevityReserveScore.toInt()}/100", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Resting Heart Rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = SleekRose, modifier = Modifier.size(12.dp))
                            Text("DİNLENİK NABIZ", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${profile.restingPulseBpm.toInt()} bpm", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Daily Steps
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = SleekGreen, modifier = Modifier.size(12.dp))
                            Text("GÜNLÜK ADIM", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("${profile.dailySteps}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 3. BIOLOGICAL PRESET PROFILES (Clean, compact horizontal selector)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "BİYOLOJİK SENARYOLAR",
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.presetProfiles) { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface)
                                .border(1.dp, colors.cardBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.applyPreset(preset.id)
                                    savedMessage = null
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = preset.title,
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = preset.tag,
                                    color = SleekPrimary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. SEGMENTED HUB BAR (3D Dijital İkiz | AI Brifing | Biyolojik Matris)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.primary else Color.Transparent)
                            .clickable { dashboardTab = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) colors.onPrimary else TextMuted
                            )
                            Text(
                                text = label,
                                color = if (isSelected) colors.onPrimary else TextSecondary,
                                fontSize = 11.5.sp,
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
                                    color = SleekSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "8-Eksenli İkiz Matrisi",
                                    color = SleekPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
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
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
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
                                    .background(SleekPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(16.dp))
                            }
                            Text("AI Tahlil OCR", color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Laboratuvar raporunu tara ve dijitalleştir", color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
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
                                    .background(SleekAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = SleekAmber, modifier = Modifier.size(16.dp))
                            }
                            Text("Whitepaper 2026", color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("Levine PhenoAge ve 8 klinik model", color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToInput,
                        modifier = Modifier.weight(1f).height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(15.dp), tint = SleekPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Biyobelirteçler", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToTests,
                        modifier = Modifier.weight(1f).height(42.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(15.dp), tint = SleekGreen)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Canlı Testler", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
