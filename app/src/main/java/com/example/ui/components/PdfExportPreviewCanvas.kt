package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ExportOutputProfile
import com.example.model.PdfExportSettingsState
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.PressInkBlack
import com.example.ui.theme.PressInkMuted
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun PdfExportPreviewCanvas(
    state: PdfExportSettingsState,
    jobCode: String,
    modifier: Modifier = Modifier
) {
    val isRgbWarning = !state.outputProfile.isCmyk
    val isLowDpi = state.colorImageDpi < 150
    val isUncoated = state.outputProfile == ExportOutputProfile.UNCOATED_FOGRA29

    // Simulate paper/ink appearance based on selected ICC Output Profile
    val imageColorFilter = when {
        isRgbWarning -> ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(1.38f) } // Oversaturated uncalibrated RGB
        )
        isUncoated -> ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(0.76f) } // Absorptive uncoated dot gain
        )
        else -> ColorFilter.colorMatrix(
            ColorMatrix().apply { setToSaturation(0.95f) } // Calibrated coated CMYK proof
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1E29))
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            .padding(10.dp)
            .testTag("pdf_export_preview_canvas")
    ) {
        // Top Press Sheet Telemetry Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PreviewPill(
                    text = "${state.colorImageDpi} DPI",
                    color = when {
                        state.colorImageDpi >= 300 -> PreflightPassGreen
                        state.colorImageDpi >= 150 -> ProcessYellowGold
                        else -> BleedAlertRed
                    }
                )
                PreviewPill(
                    text = if (state.outputProfile.isCmyk) "CMYK" else "RGB TRAP",
                    color = if (state.outputProfile.isCmyk) ProcessCyanGuide else BleedAlertRed
                )
                PreviewPill(
                    text = if (state.exportAsSpreads) "SPREAD" else "1-UP PAGE",
                    color = if (state.exportAsSpreads) ProcessYellowGold else PreflightPassGreen
                )
            }
            Text(
                text = "Est. ${state.estimatedFileSizeMb} MB",
                style = MaterialTheme.typography.labelSmall,
                color = ProcessYellowGold,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Press Sheet Stage (Simulates Oversized MediaBox with TrimBox, BleedBox, Marks & Bars)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(236.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFECE9E2)), // Oversized press sheet stock
            contentAlignment = Alignment.Center
        ) {
            val bleedFraction = (state.bleedMm / 5f).coerceIn(0f, 1f)
            val bleedExpandDp = (bleedFraction * 10f).dp
            val sheetWidthRatio = if (state.exportAsSpreads) 0.72f else 0.48f
            val trimWidth = maxWidth * sheetWidthRatio
            val trimHeight = maxHeight * 0.66f

            // 1. Live BleedBox artwork extension behind TrimBox
            if (state.bleedMm > 0f) {
                Box(
                    modifier = Modifier
                        .width(trimWidth + bleedExpandDp * 2)
                        .height(trimHeight + bleedExpandDp * 2)
                        .background(
                            if (isUncoated) Color(0xFF2B2825) else Color(0xFF171B26)
                        )
                        .border(1.dp, BleedAlertRed)
                )
            }

            // 2. TrimBox Document Sheet (Single Page or 2-Page Spread)
            Box(
                modifier = Modifier
                    .width(trimWidth)
                    .height(trimHeight)
                    .background(if (isUncoated) Color(0xFFF3ECE0) else ArchivalPaperCream)
                    .border(0.75.dp, Color(0xFF6B7280).copy(alpha = 0.65f))
            ) {
                if (state.exportAsSpreads) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SimulatedEditorialPageContent(
                            isLeftPage = true,
                            state = state,
                            colorFilter = imageColorFilter,
                            isLowDpi = isLowDpi,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        // Center spine fold line
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(PressInkMuted.copy(alpha = 0.45f))
                        )
                        SimulatedEditorialPageContent(
                            isLeftPage = false,
                            state = state,
                            colorFilter = imageColorFilter,
                            isLowDpi = isLowDpi,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    SimulatedEditorialPageContent(
                        isLeftPage = false,
                        state = state,
                        colorFilter = imageColorFilter,
                        isLowDpi = isLowDpi,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // 3. Printer's Marks Overlay Canvas (Crop Marks, Bleed Marks, Registration Targets, Color Bars)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val trimW = trimWidth.toPx()
                val trimH = trimHeight.toPx()
                val trimLeft = (size.width - trimW) / 2f
                val trimTop = (size.height - trimH) / 2f
                val trimRight = trimLeft + trimW
                val trimBottom = trimTop + trimH

                val bleedPx = bleedExpandDp.toPx()
                val offsetPx = (state.markOffsetMm * 2.2f).coerceIn(3f, 16f)
                val markLen = 14f
                val strokeColor = Color(0xFF111318)

                // Crop / Trim Marks
                if (state.cropMarks) {
                    val corners = listOf(
                        Pair(Offset(trimLeft, trimTop), Pair(-1f, -1f)),
                        Pair(Offset(trimRight, trimTop), Pair(1f, -1f)),
                        Pair(Offset(trimLeft, trimBottom), Pair(-1f, 1f)),
                        Pair(Offset(trimRight, trimBottom), Pair(1f, 1f))
                    )
                    corners.forEach { (pt, dir) ->
                        // Horizontal trim tick
                        drawLine(
                            color = strokeColor,
                            start = Offset(pt.x + dir.first * offsetPx, pt.y),
                            end = Offset(pt.x + dir.first * (offsetPx + markLen), pt.y),
                            strokeWidth = 1.4f
                        )
                        // Vertical trim tick
                        drawLine(
                            color = strokeColor,
                            start = Offset(pt.x, pt.y + dir.second * offsetPx),
                            end = Offset(pt.x, pt.y + dir.second * (offsetPx + markLen)),
                            strokeWidth = 1.4f
                        )
                    }
                }

                // Bleed Marks (Dashed / Secondary outer marks around BleedBox)
                if (state.bleedMarks && state.bleedMm > 0f) {
                    val bLeft = trimLeft - bleedPx
                    val bTop = trimTop - bleedPx
                    val bRight = trimRight + bleedPx
                    val bBottom = trimBottom + bleedPx
                    val bleedCorners = listOf(
                        Pair(Offset(bLeft, bTop), Pair(-1f, -1f)),
                        Pair(Offset(bRight, bTop), Pair(1f, -1f)),
                        Pair(Offset(bLeft, bBottom), Pair(-1f, 1f)),
                        Pair(Offset(bRight, bBottom), Pair(1f, 1f))
                    )
                    bleedCorners.forEach { (pt, dir) ->
                        drawLine(
                            color = ProcessCyanGuide,
                            start = Offset(pt.x + dir.first * (offsetPx + 2f), pt.y),
                            end = Offset(pt.x + dir.first * (offsetPx + markLen - 2f), pt.y),
                            strokeWidth = 1.2f
                        )
                        drawLine(
                            color = ProcessCyanGuide,
                            start = Offset(pt.x, pt.y + dir.second * (offsetPx + 2f)),
                            end = Offset(pt.x, pt.y + dir.second * (offsetPx + markLen - 2f)),
                            strokeWidth = 1.2f
                        )
                    }
                }

                // Registration Marks (Crosshairs + Bullseye centered on all 4 sides)
                if (state.registrationMarks) {
                    val regOffset = bleedPx + offsetPx + 8f
                    val regPoints = listOf(
                        Offset(size.width / 2f, trimTop - regOffset),
                        Offset(size.width / 2f, trimBottom + regOffset),
                        Offset(trimLeft - regOffset, size.height / 2f),
                        Offset(trimRight + regOffset, size.height / 2f)
                    )
                    regPoints.forEach { c ->
                        drawCircle(
                            color = strokeColor,
                            radius = 5.5f,
                            center = c,
                            style = Stroke(width = 1.2f)
                        )
                        drawLine(
                            color = strokeColor,
                            start = Offset(c.x - 9f, c.y),
                            end = Offset(c.x + 9f, c.y),
                            strokeWidth = 1.1f
                        )
                        drawLine(
                            color = strokeColor,
                            start = Offset(c.x, c.y - 9f),
                            end = Offset(c.x, c.y + 9f),
                            strokeWidth = 1.1f
                        )
                    }
                }

                // CMYK Color Bars + Grayscale Tint Ramp along top press margin
                if (state.colorBars) {
                    val swatches = listOf(
                        Color(0xFF00AEEF), // C
                        Color(0xFFEC008C), // M
                        Color(0xFFFFF200), // Y
                        Color(0xFF111111), // K
                        Color(0xFF2E3192), // C+M
                        Color(0xFFED1C24), // M+Y
                        Color(0xFF00A651), // C+Y
                        Color(0xFF999999), // 50% K
                        Color(0xFFCCCCCC)  // 25% K
                    )
                    val swatchW = 11f
                    val swatchH = 7f
                    val startX = trimLeft + 6f
                    val barY = 7f
                    swatches.forEachIndexed { idx, swatchColor ->
                        val x = startX + idx * (swatchW + 1.5f)
                        drawRect(
                            color = swatchColor,
                            topLeft = Offset(x, barY),
                            size = Size(swatchW, swatchH)
                        )
                        drawRect(
                            color = Color.Black.copy(alpha = 0.35f),
                            topLeft = Offset(x, barY),
                            size = Size(swatchW, swatchH),
                            style = Stroke(width = 0.6f)
                        )
                    }
                }

                // Dashed Slug Area Border when Include Slug Area is active
                if (state.includeSlugArea) {
                    val slugPad = bleedPx + 18f
                    drawRect(
                        color = ProcessCyanGuide.copy(alpha = 0.7f),
                        topLeft = Offset(
                            (trimLeft - slugPad).coerceAtLeast(4f),
                            (trimTop - slugPad).coerceAtLeast(4f)
                        ),
                        size = Size(
                            (trimW + slugPad * 2).coerceAtMost(size.width - 8f),
                            (trimH + slugPad * 2).coerceAtMost(size.height - 8f)
                        ),
                        style = Stroke(
                            width = 1.2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                        )
                    )
                }
            }

            // 4. Page Information Slug Strip along bottom of press sheet
            if (state.pageInformation) {
                Text(
                    text = "PicaPress_${jobCode.take(6)}.pdf   Pg 1   ${state.outputProfile.displayName.take(18)}   ${state.colorImageDpi}DPI",
                    fontSize = 8.sp,
                    color = PressInkBlack.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp, start = 8.dp, end = 8.dp)
                )
            }

            // 5. Missing Bleed Risk Callout
            if (state.bleedMm == 0f) {
                Surface(
                    color = BleedAlertRed.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "0mm BLEED RISK",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Legend Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Standard: ${state.standard.displayName}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Bleed: ${state.bleedMm}mm • Offset: ${state.markOffsetMm}mm",
                style = MaterialTheme.typography.labelSmall,
                color = if (state.bleedMm >= 3f) PreflightPassGreen else ProcessYellowGold
            )
        }
    }
}

