package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt

enum class WorkingColorModel(val label: String, val badge: String, val synthesisRule: String) {
    RGB(
        label = "RGB (Additive Light)",
        badge = "RGB",
        synthesisRule = "Red + Green + Blue light emits from screens (R255 G255 B255 = White). Wider gamut than print!"
    ),
    CMYK(
        label = "CMYK (Subtractive Ink)",
        badge = "CMYK",
        synthesisRule = "Cyan + Magenta + Yellow + Key Black inks absorb light on paper (C0 M0 Y0 K0 = Paper White)."
    )
}

enum class IccColorProfile(
    val displayName: String,
    val regionAndStock: String,
    val maxTacPercent: Int,
    val dotGainShift: Float,       // Simulates darker midtones on uncoated/newsprint stocks
    val paperWarmthShift: Float,   // Simulates creamy uncoated paper vs bright white coated
    val summary: String
) {
    FOGRA39(
        displayName = "ISO Coated v2 (FOGRA39)",
        regionAndStock = "Europe • Coated Gloss/Matte",
        maxTacPercent = 300,
        dotGainShift = 0.03f,
        paperWarmthShift = 0.02f,
        summary = "European commercial offset standard for coated magazine covers and art books."
    ),
    SWOP_V2(
        displayName = "U.S. Web Coated (SWOP) v2",
        regionAndStock = "North America • Web Offset",
        maxTacPercent = 300,
        dotGainShift = 0.06f,
        paperWarmthShift = 0.04f,
        summary = "Classic North American high-speed magazine web press profile with tighter 300% TAC limit."
    ),
    GRACOL_2013(
        displayName = "GRACoL 2013 (CRPC6)",
        regionAndStock = "North America • Premium Coated",
        maxTacPercent = 320,
        dotGainShift = 0.02f,
        paperWarmthShift = 0.01f,
        summary = "Modern G7-calibrated sheetfed press profile for high-saturation posters and fine packaging."
    ),
    PSO_UNCOATED(
        displayName = "PSO Uncoated v3 (FOGRA52)",
        regionAndStock = "Global • Uncoated Archival Stock",
        maxTacPercent = 260,
        dotGainShift = 0.11f,
        paperWarmthShift = 0.09f,
        summary = "Designed for absorbent uncoated paper: lower ink limit (260% TAC), softer blacks, warmer paper white."
    )
}

enum class RenderingIntent(val label: String, val explanation: String) {
    RELATIVE_COLORIMETRIC(
        label = "Relative Colorimetric",
        explanation = "Preserves exact in-gamut brand swatches and clips out-of-gamut colors to the closest printable ink."
    ),
    PERCEPTUAL(
        label = "Perceptual",
        explanation = "Compresses the entire color gamut smoothly to preserve photographic gradients and relationships."
    )
}

data class CmykValues(
    val c: Int,
    val m: Int,
    val y: Int,
    val k: Int
) {
    val totalInkCoverage: Int get() = c + m + y + k
}

data class SwatchCalibrationJob(
    val id: Int,
    val code: String,
    val swatchName: String,
    val substrateLabel: String,
    val brief: String,
    val targetCmyk: CmykValues,
    val targetProfile: IccColorProfile,
    val requiredBleedMm: Int,
    val initialColorModel: WorkingColorModel,
    val initialCmyk: CmykValues,
    val initialProfile: IccColorProfile,
    val initialBleedMm: Int,
    val lessonTakeaway: String
)

object ColorManagementEngine {

