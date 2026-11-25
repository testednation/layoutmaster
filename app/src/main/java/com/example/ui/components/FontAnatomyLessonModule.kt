package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DtpCurriculum
import com.example.model.EditorialFontChoice
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BaselineGridBlue
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.InDesignMagenta
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.PressInkBlack
import com.example.ui.theme.PressInkMuted
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.QuarkClassicTeal
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

data class AnatomyFeature(
    val id: Int,
    val title: String,
    val metricLineLabel: String,
    val color: Color,
    val targetLetters: String,
    val description: String,
    val dtpTip: String
)

private val anatomyFeatures = listOf(
    AnatomyFeature(
        id = 0,
        title = "1. Baseline",
        metricLineLabel = "BASELINE (0pt Origin)",
        color = ProcessCyanGuide,
        targetLetters = "S p h i n x",
        description = "The invisible horizontal shelf upon which the bodies of most uppercase and lowercase letters rest.",
        dtpTip = "In InDesign & QuarkXPress, 'Lock to Baseline Grid' snaps every paragraph's baseline to a fixed vertical rhythm across columns."
    ),
    AnatomyFeature(
        id = 1,
        title = "2. x-Height (Mean Line)",
        metricLineLabel = "x-HEIGHT / MEAN LINE",
        color = ProcessMagentaGuide,
        targetLetters = "p n x",
        description = "The height of lowercase letters without ascenders or descenders, measured from the baseline to the top of the lowercase 'x'.",
        dtpTip = "Typefaces with a tall x-Height (like Plus Jakarta Sans or Helvetica) appear larger and remain legible at small 8pt–9pt caption sizes."
    ),
    AnatomyFeature(
        id = 2,
        title = "3. Cap Height & Ascender",
        metricLineLabel = "CAP HEIGHT & ASCENDER",
        color = ProcessYellowGold,
        targetLetters = "S h i",
        description = "Cap Height marks the top of flat capital letters ('S', 'H', 'T'), while Ascenders ('h', 'b', 'd', 'l') often rise slightly above Cap Height.",
        dtpTip = "When aligning a Drop Cap or photo frame with the top of a headline, set InDesign's First Baseline Offset to 'Cap Height' instead of 'Ascent'."
    ),
    AnatomyFeature(
        id = 3,
        title = "4. Descender & Beard",
        metricLineLabel = "DESCENDER LINE",
        color = BleedAlertRed,
        targetLetters = "p y g q",
        description = "The portion of a lowercase letter ('p', 'y', 'g', 'q') that plunges below the baseline.",
        dtpTip = "If your Leading is set too tight (e.g., 14pt type on 12pt leading), the descenders of line 1 will crash into the ascenders of line 2!"
    ),
    AnatomyFeature(
        id = 4,
        title = "5. Counter, Aperture & Serif",
        metricLineLabel = "COUNTERS & TERMINALS",
        color = PreflightPassGreen,
        targetLetters = "S p n",
        description = "The enclosed or partially open negative space inside letters ('p', 'e', 'S') is the Counter/Aperture; Serifs are the finishing feet at stroke ends.",
        dtpTip = "High-contrast serifs like Playfair Display or Bodoni have thin hairlines—avoid printing them reversed out (white on rich black) below 10pt."
    )
)