@Composable
private fun SimulatedEditorialPageContent(
    isLeftPage: Boolean,
    state: PdfExportSettingsState,
    colorFilter: ColorFilter,
    isLowDpi: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Bar (simulating full-bleed color masthead)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(
                    if (state.outputProfile == ExportOutputProfile.SRGB_WEB) {
                        Color(0xFF00E5FF) // Neon RGB trap
                    } else if (state.outputProfile == ExportOutputProfile.UNCOATED_FOGRA29) {
                        Color(0xFF2F5666) // Muted uncoated ink
                    } else {
                        Color(0xFF143C54) // Calibrated CMYK
                    }
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (isLeftPage) "SWISS GRID MONOGRAPH" else "PICAPRESS ISSUE 14",
                fontSize = 6.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Hero Photo Block with Simulated DPI Mosaic Overlay when < 150 DPI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_editorial_hero_1795037988082),
                contentDescription = "Simulated print layout image preview",
                contentScale = ContentScale.Crop,
                colorFilter = colorFilter,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isLowDpi) 0.72f else 1f)
            )

            // Pixelation grid simulation for 72 DPI
            if (isLowDpi) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cell = 6.dp.toPx()
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.25f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1f
                        )
                        x += cell
                    }
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.25f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                        y += cell
                    }
                }
                Surface(
                    color = BleedAlertRed.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "LOW-RES 72 DPI",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // Simulated Micro Body Columns
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { idx ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (idx == 2) 0.68f else 1f)
                        .height(2.5.dp)
                        .background(PressInkBlack.copy(alpha = 0.55f))
                )
            }
        }
    }
}

@Composable
private fun PreviewPill(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
