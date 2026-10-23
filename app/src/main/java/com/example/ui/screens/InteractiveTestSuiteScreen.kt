package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AppTheme
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekRose
import com.example.ui.viewmodel.LongevityViewModel

@Composable
fun InteractiveTestSuiteScreen(
    viewModel: LongevityViewModel,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var activeTestTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        Pair("Stroop Cognitive Test", Icons.Default.Psychology),
        Pair("30s Chair Stand", Icons.Default.DirectionsRun),
        Pair("Psych Surveys", Icons.Default.Timer)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "INTERACTIVE TEST SUITE",
                color = colors.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Direct on-device physiological & cognitive assessment",
                color = colors.textSecondary,
                fontSize = 12.sp
            )
        }

        // Test Category Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTestTab,
            containerColor = colors.surface,
            contentColor = colors.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTestTab]),
                    color = colors.primary
                )
            },
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = activeTestTab == index,
                    onClick = { activeTestTab = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (activeTestTab == index) colors.primary else colors.textMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeTestTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTestTab == index) colors.primary else colors.textMuted
                            )
                        }
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (activeTestTab) {
                0 -> {
                    // STROOP TEST
                    item {
                        StroopTestComponent(viewModel = viewModel)
                    }
                }
                1 -> {
                    // 30-SEC CHAIR STAND
                    item {
                        ChairStandTimerComponent(viewModel = viewModel)
                    }
                }
                2 -> {
                    // PSYCHOLOGICAL QUESTIONNAIRES
                    item {
                        PsychologicalSurveyComponent(viewModel = viewModel)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
fun StroopTestComponent(viewModel: LongevityViewModel) {
    val themeColors = AppTheme.colors
    val isRunning by viewModel.stroopActive.collectAsStateWithLifecycle()
    val isFinished by viewModel.stroopFinished.collectAsStateWithLifecycle()
    val currentIdx by viewModel.stroopCurrentIndex.collectAsStateWithLifecycle()
    val stimulus by viewModel.stroopStimulus.collectAsStateWithLifecycle()
    val results by viewModel.stroopResults.collectAsStateWithLifecycle()
    val profile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()

    val colors = listOf(
        Pair("RED", Color(0xFFFF5252)),
        Pair("BLUE", Color(0xFF448AFF)),
        Pair("GREEN", Color(0xFF10B981)),
        Pair("YELLOW", Color(0xFFF59E0B)),
        Pair("PURPLE", Color(0xFFA855F7))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "STROOP COLOR & EXECUTIVE INHIBITION TEST",
                color = themeColors.secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Text(
                text = "Tap the button matching the INK COLOR of the word, NOT the written text itself. Measures reaction latency in milliseconds.",
                color = themeColors.textSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            if (!isRunning && !isFinished) {
                // Intro / Start Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColors.surfaceVariant)
                        .border(1.dp, themeColors.cardBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current Baseline Latency",
                            color = themeColors.textMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${profile.stroopMeanLatencyMs.toInt()} ms",
                            color = themeColors.primary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Accuracy: ${profile.stroopAccuracyPercent.toInt()}%",
                            color = themeColors.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = { viewModel.startStroopTest() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.primary, contentColor = themeColors.onPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("START 12-TRIAL TEST", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isRunning) {
                // Active Test View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Trial ${currentIdx + 1}/12", color = themeColors.textSecondary, fontSize = 12.sp)
                    Text("Accuracy: ${results.count { it.isCorrect }}/${results.size}", color = themeColors.primary, fontSize = 12.sp)
                }

                // Word Stimulus Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1014))
                        .border(1.5.dp, themeColors.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    stimulus?.let { st ->
                        Text(
                            text = st.displayedWord,
                            color = Color(st.inkColorHex),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Color Choice Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.take(3).forEachIndexed { idx, (name, col) ->
                            Button(
                                onClick = { viewModel.submitStroopAnswer(idx) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = col.copy(alpha = 0.18f), contentColor = col),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, col.copy(alpha = 0.6f))
                            ) {
                                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.drop(3).forEachIndexed { idx, (name, col) ->
                            Button(
                                onClick = { viewModel.submitStroopAnswer(idx + 3) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = col.copy(alpha = 0.18f), contentColor = col),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, col.copy(alpha = 0.6f))
                            ) {
                                Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Completed Summary Card
                val correctCount = results.count { it.isCorrect }
                val meanLatency = if (correctCount > 0) results.filter { it.isCorrect }.map { it.latencyMs }.average() else 800.0
                val accuracy = (correctCount.toDouble() / results.size.toDouble()) * 100.0

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColors.surfaceVariant)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = themeColors.green, modifier = Modifier.size(32.dp))
                    Text("Test Complete! Cognitive Twin Updated", color = themeColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Mean Reaction Latency: ${meanLatency.toInt()} ms", color = themeColors.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Accuracy: ${accuracy.toInt()}% ($correctCount/12 trials)", color = themeColors.textSecondary, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.startStroopTest() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.surfaceVariant, contentColor = themeColors.textPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Test")
                }
            }
        }
    }
}

@Composable
fun ChairStandTimerComponent(viewModel: LongevityViewModel) {
    val colors = AppTheme.colors
    val isRunning by viewModel.chairTimerActive.collectAsStateWithLifecycle()
    val secondsLeft by viewModel.chairSecondsLeft.collectAsStateWithLifecycle()
    val repCount by viewModel.chairRepCount.collectAsStateWithLifecycle()
    val isFinished by viewModel.chairFinished.collectAsStateWithLifecycle()
    val profile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "30-SECOND CHAIR STAND TEST",
                color = colors.secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Text(
                text = "Cross arms across chest. On START, sit and fully stand as many times as possible in 30 seconds. Tap the button on each stand.",
                color = colors.textSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            // Timer & Count Circle
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant)
                    .border(
                        3.dp,
                        Brush.sweepGradient(listOf(colors.primary, colors.secondary, colors.amber, colors.primary)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isRunning) "$secondsLeft s" else "$repCount Reps",
                        color = if (isRunning) colors.amber else colors.primary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isRunning) "Reps: $repCount" else if (isFinished) "Finished!" else "Baseline: ${profile.chairStand30sReps} reps",
                        color = colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (!isRunning && !isFinished) {
                Button(
                    onClick = { viewModel.startChairStandTest() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("START 30s COUNTDOWN", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isRunning) {
                // Big Tap to count stand button
                Button(
                    onClick = { viewModel.incrementChairRep() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("TAP ON FULL STAND (+1)", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            } else {
                // Completed Card
                val rating = when {
                    repCount >= 18 -> "Superior Neuromuscular Power"
                    repCount >= 14 -> "Normal Sarcopenia Buffer"
                    else -> "Elevated Sarcopenia Risk"
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surfaceVariant)
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Total: $repCount Reps in 30 seconds", color = colors.primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(rating, color = if (repCount >= 14) colors.green else colors.rose, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.resetChairTest() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant, contentColor = colors.textPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset")
                    }
                    Button(
                        onClick = { viewModel.startChairStandTest() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Retake")
                    }
                }
            }
        }
    }
}

@Composable
fun PsychologicalSurveyComponent(viewModel: LongevityViewModel) {
    val colors = AppTheme.colors
    val profile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.cardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "NEURO-PSYCHOLOGICAL & SLEEP METRICS",
                color = colors.secondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            // PHQ-9 (Mood)
            SurveyScoreSelector(
                title = "PHQ-9 Mood Burden",
                description = "Over the last 2 weeks, frequency of feeling down, depressed, or hopeless.",
                currentValue = profile.phq9Score,
                maxValue = 27,
                options = listOf(Pair("Minimal (0-4)", 2), Pair("Mild (5-9)", 6), Pair("Moderate (10-14)", 11), Pair("Severe (15+)", 18)),
                onSelect = { viewModel.updateProfile(profile.copy(phq9Score = it)) }
            )

            // GAD-7 (Anxiety)
            SurveyScoreSelector(
                title = "GAD-7 Allostatic Anxiety Load",
                description = "Feeling nervous, anxious, on edge, or unable to stop worrying.",
                currentValue = profile.gad7Score,
                maxValue = 21,
                options = listOf(Pair("Minimal (0-4)", 2), Pair("Mild (5-9)", 6), Pair("Moderate (10-14)", 12)),
                onSelect = { viewModel.updateProfile(profile.copy(gad7Score = it)) }
            )

            // PSQI (Sleep Quality)
            SurveyScoreSelector(
                title = "PSQI Pittsburgh Sleep Quality Index",
                description = "Sleep latency, awakenings, daytime dysfunction, and restorative depth.",
                currentValue = profile.psqiSleepScore,
                maxValue = 21,
                options = listOf(Pair("Optimal (<5)", 3), Pair("Mild Deficit (6-10)", 7), Pair("Severe Insomnia (11+)", 13)),
                onSelect = { viewModel.updateProfile(profile.copy(psqiSleepScore = it)) }
            )
        }
    }
}

@Composable
fun SurveyScoreSelector(
    title: String,
    description: String,
    currentValue: Int,
    maxValue: Int,
    options: List<Pair<String, Int>>,
    onSelect: (Int) -> Unit
) {
    val colors = AppTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = colors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("$currentValue/$maxValue", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(description, color = colors.textSecondary, fontSize = 11.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { (label, value) ->
                val isSelected = (currentValue - value) in -2..2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) colors.primary.copy(alpha = 0.16f) else colors.surfaceVariant)
                        .border(1.dp, if (isSelected) colors.primary else colors.borderSubtle, RoundedCornerShape(10.dp))
                        .clickable { onSelect(value) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) colors.primary else colors.textSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

