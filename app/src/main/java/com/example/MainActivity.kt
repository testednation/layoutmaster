package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FormatLineSpacing
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Spellcheck
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PicaPressDatabase
import com.example.data.local.PicaPressRepository
import com.example.model.DtpCurriculum
import com.example.model.GlossaryTargetModule
import com.example.ui.screens.CampaignScreen
import com.example.ui.screens.ColorManagementScreen
import com.example.ui.screens.DtpGlossaryScreen
import com.example.ui.screens.GridDesignLessonScreen
import com.example.ui.screens.MasterPagesSimulatorScreen
import com.example.ui.screens.PdfExportSettingsScreen
import com.example.ui.screens.PreflightChallengeScreen
import com.example.ui.screens.SoftwareShowdownScreen
import com.example.ui.screens.StudioWorkbenchScreen
import com.example.ui.screens.TypeAndGridLabScreen
import com.example.ui.screens.TypeHierarchyLessonScreen
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PicaPressTheme
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted
import com.example.viewmodel.PicaPressViewModel

enum class PicaPressDestination(
    val routeId: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    CAMPAIGN(
        routeId = "campaign",
        labelRes = R.string.nav_missions,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "nav_tab_campaign"
    ),
    WORKBENCH(
        routeId = "workbench",
        labelRes = R.string.nav_workbench,
        selectedIcon = Icons.Filled.DesignServices,
        unselectedIcon = Icons.Outlined.DesignServices,
        testTag = "nav_tab_workbench"
    ),
    TYPE_LAB(
        routeId = "type_lab",
        labelRes = R.string.nav_typelab,
        selectedIcon = Icons.Filled.Spellcheck,
        unselectedIcon = Icons.Outlined.Spellcheck,
        testTag = "nav_tab_typelab"
    ),
    PREFLIGHT(
        routeId = "preflight",
        labelRes = R.string.nav_preflight,
        selectedIcon = Icons.Filled.FactCheck,
        unselectedIcon = Icons.Outlined.FactCheck,
        testTag = "nav_tab_preflight"
    ),
    SHOWDOWN(
        routeId = "showdown",
        labelRes = R.string.nav_showdown,
        selectedIcon = Icons.Filled.CompareArrows,
        unselectedIcon = Icons.Outlined.CompareArrows,
        testTag = "nav_tab_showdown"
    )
}

enum class PreflightSubMode {
    INSPECTOR,
    COLOR_LAB,
    PDF_EXPORT
}

enum class TypeLabSubMode {
    ANATOMY_KERNING,
    ARTICLE_HIERARCHY,
    GRID_PRINCIPLES
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PicaPressTheme {
                PicaPressApp()
            }
        }
    }
}