@Composable
fun FontAnatomyLessonModule(
    modifier: Modifier = Modifier
) {
    // 0 = Font Anatomy Inspector, 1 = Real-Time Kerning & Tracking Sliders, 2 = Real-Time Leading & Measure Sliders
    var activeLessonStep by remember { mutableIntStateOf(0) }

    // Lesson Step 1: Font Anatomy State
    var anatomyFont by remember { mutableStateOf(EditorialFontChoice.PLAYFAIR) }
    var anatomySizePt by remember { mutableFloatStateOf(46f) }
    var anatomyHighlightIndex by remember { mutableFloatStateOf(0f) }
    var showAllMetricLines by remember { mutableStateOf(true) }
    var sampleSpecimenWord by remember { mutableStateOf("Sphinx pyx") }

    // Lesson Step 2: Real-Time Kerning & Tracking Sliders State
    var pairKernAV by remember { mutableFloatStateOf(-75f) }      // -150 to +150 (1/1000 em)
    var pairKernTo by remember { mutableFloatStateOf(-65f) }      // -150 to +150 (1/1000 em)
    var headlineTracking by remember { mutableFloatStateOf(20f) } // -80 to +240 (1/1000 em)
    var kickerTracking by remember { mutableFloatStateOf(120f) }  // -50 to +300 (1/1000 em)
    var kerningSampleFont by remember { mutableStateOf(EditorialFontChoice.PLAYFAIR) }

    // Lesson Step 3: Real-Time Leading & Column Measure Sliders State
    var bodyTypeSizePt by remember { mutableFloatStateOf(13f) }   // 9pt to 22pt
    var bodyLeadingPt by remember { mutableFloatStateOf(17f) }    // 9pt to 34pt
    var paragraphSpaceAfterPt by remember { mutableFloatStateOf(6f) } // 0pt to 18pt
    var columnWidthPercent by remember { mutableFloatStateOf(0.92f) } // 0.45f to 1.0f (Simulates pica column measure)
    var showBaselineGridLines by remember { mutableStateOf(true) }

    val anatomyMet = anatomySizePt >= 36f
    val kerningMet = pairKernAV <= -40f && pairKernTo <= -35f && kickerTracking >= 60f
    val leadingRatio = (bodyLeadingPt / bodyTypeSizePt) * 100f
    val leadingMet = leadingRatio in 118f..150f && paragraphSpaceAfterPt >= 4f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, ProcessCyanGuide.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .testTag("font_anatomy_lesson_module")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Lesson Header & Interactive Progress Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = ProcessCyanGuide.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Interactive Typography Lesson",
                            tint = ProcessCyanGuide,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "INTERACTIVE DTP LESSON MODULE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessCyanGuide,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Font Anatomy, Kerning & Leading",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }

                val completedChecks = listOf(anatomyMet, kerningMet, leadingMet).count { it }
                Surface(
                    color = if (completedChecks == 3) {
                        PreflightPassGreen.copy(alpha = 0.2f)
                    } else {
                        ProcessYellowGold.copy(alpha = 0.18f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$completedChecks/3 Labs Tuned",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (completedChecks == 3) PreflightPassGreen else ProcessYellowGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 3-Part Lesson Module Switcher Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Triple(0, "1. Font Anatomy", anatomyMet),
                    Triple(1, "2. Kerning & Tracking", kerningMet),
                    Triple(2, "3. Leading & Measure", leadingMet)
                )
                tabs.forEach { (idx, label, isDone) ->
                    FilterChip(
                        selected = activeLessonStep == idx,
                        onClick = { activeLessonStep = idx },
                        leadingIcon = if (isDone) {
                            {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PreflightPassGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        } else null,
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.25f),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("lesson_step_tab_$idx")
                    )
                }
            }

            // Active Lesson Module Body
            when (activeLessonStep) {
                0 -> FontAnatomyInteractiveLab(
                    selectedFont = anatomyFont,
                    onSelectFont = { anatomyFont = it },
                    specimenWord = sampleSpecimenWord,
                    onSelectSpecimenWord = { sampleSpecimenWord = it },
                    sizePt = anatomySizePt,
                    onSizeChange = { anatomySizePt = it },
                    highlightIndex = anatomyHighlightIndex,
                    onHighlightIndexChange = { anatomyHighlightIndex = it },
                    showAllLines = showAllMetricLines,
                    onToggleShowAllLines = { showAllMetricLines = it }
                )

                1 -> KerningAndTrackingRealTimeLab(
                    selectedFont = kerningSampleFont,
                    onSelectFont = { kerningSampleFont = it },
                    pairKernAV = pairKernAV,
                    onPairKernAVChange = { pairKernAV = it },
                    pairKernTo = pairKernTo,
                    onPairKernToChange = { pairKernTo = it },
                    headlineTracking = headlineTracking,
                    onHeadlineTrackingChange = { headlineTracking = it },
                    kickerTracking = kickerTracking,
                    onKickerTrackingChange = { kickerTracking = it },
                    onApplyPreset = { av, to, trk, kicker ->
                        pairKernAV = av
                        pairKernTo = to
                        headlineTracking = trk
                        kickerTracking = kicker
                    }
                )

                2 -> LeadingAndMeasureRealTimeLab(
                    fontSizePt = bodyTypeSizePt,
                    onFontSizeChange = { bodyTypeSizePt = it },
                    leadingPt = bodyLeadingPt,
                    onLeadingChange = { bodyLeadingPt = it },
                    spaceAfterPt = paragraphSpaceAfterPt,
                    onSpaceAfterChange = { paragraphSpaceAfterPt = it },
                    columnWidthFraction = columnWidthPercent,
                    onColumnWidthChange = { columnWidthPercent = it },
                    showBaselineGrid = showBaselineGridLines,
                    onToggleBaselineGrid = { showBaselineGridLines = it },
                    onApplyPreset = { size, lead, space, width ->
                        bodyTypeSizePt = size
                        bodyLeadingPt = lead
                        paragraphSpaceAfterPt = space
                        columnWidthPercent = width
                    }
                )
            }
        }
    }
}