    val calibrationJobs: List<SwatchCalibrationJob> = listOf(
        SwatchCalibrationJob(
            id = 1,
            code = "SWATCH 01 • SWISS VERMILION",
            swatchName = "Zürich Poster Vermilion",
            substrateLabel = "Coated Gloss 170gsm • Sheetfed Offset",
            brief = "The designer created a vibrant cover in RGB mode with 0mm bleed. Switch to CMYK, assign ISO Coated v2 (FOGRA39), calibrate the C/M/Y/K plates to match the printed vermilion drawdown swatch, and set 3mm bleed.",
            targetCmyk = CmykValues(c = 0, m = 88, y = 92, k = 4),
            targetProfile = IccColorProfile.FOGRA39,
            requiredBleedMm = 3,
            initialColorModel = WorkingColorModel.RGB,
            initialCmyk = CmykValues(c = 12, m = 65, y = 52, k = 0),
            initialProfile = IccColorProfile.PSO_UNCOATED,
            initialBleedMm = 0,
            lessonTakeaway = "Pure RGB red (255, 0, 0) cannot be reproduced identically in CMYK—calibrated Magenta + Yellow process plates with FOGRA39 yield a rich, predictable print red."
        ),
        SwatchCalibrationJob(
            id = 2,
            code = "SWATCH 02 • RICH BLACK",
            swatchName = "Editorial Rich Black",
            substrateLabel = "Web Offset Magazine • 300% Max TAC",
            brief = "A full-bleed dark editorial panel was accidentally built with 400% ink (C100 M100 Y100 K100), which will smudge on high-speed web rollers! Assign U.S. Web Coated (SWOP) v2, dial in balanced Rich Black under 300% TAC, and extend bleed to 3mm.",
            targetCmyk = CmykValues(c = 60, m = 40, y = 40, k = 100),
            targetProfile = IccColorProfile.SWOP_V2,
            requiredBleedMm = 3,
            initialColorModel = WorkingColorModel.CMYK,
            initialCmyk = CmykValues(c = 95, m = 90, y = 90, k = 100),
            initialProfile = IccColorProfile.GRACOL_2013,
            initialBleedMm = 1,
            lessonTakeaway = "Rich Black (C60 M40 Y40 K100 = 240% TAC) creates deeper blacks than 100% K alone while staying safely below the 300% Total Ink Coverage limit."
        ),
        SwatchCalibrationJob(
            id = 3,
            code = "SWATCH 03 • PROCESS CYAN",
            swatchName = "Alpine Exhibition Cyan",
            substrateLabel = "Premium Coated Silk • Large Format",
            brief = "Large-format exhibition posters require 5mm trim bleed and G7-calibrated GRACoL 2013 output. Convert from RGB to CMYK, choose GRACoL 2013, match the crisp process cyan swatch, and set 5mm bleed.",
            targetCmyk = CmykValues(c = 86, m = 14, y = 4, k = 0),
            targetProfile = IccColorProfile.GRACOL_2013,
            requiredBleedMm = 5,
            initialColorModel = WorkingColorModel.RGB,
            initialCmyk = CmykValues(c = 55, m = 38, y = 26, k = 10),
            initialProfile = IccColorProfile.SWOP_V2,
            initialBleedMm = 2,
            lessonTakeaway = "Electric RGB cyan shifts darker in CMYK; using GRACoL 2013 on premium coated stock maximizes printable blue-cyan saturation."
        ),
        SwatchCalibrationJob(
            id = 4,
            code = "SWATCH 04 • ARCHIVAL OLIVE",
            swatchName = "Monograph Archival Olive",
            substrateLabel = "Uncoated Cotton Paper • 260% Max TAC",
            brief = "Uncoated archival paper absorbs ink and softens contrast. Select PSO Uncoated v3 (FOGRA52) to soft-proof accurately, match the muted olive monograph swatch safely under 260% TAC, and set 3mm bleed.",
            targetCmyk = CmykValues(c = 42, m = 22, y = 78, k = 28),
            targetProfile = IccColorProfile.PSO_UNCOATED,
            requiredBleedMm = 3,
            initialColorModel = WorkingColorModel.CMYK,
            initialCmyk = CmykValues(c = 68, m = 10, y = 45, k = 5),
            initialProfile = IccColorProfile.FOGRA39,
            initialBleedMm = 0,
            lessonTakeaway = "Always match your ICC profile to the physical paper stock—sending a Coated FOGRA39 file to an Uncoated press causes unexpected dot gain and muddy midtones."
        )
    )

