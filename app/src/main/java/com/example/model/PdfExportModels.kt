package com.example.model

import kotlin.math.roundToInt

enum class PdfExportTab(val label: String, val inDesignTitle: String, val quarkTitle: String) {
    GENERAL("General", "General", "Pages & Layout"),
    COMPRESSION("Compression", "Compression", "Compression"),
    MARKS_BLEED("Marks & Bleed", "Marks and Bleeds", "Registration & Bleed"),
    OUTPUT("Output", "Output", "Color & Transparency")
}

enum class PdfStandard(
    val displayName: String,
    val acrobatVersion: String,
    val summary: String,
    val allowsLiveTransparency: Boolean,
    val forcesCmykOutput: Boolean
) {
    NONE(
        displayName = "None (Standard PDF)",
        acrobatVersion = "Acrobat 7 (PDF 1.6)",
        summary = "Unvalidated general PDF; risky for commercial offset RIPs.",
        allowsLiveTransparency = true,
        forcesCmykOutput = false
    ),
    PDF_X1A(
        displayName = "PDF/X-1a:2001",
        acrobatVersion = "Acrobat 4 (PDF 1.3)",
        summary = "Strict legacy CMYK-only print standard; flattens all transparency and embeds fonts.",
        allowsLiveTransparency = false,
        forcesCmykOutput = true
    ),
    PDF_X4(
        displayName = "PDF/X-4:2008",
        acrobatVersion = "Acrobat 7 (PDF 1.6)",
        summary = "Modern commercial print standard supporting live transparency and ICC color management.",
        allowsLiveTransparency = true,
        forcesCmykOutput = false
    )
}

enum class PdfPresetOption(val displayName: String, val description: String) {
    SMALLEST_FILE(
        displayName = "[Smallest File Size]",
        description = "Web/email proof only — 72–100 DPI, RGB, no bleed or marks."
    ),
    HIGH_QUALITY_PRINT(
        displayName = "[High Quality Print]",
        description = "Desktop & digital printers — 300 DPI, keeps source colors, no marks by default."
    ),
    PDF_X1A_2001(
        displayName = "[PDF/X-1a:2001]",
        description = "Traditional offset press — CMYK only, flattened transparency, 300 DPI."
    ),
    PDF_X4_2008(
        displayName = "[PDF/X-4:2008]",
        description = "Modern commercial press — live transparency, ICC profiles, 300 DPI."
    ),
    PRESS_QUALITY(
        displayName = "[Press Quality]",
        description = "High-res CMYK prepress defaults; customize bleed & marks per vendor."
    )
}

enum class ImageCompressionType(val displayName: String, val qualityNote: String, val sizeMultiplier: Float) {
    JPEG_LOW("JPEG (Low)", "Heavy artifacts & blockiness — unsuitable for print", 0.35f),
    JPEG_HIGH("JPEG (Maximum)", "Standard commercial balance — virtually lossless at 300 DPI", 0.85f),
    ZIP_LOSSLESS("ZIP (Lossless)", "Zero compression artifacts — ideal for fine art & sharp graphics", 1.55f)
}

enum class ExportColorConversion(val displayName: String, val explanation: String) {
    NO_CONVERSION(
        displayName = "No Color Conversion",
        explanation = "Leaves RGB objects untouched — commercial CMYK presses may reject or shift colors."
    ),
    CONVERT_PRESERVE_NUMBERS(
        displayName = "Convert to Destination (Preserve Numbers)",
        explanation = "Converts RGB photos to destination CMYK while keeping existing 100% K black text pure."
    ),
    CONVERT_ALL(
        displayName = "Convert to Destination (All)",
        explanation = "Forces every object into destination ICC profile; watch for 4-color rich black on body copy."
    )
}

