package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.KerningRecordEntity
import com.example.data.local.MissionProgressEntity
import com.example.data.local.QuizAttemptEntity
import com.example.model.DtpCurriculum
import com.example.model.DtpMission
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
import java.util.Locale

@Composable
fun CampaignScreen(
    missionProgress: List<MissionProgressEntity>,
    kerningRecords: List<KerningRecordEntity>,
    quizAttempts: List<QuizAttemptEntity>,
    onStartMission: (Int) -> Unit,
    onOpenSandbox: () -> Unit,
    onOpenTypeLesson: () -> Unit = {},
    onOpenPreflightChallenge: () -> Unit = {},
    onOpenMasterPages: () -> Unit = {},
    onOpenColorManagement: () -> Unit = {},
    onOpenGridLesson: () -> Unit = {},
    onOpenPdfExport: () -> Unit = {},
    onOpenHierarchyLesson: () -> Unit = {},
    onOpenGlossary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progressMap = remember(missionProgress) {
        missionProgress.associateBy { it.missionId }
    }
    val totalStars = missionProgress.sumOf { it.stars }
    val maxStars = DtpCurriculum.missions.size * 3
    val completedMissionsCount = missionProgress.count { it.completed }
    val bestQuizAccuracy = quizAttempts.maxOfOrNull { it.accuracyPercent } ?: 0
    val avgKerningScore = if (kerningRecords.isNotEmpty()) {
        kerningRecords.map { it.bestScore }.average().toInt()
    } else {
        0
    }

    val typographerRank = when {
        totalStars >= 15 -> "Master Swiss Typographer"
        totalStars >= 10 -> "Senior Prepress Art Director"
        totalStars >= 6 -> "InDesign & Quark Grid Specialist"
        totalStars >= 3 -> "Junior Pasteboard Compositor"
        else -> "Desktop Publishing Apprentice"
    }

    var picaConverterValue by remember { mutableFloatStateOf(3.5f) } // 3p6

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("campaign_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Card with Editorial Spread Art & Career Rank
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_editorial_hero_1795037988082),
                        contentDescription = "Swiss Desktop Publishing Hero Illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        ObsidianPasteboard.copy(alpha = 0.25f),
                                        ObsidianPasteboard.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = InDesignMagenta.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.border(1.dp, InDesignMagenta, RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = "Id • INDESIGN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                color = QuarkClassicTeal.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.border(1.dp, QuarkClassicTeal, RoundedCornerShape(6.dp))
                            ) {
                                Text(
                                    text = "QXP • QUARKXPRESS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PicaPress Studio",
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Rank: $typographerRank",
                            style = MaterialTheme.typography.titleMedium,
                            color = ProcessCyanGuide
                        )
                    }
                }

                // Stats & Quick Sandbox Row
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBadgeItem(
                            label = "LAYOUT STARS",
                            value = "$totalStars / $maxStars",
                            accent = ProcessYellowGold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatBadgeItem(
                            label = "KERNING EYE",
                            value = "$avgKerningScore%",
                            accent = ProcessCyanGuide,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatBadgeItem(
                            label = "ID vs QXP QUIZ",
                            value = "$bestQuizAccuracy%",
                            accent = ProcessMagentaGuide,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = onOpenSandbox,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_freeform_sandbox_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessCyanGuide.copy(alpha = 0.16f),
                            contentColor = ProcessCyanGuide
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DashboardCustomize,
                            contentDescription = "Open Freeform Sandbox",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Freeform Spread Layout Sandbox",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenTypeLesson,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_font_anatomy_lesson_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessMagentaGuide.copy(alpha = 0.18f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Open Font Anatomy, Kerning & Leading Lesson",
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Lesson: Font Anatomy, Kerning & Leading",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenPreflightChallenge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_preflight_challenge_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessYellowGold.copy(alpha = 0.18f),
                            contentColor = ProcessYellowGold
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Open Preflight Challenge Module",
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Preflight Challenge: Fix Missing Fonts, Overset & PPI",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenMasterPages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_master_pages_sim_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = QuarkClassicTeal.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Open Master Pages Simulator",
                            tint = QuarkClassicTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Master Pages Simulator: Headers, Footers & Auto-Folios",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenColorManagement,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_color_management_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = InDesignMagenta.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Open Color Management & Bleed Lab",
                            tint = ProcessCyanGuide,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Color Management Lab: CMYK vs RGB, ICC & Bleed",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenGridLesson,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_grid_lesson_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PreflightPassGreen.copy(alpha = 0.18f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Open Grid Design Principles Lesson",
                            tint = PreflightPassGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Grid Design Principles: Columns, Gutters & Margins",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenPdfExport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_pdf_export_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessCyanGuide.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Open Print-Ready PDF Export Dialog Lab",
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Print-Ready PDF Export: DPI, Marks & Output Profiles",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenHierarchyLesson,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_hierarchy_lesson_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessMagentaGuide.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatLineSpacing,
                            contentDescription = "Open Article Typography Hierarchy Lesson",
                            tint = ProcessCyanGuide,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Typography Hierarchy: Fix Tracking, Leading & Kerning",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = onOpenGlossary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_dtp_glossary_button"),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ProcessYellowGold.copy(alpha = 0.16f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Open Interactive DTP Glossary",
                            tint = ProcessYellowGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive DTP Glossary: Search Terms & Jump to Labs",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // Interactive Pica / Point / Inch / Millimeter Ruler Converter Widget
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StudioDockSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Pica Ruler Converter",
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Interactive Pica & Point Ruler",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "1 in = 6p0 = 72pt",
                            style = MaterialTheme.typography.labelMedium,
                            color = ProcessYellowGold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val picaNotation = DtpCurriculum.formatPicas(picaConverterValue)
                    val totalPoints = (picaConverterValue * 12f).toInt()
                    val inches = picaConverterValue / 6f
                    val millimeters = inches * 25.4f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianPasteboard)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DTP NOTATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = picaNotation,
                                style = MaterialTheme.typography.headlineMedium,
                                color = ProcessCyanGuide
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "POSTSCRIPT PTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = "${totalPoints} pt",
                                style = MaterialTheme.typography.titleLarge,
                                color = ProcessYellowGold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "INCHES / MM",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondaryMuted
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f\" • %.1fmm", inches, millimeters),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }

                    Slider(
                        value = picaConverterValue,
                        onValueChange = { picaConverterValue = (kotlin.math.round(it * 2f) / 2f) },
                        valueRange = 0.5f..12.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = ProcessCyanGuide,
                            activeTrackColor = ProcessCyanGuide
                        ),
                        modifier = Modifier.testTag("pica_converter_slider")
                    )
                }
            }
        }

        // Section Header for the 6 Hands-On DTP Missions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Editorial Layout Campaign",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Master grids, leading, story threading, runaround, master pages & preflight",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryMuted
                    )
                }
                Surface(
                    color = PreflightPassGreen.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$completedMissionsCount/${DtpCurriculum.missions.size} Done",
                        style = MaterialTheme.typography.labelMedium,
                        color = PreflightPassGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        items(DtpCurriculum.missions, key = { it.id }) { mission ->
            val progress = progressMap[mission.id]
            MissionCardItem(
                mission = mission,
                progress = progress,
                onLaunchMission = { onStartMission(mission.id) }
            )
        }
    }
}

