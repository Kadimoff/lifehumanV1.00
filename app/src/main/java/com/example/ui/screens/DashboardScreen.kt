package com.example.ui.screens

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
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MultilineChart
import androidx.compose.material.icons.filled.Science
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
import com.example.ui.components.LongevityEmojiAvatar
import com.example.ui.theme.AppTheme
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
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
    var twinViewMode by remember { mutableStateOf("3D") } // "3D", "RADAR", "BOTH"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Profile & Status
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.clickable { viewModel.openEntryMenu() }
                ) {
                    LongevityEmojiAvatar(
                        emoji = currentUser.avatarEmoji,
                        size = 44.dp,
                        fontSize = 22f,
                        onClick = { viewModel.openEntryMenu() }
                    )

                    Column {
                        Text(
                            text = "LIFEMAP HUMAN™",
                            color = colors.primary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${currentUser.name}, ${profile.chronologicalAge.toInt()} yrs",
                            color = colors.textPrimary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Bookmark / Save assessment
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.cardBorder, CircleShape)
                        .clickable {
                            viewModel.saveCurrentAssessment()
                            savedMessage = "Saved"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Save Assessment",
                        tint = if (savedMessage != null) colors.green else colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. Preset Biological Profiles
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BIOLOGICAL PRESET PROFILES",
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.presetProfiles) { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkSurface)
                                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.applyPreset(preset.id)
                                    savedMessage = null
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = preset.title,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = preset.tag,
                                    color = SleekPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. AI Morning Dashboard Summary Widget
        item {
            AiMorningBriefingWidget(
                viewModel = viewModel,
                profile = profile,
                result = twinResult
            )
        }

        // 3.1 Hero Age Delta Card
        item {
            HeroAgeCard(result = twinResult)
        }

        // 3.5 Health Connect Live Biometric Sync Card
        item {
            HealthConnectSyncCard(viewModel = viewModel)
        }

        // 4. View Switcher: 3D Twin vs 8-Axis Radar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurface)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("3D", "3D Digital Twin", Icons.Default.ViewInAr),
                    Triple("RADAR", "8-Axis Radar", Icons.Default.AutoAwesome),
                    Triple("BOTH", "Dual View", Icons.Default.Description)
                )
                tabs.forEach { (mode, label, icon) ->
                    val isSelected = twinViewMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SleekOnPrimary else Color.Transparent)
                            .border(1.dp, if (isSelected) SleekPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { twinViewMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isSelected) SleekPrimary else TextMuted
                            )
                            Text(
                                text = label,
                                color = if (isSelected) SleekPrimary else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // 5. 3D Digital Twin Visualizer
        if (twinViewMode == "3D" || twinViewMode == "BOTH") {
            item {
                DigitalTwin3DVisualizer(
                    profile = profile,
                    twinResult = twinResult
                )
            }
        }

        // 6. Dynamic 8-Axis Radar Chart
        if (twinViewMode == "RADAR" || twinViewMode == "BOTH") {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
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
                                color = SleekSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "8-Axis Twin Matrix",
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
        }

        // 7. Projected Functional Independence
        item {
            ProjectedIndependenceCard(
                projectedYears = twinResult.projectedIndependenceYears,
                chronoAge = twinResult.chronologicalAge
            )
        }

        // 8. Quick Feature Action Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToOcr() },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(SleekPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(16.dp))
                            }
                            Text("AI Vision OCR", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Scan & digitize lab reports with Nemotron™ AI Vision", color = TextSecondary, fontSize = 10.5.sp, lineHeight = 14.sp)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToWhitepaper() },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(SleekAmber.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = SleekAmber, modifier = Modifier.size(16.dp))
                            }
                            Text("Whitepaper 2026", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Mathematical equations, 8 models & Flutter code", color = TextSecondary, fontSize = 10.5.sp, lineHeight = 14.sp)
                    }
                }
            }
        }

        // Quick Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToInput,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SleekOnPrimary,
                        contentColor = SleekPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BioData Inputs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToTests,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Tests", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 9. System Reserve Scorecards
        item {
            Text(
                text = "SYSTEM RESERVE SCORECARDS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(twinResult.reserves) { reserve ->
            ReserveItemCard(reserve = reserve)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