@Composable
private fun FontAnatomyInteractiveLab(
    selectedFont: EditorialFontChoice,
    onSelectFont: (EditorialFontChoice) -> Unit,
    specimenWord: String,
    onSelectSpecimenWord: (String) -> Unit,
    sizePt: Float,
    onSizeChange: (Float) -> Unit,
    highlightIndex: Float,
    onHighlightIndexChange: (Float) -> Unit,
    showAllLines: Boolean,
    onToggleShowAllLines: (Boolean) -> Unit
) {
    val activeFeatureIndex = highlightIndex.toInt().coerceIn(0, anatomyFeatures.lastIndex)
    val activeFeature = anatomyFeatures[activeFeatureIndex]

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Use the sliders below to scale the live type specimen and scrub through the 5 core anatomical landmarks of desktop publishing typography.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD5DCF0)
        )

        // Specimen Word & Typeface Switcher Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditorialFontChoice.entries.forEach { font ->
                FilterChip(
                    selected = selectedFont == font,
                    onClick = { onSelectFont(font) },
                    label = { Text(font.label, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Sphinx pyx", "Typography", "Quark & Id", "Avant-Garde").forEach { word ->
                FilterChip(
                    selected = specimenWord == word,
                    onClick = { onSelectSpecimenWord(word) },
                    label = { Text("\"$word\"", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Live Archival Paper Specimen Canvas with Anatomical Metric Lines
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(ArchivalPaperCream)
                .border(1.5.dp, activeFeature.color, RoundedCornerShape(14.dp))
                .testTag("anatomy_live_specimen_canvas"),
            contentAlignment = Alignment.Center
        ) {
            // Draw Baseline, x-Height, Cap Height, Ascender, and Descender lines proportional to sizePt
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height * 0.54f
                val pxPerPt = (sizePt / 46f) * 1.15.dp.toPx()

                val baselineY = centerY + (14f * pxPerPt)
                val xHeightY = baselineY - (20f * pxPerPt)
                val capHeightY = baselineY - (32f * pxPerPt)
                val ascenderY = baselineY - (36f * pxPerPt)
                val descenderY = baselineY + (11f * pxPerPt)

                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                fun drawMetricLine(y: Float, color: Color, isHighlighted: Boolean, dashed: Boolean) {
                    if (!showAllLines && !isHighlighted) return
                    drawLine(
                        color = if (isHighlighted) color else color.copy(alpha = 0.42f),
                        start = Offset(12.dp.toPx(), y),
                        end = Offset(size.width - 12.dp.toPx(), y),
                        strokeWidth = if (isHighlighted) 2.5.dp.toPx() else 1.2.dp.toPx(),
                        pathEffect = if (dashed && !isHighlighted) dashEffect else null
                    )
                }

                // 0: Baseline
                drawMetricLine(baselineY, ProcessCyanGuide, activeFeatureIndex == 0, dashed = false)
                // 1: x-Height (Mean line)
                drawMetricLine(xHeightY, ProcessMagentaGuide, activeFeatureIndex == 1, dashed = true)
                // 2: Cap Height & Ascender
                drawMetricLine(capHeightY, ProcessYellowGold, activeFeatureIndex == 2, dashed = true)
                drawMetricLine(ascenderY, ProcessYellowGold, activeFeatureIndex == 2, dashed = false)
                // 3: Descender line
                drawMetricLine(descenderY, BleedAlertRed, activeFeatureIndex == 3, dashed = true)

                // 4: Counter & Serif spotlight circles when feature 4 is active
                if (activeFeatureIndex == 4) {
                    drawCircle(
                        color = PreflightPassGreen.copy(alpha = 0.25f),
                        radius = (18f * pxPerPt).coerceAtLeast(18f),
                        center = Offset(size.width * 0.38f, (baselineY + xHeightY) / 2f)
                    )
                    drawCircle(
                        color = PreflightPassGreen,
                        radius = (18f * pxPerPt).coerceAtLeast(18f),
                        center = Offset(size.width * 0.38f, (baselineY + xHeightY) / 2f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Live Rendered Specimen Text
            Text(
                text = specimenWord,
                fontFamily = selectedFont.toComposeFontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = sizePt.sp,
                color = PressInkBlack
            )

            // Top-Left Metric Legend Badge
            Surface(
                color = ObsidianPasteboard.copy(alpha = 0.9f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = "${activeFeature.metricLineLabel} • ${sizePt.toInt()} PT",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeFeature.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Bottom-Right Focus Glyphs Badge
            Surface(
                color = ObsidianPasteboard.copy(alpha = 0.88f),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Watch glyphs: ${activeFeature.targetLetters}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Slider 1: Anatomical Landmark Scrubber Slider (1 to 5)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anatomy Landmark Slider:",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Text(
                    text = activeFeature.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = activeFeature.color,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = highlightIndex,
                onValueChange = { onHighlightIndexChange(kotlin.math.round(it)) },
                valueRange = 0f..4f,
                steps = 3,
                colors = SliderDefaults.colors(
                    thumbColor = activeFeature.color,
                    activeTrackColor = activeFeature.color
                ),
                modifier = Modifier.testTag("anatomy_landmark_slider")
            )

            Text(
                text = activeFeature.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ProcessYellowGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = activeFeature.dtpTip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ProcessYellowGold
                )
            }
        }

        // Slider 2: Real-Time Point Size & Optical Scale Slider (26pt to 60pt)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StudioDockSurface)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = ProcessCyanGuide,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Live Specimen Point Size",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
                Text(
                    text = "${sizePt.toInt()} pt (${DtpCurriculum.formatPicas(sizePt / 12f)})",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessCyanGuide
                )
            }
            Slider(
                value = sizePt,
                onValueChange = onSizeChange,
                valueRange = 26f..60f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessCyanGuide,
                    activeTrackColor = ProcessCyanGuide
                ),
                modifier = Modifier.testTag("anatomy_point_size_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show All 5 Metric Guide Rules Simultaneously",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
                Switch(
                    checked = showAllLines,
                    onCheckedChange = onToggleShowAllLines,
                    colors = SwitchDefaults.colors(checkedThumbColor = ProcessCyanGuide)
                )
            }
        }
    }
}

@Composable
private fun KerningAndTrackingRealTimeLab(
    selectedFont: EditorialFontChoice,
    onSelectFont: (EditorialFontChoice) -> Unit,
    pairKernAV: Float,
    onPairKernAVChange: (Float) -> Unit,
    pairKernTo: Float,
    onPairKernToChange: (Float) -> Unit,
    headlineTracking: Float,
    onHeadlineTrackingChange: (Float) -> Unit,
    kickerTracking: Float,
    onKickerTrackingChange: (Float) -> Unit,
    onApplyPreset: (av: Float, to: Float, trk: Float, kicker: Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Kerning adjusts optical space between two specific letters (like 'To' or 'AV'), while Tracking uniformly adjusts spacing across an entire word or line.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD5DCF0)
        )

        // Quick Preset Buttons: Unkerned Raw vs Optical Balanced vs Loose Display
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = { onApplyPreset(60f, 70f, 0f, 0f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("1. Unkerned Gaps (Bad)", style = MaterialTheme.typography.labelMedium)
            }
            FilledTonalButton(
                onClick = { onApplyPreset(-75f, -65f, -10f, 140f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = PreflightPassGreen.copy(alpha = 0.2f),
                    contentColor = PreflightPassGreen
                )
            ) {
                Text("2. Optical Master (Ideal)", style = MaterialTheme.typography.labelMedium)
            }
            FilledTonalButton(
                onClick = { onApplyPreset(0f, 0f, 160f, 260f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("3. Over-Tracked (+160)", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Real-Time Sample Text Preview on Archival Paper
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ArchivalPaperCream)
                .border(1.dp, ProcessCyanGuide, RoundedCornerShape(14.dp))
                .padding(14.dp)
                .testTag("kerning_realtime_sample_box")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sample Line 1: All-Caps Kicker affected by Kicker Tracking Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SWISS EDITORIAL ARCHIVE",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = (kickerTracking * 0.025f).sp,
                        color = ProcessMagentaGuide
                    )
                    Text(
                        text = "TRK: ${if (kickerTracking >= 0) "+" else ""}${kickerTracking.toInt()}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp,
                        color = PressInkMuted
                    )
                }

                // Sample Line 2: Live Display Headline demonstrating Pair Kerning on "To" and "AV" + Global Tracking
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val trackSp = (headlineTracking * 0.02f).sp
                    // "T" + [pairKernTo gap] + "o "
                    Text(
                        text = "T",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = InDesignMagenta
                    )
                    val toGapDp = ((pairKernTo + 85f) * 0.09f).coerceIn(0f, 22f).dp
                    Spacer(modifier = Modifier.width(toGapDp))
                    Text(
                        text = "o ",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = trackSp,
                        color = InDesignMagenta
                    )

                    // "A" + [pairKernAV gap] + "V" + "ANT-GARDE"
                    Text(
                        text = "A",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = QuarkClassicTeal
                    )
                    val avGapDp = ((pairKernAV + 85f) * 0.09f).coerceIn(0f, 22f).dp
                    Spacer(modifier = Modifier.width(avGapDp))
                    Text(
                        text = "V",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = QuarkClassicTeal
                    )
                    Text(
                        text = "ANT",
                        fontFamily = selectedFont.toComposeFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = trackSp,
                        color = PressInkBlack
                    )
                }

                // Live Visual Gap Diagnostic Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ObsidianPasteboard.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pair 'T|o': ${pairKernTo.toInt()} em",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        color = if (pairKernTo in -95f..-35f) PreflightPassGreen else ProcessYellowGold
                    )
                    Text(
                        text = "Pair 'A|V': ${pairKernAV.toInt()} em",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        color = if (pairKernAV in -105f..-40f) PreflightPassGreen else ProcessYellowGold
                    )
                    Text(
                        text = "Track: ${headlineTracking.toInt()} em",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        color = ProcessCyanGuide
                    )
                }
            }
        }

        // Interactive Slider 1: Pair Kerning 'A|V' Diagonal Wedge
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1. Pair Kerning 'A ↔ V' (Diagonal Wedge)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${pairKernAV.toInt()} / 1000 em",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (pairKernAV <= -40f) PreflightPassGreen else ProcessYellowGold
                )
            }
            Slider(
                value = pairKernAV,
                onValueChange = onPairKernAVChange,
                valueRange = -120f..120f,
                colors = SliderDefaults.colors(
                    thumbColor = QuarkClassicTeal,
                    activeTrackColor = QuarkClassicTeal
                ),
                modifier = Modifier.testTag("slider_pair_kern_av")
            )

            // Interactive Slider 2: Pair Kerning 'T|o' Overhang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2. Pair Kerning 'T ↔ o' (Crossbar Overhang)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${pairKernTo.toInt()} / 1000 em",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (pairKernTo <= -35f) PreflightPassGreen else ProcessYellowGold
                )
            }
            Slider(
                value = pairKernTo,
                onValueChange = onPairKernToChange,
                valueRange = -120f..120f,
                colors = SliderDefaults.colors(
                    thumbColor = InDesignMagenta,
                    activeTrackColor = InDesignMagenta
                ),
                modifier = Modifier.testTag("slider_pair_kern_to")
            )

            // Interactive Slider 3: Headline Range Tracking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "3. Headline Range Tracking (All Characters)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${if (headlineTracking >= 0) "+" else ""}${headlineTracking.toInt()} / 1000 em",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessCyanGuide
                )
            }
            Slider(
                value = headlineTracking,
                onValueChange = onHeadlineTrackingChange,
                valueRange = -80f..220f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessCyanGuide,
                    activeTrackColor = ProcessCyanGuide
                ),
                modifier = Modifier.testTag("slider_headline_tracking")
            )

            // Interactive Slider 4: All-Caps Kicker Tracking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "4. ALL-CAPS Kicker Tracking (+60 to +180 Ideal)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "+${kickerTracking.toInt()} / 1000 em",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessMagentaGuide
                )
            }
            Slider(
                value = kickerTracking,
                onValueChange = onKickerTrackingChange,
                valueRange = 0f..280f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessMagentaGuide,
                    activeTrackColor = ProcessMagentaGuide
                ),
                modifier = Modifier.testTag("slider_kicker_tracking")
            )
        }
    }
}

