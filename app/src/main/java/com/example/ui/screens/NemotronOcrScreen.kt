package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.ui.theme.SleekSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LongevityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OcrSampleDoc(
    val id: String,
    val title: String,
    val labName: String,
    val date: String,
    val description: String,
    val biomarkers: List<ExtractedBiomarker>
)

data class ExtractedBiomarker(
    val loincCode: String,
    val name: String,
    val standardKey: String,
    val value: Double,
    val unit: String,
    val confidence: Double,
    val isOptimal: Boolean
)

enum class OcrPipelineStage(val title: String, val description: String) {
    IDLE("Document Ready", "Source laboratory report loaded and parsed for ingestion."),
    PREPROCESSING("Preprocessing & Deskew", "Applying adaptive binarization, skew rectification, and contrast filters."),
    INGESTING("Nemotron Vision Multimodal Ingestion", "Running on-device neural parser for tabular extraction and LOINC mapping."),
    NORMALIZING("LOINC Normalization & Unit Sync", "Synchronizing clinical biomarker values and unit standardization."),
    REVIEW("Human-in-the-Loop Verification", "Biomarkers extracted with high confidence. Ready for twin synchronization.")
}

@Composable
fun NemotronOcrScreen(
    viewModel: LongevityViewModel,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentProfile by viewModel.biomarkerProfile.collectAsStateWithLifecycle()

    var selectedSampleIdx by remember { mutableIntStateOf(0) }
    var pipelineStage by remember { mutableStateOf(OcrPipelineStage.IDLE) }
    var pipelineProgress by remember { mutableStateOf(0f) }
    var showJsonModal by remember { mutableStateOf(false) }
    var appliedSuccessMessage by remember { mutableStateOf<String?>(null) }

    val sampleDocs = remember {
        listOf(
            OcrSampleDoc(
                id = "doc_1",
                title = "Comprehensive Longevity Biomarker Panel",
                labName = "BioMatrix Longevity Diagnostics Inc.",
                date = "2026-08-20",
                description = "Fasting Glucose, Fasting Insulin, hs-CRP, Lipid Profile, and Complete Blood Count",
                biomarkers = listOf(
                    ExtractedBiomarker("LOINC_30522-7", "hs-CRP (High-Sensitivity)", "hsCrpMgL", 0.42, "mg/L", 0.99, true),
                    ExtractedBiomarker("LOINC_1558-6", "Fasting Glucose", "glucoseMgDl", 84.0, "mg/dL", 0.99, true),
                    ExtractedBiomarker("LOINC_20436-2", "Fasting Insulin", "insulinUuMl", 4.2, "uIU/mL", 0.98, true),
                    ExtractedBiomarker("LOINC_2093-3", "Total Cholesterol", "totalCholesterolMgDl", 168.0, "mg/dL", 0.99, true),
                    ExtractedBiomarker("LOINC_2085-9", "HDL Cholesterol", "hdlMgDl", 66.0, "mg/dL", 0.97, true),
                    ExtractedBiomarker("LOINC_2571-8", "Triglycerides", "triglyceridesMgDl", 72.0, "mg/dL", 0.98, true),
                    ExtractedBiomarker("LOINC_1751-7", "Albumin", "albuminGDl", 4.6, "g/dL", 0.99, true),
                    ExtractedBiomarker("LOINC_2160-0", "Creatinine", "creatinineUmolL", 74.0, "umol/L", 0.98, true)
                )
            ),
            OcrSampleDoc(
                id = "doc_2",
                title = "Endocrine & Hormonal Balance Panel",
                labName = "EndoHealth Clinical Laboratories",
                date = "2026-07-14",
                description = "DHEA-S, Total Testosterone, TSH, 25-OH Vitamin D3, and Immune Profile",
                biomarkers = listOf(
                    ExtractedBiomarker("LOINC_2191-5", "DHEA-Sulfate", "dheaSUgDl", 310.0, "ug/dL", 0.97, true),
                    ExtractedBiomarker("LOINC_2986-8", "Total Testosterone", "testosteroneNgDl", 680.0, "ng/dL", 0.98, true),
                    ExtractedBiomarker("LOINC_3016-3", "Thyrotropin (TSH)", "tshUiuMl", 1.8, "uIU/mL", 0.99, true),
                    ExtractedBiomarker("LOINC_62292-8", "Vitamin D (25-OH)", "vitaminDNgMl", 58.0, "ng/mL", 0.98, true),
                    ExtractedBiomarker("LOINC_26464-8", "White Blood Cells (WBC)", "wbc103Ul", 5.2, "10^3/uL", 0.99, true),
                    ExtractedBiomarker("LOINC_26474-7", "Lymphocyte Percentage", "lymphocytePercent", 34.0, "%", 0.97, true)
                )
            ),
            OcrSampleDoc(
                id = "doc_3",
                title = "Metabolic Stress Alert Panel (Suboptimal)",
                labName = "Apex Preventive Health Center",
                date = "2026-06-02",
                description = "Elevated HOMA-IR, High-Sensitivity CRP spike, and Atherogenic Dyslipidemia",
                biomarkers = listOf(
                    ExtractedBiomarker("LOINC_30522-7", "hs-CRP", "hsCrpMgL", 3.1, "mg/L", 0.98, false),
                    ExtractedBiomarker("LOINC_1558-6", "Fasting Glucose", "glucoseMgDl", 108.0, "mg/dL", 0.99, false),
                    ExtractedBiomarker("LOINC_20436-2", "Fasting Insulin", "insulinUuMl", 14.5, "uIU/mL", 0.96, false),
                    ExtractedBiomarker("LOINC_2571-8", "Triglycerides", "triglyceridesMgDl", 185.0, "mg/dL", 0.98, false),
                    ExtractedBiomarker("LOINC_2085-9", "HDL Cholesterol", "hdlMgDl", 41.0, "mg/dL", 0.97, false),
                    ExtractedBiomarker("LOINC_6768-6", "Alkaline Phosphatase (ALP)", "alpUL", 88.0, "U/L", 0.96, true)
                )
            )
        )
    }

    val currentDoc = sampleDocs[selectedSampleIdx]

    fun runNemotronPipeline() {
        coroutineScope.launch {
            appliedSuccessMessage = null
            pipelineStage = OcrPipelineStage.PREPROCESSING
            pipelineProgress = 0.25f
            delay(650)

            pipelineStage = OcrPipelineStage.INGESTING
            pipelineProgress = 0.60f
            delay(800)

            pipelineStage = OcrPipelineStage.NORMALIZING
            pipelineProgress = 0.88f
            delay(550)

            pipelineStage = OcrPipelineStage.REVIEW
            pipelineProgress = 1.0f
        }
    }

    fun applyExtractedDataToTwin() {
        var updated = currentProfile
        currentDoc.biomarkers.forEach { bm ->
            when (bm.standardKey) {
                "hsCrpMgL" -> updated = updated.copy(hsCrpMgL = bm.value)
                "glucoseMgDl" -> updated = updated.copy(glucoseMgDl = bm.value)
                "insulinUuMl" -> updated = updated.copy(insulinUuMl = bm.value)
                "totalCholesterolMgDl" -> updated = updated.copy(totalCholesterolMgDl = bm.value)
                "hdlMgDl" -> updated = updated.copy(hdlMgDl = bm.value)
                "triglyceridesMgDl" -> updated = updated.copy(triglyceridesMgDl = bm.value)
                "albuminGDl" -> updated = updated.copy(albuminGDl = bm.value)
                "creatinineUmolL" -> updated = updated.copy(creatinineUmolL = bm.value)
                "dheaSUgDl" -> updated = updated.copy(dheaSUgDl = bm.value)
                "testosteroneNgDl" -> updated = updated.copy(testosteroneNgDl = bm.value)
                "tshUiuMl" -> updated = updated.copy(tshUiuMl = bm.value)
                "vitaminDNgMl" -> updated = updated.copy(vitaminDNgMl = bm.value)
                "wbc103Ul" -> updated = updated.copy(wbc103Ul = bm.value)
                "lymphocytePercent" -> updated = updated.copy(lymphocytePercent = bm.value)
                "alpUL" -> updated = updated.copy(alpUL = bm.value)
            }
        }
        viewModel.updateProfile(updated)
        appliedSuccessMessage = "Biomarkers successfully synced to your Digital Twin!"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .testTag("nemotron_ocr_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "NVIDIA NEMOTRON™ OCR",
                        color = SleekPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Multimodal Lab Extraction & LOINC Standardization",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SleekGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Zero-Retention",
                        color = SleekGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Hardware / Backend Telemetry Badge Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SleekPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "NVIDIA Nemotron™ 70B Vision",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "vLLM / TensorRT-LLM • Zero-Retention HIPPA Compliant",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SleekGreen.copy(alpha = 0.15f))
                            .border(1.dp, SleekGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ONLINE",
                            color = SleekGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // 3. Document Selector Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECT MEDICAL LAB DOCUMENT",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleDocs.forEachIndexed { index, doc ->
                        val isSelected = selectedSampleIdx == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) SleekOnPrimary else DarkSurface)
                                .border(1.dp, if (isSelected) SleekPrimary else BorderSubtle, RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedSampleIdx = index
                                    pipelineStage = OcrPipelineStage.IDLE
                                    pipelineProgress = 0f
                                    appliedSuccessMessage = null
                                }
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Panel ${index + 1}",
                                    color = if (isSelected) SleekPrimary else TextMuted,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = doc.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Document Viewer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.DocumentScanner,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = currentDoc.title,
                                color = TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = currentDoc.date,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = currentDoc.description,
                        color = TextSecondary,
                        fontSize = 11.5.sp
                    )

                    // Pipeline Progress Bar
                    if (pipelineStage != OcrPipelineStage.IDLE) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pipelineStage.title,
                                    color = SleekPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(pipelineProgress * 100).toInt()}%",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            LinearProgressIndicator(
                                progress = { pipelineProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = SleekPrimary,
                                trackColor = DarkSurfaceVariant
                            )

                            Text(
                                text = pipelineStage.description,
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { runNemotronPipeline() },
                            modifier = Modifier.weight(1f),
                            enabled = pipelineStage == OcrPipelineStage.IDLE || pipelineStage == OcrPipelineStage.REVIEW,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekPrimary,
                                contentColor = SleekOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (pipelineStage == OcrPipelineStage.REVIEW) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (pipelineStage == OcrPipelineStage.REVIEW) "Re-Scan" else "Run AI Vision OCR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showJsonModal = !showJsonModal },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceVariant,
                                contentColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "JSON Preview",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("JSON", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // 5. JSON Preview Card (if toggled)
        if (showJsonModal) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SleekPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0B0E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "LOINC Extracted Payload (Nemotron vLLM Output)",
                            color = SleekPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = buildString {
                                append("{\n  \"document_id\": \"${currentDoc.id}\",\n  \"biomarkers\": [\n")
                                currentDoc.biomarkers.forEachIndexed { i, bm ->
                                    append("    {\n      \"loinc\": \"${bm.loincCode}\",\n      \"name\": \"${bm.name}\",\n      \"value\": ${bm.value},\n      \"unit\": \"${bm.unit}\",\n      \"confidence\": ${bm.confidence}\n    }${if (i < currentDoc.biomarkers.size - 1) "," else ""}\n")
                                }
                                append("  ]\n}")
                            },
                            color = SleekSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // 6. Extracted Biomarkers Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXTRACTED BIOMARKERS (${currentDoc.biomarkers.size})",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                if (pipelineStage == OcrPipelineStage.REVIEW) {
                    Text(
                        text = "Confidence: >98%",
                        color = SleekGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(currentDoc.biomarkers) { bm ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = bm.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = bm.loincCode,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Confidence: ${(bm.confidence * 100).toInt()}%",
                                color = SleekPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${bm.value} ${bm.unit}",
                            color = if (bm.isOptimal) SleekGreen else SleekCoral,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (bm.isOptimal) SleekGreen.copy(alpha = 0.15f) else SleekCoral.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (bm.isOptimal) Icons.Default.Check else Icons.Default.Security,
                                contentDescription = null,
                                tint = if (bm.isOptimal) SleekGreen else SleekCoral,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // 7. Commit to Digital Twin Button
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { applyExtractedDataToTwin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SleekPrimary,
                        contentColor = SleekOnPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sync Extracted Data to Digital Twin",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (appliedSuccessMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SleekGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SleekGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = appliedSuccessMessage ?: "",
                                    color = SleekGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onNavigateToDashboard,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("View Twin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
