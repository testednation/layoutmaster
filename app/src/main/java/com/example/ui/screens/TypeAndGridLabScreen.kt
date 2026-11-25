package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KerningRecordEntity
import com.example.model.DtpCurriculum
import com.example.ui.components.FontAnatomyLessonModule
import com.example.ui.components.toComposeFontFamily
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
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun TypeAndGridLabScreen(
    activeKerningIndex: Int,
    userKerningGaps: List<Int>,
    kerningRevealed: Boolean,
    kerningRecords: List<KerningRecordEntity>,
    onSelectKerningWord: (Int) -> Unit,
    onAdjustKerningGap: (pairIndex: Int, delta: Int) -> Unit,
    onSetKerningGap: (pairIndex: Int, value: Int) -> Unit,
    onEvaluateKerning: () -> Int,
    onResetKerningWord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val challenges = DtpCurriculum.kerningChallenges
    val currentChallenge = challenges.getOrElse(activeKerningIndex) { challenges.first() }
    val recordMap = remember(kerningRecords) { kerningRecords.associateBy { it.wordId } }
    var selectedPairIndex by remember(activeKerningIndex) { mutableIntStateOf(0) }
    var lastEvaluatedScore by remember(activeKerningIndex) { mutableStateOf<Int?>(null) }

    // Interactive Leading Visualizer state
    var demoFontSizePt by remember { mutableIntStateOf(12) }
    var demoLeadingPt by remember { mutableIntStateOf(15) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("type_and_grid_lab_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Font Anatomy, Kerning & Leading",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = "Interactive real-time sliders for type anatomy landmarks, pair kerning, tracking, and baseline leading",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
            }
        }

        // Interactive 3-Part Lesson Module (Font Anatomy + Real-Time Kerning + Real-Time Leading Sliders)
        item {
            FontAnatomyLessonModule()
        }

        // Section Divider for Hands-On Optical Kerning Challenge Game
        item {
            Column(modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = "Optical Pair Kerning Challenge",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = "Put the lesson into practice: balance the negative space between each letter pair and test your eye",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
            }
        }

        // Word Selector Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                challenges.forEachIndexed { index, challenge ->
                    val best = recordMap[challenge.id]?.bestScore
                    FilterChip(
                        selected = activeKerningIndex == index,
                        onClick = { onSelectKerningWord(index) },
                        label = {
                            Text(
                                text = if (best != null) "${challenge.word} ($best%)" else challenge.word,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                            selectedLabelColor = ProcessCyanGuide
                        ),
                        modifier = Modifier.testTag("kerning_word_chip_${challenge.id}")
                    )
                }
            }
        }

        // Interactive Optical Kerning Canvas Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WORD: ${currentChallenge.word}",
                                style = MaterialTheme.typography.titleMedium,
                                color = ProcessCyanGuide
                            )
                            Text(
                                text = "${currentChallenge.fontChoice.label} (${currentChallenge.fontChoice.category})",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                        }
                        Surface(
                            color = ProcessMagentaGuide.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Alt/Opt + ←/→",
                                style = MaterialTheme.typography.labelSmall,
                                color = ProcessMagentaGuide,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Archival Paper Display Box rendering each letter with dynamic kerning gaps
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ArchivalPaperCream)
                            .border(1.dp, ProcessCyanGuide.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Top & Bottom Cap-Height / Baseline cyan guide lines
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(ProcessMagentaGuide.copy(alpha = 0.35f))
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                val chars = currentChallenge.word.toCharArray()
                                chars.forEachIndexed { idx, ch ->
                                    Text(
                                        text = ch.toString(),
                                        fontFamily = currentChallenge.fontChoice.toComposeFontFamily(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 48.sp,
                                        color = PressInkBlack
                                    )
                                    if (idx < chars.size - 1) {
                                        val userGap = userKerningGaps.getOrElse(idx) { 0 }
                                        val isPairSelected = selectedPairIndex == idx
                                        // Visual gap + hairline marker
                                        val widthDp = (10 + userGap).coerceIn(0, 36).dp
                                        Box(
                                            modifier = Modifier
                                                .width(widthDp)
                                                .height(54.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(if (isPairSelected) 2.dp else 1.dp)
                                                    .height(48.dp)
                                                    .background(
                                                        if (isPairSelected) {
                                                            ProcessCyanGuide
                                                        } else {
                                                            ProcessCyanGuide.copy(alpha = 0.25f)
                                                        }
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(ProcessCyanGuide.copy(alpha = 0.45f))
                            )
                        }

                        Text(
                            text = "CAP HEIGHT & BASELINE RULER",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 8.sp,
                            color = PressInkMuted,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentChallenge.contextNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD5DCF0)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Letter Pair Selector Pills (e.g., A|V, V|A, A|N, N|T)
                    Text(
                        text = "Select Letter Pair to Kern:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (pairIdx in 0 until currentChallenge.word.length - 1) {
                            val leftChar = currentChallenge.word[pairIdx]
                            val rightChar = currentChallenge.word[pairIdx + 1]
                            val currentVal = userKerningGaps.getOrElse(pairIdx) { 0 }
                            val idealVal = currentChallenge.idealGaps.getOrElse(pairIdx) { 0 }

                            FilterChip(
                                selected = selectedPairIndex == pairIdx,
                                onClick = { selectedPairIndex = pairIdx },
                                label = {
                                    Text(
                                        text = if (kerningRevealed) {
                                            "$leftChar|$rightChar (${currentVal * 10} vs ${idealVal * 10})"
                                        } else {
                                            "$leftChar|$rightChar (${if (currentVal >= 0) "+" else ""}${currentVal * 10})"
                                        },
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("kern_pair_chip_$pairIdx")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Precision Nudge Buttons & Slider for the Selected Letter Pair
                    val activeGap = userKerningGaps.getOrElse(selectedPairIndex) { 0 }
                    val pairLabel = "${currentChallenge.word[selectedPairIndex]} ↔ ${currentChallenge.word[selectedPairIndex + 1]}"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pair $pairLabel Kerning: ${activeGap * 10} / 1000 em",
                            style = MaterialTheme.typography.titleMedium,
                            color = ProcessYellowGold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalButton(
                                onClick = { onAdjustKerningGap(selectedPairIndex, -2) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("kern_tighten_button")
                            ) {
                                Text("-20 Tight", style = MaterialTheme.typography.labelMedium)
                            }
                            FilledTonalButton(
                                onClick = { onAdjustKerningGap(selectedPairIndex, 2) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("kern_loosen_button")
                            ) {
                                Text("+20 Loose", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    Slider(
                        value = activeGap.toFloat(),
                        onValueChange = { v -> onSetKerningGap(selectedPairIndex, v.toInt()) },
                        valueRange = -15f..18f,
                        colors = SliderDefaults.colors(
                            thumbColor = ProcessCyanGuide,
                            activeTrackColor = ProcessCyanGuide
                        )
                    )

                    // Evaluation Banner if revealed
                    if (kerningRevealed && lastEvaluatedScore != null) {
                        val score = lastEvaluatedScore ?: 0
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ObsidianPasteboard)
                                .border(
                                    width = 1.dp,
                                    color = if (score >= 80) PreflightPassGreen else ProcessYellowGold,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Optical Kerning Score: $score / 100",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (score >= 80) PreflightPassGreen else ProcessYellowGold
                                )
                                Text(
                                    text = if (score >= 85) {
                                        "Master Typographer's Eye! Your diagonal and open-counter balance matches Swiss foundry standards."
                                    } else {
                                        "Compare your pair values against the target ideal values shown in the pair chips above!"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Action Buttons: Score Optical Kerning + Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                lastEvaluatedScore = onEvaluateKerning()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ProcessCyanGuide,
                                contentColor = ObsidianPasteboard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("evaluate_kerning_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Check Optical Kerning",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                lastEvaluatedScore = null
                                onResetKerningWord()
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Kerning", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset")
                        }
                    }
                }
            }
        }

        // Interactive Leading & Baseline Grid Laboratory Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatLineSpacing,
                            contentDescription = null,
                            tint = ProcessYellowGold
                        )
                        Column {
                            Text(
                                text = "Leading Ratio & Baseline Visualizer",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "InDesign Auto-Leading defaults to 120% of point size (e.g., 10pt type / 12pt leading)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    val ratioPercent = ((demoLeadingPt.toFloat() / demoFontSizePt.toFloat()) * 100).toInt()
                    val leadingVerdict = when {
                        demoLeadingPt < demoFontSizePt -> "CRASHING ASCENDERS/DESCENDERS (Illegible)"
                        demoLeadingPt == demoFontSizePt -> "SET SOLID (${demoFontSizePt}/${demoLeadingPt} • Cramped for body copy)"
                        ratioPercent in 118..145 -> "IDEAL EDITORIAL RHYTHM (${demoFontSizePt}/${demoLeadingPt} • ${ratioPercent}%)"
                        else -> "AIRY / LOOSE LEADING (${demoFontSizePt}/${demoLeadingPt} • ${ratioPercent}%)"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ArchivalPaperCream)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "SPEC: ${demoFontSizePt}/${demoLeadingPt}pt ($leadingVerdict)",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (ratioPercent in 118..145) Color(0xFF0A7B56) else BleedAlertRed
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Good typography establishes a clear visual hierarchy between display headlines, subheads, and multi-column body text. When leading is set too tight, the eye struggles to track back to the start of the next line across wide column measures.",
                                fontFamily = PlusJakartaSansFontFamily,
                                fontSize = demoFontSizePt.sp,
                                lineHeight = demoLeadingPt.sp,
                                color = PressInkBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Type Size: ${demoFontSizePt}pt",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                            Slider(
                                value = demoFontSizePt.toFloat(),
                                onValueChange = { demoFontSizePt = it.toInt() },
                                valueRange = 9f..16f
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Leading: ${demoLeadingPt}pt",
                                style = MaterialTheme.typography.labelMedium,
                                color = ProcessCyanGuide
                            )
                            Slider(
                                value = demoLeadingPt.toFloat(),
                                onValueChange = { demoLeadingPt = it.toInt() },
                                valueRange = 9f..24f
                            )
                        }
                    }
                }
            }
        }

        // Editorial Font Pairing Showcase Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spellcheck,
                            contentDescription = null,
                            tint = ProcessCyanGuide
                        )
                        Text(
                            text = "Classic DTP Font Pairing Specimens",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }

                    // Specimen 1: High-Contrast Serif + Humanist Sans
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianPasteboard)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "PAIRING 01 • LITERARY & FEATURE MAGAZINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessYellowGold
                        )
                        Text(
                            text = "Playfair Display Bold + Plus Jakarta Sans",
                            fontFamily = PlayfairDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "High-contrast transitional/modern serifs anchor feature headlines while a clean humanist sans-serif keeps dense captions crisp.",
                            fontFamily = PlusJakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondaryMuted
                        )
                    }

                    // Specimen 2: Swiss Geometric Sans + Technical Monospace
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianPasteboard)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "PAIRING 02 • SWISS ARCHITECTURAL MONOGRAPH",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessCyanGuide
                        )
                        Text(
                            text = "Space Grotesk Bold + JetBrains Mono Specs",
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "GRID: 3 COL • GUTTER: 1p6 • MARGIN: 3p0 • BLEED: 3MM • PLATES: CMYK",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = ProcessMagentaGuide
                        )
                    }
                }
            }
        }
    }
}