enum class ExportOutputProfile(
    val displayName: String,
    val isCmyk: Boolean,
    val stockType: String,
    val maxTacPercent: Int,
    val description: String
) {
    SRGB_WEB(
        displayName = "sRGB IEC61966-2.1 (RGB)",
        isCmyk = false,
        stockType = "Screen / Web Only",
        maxTacPercent = 0,
        description = "Additive screen profile. Never submit sRGB for 4-color CMYK offset presses."
    ),
    SWOP_V2(
        displayName = "U.S. Web Coated (SWOP) v2",
        isCmyk = true,
        stockType = "Coated Magazine / Catalog",
        maxTacPercent = 300,
        description = "North American web offset standard for glossy & matte coated magazine stocks."
    ),
    COATED_FOGRA39(
        displayName = "Coated FOGRA39 (ISO 12647-2)",
        isCmyk = true,
        stockType = "European Coated Sheetfed",
        maxTacPercent = 330,
        description = "International sheetfed offset benchmark for high-gloss & silk coated papers."
    ),
    GRACOL_2006(
        displayName = "GRACoL 2006 Coated (ISO 12647)",
        isCmyk = true,
        stockType = "Premium Commercial Art Book",
        maxTacPercent = 340,
        description = "Wide-gamut commercial sheetfed profile favored for fine art books and luxury print."
    ),
    UNCOATED_FOGRA29(
        displayName = "Uncoated FOGRA29",
        isCmyk = true,
        stockType = "Uncoated / Absorptive Zine",
        maxTacPercent = 280,
        description = "Compensates for higher dot gain and lower ink absorption on uncoated editorial stock."
    )
}

enum class ProfileInclusionPolicy(val displayName: String) {
    DONT_INCLUDE("Don't Include Profiles"),
    INCLUDE_TAGGED("Include Tagged Source Profiles"),
    INCLUDE_DESTINATION("Include Destination Profile")
}

data class PdfExportSettingsState(
    val basePreset: PdfPresetOption = PdfPresetOption.SMALLEST_FILE,
    val standard: PdfStandard = PdfStandard.NONE,
    val exportAsSpreads: Boolean = true,
    val optimizeFastWebView: Boolean = true,
    val colorImageDpi: Int = 72,
    val compressionType: ImageCompressionType = ImageCompressionType.JPEG_LOW,
    val cropMarks: Boolean = false,
    val bleedMarks: Boolean = false,
    val registrationMarks: Boolean = false,
    val colorBars: Boolean = false,
    val pageInformation: Boolean = false,
    val markOffsetMm: Float = 2.1f,
    val bleedMm: Float = 0f,
    val includeSlugArea: Boolean = false,
    val colorConversion: ExportColorConversion = ExportColorConversion.NO_CONVERSION,
    val outputProfile: ExportOutputProfile = ExportOutputProfile.SRGB_WEB,
    val profileInclusion: ProfileInclusionPolicy = ProfileInclusionPolicy.DONT_INCLUDE,
    val convertSpotsToProcess: Boolean = false
) {
    val estimatedFileSizeMb: Float
        get() {
            val dpiFactor = (colorImageDpi / 150f) * (colorImageDpi / 150f)
            val baseMb = 1.4f * dpiFactor * compressionType.sizeMultiplier
            val profileOverhead = if (profileInclusion != ProfileInclusionPolicy.DONT_INCLUDE) 0.55f else 0.0f
            val bleedOverhead = if (bleedMm > 0f) 0.35f else 0.0f
            val marksOverhead = if (cropMarks || colorBars) 0.15f else 0.0f
            return ((baseMb + profileOverhead + bleedOverhead + marksOverhead) * 10f).roundToInt() / 10f
        }

    val isModifiedFromPreset: Boolean
        get() = this != PdfExportEngine.settingsForPreset(basePreset)
}

data class ExportCheckItem(
    val id: String,
    val tab: PdfExportTab,
    val label: String,
    val passed: Boolean,
    val detail: String
)

data class PdfExportBrief(
    val id: Int,
    val code: String,
    val title: String,
    val clientName: String,
    val pressSpecSummary: String,
    val vendorRequirements: List<String>,
    val hintText: String,
    val startingState: PdfExportSettingsState,
    val evaluate: (PdfExportSettingsState) -> List<ExportCheckItem>
)

data class PdfExportEvaluation(
    val checks: List<ExportCheckItem>,
    val scorePercent: Int,
    val ripPassed: Boolean,
    val headlineVerdict: String,
    val ripLogLines: List<String>
) {
    fun tabHasFailures(tab: PdfExportTab): Boolean = checks.any { it.tab == tab && !it.passed }
    fun tabAllPassed(tab: PdfExportTab): Boolean =
        checks.any { it.tab == tab } && checks.filter { it.tab == tab }.all { it.passed }
}

object PdfExportEngine {

