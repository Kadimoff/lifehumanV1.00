package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.BiologicalTwinResult
import com.example.data.model.BiomarkerProfile
import com.example.data.model.TrajectoryForecast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportService {

    fun generateLongevityReport(
        context: Context,
        profile: BiomarkerProfile,
        result: BiologicalTwinResult,
        forecast: TrajectoryForecast
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (595 x 842 pt)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paints
        val darkBgPaint = Paint().apply { color = Color.parseColor("#0B132B") }
        val headerCardPaint = Paint().apply { color = Color.parseColor("#1C2541") }
        val accentTealPaint = Paint().apply { color = Color.parseColor("#00F5D4") }
        val accentCyanPaint = Paint().apply { color = Color.parseColor("#00BBF9") }
        val accentGoldPaint = Paint().apply { color = Color.parseColor("#FFD166") }
        val accentCoralPaint = Paint().apply { color = Color.parseColor("#FF5964") }
        val cardBgPaint = Paint().apply { color = Color.parseColor("#111827") }
        val tableRowAltPaint = Paint().apply { color = Color.parseColor("#162032") }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#26334D")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val textTitlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textSubtitlePaint = Paint().apply {
            color = Color.parseColor("#94A3B8")
            textSize = 9f
            isAntiAlias = true
        }

        val textBodyPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 8.5f
            isAntiAlias = true
        }

        val textBoldBodyPaint = Paint().apply {
            color = Color.WHITE
            textSize = 8.5f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textBadgePaint = Paint().apply {
            color = Color.parseColor("#00F5D4")
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }

        // 1. Header Banner
        canvas.drawRect(0f, 0f, 595f, 75f, darkBgPaint)
        canvas.drawText("LIFEMAP HUMAN™", 24f, 32f, textTitlePaint)
        canvas.drawText("Executive Clinical Longevity & Digital Twin Report", 24f, 46f, textSubtitlePaint)
        
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Date: $dateStr • Model: Morgan Levine PhenoAge v2.4", 24f, 62f, textSubtitlePaint)

        // 2. Executive Biomarker Summary Card
        var curY = 88f
        val summaryRect = RectF(20f, curY, 575f, curY + 95f)
        canvas.drawRoundRect(summaryRect, 8f, 8f, headerCardPaint)
        canvas.drawRoundRect(summaryRect, 8f, 8f, borderPaint)

        // Metrics in columns
        val colW = (575f - 20f) / 4f

        // Col 1: Chrono Age
        canvas.drawText("CHRONO AGE", 36f, curY + 22f, textSubtitlePaint)
        canvas.drawText("${profile.chronologicalAge.toInt()} yrs", 36f, curY + 44f, textTitlePaint)
        canvas.drawText("Biological Baseline", 36f, curY + 60f, textSubtitlePaint)

        // Col 2: PhenoAge
        val phenoColor = if (result.ageDifference <= 0) accentTealPaint else accentCoralPaint
        canvas.drawText("PHENOTYPIC AGE", 36f + colW, curY + 22f, textSubtitlePaint)
        val phenoPaint = Paint(textTitlePaint).apply { color = phenoColor.color }
        canvas.drawText("${result.phenoAge} yrs", 36f + colW, curY + 44f, phenoPaint)
        val diffSign = if (result.ageDifference > 0) "+${result.ageDifference}" else "${result.ageDifference}"
        canvas.drawText("Delta: $diffSign yrs (${result.paceOfAging}x)", 36f + colW, curY + 60f, textSubtitlePaint)

        // Col 3: Longevity Reserve Score (LRS)
        canvas.drawText("RESERVE SCORE (LRS)", 36f + colW * 2, curY + 22f, textSubtitlePaint)
        val lrsPaint = Paint(textTitlePaint).apply { color = accentGoldPaint.color }
        canvas.drawText("${result.longevityReserveScore}/100", 36f + colW * 2, curY + 44f, lrsPaint)
        canvas.drawText("Harmonic 7-System Mean", 36f + colW * 2, curY + 60f, textSubtitlePaint)

        // Col 4: Projected Independence
        canvas.drawText("FUNCTIONAL SPAN", 36f + colW * 3, curY + 22f, textSubtitlePaint)
        canvas.drawText("${result.projectedIndependenceYears} yrs", 36f + colW * 3, curY + 44f, textTitlePaint)
        canvas.drawText("Disability-Free Target", 36f + colW * 3, curY + 60f, textSubtitlePaint)

        // Dominant aging phenotype banner
        canvas.drawText("DOMINANT AGING PHENOTYPE: ${result.dominantAgingPhenotype.uppercase()}", 36f, curY + 84f, textBadgePaint)

        curY += 110f

        // 3. 8 Functional Reserves Scorecard Table
        canvas.drawText("8-SYSTEM FUNCTIONAL BIOLOGICAL RESERVES", 24f, curY, textBoldBodyPaint)
        curY += 10f

        // Table Header
        val tableHeadRect = RectF(20f, curY, 575f, curY + 20f)
        canvas.drawRect(tableHeadRect, headerCardPaint)
        canvas.drawText("ORGAN SYSTEM", 30f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("SCORE", 175f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("STATUS", 230f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("KEY METRICS", 350f, curY + 14f, textBoldBodyPaint)
        curY += 20f

        // Rows
        result.reserves.forEachIndexed { index, r ->
            val rowRect = RectF(20f, curY, 575f, curY + 24f)
            val bg = if (index % 2 == 0) cardBgPaint else tableRowAltPaint
            canvas.drawRect(rowRect, bg)
            canvas.drawRect(rowRect, borderPaint)

            canvas.drawText(r.name, 30f, curY + 16f, textBoldBodyPaint)

            val scoreColor = if (r.score >= 80) accentTealPaint else if (r.score >= 60) accentGoldPaint else accentCoralPaint
            val scPaint = Paint(textBoldBodyPaint).apply { color = scoreColor.color }
            canvas.drawText("${r.score.toInt()}/100", 175f, curY + 16f, scPaint)

            canvas.drawText(r.statusLabel, 230f, curY + 16f, textBodyPaint)
            canvas.drawText(r.metricSummary.take(45), 350f, curY + 16f, textSubtitlePaint)

            curY += 24f
        }

        curY += 16f

        // 4. Clinical Priority Recommendations
        canvas.drawText("PRIMARY CLINICAL LONGEVITY LEVERAGE INTERVENTIONS", 24f, curY, textBoldBodyPaint)
        curY += 10f

        val recoRect = RectF(20f, curY, 575f, curY + 54f)
        canvas.drawRoundRect(recoRect, 6f, 6f, cardBgPaint)
        canvas.drawRoundRect(recoRect, 6f, 6f, borderPaint)

        canvas.drawText("Target System: ${result.dominantAgingPhenotype}", 32f, curY + 18f, textBadgePaint)
        canvas.drawText(result.dominantPhenotypeDescription.take(90), 32f, curY + 32f, textBodyPaint)
        canvas.drawText("Action: ${result.primaryRecommendation.take(90)}", 32f, curY + 46f, textBoldBodyPaint)

        curY += 70f

        // 5. 5-Trajectory Scenario Forecasting Table
        canvas.drawText("5-TRAJECTORY SCENARIO FORECAST (PROJECTED BIOLOGICAL AGE)", 24f, curY, textBoldBodyPaint)
        curY += 10f

        val fHeadRect = RectF(20f, curY, 575f, curY + 20f)
        canvas.drawRect(fHeadRect, headerCardPaint)
        canvas.drawText("SCENARIO INTERVENTION", 30f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("YEAR 0", 250f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("YEAR 5", 330f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("YEAR 10", 410f, curY + 14f, textBoldBodyPaint)
        canvas.drawText("YEAR 20", 490f, curY + 14f, textBoldBodyPaint)
        curY += 20f

        val scList = listOf(
            "Scenario A (Status Quo)",
            "Scenario B (Nutrition Opt)",
            "Scenario C (Strength & VO2max)",
            "Scenario D (Sleep/Circadian)",
            "Scenario E (Synergistic Combo)"
        )

        val p0 = forecast.points.getOrNull(0)
        val p5 = forecast.points.getOrNull(1)
        val p10 = forecast.points.getOrNull(2)
        val p20 = forecast.points.getOrNull(3)

        scList.forEachIndexed { index, scName ->
            val rowRect = RectF(20f, curY, 575f, curY + 20f)
            val bg = if (index % 2 == 0) cardBgPaint else tableRowAltPaint
            canvas.drawRect(rowRect, bg)
            canvas.drawRect(rowRect, borderPaint)

            canvas.drawText(scName, 30f, curY + 14f, textBoldBodyPaint)

            val v0 = when (index) {
                0 -> p0?.scenarioABiologicalAge ?: 0.0
                1 -> p0?.scenarioBBiologicalAge ?: 0.0
                2 -> p0?.scenarioCBiologicalAge ?: 0.0
                3 -> p0?.scenarioDBiologicalAge ?: 0.0
                else -> p0?.scenarioEBiologicalAge ?: 0.0
            }
            val v5 = when (index) {
                0 -> p5?.scenarioABiologicalAge ?: 0.0
                1 -> p5?.scenarioBBiologicalAge ?: 0.0
                2 -> p5?.scenarioCBiologicalAge ?: 0.0
                3 -> p5?.scenarioDBiologicalAge ?: 0.0
                else -> p5?.scenarioEBiologicalAge ?: 0.0
            }
            val v10 = when (index) {
                0 -> p10?.scenarioABiologicalAge ?: 0.0
                1 -> p10?.scenarioBBiologicalAge ?: 0.0
                2 -> p10?.scenarioCBiologicalAge ?: 0.0
                3 -> p10?.scenarioDBiologicalAge ?: 0.0
                else -> p10?.scenarioEBiologicalAge ?: 0.0
            }
            val v20 = when (index) {
                0 -> p20?.scenarioABiologicalAge ?: 0.0
                1 -> p20?.scenarioBBiologicalAge ?: 0.0
                2 -> p20?.scenarioCBiologicalAge ?: 0.0
                3 -> p20?.scenarioDBiologicalAge ?: 0.0
                else -> p20?.scenarioEBiologicalAge ?: 0.0
            }

            canvas.drawText("${v0.toInt()} yrs", 250f, curY + 14f, textBodyPaint)
            canvas.drawText("${v5.toInt()} yrs", 330f, curY + 14f, textBodyPaint)
            canvas.drawText("${v10.toInt()} yrs", 410f, curY + 14f, textBodyPaint)

            val finalColor = if (index == 4) accentTealPaint else if (index == 0) accentCoralPaint else accentCyanPaint
            val fPaint = Paint(textBoldBodyPaint).apply { color = finalColor.color }
            canvas.drawText("${v20.toInt()} yrs", 490f, curY + 14f, fPaint)

            curY += 20f
        }

        curY += 24f

        // Footer disclaimer
        canvas.drawText("CONFIDENTIAL MEDICAL SUMMARY • COMPUTED LOCALLY VIA CLIENT-SIDE MATHEMATICAL TWIN ENGINE", 24f, 810f, textSubtitlePaint)
        canvas.drawText("Algorithms based on NHANES III / Morgan Levine PhenoAge methodology. For informational longevity tracking only.", 24f, 822f, textSubtitlePaint)

        pdfDocument.finishPage(page)

        // Save file
        val outputDir = File(context.cacheDir, "reports")
        if (!outputDir.exists()) outputDir.mkdirs()
        val file = File(outputDir, "LIFEMAP_Report_${System.currentTimeMillis()}.pdf")

        try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            fos.flush()
            fos.close()
            pdfDocument.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            // Fallback direct uri or handle
            null
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Export Longevity Report"))
    }
}
