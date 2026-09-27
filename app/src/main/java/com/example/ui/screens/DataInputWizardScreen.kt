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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BiomarkerProfile
import com.example.ui.components.HealthConnectSyncCard
import com.example.ui.theme.AppTheme
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

@Composable
fun DataInputWizardScreen(
    viewModel: LongevityViewModel,
    onNavigateToOcr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val profile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        Pair("Blood Biomarkers", Icons.Default.Bloodtype),
        Pair("Vitals & Body", Icons.Default.Favorite),
        Pair("Hormones & Micronutrients", Icons.Default.Medication)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BIOMARKER WIZARD",
                    color = SleekPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onNavigateToOcr,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekOnPrimary,
                            contentColor = SleekPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("AI OCR Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.updateProfile(BiomarkerProfile()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceVariant,
                            contentColor = SleekPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Defaults", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Text(
                text = "Live validation with clinical reference intervals",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = SleekPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = SleekPrimary
                )
            },
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedTabIndex == index) SleekPrimary else TextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) SleekPrimary else TextMuted
                            )
                        }
                    }
                )
            }
        }

        // Content Area
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Blood Biomarkers
                    item {
                        SectionHeader("MORGAN LEVINE PHENOAGE PANEL", "Key circulating biomarkers of mortality and organ aging")
                    }

                    item {
                        BiomarkerInputField(
                            label = "Fasting Glucose",
                            value = profile.glucoseMgDl.toString(),
                            unit = "mg/dL",
                            optimalRange = "70 – 99 mg/dL",
                            isOptimal = profile.glucoseMgDl in 70.0..99.0,
                            onValueChange = { profile.copy(glucoseMgDl = it.toDoubleOrNull() ?: profile.glucoseMgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Fasting Insulin",
                            value = profile.insulinUuMl.toString(),
                            unit = "μU/mL",
                            optimalRange = "2.0 – 8.0 μU/mL",
                            isOptimal = profile.insulinUuMl in 2.0..8.0,
                            onValueChange = { profile.copy(insulinUuMl = it.toDoubleOrNull() ?: profile.insulinUuMl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "hs-CRP (High-Sensitivity CRP)",
                            value = profile.hsCrpMgL.toString(),
                            unit = "mg/L",
                            optimalRange = "< 0.5 mg/L (Optimal)",
                            isOptimal = profile.hsCrpMgL < 0.8,
                            onValueChange = { profile.copy(hsCrpMgL = it.toDoubleOrNull() ?: profile.hsCrpMgL).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Serum Albumin",
                            value = profile.albuminGDl.toString(),
                            unit = "g/dL",
                            optimalRange = "4.2 – 5.0 g/dL",
                            isOptimal = profile.albuminGDl in 4.2..5.0,
                            onValueChange = { profile.copy(albuminGDl = it.toDoubleOrNull() ?: profile.albuminGDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Serum Creatinine",
                            value = profile.creatinineUmolL.toString(),
                            unit = "μmol/L",
                            optimalRange = "60 – 95 μmol/L",
                            isOptimal = profile.creatinineUmolL in 60.0..95.0,
                            onValueChange = { profile.copy(creatinineUmolL = it.toDoubleOrNull() ?: profile.creatinineUmolL).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Lymphocyte Percentage",
                            value = profile.lymphocytePercent.toString(),
                            unit = "%",
                            optimalRange = "25 – 38 %",
                            isOptimal = profile.lymphocytePercent in 25.0..38.0,
                            onValueChange = { profile.copy(lymphocytePercent = it.toDoubleOrNull() ?: profile.lymphocytePercent).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Mean Corpuscular Volume (MCV)",
                            value = profile.mcvFl.toString(),
                            unit = "fL",
                            optimalRange = "82 – 94 fL",
                            isOptimal = profile.mcvFl in 82.0..94.0,
                            onValueChange = { profile.copy(mcvFl = it.toDoubleOrNull() ?: profile.mcvFl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Red Cell Distribution Width (RDW)",
                            value = profile.rdwPercent.toString(),
                            unit = "%",
                            optimalRange = "11.5 – 13.5 %",
                            isOptimal = profile.rdwPercent in 11.5..13.5,
                            onValueChange = { profile.copy(rdwPercent = it.toDoubleOrNull() ?: profile.rdwPercent).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Alkaline Phosphatase (ALP)",
                            value = profile.alpUL.toString(),
                            unit = "U/L",
                            optimalRange = "45 – 90 U/L",
                            isOptimal = profile.alpUL in 45.0..90.0,
                            onValueChange = { profile.copy(alpUL = it.toDoubleOrNull() ?: profile.alpUL).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "White Blood Cell Count (WBC)",
                            value = profile.wbc103Ul.toString(),
                            unit = "10^3/μL",
                            optimalRange = "4.5 – 7.5 k/μL",
                            isOptimal = profile.wbc103Ul in 4.5..7.5,
                            onValueChange = { profile.copy(wbc103Ul = it.toDoubleOrNull() ?: profile.wbc103Ul).let(viewModel::updateProfile) }
                        )
                    }
                }

                1 -> {
                    // TAB 2: Vitals & Body Composition
                    item {
                        SectionHeader("HEALTH CONNECT SENSOR INTEGRATION", "Real-time wearable telemetry for Cardiovascular and Metabolic reserve")
                    }

                    item {
                        HealthConnectSyncCard(viewModel = viewModel)
                    }

                    item {
                        SectionHeader("DEMOGRAPHICS & CARDIOVASCULAR VITALS", "Chronological basis, blood pressure and lipid fractions")
                    }

                    item {
                        BiomarkerInputField(
                            label = "Chronological Age",
                            value = profile.chronologicalAge.toInt().toString(),
                            unit = "years",
                            optimalRange = "Target: Lower BioAge gap",
                            isOptimal = true,
                            onValueChange = { profile.copy(chronologicalAge = it.toDoubleOrNull() ?: profile.chronologicalAge).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Resting Pulse (Health Connect)",
                            value = profile.restingPulseBpm.toInt().toString(),
                            unit = "bpm",
                            optimalRange = "50 – 65 bpm (Low resting HR expands vascular reserve)",
                            isOptimal = profile.restingPulseBpm in 48.0..65.0,
                            onValueChange = { profile.copy(restingPulseBpm = it.toDoubleOrNull() ?: profile.restingPulseBpm).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Daily Steps (Health Connect)",
                            value = profile.dailySteps.toString(),
                            unit = "steps",
                            optimalRange = "> 8,000 steps/day (Triggers GLUT4 & metabolic score boost)",
                            isOptimal = profile.dailySteps >= 8000,
                            onValueChange = { profile.copy(dailySteps = it.toIntOrNull() ?: profile.dailySteps).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Active Energy Burned",
                            value = profile.activeCaloriesKcal.toInt().toString(),
                            unit = "kcal",
                            optimalRange = "> 350 kcal/day (Mitochondrial lipid oxidation)",
                            isOptimal = profile.activeCaloriesKcal >= 350.0,
                            onValueChange = { profile.copy(activeCaloriesKcal = it.toDoubleOrNull() ?: profile.activeCaloriesKcal).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Systolic Blood Pressure (SBP)",
                            value = profile.sbpMmHg.toInt().toString(),
                            unit = "mmHg",
                            optimalRange = "< 120 mmHg",
                            isOptimal = profile.sbpMmHg < 120.0,
                            onValueChange = { profile.copy(sbpMmHg = it.toDoubleOrNull() ?: profile.sbpMmHg).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Diastolic Blood Pressure (DBP)",
                            value = profile.dbpMmHg.toInt().toString(),
                            unit = "mmHg",
                            optimalRange = "< 80 mmHg",
                            isOptimal = profile.dbpMmHg < 80.0,
                            onValueChange = { profile.copy(dbpMmHg = it.toDoubleOrNull() ?: profile.dbpMmHg).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Resting Pulse",
                            value = profile.restingPulseBpm.toInt().toString(),
                            unit = "bpm",
                            optimalRange = "50 – 65 bpm",
                            isOptimal = profile.restingPulseBpm in 50.0..65.0,
                            onValueChange = { profile.copy(restingPulseBpm = it.toDoubleOrNull() ?: profile.restingPulseBpm).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Total Cholesterol",
                            value = profile.totalCholesterolMgDl.toString(),
                            unit = "mg/dL",
                            optimalRange = "< 200 mg/dL",
                            isOptimal = profile.totalCholesterolMgDl < 200.0,
                            onValueChange = { profile.copy(totalCholesterolMgDl = it.toDoubleOrNull() ?: profile.totalCholesterolMgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "HDL Cholesterol",
                            value = profile.hdlMgDl.toString(),
                            unit = "mg/dL",
                            optimalRange = "> 50 mg/dL",
                            isOptimal = profile.hdlMgDl >= 50.0,
                            onValueChange = { profile.copy(hdlMgDl = it.toDoubleOrNull() ?: profile.hdlMgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Triglycerides",
                            value = profile.triglyceridesMgDl.toString(),
                            unit = "mg/dL",
                            optimalRange = "< 100 mg/dL",
                            isOptimal = profile.triglyceridesMgDl < 100.0,
                            onValueChange = { profile.copy(triglyceridesMgDl = it.toDoubleOrNull() ?: profile.triglyceridesMgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Waist Circumference",
                            value = profile.waistCircumferenceCm.toString(),
                            unit = "cm",
                            optimalRange = "Waist-to-Height Ratio < 0.50",
                            isOptimal = profile.waistToHeightRatio < 0.50,
                            onValueChange = { profile.copy(waistCircumferenceCm = it.toDoubleOrNull() ?: profile.waistCircumferenceCm).let(viewModel::updateProfile) }
                        )
                    }
                }

                2 -> {
                    // TAB 3: Hormonal & Micronutrient Panel
                    item {
                        SectionHeader("ENDOCRINE & MICRONUTRIENT AXIS", "Steroidogenesis, thyroid conversion and physical walking pace")
                    }

                    item {
                        BiomarkerInputField(
                            label = "Vitamin D (25-OH)",
                            value = profile.vitaminDNgMl.toString(),
                            unit = "ng/mL",
                            optimalRange = "45 – 70 ng/mL",
                            isOptimal = profile.vitaminDNgMl in 45.0..70.0,
                            onValueChange = { profile.copy(vitaminDNgMl = it.toDoubleOrNull() ?: profile.vitaminDNgMl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "TSH (Thyroid Stimulating Hormone)",
                            value = profile.tshUiuMl.toString(),
                            unit = "μIU/mL",
                            optimalRange = "1.0 – 2.2 μIU/mL",
                            isOptimal = profile.tshUiuMl in 1.0..2.2,
                            onValueChange = { profile.copy(tshUiuMl = it.toDoubleOrNull() ?: profile.tshUiuMl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "DHEA-S",
                            value = profile.dheaSUgDl.toString(),
                            unit = "μg/dL",
                            optimalRange = "220 – 450 μg/dL",
                            isOptimal = profile.dheaSUgDl in 220.0..450.0,
                            onValueChange = { profile.copy(dheaSUgDl = it.toDoubleOrNull() ?: profile.dheaSUgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "Total Testosterone",
                            value = profile.testosteroneNgDl.toString(),
                            unit = "ng/dL",
                            optimalRange = "550 – 850 ng/dL (Male)",
                            isOptimal = profile.testosteroneNgDl >= 500.0,
                            onValueChange = { profile.copy(testosteroneNgDl = it.toDoubleOrNull() ?: profile.testosteroneNgDl).let(viewModel::updateProfile) }
                        )
                    }

                    item {
                        BiomarkerInputField(
                            label = "4-Meter Gait Speed",
                            value = profile.walkingSpeed4mMps.toString(),
                            unit = "m/s",
                            optimalRange = "> 1.25 m/s",
                            isOptimal = profile.walkingSpeed4mMps >= 1.25,
                            onValueChange = { profile.copy(walkingSpeed4mMps = it.toDoubleOrNull() ?: profile.walkingSpeed4mMps).let(viewModel::updateProfile) }
                        )
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
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            color = SleekSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Text(
            text = subtitle,
            color = TextSecondary,
            fontSize = 11.5.sp
        )
    }
}

@Composable
fun BiomarkerInputField(
    label: String,
    value: String,
    unit: String,
    optimalRange: String,
    isOptimal: Boolean,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isOptimal) SleekGreen.copy(alpha = 0.15f) else SleekAmber.copy(alpha = 0.15f))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isOptimal) "Optimal" else "Suboptimal",
                        color = if (isOptimal) SleekGreen else SleekAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                        focusedBorderColor = SleekPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Text(
                    text = unit,
                    color = SleekSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Target: $optimalRange",
                color = TextMuted,
                fontSize = 10.5.sp
            )
        }
    }
}