    fun settingsForPreset(preset: PdfPresetOption): PdfExportSettingsState = when (preset) {
        PdfPresetOption.SMALLEST_FILE -> PdfExportSettingsState(
            basePreset = preset,
            standard = PdfStandard.NONE,
            exportAsSpreads = true,
            optimizeFastWebView = true,
            colorImageDpi = 72,
            compressionType = ImageCompressionType.JPEG_LOW,
            cropMarks = false,
            bleedMarks = false,
            registrationMarks = false,
            colorBars = false,
            pageInformation = false,
            markOffsetMm = 2.1f,
            bleedMm = 0f,
            includeSlugArea = false,
            colorConversion = ExportColorConversion.NO_CONVERSION,
            outputProfile = ExportOutputProfile.SRGB_WEB,
            profileInclusion = ProfileInclusionPolicy.DONT_INCLUDE,
            convertSpotsToProcess = false
        )
        PdfPresetOption.HIGH_QUALITY_PRINT -> PdfExportSettingsState(
            basePreset = preset,
            standard = PdfStandard.NONE,
            exportAsSpreads = false,
            optimizeFastWebView = false,
            colorImageDpi = 300,
            compressionType = ImageCompressionType.JPEG_HIGH,
            cropMarks = false,
            bleedMarks = false,
            registrationMarks = false,
            colorBars = false,
            pageInformation = false,
            markOffsetMm = 2.1f,
            bleedMm = 0f,
            includeSlugArea = false,
            colorConversion = ExportColorConversion.NO_CONVERSION,
            outputProfile = ExportOutputProfile.SWOP_V2,
            profileInclusion = ProfileInclusionPolicy.INCLUDE_TAGGED,
            convertSpotsToProcess = false
        )
        PdfPresetOption.PDF_X1A_2001 -> PdfExportSettingsState(
            basePreset = preset,
            standard = PdfStandard.PDF_X1A,
            exportAsSpreads = false,
            optimizeFastWebView = false,
            colorImageDpi = 300,
            compressionType = ImageCompressionType.JPEG_HIGH,
            cropMarks = false,
            bleedMarks = false,
            registrationMarks = false,
            colorBars = false,
            pageInformation = false,
            markOffsetMm = 3.0f,
            bleedMm = 0f,
            includeSlugArea = false,
            colorConversion = ExportColorConversion.CONVERT_PRESERVE_NUMBERS,
            outputProfile = ExportOutputProfile.SWOP_V2,
            profileInclusion = ProfileInclusionPolicy.INCLUDE_DESTINATION,
            convertSpotsToProcess = true
        )
        PdfPresetOption.PDF_X4_2008 -> PdfExportSettingsState(
            basePreset = preset,
            standard = PdfStandard.PDF_X4,
            exportAsSpreads = false,
            optimizeFastWebView = false,
            colorImageDpi = 300,
            compressionType = ImageCompressionType.JPEG_HIGH,
            cropMarks = false,
            bleedMarks = false,
            registrationMarks = false,
            colorBars = false,
            pageInformation = false,
            markOffsetMm = 3.0f,
            bleedMm = 0f,
            includeSlugArea = false,
            colorConversion = ExportColorConversion.CONVERT_PRESERVE_NUMBERS,
            outputProfile = ExportOutputProfile.COATED_FOGRA39,
            profileInclusion = ProfileInclusionPolicy.INCLUDE_DESTINATION,
            convertSpotsToProcess = false
        )
        PdfPresetOption.PRESS_QUALITY -> PdfExportSettingsState(
            basePreset = preset,
            standard = PdfStandard.NONE,
            exportAsSpreads = false,
            optimizeFastWebView = false,
            colorImageDpi = 300,
            compressionType = ImageCompressionType.JPEG_HIGH,
            cropMarks = false,
            bleedMarks = false,
            registrationMarks = false,
            colorBars = false,
            pageInformation = false,
            markOffsetMm = 2.1f,
            bleedMm = 0f,
            includeSlugArea = false,
            colorConversion = ExportColorConversion.CONVERT_PRESERVE_NUMBERS,
            outputProfile = ExportOutputProfile.SWOP_V2,
            profileInclusion = ProfileInclusionPolicy.DONT_INCLUDE,
            convertSpotsToProcess = false
        )
    }

