package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReserveScoreItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekOnPrimary
import com.example.ui.theme.SleekPrimary
import com.example.ui.theme.SleekRose
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    reserves: List<ReserveScoreItem>,
    longevityReserveScore: Double,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(290.dp)
) {
    // 8-Axis dynamic list
    val axisNames = listOf(
        "Vascular",
        "Metabolic",
        "Immune",
        "Hormonal",
        "Muscle",
        "Cognitive",
        "Psych",
        "Longevity"
    )

    val axisScores = listOf(
        reserves.find { it.key == "vascular" }?.score ?: 75.0,
        reserves.find { it.key == "metabolic" }?.score ?: 75.0,
        reserves.find { it.key == "inflammatory" }?.score ?: 75.0,
        reserves.find { it.key == "hormonal" }?.score ?: 75.0,
        reserves.find { it.key == "muscle" }?.score ?: 75.0,
        reserves.find { it.key == "cognitive" }?.score ?: 75.0,
        reserves.find { it.key == "psychological" }?.score ?: 75.0,
        longevityReserveScore
    )

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(axisScores) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(700))
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 2f) - 34f
            val numAxes = 8
            val angleStep = (2 * PI / numAxes).toFloat()

            // 1. Concentric Guide Circles with dashed stroke (Sleek Interface SVG aesthetic)
            val rings = listOf(0.33f, 0.66f, 1.0f)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)

            rings.forEach { factor ->
                drawCircle(
                    color = CardBorder,
                    radius = radius * factor,
                    center = center,
                    style = Stroke(width = 1f, pathEffect = dashEffect)
                )
            }

            // 2. Axis Lines and Text Labels
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#CCC2DC")
                textSize = 24f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }

            for (i in 0 until numAxes) {
                val angle = -PI.toFloat() / 2f + i * angleStep
                val endX = center.x + radius * cos(angle)
                val endY = center.y + radius * sin(angle)

                // Axis Spoke
                drawLine(
                    color = CardBorder,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1f
                )

                // Label placement slightly beyond outer ring
                val labelDist = radius + 22f
                val labelX = center.x + labelDist * cos(angle)
                val labelY = center.y + labelDist * sin(angle) + 8f

                drawContext.canvas.nativeCanvas.drawText(
                    axisNames[i],
                    labelX,
                    labelY,
                    textPaint
                )
            }

            // 3. User Data Polygon Fill and Stroke
            val dataPath = Path()
            val animatedScorePoints = mutableListOf<Offset>()

            for (i in 0 until numAxes) {
                val angle = -PI.toFloat() / 2f + i * angleStep
                val normalizedScore = (axisScores[i].toFloat() / 100f).coerceIn(0.15f, 1f)
                val animatedRadius = radius * normalizedScore * animationProgress.value
                val x = center.x + animatedRadius * cos(angle)
                val y = center.y + animatedRadius * sin(angle)
                val point = Offset(x, y)
                animatedScorePoints.add(point)

                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Translucent Sleek Lilac Fill
            drawPath(
                path = dataPath,
                color = SleekPrimary.copy(alpha = 0.22f),
                style = Fill
            )

            // Sleek Lilac Outline
            drawPath(
                path = dataPath,
                color = SleekPrimary,
                style = Stroke(width = 2.2f, cap = StrokeCap.Round)
            )

            // Small Glowing Nodes
            animatedScorePoints.forEach { pt ->
                drawCircle(
                    color = SleekOnPrimary,
                    radius = 4f,
                    center = pt
                )
                drawCircle(
                    color = SleekPrimary,
                    radius = 2.5f,
                    center = pt
                )
            }
        }

        // Center Hub Badge: "LRS" and Score
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(CircleShape)
                .background(DarkSurface.copy(alpha = 0.85f))
                .border(1.dp, CardBorder.copy(alpha = 0.6f), CircleShape)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "LRS",
                color = TextPrimary.copy(alpha = 0.7f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${longevityReserveScore.toInt()}",
                color = SleekPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

