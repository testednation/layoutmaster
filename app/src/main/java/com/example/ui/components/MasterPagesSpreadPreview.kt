package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DtpSoftwareMode
import com.example.model.FolioPlacement
import com.example.model.HeaderAlignment
import com.example.model.MasterId
import com.example.model.MasterPageFormatter
import com.example.model.MasterTemplateSpec
import com.example.model.SimulatedDocPage
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.ArchivalPaperWarm
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
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

fun MasterId.accentColor(): Color = when (this) {
    MasterId.A_MASTER -> ProcessMagentaGuide
    MasterId.B_MASTER -> ProcessYellowGold
    MasterId.NONE -> TextSecondaryMuted
}

@Composable
fun PagesPanelStrip(
    softwareMode: DtpSoftwareMode,
    editingMaster: MasterId,
    pages: List<SimulatedDocPage>,
    selectedPageIndex: Int,
    startingPageNumber: Int,
    onSelectEditingMaster: (MasterId) -> Unit,
    onSelectDocPage: (Int) -> Unit,
    onAddSpread: () -> Unit,
    onRemoveSpread: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
            .testTag("pages_panel_strip")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header row: Panel title + Add/Remove Spread buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (softwareMode == DtpSoftwareMode.QUARKXPRESS) {
                            "PAGE LAYOUT PALETTE"
                        } else {
                            "PAGES PANEL (PARENT PAGES)"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = ProcessCyanGuide,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Upper dock = Masters • Lower dock = Document Pages (${pages.size} pp)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onRemoveSpread,
                        enabled = pages.size > 4,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("remove_spread_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove last spread",
                            tint = if (pages.size > 4) Color.White else TextSecondaryMuted.copy(alpha = 0.4f)
                        )
                    }
                    Surface(
                        color = ProcessCyanGuide.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable(enabled = pages.size < 10) { onAddSpread() }
                            .testTag("add_spread_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add facing spread",
                                tint = if (pages.size < 10) ProcessCyanGuide else TextSecondaryMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "+2 Pages",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (pages.size < 10) ProcessCyanGuide else TextSecondaryMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Upper Dock: Master Templates (A-Master, B-Master, [None])
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(MasterId.A_MASTER, MasterId.B_MASTER).forEach { master ->
                    val isEditing = editingMaster == master
                    val accent = master.accentColor()
                    val countApplied = pages.count { it.assignedMaster == master }
                    Surface(
                        color = if (isEditing) accent.copy(alpha = 0.22f) else ObsidianPasteboard,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .border(
                                width = if (isEditing) 1.5.dp else 1.dp,
                                color = if (isEditing) accent else StudioBorderSubtle,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectEditingMaster(master) }
                            .testTag("select_master_template_${master.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Miniature Facing Spread Icon
                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 14.dp, height = 18.dp)
                                        .background(ArchivalPaperCream, RoundedCornerShape(1.dp))
                                        .border(0.8.dp, accent)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(width = 14.dp, height = 18.dp)
                                        .background(ArchivalPaperCream, RoundedCornerShape(1.dp))
                                        .border(0.8.dp, accent)
                                )
                            }
                            Column {
                                Text(
                                    text = master.labelFor(softwareMode),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isEditing) Color.White else accent,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isEditing) "EDITING • $countApplied pp" else "Applied: $countApplied pp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }
                }

                // [None] Master indicator pill
                val noneCount = pages.count { it.assignedMaster == MasterId.NONE }
                Surface(
                    color = ObsidianPasteboard,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "[None]",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondaryMuted
                        )
                        Text(
                            text = "Clean ($noneCount pp)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }

            // Divider line mirroring InDesign's Pages Panel separator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(StudioBorderSubtle)
            )

            // Lower Dock: Mini Document Page Thumbnails (shows facing pairs & live Master badge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { idx, page ->
                    val isSelected = selectedPageIndex == idx
                    val masterAccent = page.assignedMaster.accentColor()
                    val displayNum = startingPageNumber + idx
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) ProcessCyanGuide.copy(alpha = 0.2f) else Color.Transparent
                            )
                            .clickable { onSelectDocPage(idx) }
                            .padding(4.dp)
                            .testTag("pages_panel_thumb_$idx")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 36.dp, height = 44.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(ArchivalPaperCream)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) ProcessCyanGuide else masterAccent,
                                    shape = RoundedCornerShape(3.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = page.assignedMaster.shortBadge,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (page.assignedMaster == MasterId.NONE) PressInkMuted else masterAccent
                            )
                            if (page.overriddenHeaderText != null) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(6.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(ProcessCyanGuide)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "p.$displayNum",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) ProcessCyanGuide else TextSecondaryMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MasterPropagationSpreadsGrid(
    pages: List<SimulatedDocPage>,
    masterA: MasterTemplateSpec,
    masterB: MasterTemplateSpec,
    startingPageNumber: Int,
    selectedPageIndex: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group pages into realistic facing spreads:
    // Spread 0: Cover (Page 1 solo recto)
    // Subsequent spreads: Facing pairs (2-3, 4-5, 6-7, etc.)
    val spreads: List<Pair<SimulatedDocPage?, SimulatedDocPage?>> = buildList {
        if (pages.isNotEmpty()) {
            add(null to pages[0]) // Right-hand Cover Page (Recto)
            var i = 1
            while (i < pages.size) {
                val left = pages[i]
                val right = pages.getOrNull(i + 1)
                add(left to right)
                i += 2
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("master_propagation_spreads_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        spreads.forEachIndexed { spreadIdx, (leftPage, rightPage) ->
            val spreadLabel = when {
                leftPage == null && rightPage != null ->
                    "SPREAD 01 • FRONT COVER (PAGE ${startingPageNumber + rightPage.pageIndex} RECTO)"
                leftPage != null && rightPage != null ->
                    "FACING SPREAD • PAGES ${startingPageNumber + leftPage.pageIndex}–${startingPageNumber + rightPage.pageIndex} (VERSO / RECTO)"
                leftPage != null ->
                    "BACK PAGE • PAGE ${startingPageNumber + leftPage.pageIndex} (VERSO)"
                else -> "SPREAD ${spreadIdx + 1}"
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(StudioDockSurface)
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = spreadLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryMuted,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(198.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left Page (Verso / Even)
                    if (leftPage != null) {
                        SimulatedMasterPageTile(
                            page = leftPage,
                            isLeftVerso = true,
                            pageNumber = startingPageNumber + leftPage.pageIndex,
                            masterSpec = when (leftPage.assignedMaster) {
                                MasterId.A_MASTER -> masterA
                                MasterId.B_MASTER -> masterB
                                MasterId.NONE -> null
                            },
                            isSelected = selectedPageIndex == leftPage.pageIndex,
                            onClick = { onSelectPage(leftPage.pageIndex) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty pasteboard on left side of front cover
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianPasteboard.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PASTEBOARD\n(Outside Cover)",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.5.sp,
                                textAlign = TextAlign.Center,
                                color = TextSecondaryMuted.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Spine Fold Shadow between Facing Pages
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.35f),
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )

                    // Right Page (Recto / Odd)
                    if (rightPage != null) {
                        SimulatedMasterPageTile(
                            page = rightPage,
                            isLeftVerso = false,
                            pageNumber = startingPageNumber + rightPage.pageIndex,
                            masterSpec = when (rightPage.assignedMaster) {
                                MasterId.A_MASTER -> masterA
                                MasterId.B_MASTER -> masterB
                                MasterId.NONE -> null
                            },
                            isSelected = selectedPageIndex == rightPage.pageIndex,
                            onClick = { onSelectPage(rightPage.pageIndex) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(ObsidianPasteboard.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PASTEBOARD\n(Back Cover)",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.5.sp,
                                textAlign = TextAlign.Center,
                                color = TextSecondaryMuted.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimulatedMasterPageTile(
    page: SimulatedDocPage,
    isLeftVerso: Boolean,
    pageNumber: Int,
    masterSpec: MasterTemplateSpec?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val masterAccent = page.assignedMaster.accentColor()
    val formattedFolio = masterSpec?.let {
        MasterPageFormatter.formatPageNumber(pageNumber, it.folioStyle)
    } ?: ""

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(3.dp))
            .background(if (page.assignedMaster == MasterId.B_MASTER) ArchivalPaperWarm else ArchivalPaperCream)
            .border(
                width = if (isSelected) 2.2.dp else 0.8.dp,
                color = if (isSelected) ProcessCyanGuide else StudioBorderSubtle,
                shape = RoundedCornerShape(3.dp)
            )
            .clickable { onClick() }
            .testTag("simulated_doc_page_${page.pageIndex}")
    ) {
        // Master Guides & Column Overlay (Non-Printing Magenta Margin & Cyan Columns)
        if (masterSpec?.showMasterGuides == true) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val m = 8.dp.toPx()
                val cw = (size.width - m * 2).coerceAtLeast(10f)
                val ch = (size.height - m * 2).coerceAtLeast(10f)
                drawRect(
                    color = masterAccent.copy(alpha = 0.42f),
                    topLeft = Offset(m, m),
                    size = Size(cw, ch),
                    style = Stroke(width = 0.9.dp.toPx())
                )
                val cols = masterSpec.masterColumns.coerceIn(1, 3)
                if (cols > 1) {
                    val gap = 4.dp.toPx()
                    val colW = (cw - (cols - 1) * gap) / cols
                    for (c in 1 until cols) {
                        val x1 = m + c * colW + (c - 1) * gap
                        val x2 = x1 + gap
                        drawLine(ProcessCyanGuide.copy(alpha = 0.35f), Offset(x1, m), Offset(x1, size.height - m), 1f)
                        drawLine(ProcessCyanGuide.copy(alpha = 0.35f), Offset(x2, m), Offset(x2, size.height - m), 1f)
                    }
                }
            }
        }

        // Optional B-Master / Accent Top Band
        if (masterSpec?.showAccentBand == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(ProcessYellowGold.copy(alpha = 0.85f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 9.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. REPEATING HEADER ZONE (Propagated from Master or Overridden Locally)
            Column(modifier = Modifier.fillMaxWidth()) {
                val effectiveHeaderText = page.overriddenHeaderText ?: masterSpec?.headerText
                val showHeaderRow = (masterSpec?.showHeader == true || page.overriddenHeaderText != null) ||
                    (masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.HEADER_OUTSIDE)

                if (showHeaderRow) {
                    val headerTextAlign = when {
                        masterSpec?.headerAlignment == HeaderAlignment.CENTER -> TextAlign.Center
                        masterSpec?.headerAlignment == HeaderAlignment.OUTSIDE_MIRROR ->
                            if (isLeftVerso) TextAlign.Start else TextAlign.End
                        else -> TextAlign.Start
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left outside folio if Verso + Header Outside
                        if (isLeftVerso && masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.HEADER_OUTSIDE) {
                            Text(
                                text = formattedFolio,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = masterAccent,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        if (masterSpec?.showHeader == true || page.overriddenHeaderText != null) {
                            Text(
                                text = effectiveHeaderText.orEmpty(),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (page.overriddenHeaderText != null) ProcessCyanGuide else PressInkMuted,
                                textAlign = headerTextAlign,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        // Right outside folio if Recto + Header Outside
                        if (!isLeftVerso && masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.HEADER_OUTSIDE) {
                            Text(
                                text = formattedFolio,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = masterAccent,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }

                    if (masterSpec?.showHeaderRule == true) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.7.dp)
                                .background(PressInkBlack.copy(alpha = 0.4f))
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // 2. DOCUMENT PAGE LOCAL BODY CONTENT (Unique per page)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = masterAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        Text(
                            text = page.assignedMaster.shortBadge,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (page.assignedMaster == MasterId.NONE) PressInkMuted else Color(0xFF2B2420),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    if (page.overriddenHeaderText != null) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Master Item Overridden",
                            tint = ProcessCyanGuide,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = page.contentTitle,
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    lineHeight = 12.5.sp,
                    color = PressInkBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = page.contentSnippet,
                    fontFamily = PlusJakartaSansFontFamily,
                    fontSize = 7.5.sp,
                    lineHeight = 10.sp,
                    color = PressInkMuted,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. REPEATING FOOTER & AUTO-FOLIO (<#>) ZONE
            Column(modifier = Modifier.fillMaxWidth()) {
                val hasFooterContent = masterSpec?.showFooter == true ||
                    (masterSpec?.showAutoFolio == true && masterSpec.folioPlacement != FolioPlacement.HEADER_OUTSIDE)

                if (hasFooterContent) {
                    if (masterSpec?.showFooterRule == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.7.dp)
                                .background(PressInkBlack.copy(alpha = 0.35f))
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    when {
                        masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.FOOTER_CENTER -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (masterSpec.showFooter) {
                                    Text(
                                        text = masterSpec.footerText,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 6.5.sp,
                                        color = PressInkMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "— $formattedFolio —",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = masterAccent
                                )
                            }
                        }

                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isLeftVerso && masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.FOOTER_OUTSIDE) {
                                    Text(
                                        text = formattedFolio,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = masterAccent,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }

                                if (masterSpec?.showFooter == true) {
                                    Text(
                                        text = masterSpec.footerText,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 6.5.sp,
                                        color = PressInkMuted,
                                        textAlign = if (isLeftVerso) TextAlign.End else TextAlign.Start,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }

                                if (!isLeftVerso && masterSpec?.showAutoFolio == true && masterSpec.folioPlacement == FolioPlacement.FOOTER_OUTSIDE) {
                                    Text(
                                        text = formattedFolio,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = masterAccent,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "[None] Master • No Folio",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 6.5.sp,
                        color = PressInkMuted.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
