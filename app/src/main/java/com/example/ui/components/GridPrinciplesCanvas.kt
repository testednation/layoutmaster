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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DtpCurriculum
import com.example.model.GridImagePlacement
import com.example.model.GridLessonState
import com.example.model.GridTextAlignment
import com.example.ui.theme.ArchivalPaperCream
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
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun GridPrinciplesCanvas(
    state: GridLessonState,
    modifier: Modifier = Modifier
) {
    val mTopDp = (state.marginPreset.topPicas * 4.2f).dp
    val mBottomDp = (state.marginPreset.bottomPicas * 4.2f).dp
    val mLeftDp = (state.marginPreset.leftPicas * 4.2f).dp
    val mRightDp = (state.marginPreset.rightPicas * 4.2f).dp
    val gutterDp = (state.gutterPicas * 5.2f).coerceIn(2.5f, 16f).dp

    val composeTextAlign = when (state.textAlignment) {
        GridTextAlignment.FLUSH_LEFT -> TextAlign.Start
        GridTextAlignment.JUSTIFIED -> TextAlign.Justify
        GridTextAlignment.CENTERED -> TextAlign.Center
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ObsidianPasteboard)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            .testTag("grid_principles_canvas")
    ) {
        // Top Pica Ruler Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDockSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.showGuides) {
                    "GUIDES ON • ${state.columns} COL • GUTTER ${DtpCurriculum.formatPicas(state.gutterPicas)}"
                } else {
                    "TRIM PREVIEW (W) • CLEAN READER VIEW"
                },
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (state.showGuides) ProcessCyanGuide else PreflightPassGreen
            )
            Text(
                text = "PAGE 51p0 × 66p0",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                color = TextSecondaryMuted
            )
        }

        // Pasteboard & Simulated Editorial Page
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(355.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ArchivalPaperCream)
            ) {
                // Non-printing Grid Guides Canvas (Magenta Margins, Shaded Cyan Gutters, Modular Row Flowlines)
                if (state.showGuides) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val topPx = mTopDp.toPx()
                        val bottomPx = mBottomDp.toPx()
                        val leftPx = mLeftDp.toPx()
                        val rightPx = mRightDp.toPx()
                        val contentW = (size.width - leftPx - rightPx).coerceAtLeast(16f)
                        val contentH = (size.height - topPx - bottomPx).coerceAtLeast(16f)

                        // 1. Shaded Cyan Gutter Bands + Column Boundary Lines
                        val cols = state.columns.coerceIn(1, 4)
                        val gapPx = gutterDp.toPx()
                        if (cols > 1) {
                            val totalGap = (cols - 1) * gapPx
                            val colW = (contentW - totalGap) / cols
                            for (i in 1 until cols) {
                                val gx = leftPx + i * colW + (i - 1) * gapPx
                                // Shaded gutter negative space band
                                drawRect(
                                    color = if (state.hasGutterJumpRisk) {
                                        BleedAlertRed.copy(alpha = 0.22f)
                                    } else {
                                        ProcessCyanGuide.copy(alpha = 0.14f)
                                    },
                                    topLeft = Offset(gx, topPx),
                                    size = Size(gapPx, contentH)
                                )
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.85f),
                                    start = Offset(gx, topPx),
                                    end = Offset(gx, topPx + contentH),
                                    strokeWidth = 1.dp.toPx()
                                )
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.85f),
                                    start = Offset(gx + gapPx, topPx),
                                    end = Offset(gx + gapPx, topPx + contentH),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        }

                        // 2. Modular Horizontal Row Flowlines (InDesign Layout > Create Guides)
                        if (state.modularRows > 1) {
                            val rowGap = 6.dp.toPx()
                            val rowH = (contentH - (state.modularRows - 1) * rowGap) / state.modularRows
                            val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                            for (r in 1 until state.modularRows) {
                                val ry = topPx + r * rowH + (r - 1) * rowGap
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.65f),
                                    start = Offset(leftPx, ry),
                                    end = Offset(leftPx + contentW, ry),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = dash
                                )
                                drawLine(
                                    color = ProcessCyanGuide.copy(alpha = 0.65f),
                                    start = Offset(leftPx, ry + rowGap),
                                    end = Offset(leftPx + contentW, ry + rowGap),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = dash
                                )
                            }
                        }

                        // 3. Magenta Margin Guide Frame
                        drawRect(
                            color = ProcessMagentaGuide.copy(alpha = 0.9f),
                            topLeft = Offset(leftPx, topPx),
                            size = Size(contentW, contentH),
                            style = Stroke(width = 1.3.dp.toPx())
                        )
                    }

                    // Margin Measurement Callout Pills on Outer Margins
                    Text(
                        text = "TOP ${DtpCurriculum.formatPicas(state.marginPreset.topPicas)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProcessMagentaGuide,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 1.dp)
                    )
                    Text(
                        text = "FOOT ${DtpCurriculum.formatPicas(state.marginPreset.bottomPicas)} • IN:${DtpCurriculum.formatPicas(state.marginPreset.leftPicas)} OUT:${DtpCurriculum.formatPicas(state.marginPreset.rightPicas)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProcessMagentaGuide,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 1.dp)
                    )
                }

                // Live Page Editorial Content inside Margins
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = mTopDp,
                            bottom = mBottomDp,
                            start = mLeftDp,
                            end = mRightDp
                        )
                ) {
                    val contentWidthDp = maxWidth
                    val cols = state.columns.coerceIn(1, 4)
                    val totalGapDp = gutterDp * (cols - 1)
                    val singleColDp = ((contentWidthDp - totalGapDp) / cols).coerceAtLeast(36.dp)

                    val effectiveImageCols = when (state.imagePlacement) {
                        GridImagePlacement.SPAN_ONE_COL -> 1
                        GridImagePlacement.SPAN_TWO_COLS -> 2.coerceAtMost(cols)
                        GridImagePlacement.FULL_MARGIN -> cols
                        GridImagePlacement.OFF_GRID_BROKEN -> cols
                    }

                    val imageWidthDp = when (state.imagePlacement) {
                        GridImagePlacement.OFF_GRID_BROKEN -> (contentWidthDp * 0.62f)
                        else -> (singleColDp * effectiveImageCols) + (gutterDp * (effectiveImageCols - 1))
                    }

                    val isAsymmetricSideBySide =
                        state.imagePlacement != GridImagePlacement.FULL_MARGIN &&
                            state.imagePlacement != GridImagePlacement.OFF_GRID_BROKEN &&
                            effectiveImageCols < cols

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Editorial Masthead / Headline
                        Text(
                            text = "GRID SYSTEMS IN PRINT",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (cols >= 4) 17.sp else 20.sp,
                            lineHeight = 22.sp,
                            color = PressInkBlack,
                            textAlign = composeTextAlign,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Image Placement Row (Demonstrates 1-Col, 2-Col Asymmetric + Caption, Full Margin, or Off-Grid)
                        if (isAsymmetricSideBySide) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(86.dp),
                                horizontalArrangement = Arrangement.spacedBy(gutterDp)
                            ) {
                                GridSnappedHeroImage(
                                    state = state,
                                    spanLabel = if (effectiveImageCols == 1) "1 COL SNAP ✓" else "$effectiveImageCols COL + GUTTER ✓",
                                    modifier = Modifier
                                        .width(imageWidthDp)
                                        .fillMaxHeight()
                                )

                                // Companion caption / pull-quote snapped into remaining column(s)
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .then(
                                            if (state.showGuides) {
                                                Modifier.border(0.7.dp, ProcessCyanGuide.copy(alpha = 0.6f))
                                            } else Modifier
                                        )
                                        .padding(4.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "FIG 01 • GRID SNAP",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProcessMagentaGuide
                                    )
                                    Text(
                                        text = "Remaining column holds a structured pull-quote or photo caption aligned to the exact same gutter.",
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = 7.5.sp,
                                        lineHeight = 10.sp,
                                        textAlign = composeTextAlign,
                                        color = PressInkMuted,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(86.dp)
                            ) {
                                GridSnappedHeroImage(
                                    state = state,
                                    spanLabel = if (state.imagePlacement.isSnappedToGrid) {
                                        "FULL MARGIN SPAN ✓"
                                    } else {
                                        "⚠ OFF-GRID MISALIGNED"
                                    },
                                    modifier = Modifier
                                        .width(imageWidthDp)
                                        .fillMaxHeight()
                                        .then(
                                            if (!state.imagePlacement.isSnappedToGrid) {
                                                Modifier.offset(x = 22.dp)
                                            } else {
                                                Modifier
                                            }
                                        )
                                )
                            }
                        }

                        // Multi-Column Body Copy showing Live Text Alignment & Gutter Width Interaction
                        val paragraphs = listOf(
                            "A proportional typographic grid brings mathematical clarity to editorial spreads. Margins frame the composition while gutters establish crisp breathing room between vertical columns.",
                            "When column measures narrow in 3-column or 4-column magazine layouts, Flush Left alignment maintains even word spacing and prevents distracting vertical rivers.",
                            "Justified text locks both left and right edges tightly to column guides, making it ideal for wide book spreads with careful InDesign hyphenation rules.",
                            "Snapping images across one, two, or all columns anchors photography to the architecture of the page rather than floating arbitrarily."
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(gutterDp)
                        ) {
                            for (c in 0 until cols) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .then(
                                            if (state.showGuides) {
                                                Modifier.border(0.6.dp, ProcessCyanGuide.copy(alpha = 0.45f))
                                            } else Modifier
                                        )
                                        .padding(horizontal = 2.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = paragraphs[c % paragraphs.size],
                                        fontFamily = PlusJakartaSansFontFamily,
                                        fontSize = when (cols) {
                                            1 -> 10.sp
                                            2 -> 9.sp
                                            3 -> 8.2.sp
                                            else -> 7.4.sp
                                        },
                                        lineHeight = when (cols) {
                                            1 -> 13.5.sp
                                            2 -> 12.sp
                                            3 -> 11.sp
                                            else -> 10.sp
                                        },
                                        letterSpacing = if (state.hasWhitespaceRiverRisk) 0.6.sp else 0.sp,
                                        textAlign = composeTextAlign,
                                        color = PressInkBlack,
                                        overflow = TextOverflow.Clip
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Real-Time Grid Telemetry & Diagnostic Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDockSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COL MEASURE: ${DtpCurriculum.formatPicas(state.singleColumnWidthPicas)} (~${state.estimatedCpl} CPL)",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProcessCyanGuide
                )
                Text(
                    text = "ALIGN: ${state.textAlignment.label.uppercase()}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp,
                    color = ProcessYellowGold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (state.isOptimalGrid) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = if (state.isOptimalGrid) PreflightPassGreen else BleedAlertRed,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = state.gridFeedbackMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.isOptimalGrid) PreflightPassGreen else Color(0xFFFFB4B4)
                )
            }
        }
    }
}

@Composable
private fun GridSnappedHeroImage(
    state: GridLessonState,
    spanLabel: String,
    modifier: Modifier = Modifier
) {
    val snapped = state.imagePlacement.isSnappedToGrid
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .border(
                width = if (state.showGuides) 1.5.dp else 0.dp,
                color = if (!state.showGuides) Color.Transparent else if (snapped) PreflightPassGreen else BleedAlertRed,
                shape = RoundedCornerShape(3.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_editorial_hero_1795037988082),
            contentDescription = "Grid-aligned editorial photograph",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (state.showGuides) {
            Surface(
                color = if (snapped) ObsidianPasteboard.copy(alpha = 0.86f) else BleedAlertRed.copy(alpha = 0.92f),
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Text(
                    text = spanLabel,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (snapped) PreflightPassGreen else Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}
