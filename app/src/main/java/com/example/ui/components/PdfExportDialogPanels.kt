package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DtpSoftwareMode
import com.example.model.ExportColorConversion
import com.example.model.ExportOutputProfile
import com.example.model.ImageCompressionType
import com.example.model.PdfExportSettingsState
import com.example.model.PdfStandard
import com.example.model.ProfileInclusionPolicy
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted
import kotlin.math.roundToInt

@Composable
fun GeneralExportPanel(
    state: PdfExportSettingsState,
    softwareMode: DtpSoftwareMode,
    onUpdateState: (PdfExportSettingsState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DialogSectionHeader(
            title = if (softwareMode == DtpSoftwareMode.INDESIGN) "Standards & Compatibility" else "PDF Verification Standard",
            subtitle = "ISO PDF/X standards lock down fonts, geometry boxes, and color intent for RIPs."
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            PdfStandard.entries.forEach { std ->
                val selected = state.standard == std
                DialogOptionCard(
                    title = std.displayName,
                    subtitle = "${std.acrobatVersion} • ${std.summary}",
                    selected = selected,
                    accent = if (std == PdfStandard.NONE) ProcessYellowGold else ProcessCyanGuide,
                    onClick = { onUpdateState(state.copy(standard = std)) },
                    testTag = "export_standard_${std.name.lowercase()}"
                )
            }
        }

        DialogSectionHeader(
            title = "Pages vs. Spreads",
            subtitle = "Commercial printers impose single pages into printer signatures automatically."
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !state.exportAsSpreads,
                onClick = { onUpdateState(state.copy(exportAsSpreads = false)) },
                label = { Text("Pages (1-Up Print)", style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PreflightPassGreen.copy(alpha = 0.22f),
                    selectedLabelColor = PreflightPassGreen
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("export_mode_single_pages")
            )
            FilterChip(
                selected = state.exportAsSpreads,
                onClick = { onUpdateState(state.copy(exportAsSpreads = true)) },
                label = { Text("Spreads (Reader)", style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ProcessYellowGold.copy(alpha = 0.22f),
                    selectedLabelColor = ProcessYellowGold
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("export_mode_spreads")
            )
        }

        DialogSwitchRow(
            title = "Optimize for Fast Web View",
            subtitle = "Byte-serves pages for browser viewing; unnecessary for press plates.",
            checked = state.optimizeFastWebView,
            onCheckedChange = { onUpdateState(state.copy(optimizeFastWebView = it)) },
            testTag = "export_toggle_fast_web"
        )
    }
}

@Composable
fun CompressionExportPanel(
    state: PdfExportSettingsState,
    softwareMode: DtpSoftwareMode,
    onUpdateState: (PdfExportSettingsState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DialogSectionHeader(
            title = if (softwareMode == DtpSoftwareMode.INDESIGN) "Color Images • Bicubic Downsampling" else "Color Bitmap Downsampling",
            subtitle = "Offset presses use 150–175 LPI halftone screens, requiring 2x resolution (~300 DPI)."
        )

        val dpiOptions = listOf(72, 150, 300, 600)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dpiOptions.forEach { dpi ->
                val selected = state.colorImageDpi == dpi
                val chipColor = when {
                    dpi < 150 -> BleedAlertRed
                    dpi == 300 -> PreflightPassGreen
                    else -> ProcessCyanGuide
                }
                FilterChip(
                    selected = selected,
                    onClick = { onUpdateState(state.copy(colorImageDpi = dpi)) },
                    label = { Text("$dpi DPI", style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = chipColor.copy(alpha = 0.22f),
                        selectedLabelColor = chipColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_dpi_$dpi")
                )
            }
        }

        val dpiHint = when (state.colorImageDpi) {
            72 -> "72 DPI is screen resolution only — printed photos will show jagged pixel blocks."
            150 -> "150 DPI works for draft proofs or wide-format posters viewed from 2+ meters."
            300 -> "300 DPI is the gold standard for coated & uncoated commercial offset printing."
            else -> "600 DPI quadruples file size with minimal visual gain for continuous-tone photos."
        }
        InlineSpecCallout(
            text = dpiHint,
            isWarning = state.colorImageDpi < 150 || state.colorImageDpi > 300
        )

        DialogSectionHeader(
            title = "Image Compression Algorithm",
            subtitle = "Balances file size against halftone edge fidelity."
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ImageCompressionType.entries.forEach { comp ->
                DialogOptionCard(
                    title = comp.displayName,
                    subtitle = comp.qualityNote,
                    selected = state.compressionType == comp,
                    accent = if (comp == ImageCompressionType.JPEG_LOW) BleedAlertRed else ProcessCyanGuide,
                    onClick = { onUpdateState(state.copy(compressionType = comp)) },
                    testTag = "export_comp_${comp.name.lowercase()}"
                )
            }
        }
    }
}

@Composable
fun MarksAndBleedExportPanel(
    state: PdfExportSettingsState,
    softwareMode: DtpSoftwareMode,
    onUpdateState: (PdfExportSettingsState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DialogSectionHeader(
            title = if (softwareMode == DtpSoftwareMode.INDESIGN) "Printer's Marks" else "Registration & Trim Marks",
            subtitle = "Non-printing marks placed outside the TrimBox guide guillotine cutters and press operators."
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianPasteboard)
                .border(1.dp, StudioBorderSubtle, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DialogCheckboxRow(
                label = "Crop Marks (Trim Marks)",
                hint = "Corner hairlines defining final finished trim dimensions",
                checked = state.cropMarks,
                onCheckedChange = { onUpdateState(state.copy(cropMarks = it)) },
                testTag = "export_mark_crop"
            )
            DialogCheckboxRow(
                label = "Bleed Marks",
                hint = "Outer marks defining the extended bleed boundary",
                checked = state.bleedMarks,
                onCheckedChange = { onUpdateState(state.copy(bleedMarks = it)) },
                testTag = "export_mark_bleed"
            )
            DialogCheckboxRow(
                label = "Registration Marks",
                hint = "Bullseye crosshairs used to align C, M, Y, and K plates",
                checked = state.registrationMarks,
                onCheckedChange = { onUpdateState(state.copy(registrationMarks = it)) },
                testTag = "export_mark_registration"
            )
            DialogCheckboxRow(
                label = "Color Bars",
                hint = "CMYK solid & tint wedge checked with a press densitometer",
                checked = state.colorBars,
                onCheckedChange = { onUpdateState(state.copy(colorBars = it)) },
                testTag = "export_mark_colorbars"
            )
            DialogCheckboxRow(
                label = "Page Information",
                hint = "Prints filename, page number, and timestamp in the slug",
                checked = state.pageInformation,
                onCheckedChange = { onUpdateState(state.copy(pageInformation = it)) },
                testTag = "export_mark_pageinfo"
            )
        }

        // Bleed Selector (0 mm / 3 mm / 5 mm)
        DialogSectionHeader(
            title = "Bleed & Slug (Top / Bottom / Inside / Outside)",
            subtitle = "Extends edge artwork past the trim line to prevent white paper slivers."
        )

        val bleedPresets = listOf(0f to "0 mm (None)", 3f to "3.0 mm (Std)", 5f to "5.0 mm (Heavy)")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            bleedPresets.forEach { (mm, label) ->
                val selected = state.bleedMm == mm
                val accent = if (mm == 0f) BleedAlertRed else PreflightPassGreen
                FilterChip(
                    selected = selected,
                    onClick = { onUpdateState(state.copy(bleedMm = mm)) },
                    label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accent.copy(alpha = 0.22f),
                        selectedLabelColor = accent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_bleed_${mm.toInt()}mm")
                )
            }
        }

        // Mark Offset Slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianPasteboard)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Crop Mark Offset",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Text(
                    text = "${state.markOffsetMm} mm",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProcessCyanGuide,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = state.markOffsetMm,
                onValueChange = { raw ->
                    val stepped = (raw * 2f).roundToInt() / 2f
                    onUpdateState(state.copy(markOffsetMm = stepped))
                },
                valueRange = 1.0f..6.0f,
                colors = SliderDefaults.colors(
                    thumbColor = ProcessCyanGuide,
                    activeTrackColor = ProcessCyanGuide
                ),
                modifier = Modifier.testTag("export_mark_offset_slider")
            )
            Text(
                text = "Tip: Keep Offset >= 3.0 mm when using heavy bleeds so crop hairlines never print inside trimmed artwork.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }

        DialogSwitchRow(
            title = "Include Slug Area",
            subtitle = "Retains designer press notes and fold instructions outside the bleed box.",
            checked = state.includeSlugArea,
            onCheckedChange = { onUpdateState(state.copy(includeSlugArea = it)) },
            testTag = "export_toggle_slug"
        )
    }
}

@Composable
fun OutputColorExportPanel(
    state: PdfExportSettingsState,
    softwareMode: DtpSoftwareMode,
    onUpdateState: (PdfExportSettingsState) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DialogSectionHeader(
            title = if (softwareMode == DtpSoftwareMode.INDESIGN) "Color Conversion" else "Color Output Mode",
            subtitle = "Choose how RGB assets and CMYK swatches map into the destination press profile."
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ExportColorConversion.entries.forEach { conv ->
                DialogOptionCard(
                    title = conv.displayName,
                    subtitle = conv.explanation,
                    selected = state.colorConversion == conv,
                    accent = if (conv == ExportColorConversion.NO_CONVERSION) ProcessYellowGold else ProcessCyanGuide,
                    onClick = { onUpdateState(state.copy(colorConversion = conv)) },
                    testTag = "export_conv_${conv.name.lowercase()}"
                )
            }
        }

        DialogSectionHeader(
            title = "Destination Output Profile (ICC)",
            subtitle = "Match the exact paper stock and regional press standard specified by the printer."
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ExportOutputProfile.entries.forEach { profile ->
                val accent = if (profile.isCmyk) ProcessCyanGuide else BleedAlertRed
                DialogOptionCard(
                    title = profile.displayName,
                    subtitle = "${profile.stockType} • ${if (profile.isCmyk) "Max Ink ${profile.maxTacPercent}% TAC" else "Screen RGB"} — ${profile.description}",
                    selected = state.outputProfile == profile,
                    accent = accent,
                    onClick = { onUpdateState(state.copy(outputProfile = profile)) },
                    testTag = "export_profile_${profile.name.lowercase()}"
                )
            }
        }

        DialogSectionHeader(
            title = "Profile Inclusion Policy",
            subtitle = "Embedding the Destination Profile tells the RIP which ICC output intent to honor."
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileInclusionPolicy.entries.forEach { policy ->
                FilterChip(
                    selected = state.profileInclusion == policy,
                    onClick = { onUpdateState(state.copy(profileInclusion = policy)) },
                    label = { Text(policy.displayName, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("export_inclusion_${policy.name.lowercase()}")
                )
            }
        }

        DialogSwitchRow(
            title = "Ink Manager: Convert All Spots to Process",
            subtitle = "Maps Pantone spot inks to 4-color CMYK plates so you avoid extra press plate charges.",
            checked = state.convertSpotsToProcess,
            onCheckedChange = { onUpdateState(state.copy(convertSpotsToProcess = it)) },
            testTag = "export_toggle_spots_to_process"
        )
    }
}

@Composable
private fun DialogSectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryMuted
        )
    }
}

@Composable
private fun DialogOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (selected) accent.copy(alpha = 0.16f) else ObsidianPasteboard,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) accent else StudioBorderSubtle,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else Color(0xFFDCE2F2),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }
    }
}

@Composable
private fun DialogCheckboxRow(
    label: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 6.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = label,
            tint = if (checked) ProcessCyanGuide else TextSecondaryMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            Text(
                text = hint,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }
    }
}

@Composable
private fun DialogSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(StudioDockSurface)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ObsidianPasteboard,
                checkedTrackColor = ProcessCyanGuide
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun InlineSpecCallout(text: String, isWarning: Boolean) {
    val accent = if (isWarning) ProcessYellowGold else PreflightPassGreen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isWarning) Icons.Default.WarningAmber else Icons.Default.Info,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}
