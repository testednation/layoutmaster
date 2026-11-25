package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.FontDownloadOff
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EditorialFontChoice
import com.example.ui.components.toComposeFontFamily
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.InDesignMagenta
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PlusJakartaSansFontFamily
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

enum class PreflightInspectorCategory(
    val title: String,
    val icon: ImageVector
) {
    FONTS("1. Missing Fonts", Icons.Default.FontDownloadOff),
    OVERSET_TEXT("2. Overset Text [+]", Icons.Default.TextFields),
    LINKS_PPI("3. Links & PPI", Icons.Default.BrokenImage),
    COLOR_BLEED("4. Color & Bleed", Icons.Default.ColorLens)
}

data class PreflightCaseSpec(
    val id: Int,
    val code: String,
    val documentTitle: String,
    val clientBrief: String,
    val missingHeadlineFontName: String,
    val missingBodyFontName: String?,
    val recommendedFont: EditorialFontChoice,
    val brokenImageFileName: String,
    val initialPpi: Int,
    val initialDistortedScale: Boolean,
    val initialBodySizePt: Int,
    val initialLeadingPt: Int,
    val initialOversetWords: Int,
    val initialBleedMm: Int,
    val initialColorSpace: String,
    val initialUsesRegistrationInk: Boolean
)

private val preflightCases = listOf(
    PreflightCaseSpec(
        id = 1,
        code = "JOB #101 • MAGAZINE COVER",
        documentTitle = "MONOLITH ARCHITECTURE 09",
        clientBrief = "A luxury architecture quarterly arrived 10 minutes before offset press lock. Inspect the pink missing-font highlight, relink the 72 PPI low-res cover placement, thread the [+] overset article, and add 3mm CMYK bleed.",
        missingHeadlineFontName = "[Bodoni-Poster-Compressed.otf]",
        missingBodyFontName = null,
        recommendedFont = EditorialFontChoice.PLAYFAIR,
        brokenImageFileName = "monolith_facade_web_72dpi.jpg",
        initialPpi = 72,
        initialDistortedScale = false,
        initialBodySizePt = 14,
        initialLeadingPt = 18,
        initialOversetWords = 34,
        initialBleedMm = 0,
        initialColorSpace = "RGB",
        initialUsesRegistrationInk = false
    ),
    PreflightCaseSpec(
        id = 2,
        code = "JOB #102 • EXPEDITION CATALOG",
        documentTitle = "ALPINE SUMMIT FIELD GUIDE",
        clientBrief = "The junior designer stretched the hero photo non-proportionally (142% x 88%), left a missing font in the masthead, set body copy in 400% [Registration] ink instead of 100% K [Black], and left unthreaded overset copy.",
        missingHeadlineFontName = "[HelveticaNeue-HeavyExt.ps]",
        missingBodyFontName = "[GillSans-Light.ttf]",
        recommendedFont = EditorialFontChoice.SPACE_GROTESK,
        brokenImageFileName = "alpine_ridge_missing.tif [?]",
        initialPpi = 96,
        initialDistortedScale = true,
        initialBodySizePt = 13,
        initialLeadingPt = 17,
        initialOversetWords = 48,
        initialBleedMm = 1,
        initialColorSpace = "RGB",
        initialUsesRegistrationInk = true
    ),
    PreflightCaseSpec(
        id = 3,
        code = "JOB #103 • EXHIBITION MONOGRAPH",
        documentTitle = "BAUHAUS & SWISS GRIDS 1958",
        clientBrief = "Final press check! Replace both missing PostScript fonts, relink the 300 PPI CMYK separation TIFF with proportional scaling, clear the red [+] overset port, and extend the trim bleed to 3mm.",
        missingHeadlineFontName = "[AkzidenzGrotesk-Super.otf]",
        missingBodyFontName = "[Univers-55-Roman.pfb]",
        recommendedFont = EditorialFontChoice.SPACE_GROTESK,
        brokenImageFileName = "zurich_poster_draft_lowres.png",
        initialPpi = 72,
        initialDistortedScale = true,
        initialBodySizePt = 14,
        initialLeadingPt = 19,
        initialOversetWords = 62,
        initialBleedMm = 0,
        initialColorSpace = "RGB",
        initialUsesRegistrationInk = true
    )
)

data class PreflightErrorDiagnostic(
    val category: PreflightInspectorCategory,
    val title: String,
    val locationTag: String,
    val details: String
)