@Composable
private fun LeadingAndMeasureRealTimeLab(
    fontSizePt: Float,
    onFontSizeChange: (Float) -> Unit,
    leadingPt: Float,
    onLeadingChange: (Float) -> Unit,
    spaceAfterPt: Float,
    onSpaceAfterChange: (Float) -> Unit,
    columnWidthFraction: Float,
    onColumnWidthChange: (Float) -> Unit,
    showBaselineGrid: Boolean,
    onToggleBaselineGrid: (Boolean) -> Unit,
    onApplyPreset: (size: Float, lead: Float, space: Float, width: Float) -> Unit
) {
    val sizeInt = fontSizePt.toInt()
    val leadInt = leadingPt.toInt()
    val ratioPercent = ((leadingPt / fontSizePt) * 100f).toInt()
    val simulatedPicas = (columnWidthFraction * 28f).coerceIn(12f, 28f)
    val estimatedCpl = ((simulatedPicas * 12f) / (fontSizePt * 0.52f)).toInt()

    val statusColor = when {
        leadingPt < fontSizePt -> BleedAlertRed
        ratioPercent in 118..148 -> PreflightPassGreen
        else -> ProcessYellowGold
    }

    val statusSummary = when {
        leadingPt < fontSizePt -> "NEGATIVE LEADING ($sizeInt/${leadInt}pt): Descenders crash into ascenders!"
        leadingPt == fontSizePt -> "SET SOLID ($sizeInt/${leadInt}pt • 100%): No breathing room between lines."
        ratioPercent in 118..148 -> "OPTIMAL EDITORIAL LEADING ($sizeInt/${leadInt}pt • $ratioPercent%): Balanced reading rhythm."
        else -> "LOOSE LEADING ($sizeInt/${leadInt}pt • $ratioPercent%): Airy look suited for wide columns or pull-quotes."
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Adjust Point Size, Leading (baseline-to-baseline distance), Paragraph Space After, and Column Measure in real time on the editorial sample below.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD5DCF0)
        )

        // Quick Preset Chips for Leading Scenarios
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = { onApplyPreset(14f, 11f, 0f, 1.0f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("Crashing (14/11pt)", style = MaterialTheme.typography.labelMedium)
            }
            FilledTonalButton(
                onClick = { onApplyPreset(12f, 12f, 0f, 0.95f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("Set Solid (12/12pt)", style = MaterialTheme.typography.labelMedium)
            }
            FilledTonalButton(
                onClick = { onApplyPreset(12f, 16f, 7f, 0.88f) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = PreflightPassGreen.copy(alpha = 0.2f),
                    contentColor = PreflightPassGreen
                )
            ) {
                Text("Swiss Editorial (12/16pt)", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Live Multi-Paragraph Sample Text Canvas with Real-Time Baseline Grid Overlay & Column Width
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ObsidianPasteboard)
                .border(1.dp, statusColor, RoundedCornerShape(14.dp))
                .padding(12.dp)
                .testTag("leading_realtime_sample_box"),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Live Spec Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SPEC: $sizeInt/${leadInt}pt ($ratioPercent%) • Measure: ${DtpCurriculum.formatPicas(simulatedPicas)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                    Text(
                        text = "~$estimatedCpl chars/line",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 9.5.sp,
                        color = if (estimatedCpl in 42..75) PreflightPassGreen else ProcessYellowGold
                    )
                }

                // Resizable Column Frame on Archival Paper
                Box(
                    modifier = Modifier
                        .fillMaxWidth(columnWidthFraction.coerceIn(0.45f, 1.0f))
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArchivalPaperCream)
                        .border(1.dp, ProcessCyanGuide, RoundedCornerShape(8.dp))
                ) {
                    if (showBaselineGrid) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            val stepPx = leadingPt.dp.toPx().coerceAtLeast(10f)
                            var y = stepPx
                            while (y < size.height) {
                                drawLine(
                                    color = BaselineGridBlue.copy(alpha = 0.38f),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                                y += stepPx
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = PressInkBlack
                                    )
                                ) {
                                    append("Paragraph 1 ($sizeInt/${leadInt}pt): ")
                                }
                                append(
                                    "In metal typesetting, compositors inserted thin strips of lead between lines of Linotype slugs. Moving the Leading slider increases baseline-to-baseline breathing room."
                                )
                            },
                            fontFamily = EditorialFontChoice.PLUS_JAKARTA.toComposeFontFamily(),
                            fontSize = fontSizePt.sp,
                            lineHeight = leadingPt.sp,
                            color = PressInkBlack
                        )

                        Spacer(modifier = Modifier.height(spaceAfterPt.dp))

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = InDesignMagenta
                                    )
                                ) {
                                    append("Paragraph 2 (+${spaceAfterPt.toInt()}pt Space After): ")
                                }
                                append(
                                    "Never press Return twice between paragraphs in InDesign or QuarkXPress! Use Paragraph Space After so column breaks never leave awkward blank lines at the top of a frame."
                                )
                            },
                            fontFamily = EditorialFontChoice.PLUS_JAKARTA.toComposeFontFamily(),
                            fontSize = fontSizePt.sp,
                            lineHeight = leadingPt.sp,
                            color = PressInkBlack
                        )
                    }
                }

                Text(
                    text = statusSummary,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
            }
        }

        // 4 Interactive Sliders for Leading, Point Size, Paragraph Space After, and Column Width
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(StudioDockSurface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Slider 1: Type Size (9pt to 22pt)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1. Body Font Size", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("${sizeInt} pt", style = MaterialTheme.typography.labelLarge, color = ProcessYellowGold)
            }
            Slider(
                value = fontSizePt,
                onValueChange = onFontSizeChange,
                valueRange = 9f..22f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessYellowGold,
                    activeTrackColor = ProcessYellowGold
                ),
                modifier = Modifier.testTag("lesson_font_size_slider")
            )

            // Slider 2: Leading (9pt to 34pt)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("2. Leading (Line Spacing)", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("${leadInt} pt ($ratioPercent%)", style = MaterialTheme.typography.labelLarge, color = statusColor)
            }
            Slider(
                value = leadingPt,
                onValueChange = onLeadingChange,
                valueRange = 9f..34f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessCyanGuide,
                    activeTrackColor = ProcessCyanGuide
                ),
                modifier = Modifier.testTag("lesson_leading_slider")
            )

            // Slider 3: Paragraph Space After (0pt to 18pt)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("3. Paragraph Space After", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("+${spaceAfterPt.toInt()} pt", style = MaterialTheme.typography.labelLarge, color = ProcessMagentaGuide)
            }
            Slider(
                value = spaceAfterPt,
                onValueChange = onSpaceAfterChange,
                valueRange = 0f..18f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessMagentaGuide,
                    activeTrackColor = ProcessMagentaGuide
                ),
                modifier = Modifier.testTag("lesson_space_after_slider")
            )

            // Slider 4: Column Measure Width (Picas)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("4. Column Measure (Line Width)", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(
                    text = "${DtpCurriculum.formatPicas(simulatedPicas)} (~$estimatedCpl CPL)",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessCyanGuide
                )
            }
            Slider(
                value = columnWidthFraction,
                onValueChange = onColumnWidthChange,
                valueRange = 0.50f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessCyanGuide,
                    activeTrackColor = ProcessCyanGuide
                ),
                modifier = Modifier.testTag("lesson_column_measure_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show Cyan Baseline Grid Rules",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
                Switch(
                    checked = showBaselineGrid,
                    onCheckedChange = onToggleBaselineGrid,
                    colors = SwitchDefaults.colors(checkedThumbColor = ProcessCyanGuide)
                )
            }
        }
    }
}
