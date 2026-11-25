package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.KerningRecordEntity
import com.example.data.local.MissionProgressEntity
import com.example.data.local.PicaPressRepository
import com.example.data.local.QuizAttemptEntity
import com.example.data.local.SavedSpreadEntity
import com.example.model.DtpCurriculum
import com.example.model.DtpMission
import com.example.model.DtpSoftwareMode
import com.example.model.EditorialFontChoice
import com.example.model.MissionObjectiveCheck
import com.example.model.SpreadLayoutState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PicaPressViewModel(
    private val repository: PicaPressRepository
) : ViewModel() {

    val missionProgressList: StateFlow<List<MissionProgressEntity>> =
        repository.missionProgressFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val savedSpreadsList: StateFlow<List<SavedSpreadEntity>> =
        repository.savedSpreadsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val quizAttemptsList: StateFlow<List<QuizAttemptEntity>> =
        repository.quizAttemptsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    val kerningRecordsList: StateFlow<List<KerningRecordEntity>> =
        repository.kerningRecordsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Active Mission in the Spread Workbench (null = Freeform Sandbox Studio mode)
    private val _activeMissionId = MutableStateFlow<Int?>(1)
    val activeMissionId: StateFlow<Int?> = _activeMissionId.asStateFlow()

    // Live Spread Layout State in the Workbench
    private val _spreadState = MutableStateFlow(DtpCurriculum.missions.first().initialState)
    val spreadState: StateFlow<SpreadLayoutState> = _spreadState.asStateFlow()

    // Active Kerning Challenge Index + User Gap Adjustments
    private val _activeKerningIndex = MutableStateFlow(0)
    val activeKerningIndex: StateFlow<Int> = _activeKerningIndex.asStateFlow()

    private val _userKerningGaps = MutableStateFlow(
        DtpCurriculum.kerningChallenges.first().initialGaps
    )
    val userKerningGaps: StateFlow<List<Int>> = _userKerningGaps.asStateFlow()

    private val _kerningRevealed = MutableStateFlow(false)
    val kerningRevealed: StateFlow<Boolean> = _kerningRevealed.asStateFlow()

    fun loadMission(missionId: Int) {
        val mission = DtpCurriculum.missions.find { it.id == missionId } ?: return
        _activeMissionId.value = mission.id
        _spreadState.value = mission.initialState.copy(
            softwareMode = _spreadState.value.softwareMode
        )
    }

    fun enterFreeformSandbox() {
        _activeMissionId.value = null
        _spreadState.value = SpreadLayoutState(
            title = "SWISS TYPOGRAPHY ANNUAL",
            softwareMode = _spreadState.value.softwareMode,
            columns = 3,
            gutterPicas = 1.5f,
            marginPicas = 3.0f,
            bleedMm = 3,
            headlineFont = EditorialFontChoice.PLAYFAIR,
            headlineSizePt = 28,
            bodySizePt = 11,
            leadingPt = 15,
            tracking = -5,
            dropCapLines = 3,
            textWrapEnabled = true,
            textWrapOffsetPicas = 1.5f,
            linkedFrames = true,
            masterPageApplied = true,
            baselineGridSnap = true,
            colorSpace = "CMYK",
            imagePpi = 300
        )
    }

    fun loadSavedSpreadIntoWorkbench(saved: SavedSpreadEntity) {
        _activeMissionId.value = null
        val font = EditorialFontChoice.entries.find { it.id == saved.headlineFont }
            ?: EditorialFontChoice.PLAYFAIR
        val mode = if (saved.softwareMode == "QuarkXPress") {
            DtpSoftwareMode.QUARKXPRESS
        } else {
            DtpSoftwareMode.INDESIGN
        }
        _spreadState.value = SpreadLayoutState(
            title = saved.title,
            softwareMode = mode,
            columns = saved.columns,
            gutterPicas = saved.gutterPicas,
            marginPicas = saved.marginPicas,
            bleedMm = saved.bleedMm,
            headlineFont = font,
            headlineSizePt = saved.headlineSizePt,
            bodySizePt = saved.bodySizePt,
            leadingPt = saved.leadingPt,
            tracking = saved.tracking,
            dropCapLines = saved.dropCapLines,
            textWrapEnabled = saved.textWrapEnabled,
            textWrapOffsetPicas = saved.textWrapOffsetPicas,
            linkedFrames = saved.linkedFrames,
            masterPageApplied = saved.masterPageApplied,
            baselineGridSnap = saved.baselineGridSnap,
            colorSpace = saved.colorSpace,
            imagePpi = saved.imagePpi
        )
    }

    fun updateSpread(transform: (SpreadLayoutState) -> SpreadLayoutState) {
        _spreadState.update(transform)
    }

    fun toggleSoftwareMode() {
        _spreadState.update { current ->
            val next = if (current.softwareMode == DtpSoftwareMode.INDESIGN) {
                DtpSoftwareMode.QUARKXPRESS
            } else {
                DtpSoftwareMode.INDESIGN
            }
            current.copy(softwareMode = next)
        }
    }

    fun togglePreviewTrimMode() {
        _spreadState.update { it.copy(previewTrimMode = !it.previewTrimMode) }
    }

    fun getActiveMission(): DtpMission? {
        val id = _activeMissionId.value ?: return null
        return DtpCurriculum.missions.find { it.id == id }
    }

    fun evaluateActiveMission(): List<MissionObjectiveCheck> {
        val mission = getActiveMission() ?: return emptyList()
        return mission.evaluate(_spreadState.value)
    }

    fun submitMissionPressCheck(onResult: (score: Int, stars: Int, passed: Boolean) -> Unit) {
        val mission = getActiveMission() ?: return
        val checks = mission.evaluate(_spreadState.value)
        val passedCount = checks.count { it.isMet }
        val total = checks.size.coerceAtLeast(1)
        val ratio = passedCount.toFloat() / total.toFloat()
        val preflightBonus = if (_spreadState.value.preflightIssues.isEmpty()) 10 else 0
        val score = ((ratio * 90f).toInt() + preflightBonus).coerceIn(0, 100)
        val stars = when {
            passedCount == total && _spreadState.value.preflightIssues.isEmpty() -> 3
            passedCount == total -> 2
            ratio >= 0.65f -> 1
            else -> 0
        }
        viewModelScope.launch {
            repository.recordMissionCompletion(mission.id, score, stars)
        }
        onResult(score, stars, stars >= 1)
    }

    fun saveCurrentSpreadToPortfolio(customTitle: String) {
        val s = _spreadState.value
        val finalTitle = customTitle.trim().ifEmpty { s.title }
        viewModelScope.launch {
            repository.saveCustomSpread(
                SavedSpreadEntity(
                    title = finalTitle,
                    softwareMode = if (s.softwareMode == DtpSoftwareMode.QUARKXPRESS) "QuarkXPress" else "InDesign",
                    columns = s.columns,
                    gutterPicas = s.gutterPicas,
                    marginPicas = s.marginPicas,
                    bleedMm = s.bleedMm,
                    headlineFont = s.headlineFont.id,
                    headlineSizePt = s.headlineSizePt,
                    bodySizePt = s.bodySizePt,
                    leadingPt = s.leadingPt,
                    tracking = s.tracking,
                    dropCapLines = s.dropCapLines,
                    textWrapEnabled = s.textWrapEnabled,
                    textWrapOffsetPicas = s.textWrapOffsetPicas,
                    linkedFrames = s.linkedFrames,
                    masterPageApplied = s.masterPageApplied,
                    baselineGridSnap = s.baselineGridSnap,
                    colorSpace = s.colorSpace,
                    imagePpi = s.imagePpi,
                    preflightScore = s.preflightScore
                )
            )
        }
    }

    fun deleteSavedSpread(id: Int) {
        viewModelScope.launch {
            repository.deleteSpread(id)
        }
    }

    // Kerning Lab methods
    fun selectKerningWord(index: Int) {
        val challenges = DtpCurriculum.kerningChallenges
        if (index !in challenges.indices) return
        _activeKerningIndex.value = index
        _userKerningGaps.value = challenges[index].initialGaps
        _kerningRevealed.value = false
    }

    fun adjustKerningGap(pairIndex: Int, delta: Int) {
        val current = _userKerningGaps.value.toMutableList()
        if (pairIndex !in current.indices) return
        current[pairIndex] = (current[pairIndex] + delta).coerceIn(-18, 22)
        _userKerningGaps.value = current
    }

    fun setKerningGap(pairIndex: Int, value: Int) {
        val current = _userKerningGaps.value.toMutableList()
        if (pairIndex !in current.indices) return
        current[pairIndex] = value.coerceIn(-18, 22)
        _userKerningGaps.value = current
    }

    fun evaluateKerningChallenge(): Int {
        val challenge = DtpCurriculum.kerningChallenges[_activeKerningIndex.value]
        val user = _userKerningGaps.value
        val ideal = challenge.idealGaps
        var totalDeviation = 0
        for (i in ideal.indices) {
            totalDeviation += kotlin.math.abs((user.getOrNull(i) ?: 0) - ideal[i])
        }
        val avgDiff = totalDeviation.toFloat() / ideal.size.coerceAtLeast(1)
        val score = (100f - avgDiff * 6.5f).toInt().coerceIn(0, 100)
        _kerningRevealed.value = true
        viewModelScope.launch {
            repository.recordKerningScore(challenge.id, challenge.word, score)
        }
        return score
    }

    fun resetActiveKerningWord() {
        val challenge = DtpCurriculum.kerningChallenges[_activeKerningIndex.value]
        _userKerningGaps.value = challenge.initialGaps
        _kerningRevealed.value = false
    }

    fun recordQuizScore(modeTitle: String, score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            repository.recordQuizAttempt(modeTitle, score, totalQuestions)
        }
    }

    class Factory(private val repository: PicaPressRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PicaPressViewModel(repository) as T
        }
    }
}
