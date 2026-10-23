package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.withFrameNanos
import kotlin.math.exp

/**
 * Seconds since the clock was first composed, advanced every frame.
 * Read it inside draw / graphicsLayer lambdas so the animation only redraws instead of recomposing.
 */
@Composable
fun rememberEcgClock(): MutableFloatState {
    val clock = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> clock.floatValue = (now - start) / 1_000_000_000f }
        }
    }
    return clock
}

/** Length of one cardiac cycle in seconds for the given heart rate. */
fun beatPeriodSeconds(bpm: Double): Float = 60f / bpm.toFloat().coerceIn(30f, 200f)

/** 0..1 contraction intensity that peaks with the R wave; drives the beating heart icon. */
fun heartbeatPulse(timeSec: Float, bpm: Double): Float {
    val period = beatPeriodSeconds(bpm)
    val t = ((timeSec % period) + period) % period
    val d = (t - R_PEAK_SEC * templateScale(period)) / 0.07f
    return exp(-d * d)
}

private const val R_PEAK_SEC = 0.24f

// The PQRST template is defined for a ~0.8 s beat; squeeze it at high heart rates so waves never overlap.
private fun templateScale(period: Float): Float = (period / 0.8f).coerceAtMost(1f)

/**
 * Synthetic lead-II ECG amplitude (R peak = 1.0) at an absolute time, built from
 * Gaussian P, Q, R, S and T components repeating at the beat period.
 */
fun ecgAmplitude(timeSec: Float, bpm: Double): Float {
    val period = beatPeriodSeconds(bpm)
    val k = templateScale(period)
    val t = (((timeSec % period) + period) % period) / k
    fun wave(center: Float, width: Float, amp: Float): Float {
        val d = (t - center) / width
        return amp * exp(-d * d)
    }
    return wave(0.12f, 0.035f, 0.13f) +   // P: atrial depolarisation
        wave(0.215f, 0.010f, -0.12f) +     // Q
        wave(R_PEAK_SEC, 0.011f, 1.0f) +   // R: ventricular depolarisation
        wave(0.265f, 0.012f, -0.26f) +     // S
        wave(0.48f, 0.055f, 0.30f)         // T: ventricular repolarisation
}

/**
 * Bedside-monitor style ECG strip: the trace is written left to right by a glowing sweep head,
 * erasing the oldest beat just ahead of it, at the patient's actual heart rate.
 */
@Composable
fun LiveEcgMonitor(
    bpm: Double,
    clock: MutableFloatState,
    traceColor: Color,
    gridColor: Color,
    modifier: Modifier = Modifier,
    windowSeconds: Float = 3.2f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h * 0.58f
        val amp = h * 0.46f

        // ECG paper grid: minor every 6dp, major every 30dp
        val minor = 6.dp.toPx()
        var gx = 0f
        var i = 0
        while (gx <= w) {
            drawLine(gridColor.copy(alpha = if (i % 5 == 0) 0.55f else 0.22f), Offset(gx, 0f), Offset(gx, h), 1f)
            gx += minor; i++
        }
        var gy = 0f
        i = 0
        while (gy <= h) {
            drawLine(gridColor.copy(alpha = if (i % 5 == 0) 0.55f else 0.22f), Offset(0f, gy), Offset(w, gy), 1f)
            gy += minor; i++
        }

        val now = clock.floatValue
        val head = (now % windowSeconds) / windowSeconds
        val gap = 0.05f
        val step = 2.5f
        val glowWidth = 5.dp.toPx()
        val lineWidth = 1.8.dp.toPx()

        var prev: Offset? = null
        var x = 0f
        while (x <= w) {
            val f = x / w
            val inGap = f > head && f < head + gap
            if (inGap) {
                prev = null
            } else {
                val age = if (f <= head) (head - f) * windowSeconds else (head - f + 1f) * windowSeconds
                val y = midY - ecgAmplitude(now - age, bpm) * amp
                val point = Offset(x, y)
                prev?.let { p ->
                    // Older samples fade so the newest beat reads first
                    val alpha = 1f - 0.7f * (age / windowSeconds)
                    drawLine(traceColor.copy(alpha = 0.18f * alpha), p, point, glowWidth, StrokeCap.Round)
                    drawLine(traceColor.copy(alpha = alpha), p, point, lineWidth, StrokeCap.Round)
                }
                prev = point
            }
            x += step
        }

        // Sweep head with glow
        val headX = head * w
        val headY = midY - ecgAmplitude(now, bpm) * amp
        drawCircle(
            brush = Brush.radialGradient(
                listOf(traceColor.copy(alpha = 0.55f), Color.Transparent),
                center = Offset(headX, headY),
                radius = 10.dp.toPx()
            ),
            radius = 10.dp.toPx(),
            center = Offset(headX, headY)
        )
        drawCircle(Color.White, radius = 2.2.dp.toPx(), center = Offset(headX, headY))
    }
}