@Composable
fun PicaPressApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        val db = PicaPressDatabase.getInstance(context)
        PicaPressRepository(db.picaPressDao())
    }
    val viewModel: PicaPressViewModel = viewModel(
        factory = PicaPressViewModel.Factory(repository)
    )

    val missionProgress by viewModel.missionProgressList.collectAsStateWithLifecycle()
    val savedSpreads by viewModel.savedSpreadsList.collectAsStateWithLifecycle()
    val quizAttempts by viewModel.quizAttemptsList.collectAsStateWithLifecycle()
    val kerningRecords by viewModel.kerningRecordsList.collectAsStateWithLifecycle()

    val activeMissionId by viewModel.activeMissionId.collectAsStateWithLifecycle()
    val spreadState by viewModel.spreadState.collectAsStateWithLifecycle()
    val activeKerningIndex by viewModel.activeKerningIndex.collectAsStateWithLifecycle()
    val userKerningGaps by viewModel.userKerningGaps.collectAsStateWithLifecycle()
    val kerningRevealed by viewModel.kerningRevealed.collectAsStateWithLifecycle()

    var currentDestination by rememberSaveable { mutableStateOf(PicaPressDestination.CAMPAIGN) }
    var workbenchMasterPagesMode by rememberSaveable { mutableStateOf(false) }
    var preflightSubMode by rememberSaveable { mutableStateOf(PreflightSubMode.INSPECTOR) }
    var typeLabSubMode by rememberSaveable { mutableStateOf(TypeLabSubMode.ANATOMY_KERNING) }
    var showdownGlossaryMode by rememberSaveable { mutableStateOf(false) }

    // Mandatory BackHandler for custom state switching on secondary screens
    BackHandler(enabled = currentDestination != PicaPressDestination.CAMPAIGN) {
        if (currentDestination == PicaPressDestination.WORKBENCH && workbenchMasterPagesMode) {
            workbenchMasterPagesMode = false
        } else if (currentDestination == PicaPressDestination.TYPE_LAB && typeLabSubMode != TypeLabSubMode.ANATOMY_KERNING) {
            typeLabSubMode = TypeLabSubMode.ANATOMY_KERNING
        } else if (currentDestination == PicaPressDestination.PREFLIGHT && preflightSubMode != PreflightSubMode.INSPECTOR) {
            preflightSubMode = PreflightSubMode.INSPECTOR
        } else if (currentDestination == PicaPressDestination.SHOWDOWN && showdownGlossaryMode) {
            showdownGlossaryMode = false
        } else {
            currentDestination = PicaPressDestination.CAMPAIGN
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ObsidianPasteboard,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = StudioDockSurface,
                contentColor = ProcessCyanGuide
            ) {
                PicaPressDestination.entries.forEach { dest ->
                    val selected = currentDestination == dest
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(
                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                contentDescription = stringResource(id = dest.labelRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = dest.labelRes),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ObsidianPasteboard,
                            selectedTextColor = ProcessCyanGuide,
                            indicatorColor = ProcessCyanGuide,
                            unselectedIconColor = TextSecondaryMuted,
                            unselectedTextColor = TextSecondaryMuted
                        ),
                        modifier = Modifier.testTag(dest.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            PicaPressDestination.CAMPAIGN -> {
                CampaignScreen(
                    missionProgress = missionProgress,
                    kerningRecords = kerningRecords,
                    quizAttempts = quizAttempts,
                    onStartMission = { missionId ->
                        viewModel.loadMission(missionId)
                        workbenchMasterPagesMode = false
                        currentDestination = PicaPressDestination.WORKBENCH
                    },
                    onOpenSandbox = {
                        viewModel.enterFreeformSandbox()
                        workbenchMasterPagesMode = false
                        currentDestination = PicaPressDestination.WORKBENCH
                    },
                    onOpenTypeLesson = {
                        typeLabSubMode = TypeLabSubMode.ANATOMY_KERNING
                        currentDestination = PicaPressDestination.TYPE_LAB
                    },
                    onOpenPreflightChallenge = {
                        preflightSubMode = PreflightSubMode.INSPECTOR
                        currentDestination = PicaPressDestination.PREFLIGHT
                    },
                    onOpenMasterPages = {
                        workbenchMasterPagesMode = true
                        currentDestination = PicaPressDestination.WORKBENCH
                    },
                    onOpenColorManagement = {
                        preflightSubMode = PreflightSubMode.COLOR_LAB
                        currentDestination = PicaPressDestination.PREFLIGHT
                    },
                    onOpenGridLesson = {
                        typeLabSubMode = TypeLabSubMode.GRID_PRINCIPLES
                        currentDestination = PicaPressDestination.TYPE_LAB
                    },
                    onOpenPdfExport = {
                        preflightSubMode = PreflightSubMode.PDF_EXPORT
                        currentDestination = PicaPressDestination.PREFLIGHT
                    },
                    onOpenHierarchyLesson = {
                        typeLabSubMode = TypeLabSubMode.ARTICLE_HIERARCHY
                        currentDestination = PicaPressDestination.TYPE_LAB
                    },
                    onOpenGlossary = {
                        showdownGlossaryMode = true
                        currentDestination = PicaPressDestination.SHOWDOWN
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            PicaPressDestination.WORKBENCH -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Studio Sub-Mode Switcher: Spread Editor vs. Master Pages Simulator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !workbenchMasterPagesMode,
                            onClick = { workbenchMasterPagesMode = false },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.DesignServices,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Spread Layout",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("workbench_subtab_spread")
                        )
                        FilterChip(
                            selected = workbenchMasterPagesMode,
                            onClick = { workbenchMasterPagesMode = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Layers,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Master Pages",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("workbench_subtab_master_pages")
                        )
                    }

                    if (workbenchMasterPagesMode) {
                        MasterPagesSimulatorScreen(
                            softwareMode = spreadState.softwareMode,
                            onToggleSoftwareMode = viewModel::toggleSoftwareMode,
                            onRecordMasterAchievement = viewModel::recordQuizScore,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        val activeMission = remember(activeMissionId) {
                            DtpCurriculum.missions.find { it.id == activeMissionId }
                        }
                        val objectiveChecks = remember(activeMission, spreadState) {
                            activeMission?.evaluate?.invoke(spreadState) ?: emptyList()
                        }
                        StudioWorkbenchScreen(
                            spreadState = spreadState,
                            activeMission = activeMission,
                            objectiveChecks = objectiveChecks,
                            savedSpreads = savedSpreads,
                            onUpdateSpread = viewModel::updateSpread,
                            onToggleSoftwareMode = viewModel::toggleSoftwareMode,
                            onTogglePreviewMode = viewModel::togglePreviewTrimMode,
                            onSelectMission = viewModel::loadMission,
                            onSwitchToSandbox = viewModel::enterFreeformSandbox,
                            onSubmitMissionPressCheck = viewModel::submitMissionPressCheck,
                            onSaveSpread = viewModel::saveCurrentSpreadToPortfolio,
                            onLoadSavedSpread = viewModel::loadSavedSpreadIntoWorkbench,
                            onDeleteSavedSpread = viewModel::deleteSavedSpread,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            PicaPressDestination.TYPE_LAB -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Type & Grid Lab Sub-Mode Switcher: Anatomy | Hierarchy | Grids
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = typeLabSubMode == TypeLabSubMode.ANATOMY_KERNING,
                            onClick = { typeLabSubMode = TypeLabSubMode.ANATOMY_KERNING },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Spellcheck,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Anatomy",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("type_lab_subtab_kerning")
                        )
                        FilterChip(
                            selected = typeLabSubMode == TypeLabSubMode.ARTICLE_HIERARCHY,
                            onClick = { typeLabSubMode = TypeLabSubMode.ARTICLE_HIERARCHY },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FormatLineSpacing,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Hierarchy",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("type_lab_subtab_hierarchy")
                        )
                        FilterChip(
                            selected = typeLabSubMode == TypeLabSubMode.GRID_PRINCIPLES,
                            onClick = { typeLabSubMode = TypeLabSubMode.GRID_PRINCIPLES },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.GridView,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Grids",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("type_lab_subtab_grid")
                        )
                    }

                    when (typeLabSubMode) {
                        TypeLabSubMode.ANATOMY_KERNING -> {
                            TypeAndGridLabScreen(
                                activeKerningIndex = activeKerningIndex,
                                userKerningGaps = userKerningGaps,
                                kerningRevealed = kerningRevealed,
                                kerningRecords = kerningRecords,
                                onSelectKerningWord = viewModel::selectKerningWord,
                                onAdjustKerningGap = viewModel::adjustKerningGap,
                                onSetKerningGap = viewModel::setKerningGap,
                                onEvaluateKerning = viewModel::evaluateKerningChallenge,
                                onResetKerningWord = viewModel::resetActiveKerningWord,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        TypeLabSubMode.ARTICLE_HIERARCHY -> {
                            TypeHierarchyLessonScreen(
                                softwareMode = spreadState.softwareMode,
                                onRecordHierarchyScore = viewModel::recordQuizScore,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        TypeLabSubMode.GRID_PRINCIPLES -> {
                            GridDesignLessonScreen(
                                softwareMode = spreadState.softwareMode,
                                onRecordGridLessonProgress = viewModel::recordQuizScore,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            PicaPressDestination.PREFLIGHT -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Prepress Sub-Mode Switcher: Live Preflight | Color Lab | PDF Export
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = preflightSubMode == PreflightSubMode.INSPECTOR,
                            onClick = { preflightSubMode = PreflightSubMode.INSPECTOR },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.FactCheck,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Preflight",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preflight_subtab_inspector")
                        )
                        FilterChip(
                            selected = preflightSubMode == PreflightSubMode.COLOR_LAB,
                            onClick = { preflightSubMode = PreflightSubMode.COLOR_LAB },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ColorLens,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Color Lab",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preflight_subtab_color_lab")
                        )
                        FilterChip(
                            selected = preflightSubMode == PreflightSubMode.PDF_EXPORT,
                            onClick = { preflightSubMode = PreflightSubMode.PDF_EXPORT },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.PictureAsPdf,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "PDF Export",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preflight_subtab_pdf_export")
                        )
                    }

                    when (preflightSubMode) {
                        PreflightSubMode.INSPECTOR -> {
                            val preflightRescues = remember(quizAttempts) {
                                quizAttempts.count { it.modeTitle.startsWith("Preflight:") && it.accuracyPercent == 100 }
                            }
                            PreflightChallengeScreen(
                                completedCasesCount = preflightRescues,
                                onRecordPreflightRescue = viewModel::recordQuizScore,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        PreflightSubMode.COLOR_LAB -> {
                            ColorManagementScreen(
                                softwareMode = spreadState.softwareMode,
                                onRecordColorCalibration = viewModel::recordQuizScore,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        PreflightSubMode.PDF_EXPORT -> {
                            PdfExportSettingsScreen(
                                softwareMode = spreadState.softwareMode,
                                onToggleSoftwareMode = viewModel::toggleSoftwareMode,
                                onRecordExportScore = viewModel::recordQuizScore,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            PicaPressDestination.SHOWDOWN -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Reference Sub-Mode Switcher: Id vs QXP Showdown | DTP Glossary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioDockSurface)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !showdownGlossaryMode,
                            onClick = { showdownGlossaryMode = false },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CompareArrows,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "Id vs QXP",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("showdown_subtab_quiz")
                        )
                        FilterChip(
                            selected = showdownGlossaryMode,
                            onClick = { showdownGlossaryMode = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "DTP Glossary",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.24f),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("showdown_subtab_glossary")
                        )
                    }

                    if (showdownGlossaryMode) {
                        DtpGlossaryScreen(
                            onNavigateToModule = { target ->
                                when (target) {
                                    GlossaryTargetModule.SPREAD_WORKBENCH -> {
                                        workbenchMasterPagesMode = false
                                        currentDestination = PicaPressDestination.WORKBENCH
                                    }
                                    GlossaryTargetModule.MASTER_PAGES -> {
                                        workbenchMasterPagesMode = true
                                        currentDestination = PicaPressDestination.WORKBENCH
                                    }
                                    GlossaryTargetModule.FONT_ANATOMY -> {
                                        typeLabSubMode = TypeLabSubMode.ANATOMY_KERNING
                                        currentDestination = PicaPressDestination.TYPE_LAB
                                    }
                                    GlossaryTargetModule.ARTICLE_HIERARCHY -> {
                                        typeLabSubMode = TypeLabSubMode.ARTICLE_HIERARCHY
                                        currentDestination = PicaPressDestination.TYPE_LAB
                                    }
                                    GlossaryTargetModule.GRID_PRINCIPLES -> {
                                        typeLabSubMode = TypeLabSubMode.GRID_PRINCIPLES
                                        currentDestination = PicaPressDestination.TYPE_LAB
                                    }
                                    GlossaryTargetModule.LIVE_PREFLIGHT -> {
                                        preflightSubMode = PreflightSubMode.INSPECTOR
                                        currentDestination = PicaPressDestination.PREFLIGHT
                                    }
                                    GlossaryTargetModule.COLOR_BLEED_LAB -> {
                                        preflightSubMode = PreflightSubMode.COLOR_LAB
                                        currentDestination = PicaPressDestination.PREFLIGHT
                                    }
                                    GlossaryTargetModule.PDF_EXPORT_LAB -> {
                                        preflightSubMode = PreflightSubMode.PDF_EXPORT
                                        currentDestination = PicaPressDestination.PREFLIGHT
                                    }
                                    GlossaryTargetModule.SOFTWARE_SHOWDOWN -> {
                                        showdownGlossaryMode = false
                                        currentDestination = PicaPressDestination.SHOWDOWN
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        SoftwareShowdownScreen(
                            quizAttempts = quizAttempts,
                            onRecordQuizScore = viewModel::recordQuizScore,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
