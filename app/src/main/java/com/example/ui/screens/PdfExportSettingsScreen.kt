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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DtpSoftwareMode
import com.example.model.PdfExportEngine
import com.example.model.PdfExportTab
import com.example.model.PdfPresetOption
import com.example.ui.components.CompressionExportPanel
import com.example.ui.components.GeneralExportPanel
import com.example.ui.components.MarksAndBleedExportPanel
import com.example.ui.components.OutputColorExportPanel
import com.example.ui.components.PdfExportPreviewCanvas
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
fun PdfExportSettingsScreen(
    softwareMode: DtpSoftwareMode,
    onToggleSoftwareMode: () -> Unit,
    onRecordExportScore: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val briefs = PdfExportEngine.printBriefs
    var selectedBriefIndex by remember { mutableIntStateOf(0) }
    val activeBrief = briefs[selectedBriefIndex]

    var exportState by remember(selectedBriefIndex) { mutableStateOf(activeBrief.startingState) }
    var activeTab by remember { mutableStateOf(PdfExportTab.GENERAL) }
    var showVendorSpec by remember { mutableStateOf(true) }
    var showHint by remember(selectedBriefIndex) { mutableStateOf(false) }
    var showExportResultDialog by remember { mutableStateOf(false) }
    var certifiedBriefIds by remember { mutableStateOf(setOf<Int>()) }

    val evaluation = remember(activeBrief, exportState) {
        PdfExportEngine.evaluate(activeBrief, exportState)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("pdf_export_settings_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Commercial Print Brief Selector Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Print-Ready PDF Export Lab",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Configure real export dialogs for commercial offset & wide-format RIPs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Surface(
                        color = PreflightPassGreen.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${certifiedBriefIds.size}/${briefs.size} Certified",
                            style = MaterialTheme.typography.labelMedium,
                            color = PreflightPassGreen,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    briefs.forEachIndexed { idx, brief ->
                        val isCertified = certifiedBriefIds.contains(brief.id)
                        FilterChip(
                            selected = selectedBriefIndex == idx,
                            onClick = { selectedBriefIndex = idx },
                            leadingIcon = if (isCertified) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Certified",
                                        tint = PreflightPassGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            label = {
                                Text(
                                    text = brief.code,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier.testTag("export_brief_chip_${brief.id}")
                        )
                    }
                }
            }
        }

        // 2. Printer Spec Sheet Card (Collapsible)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVendorSpec = !showVendorSpec }
                            .testTag("export_toggle_spec_sheet"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = "Print Shop Brief",
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = activeBrief.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Client: ${activeBrief.clientName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProcessCyanGuide
                                )
                            }
                        }
                        Text(
                            text = if (showVendorSpec) "Hide Spec" else "Show Spec",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessYellowGold
                        )
                    }

                    AnimatedVisibility(visible = showVendorSpec) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = activeBrief.pressSpecSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD5DCF0)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            activeBrief.vendorRequirements.forEach { req ->
                                Text(
                                    text = "• $req",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = showHint) {
                        Surface(
                            color = ProcessYellowGold.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Hint",
                                    tint = ProcessYellowGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeBrief.hintText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ProcessYellowGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Press Sheet & Marks Preview Canvas
        item {
            PdfExportPreviewCanvas(
                state = exportState,
                jobCode = activeBrief.code
            )
        }

        // 4. Simulated Desktop Export Dialog Window (InDesign / QuarkXPress Chrome)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (softwareMode == DtpSoftwareMode.INDESIGN) {
                            InDesignMagenta.copy(alpha = 0.55f)
                        } else {
                            QuarkClassicTeal.copy(alpha = 0.55f)
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Simulated Window Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(BleedAlertRed))
                                Box(Modifier.size(8.dp).clip(CircleShape).background(ProcessYellowGold))
                                Box(Modifier.size(8.dp).clip(CircleShape).background(PreflightPassGreen))
                            }
                            Text(
                                text = if (softwareMode == DtpSoftwareMode.INDESIGN) {
                                    "Export Adobe PDF"
                                } else {
                                    "Export Layout as PDF"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = if (softwareMode == DtpSoftwareMode.INDESIGN) {
                                InDesignMagenta.copy(alpha = 0.22f)
                            } else {
                                QuarkClassicTeal.copy(alpha = 0.22f)
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .clickable { onToggleSoftwareMode() }
                                .testTag("export_toggle_software_mode")
                        ) {
                            Text(
                                text = if (softwareMode == DtpSoftwareMode.INDESIGN) "Id Mode ⇄" else "QXP Mode ⇄",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Preset Selector Strip (Just like Adobe PDF Preset dropdown)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ObsidianPasteboard.copy(alpha = 0.65f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (softwareMode == DtpSoftwareMode.INDESIGN) "Adobe PDF Preset:" else "PDF Output Style:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = buildString {
                                    append(exportState.basePreset.displayName)
                                    if (exportState.isModifiedFromPreset) append(" (Modified)")
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = ProcessCyanGuide,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PdfPresetOption.entries.forEach { preset ->
                                FilterChip(
                                    selected = exportState.basePreset == preset,
                                    onClick = { exportState = PdfExportEngine.settingsForPreset(preset) },
                                    label = {
                                        Text(
                                            text = preset.displayName.removeSurrounding("[", "]"),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.2f),
                                        selectedLabelColor = ProcessCyanGuide
                                    ),
                                    modifier = Modifier.testTag("export_preset_${preset.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Dialog Category Tabs with Live Readiness Status Dots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PdfExportTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            val hasFail = evaluation.tabHasFailures(tab)
                            val allPass = evaluation.tabAllPassed(tab)
                            val tabTitle = if (softwareMode == DtpSoftwareMode.INDESIGN) tab.inDesignTitle else tab.quarkTitle

                            Surface(
                                color = if (selected) ProcessMagentaGuide.copy(alpha = 0.24f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) ProcessMagentaGuide else StudioBorderSubtle,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { activeTab = tab }
                                    .testTag("export_dialog_tab_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    allPass -> PreflightPassGreen
                                                    hasFail -> BleedAlertRed
                                                    else -> TextSecondaryMuted
                                                }
                                            )
                                    )
                                    Text(
                                        text = tabTitle,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else TextSecondaryMuted,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Active Export Dialog Settings Panel
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        when (activeTab) {
                            PdfExportTab.GENERAL -> GeneralExportPanel(
                                state = exportState,
                                softwareMode = softwareMode,
                                onUpdateState = { exportState = it }
                            )
                            PdfExportTab.COMPRESSION -> CompressionExportPanel(
                                state = exportState,
                                softwareMode = softwareMode,
                                onUpdateState = { exportState = it }
                            )
                            PdfExportTab.MARKS_BLEED -> MarksAndBleedExportPanel(
                                state = exportState,
                                softwareMode = softwareMode,
                                onUpdateState = { exportState = it }
                            )
                            PdfExportTab.OUTPUT -> OutputColorExportPanel(
                                state = exportState,
                                softwareMode = softwareMode,
                                onUpdateState = { exportState = it }
                            )
                        }
                    }

                    // Simulated Export Dialog Action Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { exportState = activeBrief.startingState },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("export_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Export Dialog",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = { showHint = !showHint },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("export_hint_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Toggle Spec Hint",
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hint", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                val passedCount = evaluation.checks.count { it.passed }
                                if (evaluation.ripPassed) {
                                    certifiedBriefIds = certifiedBriefIds + activeBrief.id
                                }
                                onRecordExportScore(
                                    "PDF Export: ${activeBrief.code}",
                                    passedCount,
                                    evaluation.checks.size
                                )
                                showExportResultDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (evaluation.ripPassed) PreflightPassGreen else ProcessCyanGuide,
                                contentColor = ObsidianPasteboard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_pdf_submit_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Export Print-Ready PDF",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Export PDF (${evaluation.scorePercent}%)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // RIP Verification & Export Report Dialog
    if (showExportResultDialog) {
        AlertDialog(
            onDismissRequest = { showExportResultDialog = false },
            containerColor = StudioCardSurface,
            icon = {
                Icon(
                    imageVector = if (evaluation.ripPassed) Icons.Default.Verified else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (evaluation.ripPassed) PreflightPassGreen else ProcessYellowGold,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = evaluation.headlineVerdict,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (evaluation.ripPassed) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "RIP Score: ${evaluation.scorePercent}% • Est. Output: ${exportState.estimatedFileSizeMb} MB",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )

                    evaluation.checks.forEach { check ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (check.passed) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (check.passed) PreflightPassGreen else BleedAlertRed,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(15.dp)
                            )
                            Column {
                                Text(
                                    text = check.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = check.detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }

                    // Mini RIP Console Log
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianPasteboard)
                            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        evaluation.ripLogLines.take(6).forEach { line ->
                            Text(
                                text = line,
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace,
                                color = ProcessCyanGuide.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (evaluation.ripPassed && selectedBriefIndex < briefs.lastIndex) {
                    Button(
                        onClick = {
                            showExportResultDialog = false
                            selectedBriefIndex += 1
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PreflightPassGreen,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier.testTag("export_dialog_next_brief")
                    ) {
                        Text("Next Print Brief")
                    }
                } else {
                    Button(
                        onClick = { showExportResultDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier.testTag("export_dialog_continue")
                    ) {
                        Text(if (evaluation.ripPassed) "Done" else "Fix Settings")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportResultDialog = false }) {
                    Text("Close", color = TextSecondaryMuted)
                }
            }
        )
    }
}