    /**
     * Converts CMYK + ICC profile substrate physics into a simulated soft-proof Compose Color.
     * In RGB working mode (unconverted), simulates an overly luminous/cool screen color with gamut shift.
     */
    fun simulateRenderedSwatch(
        cmyk: CmykValues,
        profile: IccColorProfile,
        colorModel: WorkingColorModel,
        softProofPaper: Boolean
    ): Color {
        val c = (cmyk.c / 100f).coerceIn(0f, 1f)
        val m = (cmyk.m / 100f).coerceIn(0f, 1f)
        val y = (cmyk.y / 100f).coerceIn(0f, 1f)
        val k = (cmyk.k / 100f).coerceIn(0f, 1f)

        // Subtractive ink reflectance model + ICC profile dot gain & paper warmth
        val gain = if (softProofPaper && colorModel == WorkingColorModel.CMYK) profile.dotGainShift else 0f
        val warmth = if (softProofPaper && colorModel == WorkingColorModel.CMYK) profile.paperWarmthShift else 0f

        val effC = (c + gain * c * (1f - c)).coerceIn(0f, 1f)
        val effM = (m + gain * m * (1f - m)).coerceIn(0f, 1f)
        val effY = (y + gain * y * (1f - y)).coerceIn(0f, 1f)
        val effK = (k + gain * 0.5f * k * (1f - k)).coerceIn(0f, 1f)

        var r = (1f - effC) * (1f - effK)
        var g = (1f - effM) * (1f - effK)
        var b = (1f - effY) * (1f - effK)

        if (colorModel == WorkingColorModel.RGB) {
            // Uncalibrated screen RGB looks artificially bright/electric & slightly cool
            r = (r * 1.12f + 0.05f).coerceIn(0f, 1f)
            g = (g * 1.10f + 0.04f).coerceIn(0f, 1f)
            b = (b * 1.15f + 0.06f).coerceIn(0f, 1f)
        } else if (softProofPaper) {
            // Paper white-point simulation (slight warm/cream tint and ink absorption ceiling)
            r = (r * (0.97f - warmth * 0.15f)).coerceIn(0f, 1f)
            g = (g * (0.96f - warmth * 0.35f)).coerceIn(0f, 1f)
            b = (b * (0.94f - warmth * 0.75f)).coerceIn(0f, 1f)
        }

        return Color(red = r, green = g, blue = b, alpha = 1f)
    }

    /**
     * Converts CMYK to approximate 0..255 RGB values for live DTP Swatch Inspector readout.
     */
    fun toRgbTriple(cmyk: CmykValues): Triple<Int, Int, Int> {
        val c = cmyk.c / 100f
        val m = cmyk.m / 100f
        val y = cmyk.y / 100f
        val k = cmyk.k / 100f
        val r = ((1f - c) * (1f - k) * 255f).toInt().coerceIn(0, 255)
        val g = ((1f - m) * (1f - k) * 255f).toInt().coerceIn(0, 255)
        val b = ((1f - y) * (1f - k) * 255f).toInt().coerceIn(0, 255)
        return Triple(r, g, b)
    }

    /**
     * Computes a realistic press-room Delta-E (ΔE) score between user's digital output and the target swatch.
     * Accounts for C/M/Y/K channel distance, RGB unconverted gamut penalty, and ICC profile mismatch.
     */
    fun computeDeltaE(
        userCmyk: CmykValues,
        userProfile: IccColorProfile,
        userColorModel: WorkingColorModel,
        targetCmyk: CmykValues,
        targetProfile: IccColorProfile
    ): Float {
        val dc = (userCmyk.c - targetCmyk.c).toFloat()
        val dm = (userCmyk.m - targetCmyk.m).toFloat()
        val dy = (userCmyk.y - targetCmyk.y).toFloat()
        val dk = (userCmyk.k - targetCmyk.k).toFloat()

        // Weighted Euclidean channel distance scaled to realistic Delta-E range
        val channelDelta = sqrt(dc * dc * 0.14f + dm * dm * 0.16f + dy * dy * 0.12f + dk * dk * 0.22f)
        val rgbGamutPenalty = if (userColorModel == WorkingColorModel.RGB) 7.5f else 0f
        val profileMismatchPenalty = if (userProfile != targetProfile) {
            abs(userProfile.dotGainShift - targetProfile.dotGainShift) * 45f + 2.8f
        } else {
            0f
        }

        return (channelDelta + rgbGamutPenalty + profileMismatchPenalty).coerceIn(0f, 50f)
    }
}
