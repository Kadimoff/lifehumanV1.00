package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.data.model.MorningLongevityBriefing
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCyan
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekIndigo
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LongevityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun AiMorningBriefingWidget(
    viewModel: LongevityViewModel,
    profile: BiomarkerProfile,
    result: BiologicalTwinResult,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val briefing by viewModel.morningBriefing.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingBriefing.collectAsStateWithLifecycle()
    var isExpanded by remember { mutableStateOf(false) }

    val todayDateFormatted = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val bioAgeDiff = (result.phenoAge - profile.chronologicalAge * 10.0).roundToInt() / 10.0
    val isYounger = bioAgeDiff <= 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        SleekCyan.copy(alpha = 0.6f),
                        SleekPrimary.copy(alpha = 0.4f),
                        SleekIndigo.copy(alpha = 0.6f)
                    )
                ),
                RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: AI Sparkling Badge + Date + Regenerate Button
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SleekCyan.copy(alpha = 0.3f), SleekPrimary.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Intelligence",
                            tint = SleekCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "MORNING LONGEVITY BRIEFING",
                                color = SleekCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (briefing?.isAiGenerated == true) SleekPrimary.copy(alpha = 0.2f)
                                        else SleekIndigo.copy(alpha = 0.2f)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (briefing?.isAiGenerated == true) "GEMINI 3.5 AI" else "CLINICAL TWIN ENGINE",
                                    color = if (briefing?.isAiGenerated == true) SleekPrimary else SleekIndigo,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = todayDateFormatted,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Actions: Copy & Regenerate
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    briefing?.let { currentBriefing ->
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Morning Longevity Briefing",
                                    """
                                    LIFEMAP HUMAN™ Morning Briefing (${todayDateFormatted})
                                    ---------------------------------------------------
                                    ${currentBriefing.headline}
                                    Biological Age: ${result.phenoAge} yrs (${if (bioAgeDiff > 0.0) "+$bioAgeDiff" else "$bioAgeDiff"} yrs vs Chrono ${profile.chronologicalAge} yrs)
                                    Aging Speed: ${currentBriefing.rateOfAgingSummary}
                                    
                                    TOP HEALTH PRIORITY:
                                    ${currentBriefing.topHealthPriority}
                                    
                                    PROTOCOL:
                                    ${currentBriefing.actionProtocol}
                                    
                                    WEARABLE TELEMETRY:
                                    ${currentBriefing.wearableTelemetryImpact}
                                    """.trimIndent()
                                )
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Morning briefing copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Briefing",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .clickable(enabled = !isGenerating) {
                                viewModel.refreshMorningBriefing(forceAi = true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = SleekCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate AI Analysis",
                                tint = SleekCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Morning Headline & Biological Age Analysis
            briefing?.let { b ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = b.headline,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 22.sp
                    )

                    // BioAge & Rate of Aging Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Biological Age Chip
                        Box(
                            modifier = Modifier
                                .weight(1.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "BIOLOGICAL AGE",
                                    color = TextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${result.phenoAge} yrs",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isYounger) SleekGreen.copy(alpha = 0.2f) else SleekAmber.copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = if (bioAgeDiff > 0.0) "+$bioAgeDiff yrs" else "$bioAgeDiff yrs",
                                            color = if (isYounger) SleekGreen else SleekAmber,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Rate of Aging Velocity Chip
                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "RATE OF AGING PACE",
                                    color = TextMuted,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = b.rateOfAgingSummary,
                                    color = if (isYounger) SleekGreen else SleekCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // Biological Age Analysis Narrative
                    Text(
                        text = b.biologicalAgeAnalysis,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                // TOP HEALTH PRIORITY HIGHLIGHT CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    SleekPrimary.copy(alpha = 0.12f),
                                    SleekCyan.copy(alpha = 0.08f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(SleekPrimary.copy(alpha = 0.4f), SleekCyan.copy(alpha = 0.3f))
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "TODAY'S #1 HEALTH PRIORITY",
                                    color = SleekPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Target: ${b.keyBiomarkerToTarget}",
                                    color = SleekCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = b.topHealthPriority,
                            color = TextPrimary,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = b.priorityRationale,
                            color = TextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        // Actionable Daily Protocol Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkBackground.copy(alpha = 0.6f))
                                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = SleekAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Column {
                                    Text(
                                        text = "Target Action Protocol:",
                                        color = SleekAmber,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = b.actionProtocol,
                                        color = TextPrimary,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Yesterday's Health Connect Telemetry Modulation Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = SleekSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "YESTERDAY'S WEARABLE MODULATION",
                                color = SleekSecondary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Mini metrics summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.DirectionsWalk, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(12.dp))
                                Text("${profile.dailySteps} steps", color = TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = SleekRed, modifier = Modifier.size(12.dp))
                                Text("${profile.restingPulseBpm.toInt()} bpm pulse", color = TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = SleekAmber, modifier = Modifier.size(12.dp))
                                Text("${profile.activeCaloriesKcal.toInt()} kcal", color = TextPrimary, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Text(
                            text = b.wearableTelemetryImpact,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
