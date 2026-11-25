package com.example.model

enum class MasterId(
    val shortBadge: String,
    val indesignLabel: String,
    val quarkLabel: String
) {
    A_MASTER("A", "A-Parent", "A-Master"),
    B_MASTER("B", "B-Parent", "B-Master"),
    NONE("—", "[None]", "[None]");

    fun labelFor(mode: DtpSoftwareMode): String =
        if (mode == DtpSoftwareMode.QUARKXPRESS) quarkLabel else indesignLabel
}

enum class HeaderAlignment(val label: String) {
    OUTSIDE_MIRROR("Outside Edges (Mirror)"),
    CENTER("Centered"),
    LEFT("Flush Left")
}

enum class FolioPlacement(val label: String) {
    FOOTER_OUTSIDE("Footer Outside"),
    HEADER_OUTSIDE("Header Outside"),
    FOOTER_CENTER("Footer Center")
}

enum class FolioNumberStyle(val label: String, val example: String) {
    ARABIC("Arabic", "1, 2, 3"),
    ZERO_PADDED("Zero-Padded", "01, 02, 03"),
    ROMAN_LOWER("Roman Lower", "i, ii, iii"),
    ROMAN_UPPER("Roman Upper", "I, II, III"),
    SECTION_PREFIX("Section Prefix", "A-1, A-2")
}

data class MasterTemplateSpec(
    val id: MasterId,
    val showHeader: Boolean,
    val headerText: String,
    val headerAlignment: HeaderAlignment,
    val showHeaderRule: Boolean,
    val showFooter: Boolean,
    val footerText: String,
    val showFooterRule: Boolean,
    val showAutoFolio: Boolean,
    val folioPlacement: FolioPlacement,
    val folioStyle: FolioNumberStyle,
    val showMasterGuides: Boolean,
    val masterColumns: Int,
    val showAccentBand: Boolean
)

data class SimulatedDocPage(
    val pageIndex: Int, // 0-based index in document
    val contentTitle: String,
    val contentSnippet: String,
    val assignedMaster: MasterId,
    val overriddenHeaderText: String? = null // Non-null simulates Cmd+Shift-Click local master override
)

object MasterPageFormatter {
    fun formatPageNumber(
        pageNumber: Int,
        style: FolioNumberStyle,
        sectionPrefix: String = "A"
    ): String {
        val safeNum = pageNumber.coerceAtLeast(1)
        return when (style) {
            FolioNumberStyle.ARABIC -> safeNum.toString()
            FolioNumberStyle.ZERO_PADDED -> safeNum.toString().padStart(2, '0')
            FolioNumberStyle.ROMAN_LOWER -> toRoman(safeNum).lowercase()
            FolioNumberStyle.ROMAN_UPPER -> toRoman(safeNum)
            FolioNumberStyle.SECTION_PREFIX -> "$sectionPrefix-$safeNum"
        }
    }

    private fun toRoman(num: Int): String {
        val values = intArrayOf(100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        var remaining = num
        val sb = StringBuilder()
        for (i in values.indices) {
            while (remaining >= values[i]) {
                remaining -= values[i]
                sb.append(symbols[i])
            }
        }
        return sb.toString()
    }

    fun defaultMasterA(): MasterTemplateSpec = MasterTemplateSpec(
        id = MasterId.A_MASTER,
        showHeader = true,
        headerText = "SWISS DESIGN QUARTERLY",
        headerAlignment = HeaderAlignment.OUTSIDE_MIRROR,
        showHeaderRule = true,
        showFooter = true,
        footerText = "ZÜRICH PRESS • PRINT EDITION",
        showFooterRule = true,
        showAutoFolio = true,
        folioPlacement = FolioPlacement.FOOTER_OUTSIDE,
        folioStyle = FolioNumberStyle.ZERO_PADDED,
        showMasterGuides = true,
        masterColumns = 2,
        showAccentBand = false
    )

    fun defaultMasterB(): MasterTemplateSpec = MasterTemplateSpec(
        id = MasterId.B_MASTER,
        showHeader = false,
        headerText = "CHAPTER OPENER",
        headerAlignment = HeaderAlignment.CENTER,
        showHeaderRule = false,
        showFooter = true,
        footerText = "FEATURE MONOGRAPH",
        showFooterRule = false,
        showAutoFolio = true,
        folioPlacement = FolioPlacement.FOOTER_CENTER,
        folioStyle = FolioNumberStyle.ROMAN_UPPER,
        showMasterGuides = true,
        masterColumns = 3,
        showAccentBand = true
    )

    fun initialDocumentPages(): List<SimulatedDocPage> = listOf(
        SimulatedDocPage(
            pageIndex = 0,
            contentTitle = "COVER: GRID SYSTEMS",
            contentSnippet = "Issue 18 • Special Archival Feature on Modular Typography & Offset Lithography.",
            assignedMaster = MasterId.A_MASTER
        ),
        SimulatedDocPage(
            pageIndex = 1,
            contentTitle = "01. THE BASELINE RHYTHM",
            contentSnippet = "Synchronizing columns across facing spreads keeps the reader's eye anchored from verso to recto.",
            assignedMaster = MasterId.A_MASTER
        ),
        SimulatedDocPage(
            pageIndex = 2,
            contentTitle = "02. PICA & POINT GEOMETRY",
            contentSnippet = "Six picas equal one PostScript inch, allowing exact modular divisions of paper margins and gutters.",
            assignedMaster = MasterId.A_MASTER
        ),
        SimulatedDocPage(
            pageIndex = 3,
            contentTitle = "PART II: TYPOGRAPHIC SCALE",
            contentSnippet = "Chapter opener spread introducing optical kerning, high-contrast serifs, and Swiss geometric sans.",
            assignedMaster = MasterId.A_MASTER
        ),
        SimulatedDocPage(
            pageIndex = 4,
            contentTitle = "03. RUNNING HEADERS & FOLIOS",
            contentSnippet = "Automatic page number markers (<#>) update instantly when spreads are added, deleted, or renumbered.",
            assignedMaster = MasterId.A_MASTER
        ),
        SimulatedDocPage(
            pageIndex = 5,
            contentTitle = "COLOPHON & PRESS SPECS",
            contentSnippet = "Set in Playfair Display & Plus Jakarta Sans. Printed on 140gsm uncoated archival stock in CMYK.",
            assignedMaster = MasterId.A_MASTER
        )
    )
}
