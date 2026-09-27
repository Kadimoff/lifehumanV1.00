package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TrajectoryChart
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkBackground
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
import com.example.ui.viewmodel.LongevityViewModel
import kotlin.math.roundToInt

@Composable
fun ScenarioTrajectoryScreen(
    viewModel: LongevityViewModel,
    modifier: Modifier = Modifier
) {
    val forecast by viewModel.trajectoryForecast.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedScrubYear.collectAsStateWithLifecycle()

    val currentPoint = forecast.points.find { it.year == selectedYear } ?: forecast.points[1]

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "5-TRAJECTORY SCENARIOS",
                    color = SleekPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Gompertz-Levine longevity projections over 5, 10, and 20 years",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Multi-Line Trajectory Chart
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROJECTED BIOLOGICAL AGE CURVES",
                            color = SleekSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Baseline: ${forecast.baseBioAge} yrs",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }

                    TrajectoryChart(
                        forecast = forecast,
                        selectedYear = selectedYear,
                        onSelectYear = { viewModel.setSelectedScrubYear(it) }
                    )
                }
            }
        }

        // Year Scrubber Slider Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TIME HORIZON SCRUBBER",
                            color = SleekPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (selectedYear == 0) "Year 0 (Current Baseline)" else "+$selectedYear Years Horizon",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = when (selectedYear) {
                            0 -> 0f
                            5 -> 1f
                            10 -> 2f
                            else -> 3f
                        },
                        onValueChange = { floatVal ->
                            val yr = when (floatVal.roundToInt()) {
                                0 -> 0
                                1 -> 5
                                2 -> 10
                                else -> 20
                            }
                            viewModel.setSelectedScrubYear(yr)
                        },
                        valueRange = 0f..3f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = SleekPrimary,
                            activeTrackColor = SleekPrimary,
                            inactiveTrackColor = BorderSubtle
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Present", color = TextMuted, fontSize = 10.sp)
                        Text("Year 5", color = TextMuted, fontSize = 10.sp)
                        Text("Year 10", color = TextMuted, fontSize = 10.sp)
                        Text("Year 20", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Projected Values for Selected Year
        item {
            Text(
                text = "PROJECTED OUTCOMES AT YEAR $selectedYear",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }

        item {
            ScenarioOutcomeCard(
                name = "Scenario A: Status Quo",
                subtitle = "Natural biological decay at present biomarker rate",
                color = SleekRose,
                icon = Icons.Default.Warning,
                bioAge = currentPoint.scenarioABiologicalAge,
                baseBio = forecast.baseBioAge,
                gainDescription = "No lifestyle mitigation • Gompertz risk acceleration"
            )
        }

        item {
            ScenarioOutcomeCard(
                name = "Scenario B: Nutrition Optimization",
                subtitle = "Lowers HOMA-IR, TyG index & systemic hs-CRP",
                color = SleekAmber,
                icon = Icons.Default.Restaurant,
                bioAge = currentPoint.scenarioBBiologicalAge,
                baseBio = forecast.baseBioAge,
                gainDescription = "+${forecast.scenarioBGainYears} years healthspan extension"
            )
        }

        item {
            ScenarioOutcomeCard(
                name = "Scenario C: Physical Activity & Strength",
                subtitle = "Zone 2 VO2max + Hypertrophy sarcopenia prevention",
                color = SleekSecondary,
                icon = Icons.Default.FitnessCenter,
                bioAge = currentPoint.scenarioCBiologicalAge,
                baseBio = forecast.baseBioAge,
                gainDescription = "+${forecast.scenarioCGainYears} years healthspan extension"
            )
        }

        item {
            ScenarioOutcomeCard(
                name = "Scenario D: Sleep & Circadian",
                subtitle = "Basal cortisol reduction & glymphatic clearance",
                color = SleekCoral,
                icon = Icons.Default.Bedtime,
                bioAge = currentPoint.scenarioDBiologicalAge,
                baseBio = forecast.baseBioAge,
                gainDescription = "+${forecast.scenarioDGainYears} years healthspan extension"
            )
        }

        item {
            ScenarioOutcomeCard(
                name = "Scenario E: Synergistic Protocol",
                subtitle = "Concurrent nutrition, resistance, VO2max & restorative sleep",
                color = SleekGreen,
                icon = Icons.Default.AutoAwesome,
                bioAge = currentPoint.scenarioEBiologicalAge,
                baseBio = forecast.baseBioAge,
                gainDescription = "+${forecast.scenarioEGainYears} years max healthspan extension!"
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ScenarioOutcomeCard(
    name: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    bioAge: Double,
    baseBio: Double,
    gainDescription: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .border(1.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = gainDescription,
                    color = color,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${bioAge.toInt()} yrs",
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Projected",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

