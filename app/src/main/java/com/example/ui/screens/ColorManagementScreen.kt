package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
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
import com.example.model.CmykValues
import com.example.model.ColorManagementEngine
import com.example.model.DtpSoftwareMode
import com.example.model.IccColorProfile
import com.example.model.RenderingIntent
import com.example.model.SwatchCalibrationJob
import com.example.model.WorkingColorModel
import com.example.ui.components.SwatchProofAndBleedCanvas
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
import java.util.Locale

@Composable
fun ColorManagementScreen(
    softwareMode: DtpSoftwareMode,
    onRecordColorCalibration: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val jobs = ColorManagementEngine.calibrationJobs
    var selectedJobIndex by remember { mutableIntStateOf(0) }
    val activeJob = jobs[selectedJobIndex]

    // Live interactive calibration parameters (reset per swatch job)
    var colorModel by remember(selectedJobIndex) { mutableStateOf(activeJob.initialColorModel) }
    var cyan by remember(selectedJobIndex) { mutableFloatStateOf(activeJob.initialCmyk.c.toFloat()) }
    var magenta by remember(selectedJobIndex) { mutableFloatStateOf(activeJob.initialCmyk.m.toFloat()) }
    var yellow by remember(selectedJobIndex) { mutableFloatStateOf(activeJob.initialCmyk.y.toFloat()) }
    var keyBlack by remember(selectedJobIndex) { mutableFloatStateOf(activeJob.initialCmyk.k.toFloat()) }
    var selectedProfile by remember(selectedJobIndex) { mutableStateOf(activeJob.initialProfile) }
    var renderingIntent by remember(selectedJobIndex) { mutableStateOf(RenderingIntent.RELATIVE_COLORIMETRIC) }
    var softProofPaper by remember(selectedJobIndex) { mutableStateOf(true) }
    var bleedMm by remember(selectedJobIndex) { mutableFloatStateOf(activeJob.initialBleedMm.toFloat()) }
    var testGuillotineShift by remember(selectedJobIndex) { mutableStateOf(true) }

    var activeToolTab by remember { mutableIntStateOf(0) }
    var showTargetHint by remember(selectedJobIndex) { mutableStateOf(false) }
    var showProofResultDialog by remember { mutableStateOf(false) }

    val userCmyk = CmykValues(
        c = cyan.toInt(),
        m = magenta.toInt(),
        y = yellow.toInt(),
        k = keyBlack.toInt()
    )

    val deltaE = ColorManagementEngine.computeDeltaE(
        userCmyk = userCmyk,
        userProfile = selectedProfile,
        userColorModel = colorModel,
        targetCmyk = activeJob.targetCmyk,
        targetProfile = activeJob.targetProfile
    )

    val isCmykMode = colorModel == WorkingColorModel.CMYK
    val isProfileMatched = selectedProfile == activeJob.targetProfile
    val isTacSafe = userCmyk.totalInkCoverage <= selectedProfile.maxTacPercent
    val isSwatchCalibrated = deltaE <= 4.0f && isTacSafe
    val isBleedValid = bleedMm.toInt() >= activeJob.requiredBleedMm

    val checks = listOf(
        Triple(
            "Convert Working Space to Subtractive CMYK",
            isCmykMode,
            if (isCmykMode) "CMYK Plates Active" else "Currently RGB (Out of Print Gamut)"
        ),
        Triple(
            "Assign ICC Profile: ${activeJob.targetProfile.displayName}",
            isProfileMatched,
            if (isProfileMatched) "Profile Matched" else "Current: ${selectedProfile.displayName}"
        ),
        Triple(
            "Calibrate Swatch (ΔE ≤ 4.0 & TAC ≤ ${selectedProfile.maxTacPercent}%)",
            isSwatchCalibrated,
            String.format(Locale.US, "ΔE %.1f • TAC %d%%", deltaE, userCmyk.totalInkCoverage)
        ),
        Triple(
            "Set Print Bleed to ${activeJob.requiredBleedMm}mm+",
            isBleedValid,
            "Current Bleed: ${bleedMm.toInt()}mm"
        )
    )
    val passedCount = checks.count { it.second }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("color_management_screen_list"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Color Management & Bleed Lab",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = if (softwareMode == DtpSoftwareMode.QUARKXPRESS) {
                            "QuarkXPress Color Manager: Calibrate CMYK, ICC Profiles & Bleed to match printed proof"
                        } else {
                            "InDesign Color Settings: Calibrate CMYK, ICC Profiles & Bleed to match printed swatch"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                TextButton(
                    onClick = {
                        colorModel = activeJob.initialColorModel
                        cyan = activeJob.initialCmyk.c.toFloat()
                        magenta = activeJob.initialCmyk.m.toFloat()
                        yellow = activeJob.initialCmyk.y.toFloat()
                        keyBlack = activeJob.initialCmyk.k.toFloat()
                        selectedProfile = activeJob.initialProfile
                        bleedMm = activeJob.initialBleedMm.toFloat()
                        showTargetHint = false
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Swatch", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Swatch Job Selector Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jobs.forEachIndexed { idx, job ->
                    FilterChip(
                        selected = selectedJobIndex == idx,
                        onClick = { selectedJobIndex = idx },
                        label = {
                            Text(
                                text = job.code,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                            selectedLabelColor = ProcessCyanGuide
                        ),
                        modifier = Modifier.testTag("color_job_chip_$idx")
                    )
                }
            }
        }

        // Job Brief Card
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
                            text = activeJob.substrateLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = ProcessYellowGold
                        )
                        Text(
                            text = "$passedCount / 4 Calibrated",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (passedCount == 4) PreflightPassGreen else ProcessCyanGuide,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeJob.brief,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD5DCF0)
                    )
                }
            }
        }

        // Live Side-by-Side Swatch Proof & Guillotine Bleed Visualizer
        item {
            SwatchProofAndBleedCanvas(
                job = activeJob,
                userCmyk = userCmyk,
                userProfile = selectedProfile,
                userColorModel = colorModel,
                bleedMm = bleedMm.toInt(),
                softProofPaper = softProofPaper,
                testGuillotineShift = testGuillotineShift,
                deltaE = deltaE
            )
        }

        // 3 Calibration Tool Tabs (CMYK vs RGB | ICC Profiles | Bleed & Trim)
        item {
            val toolTabs = listOf(
                "1. CMYK vs RGB" to Icons.Default.ColorLens,
                "2. ICC Profiles" to Icons.Default.Tune,
                "3. Bleed & Trim" to Icons.Default.ContentCut
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
                        selectedTabIndex = activeToolTab,
                        containerColor = StudioCardSurface,
                        contentColor = ProcessCyanGuide,
                        edgePadding = 8.dp
                    ) {
                        toolTabs.forEachIndexed { idx, (label, icon) ->
                            Tab(
                                selected = activeToolTab == idx,
                                onClick = { activeToolTab = idx },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Text(label, style = MaterialTheme.typography.labelLarge)
                                    }
                                },
                                modifier = Modifier.testTag("color_tool_tab_$idx")
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (activeToolTab) {
                            0 -> CmykVsRgbMixerPanel(
                                activeJob = activeJob,
                                colorModel = colorModel,
                                cyan = cyan,
                                magenta = magenta,
                                yellow = yellow,
                                keyBlack = keyBlack,
                                maxTac = selectedProfile.maxTacPercent,
                                showTargetHint = showTargetHint,
                                onColorModelChange = { colorModel = it },
                                onCyanChange = { cyan = it },
                                onMagentaChange = { magenta = it },
                                onYellowChange = { yellow = it },
                                onKeyBlackChange = { keyBlack = it },
                                onToggleHint = { showTargetHint = !showTargetHint }
                            )

                            1 -> IccProfileCalibrationPanel(
                                selectedProfile = selectedProfile,
                                renderingIntent = renderingIntent,
                                softProofPaper = softProofPaper,
                                onSelectProfile = { selectedProfile = it },
                                onSelectIntent = { renderingIntent = it },
                                onToggleSoftProof = { softProofPaper = it }
                            )

                            2 -> BleedAndTrimSettingsPanel(
                                requiredBleedMm = activeJob.requiredBleedMm,
                                bleedMm = bleedMm,
                                testGuillotineShift = testGuillotineShift,
                                onBleedChange = { bleedMm = it },
                                onToggleGuillotineTest = { testGuillotineShift = it }
                            )
                        }
                    }
                }
            }
        }

        // Live Calibration Checklist & Spectrophotometer Press Check Button
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (passedCount == 4) PreflightPassGreen else StudioBorderSubtle,
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Press Calibration Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    checks.forEach { (title, isMet, detail) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (isMet) PreflightPassGreen else ProcessYellowGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isMet) PreflightPassGreen else Color.White,
                                    fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            onRecordColorCalibration(
                                "Color Lab: ${activeJob.swatchName}",
                                passedCount,
                                4
                            )
                            showProofResultDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (passedCount == 4) PreflightPassGreen else ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_spectro_proof_button")
                    ) {
                        Icon(
                            imageVector = if (passedCount == 4) Icons.Default.Verified else Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (passedCount == 4) {
                                "Spectrophotometer Match Approved! Sign Press Proof"
                            } else {
                                "Run Spectrophotometer Proof Check ($passedCount/4 Ready)"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showProofResultDialog) {
        val allPassed = passedCount == 4
        AlertDialog(
            onDismissRequest = { showProofResultDialog = false },
            containerColor = StudioCardSurface,
            title = {
                Text(
                    text = if (allPassed) {
                        "Press Proof Signed — Color Approved!"
                    } else {
                        "Color Proof Needs Calibration ($passedCount/4)"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (allPassed) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = String.format(
                            Locale.US,
                            "Measured ΔE: %.1f • Plates: %s • TAC: %d%% • Bleed: %dmm",
                            deltaE,
                            colorModel.badge,
                            userCmyk.totalInkCoverage,
                            bleedMm.toInt()
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = ProcessCyanGuide
                    )
                    Text(
                        text = activeJob.lessonTakeaway,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showProofResultDialog = false
                        if (allPassed && selectedJobIndex < jobs.lastIndex) {
                            selectedJobIndex += 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    )
                ) {
                    Text(
                        text = if (allPassed && selectedJobIndex < jobs.lastIndex) {
                            "Next Swatch Job →"
                        } else {
                            "Continue Calibrating"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showProofResultDialog = false }) {
                    Text("Close", color = TextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
private fun CmykVsRgbMixerPanel(
    activeJob: SwatchCalibrationJob,
    colorModel: WorkingColorModel,
    cyan: Float,
    magenta: Float,
    yellow: Float,
    keyBlack: Float,
    maxTac: Int,
    showTargetHint: Boolean,
    onColorModelChange: (WorkingColorModel) -> Unit,
    onCyanChange: (Float) -> Unit,
    onMagentaChange: (Float) -> Unit,
    onYellowChange: (Float) -> Unit,
    onKeyBlackChange: (Float) -> Unit,
    onToggleHint: () -> Unit
) {
    val tac = cyan.toInt() + magenta.toInt() + yellow.toInt() + keyBlack.toInt()
    val tacOver = tac > maxTac

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Working Color Space Switcher (Additive RGB vs Subtractive CMYK)
        Text(
            text = "Working Color Space (Additive Light vs. Subtractive Ink)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WorkingColorModel.entries.forEach { model ->
                val isSelected = colorModel == model
                FilterChip(
                    selected = isSelected,
                    onClick = { onColorModelChange(model) },
                    label = {
                        Text(
                            text = model.label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (model == WorkingColorModel.CMYK) {
                            ProcessCyanGuide.copy(alpha = 0.24f)
                        } else {
                            BleedAlertRed.copy(alpha = 0.22f)
                        },
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("color_space_chip_${model.name}")
                )
            }
        }

        // Short educational callout explaining RGB vs CMYK physics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianPasteboard)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = if (colorModel == WorkingColorModel.CMYK) ProcessCyanGuide else ProcessYellowGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = colorModel.synthesisRule,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD5DCF0)
            )
        }

        // CMYK Ink Plate Sliders
        CmykChannelSlider(
            label = "C • Cyan Plate",
            value = cyan,
            color = ProcessCyanGuide,
            onValueChange = onCyanChange,
            tag = "slider_cmyk_c"
        )
        CmykChannelSlider(
            label = "M • Magenta Plate",
            value = magenta,
            color = ProcessMagentaGuide,
            onValueChange = onMagentaChange,
            tag = "slider_cmyk_m"
        )
        CmykChannelSlider(
            label = "Y • Yellow Plate",
            value = yellow,
            color = ProcessYellowGold,
            onValueChange = onYellowChange,
            tag = "slider_cmyk_y"
        )
        CmykChannelSlider(
            label = "K • Key (Black) Plate",
            value = keyBlack,
            color = Color(0xFFC4CEE3),
            onValueChange = onKeyBlackChange,
            tag = "slider_cmyk_k"
        )

        // Total Ink Coverage Warning + Target Swatch Spec Hint Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (tacOver) {
                    "⚠ TAC $tac% exceeds $maxTac% press limit!"
                } else {
                    "Total Ink Coverage (TAC): $tac% / $maxTac%"
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (tacOver) BleedAlertRed else PreflightPassGreen
            )
            TextButton(
                onClick = onToggleHint,
                modifier = Modifier.testTag("toggle_swatch_hint_button")
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ProcessYellowGold, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (showTargetHint) "Hide Swatch Recipe" else "Swatch Recipe Hint",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProcessYellowGold
                )
            }
        }

        if (showTargetHint) {
            val t = activeJob.targetCmyk
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ProcessYellowGold.copy(alpha = 0.14f))
                    .border(1.dp, ProcessYellowGold, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Press Drawdown Target Recipe: C:${t.c}%  M:${t.m}%  Y:${t.y}%  K:${t.k}%  •  Profile: ${activeJob.targetProfile.displayName}  •  Bleed: ${activeJob.requiredBleedMm}mm",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProcessYellowGold
                )
            }
        }
    }
}

@Composable
private fun CmykChannelSlider(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit,
    tag: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${value.toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = { onValueChange(kotlin.math.round(it)) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun IccProfileCalibrationPanel(
    selectedProfile: IccColorProfile,
    renderingIntent: RenderingIntent,
    softProofPaper: Boolean,
    onSelectProfile: (IccColorProfile) -> Unit,
    onSelectIntent: (RenderingIntent) -> Unit,
    onToggleSoftProof: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "CMYK Output Destination ICC Profile",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessCyanGuide
        )
        Text(
            text = "An ICC profile tells InDesign/QuarkXPress how ink behaves on a specific paper stock (dot gain, white point, and maximum Total Ink Coverage).",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryMuted
        )

        IccColorProfile.entries.forEach { profile ->
            val isSelected = selectedProfile == profile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) ProcessCyanGuide.copy(alpha = 0.16f) else ObsidianPasteboard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) ProcessCyanGuide else StudioBorderSubtle,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectProfile(profile) }
                    .padding(10.dp)
                    .testTag("icc_profile_option_${profile.name}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) ProcessCyanGuide else Color.White
                    )
                    Surface(
                        color = StudioCardSurface,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "MAX TAC ${profile.maxTacPercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessYellowGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = profile.regionAndStock,
                    style = MaterialTheme.typography.labelSmall,
                    color = ProcessMagentaGuide
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = profile.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD5DCF0)
                )
            }
        }

        // Rendering Intent & Soft Proof Paper Simulation
        Text(
            text = "Color Conversion Rendering Intent:",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RenderingIntent.entries.forEach { intent ->
                FilterChip(
                    selected = renderingIntent == intent,
                    onClick = { onSelectIntent(intent) },
                    label = { Text(intent.label, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Text(
            text = renderingIntent.explanation,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryMuted
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Simulate Paper Color (Soft Proof)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Previews paper warmth and ink dot-gain on screen",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted
                )
            }
            Switch(
                checked = softProofPaper,
                onCheckedChange = onToggleSoftProof,
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessCyanGuide)
            )
        }
    }
}

@Composable
private fun BleedAndTrimSettingsPanel(
    requiredBleedMm: Int,
    bleedMm: Float,
    testGuillotineShift: Boolean,
    onBleedChange: (Float) -> Unit,
    onToggleGuillotineTest: (Boolean) -> Unit
) {
    val isSafe = bleedMm.toInt() >= requiredBleedMm
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Document Bleed & Bindery Trim Tolerance",
            style = MaterialTheme.typography.titleMedium,
            color = ProcessYellowGold
        )
        Text(
            text = "Commercial paper guillotines shift ±1.5mm when trimming thick stacks. Extending your swatch artwork to the outer Bleed Guide prevents white paper hairlines.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryMuted
        )

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
                    text = "Document Bleed (All 4 Sides)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "${bleedMm.toInt()} mm (Required: ≥ ${requiredBleedMm}mm)",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSafe) PreflightPassGreen else BleedAlertRed
                )
            }
            Slider(
                value = bleedMm,
                onValueChange = { onBleedChange(kotlin.math.round(it)) },
                valueRange = 0f..6f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = if (isSafe) PreflightPassGreen else BleedAlertRed,
                    activeTrackColor = if (isSafe) PreflightPassGreen else BleedAlertRed
                ),
                modifier = Modifier.testTag("color_lab_bleed_slider")
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 3, 5).forEach { preset ->
                    FilterChip(
                        selected = bleedMm.toInt() == preset,
                        onClick = { onBleedChange(preset.toFloat()) },
                        label = {
                            Text(
                                text = when (preset) {
                                    0 -> "0mm (No Bleed)"
                                    3 -> "3mm (0.125\" Std)"
                                    else -> "5mm (Large Format)"
                                },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }

        // Interactive Bindery Guillotine Shift Test Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .border(
                    width = 1.dp,
                    color = if (testGuillotineShift && !isSafe) BleedAlertRed else StudioBorderSubtle,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Simulate ±1.5mm Bindery Guillotine Shift",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (testGuillotineShift && !isSafe) {
                        "DEFECT VISIBLE ABOVE: Insufficient bleed exposes raw white paper edge!"
                    } else {
                        "Edge trims cleanly inside the extended bleed ink zone."
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (testGuillotineShift && !isSafe) BleedAlertRed else PreflightPassGreen
                )
            }
            Switch(
                checked = testGuillotineShift,
                onCheckedChange = onToggleGuillotineTest,
                colors = SwitchDefaults.colors(checkedThumbColor = ProcessYellowGold),
                modifier = Modifier.testTag("toggle_guillotine_shift_switch")
            )
        }
    }
}