@Composable
fun PreflightChallengeScreen(
    completedCasesCount: Int,
    onRecordPreflightRescue: (caseTitle: String, score: Int, totalChecks: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCaseIndex by remember { mutableIntStateOf(0) }
    val activeCase = preflightCases[selectedCaseIndex]

    // Interactive document remediation states (reset whenever selectedCaseIndex changes)
    var replacedFont by remember(selectedCaseIndex) { mutableStateOf<EditorialFontChoice?>(null) }
    var bodyFontsReplaced by remember(selectedCaseIndex) {
        mutableStateOf(activeCase.missingBodyFontName == null)
    }

    var framesThreaded by remember(selectedCaseIndex) { mutableStateOf(false) }
    var bodySizePt by remember(selectedCaseIndex) { mutableFloatStateOf(activeCase.initialBodySizePt.toFloat()) }
    var leadingPt by remember(selectedCaseIndex) { mutableFloatStateOf(activeCase.initialLeadingPt.toFloat()) }

    var imagePpi by remember(selectedCaseIndex) { mutableIntStateOf(activeCase.initialPpi) }
    var imageLinkRestored by remember(selectedCaseIndex) { mutableStateOf(false) }
    var distortedScale by remember(selectedCaseIndex) { mutableStateOf(activeCase.initialDistortedScale) }

    var bleedMm by remember(selectedCaseIndex) { mutableFloatStateOf(activeCase.initialBleedMm.toFloat()) }
    var colorSpace by remember(selectedCaseIndex) { mutableStateOf(activeCase.initialColorSpace) }
    var usesRegistrationInk by remember(selectedCaseIndex) { mutableStateOf(activeCase.initialUsesRegistrationInk) }

    var activeCategory by remember { mutableStateOf(PreflightInspectorCategory.FONTS) }
    var showPackageReportDialog by remember { mutableStateOf(false) }

    // Derived live preflight error diagnostics
    val fontErrorFixed = replacedFont != null && bodyFontsReplaced
    val oversetErrorFixed = framesThreaded && bodySizePt.toInt() <= 11 && leadingPt.toInt() <= 15
    val imageErrorFixed = imagePpi >= 300 && imageLinkRestored && !distortedScale
    val colorBleedErrorFixed = colorSpace == "CMYK" && bleedMm.toInt() >= 3 && !usesRegistrationInk

    val activeErrors = remember(
        fontErrorFixed,
        oversetErrorFixed,
        imageErrorFixed,
        colorBleedErrorFixed,
        replacedFont,
        bodyFontsReplaced,
        framesThreaded,
        bodySizePt,
        leadingPt,
        imagePpi,
        imageLinkRestored,
        distortedScale,
        colorSpace,
        bleedMm,
        usesRegistrationInk,
        activeCase
    ) {
        buildList {
            if (!fontErrorFixed) {
                val missingList = buildList {
                    if (replacedFont == null) add(activeCase.missingHeadlineFontName)
                    if (!bodyFontsReplaced && activeCase.missingBodyFontName != null) {
                        add(activeCase.missingBodyFontName)
                    }
                }.joinToString(", ")
                add(
                    PreflightErrorDiagnostic(
                        category = PreflightInspectorCategory.FONTS,
                        title = "MISSING FONT(S) IN DOCUMENT",
                        locationTag = "Page 1 • Headline & Story Frames",
                        details = "Uninstalled font $missingList is highlighted in pink and will substitute with Courier on press."
                    )
                )
            }
            if (!oversetErrorFixed) {
                val reason = if (!framesThreaded) {
                    "Column 1 Out-Port [+] is unthreaded (${activeCase.initialOversetWords} words cut off)."
                } else {
                    "Body type (${bodySizePt.toInt()}/${leadingPt.toInt()}pt) is still too large for the 2-column frame capacity."
                }
                add(
                    PreflightErrorDiagnostic(
                        category = PreflightInspectorCategory.OVERSET_TEXT,
                        title = "OVERSET TEXT [+] DETECTED",
                        locationTag = "Page 1 • Story Frame Out-Port",
                        details = reason
                    )
                )
            }
            if (!imageErrorFixed) {
                val imgProblem = buildList {
                    if (!imageLinkRestored || imagePpi < 300) {
                        add("Effective resolution is $imagePpi PPI (minimum 300 PPI required for offset print)")
                    }
                    if (distortedScale) {
                        add("Non-proportional X/Y scaling (142% × 88%) distorts pixels")
                    }
                }.joinToString(" • ")
                add(
                    PreflightErrorDiagnostic(
                        category = PreflightInspectorCategory.LINKS_PPI,
                        title = "LOW-RES / MODIFIED IMAGE LINK",
                        locationTag = "Page 1 • ${activeCase.brokenImageFileName}",
                        details = imgProblem
                    )
                )
            }
            if (!colorBleedErrorFixed) {
                val colorProblems = buildList {
                    if (colorSpace != "CMYK") add("Plates set to $colorSpace instead of Process CMYK")
                    if (bleedMm.toInt() < 3) add("Bleed is ${bleedMm.toInt()}mm (requires >= 3mm)")
                    if (usesRegistrationInk) add("Body text uses 400% [Registration] swatch instead of 100% K [Black]")
                }.joinToString(" • ")
                add(
                    PreflightErrorDiagnostic(
                        category = PreflightInspectorCategory.COLOR_BLEED,
                        title = "PREPRESS COLOR & BLEED VIOLATION",
                        locationTag = "Page 1 • Document Setup & Swatches",
                        details = colorProblems
                    )
                )
            }
        }
    }

    val fixedCategoriesCount = listOf(
        fontErrorFixed,
        oversetErrorFixed,
        imageErrorFixed,
        colorBleedErrorFixed
    ).count { it }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("preflight_challenge_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Live Preflight Inspector",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Tap red/pink defects directly on the spread or use the 4 Prepress panels below to resolve every print error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                Surface(
                    color = if (activeErrors.isEmpty()) {
                        PreflightPassGreen.copy(alpha = 0.2f)
                    } else {
                        BleedAlertRed.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (activeErrors.isEmpty()) PreflightPassGreen else BleedAlertRed,
                        RoundedCornerShape(10.dp)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (activeErrors.isEmpty()) PreflightPassGreen else BleedAlertRed)
                        )
                        Text(
                            text = if (activeErrors.isEmpty()) "0 ERRORS" else "${activeErrors.size} ERRORS",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (activeErrors.isEmpty()) PreflightPassGreen else BleedAlertRed
                        )
                    }
                }
            }
        }

        // Client Job Selector Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preflightCases.forEachIndexed { idx, spec ->
                    FilterChip(
                        selected = selectedCaseIndex == idx,
                        onClick = { selectedCaseIndex = idx },
                        label = {
                            Text(
                                text = spec.code,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                            selectedLabelColor = ProcessCyanGuide
                        ),
                        modifier = Modifier.testTag("preflight_case_chip_$idx")
                    )
                }
            }
        }

        // Client Brief Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeCase.documentTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = ProcessYellowGold
                        )
                        Text(
                            text = "$fixedCategoriesCount/4 Prepress Checks Passed",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (fixedCategoriesCount == 4) PreflightPassGreen else ProcessCyanGuide
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeCase.clientBrief,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD5DCF0)
                    )
                }
            }
        }

        // Interactive Document Canvas with Tappable Error Hotspots
        item {
            PreflightInteractiveDocumentCanvas(
                caseSpec = activeCase,
                replacedFont = replacedFont,
                bodyFontsReplaced = bodyFontsReplaced,
                framesThreaded = framesThreaded,
                bodySizePt = bodySizePt.toInt(),
                leadingPt = leadingPt.toInt(),
                imagePpi = imagePpi,
                imageLinkRestored = imageLinkRestored,
                distortedScale = distortedScale,
                bleedMm = bleedMm.toInt(),
                colorSpace = colorSpace,
                usesRegistrationInk = usesRegistrationInk,
                fontErrorFixed = fontErrorFixed,
                oversetErrorFixed = oversetErrorFixed,
                imageErrorFixed = imageErrorFixed,
                colorBleedErrorFixed = colorBleedErrorFixed,
                selectedCategory = activeCategory,
                onSelectCategory = { activeCategory = it }
            )
        }

        // 4 Category Inspector Tabs (Tap any category to open its DTP Remediation Tool)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categoryStatus = mapOf(
                    PreflightInspectorCategory.FONTS to fontErrorFixed,
                    PreflightInspectorCategory.OVERSET_TEXT to oversetErrorFixed,
                    PreflightInspectorCategory.LINKS_PPI to imageErrorFixed,
                    PreflightInspectorCategory.COLOR_BLEED to colorBleedErrorFixed
                )

                PreflightInspectorCategory.entries.forEach { category ->
                    val isFixed = categoryStatus[category] == true
                    val isSelected = activeCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeCategory = category },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFixed) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isFixed) PreflightPassGreen else BleedAlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isFixed) {
                                PreflightPassGreen.copy(alpha = 0.2f)
                            } else {
                                BleedAlertRed.copy(alpha = 0.22f)
                            },
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("preflight_category_tab_${category.name}")
                    )
                }
            }
        }

        // Active Remediation Tool Drawer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ProcessCyanGuide.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeCategory) {
                        PreflightInspectorCategory.FONTS -> FontSubstitutionToolPanel(
                            caseSpec = activeCase,
                            replacedFont = replacedFont,
                            bodyFontsReplaced = bodyFontsReplaced,
                            onReplaceHeadlineFont = { replacedFont = it },
                            onReplaceBodyFonts = { bodyFontsReplaced = true }
                        )

                        PreflightInspectorCategory.OVERSET_TEXT -> OversetTextRemediationPanel(
                            framesThreaded = framesThreaded,
                            bodySizePt = bodySizePt,
                            leadingPt = leadingPt,
                            oversetFixed = oversetErrorFixed,
                            onToggleThread = { framesThreaded = it },
                            onBodySizeChange = { bodySizePt = it },
                            onLeadingChange = { leadingPt = it }
                        )

                        PreflightInspectorCategory.LINKS_PPI -> ImageLinksRemediationPanel(
                            caseSpec = activeCase,
                            imagePpi = imagePpi,
                            imageLinkRestored = imageLinkRestored,
                            distortedScale = distortedScale,
                            onRelinkHighRes = {
                                imageLinkRestored = true
                                imagePpi = 300
                            },
                            onFixProportionalScale = { distortedScale = false }
                        )

                        PreflightInspectorCategory.COLOR_BLEED -> ColorAndBleedRemediationPanel(
                            colorSpace = colorSpace,
                            bleedMm = bleedMm,
                            usesRegistrationInk = usesRegistrationInk,
                            onColorSpaceChange = { colorSpace = it },
                            onBleedChange = { bleedMm = it },
                            onFixRegistrationInk = { usesRegistrationInk = false }
                        )
                    }
                }
            }
        }

        // Live Preflight Error Tree Panel (mirrors InDesign Window > Output > Preflight)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FactCheck,
                                contentDescription = null,
                                tint = if (activeErrors.isEmpty()) PreflightPassGreen else BleedAlertRed
                            )
                            Text(
                                text = "PREFLIGHT PANEL • [Press Quality Profile]",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }

                        TextButton(
                            onClick = {
                                replacedFont = null
                                bodyFontsReplaced = activeCase.missingBodyFontName == null
                                framesThreaded = false
                                bodySizePt = activeCase.initialBodySizePt.toFloat()
                                leadingPt = activeCase.initialLeadingPt.toFloat()
                                imagePpi = activeCase.initialPpi
                                imageLinkRestored = false
                                distortedScale = activeCase.initialDistortedScale
                                bleedMm = activeCase.initialBleedMm.toFloat()
                                colorSpace = activeCase.initialColorSpace
                                usesRegistrationInk = activeCase.initialUsesRegistrationInk
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Document", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Errors", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    if (activeErrors.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PreflightPassGreen.copy(alpha = 0.15f))
                                .border(1.dp, PreflightPassGreen, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "✓ 0 ERRORS: All missing fonts replaced, links at 300 PPI proportional, 0 overset characters, and 3mm CMYK bleed verified.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PreflightPassGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        activeErrors.forEach { err ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ObsidianPasteboard)
                                    .border(
                                        width = 1.dp,
                                        color = if (activeCategory == err.category) ProcessCyanGuide else BleedAlertRed.copy(alpha = 0.45f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { activeCategory = err.category }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "▼ ${err.title}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = BleedAlertRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "(${err.locationTag})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ProcessYellowGold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = err.details,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFD5DCF0)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = ProcessCyanGuide.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "FIX →",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ProcessCyanGuide,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onRecordPreflightRescue(
                                "Preflight: ${activeCase.documentTitle}",
                                fixedCategoriesCount,
                                4
                            )
                            showPackageReportDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeErrors.isEmpty()) PreflightPassGreen else ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("package_for_print_button")
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (activeErrors.isEmpty()) {
                                "Package for Print (Cmd+Alt+Shift+P) • 100% Ready!"
                            } else {
                                "Run Package & Collect for Output ($fixedCategoriesCount/4 Fixed)"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Package / Collect for Output Prepress Report Dialog
    if (showPackageReportDialog) {
        val allResolved = activeErrors.isEmpty()
        val scorePercent = (fixedCategoriesCount * 25).coerceIn(0, 100)
        AlertDialog(
            onDismissRequest = { showPackageReportDialog = false },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = if (allResolved) {
                        "Package Folder Created — Press Ready!"
                    } else {
                        "Prepress Package Warning ($scorePercent%)"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (allResolved) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "JOB REPORT: ${activeCase.documentTitle}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = ProcessCyanGuide
                    )
                    Text(
                        text = "${if (fontErrorFixed) "✓" else "✗"} Fonts Packaged: ${replacedFont?.label ?: "MISSING " + activeCase.missingHeadlineFontName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (fontErrorFixed) PreflightPassGreen else BleedAlertRed
                    )
                    Text(
                        text = "${if (oversetErrorFixed) "✓" else "✗"} Story Frames: ${if (oversetErrorFixed) "0 Overset Characters (Threaded)" else "OVERSET TEXT [+] CUT OFF"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (oversetErrorFixed) PreflightPassGreen else BleedAlertRed
                    )
                    Text(
                        text = "${if (imageErrorFixed) "✓" else "✗"} Linked Graphics: ${imagePpi} PPI • ${if (distortedScale) "DISTORTED 142%x88%" else "100%x100% Proportional"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (imageErrorFixed) PreflightPassGreen else BleedAlertRed
                    )
                    Text(
                        text = "${if (colorBleedErrorFixed) "✓" else "✗"} Plates & Trim: $colorSpace • ${bleedMm.toInt()}mm Bleed • ${if (usesRegistrationInk) "400% [Registration] Error" else "100% K [Black]"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (colorBleedErrorFixed) PreflightPassGreen else BleedAlertRed
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPackageReportDialog = false
                        if (allResolved && selectedCaseIndex < preflightCases.lastIndex) {
                            selectedCaseIndex += 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Text(
                        text = if (allResolved && selectedCaseIndex < preflightCases.lastIndex) {
                            "Next Client Document →"
                        } else {
                            "Return to Inspector"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPackageReportDialog = false }) {
                    Text("Close", color = TextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
private fun PreflightInteractiveDocumentCanvas(
    caseSpec: PreflightCaseSpec,
    replacedFont: EditorialFontChoice?,
    bodyFontsReplaced: Boolean,
    framesThreaded: Boolean,
    bodySizePt: Int,
    leadingPt: Int,
    imagePpi: Int,
    imageLinkRestored: Boolean,
    distortedScale: Boolean,
    bleedMm: Int,
    colorSpace: String,
    usesRegistrationInk: Boolean,
    fontErrorFixed: Boolean,
    oversetErrorFixed: Boolean,
    imageErrorFixed: Boolean,
    colorBleedErrorFixed: Boolean,
    selectedCategory: PreflightInspectorCategory,
    onSelectCategory: (PreflightInspectorCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianPasteboard)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            .testTag("preflight_document_canvas")
    ) {
        // Top Pasteboard Bar indicating Interactive Hotspot Inspection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioDockSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = ProcessCyanGuide,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "TAP ANY HIGHLIGHTED ERROR ON SPREAD TO INSPECT",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProcessCyanGuide
                )
            }
            Text(
                text = "BLEED: ${bleedMm}MM • $colorSpace",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (colorBleedErrorFixed) PreflightPassGreen else BleedAlertRed
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Bleed Guide & Crop Marks Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(325.dp)
            ) {
                val bleedPx = (bleedMm * 3f).dp.toPx()
                val trimLeft = 10.dp.toPx()
                val trimTop = 10.dp.toPx()
                val trimW = size.width - 20.dp.toPx()
                val trimH = size.height - 20.dp.toPx()

                // Crop Marks
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

                // Bleed Guide Outline (Red when >= 3mm, Yellow warning dashed when < 3mm)
                drawRect(
                    color = if (bleedMm >= 3) PreflightPassGreen else BleedAlertRed,
                    topLeft = Offset(trimLeft - bleedPx, trimTop - bleedPx),
                    size = Size(trimW + bleedPx * 2, trimH + bleedPx * 2),
                    style = Stroke(
                        width = 1.6.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                    )
                )
            }

            // Archival Page Surface
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(305.dp)
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ArchivalPaperCream)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Hotspot 4: Top Bleed & Color Plate Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (colorBleedErrorFixed) {
                                Color(0xFFE6F6EF)
                            } else {
                                BleedAlertRed.copy(alpha = 0.16f)
                            }
                        )
                        .border(
                            width = if (selectedCategory == PreflightInspectorCategory.COLOR_BLEED) 1.5.dp else 0.8.dp,
                            color = if (colorBleedErrorFixed) PreflightPassGreen else BleedAlertRed,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectCategory(PreflightInspectorCategory.COLOR_BLEED) }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("hotspot_color_bleed"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (bleedMm < 3) {
                            "⚠ BLEED: ${bleedMm}MM (WHITE TRIM EDGE RISK!)"
                        } else {
                            "✓ 3MM FULL BLEED EXTENDED"
                        },
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bleedMm >= 3) Color(0xFF096A48) else BleedAlertRed
                    )
                    Text(
                        text = if (colorSpace == "CMYK" && !usesRegistrationInk) {
                            "PLATES: CMYK • 100% K"
                        } else if (usesRegistrationInk) {
                            "⚠ 400% [REGISTRATION] INK"
                        } else {
                            "⚠ RGB PLATES (UNCONVERTED)"
                        },
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (colorBleedErrorFixed) Color(0xFF096A48) else BleedAlertRed
                    )
                }

                // Hotspot 1: Headline Frame with Iconic InDesign Pink Missing-Font Highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (replacedFont == null) {
                                Color(0xFFFFB3C6) // Classic InDesign Pink Missing Font Highlight!
                            } else {
                                Color.Transparent
                            }
                        )
                        .border(
                            width = if (selectedCategory == PreflightInspectorCategory.FONTS) 1.5.dp else 0.9.dp,
                            color = if (fontErrorFixed) PreflightPassGreen else InDesignMagenta,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectCategory(PreflightInspectorCategory.FONTS) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("hotspot_missing_font")
                ) {
                    Column {
                        if (replacedFont == null) {
                            Text(
                                text = "⚠ MISSING FONT: ${caseSpec.missingHeadlineFontName}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E0031)
                            )
                        }
                        Text(
                            text = caseSpec.documentTitle,
                            fontFamily = (replacedFont ?: EditorialFontChoice.JETBRAINS_MONO).toComposeFontFamily(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PressInkBlack,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Hotspot 3: Placed Image Frame with Low-Res 72 PPI / Missing Link [?] / Non-Proportional Distortion
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(
                            width = if (selectedCategory == PreflightInspectorCategory.LINKS_PPI) 1.8.dp else 1.dp,
                            color = if (imageErrorFixed) PreflightPassGreen else BleedAlertRed,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectCategory(PreflightInspectorCategory.LINKS_PPI) }
                        .testTag("hotspot_image_link")
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_editorial_hero_1795037988082),
                        contentDescription = "Placed document graphic",
                        contentScale = if (distortedScale) ContentScale.FillBounds else ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (imagePpi < 300 || !imageLinkRestored) 0.48f else 1f)
                    )

                    // Top-Left InDesign Link Status Badge ([?] Red Missing / [!] Low-Res / Check OK)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .background(
                                if (imageErrorFixed) PreflightPassGreen else BleedAlertRed,
                                RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (!imageLinkRestored) {
                                "[?] LINK: ${caseSpec.brokenImageFileName} ($imagePpi PPI)"
                            } else if (distortedScale) {
                                "[!] DISTORTED SCALE: 142% × 88%"
                            } else {
                                "✓ 300 PPI TIFF • 100% × 100%"
                            },
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Hotspot 2: 2-Column Body Story with Overset Text [+] & Optional Missing Body Font / Registration Ink
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (!bodyFontsReplaced) Color(0xFFFFD6E0) else Color.Transparent
                        )
                        .border(
                            width = if (selectedCategory == PreflightInspectorCategory.OVERSET_TEXT) 1.6.dp else 0.9.dp,
                            color = if (oversetErrorFixed) ProcessCyanGuide else BleedAlertRed,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectCategory(PreflightInspectorCategory.OVERSET_TEXT) }
                        .padding(4.dp)
                        .testTag("hotspot_overset_story"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sampleCopyCol1 =
                        "Prepress preflighting verifies that every font outline is embedded, every linked image meets 300 effective PPI, and every story frame is threaded cleanly without cut-off paragraphs."
                    val sampleCopyCol2 =
                        "When an unthreaded text box or oversized leading causes story overflow, InDesign displays a red [+] in the out-port and flags an immediate Preflight error."

                    // Column 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.8.dp, ProcessCyanGuide.copy(alpha = 0.6f))
                            .padding(4.dp)
                    ) {
                        Text(
                            text = sampleCopyCol1,
                            fontFamily = if (bodyFontsReplaced) PlusJakartaSansFontFamily else JetBrainsMonoFontFamily,
                            fontSize = (bodySizePt * 0.72f).sp,
                            lineHeight = (leadingPt * 0.75f).sp,
                            color = if (usesRegistrationInk) Color(0xFF3B0944) else PressInkBlack,
                            overflow = TextOverflow.Clip
                        )

                        if (!framesThreaded) {
                            // Red Overset [+] on Column 1 when unthreaded!
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(BleedAlertRed, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "[+] OVERSET",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            // Blue Thread Arrow Port
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(ProcessCyanGuide, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "→ THREADED",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ObsidianPasteboard
                                )
                            }
                        }
                    }

                    // Column 2
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(
                                0.8.dp,
                                if (framesThreaded) ProcessCyanGuide else StudioBorderSubtle.copy(alpha = 0.4f)
                            )
                            .padding(4.dp)
                    ) {
                        if (framesThreaded) {
                            Text(
                                text = sampleCopyCol2,
                                fontFamily = if (bodyFontsReplaced) PlusJakartaSansFontFamily else JetBrainsMonoFontFamily,
                                fontSize = (bodySizePt * 0.72f).sp,
                                lineHeight = (leadingPt * 0.75f).sp,
                                color = if (usesRegistrationInk) Color(0xFF3B0944) else PressInkBlack,
                                overflow = TextOverflow.Clip
                            )
                            if (!oversetErrorFixed) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(BleedAlertRed, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "[+] ${bodySizePt}/${leadingPt}pt TOO LARGE",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .background(PreflightPassGreen, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "✓ STORY FITS",
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ObsidianPasteboard
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "EMPTY UNTHREADED COLUMN\nTap to thread Out-Port [+]",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                color = PressInkMuted,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontSubstitutionToolPanel(
    caseSpec: PreflightCaseSpec,
    replacedFont: EditorialFontChoice?,
    bodyFontsReplaced: Boolean,
    onReplaceHeadlineFont: (EditorialFontChoice) -> Unit,
    onReplaceBodyFonts: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    imageVector = Icons.Default.FindReplace,
                    contentDescription = null,
                    tint = InDesignMagenta
                )
                Text(
                    text = "Type > Find/Replace Font (Quark: Utilities > Usage)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        // Missing Headline Font Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Missing Font: ${caseSpec.missingHeadlineFontName}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (replacedFont == null) BleedAlertRed else PreflightPassGreen
                )
                Text(
                    text = if (replacedFont == null) "PINK HIGHLIGHT" else "REPLACED ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (replacedFont == null) InDesignMagenta else PreflightPassGreen
                )
            }

            Text(
                text = "Select an installed font family below to Click 'Change All':",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryMuted
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditorialFontChoice.entries.forEach { font ->
                    val isRec = font == caseSpec.recommendedFont
                    FilterChip(
                        selected = replacedFont == font,
                        onClick = { onReplaceHeadlineFont(font) },
                        label = {
                            Text(
                                text = if (isRec) "${font.label} (Recommended)" else font.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.testTag("replace_font_chip_${font.id}")
                    )
                }
            }
        }

        // Optional Second Missing Body Font
        if (caseSpec.missingBodyFontName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianPasteboard)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Body Copy Missing: ${caseSpec.missingBodyFontName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (bodyFontsReplaced) PreflightPassGreen else BleedAlertRed
                    )
                    Text(
                        text = "Substitute with bundled Plus Jakarta Sans Regular",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                FilledTonalButton(
                    onClick = onReplaceBodyFonts,
                    enabled = !bodyFontsReplaced,
                    modifier = Modifier.testTag("replace_body_font_button")
                ) {
                    Text(if (bodyFontsReplaced) "Replaced ✓" else "Change All")
                }
            }
        }
    }
}

@Composable
private fun OversetTextRemediationPanel(
    framesThreaded: Boolean,
    bodySizePt: Float,
    leadingPt: Float,
    oversetFixed: Boolean,
    onToggleThread: (Boolean) -> Unit,
    onBodySizeChange: (Float) -> Unit,
    onLeadingChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Story Frame Threading & Copyfitting Inspector",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "1. Thread Column 1 Out-Port [+] into Column 2",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = if (framesThreaded) {
                        "Story linked across both columns"
                    } else {
                        "Column 1 is unlinked! Half the article is trapped inside the frame."
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (framesThreaded) PreflightPassGreen else BleedAlertRed
                )
            }
            Switch(
                checked = framesThreaded,
                onCheckedChange = onToggleThread,
                modifier = Modifier.testTag("preflight_thread_frames_switch")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2. Copyfit Body Size / Leading (Target <= 11/15pt)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${bodySizePt.toInt()} / ${leadingPt.toInt()} pt",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (oversetFixed) PreflightPassGreen else ProcessYellowGold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Body Size (${bodySizePt.toInt()}pt)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                    Slider(
                        value = bodySizePt,
                        onValueChange = onBodySizeChange,
                        valueRange = 9f..15f,
                        colors = SliderDefaults.colors(
                            thumbColor = ProcessCyanGuide,
                            activeTrackColor = ProcessCyanGuide
                        )
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Leading (${leadingPt.toInt()}pt)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                    Slider(
                        value = leadingPt,
                        onValueChange = onLeadingChange,
                        valueRange = 11f..20f,
                        colors = SliderDefaults.colors(
                            thumbColor = ProcessCyanGuide,
                            activeTrackColor = ProcessCyanGuide
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageLinksRemediationPanel(
    caseSpec: PreflightCaseSpec,
    imagePpi: Int,
    imageLinkRestored: Boolean,
    distortedScale: Boolean,
    onRelinkHighRes: () -> Unit,
    onFixProportionalScale: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Window > Links & Effective PPI Inspector",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessYellowGold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Placed File: ${caseSpec.brokenImageFileName}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (imageLinkRestored && imagePpi >= 300) PreflightPassGreen else BleedAlertRed
                )
                Text(
                    text = "Effective Resolution: $imagePpi PPI (Offset Litho requires 300 PPI CMYK TIFF)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onRelinkHighRes,
                enabled = !imageLinkRestored || imagePpi < 300,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProcessCyanGuide,
                    contentColor = ObsidianPasteboard
                ),
                modifier = Modifier.testTag("relink_300ppi_button")
            ) {
                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (imageLinkRestored && imagePpi >= 300) "300 PPI Linked ✓" else "Relink 300 PPI")
            }
        }

        // Proportional X/Y Scaling Check
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (distortedScale) {
                        "Graphic Scale: 142% X × 88% Y (Non-Proportional!)"
                    } else {
                        "Graphic Scale: 100% X × 100% Y (Proportional ✓)"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (distortedScale) BleedAlertRed else PreflightPassGreen
                )
                Text(
                    text = "Use Fit Content Proportionally (Cmd+Opt+Shift+E) so photos never look squished",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onFixProportionalScale,
                enabled = distortedScale,
                modifier = Modifier.testTag("fix_proportional_scale_button")
            ) {
                Text(if (distortedScale) "Make 100%×100%" else "Proportional ✓")
            }
        }
    }
}

