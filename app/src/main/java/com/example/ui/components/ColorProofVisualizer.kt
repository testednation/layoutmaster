package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CmykValues
import com.example.model.ColorManagementEngine
import com.example.model.IccColorProfile
import com.example.model.SwatchCalibrationJob
import com.example.model.WorkingColorModel
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.PressInkBlack
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted
import java.util.Locale

@Composable
fun SwatchProofAndBleedCanvas(
    job: SwatchCalibrationJob,
    userCmyk: CmykValues,
    userProfile: IccColorProfile,
    userColorModel: WorkingColorModel,
    bleedMm: Int,
    softProofPaper: Boolean,
    testGuillotineShift: Boolean,
    deltaE: Float,
    modifier: Modifier = Modifier
) {
    val targetColor = ColorManagementEngine.simulateRenderedSwatch(
        cmyk = job.targetCmyk,
        profile = job.targetProfile,
        colorModel = WorkingColorModel.CMYK,
        softProofPaper = true
    )
    val userColor = ColorManagementEngine.simulateRenderedSwatch(
        cmyk = userCmyk,
        profile = userProfile,
        colorModel = userColorModel,
        softProofPaper = softProofPaper
    )
    val (r, g, b) = ColorManagementEngine.toRgbTriple(userCmyk)
    val tac = userCmyk.totalInkCoverage
    val isTacExceeded = tac > userProfile.maxTacPercent
    val isDeltaMatch = deltaE <= 4.0f
    val isBleedSafe = bleedMm >= job.requiredBleedMm
    val hasWhiteTrimEdgeDefect = testGuillotineShift && !isBleedSafe

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianPasteboard)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            .testTag("swatch_proof_bleed_canvas")
    ) {
        // Top Spectrophotometer Status Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDockSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            when {
                                isDeltaMatch && !isTacExceeded -> PreflightPassGreen
                                deltaE <= 8.5f -> ProcessYellowGold
                                else -> BleedAlertRed
                            }
                        )
                )
                Text(
                    text = String.format(Locale.US, "SPECTRO ΔE: %.1f", deltaE),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isDeltaMatch -> PreflightPassGreen
                        deltaE <= 8.5f -> ProcessYellowGold
                        else -> BleedAlertRed
                    }
                )
                Text(
                    text = if (isDeltaMatch) "(PRESS MATCH ✓)" else "(Target ≤ 4.0)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }

            Surface(
                color = if (isTacExceeded) BleedAlertRed.copy(alpha = 0.2f) else ObsidianPasteboard,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isTacExceeded) BleedAlertRed else StudioBorderSubtle,
                    shape = RoundedCornerShape(6.dp)
                )
            ) {
                Text(
                    text = "TAC: $tac% / ${userProfile.maxTacPercent}%",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isTacExceeded) BleedAlertRed else ProcessCyanGuide,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }

        // Split Press-Proof Card: Left = Target Printed Swatch | Right = Calibrated Digital Output with Bleed & Trim Marks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Crop Marks & Bleed Perimeter Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(216.dp)
            ) {
                val trimLeft = 10.dp.toPx()
                val trimTop = 10.dp.toPx()
                val trimW = size.width - 20.dp.toPx()
                val trimH = size.height - 20.dp.toPx()
                val bleedPx = (bleedMm * 2.0f).dp.toPx().coerceAtMost(10.dp.toPx())

                // Registration / Trim Crop Marks
                val cropLen = 8.dp.toPx()
                val markColor = Color(0xFF8C96B0)
                drawLine(markColor, Offset(0f, trimTop), Offset(cropLen, trimTop), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft, 0f), Offset(trimLeft, cropLen), strokeWidth = 1.5f)
                drawLine(markColor, Offset(size.width - cropLen, trimTop), Offset(size.width, trimTop), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft + trimW, 0f), Offset(trimLeft + trimW, cropLen), strokeWidth = 1.5f)
                drawLine(markColor, Offset(0f, trimTop + trimH), Offset(cropLen, trimTop + trimH), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft, size.height - cropLen), Offset(trimLeft, size.height), strokeWidth = 1.5f)
                drawLine(markColor, Offset(size.width - cropLen, trimTop + trimH), Offset(size.width, trimTop + trimH), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft + trimW, size.height - cropLen), Offset(trimLeft + trimW, size.height), strokeWidth = 1.5f)

                // Red Bleed Guide Box (visible around the trimmed card)
                drawRect(
                    color = if (isBleedSafe) PreflightPassGreen else BleedAlertRed,
                    topLeft = Offset(trimLeft - bleedPx, trimTop - bleedPx),
                    size = Size(trimW + bleedPx * 2, trimH + bleedPx * 2),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                    )
                )
            }

            // Inner Split Swatch Proof Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ArchivalPaperCream)
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(4.dp))
            ) {
                // LEFT HALF: Physical Target Printed Press Swatch
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(targetColor)
                            .padding(10.dp)
                    ) {
                        Surface(
                            color = ObsidianPasteboard.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "TARGET PRINT SWATCH",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProcessYellowGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = job.swatchName,
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (job.targetCmyk.k > 60) Color.White else Color.White.copy(alpha = 0.95f),
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }

                    // Swatch Paper Footer Strip (Pantone / Prepress Specimen Footer)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArchivalPaperCream)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = job.targetProfile.displayName,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = PressInkBlack,
                            maxLines = 1
                        )
                        Text(
                            text = "REQ BLEED: ${job.requiredBleedMm}mm • CMYK",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 7.5.sp,
                            color = Color(0xFF4A4740)
                        )
                    }
                }

                // Hairline comparison seam
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(ArchivalPaperCream)
                )

                // RIGHT HALF: User's Live Digital Output (with Guillotine Bleed Shift Simulation!)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(ArchivalPaperCream)
                    ) {
                        // Artwork Swatch Fill (shows a stark white paper sliver on top/right if guillotine shift happens without enough bleed!)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = if (hasWhiteTrimEdgeDefect) 7.dp else 0.dp,
                                    end = if (hasWhiteTrimEdgeDefect) 7.dp else 0.dp
                                )
                                .background(userColor)
                                .padding(10.dp)
                        ) {
                            Surface(
                                color = ObsidianPasteboard.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "YOUR ${userColorModel.badge} OUTPUT",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (userColorModel == WorkingColorModel.CMYK) ProcessCyanGuide else BleedAlertRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            // Out-Of-Gamut Warning Triangle when editing in RGB mode
                            if (userColorModel == WorkingColorModel.RGB) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(BleedAlertRed.copy(alpha = 0.92f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = "Out of CMYK Gamut Warning",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "OUT OF PRINT GAMUT",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else if (hasWhiteTrimEdgeDefect) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(BleedAlertRed.copy(alpha = 0.92f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = "White Trim Edge Error",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "WHITE TRIM HAIRLINE!",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // User Digital Output Live Values Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ArchivalPaperCream)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "C${userCmyk.c} M${userCmyk.m} Y${userCmyk.y} K${userCmyk.k}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = PressInkBlack
                        )
                        Text(
                            text = "RGB($r,$g,$b) • Bleed ${bleedMm}mm",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 7.5.sp,
                            color = if (isBleedSafe) Color(0xFF0A7B56) else BleedAlertRed
                        )
                    }
                }
            }
        }

        // Bottom Plate Separation Mini-Strip (Shows 4-Color Offset Plates C, M, Y, K)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDockSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlateInkChip("C", userCmyk.c, ProcessCyanGuide, Modifier.weight(1f))
            PlateInkChip("M", userCmyk.m, ProcessMagentaGuide, Modifier.weight(1f))
            PlateInkChip("Y", userCmyk.y, ProcessYellowGold, Modifier.weight(1f))
            PlateInkChip("K", userCmyk.k, Color(0xFFB4BED6), Modifier.weight(1f))
        }
    }
}

@Composable
private fun PlateInkChip(
    plateLetter: String,
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ObsidianPasteboard)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = plateLetter,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "$percent%",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(StudioBorderSubtle.copy(alpha = 0.4f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percent / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}
