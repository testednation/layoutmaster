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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ArticleTypeElement
import com.example.model.DtpSoftwareMode
import com.example.model.HierarchyLessonCatalog
import com.example.ui.components.MockArticleHierarchyCanvas
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
import kotlin.math.roundToInt

@Composable
fun TypeHierarchyLessonScreen(
    softwareMode: DtpSoftwareMode,
    onRecordHierarchyScore: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lessons = HierarchyLessonCatalog.lessons
    var selectedLessonIndex by remember { mutableIntStateOf(0) }
    val activeLesson = lessons[selectedLessonIndex]

    var articleState by remember(selectedLessonIndex) { mutableStateOf(activeLesson.brokenState) }
    var selectedElement by remember(selectedLessonIndex) { mutableStateOf(ArticleTypeElement.HEADLINE) }
    var showOverlayGuides by remember { mutableStateOf(true) }
    var comparingBroken by remember(selectedLessonIndex) { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var completedLessonIds by remember { mutableStateOf(setOf<Int>()) }

    val evaluation = remember(activeLesson, articleState) {
        HierarchyLessonCatalog.evaluate(activeLesson, articleState)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("type_hierarchy_lesson_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header & Lesson Case Picker
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Article Hierarchy Repair Lab",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fix real tracking, leading & pair-kerning flaws in editorial articles",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Surface(
                        color = PreflightPassGreen.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${completedLessonIds.size}/${lessons.size} Fixed",
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
                    lessons.forEachIndexed { idx, lesson ->
                        val isDone = completedLessonIds.contains(lesson.id)
                        FilterChip(
                            selected = selectedLessonIndex == idx,
                            onClick = { selectedLessonIndex = idx },
                            leadingIcon = if (isDone) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Lesson Completed",
                                        tint = PreflightPassGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            label = {
                                Text(
                                    text = lesson.badgeCode,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier.testTag("hierarchy_case_chip_${lesson.id}")
                        )
                    }
                }

                // Case Brief Banner
                Surface(
                    color = StudioCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = activeLesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = ProcessYellowGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeLesson.problemOverview,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD5DCF0)
                        )
                    }
                }
            }
        }

        // 2. Canvas Toolbar (Metrics Guides vs Clean Proof + Compare Broken Original)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = showOverlayGuides,
                    onClick = { showOverlayGuides = !showOverlayGuides },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showOverlayGuides) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Type Guides",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (showOverlayGuides) "Metrics On" else "Clean Proof (W)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.2f),
                        selectedLabelColor = ProcessCyanGuide
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hierarchy_toggle_guides")
                )

                FilterChip(
                    selected = comparingBroken,
                    onClick = { comparingBroken = !comparingBroken },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Compare Original Broken Article",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (comparingBroken) "Showing Broken" else "Compare Broken",
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BleedAlertRed.copy(alpha = 0.22f),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hierarchy_compare_broken")
                )
            }
        }

        // 3. Interactive Mock Editorial Article Canvas
        item {
            MockArticleHierarchyCanvas(
                lesson = activeLesson,
                state = articleState,
                evaluation = evaluation,
                selectedElement = selectedElement,
                showOverlayGuides = showOverlayGuides,
                comparingBroken = comparingBroken,
                onSelectElement = { element ->
                    comparingBroken = false
                    selectedElement = element
                }
            )
        }

        // 4. Character Panel Inspector Card (Element Tabs + Live Sliders)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                imageVector = Icons.Default.FormatLineSpacing,
                                contentDescription = null,
                                tint = ProcessCyanGuide,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (softwareMode == DtpSoftwareMode.INDESIGN) {
                                    "Character Panel • ${selectedElement.fullName}"
                                } else {
                                    "Measurements • ${selectedElement.fullName}"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Element Selector Row with Red/Green Diagnostic Dots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ArticleTypeElement.entries.forEach { elem ->
                            val isSelected = selectedElement == elem
                            val hasIssue = evaluation.elementHasIssue(elem)
                            val isPassed = evaluation.elementPassed(elem)

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    comparingBroken = false
                                    selectedElement = elem
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isPassed -> PreflightPassGreen
                                                    hasIssue -> BleedAlertRed
                                                    else -> TextSecondaryMuted
                                                }
                                            )
                                    )
                                },
                                label = {
                                    Text(
                                        text = elem.shortLabel,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("hierarchy_elem_tab_${elem.name.lowercase()}")
                            )
                        }
                    }

                    Text(
                        text = selectedElement.roleSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )

                    // Element-Specific Real-Time Controls
                    when (selectedElement) {
                        ArticleTypeElement.KICKER -> {
                            TypeMetricSlider(
                                label = "All-Caps Tracking (VA)",
                                valueDisplay = "${HierarchyLessonCatalog.formatEm(articleState.kickerTrackingEm)} /1000 em",
                                value = articleState.kickerTrackingEm.toFloat(),
                                valueRange = -80f..240f,
                                accent = ProcessCyanGuide,
                                onValueChange = {
                                    val snapped = (it / 5f).roundToInt() * 5
                                    articleState = articleState.copy(kickerTrackingEm = snapped)
                                },
                                testTag = "slider_kicker_tracking"
                            )
                        }

                        ArticleTypeElement.HEADLINE -> {
                            TypeMetricSlider(
                                label = "Pair Kerning (${activeLesson.kernPairDisplay})",
                                valueDisplay = "${HierarchyLessonCatalog.formatEm(articleState.headlineKerningEm)} /1000 em",
                                value = articleState.headlineKerningEm.toFloat(),
                                valueRange = -120f..120f,
                                accent = ProcessYellowGold,
                                helperNote = "Kerning adjusts ONLY the highlighted ${activeLesson.kernPairDisplay} diagonal pair without squeezing the rest of the headline.",
                                onValueChange = {
                                    val snapped = (it / 5f).roundToInt() * 5
                                    articleState = articleState.copy(headlineKerningEm = snapped)
                                },
                                testTag = "slider_headline_kerning"
                            )
                            TypeMetricSlider(
                                label = "Overall Headline Tracking (VA)",
                                valueDisplay = "${HierarchyLessonCatalog.formatEm(articleState.headlineTrackingEm)} /1000 em",
                                value = articleState.headlineTrackingEm.toFloat(),
                                valueRange = -80f..120f,
                                accent = ProcessCyanGuide,
                                onValueChange = {
                                    val snapped = (it / 5f).roundToInt() * 5
                                    articleState = articleState.copy(headlineTrackingEm = snapped)
                                },
                                testTag = "slider_headline_tracking"
                            )
                            val leadPt = (activeLesson.headlineSizePt * articleState.headlineLeadingRatio).roundToInt()
                            TypeMetricSlider(
                                label = "Display Leading",
                                valueDisplay = "${activeLesson.headlineSizePt.toInt()}/${leadPt} pt (${HierarchyLessonCatalog.formatRatio(articleState.headlineLeadingRatio)})",
                                value = articleState.headlineLeadingRatio,
                                valueRange = 0.85f..1.80f,
                                accent = ProcessMagentaGuide,
                                onValueChange = {
                                    val snapped = (it * 50f).roundToInt() / 50f
                                    articleState = articleState.copy(headlineLeadingRatio = snapped)
                                },
                                testTag = "slider_headline_leading"
                            )
                        }

                        ArticleTypeElement.SUBHEAD -> {
                            TypeMetricSlider(
                                label = "Deck Tracking (VA)",
                                valueDisplay = "${HierarchyLessonCatalog.formatEm(articleState.subheadTrackingEm)} /1000 em",
                                value = articleState.subheadTrackingEm.toFloat(),
                                valueRange = -80f..120f,
                                accent = ProcessCyanGuide,
                                onValueChange = {
                                    val snapped = (it / 5f).roundToInt() * 5
                                    articleState = articleState.copy(subheadTrackingEm = snapped)
                                },
                                testTag = "slider_subhead_tracking"
                            )
                            val leadPt = (activeLesson.subheadSizePt * articleState.subheadLeadingRatio).roundToInt()
                            TypeMetricSlider(
                                label = "Deck Leading",
                                valueDisplay = "${activeLesson.subheadSizePt.toInt()}/${leadPt} pt (${HierarchyLessonCatalog.formatRatio(articleState.subheadLeadingRatio)})",
                                value = articleState.subheadLeadingRatio,
                                valueRange = 0.95f..1.85f,
                                accent = ProcessMagentaGuide,
                                onValueChange = {
                                    val snapped = (it * 50f).roundToInt() / 50f
                                    articleState = articleState.copy(subheadLeadingRatio = snapped)
                                },
                                testTag = "slider_subhead_leading"
                            )
                        }

                        ArticleTypeElement.BODY -> {
                            TypeMetricSlider(
                                label = "Body Tracking (VA)",
                                valueDisplay = "${HierarchyLessonCatalog.formatEm(articleState.bodyTrackingEm)} /1000 em",
                                value = articleState.bodyTrackingEm.toFloat(),
                                valueRange = -80f..120f,
                                accent = ProcessCyanGuide,
                                onValueChange = {
                                    val snapped = (it / 5f).roundToInt() * 5
                                    articleState = articleState.copy(bodyTrackingEm = snapped)
                                },
                                testTag = "slider_body_tracking"
                            )
                            val leadPt = (activeLesson.bodySizePt * articleState.bodyLeadingRatio).roundToInt()
                            TypeMetricSlider(
                                label = "Body Leading (Line Height)",
                                valueDisplay = "${activeLesson.bodySizePt.toInt()}/${leadPt} pt (${HierarchyLessonCatalog.formatRatio(articleState.bodyLeadingRatio)})",
                                value = articleState.bodyLeadingRatio,
                                valueRange = 0.95f..2.05f,
                                accent = ProcessMagentaGuide,
                                onValueChange = {
                                    val snapped = (it * 50f).roundToInt() / 50f
                                    articleState = articleState.copy(bodyLeadingRatio = snapped)
                                },
                                testTag = "slider_body_leading"
                            )
                        }
                    }

                    // DTP Keyboard Shortcut Reference Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianPasteboard)
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "DTP Keyboard Shortcut",
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (softwareMode == DtpSoftwareMode.INDESIGN) {
                                "InDesign: Opt/Alt + ←/→ (Kern/Track) • Opt/Alt + ↑/↓ (Leading)"
                            } else {
                                "QuarkXPress: Cmd/Ctrl + Shift + [ / ] (Kern/Track) • Shift + Cmd + \" (Leading)"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryMuted
                        )
                    }
                }
            }
        }

        // 5. Live Visual Hierarchy Diagnostics & Action Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
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
                        Text(
                            text = "Hierarchy Diagnostic Checklist",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${evaluation.scorePercent}% Clarity",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (evaluation.allFixed) PreflightPassGreen else ProcessYellowGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    evaluation.checks.forEach { check ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianPasteboard)
                                .padding(8.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (check.passed) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = if (check.passed) PreflightPassGreen else BleedAlertRed,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = check.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = check.feedback,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryMuted
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                comparingBroken = false
                                articleState = activeLesson.brokenState
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hierarchy_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Article",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", style = MaterialTheme.typography.labelMedium)
                        }

                        OutlinedButton(
                            onClick = {
                                comparingBroken = false
                                articleState = activeLesson.idealState
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("hierarchy_preview_ideal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Preview Ideal Hierarchy",
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ideal", style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                val passed = evaluation.checks.count { it.passed }
                                if (evaluation.allFixed) {
                                    completedLessonIds = completedLessonIds + activeLesson.id
                                }
                                onRecordHierarchyScore(
                                    "Type Hierarchy: ${activeLesson.badgeCode}",
                                    passed,
                                    evaluation.checks.size
                                )
                                showResultDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (evaluation.allFixed) PreflightPassGreen else ProcessCyanGuide,
                                contentColor = ObsidianPasteboard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hierarchy_certify_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Certify Article Hierarchy",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Certify (${evaluation.scorePercent}%)",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            containerColor = StudioCardSurface,
            icon = {
                Icon(
                    imageVector = if (evaluation.allFixed) Icons.Default.Verified else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (evaluation.allFixed) PreflightPassGreen else ProcessYellowGold,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = evaluation.verdictTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (evaluation.allFixed) PreflightPassGreen else ProcessYellowGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Article Clarity Score: ${evaluation.scorePercent}% (${evaluation.checks.count { it.passed }}/${evaluation.checks.size} hierarchy rules met)",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                    Text(
                        text = if (evaluation.allFixed) {
                            "Great eye! Toggle 'Compare Broken' to appreciate how crisp kerning, purposeful tracking, and disciplined leading transform editorial hierarchy."
                        } else {
                            "Tap any red-flagged region in the mock article and refine its tracking, leading, or pair kerning until all checks pass."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryMuted
                    )
                }
            },
            confirmButton = {
                if (evaluation.allFixed && selectedLessonIndex < lessons.lastIndex) {
                    Button(
                        onClick = {
                            showResultDialog = false
                            selectedLessonIndex += 1
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PreflightPassGreen,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier.testTag("hierarchy_dialog_next_case")
                    ) {
                        Text("Next Broken Article")
                    }
                } else {
                    Button(
                        onClick = { showResultDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier.testTag("hierarchy_dialog_close")
                    ) {
                        Text(if (evaluation.allFixed) "Done" else "Keep Refining")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showResultDialog = false }) {
                    Text("Close", color = TextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
private fun TypeMetricSlider(
    label: String,
    valueDisplay: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    accent: Color,
    helperNote: String? = null,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(StudioDockSurface)
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
            Text(
                text = valueDisplay,
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent
            ),
            modifier = Modifier.testTag(testTag)
        )
        if (helperNote != null) {
            Text(
                text = helperNote,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryMuted
            )
        }
    }
}