    val printBriefs: List<PdfExportBrief> = listOf(
        PdfExportBrief(
            id = 1,
            code = "JOB-01 • OFFSET COVER",
            title = "Coated Magazine Cover (Modern RIP)",
            clientName = "Helvetica Quarterly — Issue 14",
            pressSpecSummary = "Modern sheetfed offset printer using Adobe PDF Print Engine. Requires single pages, 300 DPI imagery, 3 mm bleed with trim marks, and CMYK ICC conversion.",
            vendorRequirements = listOf(
                "Standard: PDF/X-4:2008 (keeps live drop-shadow transparency)",
                "Layout: Export as Single Pages (NOT Reader Spreads)",
                "Resolution: 300 DPI color images with JPEG Maximum or ZIP",
                "Marks & Bleed: Crop/Trim Marks ON + 3.0 mm bleed",
                "Output: Convert to Coated FOGRA39 or SWOP v2 (Preserve Numbers) + Include Destination Profile"
            ),
            hintText = "Start from the [PDF/X-4:2008] preset, verify 300 DPI, enable Crop Marks and 3.0 mm Bleed under Marks & Bleed, and confirm Coated FOGRA39 or SWOP v2 with Destination Profile included.",
            startingState = settingsForPreset(PdfPresetOption.SMALLEST_FILE),
            evaluate = { s ->
                listOf(
                    ExportCheckItem(
                        id = "std_x4",
                        tab = PdfExportTab.GENERAL,
                        label = "PDF/X-4:2008 Standard",
                        passed = s.standard == PdfStandard.PDF_X4,
                        detail = if (s.standard == PdfStandard.PDF_X4) "PDF/X-4 active — live transparency preserved." else "Set Standard to PDF/X-4:2008 under General."
                    ),
                    ExportCheckItem(
                        id = "single_pages",
                        tab = PdfExportTab.GENERAL,
                        label = "Export Single Pages (Imposition Ready)",
                        passed = !s.exportAsSpreads,
                        detail = if (!s.exportAsSpreads) "Single pages enabled for press imposition." else "Disable 'Spreads' — print houses impose single pages."
                    ),
                    ExportCheckItem(
                        id = "dpi_300",
                        tab = PdfExportTab.COMPRESSION,
                        label = "300 DPI High-Res Images",
                        passed = s.colorImageDpi >= 300 && s.compressionType != ImageCompressionType.JPEG_LOW,
                        detail = if (s.colorImageDpi >= 300 && s.compressionType != ImageCompressionType.JPEG_LOW) {
                            "${s.colorImageDpi} DPI with ${s.compressionType.displayName} — sharp halftones."
                        } else {
                            "Raise Color DPI to 300 and avoid JPEG (Low) compression."
                        }
                    ),
                    ExportCheckItem(
                        id = "marks_bleed_3mm",
                        tab = PdfExportTab.MARKS_BLEED,
                        label = "Crop Marks & 3.0 mm Bleed",
                        passed = s.cropMarks && s.bleedMm >= 3.0f,
                        detail = if (s.cropMarks && s.bleedMm >= 3.0f) {
                            "Trim marks active with ${s.bleedMm} mm bleed safety."
                        } else {
                            "Enable Crop Marks and set Bleed to at least 3.0 mm (0.125\")."
                        }
                    ),
                    ExportCheckItem(
                        id = "cmyk_coated",
                        tab = PdfExportTab.OUTPUT,
                        label = "Coated CMYK Output & ICC Profile",
                        passed = s.colorConversion != ExportColorConversion.NO_CONVERSION &&
                            (s.outputProfile == ExportOutputProfile.COATED_FOGRA39 || s.outputProfile == ExportOutputProfile.SWOP_V2) &&
                            s.profileInclusion == ProfileInclusionPolicy.INCLUDE_DESTINATION,
                        detail = if (s.outputProfile.isCmyk && s.colorConversion != ExportColorConversion.NO_CONVERSION && s.profileInclusion == ProfileInclusionPolicy.INCLUDE_DESTINATION) {
                            "Calibrated ${s.outputProfile.displayName} with embedded output intent."
                        } else {
                            "Convert to Coated FOGRA39 or SWOP v2 and include Destination Profile."
                        }
                    )
                )
            }
        ),
        PdfExportBrief(
            id = 2,
            code = "JOB-02 • LEGACY OFFSET",
            title = "Fine Art Monograph (Legacy RIP)",
            clientName = "Basel Architecture Press",
            pressSpecSummary = "Older PostScript RIP requires flattened PDF/X-1a, lossless ZIP imagery, full press marks (Crop + Registration + Color Bars), and all spot inks converted to GRACoL CMYK.",
            vendorRequirements = listOf(
                "Standard: PDF/X-1a:2001 (flattens transparency for legacy RIP)",
                "Resolution: 300 DPI with ZIP (Lossless) compression",
                "Marks & Bleed: Crop Marks + Registration Marks + Color Bars + 3.0 mm Bleed",
                "Output: Convert to GRACoL 2006 Coated + Convert All Spots to Process CMYK"
            ),
            hintText = "Choose PDF/X-1a:2001 in General, switch Compression to ZIP (Lossless) at 300 DPI, turn on Crop, Registration, and Color Bars with 3.0 mm Bleed, and pick GRACoL 2006 + Spot-to-Process in Output.",
            startingState = settingsForPreset(PdfPresetOption.HIGH_QUALITY_PRINT),
            evaluate = { s ->
                listOf(
                    ExportCheckItem(
                        id = "std_x1a",
                        tab = PdfExportTab.GENERAL,
                        label = "PDF/X-1a:2001 Flattened Standard",
                        passed = s.standard == PdfStandard.PDF_X1A && !s.exportAsSpreads,
                        detail = if (s.standard == PdfStandard.PDF_X1A && !s.exportAsSpreads) {
                            "PDF/X-1a single pages ready for legacy RIP."
                        } else {
                            "Select PDF/X-1a:2001 and Single Pages under General."
                        }
                    ),
                    ExportCheckItem(
                        id = "zip_300",
                        tab = PdfExportTab.COMPRESSION,
                        label = "300 DPI + ZIP Lossless",
                        passed = s.colorImageDpi >= 300 && s.compressionType == ImageCompressionType.ZIP_LOSSLESS,
                        detail = if (s.colorImageDpi >= 300 && s.compressionType == ImageCompressionType.ZIP_LOSSLESS) {
                            "Lossless ZIP at ${s.colorImageDpi} DPI preserves fine architectural lines."
                        } else {
                            "Set 300 DPI and ZIP (Lossless) under Compression."
                        }
                    ),
                    ExportCheckItem(
                        id = "full_press_marks",
                        tab = PdfExportTab.MARKS_BLEED,
                        label = "Crop, Registration, Color Bars & 3mm Bleed",
                        passed = s.cropMarks && s.registrationMarks && s.colorBars && s.bleedMm >= 3.0f,
                        detail = if (s.cropMarks && s.registrationMarks && s.colorBars && s.bleedMm >= 3.0f) {
                            "Full pressman inspection marks + ${s.bleedMm} mm bleed enabled."
                        } else {
                            "Enable Crop Marks, Registration Marks, Color Bars, and 3.0 mm Bleed."
                        }
                    ),
                    ExportCheckItem(
                        id = "gracol_spots",
                        tab = PdfExportTab.OUTPUT,
                        label = "GRACoL 2006 Coated + Convert Spots",
                        passed = s.outputProfile == ExportOutputProfile.GRACOL_2006 &&
                            s.colorConversion != ExportColorConversion.NO_CONVERSION &&
                            s.convertSpotsToProcess,
                        detail = if (s.outputProfile == ExportOutputProfile.GRACOL_2006 && s.convertSpotsToProcess) {
                            "All spot plates mapped cleanly into 4-color GRACoL CMYK."
                        } else {
                            "Convert to GRACoL 2006 Coated and toggle 'Convert All Spots to Process'."
                        }
                    )
                )
            }
        ),
        PdfExportBrief(
            id = 3,
            code = "JOB-03 • UNCOATED ZINE",
            title = "Uncoated Editorial Zine (Munken Stock)",
            clientName = "Nordic Type Foundry",
            pressSpecSummary = "Absorbent uncoated paper suffers severe ink dot gain if exported with a coated profile. Configure Uncoated FOGRA29, preserve 100% K text, and include Page Info slug.",
            vendorRequirements = listOf(
                "Standard: PDF/X-4:2008 & Single Pages",
                "Resolution: 300 DPI (JPEG Maximum or ZIP)",
                "Marks & Bleed: Crop Marks + Page Information + 3.0 mm Bleed",
                "Output: Convert to Destination (Preserve Numbers) -> Uncoated FOGRA29"
            ),
            hintText = "Use PDF/X-4:2008, keep 300 DPI, enable Crop Marks + Page Information + 3.0 mm Bleed, then set Output to 'Convert to Destination (Preserve Numbers)' with Uncoated FOGRA29.",
            startingState = settingsForPreset(PdfPresetOption.PRESS_QUALITY),
            evaluate = { s ->
                listOf(
                    ExportCheckItem(
                        id = "zine_x4",
                        tab = PdfExportTab.GENERAL,
                        label = "PDF/X-4 Single Pages",
                        passed = s.standard == PdfStandard.PDF_X4 && !s.exportAsSpreads,
                        detail = if (s.standard == PdfStandard.PDF_X4 && !s.exportAsSpreads) {
                            "Modern PDF/X-4 single pages configured."
                        } else {
                            "Select PDF/X-4:2008 and Single Pages."
                        }
                    ),
                    ExportCheckItem(
                        id = "zine_dpi",
                        tab = PdfExportTab.COMPRESSION,
                        label = "300 DPI Editorial Images",
                        passed = s.colorImageDpi >= 300 && s.compressionType != ImageCompressionType.JPEG_LOW,
                        detail = if (s.colorImageDpi >= 300 && s.compressionType != ImageCompressionType.JPEG_LOW) {
                            "${s.colorImageDpi} DPI ready for uncoated stochastic screening."
                        } else {
                            "Set Color Image DPI to 300 with JPEG (Maximum) or ZIP."
                        }
                    ),
                    ExportCheckItem(
                        id = "zine_marks",
                        tab = PdfExportTab.MARKS_BLEED,
                        label = "Crop Marks, Page Info & 3.0 mm Bleed",
                        passed = s.cropMarks && s.pageInformation && s.bleedMm >= 3.0f,
                        detail = if (s.cropMarks && s.pageInformation && s.bleedMm >= 3.0f) {
                            "Crop marks, filename/date slug info, and ${s.bleedMm} mm bleed active."
                        } else {
                            "Turn on Crop Marks, Page Information, and 3.0 mm Bleed."
                        }
                    ),
                    ExportCheckItem(
                        id = "zine_uncoated",
                        tab = PdfExportTab.OUTPUT,
                        label = "Uncoated FOGRA29 (Preserve Numbers)",
                        passed = s.outputProfile == ExportOutputProfile.UNCOATED_FOGRA29 &&
                            s.colorConversion == ExportColorConversion.CONVERT_PRESERVE_NUMBERS,
                        detail = if (s.outputProfile == ExportOutputProfile.UNCOATED_FOGRA29 && s.colorConversion == ExportColorConversion.CONVERT_PRESERVE_NUMBERS) {
                            "280% TAC limit protects uncoated stock while keeping black text crisp."
                        } else {
                            "Choose 'Convert to Destination (Preserve Numbers)' and Uncoated FOGRA29."
                        }
                    )
                )
            }
        ),
        PdfExportBrief(
            id = 4,
            code = "JOB-04 • WIDE FORMAT",
            title = "Exhibition Poster (5 mm Heavy Bleed)",
            clientName = "Kunsthalle Zurich",
            pressSpecSummary = "Large-format posters trimmed on an industrial board cutter require extra 5.0 mm bleed, both Crop & Bleed Marks, Offset >= 3.0 mm so marks never enter the bleed zone, and CMYK output.",
            vendorRequirements = listOf(
                "Standard: PDF/X-4:2008 & Single Pages",
                "Resolution: 150–300 DPI (not bloated 600 DPI, not blurry 72 DPI)",
                "Marks & Bleed: Crop Marks + Bleed Marks + 5.0 mm Bleed + Offset >= 3.0 mm",
                "Output: Convert to Coated CMYK (FOGRA39 or SWOP v2) + Include Destination Profile"
            ),
            hintText = "Set PDF/X-4:2008, pick 150 or 300 DPI, turn on Crop Marks + Bleed Marks with 5.0 mm Bleed and Mark Offset >= 3.0 mm, and convert to Coated FOGRA39 or SWOP v2.",
            startingState = settingsForPreset(PdfPresetOption.SMALLEST_FILE),
            evaluate = { s ->
                listOf(
                    ExportCheckItem(
                        id = "poster_x4",
                        tab = PdfExportTab.GENERAL,
                        label = "PDF/X-4 Single Page Poster",
                        passed = s.standard == PdfStandard.PDF_X4 && !s.exportAsSpreads,
                        detail = if (s.standard == PdfStandard.PDF_X4 && !s.exportAsSpreads) {
                            "PDF/X-4 single sheet configured."
                        } else {
                            "Use PDF/X-4:2008 and disable Spreads."
                        }
                    ),
                    ExportCheckItem(
                        id = "poster_dpi",
                        tab = PdfExportTab.COMPRESSION,
                        label = "150–300 DPI Optimal Viewing Resolution",
                        passed = s.colorImageDpi in 150..300 && s.compressionType != ImageCompressionType.JPEG_LOW,
                        detail = if (s.colorImageDpi in 150..300 && s.compressionType != ImageCompressionType.JPEG_LOW) {
                            "${s.colorImageDpi} DPI avoids RIP memory bloat while staying crisp."
                        } else if (s.colorImageDpi > 300) {
                            "600 DPI creates oversized RIP files on large-format posters — use 150 or 300 DPI."
                        } else {
                            "72 DPI is too blocky even at poster distance — raise to 150 or 300 DPI."
                        }
                    ),
                    ExportCheckItem(
                        id = "poster_bleed_5mm",
                        tab = PdfExportTab.MARKS_BLEED,
                        label = "5.0 mm Bleed, Crop+Bleed Marks & >= 3mm Offset",
                        passed = s.cropMarks && s.bleedMarks && s.bleedMm >= 5.0f && s.markOffsetMm >= 3.0f,
                        detail = if (s.cropMarks && s.bleedMarks && s.bleedMm >= 5.0f && s.markOffsetMm >= 3.0f) {
                            "Marks sit safely outside the 5.0 mm bleed zone."
                        } else {
                            "Enable Crop & Bleed Marks, set Bleed to 5.0 mm, and increase Mark Offset to >= 3.0 mm."
                        }
                    ),
                    ExportCheckItem(
                        id = "poster_cmyk",
                        tab = PdfExportTab.OUTPUT,
                        label = "CMYK Output Profile & Output Intent",
                        passed = s.outputProfile.isCmyk &&
                            s.colorConversion != ExportColorConversion.NO_CONVERSION &&
                            s.profileInclusion == ProfileInclusionPolicy.INCLUDE_DESTINATION,
                        detail = if (s.outputProfile.isCmyk && s.colorConversion != ExportColorConversion.NO_CONVERSION && s.profileInclusion == ProfileInclusionPolicy.INCLUDE_DESTINATION) {
                            "${s.outputProfile.displayName} embedded for wide-format RIP."
                        } else {
                            "Convert to a Coated CMYK profile and include the Destination Profile."
                        }
                    )
                )
            }
        )
    )

