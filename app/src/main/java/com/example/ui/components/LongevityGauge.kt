package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.ReserveScoreItem
import com.example.ui.theme.AppTheme
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekSecondary
import kotlin.math.abs

@Composable
fun HeroAgeCard(
    result: BiologicalTwinResult,
    modifier: Modifier = Modifier
) {
    val isYounger = result.ageDifference <= 0
    val diffYears = abs(result.ageDifference)
    val diffTag = if (isYounger) "▼ ${String.format("%.1f", diffYears)} Yıl Daha Genç" else "▲ ${String.format("%.1f", diffYears)} Yıl İleri"

    // High-Fidelity Glassmorphism Cyan Hero Card
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(
                    Color(0xFF99F6FF),
                    Color(0xFF00E5FF).copy(alpha = 0.6f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF00B4D8),
                            Color(0xFF0284C7)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF88))
                        )
                        Text(
                            text = "BİYOLOJİK YAŞ (PHENOAGE)",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "KRONOLOJİK: ${String.format("%.1f", result.chronologicalAge)} YAŞ",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Main Age Display Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Large Biological Age & Delta Badge with Heartbeat Wave
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${result.phenoAge}",
                                color = Color(0xFF0A192F),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1.5).sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "yaş",
                                color = Color(0xFF0A192F),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        // Mint pill badge with ECG heartbeat pulse wave
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xDDF0FDF4))
                                    .border(1.2.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = diffTag,
                                    color = Color(0xFF065F46),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.2.sp
                                )
                            }

                            // ECG pulse wave Canvas
                            Canvas(modifier = Modifier.size(width = 46.dp, height = 22.dp)) {
                                val w = size.width
                                val h = size.height
                                val path = Path().apply {
                                    moveTo(0f, h * 0.5f)
                                    lineTo(w * 0.22f, h * 0.5f)
                                    lineTo(w * 0.32f, h * 0.2f)
                                    lineTo(w * 0.44f, h * 0.9f)
                                    lineTo(w * 0.56f, h * 0.1f)
                                    lineTo(w * 0.68f, h * 0.65f)
                                    lineTo(w * 0.78f, h * 0.5f)
                                    lineTo(w, h * 0.5f)
                                }
                                drawPath(
                                    path = path,
                                    color = Color(0xFF065F46),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }

                    // Right Column: Inset Frosted Card for Aging Pace
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.38f))
                            .border(1.2.dp, Color.White.copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = "YAŞLANMA HIZI",
                                color = Color(0xFF334155),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${result.paceOfAging}x",
                                color = if (result.paceOfAging <= 1.0) Color(0xFF059669) else Color(0xFFFF4848),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (result.paceOfAging <= 1.0) "Biyolojik Kazanç" else "Hızlanmış Yıpranma",
                                color = if (result.paceOfAging <= 1.0) Color(0xFF059669) else Color(0xFFDC2626),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DominantAgingPhenotypeCard(
    result: BiologicalTwinResult,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        shape = RoundedCornerShape(20.dp)
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
                    text = "PRIMARY AGING PHENOTYPE",
                    color = colors.textPrimary.copy(alpha = 0.65f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.primary.copy(alpha = 0.16f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "OPTIMAL",
                        color = colors.primary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = result.dominantAgingPhenotype,
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = result.dominantPhenotypeDescription,
                color = colors.textSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            // Leverage Action box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.borderSubtle, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "LEVERAGE ACTION",
                        color = colors.primary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = result.primaryRecommendation,
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectedIndependenceCard(
    projectedYears: Double,
    chronoAge: Double,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val healthySpanAhead = (projectedYears - chronoAge).coerceAtLeast(0.0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PROJECTED HEALTHSPAN",
                    color = colors.secondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${projectedYears.toInt()} Years Independence",
                    color = colors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+${healthySpanAhead.toInt()} disability-free active years",
                    color = colors.textMuted,
                    fontSize = 11.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.14f))
                    .border(1.dp, colors.primary.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${projectedYears.toInt()}",
                    color = colors.primary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReserveItemCard(
    reserve: ReserveScoreItem,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }

    val scoreColor = when {
        reserve.score >= 80 -> SleekGreen
        reserve.score >= 60 -> colors.primary
        else -> SleekRose
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserve.name,
                        color = colors.textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = reserve.statusLabel,
                        color = scoreColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${reserve.score.toInt()}",
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "/100",
                        color = colors.textMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sleek Thin Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(colors.borderSubtle)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (reserve.score.toFloat() / 100f).coerceIn(0.05f, 1f))
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(scoreColor)
                )
            }

            AnimatedVisibility(visible = expanded, enter = fadeIn()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Biomarkers: ${reserve.metricSummary}",
                        color = colors.textSecondary,
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = reserve.clinicalInsight,
                        color = colors.textMuted,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.surfaceVariant)
                            .border(1.dp, colors.borderSubtle, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "💡 Action: ${reserve.dominantLeverageAction}",
                            color = colors.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

