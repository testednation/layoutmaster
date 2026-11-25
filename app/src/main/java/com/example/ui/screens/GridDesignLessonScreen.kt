package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PhotoSizeSelectLarge
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import com.example.model.DtpCurriculum
import com.example.model.DtpSoftwareMode
import com.example.model.GridArchetype
import com.example.model.GridImagePlacement
import com.example.model.GridLessonState
import com.example.model.GridTextAlignment
import com.example.model.MarginPreset
import com.example.ui.components.GridPrinciplesCanvas
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun GridDesignLessonScreen(
    softwareMode: DtpSoftwareMode,
    onRecordGridLessonProgress: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedArchetype by remember { mutableStateOf<GridArchetype?>(GridArchetype.SWISS_THREE) }
    var gridState by remember { mutableStateOf(GridLessonState()) }
    var activeControlTab by remember { mutableIntStateOf(0) }

    // Experiment tracking for the interactive 4-step lesson checklist
    var testedColumns by remember { mutableStateOf(setOf(3)) }
    var testedGuttersOrMargins by remember { mutableStateOf(false) }
    var testedTextAlignment by remember { mutableStateOf(false) }
    var testedImageSnapping by remember { mutableStateOf(false) }
    var showMasteryDialog by remember { mutableStateOf(false) }

    val checks = listOf(
        Triple(
            "Compare at least 2 different column layouts",
            testedColumns.size >= 2,
            "Explored: ${testedColumns.sorted().joinToString(", ") { "${it}-Col" }}"
        ),
        Triple(
            "Adjust Gutter Width or Margin Proportion",
            testedGuttersOrMargins,
            "Try Swiss Book (Asymmetric) or 0p6 vs 1p6 gutter"
        ),
        Triple(
            "Compare Flush Left vs. Justified Text Alignment",
            testedTextAlignment,
            "Observe whitespace river risk in narrow columns"
        ),
        Triple(
            "Test Image Placement & Snap to Grid Modules",
            testedImageSnapping && gridState.imagePlacement.isSnappedToGrid,
            if (gridState.imagePlacement.isSnappedToGrid) "Snapped: ${gridState.imagePlacement.label}" else "Fix off-grid image!"
        )
    )
    val passedCount = checks.count { it.second }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("grid_design_lesson_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header + Guides Toggle (W / F7)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Grid Design Principles",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (softwareMode == DtpSoftwareMode.QUARKXPRESS) {
                            "QuarkXPress Master Guides: Toggle columns, gutters & margins to study alignment"
                        } else {
                            "InDesign Margins & Columns: Toggle grids, gutters & margins to study alignment"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { gridState = gridState.copy(showGuides = !gridState.showGuides) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("grid_lesson_toggle_guides_btn")
                ) {
                    Icon(
                        imageVector = if (gridState.showGuides) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Grid Guides",
                        tint = if (gridState.showGuides) ProcessCyanGuide else PreflightPassGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (gridState.showGuides) "Guides On" else "Clean (W)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }

        // 4 Classic Editorial Grid Archetype Preset Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Classic Editorial Grid Archetypes (1-Tap Presets):",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProcessCyanGuide
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GridArchetype.entries.forEach { archetype ->
                        FilterChip(
                            selected = selectedArchetype == archetype,
                            onClick = {
                                selectedArchetype = archetype
                                testedColumns = testedColumns + archetype.columns
                                gridState = gridState.copy(
                                    columns = archetype.columns,
                                    gutterPicas = archetype.gutterPicas,
                                    marginPreset = archetype.marginPreset,
                                    modularRows = archetype.modularRows,
                                    imagePlacement = archetype.imagePlacement,
                                    textAlignment = archetype.textAlignment
                                )
                            },
                            label = {
                                Text(
                                    text = archetype.label,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("grid_archetype_chip_${archetype.name}")
                        )
                    }
                }

                selectedArchetype?.let { arch ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioCardSurface)
                            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${arch.subtitle}: ${arch.principleNote}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD5DCF0)
                        )
                    }
                }
            }
        }

        // Live Interactive Editorial Grid Spread Canvas
        item {
            GridPrinciplesCanvas(state = gridState)
        }

        // 3 Interactive Control Tabs: Columns & Gutters | Margins & Text Alignment | Image Placement
        item {
            val controlTabs = listOf(
                "1. Columns & Gutters" to Icons.Default.GridView,
                "2. Margins & Text" to Icons.AutoMirrored.Filled.FormatAlignLeft,
                "3. Image Placement" to Icons.Default.PhotoSizeSelectLarge
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ProcessCyanGuide.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column {
                    ScrollableTabRow(
                        selectedTabIndex = activeControlTab,
                        containerColor = StudioCardSurface,
                        contentColor = ProcessCyanGuide,
                        edgePadding = 8.dp
                    ) {
                        controlTabs.forEachIndexed { idx, (label, icon) ->
                            Tab(
                                selected = activeControlTab == idx,
                                onClick = { activeControlTab = idx },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Text(label, style = MaterialTheme.typography.labelLarge)
                                    }
                                },
                                modifier = Modifier.testTag("grid_control_tab_$idx")
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (activeControlTab) {
                            0 -> ColumnsAndGuttersControls(
                                state = gridState,
                                onSelectColumns = { col ->
                                    selectedArchetype = null
                                    testedColumns = testedColumns + col
                                    gridState = gridState.copy(columns = col)
                                },
                                onSelectGutter = { picas ->
                                    selectedArchetype = null
                                    testedGuttersOrMargins = true
                                    gridState = gridState.copy(gutterPicas = picas)
                                },
                                onSelectModularRows = { rows ->
                                    selectedArchetype = null
                                    gridState = gridState.copy(modularRows = rows)
                                }
                            )

                            1 -> MarginsAndTextAlignmentControls(
                                state = gridState,
                                onSelectMarginPreset = { preset ->
                                    selectedArchetype = null
                                    testedGuttersOrMargins = true
                                    gridState = gridState.copy(marginPreset = preset)
                                },
                                onSelectTextAlignment = { align ->
                                    selectedArchetype = null
                                    testedTextAlignment = true
                                    gridState = gridState.copy(textAlignment = align)
                                }
                            )

                            2 -> ImagePlacementControls(
                                state = gridState,
                                onSelectImagePlacement = { placement ->
                                    selectedArchetype = null
                                    testedImageSnapping = true
                                    gridState = gridState.copy(imagePlacement = placement)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Guided 4-Step Grid Principles Checklist Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (passedCount == checks.size) PreflightPassGreen else StudioBorderSubtle,
                        RoundedCornerShape(16.dp)
                    )
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
                        Text(
                            text = "Grid Principles Lab Experiments",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "$passedCount / ${checks.size} Explored",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (passedCount == checks.size) PreflightPassGreen else ProcessYellowGold
                        )
                    }

                    checks.forEach { (title, isMet, detail) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (isMet) PreflightPassGreen else TextSecondaryMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isMet) PreflightPassGreen else Color.White,
                                    fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
                                )
                                Text(
                                    text = detail,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onRecordGridLessonProgress("Grid Design Principles", passedCount, checks.size)
                            showMasteryDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (passedCount == checks.size) PreflightPassGreen else ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_grid_lesson_button")
                    ) {
                        Text(
                            text = if (passedCount == checks.size) {
                                "Grid Architecture Complete! Save Lesson Progress"
                            } else {
                                "Record Grid Lab Progress ($passedCount/${checks.size})"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showMasteryDialog) {
        AlertDialog(
            onDismissRequest = { showMasteryDialog = false },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = if (passedCount == checks.size) {
                        "Swiss Grid Mastery Unlocked!"
                    } else {
                        "Grid Lab Progress Saved ($passedCount/${checks.size})"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (passedCount == checks.size) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Text(
                    text = if (passedCount == checks.size) {
                        "You explored how column counts, pica gutter spacing, asymmetric margins, text alignment, and grid-snapped imagery work together in InDesign and QuarkXPress."
                    } else {
                        "Try all 4 experiments—including toggling between Flush Left and Justified alignment and snapping your image cleanly across columns!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showMasteryDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Text("Keep Exploring")
                }
            }
        )
    }
}

@Composable
private fun ColumnsAndGuttersControls(
    state: GridLessonState,
    onSelectColumns: (Int) -> Unit,
    onSelectGutter: (Float) -> Unit,
    onSelectModularRows: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Column Layout Toggle Buttons
        Text(
            text = "Column Layout (Vertical Grid Divisions)",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 2, 3, 4).forEach { col ->
                FilterChip(
                    selected = state.columns == col,
                    onClick = { onSelectColumns(col) },
                    label = { Text("$col Col") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("grid_col_toggle_$col")
                )
            }
        }

        // Gutter Width Presets + Precision Slider
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gutter Width (Space Between Columns)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${DtpCurriculum.formatPicas(state.gutterPicas)} (${(state.gutterPicas * 12).toInt()}pt)",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (state.hasGutterJumpRisk) BleedAlertRed else ProcessCyanGuide
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    0.5f to "0p6 Tight",
                    1.0f to "1p0 Std",
                    1.5f to "1p6 Swiss",
                    2.0f to "2p0 Airy"
                ).forEach { (picas, label) ->
                    FilterChip(
                        selected = kotlin.math.abs(state.gutterPicas - picas) < 0.1f,
                        onClick = { onSelectGutter(picas) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.testTag("grid_gutter_preset_${label.substringBefore(" ")}")
                    )
                }
            }

            Slider(
                value = state.gutterPicas,
                onValueChange = { raw ->
                    val snapped = kotlin.math.round(raw * 2f) / 2f
                    onSelectGutter(snapped)
                },
                valueRange = 0.5f..2.5f,
                colors = SliderDefaults.colors(
                    thumbColor = if (state.hasGutterJumpRisk) BleedAlertRed else ProcessCyanGuide,
                    activeTrackColor = if (state.hasGutterJumpRisk) BleedAlertRed else ProcessCyanGuide
                ),
                modifier = Modifier.testTag("grid_gutter_slider")
            )
            Text(
                text = "Rule of Thumb: Gutter width should equal or exceed your body copy leading (e.g., 12pt–18pt / 1p0–1p6) so columns stay visually distinct.",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryMuted
            )
        }

        // Modular Horizontal Flowlines Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Horizontal Modular Flowlines",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "InDesign: Layout > Create Guides (Rows)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0 to "Off", 3 to "3 Rows", 4 to "4 Rows").forEach { (rows, label) ->
                    FilterChip(
                        selected = state.modularRows == rows,
                        onClick = { onSelectModularRows(rows) },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MarginsAndTextAlignmentControls(
    state: GridLessonState,
    onSelectMarginPreset: (MarginPreset) -> Unit,
    onSelectTextAlignment: (GridTextAlignment) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Page Margin Proportions (Magenta Guide)",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessMagentaGuide
        )

        MarginPreset.entries.forEach { preset ->
            val selected = state.marginPreset == preset
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) ProcessMagentaGuide.copy(alpha = 0.16f) else ObsidianPasteboard)
                    .border(
                        width = if (selected) 1.4.dp else 1.dp,
                        color = if (selected) ProcessMagentaGuide else StudioBorderSubtle,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectMarginPreset(preset) }
                    .padding(10.dp)
                    .testTag("grid_margin_preset_${preset.name}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) Color.White else ProcessMagentaGuide
                    )
                    Text(
                        text = "T:${DtpCurriculum.formatPicas(preset.topPicas)} B:${DtpCurriculum.formatPicas(preset.bottomPicas)} L:${DtpCurriculum.formatPicas(preset.leftPicas)} R:${DtpCurriculum.formatPicas(preset.rightPicas)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProcessYellowGold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = preset.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD5DCF0)
                )
            }
        }

        Text(
            text = "Text Alignment & Column Interaction",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GridTextAlignment.entries.forEach { align ->
                FilterChip(
                    selected = state.textAlignment == align,
                    onClick = { onSelectTextAlignment(align) },
                    label = {
                        Text(
                            text = "${align.label} (${align.dtpShortcut})",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.testTag("grid_align_chip_${align.name}")
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(10.dp)
        ) {
            Text(
                text = state.textAlignment.guidance,
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.hasWhitespaceRiverRisk || state.hasCenteredBodyWarning) {
                    ProcessYellowGold
                } else {
                    Color(0xFFD5DCF0)
                }
            )
        }
    }
}

@Composable
private fun ImagePlacementControls(
    state: GridLessonState,
    onSelectImagePlacement: (GridImagePlacement) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Image Placement & Grid-Spanning Rules",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessYellowGold
        )
        Text(
            text = "In editorial design, photographs should always align strictly to column edges—never end halfway inside a column or float randomly across a gutter.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryMuted
        )

        GridImagePlacement.entries.forEach { placement ->
            val selected = state.imagePlacement == placement
            val accent = if (placement.isSnappedToGrid) PreflightPassGreen else BleedAlertRed
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) accent.copy(alpha = 0.16f) else ObsidianPasteboard)
                    .border(
                        width = if (selected) 1.5.dp else 1.dp,
                        color = if (selected) accent else StudioBorderSubtle,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectImagePlacement(placement) }
                    .padding(10.dp)
                    .testTag("grid_image_placement_${placement.name}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = placement.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selected) Color.White else accent
                    )
                    Surface(
                        color = accent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = if (placement.isSnappedToGrid) "GRID SNAPPED ✓" else "BREAKS GRID ✗",
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = placement.spansDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD5DCF0)
                )
            }
        }
    }
}