    fun evaluate(brief: PdfExportBrief, state: PdfExportSettingsState): PdfExportEvaluation {
        val checks = brief.evaluate(state)
        val passedCount = checks.count { it.passed }
        val scorePercent = if (checks.isEmpty()) 100 else ((passedCount.toFloat() / checks.size) * 100f).roundToInt()
        val ripPassed = passedCount == checks.size

        val logLines = buildList {
            add("RIP ENGINE: Initializing PDF Prepress FlightCheck...")
            add("PDF STANDARD: ${state.standard.displayName} (${state.standard.acrobatVersion})")
            add("IMPOSITION: ${if (state.exportAsSpreads) "READER SPREADS [WARN]" else "SINGLE PAGES [OK]"}")
            add("RASTER RES: ${state.colorImageDpi} DPI • ${state.compressionType.displayName} • Est. ${state.estimatedFileSizeMb} MB")
            add("GEOMETRY: TrimBox + ${state.bleedMm} mm BleedBox • Mark Offset ${state.markOffsetMm} mm")
            add("COLOR INTENT: ${state.outputProfile.displayName} (${if (state.outputProfile.isCmyk) "CMYK" else "RGB REJECT"})")
            checks.forEach { item ->
                val tag = if (item.passed) "[PASS]" else "[FAIL]"
                add("$tag ${item.label}: ${item.detail}")
            }
        }

        val headline = when {
            ripPassed -> "PRESS-READY PDF CERTIFIED"
            scorePercent >= 60 -> "RIP WARNING — ADJUST FAILED SPECS"
            else -> "PREPRESS REJECTED — NOT PRINT-READY"
        }

        return PdfExportEvaluation(
            checks = checks,
            scorePercent = scorePercent,
            ripPassed = ripPassed,
            headlineVerdict = headline,
            ripLogLines = logLines
        )
    }
}
