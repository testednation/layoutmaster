package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.ViewAgenda
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.model.DtpSoftwareMode
import com.example.model.FolioNumberStyle
import com.example.model.FolioPlacement
import com.example.model.HeaderAlignment
import com.example.model.MasterId
import com.example.model.MasterPageFormatter
import com.example.model.MasterTemplateSpec
import com.example.model.SimulatedDocPage
import com.example.ui.components.MasterPropagationSpreadsGrid
import com.example.ui.components.PagesPanelStrip
import com.example.ui.components.accentColor
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
fun MasterPagesSimulatorScreen(
    softwareMode: DtpSoftwareMode,
    onToggleSoftwareMode: () -> Unit,
    onRecordMasterAchievement: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var masterA by remember { mutableStateOf(MasterPageFormatter.defaultMasterA()) }
    var masterB by remember { mutableStateOf(MasterPageFormatter.defaultMasterB()) }
    var docPages by remember { mutableStateOf(MasterPageFormatter.initialDocumentPages()) }
    var editingMasterId by remember { mutableStateOf(MasterId.A_MASTER) }
    var selectedPageIndex by remember { mutableIntStateOf(0) }
    var startingPageNumber by remember { mutableFloatStateOf(1f) }
    var editorTab by remember { mutableIntStateOf(0) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    val isQuark = softwareMode == DtpSoftwareMode.QUARKXPRESS
    val activeMasterSpec = if (editingMasterId == MasterId.B_MASTER) masterB else masterA
    val safeSelectedIdx = selectedPageIndex.coerceIn(0, docPages.lastIndex)
    val selectedPage = docPages[safeSelectedIdx]

    fun updateActiveMaster(transform: (MasterTemplateSpec) -> MasterTemplateSpec) {
        if (editingMasterId == MasterId.B_MASTER) {
            masterB = transform(masterB)
        } else {
            masterA = transform(masterA)
        }
    }

    // Interactive Master Pages Learning Objectives
    val headerCustomized = masterA.headerText.trim() != "SWISS DESIGN QUARTERLY" ||
        masterA.footerText.trim() != "ZÜRICH PRESS • PRINT EDITION"
    val coverCleanedWithNone = docPages.firstOrNull()?.assignedMaster == MasterId.NONE
    val multiMasterApplied = docPages.any { it.assignedMaster == MasterId.B_MASTER } &&
        docPages.any { it.assignedMaster == MasterId.A_MASTER }
    val folioOrOverrideTested = startingPageNumber.toInt() > 1 ||
        masterA.folioStyle != FolioNumberStyle.ZERO_PADDED ||
        docPages.any { it.overriddenHeaderText != null }

    val checks = listOf(
        Triple(
            "Edit A-Master Header or Footer text",
            headerCustomized,
            "Propagates live to all A-Master pages"
        ),
        Triple(
            "Assign [None] to Page 1 (Front Cover)",
            coverCleanedWithNone,
            "Covers shouldn't show running headers/folios"
        ),
        Triple(
            "Apply B-Master to a Chapter Opener page",
            multiMasterApplied,
            "Mix A-Master & B-Master in one document"
        ),
        Triple(
            "Change Auto-Folio <#> Style, Start Page, or Local Override",
            folioOrOverrideTested,
            "Tests Numbering Options or Cmd+Shift-Click"
        )
    )
    val passedChecks = checks.count { it.second }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("master_pages_simulator_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isQuark) "QuarkXPress Master Pages" else "InDesign Parent / Master Pages",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Edit a Master template once — watch headers, footers, and automatic <#> page numbers propagate across every spread",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = onToggleSoftwareMode,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isQuark) QuarkClassicTeal.copy(alpha = 0.2f) else InDesignMagenta.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("master_mode_toggle_btn")
                ) {
                    Text(
                        text = if (isQuark) "QXP ↔ Id" else "Id ↔ QXP",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Interactive Pages Panel (Upper Master Dock + Lower Document Pages Strip)
        item {
            PagesPanelStrip(
                softwareMode = softwareMode,
                editingMaster = editingMasterId,
                pages = docPages,
                selectedPageIndex = safeSelectedIdx,
                startingPageNumber = startingPageNumber.toInt(),
                onSelectEditingMaster = { editingMasterId = it },
                onSelectDocPage = { selectedPageIndex = it },
                onAddSpread = {
                    if (docPages.size < 10) {
                        val nextIdx = docPages.size
                        docPages = docPages + listOf(
                            SimulatedDocPage(
                                pageIndex = nextIdx,
                                contentTitle = "ADDED VERSO PAGE ${nextIdx + 1}",
                                contentSnippet = "Newly inserted left-hand page automatically inherits ${MasterId.A_MASTER.labelFor(softwareMode)} headers and auto-folios.",
                                assignedMaster = MasterId.A_MASTER
                            ),
                            SimulatedDocPage(
                                pageIndex = nextIdx + 1,
                                contentTitle = "ADDED RECTO PAGE ${nextIdx + 2}",
                                contentSnippet = "Newly inserted right-hand page mirrors outside-edge page numbering automatically.",
                                assignedMaster = MasterId.A_MASTER
                            )
                        )
                    }
                },
                onRemoveSpread = {
                    if (docPages.size > 4) {
                        docPages = docPages.dropLast(2)
                        selectedPageIndex = selectedPageIndex.coerceAtMost(docPages.lastIndex)
                    }
                }
            )
        }

        // Master Template Live Editor Card (Controls that immediately propagate to all pages)
        item {
            val activeAccent = activeMasterSpec.id.accentColor()
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.4.dp, activeAccent.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .testTag("master_template_editor_card")
            ) {
                Column {
                    // Editor Header showing which Master is currently being edited
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(activeAccent.copy(alpha = 0.14f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = activeAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "EDITING ${activeMasterSpec.id.labelFor(softwareMode).uppercase()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Live changes propagate to ${docPages.count { it.assignedMaster == activeMasterSpec.id }} document page(s)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = activeAccent
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(MasterId.A_MASTER, MasterId.B_MASTER).forEach { m ->
                                FilterChip(
                                    selected = editingMasterId == m,
                                    onClick = { editingMasterId = m },
                                    label = { Text(m.shortBadge, style = MaterialTheme.typography.labelMedium) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = m.accentColor().copy(alpha = 0.3f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // 3 Sub-Tabs for Master Editing: Header/Footer | Auto-Folio <#> | Guides & Columns
                    val editorTabs = listOf(
                        "1. Header & Footer" to Icons.Default.ViewAgenda,
                        "2. Auto-Folio <#>" to Icons.Default.FormatListNumbered,
                        "3. Guides & Grid" to Icons.Default.GridOn
                    )
                    ScrollableTabRow(
                        selectedTabIndex = editorTab,
                        containerColor = StudioDockSurface,
                        contentColor = ProcessCyanGuide,
                        edgePadding = 8.dp
                    ) {
                        editorTabs.forEachIndexed { idx, (label, icon) ->
                            Tab(
                                selected = editorTab == idx,
                                onClick = { editorTab = idx },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Text(label, style = MaterialTheme.typography.labelLarge)
                                    }
                                },
                                modifier = Modifier.testTag("master_editor_tab_$idx")
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (editorTab) {
                            0 -> MasterHeaderFooterEditor(
                                spec = activeMasterSpec,
                                onUpdate = ::updateActiveMaster
                            )
                            1 -> MasterAutoFolioEditor(
                                spec = activeMasterSpec,
                                startingPageNumber = startingPageNumber,
                                softwareMode = softwareMode,
                                onStartingPageNumberChange = { startingPageNumber = it },
                                onUpdate = ::updateActiveMaster
                            )
                            2 -> MasterGuidesAndColumnsEditor(
                                spec = activeMasterSpec,
                                onUpdate = ::updateActiveMaster
                            )
                        }
                    }
                }
            }
        }

        // Selected Document Page Inspector (Assign Master A/B/None & Local Override Cmd+Shift-Click)
        item {
            SelectedDocumentPageInspector(
                page = selectedPage,
                displayPageNumber = startingPageNumber.toInt() + selectedPage.pageIndex,
                softwareMode = softwareMode,
                onAssignMaster = { newMaster ->
                    docPages = docPages.map {
                        if (it.pageIndex == selectedPage.pageIndex) {
                            it.copy(assignedMaster = newMaster, overriddenHeaderText = null)
                        } else {
                            it
                        }
                    }
                },
                onToggleLocalOverride = { overrideText ->
                    docPages = docPages.map {
                        if (it.pageIndex == selectedPage.pageIndex) {
                            it.copy(overriddenHeaderText = overrideText)
                        } else {
                            it
                        }
                    }
                }
            )
        }

        // Live Multi-Page Facing-Spreads Document View (Shows Instant Propagation!)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Multi-Page Document Spreads",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Tap any simulated page below to change its Master or test a local override",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }
                TextButton(
                    onClick = {
                        masterA = MasterPageFormatter.defaultMasterA()
                        masterB = MasterPageFormatter.defaultMasterB()
                        docPages = MasterPageFormatter.initialDocumentPages()
                        startingPageNumber = 1f
                        selectedPageIndex = 0
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Document", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        item {
            MasterPropagationSpreadsGrid(
                pages = docPages,
                masterA = masterA,
                masterB = masterB,
                startingPageNumber = startingPageNumber.toInt(),
                selectedPageIndex = safeSelectedIdx,
                onSelectPage = { selectedPageIndex = it }
            )
        }

        // Interactive Master Pages Lab Checklist Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (passedChecks == checks.size) PreflightPassGreen else StudioBorderSubtle,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Master Pages Mastery Checklist",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "$passedChecks / ${checks.size} Done",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (passedChecks == checks.size) PreflightPassGreen else ProcessYellowGold
                        )
                    }

                    checks.forEach { (label, met, hint) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (met) PreflightPassGreen else TextSecondaryMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (met) PreflightPassGreen else Color.White,
                                        fontWeight = if (met) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = hint,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onRecordMasterAchievement(
                                "Master Pages Lab",
                                passedChecks,
                                checks.size
                            )
                            showCompletionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (passedChecks == checks.size) PreflightPassGreen else ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("complete_master_pages_lab_button")
                    ) {
                        Text(
                            text = if (passedChecks == checks.size) {
                                "All 4 Master Milestones Complete! Save Achievement"
                            } else {
                                "Record Master Pages Progress ($passedChecks/${checks.size})"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = if (passedChecks == checks.size) {
                        "Master Pages Architecture Approved!"
                    } else {
                        "Master Pages Progress Saved ($passedChecks/${checks.size})"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (passedChecks == checks.size) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Text(
                    text = if (passedChecks == checks.size) {
                        "You mastered repeating running headers, footers, automatic <#> folio numbering, multi-master assignment (A-Master, B-Master, [None]), and local page overrides!"
                    } else {
                        "Try assigning [None] to Page 1 (Cover), B-Master to Page 4 (Chapter Opener), editing A-Master's running header, and adjusting the starting folio number."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCompletionDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Text("Continue Experimenting")
                }
            }
        )
    }
}

@Composable
private fun MasterHeaderFooterEditor(
    spec: MasterTemplateSpec,
    onUpdate: ((MasterTemplateSpec) -> MasterTemplateSpec) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Repeating Running Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Repeating Running Header",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Appears at the top of every page linked to this master",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = spec.showHeader,
                onCheckedChange = { checked -> onUpdate { it.copy(showHeader = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessMagentaGuide),
                modifier = Modifier.testTag("toggle_master_header_switch")
            )
        }

        AnimatedVisibility(visible = spec.showHeader) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = spec.headerText,
                    onValueChange = { txt -> onUpdate { it.copy(headerText = txt) } },
                    label = { Text("Master Header Text") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProcessCyanGuide,
                        unfocusedBorderColor = StudioBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("master_header_text_input")
                )

                Text(
                    text = "Header Alignment Across Facing Spreads:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryMuted
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HeaderAlignment.entries.forEach { align ->
                        FilterChip(
                            selected = spec.headerAlignment == align,
                            onClick = { onUpdate { it.copy(headerAlignment = align) } },
                            label = { Text(align.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Header Hairline Rule (0.5pt divider)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Switch(
                        checked = spec.showHeaderRule,
                        onCheckedChange = { checked -> onUpdate { it.copy(showHeaderRule = checked) } }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(StudioBorderSubtle)
        )

        // Repeating Running Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Repeating Running Footer",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Publication imprint or chapter title at page bottom",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = spec.showFooter,
                onCheckedChange = { checked -> onUpdate { it.copy(showFooter = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessCyanGuide),
                modifier = Modifier.testTag("toggle_master_footer_switch")
            )
        }

        AnimatedVisibility(visible = spec.showFooter) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = spec.footerText,
                    onValueChange = { txt -> onUpdate { it.copy(footerText = txt) } },
                    label = { Text("Master Footer Text") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProcessCyanGuide,
                        unfocusedBorderColor = StudioBorderSubtle,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("master_footer_text_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Footer Hairline Rule",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Switch(
                        checked = spec.showFooterRule,
                        onCheckedChange = { checked -> onUpdate { it.copy(showFooterRule = checked) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun MasterAutoFolioEditor(
    spec: MasterTemplateSpec,
    startingPageNumber: Float,
    softwareMode: DtpSoftwareMode,
    onStartingPageNumberChange: (Float) -> Unit,
    onUpdate: ((MasterTemplateSpec) -> MasterTemplateSpec) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (softwareMode == DtpSoftwareMode.QUARKXPRESS) {
                        "Auto Page Number Marker (<#> via Cmd+3)"
                    } else {
                        "Current Page Number Marker (<A> via Cmd+Opt+Shift+N)"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Automatically calculates each page's folio across the entire book",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = spec.showAutoFolio,
                onCheckedChange = { checked -> onUpdate { it.copy(showAutoFolio = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessYellowGold),
                modifier = Modifier.testTag("toggle_auto_folio_switch")
            )
        }

        AnimatedVisibility(visible = spec.showAutoFolio) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Folio Placement (Mirrored on Verso / Recto):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryMuted
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FolioPlacement.entries.forEach { placement ->
                        FilterChip(
                            selected = spec.folioPlacement == placement,
                            onClick = { onUpdate { it.copy(folioPlacement = placement) } },
                            label = { Text(placement.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Text(
                    text = "Numbering & Section Style:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryMuted
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FolioNumberStyle.entries.forEach { style ->
                        FilterChip(
                            selected = spec.folioStyle == style,
                            onClick = { onUpdate { it.copy(folioStyle = style) } },
                            label = {
                                Text(
                                    text = "${style.label} (${style.example})",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.testTag("folio_style_chip_${style.name}")
                        )
                    }
                }

                // Starting Page Number Slider (Simulates Layout > Numbering & Section Options)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianPasteboard)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Section Start Page Number",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Starts at Page ${startingPageNumber.toInt()}",
                            style = MaterialTheme.typography.labelLarge,
                            color = ProcessYellowGold
                        )
                    }
                    Slider(
                        value = startingPageNumber,
                        onValueChange = { onStartingPageNumberChange(kotlin.math.round(it)) },
                        valueRange = 1f..25f,
                        colors = SliderDefaults.colors(
                            thumbColor = ProcessYellowGold,
                            activeTrackColor = ProcessYellowGold
                        ),
                        modifier = Modifier.testTag("starting_page_number_slider")
                    )
                }
            }
        }
    }
}

@Composable
private fun MasterGuidesAndColumnsEditor(
    spec: MasterTemplateSpec,
    onUpdate: ((MasterTemplateSpec) -> MasterTemplateSpec) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Show Non-Printing Master Guides",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Margin boxes and column guides inherited by linked pages",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = spec.showMasterGuides,
                onCheckedChange = { checked -> onUpdate { it.copy(showMasterGuides = checked) } }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Master Column Grid: ${spec.masterColumns} Col",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1, 2, 3).forEach { cols ->
                    FilterChip(
                        selected = spec.masterColumns == cols,
                        onClick = { onUpdate { it.copy(masterColumns = cols) } },
                        label = { Text("$cols Col") }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Top Color Accent Bleed Band",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Signature golden bar at top of Chapter Opener spreads",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = spec.showAccentBand,
                onCheckedChange = { checked -> onUpdate { it.copy(showAccentBand = checked) } },
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessYellowGold)
            )
        }
    }
}

@Composable
private fun SelectedDocumentPageInspector(
    page: SimulatedDocPage,
    displayPageNumber: Int,
    softwareMode: DtpSoftwareMode,
    onAssignMaster: (MasterId) -> Unit,
    onToggleLocalOverride: (String?) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ProcessCyanGuide.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .testTag("selected_page_inspector_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SELECTED PAGE: PAGE $displayPageNumber (${if (page.pageIndex % 2 == 1) "Verso / Left" else "Recto / Right"})",
                        style = MaterialTheme.typography.labelLarge,
                        color = ProcessCyanGuide,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = page.contentTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Surface(
                    color = page.assignedMaster.accentColor().copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = page.assignedMaster.labelFor(softwareMode),
                        style = MaterialTheme.typography.labelSmall,
                        color = page.assignedMaster.accentColor(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Assign Master Selector Chips for this Page
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Apply Master:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryMuted
                )
                MasterId.entries.forEach { master ->
                    FilterChip(
                        selected = page.assignedMaster == master,
                        onClick = { onAssignMaster(master) },
                        label = {
                            Text(
                                text = master.labelFor(softwareMode),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = master.accentColor().copy(alpha = 0.25f),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("assign_master_${master.name}")
                    )
                }
            }

            // Local Master Item Override Simulation (Cmd+Shift-Click in InDesign)
            if (page.assignedMaster != MasterId.NONE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianPasteboard)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (page.overriddenHeaderText != null) {
                                "Local Override Active (Detached from Master)"
                            } else {
                                "Override Master Header on Page $displayPageNumber Only"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (page.overriddenHeaderText != null) ProcessCyanGuide else Color.White
                        )
                        Text(
                            text = if (softwareMode == DtpSoftwareMode.QUARKXPRESS) {
                                "Edit master item locally without affecting other pages"
                            } else {
                                "Simulates Cmd/Ctrl+Shift+Click to unlock a Parent item"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (page.overriddenHeaderText == null) {
                        FilledTonalButton(
                            onClick = { onToggleLocalOverride("SPECIAL FEATURE • PAGE $displayPageNumber OVERRIDE") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("override_master_item_btn")
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Override", style = MaterialTheme.typography.labelSmall)
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { onToggleLocalOverride(null) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("reapply_master_item_btn")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-Apply", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