@Composable
private fun ColorAndBleedRemediationPanel(
    colorSpace: String,
    bleedMm: Float,
    usesRegistrationInk: Boolean,
    onColorSpaceChange: (String) -> Unit,
    onBleedChange: (Float) -> Unit,
    onFixRegistrationInk: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Swatches, Ink Limit & Document Bleed Setup",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessMagentaGuide
        )

        // Color Space (RGB -> CMYK)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "1. Convert Swatches & Plates to Process CMYK",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Current Document Intent: $colorSpace",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (colorSpace == "CMYK") PreflightPassGreen else BleedAlertRed
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CMYK", "RGB").forEach { mode ->
                    FilterChip(
                        selected = colorSpace == mode,
                        onClick = { onColorSpaceChange(mode) },
                        label = { Text(mode) },
                        modifier = Modifier.testTag("preflight_color_chip_$mode")
                    )
                }
            }
        }

        // Document Bleed Slider (0mm to 5mm)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2. Document Bleed (Extend art past Trim Line)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${bleedMm.toInt()} mm (Target: >= 3mm)",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (bleedMm.toInt() >= 3) PreflightPassGreen else BleedAlertRed
                )
            }
            Slider(
                value = bleedMm,
                onValueChange = { onBleedChange(kotlin.math.round(it)) },
                valueRange = 0f..5f,
                steps = 4,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessMagentaGuide,
                    activeTrackColor = ProcessMagentaGuide
                ),
                modifier = Modifier.testTag("preflight_bleed_slider")
            )
        }

        // Registration Ink Trap Check (400% TAC vs 100% K Black)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (usesRegistrationInk) {
                        "3. Ink Limit Alert: Body set in [Registration] (400% TAC)"
                    } else {
                        "3. Body Ink Swatch: [Black] (100% K Process Ink ✓)"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (usesRegistrationInk) BleedAlertRed else PreflightPassGreen
                )
                Text(
                    text = "[Registration] prints 100% on all 4 CMYK plates and is ONLY for crop marks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onFixRegistrationInk,
                enabled = usesRegistrationInk,
                modifier = Modifier.testTag("fix_registration_ink_button")
            ) {
                Text(if (usesRegistrationInk) "Set 100% K [Black]" else "100% K ✓")
            }
        }
    }
}
