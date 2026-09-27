package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrajectoryForecast
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorder
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

@Composable
fun TrajectoryChart(
    forecast: TrajectoryForecast,
    selectedYear: Int,
    onSelectYear: (Int) -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(forecast) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, animationSpec = tween(800))
    }

    val years = listOf(0, 5, 10, 20)
    val points = forecast.points

    // Compute min and max biological ages for scaling
    val allBioValues = points.flatMap {
        listOf(
            it.scenarioABiologicalAge,
            it.scenarioBBiologicalAge,
            it.scenarioCBiologicalAge,
            it.scenarioDBiologicalAge,
            it.scenarioEBiologicalAge
        )
    }
    val minBio = (allBioValues.minOrNull() ?: 35.0) - 4.0
    val maxBio = (allBioValues.maxOrNull() ?: 80.0) + 4.0

    Column(modifier = modifier) {
        // Chart Canvas
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp)) {
                val w = size.width
                val h = size.height

                val leftPad = 48f
                val rightPad = 24f
                val bottomPad = 32f
                val topPad = 16f

                val plotW = w - leftPad - rightPad
                val plotH = h - bottomPad - topPad

                // Horizontal Grid lines (Biological Age intervals)
                val gridSteps = 4
                val bioStep = (maxBio - minBio) / gridSteps
                val gridPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#938F99")
                    textSize = 20f
                    isAntiAlias = true
                }

                for (i in 0..gridSteps) {
                    val value = minBio + i * bioStep
                    val y = topPad + plotH - (i.toFloat() / gridSteps.toFloat()) * plotH

                    drawLine(
                        color = CardBorder.copy(alpha = 0.5f),
                        start = Offset(leftPad, y),
                        end = Offset(w - rightPad, y),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        "${value.toInt()}y",
                        12f,
                        y + 6f,
                        gridPaint
                    )
                }

                // X-Axis Year markers
                fun xForYear(year: Int): Float {
                    val factor = when (year) {
                        0 -> 0.0f
                        5 -> 0.25f
                        10 -> 0.50f
                        else -> 1.0f
                    }
                    return leftPad + factor * plotW
                }

                fun yForBio(bio: Double): Float {
                    val norm = ((bio - minBio) / (maxBio - minBio)).coerceIn(0.0, 1.0)
                    return topPad + plotH - (norm.toFloat() * plotH)
                }

                years.forEach { yr ->
                    val x = xForYear(yr)
                    drawLine(
                        color = if (yr == selectedYear) SleekPrimary.copy(alpha = 0.45f) else CardBorder.copy(alpha = 0.35f),
                        start = Offset(x, topPad),
                        end = Offset(x, topPad + plotH),
                        strokeWidth = if (yr == selectedYear) 2f else 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        "Yr $yr",
                        x - 14f,
                        h - 6f,
                        gridPaint
                    )
                }

                // Draw Scenarios Lines in Sleek Palette
                val scenarios = listOf(
                    Triple("Scenario A (Status Quo)", SleekRose, points.map { Pair(it.year, it.scenarioABiologicalAge) }),
                    Triple("Scenario B (Nutrition)", SleekAmber, points.map { Pair(it.year, it.scenarioBBiologicalAge) }),
                    Triple("Scenario C (Strength)", SleekSecondary, points.map { Pair(it.year, it.scenarioCBiologicalAge) }),
                    Triple("Scenario D (Sleep)", SleekCoral, points.map { Pair(it.year, it.scenarioDBiologicalAge) }),
                    Triple("Scenario E (Synergistic)", SleekGreen, points.map { Pair(it.year, it.scenarioEBiologicalAge) })
                )

                scenarios.forEach { (_, color, coords) ->
                    val path = Path()
                    coords.forEachIndexed { index, (yr, bio) ->
                        val targetX = xForYear(yr)
                        val targetY = yForBio(bio)
                        // Animate from base BioAge
                        val animatedY = yForBio(forecast.baseBioAge) + (targetY - yForBio(forecast.baseBioAge)) * animProgress.value

                        if (index == 0) path.moveTo(targetX, animatedY) else path.lineTo(targetX, animatedY)
                    }

                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = if (color == SleekGreen || color == SleekRose) 3f else 1.8f, cap = StrokeCap.Round)
                    )

                    // Draw node circles
                    coords.forEach { (yr, bio) ->
                        val targetX = xForYear(yr)
                        val animatedY = yForBio(forecast.baseBioAge) + (yForBio(bio) - yForBio(forecast.baseBioAge)) * animProgress.value
                        drawCircle(
                            color = color,
                            radius = if (yr == selectedYear) 5f else 3f,
                            center = Offset(targetX, animatedY)
                        )
                    }
                }
            }
        }

        // Year Selector Chips in Sleek styling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            years.forEach { yr ->
                val isSelected = yr == selectedYear
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SleekOnPrimary else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isSelected) SleekPrimary else BorderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelectYear(yr) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (yr == 0) "Present" else "+$yr Yrs",
                        color = if (isSelected) SleekPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

