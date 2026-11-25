package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.SavedSpreadEntity
import com.example.model.DtpCurriculum
import com.example.model.DtpMission
import com.example.model.DtpSoftwareMode
import com.example.model.EditorialFontChoice
import com.example.model.MissionObjectiveCheck
import com.example.model.SpreadLayoutState
import com.example.ui.components.InteractiveSpreadCanvas
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.InDesignMagenta
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.QuarkClassicTeal
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun StudioWorkbenchScreen(
    spreadState: SpreadLayoutState,
    activeMission: DtpMission?,
    objectiveChecks: List<MissionObjectiveCheck>,
    savedSpreads: List<SavedSpreadEntity>,
    onUpdateSpread: ((SpreadLayoutState) -> SpreadLayoutState) -> Unit,
    onToggleSoftwareMode: () -> Unit,
    onTogglePreviewMode: () -> Unit,
    onSelectMission: (Int) -> Unit,
    onSwitchToSandbox: () -> Unit,
    onSubmitMissionPressCheck: ((score: Int, stars: Int, passed: Boolean) -> Unit) -> Unit,
    onSaveSpread: (String) -> Unit,
    onLoadSavedSpread: (SavedSpreadEntity) -> Unit,
    onDeleteSavedSpread: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaletteTab by remember { mutableIntStateOf(0) }
    var pressCheckDialogResult by remember { mutableStateOf<Triple<Int, Int, Boolean>?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var customSaveTitle by remember(spreadState.title) { mutableStateOf(spreadState.title) }

    val isQuark = spreadState.softwareMode == DtpSoftwareMode.QUARKXPRESS
    val modeAccentColor = if (isQuark) QuarkClassicTeal else InDesignMagenta

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("studio_workbench_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Application Bar: Software Mode Switcher (InDesign vs QuarkXPress) + Preview Toggle (W / F7)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Software Workspace Switcher Pill (Id vs QXP)
                Surface(
                    color = modeAccentColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .border(1.dp, modeAccentColor, RoundedCornerShape(10.dp))
                        .clickable { onToggleSoftwareMode() }
                        .testTag("toggle_software_mode_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(modeAccentColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = spreadState.softwareMode.shortBadge,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "${spreadState.softwareMode.displayName} UI",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap to switch Id ↔ QXP terms",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch between InDesign and QuarkXPress mode",
                            tint = modeAccentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Normal vs Trim Preview Mode Toggle ('W' in InDesign / 'F7' in QuarkXPress)
                OutlinedButton(
                    onClick = onTogglePreviewMode,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("toggle_preview_trim_button")
                ) {
                    Icon(
                        imageVector = if (spreadState.previewTrimMode) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = "Toggle Normal / Preview Mode",
                        tint = if (spreadState.previewTrimMode) PreflightPassGreen else ProcessCyanGuide,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (spreadState.previewTrimMode) {
                            "Trim View (${if (isQuark) "F7" else "W"})"
                        } else {
                            "Guides On (${if (isQuark) "F7" else "W"})"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }

        // Mission / Sandbox Selector Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeMission == null,
                    onClick = onSwitchToSandbox,
                    label = {
                        Text(
                            text = "Freeform Sandbox",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                        selectedLabelColor = ProcessCyanGuide
                    )
                )
                DtpCurriculum.missions.forEach { mission ->
                    FilterChip(
                        selected = activeMission?.id == mission.id,
                        onClick = { onSelectMission(mission.id) },
                        label = {
                            Text(
                                text = "M0${mission.id}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.25f),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Active Mission Brief & Live Objective Checklist Card
        if (activeMission != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ProcessMagentaGuide.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${activeMission.codeName}: ${activeMission.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = activeMission.conceptSummary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        objectiveChecks.forEach { check ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (check.isMet) {
                                            Icons.Default.CheckCircle
                                        } else {
                                            Icons.Default.ErrorOutline
                                        },
                                        contentDescription = null,
                                        tint = if (check.isMet) PreflightPassGreen else ProcessYellowGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = check.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (check.isMet) PreflightPassGreen else Color.White,
                                        fontWeight = if (check.isMet) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                                Text(
                                    text = check.technicalDetail,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        val allMet = objectiveChecks.all { it.isMet }
                        Button(
                            onClick = {
                                onSubmitMissionPressCheck { score, stars, passed ->
                                    pressCheckDialogResult = Triple(score, stars, passed)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (allMet) PreflightPassGreen else ProcessCyanGuide,
                                contentColor = ObsidianPasteboard
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_press_check_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (allMet) {
                                    "All Objectives Met! Run Press Check & Collect Stars"
                                } else {
                                    "Run Preflight & Submit Layout (${objectiveChecks.count { it.isMet }}/${objectiveChecks.size})"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // The Live Interactive Pasteboard & Spread Canvas
        item {
            InteractiveSpreadCanvas(state = spreadState)
        }

        // DTP Control Palette Tabs (Grid & Guides | Type & Copy | Frames & Wrap | Preflight & Portfolio)
        item {
            val paletteTabs = listOf(
                "1. Grid & Master" to Icons.Default.GridView,
                "2. Type & Leading" to Icons.Default.TextFields,
                "3. Thread & Wrap" to Icons.Default.Link,
                "4. Preflight & Save" to Icons.Default.FactCheck
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column {
                    ScrollableTabRow(
                        selectedTabIndex = selectedPaletteTab,
                        containerColor = StudioCardSurface,
                        contentColor = ProcessCyanGuide,
                        edgePadding = 8.dp
                    ) {
                        paletteTabs.forEachIndexed { index, (title, icon) ->
                            Tab(
                                selected = selectedPaletteTab == index,
                                onClick = { selectedPaletteTab = index },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("workbench_tab_$index")
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (selectedPaletteTab) {
                            0 -> GridAndMasterPalette(
                                state = spreadState,
                                isQuark = isQuark,
                                onUpdate = onUpdateSpread
                            )
                            1 -> TypographyAndLeadingPalette(
                                state = spreadState,
                                onUpdate = onUpdateSpread
                            )
                            2 -> FramesAndTextWrapPalette(
                                state = spreadState,
                                isQuark = isQuark,
                                onUpdate = onUpdateSpread
                            )
                            3 -> PreflightAndOutputPalette(
                                state = spreadState,
                                isQuark = isQuark,
                                onUpdate = onUpdateSpread,
                                onOpenSaveDialog = { showSaveDialog = true }
                            )
                        }
                    }
                }
            }
        }

        // Saved Custom Spreads Portfolio (Room Database)
        if (savedSpreads.isNotEmpty()) {
            item {
                Text(
                    text = "Saved Portfolio Spreads (${savedSpreads.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(savedSpreads, key = { it.id }) { saved ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StudioBorderSubtle, RoundedCornerShape(12.dp))
                        .clickable { onLoadSavedSpread(saved) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = saved.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "${saved.softwareMode} • ${saved.columns} Col • G:${DtpCurriculum.formatPicas(saved.gutterPicas)} • ${saved.bodySizePt}/${saved.leadingPt}pt • ${saved.colorSpace} ${saved.imagePpi}PPI",
                                style = MaterialTheme.typography.labelSmall,
                                color = ProcessCyanGuide
                            )
                        }
                        IconButton(onClick = { onDeleteSavedSpread(saved.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete saved spread",
                                tint = BleedAlertRed
                            )
                        }
                    }
                }
            }
        }
    }

    // Press Check Evaluation Dialog
    pressCheckDialogResult?.let { (score, stars, passed) ->
        AlertDialog(
            onDismissRequest = { pressCheckDialogResult = null },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = if (passed) "Press Check Approved! ($stars ★)" else "Preflight Rejected by Press",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (passed) PreflightPassGreen else BleedAlertRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Layout & Prepress Score: $score / 100",
                        style = MaterialTheme.typography.titleMedium,
                        color = ProcessYellowGold
                    )
                    objectiveChecks.forEach { check ->
                        Text(
                            text = "${if (check.isMet) "✓" else "✗"} ${check.label} (${check.technicalDetail})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (check.isMet) PreflightPassGreen else Color.White
                        )
                    }
                    if (spreadState.preflightIssues.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Active Preflight Warnings:",
                            style = MaterialTheme.typography.labelLarge,
                            color = BleedAlertRed
                        )
                        spreadState.preflightIssues.forEach { issue ->
                            Text(
                                text = "• $issue",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFFB4B4)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pressCheckDialogResult = null
                        val currentId = activeMission?.id ?: 1
                        if (passed && currentId < DtpCurriculum.missions.size) {
                            onSelectMission(currentId + 1)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Text(
                        text = if (passed && (activeMission?.id ?: 6) < 6) {
                            "Next Mission →"
                        } else {
                            "Continue Editing"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pressCheckDialogResult = null }) {
                    Text("Close", color = TextSecondaryMuted)
                }
            }
        )
    }

    // Save Spread to Portfolio Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = "Save Spread to Local Portfolio",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Archive this ${spreadState.softwareMode.displayName} spread configuration to your local Room database:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                    OutlinedTextField(
                        value = customSaveTitle,
                        onValueChange = { customSaveTitle = it },
                        label = { Text("Publication Spread Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveSpread(customSaveTitle)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Icon(Icons.Default.BookmarkAdded, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Spread")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
private fun GridAndMasterPalette(
    state: SpreadLayoutState,
    isQuark: Boolean,
    onUpdate: ((SpreadLayoutState) -> SpreadLayoutState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isQuark) {
                "QuarkXPress Page > Master Guides & Grid"
            } else {
                "InDesign Layout > Margins and Columns"
            },
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )

        // Columns Selector (1 to 4 Columns)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Columns: ${state.columns}",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1, 2, 3, 4).forEach { col ->
                    FilterChip(
                        selected = state.columns == col,
                        onClick = { onUpdate { it.copy(columns = col) } },
                        label = { Text("$col Col") },
                        modifier = Modifier.testTag("set_columns_$col")
                    )
                }
            }
        }

        // Gutter Width in Picas (0p6 to 2p6)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Column Gutter (Picas & Points)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${DtpCurriculum.formatPicas(state.gutterPicas)} (${(state.gutterPicas * 12).toInt()}pt)",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessCyanGuide
                )
            }
            Slider(
                value = state.gutterPicas,
                onValueChange = { raw ->
                    val snapped = kotlin.math.round(raw * 2f) / 2f
                    onUpdate { it.copy(gutterPicas = snapped) }
                },
                valueRange = 0.5f..2.5f,
                colors = SliderDefaults.colors(thumbColor = ProcessCyanGuide, activeTrackColor = ProcessCyanGuide)
            )
        }

        // Page Margins in Picas (1p6 to 4p6)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Page Margins (Magenta Guide)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${DtpCurriculum.formatPicas(state.marginPicas)} (${(state.marginPicas * 12).toInt()}pt)",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessMagentaGuide
                )
            }
            Slider(
                value = state.marginPicas,
                onValueChange = { raw ->
                    val snapped = kotlin.math.round(raw * 2f) / 2f
                    onUpdate { it.copy(marginPicas = snapped) }
                },
                valueRange = 1.5f..4.5f,
                colors = SliderDefaults.colors(thumbColor = ProcessMagentaGuide, activeTrackColor = ProcessMagentaGuide)
            )
        }

        // Master Page & Baseline Grid Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Apply ${state.softwareMode.masterPageTerm}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Adds running header & automatic folio <04>",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = state.masterPageApplied,
                onCheckedChange = { checked -> onUpdate { it.copy(masterPageApplied = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessMagentaGuide)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Lock Paragraphs to Baseline Grid",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Aligns text lines horizontally across all columns",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = state.baselineGridSnap,
                onCheckedChange = { checked -> onUpdate { it.copy(baselineGridSnap = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessCyanGuide)
            )
        }
    }
}

@Composable
private fun TypographyAndLeadingPalette(
    state: SpreadLayoutState,
    onUpdate: ((SpreadLayoutState) -> SpreadLayoutState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Character & Paragraph Specifications",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessYellowGold
        )

        // Headline Font Family Selector
        Text(
            text = "Headline Typeface:",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondaryMuted
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditorialFontChoice.entries.forEach { font ->
                FilterChip(
                    selected = state.headlineFont == font,
                    onClick = { onUpdate { it.copy(headlineFont = font) } },
                    label = {
                        Text(
                            text = font.label,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }

        // Headline Point Size (18pt to 34pt)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Headline Point Size", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("${state.headlineSizePt} pt", style = MaterialTheme.typography.labelLarge, color = ProcessYellowGold)
            }
            Slider(
                value = state.headlineSizePt.toFloat(),
                onValueChange = { v -> onUpdate { it.copy(headlineSizePt = v.toInt()) } },
                valueRange = 18f..34f
            )
        }

        // Body Copy Size / Leading ("11/14 pt")
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Body Size / Leading Ratio", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(
                    text = "${state.bodySizePt} / ${state.leadingPt} pt",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (state.leadingPt >= state.bodySizePt + 2) PreflightPassGreen else BleedAlertRed
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Body Size (${state.bodySizePt}pt)", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
                    Slider(
                        value = state.bodySizePt.toFloat(),
                        onValueChange = { v -> onUpdate { it.copy(bodySizePt = v.toInt()) } },
                        valueRange = 9f..14f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Leading (${state.leadingPt}pt)", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
                    Slider(
                        value = state.leadingPt.toFloat(),
                        onValueChange = { v -> onUpdate { it.copy(leadingPt = v.toInt()) } },
                        valueRange = 10f..20f
                    )
                }
            }
        }

        // Tracking (-30 to +40) & Drop Cap Lines (1 to 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tracking: ${state.tracking} em/1000",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
                Slider(
                    value = state.tracking.toFloat(),
                    onValueChange = { v ->
                        val snapped = (v.toInt() / 5) * 5
                        onUpdate { it.copy(tracking = snapped) }
                    },
                    valueRange = -30f..40f
                )
            }
            Column {
                Text(
                    text = "Drop Cap Lines",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1, 2, 3, 4).forEach { lines ->
                        FilterChip(
                            selected = state.dropCapLines == lines,
                            onClick = { onUpdate { it.copy(dropCapLines = lines) } },
                            label = { Text("${lines}L") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FramesAndTextWrapPalette(
    state: SpreadLayoutState,
    isQuark: Boolean,
    onUpdate: ((SpreadLayoutState) -> SpreadLayoutState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Story Threading & ${state.softwareMode.wrapMenuName}",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )

        // Story Threading Toggle (Fixes Overset Text [+])
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .border(
                    width = 1.dp,
                    color = if (state.hasOversetText) BleedAlertRed else PreflightPassGreen,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isQuark) {
                        "Link Text Boxes (Quark Linking Chain Tool)"
                    } else {
                        "Thread Story Out-Port [+] Across Columns"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = if (state.hasOversetText) {
                        "ALERT: Red [+] Overset Text detected! Enable threading or reduce type size."
                    } else {
                        "Story flows cleanly across all ${state.columns} columns with 0 overset characters."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.hasOversetText) BleedAlertRed else PreflightPassGreen
                )
            }
            Switch(
                checked = state.linkedFrames,
                onCheckedChange = { linked -> onUpdate { it.copy(linkedFrames = linked) } },
                modifier = Modifier.testTag("toggle_story_threading_switch")
            )
        }

        // Text Wrap / Item Runaround Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isQuark) "Enable Item Runaround (Cmd+T)" else "Enable Bounding-Box Text Wrap",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Repels column text away from the placed hero graphic frame",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = state.textWrapEnabled,
                onCheckedChange = { wrap -> onUpdate { it.copy(textWrapEnabled = wrap) } },
                modifier = Modifier.testTag("toggle_text_wrap_switch")
            )
        }

        // Text Wrap Offset Slider (0p0 to 2p0)
        AnimatedVisibility(visible = state.textWrapEnabled) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isQuark) "Runaround Outset Cushion" else "Text Wrap Offset Cushion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${DtpCurriculum.formatPicas(state.textWrapOffsetPicas)} (${(state.textWrapOffsetPicas * 12).toInt()}pt)",
                        style = MaterialTheme.typography.labelLarge,
                        color = ProcessYellowGold
                    )
                }
                Slider(
                    value = state.textWrapOffsetPicas,
                    onValueChange = { raw ->
                        val snapped = kotlin.math.round(raw * 2f) / 2f
                        onUpdate { it.copy(textWrapOffsetPicas = snapped) }
                    },
                    valueRange = 0.0f..2.5f
                )
            }
        }
    }
}

@Composable
private fun PreflightAndOutputPalette(
    state: SpreadLayoutState,
    isQuark: Boolean,
    onUpdate: ((SpreadLayoutState) -> SpreadLayoutState) -> Unit,
    onOpenSaveDialog: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Preflight & ${state.softwareMode.packageMenuName}",
                style = MaterialTheme.typography.titleMedium,
                color = if (state.preflightIssues.isEmpty()) PreflightPassGreen else BleedAlertRed
            )
            Text(
                text = "Score: ${state.preflightScore}/100",
                style = MaterialTheme.typography.labelLarge,
                color = ProcessYellowGold
            )
        }

        // Color Space Switch (CMYK vs RGB)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Document Color Plates", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("Offset lithography requires 4-color Process CMYK", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CMYK", "RGB").forEach { space ->
                    FilterChip(
                        selected = state.colorSpace == space,
                        onClick = { onUpdate { it.copy(colorSpace = space) } },
                        label = { Text(space) }
                    )
                }
            }
        }

        // Placed Image Effective PPI (300 PPI Print TIFF vs 72 PPI Web JPEG)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Linked Graphic Effective PPI (${state.softwareMode.placeShortcut})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text("Relink high-res 300 PPI asset in Links palette", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(300, 72).forEach { ppi ->
                    FilterChip(
                        selected = state.imagePpi == ppi,
                        onClick = { onUpdate { it.copy(imagePpi = ppi) } },
                        label = { Text("$ppi PPI") }
                    )
                }
            }
        }

        // Bleed Settings (0mm, 3mm, 5mm)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Document Bleed (Red Outer Guide)", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text("Standard trim bleed is 3mm (0.125 in)", style = MaterialTheme.typography.labelSmall, color = TextSecondaryMuted)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0, 3, 5).forEach { mm ->
                    FilterChip(
                        selected = state.bleedMm == mm,
                        onClick = { onUpdate { it.copy(bleedMm = mm) } },
                        label = { Text("${mm}mm") }
                    )
                }
            }
        }

        // Preflight Issues Summary Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (state.preflightIssues.isEmpty()) {
                Text(
                    text = "✓ No Preflight Errors. Separations, fonts, links, and bleed are ready for commercial printing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PreflightPassGreen
                )
            } else {
                state.preflightIssues.forEach { err ->
                    Text(
                        text = "• $err",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BleedAlertRed
                    )
                }
            }
        }

        Button(
            onClick = onOpenSaveDialog,
            colors = ButtonDefaults.buttonColors(
                containerColor = ProcessYellowGold,
                contentColor = ObsidianPasteboard
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("save_spread_portfolio_button")
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isQuark) "Collect for Output & Save to Portfolio" else "Package Spread & Save to Portfolio",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