@Composable
private fun StatBadgeItem(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ObsidianPasteboard)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MissionCardItem(
    mission: DtpMission,
    progress: MissionProgressEntity?,
    onLaunchMission: () -> Unit
) {
    val stars = progress?.stars ?: 0
    val isCompleted = progress?.completed == true

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isCompleted) PreflightPassGreen.copy(alpha = 0.55f) else StudioBorderSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onLaunchMission() }
            .testTag("mission_card_${mission.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = ProcessCyanGuide.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mission.codeName,
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessCyanGuide,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mission Completed",
                            tint = PreflightPassGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 3-Star Mastery Rating
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= stars) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) ProcessYellowGold else TextSecondaryMuted.copy(alpha = 0.45f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mission.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mission.softwareFocus,
                style = MaterialTheme.typography.labelMedium,
                color = ProcessMagentaGuide
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mission.conceptSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD5DCF0)
            )

            Spacer(modifier = Modifier.height(10.dp))
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
                    contentDescription = "Pro Tip",
                    tint = ProcessYellowGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = mission.proTip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ProcessYellowGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (progress != null) {
                        "Best Press Score: ${progress.bestScore}/100"
                    } else {
                        "Not yet submitted to press"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isCompleted) PreflightPassGreen else TextSecondaryMuted
                )

                Button(
                    onClick = onLaunchMission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProcessCyanGuide,
                        contentColor = ObsidianPasteboard
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("launch_mission_btn_${mission.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DesignServices,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) "Replay Layout" else "Open in Studio",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
