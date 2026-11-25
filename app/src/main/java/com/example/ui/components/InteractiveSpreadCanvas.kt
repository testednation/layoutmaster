package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DtpCurriculum
import com.example.model.EditorialFontChoice
import com.example.model.SpreadLayoutState
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BaselineGridBlue
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.PressInkBlack
import com.example.ui.theme.PressInkMuted
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioDockSurface

fun EditorialFontChoice.toComposeFontFamily(): FontFamily = when (this) {
    EditorialFontChoice.PLAYFAIR -> PlayfairDisplayFontFamily
    EditorialFontChoice.SPACE_GROTESK -> SpaceGroteskFontFamily
    EditorialFontChoice.PLUS_JAKARTA -> PlusJakartaSansFontFamily
    EditorialFontChoice.JETBRAINS_MONO -> JetBrainsMonoFontFamily
}

@Composable
fun InteractiveSpreadCanvas(
    state: SpreadLayoutState,
    modifier: Modifier = Modifier
) {
    val showGuides = !state.previewTrimMode
    val marginDp = (state.marginPicas * 4.5f).coerceIn(6f, 24f).dp
    val gutterDp = (state.gutterPicas * 5f).coerceIn(3f, 18f).dp
    val wrapOffsetDp = if (state.textWrapEnabled) (state.textWrapOffsetPicas * 5f).dp else 0.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianPasteboard)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            .testTag("interactive_spread_canvas")
    ) {
        // Top DTP Ruler Bar (Picas: 0p, 6p, 12p, 18p, 24p, 30p, 36p, 42p, 48p)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(StudioDockSurface)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (showGuides) "PICAS (1in=6p0)" else "TRIM PREVIEW (${state.softwareMode.previewShortcut.substringBefore(" ")})",
                style = MaterialTheme.typography.labelSmall,
                color = if (showGuides) ProcessCyanGuide else PreflightPassGreen
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("0p", "6p", "12p", "18p", "24p", "30p", "36p", "42p").forEach { tick ->
                    Text(
                        text = tick,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8A94AD)
                    )
                }
            }
        }

        // Pasteboard surrounding the Magazine Spread
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Bleed Guide & Crop Marks Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(335.dp)
            ) {
                val bleedPx = (state.bleedMm * 2.5f).dp.toPx()
                val trimLeft = 10.dp.toPx()
                val trimTop = 10.dp.toPx()
                val trimWidth = size.width - 20.dp.toPx()
                val trimHeight = size.height - 20.dp.toPx()

                // Draw Crop Marks & Registration Marks at corners
                val cropLen = 8.dp.toPx()
                val markColor = Color(0xFF7B849E)
                // Top-left crop mark
                drawLine(markColor, Offset(0f, trimTop), Offset(cropLen, trimTop), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft, 0f), Offset(trimLeft, cropLen), strokeWidth = 1.5f)
                // Top-right crop mark
                drawLine(markColor, Offset(size.width - cropLen, trimTop), Offset(size.width, trimTop), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft + trimWidth, 0f), Offset(trimLeft + trimWidth, cropLen), strokeWidth = 1.5f)
                // Bottom-left crop mark
                drawLine(markColor, Offset(0f, trimTop + trimHeight), Offset(cropLen, trimTop + trimHeight), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft, size.height - cropLen), Offset(trimLeft, size.height), strokeWidth = 1.5f)
                // Bottom-right crop mark
                drawLine(markColor, Offset(size.width - cropLen, trimTop + trimHeight), Offset(size.width, trimTop + trimHeight), strokeWidth = 1.5f)
                drawLine(markColor, Offset(trimLeft + trimWidth, size.height - cropLen), Offset(trimLeft + trimWidth, size.height), strokeWidth = 1.5f)

                // Red Bleed Perimeter Line (Visible in Normal Authoring View)
                if (showGuides && state.bleedMm > 0) {
                    drawRect(
                        color = BleedAlertRed,
                        topLeft = Offset(trimLeft - bleedPx, trimTop - bleedPx),
                        size = Size(trimWidth + bleedPx * 2, trimHeight + bleedPx * 2),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                        )
                    )
                }
            }

            // The Trimmed Paper Page Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(315.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(ArchivalPaperCream)
            ) {
                // Non-printing Baseline Grid + Margin + Column Guides Overlay
                if (showGuides) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val mPx = marginDp.toPx()
                        val gPx = gutterDp.toPx()
                        val contentW = (size.width - mPx * 2).coerceAtLeast(10f)
                        val contentH = (size.height - mPx * 2).coerceAtLeast(10f)

                        // Baseline Grid horizontal lines
                        if (state.baselineGridSnap) {
                            val stepPx = (state.leadingPt * 0.95f).coerceAtLeast(9f).dp.toPx()
                            var y = mPx + stepPx
                            while (y < size.height - mPx) {
                                drawLine(
                                    color = BaselineGridBlue.copy(alpha = 0.32f),
                                    start = Offset(mPx, y),
                                    end = Offset(size.width - mPx, y),
                                    strokeWidth = 1f
                                )
                                y += stepPx
                            }
                        }

                        // Magenta Margin Guide rectangle
                        drawRect(
                            color = ProcessMagentaGuide.copy(alpha = 0.85f),
                            topLeft = Offset(mPx, mPx),
                            size = Size(contentW, contentH),
                            style = Stroke(width = 1.2.dp.toPx())
                        )

                        // Cyan Column & Gutter Guides
                        val cols = state.columns.coerceIn(1, 4)
                        if (cols > 1) {
                            val totalGutters = (cols - 1) * gPx
                            val colWidth = (contentW - totalGutters) / cols
                            for (i in 1 until cols) {
                                val gutterLeft = mPx + i * colWidth + (i - 1) * gPx
                                val gutterRight = gutterLeft + gPx
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.85f),
                                    start = Offset(gutterLeft, mPx),
                                    end = Offset(gutterLeft, size.height - mPx),
                                    strokeWidth = 1.1.dp.toPx()
                                )
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.85f),
                                    start = Offset(gutterRight, mPx),
                                    end = Offset(gutterRight, size.height - mPx),
                                    strokeWidth = 1.1.dp.toPx()
                                )
                            }
                        }
                    }
                }

                // Actual Page Content inside Margin Area
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(marginDp)
                ) {
                    // A-Master Running Header & Automatic Folio Number
                    if (state.masterPageApplied) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "A-MASTER • ${state.softwareMode.displayName.uppercase()} GRID",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showGuides) ProcessMagentaGuide else PressInkMuted
                            )
                            Text(
                                text = "ISSUE 18 • FOLIO <04>",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = PressInkMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.8.dp)
                                .background(PressInkBlack.copy(alpha = 0.5f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Editorial Headline Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (showGuides) {
                                    Modifier.border(0.8.dp, ProcessCyanGuide.copy(alpha = 0.7f))
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 3.dp, vertical = 2.dp)
                    ) {
                        val scaledHeadlineSp = (state.headlineSizePt * 0.72f).coerceIn(13f, 24f).sp
                        Text(
                            text = state.title,
                            fontFamily = state.headlineFont.toComposeFontFamily(),
                            fontWeight = FontWeight.Bold,
                            fontSize = scaledHeadlineSp,
                            lineHeight = (scaledHeadlineSp.value * 1.12f).sp,
                            letterSpacing = (state.tracking * 0.02f).sp,
                            color = PressInkBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Hero Graphic Frame with Text Wrap / Item Runaround visualization
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (showGuides && state.textWrapEnabled) {
                                        Modifier
                                            .border(
                                                width = 1.dp,
                                                color = ProcessYellowGold,
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                            .padding(wrapOffsetDp)
                                    } else {
                                        Modifier.padding(bottom = wrapOffsetDp)
                                    }
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(74.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(
                                        width = if (showGuides) 1.dp else 0.dp,
                                        color = if (showGuides) ProcessCyanGuide else Color.Transparent,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_editorial_hero_1795037988082),
                                    contentDescription = "Placed editorial spread graphic",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(if (state.imagePpi < 300) 0.55f else 1f)
                                )

                                // Graphic Link & Color Space Inspector Tag
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .background(
                                            ObsidianPasteboard.copy(alpha = 0.86f),
                                            RoundedCornerShape(3.dp)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${state.imagePpi} PPI",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.imagePpi >= 300) PreflightPassGreen else BleedAlertRed
                                    )
                                    Text(
                                        text = state.colorSpace,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.colorSpace == "CMYK") ProcessCyanGuide else BleedAlertRed
                                    )
                                }

                                // Text Wrap Collision Warning when Text Wrap is Disabled
                                this@Column.AnimatedVisibility(
                                    visible = !state.textWrapEnabled,
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BleedAlertRed.copy(alpha = 0.88f))
                                            .padding(vertical = 2.dp, horizontal = 6.dp)
                                    ) {
                                        Text(
                                            text = "COLLISION: TEXT WRAP / RUNAROUND OFF — TEXT OVERLAPS IMAGE",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Multi-Column Threaded Body Story Frames
                    val columnsCount = state.columns.coerceIn(1, 4)
                    val sampleParagraphs = listOf(
                        "Swiss typographer Josef Müller-Brockmann demonstrated that a modular grid divides two-dimensional paper into proportional fields of picas and points. Every baseline aligns across adjacent gutters.",
                        "When Aldus PageMaker, QuarkXPress, and Adobe InDesign revolutionized prepress, compositors gained instant control over optical kerning, story threading ports, and 300 PPI CMYK separations.",
                        "Before packaging files for offset lithography, always verify 3mm bleed beyond the trim edge, convert RGB swatches to four-color process CMYK, and resolve every red [+] overset text port.",
                        "Master pages automate running headers and folios across 300-page monographs while paragraph styles lock body copy to a 12pt or 14pt baseline grid."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(gutterDp)
                        ) {
                            for (colIndex in 0 until columnsCount) {
                                val isColumnActive = state.linkedFrames || colIndex == 0
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .then(
                                            if (showGuides) {
                                                Modifier.border(
                                                    width = 0.9.dp,
                                                    color = if (isColumnActive) ProcessCyanGuide else StudioBorderSubtle.copy(alpha = 0.4f)
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .padding(3.dp)
                                ) {
                                    if (isColumnActive) {
                                        Column {
                                            // Render Drop Cap in Column 0
                                            if (colIndex == 0 && state.dropCapLines > 1) {
                                                Row(verticalAlignment = Alignment.Top) {
                                                    Text(
                                                        text = "S",
                                                        fontFamily = PlayfairDisplayFontFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = (11 + state.dropCapLines * 4).sp,
                                                        lineHeight = (12 + state.dropCapLines * 4).sp,
                                                        color = PressInkBlack,
                                                        modifier = Modifier.padding(end = 4.dp)
                                                    )
                                                    Text(
                                                        text = sampleParagraphs[0].drop(1),
                                                        fontFamily = PlusJakartaSansFontFamily,
                                                        fontSize = (state.bodySizePt * 0.78f).sp,
                                                        lineHeight = (state.leadingPt * 0.82f).sp,
                                                        letterSpacing = (state.tracking * 0.015f).sp,
                                                        color = PressInkBlack,
                                                        maxLines = 6,
                                                        overflow = TextOverflow.Clip
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = sampleParagraphs[colIndex % sampleParagraphs.size],
                                                    fontFamily = PlusJakartaSansFontFamily,
                                                    fontSize = (state.bodySizePt * 0.78f).sp,
                                                    lineHeight = (state.leadingPt * 0.82f).sp,
                                                    letterSpacing = (state.tracking * 0.015f).sp,
                                                    color = PressInkBlack,
                                                    overflow = TextOverflow.Clip
                                                )
                                            }
                                        }
                                    } else if (showGuides) {
                                        Text(
                                            text = "UNTHREADED FRAME\nClick Out-Port [+] to link story",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 7.5.sp,
                                            lineHeight = 10.sp,
                                            color = PressInkMuted.copy(alpha = 0.65f),
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }

                                    // In-Port / Out-Port or Red Overset Text [+] Indicator
                                    if (showGuides) {
                                        val isLastActiveCol = (!state.linkedFrames && colIndex == 0) ||
                                            (state.linkedFrames && colIndex == columnsCount - 1)
                                        if (isLastActiveCol && state.hasOversetText) {
                                            // Iconic Red Overset Text [+] square at bottom-right of text frame
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .background(BleedAlertRed, RoundedCornerShape(2.dp))
                                                    .border(1.dp, Color.White, RoundedCornerShape(2.dp))
                                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                                                    .testTag("overset_text_indicator")
                                            ) {
                                                Text(
                                                    text = "[+]",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 8.sp,
                                                    color = Color.White
                                                )
                                            }
                                        } else if (state.linkedFrames && colIndex < columnsCount - 1) {
                                            // Blue Thread Port [>] showing story linked to next column
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .background(ProcessCyanGuide, RoundedCornerShape(2.dp))
                                                    .padding(horizontal = 3.dp, vertical = 0.5.dp)
                                            ) {
                                                Text(
                                                    text = "→",
                                                    fontFamily = JetBrainsMonoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 8.sp,
                                                    color = ObsidianPasteboard
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Live Preflight & Measurement Status Bar (InDesign Preflight + Quark Measurements Bar)
        Surface(
            color = StudioDockSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val hasErrors = state.preflightIssues.isNotEmpty()
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (hasErrors) BleedAlertRed else PreflightPassGreen)
                    )
                    Text(
                        text = if (hasErrors) {
                            "${state.preflightIssues.size} PREFLIGHT ERROR(S)"
                        } else {
                            "0 ERRORS • PRESS READY"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (hasErrors) BleedAlertRed else PreflightPassGreen
                    )
                }

                Text(
                    text = "${state.columns}Col • G:${DtpCurriculum.formatPicas(state.gutterPicas)} • M:${DtpCurriculum.formatPicas(state.marginPicas)} • ${state.bodySizePt}/${state.leadingPt}pt • Bleed:${state.bleedMm}mm",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB4BED6),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
