package com.example.model

enum class DtpSoftwareMode(
    val displayName: String,
    val shortBadge: String,
    val placeShortcut: String,
    val previewShortcut: String,
    val wrapMenuName: String,
    val packageMenuName: String,
    val masterPageTerm: String
) {
    INDESIGN(
        displayName = "Adobe InDesign",
        shortBadge = "Id",
        placeShortcut = "Cmd/Ctrl+D (Place)",
        previewShortcut = "W (Preview / Trim View)",
        wrapMenuName = "Window > Text Wrap (Cmd+Alt+W)",
        packageMenuName = "File > Package (Cmd+Alt+Shift+P)",
        masterPageTerm = "A-Parent / Master Spread"
    ),
    QUARKXPRESS(
        displayName = "QuarkXPress",
        shortBadge = "QXP",
        placeShortcut = "Cmd/Ctrl+E (Import / Get Text)",
        previewShortcut = "F7 (Show/Hide Guides)",
        wrapMenuName = "Item > Runaround (Cmd/Ctrl+T)",
        packageMenuName = "File > Collect for Output",
        masterPageTerm = "A-Master Page Layout"
    )
}

enum class EditorialFontChoice(
    val id: String,
    val label: String,
    val category: String
) {
    PLAYFAIR("Playfair", "Playfair Display", "High-Contrast Editorial Serif"),
    SPACE_GROTESK("SpaceGrotesk", "Space Grotesk", "Swiss Geometric Sans"),
    PLUS_JAKARTA("PlusJakarta", "Plus Jakarta Sans", "Clean Humanist Sans"),
    JETBRAINS_MONO("JetBrainsMono", "JetBrains Mono", "Technical Monospace Spec")
}

data class SpreadLayoutState(
    val title: String = "ZÜRICH DESIGN QUARTERLY",
    val deckSubtitle: String = "How grids, picas, and baseline rhythms shaped modern publishing from QuarkXPress 3.3 to Adobe InDesign.",
    val softwareMode: DtpSoftwareMode = DtpSoftwareMode.INDESIGN,
    val previewTrimMode: Boolean = false, // 'W' in InDesign / 'F7' in Quark
    val columns: Int = 2,
    val gutterPicas: Float = 1.0f, // 1p0 = 12pt
    val marginPicas: Float = 3.0f, // 3p0 = 36pt = 0.5 inch
    val bleedMm: Int = 3,          // Standard 3mm print bleed
    val headlineFont: EditorialFontChoice = EditorialFontChoice.PLAYFAIR,
    val headlineSizePt: Int = 26,
    val bodySizePt: Int = 11,
    val leadingPt: Int = 14,
    val tracking: Int = 0,         // in 1/1000 em (-40 to +60)
    val dropCapLines: Int = 3,
    val textWrapEnabled: Boolean = true,
    val textWrapOffsetPicas: Float = 1.0f,
    val linkedFrames: Boolean = true,
    val masterPageApplied: Boolean = true,
    val baselineGridSnap: Boolean = true,
    val colorSpace: String = "CMYK", // "CMYK" or "RGB"
    val imagePpi: Int = 300          // 300 PPI vs 72 PPI
) {
    /**
     * Calculates whether the current text frame settings result in Overset Text [+]
     * (when body copy overflows available column frames).
     */
    val hasOversetText: Boolean
        get() {
            // Unlinked frames or overly large body size/leading or giant tracking overflows the spread
            if (!linkedFrames) return true
            val capacityScore = columns * 18 - (bodySizePt - 10) * 6 - (leadingPt - 13) * 3 - (tracking / 10)
            return capacityScore < 20
        }

    /**
     * Evaluates real DTP Preflight warnings/errors just like InDesign's Preflight Panel
     * or QuarkXPress Job Jackets.
     */
    val preflightIssues: List<String>
        get() {
            val issues = mutableListOf<String>()
            if (hasOversetText) {
                issues.add("OVERSET TEXT [+]: Story overflows frame. Thread text frames or reduce body size/leading.")
            }
            if (colorSpace != "CMYK") {
                issues.add("COLOR SPACE: Document uses RGB plates instead of Process CMYK for offset lithography.")
            }
            if (imagePpi < 300) {
                issues.add("EFFECTIVE PPI: Placed hero artwork is $imagePpi PPI (minimum 300 PPI required for print).")
            }
            if (bleedMm < 3) {
                issues.add("BLEED MISSING: Document bleed is ${bleedMm}mm (requires >= 3mm to prevent white hairline edges on trim).")
            }
            if (leadingPt < bodySizePt + 1) {
                issues.add("TIGHT LEADING: Leading (${leadingPt}pt) is colliding with body size (${bodySizePt}pt).")
            }
            return issues
        }

    val preflightScore: Int
        get() = (100 - preflightIssues.size * 20).coerceIn(0, 100)
}

data class MissionObjectiveCheck(
    val label: String,
    val isMet: Boolean,
    val technicalDetail: String
)

data class DtpMission(
    val id: Int,
    val codeName: String,
    val title: String,
    val softwareFocus: String,
    val conceptSummary: String,
    val proTip: String,
    val initialState: SpreadLayoutState,
    val evaluate: (SpreadLayoutState) -> List<MissionObjectiveCheck>
)

data class RosettaStoneEntry(
    val concept: String,
    val category: String,
    val indesignWay: String,
    val quarkWay: String,
    val whyItMatters: String
)

data class ShowdownQuestion(
    val id: Int,
    val category: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class KerningChallengeWord(
    val id: String,
    val word: String,
    val fontChoice: EditorialFontChoice,
    val contextNote: String,
    // Relative gap units between each consecutive character pair (length = word.length - 1)
    val initialGaps: List<Int>,
    val idealGaps: List<Int>
)

object DtpCurriculum {

    fun formatPicas(picas: Float): String {
        val wholePicas = picas.toInt()
        val points = ((picas - wholePicas) * 12f).toInt()
        return "${wholePicas}p${points}"
    }

    val missions: List<DtpMission> = listOf(
        DtpMission(
            id = 1,
            codeName = "MISSION 01 • SWISS GRID",
            title = "The 3-Column Swiss Grid & Pica Rulers",
            softwareFocus = "InDesign Layout > Margins and Columns / QuarkXPress Page > Master Guides",
            conceptSummary = "In desktop publishing, 1 inch equals 6 picas (6p0), and 1 pica equals 12 points (0p12). Build a balanced 3-column Swiss architectural spread with a 3p0 margin and a 1p6 (18pt) column gutter.",
            proTip = "In both InDesign and QuarkXPress, you can type '3p6' directly into any measurement field to specify 3 picas and 6 points!",
            initialState = SpreadLayoutState(
                title = "NEUE GRAFIK ARCHIVE",
                columns = 1,
                gutterPicas = 0.5f,
                marginPicas = 1.5f,
                baselineGridSnap = false
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Set Page Columns to 3",
                        isMet = state.columns == 3,
                        technicalDetail = "Current: ${state.columns} col (Target: 3 columns)"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Column Gutter to 1p6 (1.5 picas / 18pt)",
                        isMet = kotlin.math.abs(state.gutterPicas - 1.5f) < 0.1f,
                        technicalDetail = "Current: ${formatPicas(state.gutterPicas)} (Target: 1p6)"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Page Margins to 3p0 (36pt / 0.5 in)",
                        isMet = kotlin.math.abs(state.marginPicas - 3.0f) < 0.1f,
                        technicalDetail = "Current: ${formatPicas(state.marginPicas)} (Target: 3p0)"
                    ),
                    MissionObjectiveCheck(
                        label = "Enable Snap to Baseline Grid",
                        isMet = state.baselineGridSnap,
                        technicalDetail = if (state.baselineGridSnap) "Baseline lock active" else "Paragraphs drifting off baseline"
                    )
                )
            }
        ),
        DtpMission(
            id = 2,
            codeName = "MISSION 02 • TYPE HIERARCHY",
            title = "Editorial Typography, Leading & Drop Caps",
            softwareFocus = "Character & Paragraph Panels (Cmd+T / Cmd+Alt+T)",
            conceptSummary = "A feature article arrived with solid 11/11 leading (lines crashing together) and no visual entry point. Set an authoritative Serif headline at 28pt, open the body leading to 11/15pt, tighten tracking to -10, and add a 3-line Drop Cap.",
            proTip = "Leading is named after the strips of lead metal that letterpress compositors inserted between lines of Linotype!",
            initialState = SpreadLayoutState(
                title = "THE ART OF LEAD & INK",
                headlineFont = EditorialFontChoice.JETBRAINS_MONO,
                headlineSizePt = 18,
                bodySizePt = 11,
                leadingPt = 11,
                tracking = 30,
                dropCapLines = 1
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Select Playfair Display (Editorial Serif) Headline",
                        isMet = state.headlineFont == EditorialFontChoice.PLAYFAIR,
                        technicalDetail = "Current: ${state.headlineFont.label}"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Headline Size to 28pt or larger",
                        isMet = state.headlineSizePt >= 28,
                        technicalDetail = "Current: ${state.headlineSizePt}pt"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Body Copy to 11pt with 14pt–15pt Leading",
                        isMet = state.bodySizePt == 11 && state.leadingPt in 14..15,
                        technicalDetail = "Current: ${state.bodySizePt}/${state.leadingPt}pt (Target: 11/14 or 11/15)"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Paragraph Drop Cap to 3 Lines",
                        isMet = state.dropCapLines == 3,
                        technicalDetail = "Current: ${state.dropCapLines}-line drop cap"
                    )
                )
            }
        ),
        DtpMission(
            id = 3,
            codeName = "MISSION 03 • STORY THREADING",
            title = "Eliminate Overset Text [+] & Thread Frames",
            softwareFocus = "InDesign Out-Port Threading / QuarkXPress Linking Tool",
            conceptSummary = "Look at the bottom-right corner of Column 1: a red [+] Overset Text mark warns that half the journalist's story is cut off! Link the text frames across 3 columns and tune body type so 0 characters overflow.",
            proTip = "In InDesign, clicking the red [+] Out-Port loads your cursor with the remaining story so you can thread it into the next frame.",
            initialState = SpreadLayoutState(
                title = "OVERSET CRISIS AT PRESS TIME",
                columns = 2,
                linkedFrames = false,
                bodySizePt = 13,
                leadingPt = 17,
                tracking = 20
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Thread / Link Text Frames Across Columns",
                        isMet = state.linkedFrames,
                        technicalDetail = if (state.linkedFrames) "Story threaded across columns" else "Unlinked frame! Red [+] overset port active"
                    ),
                    MissionObjectiveCheck(
                        label = "Expand Layout to 3 Columns",
                        isMet = state.columns == 3,
                        technicalDetail = "Current: ${state.columns} columns"
                    ),
                    MissionObjectiveCheck(
                        label = "Clear Overset Text [+] Warning",
                        isMet = !state.hasOversetText,
                        technicalDetail = if (!state.hasOversetText) "All copy fits cleanly inside threaded frames" else "Story still overflows! Reduce body pt or tracking"
                    )
                )
            }
        ),
        DtpMission(
            id = 4,
            codeName = "MISSION 04 • TEXT WRAP / RUNAROUND",
            title = "Item Runaround & Graphic Frame Offset",
            softwareFocus = "InDesign Text Wrap Panel / QuarkXPress Item > Runaround (Cmd+T)",
            conceptSummary = "The central illustration frame is sitting right on top of the body text! Turn on Text Wrap (Runaround) and set a clean 1p6 (18pt) offset cushion so the columns repel cleanly around the graphic.",
            proTip = "Never let body copy touch the edge of a photo frame—always add at least 0p9 to 1p6 of Text Wrap offset.",
            initialState = SpreadLayoutState(
                title = "BAUHAUS EXHIBITION CATALOG",
                columns = 2,
                textWrapEnabled = false,
                textWrapOffsetPicas = 0.0f,
                headlineFont = EditorialFontChoice.SPACE_GROTESK
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Enable Text Wrap / Item Runaround",
                        isMet = state.textWrapEnabled,
                        technicalDetail = if (state.textWrapEnabled) "Bounding-box wrap active" else "Text is colliding behind the image frame!"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Wrap Offset Cushion to 1p6 (1.5 picas)",
                        isMet = kotlin.math.abs(state.textWrapOffsetPicas - 1.5f) < 0.1f,
                        technicalDetail = "Current offset: ${formatPicas(state.textWrapOffsetPicas)} (Target: 1p6)"
                    ),
                    MissionObjectiveCheck(
                        label = "Use Space Grotesk (Swiss Geometric Sans) Headline",
                        isMet = state.headlineFont == EditorialFontChoice.SPACE_GROTESK,
                        technicalDetail = "Current: ${state.headlineFont.label}"
                    )
                )
            }
        ),
        DtpMission(
            id = 5,
            codeName = "MISSION 05 • MASTER PAGES",
            title = "A-Master Running Headers & Baseline Rhythm",
            softwareFocus = "Pages Panel > A-Master (Parent Page) & Baseline Grid",
            conceptSummary = "A 64-page magazine needs automatic page folios (<#>) and running headers on every spread, plus cross-column baseline alignment. Apply the A-Master template, lock to the Baseline Grid, and verify in Trim Preview mode (W).",
            proTip = "In InDesign (Cmd+Shift+Alt+N) and QuarkXPress, inserting the Current Page Number marker on A-Master automatically numbers every page.",
            initialState = SpreadLayoutState(
                title = "FOLIO & MASTER ARCHITECTURE",
                columns = 3,
                masterPageApplied = false,
                baselineGridSnap = false,
                previewTrimMode = false
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Apply A-Master Running Header & Auto-Folio",
                        isMet = state.masterPageApplied,
                        technicalDetail = if (state.masterPageApplied) "A-Master folio & header visible" else "Master page items missing"
                    ),
                    MissionObjectiveCheck(
                        label = "Lock Paragraphs to Baseline Grid",
                        isMet = state.baselineGridSnap,
                        technicalDetail = if (state.baselineGridSnap) "Cross-column baselines aligned" else "Columns unaligned vertically"
                    ),
                    MissionObjectiveCheck(
                        label = "Ensure 0 Overset Text Errors",
                        isMet = !state.hasOversetText,
                        technicalDetail = if (!state.hasOversetText) "0 overset frames" else "Fix overset text before approving master"
                    )
                )
            }
        ),
        DtpMission(
            id = 6,
            codeName = "MISSION 06 • PREFLIGHT & PRESS CHECK",
            title = "Emergency Offset Litho Preflight Rescue",
            softwareFocus = "InDesign Live Preflight & Package / QuarkXPress Collect for Output",
            conceptSummary = "The printing press rolls in 3 minutes, and Live Preflight shows 4 fatal print errors: RGB color mode, a 72 PPI low-res web image, 0mm bleed, and unlinked overset text! Fix all 4 issues to achieve a 100% Press-Ready Preflight.",
            proTip = "Commercial offset printing requires 4-color Process CMYK plates, 300 PPI continuous-tone images, and a 3mm (or 0.125 in) bleed.",
            initialState = SpreadLayoutState(
                title = "PRINT PRODUCTION FINALE",
                columns = 2,
                colorSpace = "RGB",
                imagePpi = 72,
                bleedMm = 0,
                linkedFrames = false,
                bodySizePt = 12,
                leadingPt = 14
            ),
            evaluate = { state ->
                listOf(
                    MissionObjectiveCheck(
                        label = "Convert Color Space to Process CMYK",
                        isMet = state.colorSpace == "CMYK",
                        technicalDetail = "Current plates: ${state.colorSpace}"
                    ),
                    MissionObjectiveCheck(
                        label = "Relink Hero Graphic with 300 PPI High-Res TIFF",
                        isMet = state.imagePpi >= 300,
                        technicalDetail = "Current image resolution: ${state.imagePpi} PPI"
                    ),
                    MissionObjectiveCheck(
                        label = "Set Document Bleed to 3mm (or 4mm)",
                        isMet = state.bleedMm >= 3,
                        technicalDetail = "Current bleed: ${state.bleedMm}mm"
                    ),
                    MissionObjectiveCheck(
                        label = "Resolve Overset Text (100% Green Preflight)",
                        isMet = state.preflightIssues.isEmpty(),
                        technicalDetail = if (state.preflightIssues.isEmpty()) "0 Preflight Errors — Ready to Package!" else "${state.preflightIssues.size} Preflight issue(s) remaining"
                    )
                )
            }
        )
    )

    val rosettaStoneEntries: List<RosettaStoneEntry> = listOf(
        RosettaStoneEntry(
            concept = "Importing Text & Graphics",
            category = "Core Workflow",
            indesignWay = "File > Place (Cmd/Ctrl + D) loads the 'Place Gun' cursor for any text, PSD, TIFF, or PDF file.",
            quarkWay = "File > Import / Get Text or Picture (Cmd/Ctrl + E) into a selected Content Box.",
            whyItMatters = "DTP apps do not embed raw files by default—they maintain high-res file links for fast layout performance."
        ),
        RosettaStoneEntry(
            concept = "Flowing Text around Images",
            category = "Layout & Frames",
            indesignWay = "Window > Text Wrap (Cmd + Alt + W) with Bounding Box, Object Shape, or Jump Object modes.",
            quarkWay = "Item > Runaround (Cmd/Ctrl + T) specifying Item, Auto Image, or Alpha Channel offset.",
            whyItMatters = "Creates clean breathing room (measured in picas/points) between editorial photography and columns."
        ),
        RosettaStoneEntry(
            concept = "Clean Trim Preview vs. Authoring Guides",
            category = "Workspace",
            indesignWay = "Press 'W' (when not editing text) to toggle between Normal View (all grids/guides) and Preview Mode.",
            quarkWay = "Press 'F7' to toggle Show/Hide Guides, or switch to Trim View at the bottom of the document window.",
            whyItMatters = "Professional designers constantly flip between structural grid view and clean reader view."
        ),
        RosettaStoneEntry(
            concept = "Threading Multi-Column Stories",
            category = "Typography",
            indesignWay = "Click the In-Port or Out-Port (or red [+] Overset mark) on a text frame border to thread frames.",
            quarkWay = "Select the Linking Chain tool on the Tool Palette, click the source box, then click the target box.",
            whyItMatters = "Allows a single long article to flow dynamically across columns, around pull-quotes, and across pages."
        ),
        RosettaStoneEntry(
            concept = "Master Pages / Parent Pages",
            category = "Document Architecture",
            indesignWay = "Called 'Parent Pages' (formerly Master Pages) in the Pages panel; Cmd+Shift-click overrides an item.",
            quarkWay = "Called 'Master Pages' at the top/bottom of the Page Layout palette; supports multiple Master Guides.",
            whyItMatters = "Holds repeating running headers, folios (automatic page numbers), and column grids across an entire book."
        ),
        RosettaStoneEntry(
            concept = "Handing Off to the Printing Press",
            category = "Prepress & Output",
            indesignWay = "File > Package (Cmd + Alt + Shift + P) gathers the .INDD, .IDML, linked art, and fonts into one folder.",
            quarkWay = "File > Collect for Output gathers the .QXP project, linked high-res images, color profiles, and report.",
            whyItMatters = "Ensures the prepress operator never encounters missing fonts or broken image links on press day."
        )
    )

    val showdownQuestions: List<ShowdownQuestion> = listOf(
        ShowdownQuestion(
            id = 1,
            category = "Picas & Points",
            prompt = "In InDesign and QuarkXPress measurement fields, how many points are in 1 Pica (1p0), and how many Picas are in 1 Inch?",
            options = listOf(
                "12 points = 1 pica; 6 picas = 1 inch (72 points/inch)",
                "10 points = 1 pica; 10 picas = 1 inch (100 points/inch)",
                "16 points = 1 pica; 4 picas = 1 inch (64 points/inch)",
                "8 points = 1 pica; 9 picas = 1 inch (72 points/inch)"
            ),
            correctIndex = 0,
            explanation = "1 PostScript inch is 72 points. Because 1 pica = 12 points, there are exactly 6 picas in an inch. So '3p6' equals 42 points!"
        ),
        ShowdownQuestion(
            id = 2,
            category = "InDesign vs. QuarkXPress",
            prompt = "You want to place a high-resolution TIFF photo into a picture frame. Which keyboard shortcuts do you press in Adobe InDesign vs. QuarkXPress?",
            options = listOf(
                "InDesign: Cmd/Ctrl+D (Place) • QuarkXPress: Cmd/Ctrl+E (Import/Get)",
                "InDesign: Cmd/Ctrl+I • QuarkXPress: Cmd/Ctrl+P",
                "InDesign: Cmd/Ctrl+G • QuarkXPress: Cmd/Ctrl+L",
                "InDesign: Cmd/Ctrl+B • QuarkXPress: Cmd/Ctrl+M"
            ),
            correctIndex = 0,
            explanation = "Cmd/Ctrl+D ('Place') is InDesign's most iconic shortcut, while QuarkXPress veterans know Cmd/Ctrl+E ('Get Text/Picture') by muscle memory."
        ),
        ShowdownQuestion(
            id = 3,
            category = "Overset Text",
            prompt = "What does a small red square with a plus sign [+] at the bottom-right edge of a text frame indicate?",
            options = listOf(
                "Overset Text: the story is longer than the frame and hidden copy is cut off",
                "The text frame is locked to the A-Master page",
                "Optical kerning is enabled for the paragraph",
                "The frame uses a CMYK spot color stroke"
            ),
            correctIndex = 0,
            explanation = "The red Out-Port [+] indicates Overset Text. Clicking it loads your cursor to thread the overflowing text into another column."
        ),
        ShowdownQuestion(
            id = 4,
            category = "Typography",
            prompt = "A creative director asks you to set body copy at '10/13' (spoken 'ten on thirteen'). What do 10 and 13 represent?",
            options = listOf(
                "10pt font size with 13pt leading (baseline-to-baseline vertical line spacing)",
                "10 picas column width with 13 points gutter",
                "10% tracking with 13pt drop cap height",
                "10pt headline with 13 columns"
            ),
            correctIndex = 0,
            explanation = "Type specs are written Size/Leading. '10/13' means 10-point type set on 13 points of leading (3 points of extra vertical breathing room)."
        ),
        ShowdownQuestion(
            id = 5,
            category = "Prepress & Print",
            prompt = "Why must a full-bleed magazine cover extend 3mm (or 0.125 in) past the trim edge to the red Bleed Guide?",
            options = listOf(
                "So mechanical guillotine paper trimmers don't leave accidental white hairline slivers along the edge",
                "To give the spiral binding punch extra margin inside the spine",
                "To increase the PPI resolution of placed vector logos",
                "So the baseline grid snaps to the printer's registration marks"
            ),
            correctIndex = 0,
            explanation = "High-speed bindery guillotines can shift by 1–2mm when cutting stacks of paper. Extending background art to the 3mm bleed line guarantees edge-to-edge ink."
        ),
        ShowdownQuestion(
            id = 6,
            category = "InDesign vs. QuarkXPress",
            prompt = "What is QuarkXPress's historical term for making body text flow around the boundary of an image or pull-quote box?",
            options = listOf(
                "Runaround (Cmd/Ctrl + T)",
                "Text Wrap (Cmd + Alt + W)",
                "Story Threading",
                "Pasteboard Bleed"
            ),
            correctIndex = 0,
            explanation = "QuarkXPress calls text wrapping 'Runaround' (Item > Runaround), whereas Adobe InDesign calls it 'Text Wrap'."
        ),
        ShowdownQuestion(
            id = 7,
            category = "Workspace Mastery",
            prompt = "While working in InDesign (with the Selection Tool active, not typing text), what happens when you press the single key 'W'?",
            options = listOf(
                "Toggles between Normal View (showing grids, guides & frame edges) and clean Trim Preview Mode",
                "Opens the Preflight & Package dialog",
                "Converts all RGB images to CMYK",
                "Unthreads all linked text columns"
            ),
            correctIndex = 0,
            explanation = "Pressing 'W' in InDesign instantly hides all non-printing cyan/magenta guides and masks the bleed area so you see the trimmed page."
        ),
        ShowdownQuestion(
            id = 8,
            category = "Typography",
            prompt = "What is the difference between Kerning and Tracking in desktop publishing?",
            options = listOf(
                "Kerning adjusts space between a single specific pair of letters (like 'AV'); Tracking adjusts uniform spacing across a range of text",
                "Kerning is vertical line spacing; Tracking is horizontal column width",
                "Kerning only works on monospace fonts; Tracking only works on serif fonts",
                "Kerning is used in QuarkXPress; Tracking is used in InDesign"
            ),
            correctIndex = 0,
            explanation = "Kerning fixes optical gaps between awkward letter pairs (like 'To', 'AV', 'WA'), while Tracking tightens or loosens an entire word, line, or paragraph."
        ),
        ShowdownQuestion(
            id = 9,
            category = "Editorial Polish",
            prompt = "In book and magazine layout, what is a 'Widow' or 'Orphan' that Paragraph Keep Options help prevent?",
            options = listOf(
                "A single word stranded alone at the bottom of a paragraph, or a single line stranded at the top/bottom of a column",
                "An unlinked image missing from the Links panel",
                "A font family without an italic weight installed",
                "A blank master page at the end of a signature"
            ),
            correctIndex = 0,
            explanation = "Typographers use Paragraph > Keep Options (Keep Lines Together: Start 2 / End 2) or subtle -5 to -15 tracking to eliminate stranded single lines."
        ),
        ShowdownQuestion(
            id = 10,
            category = "Prepress & Print",
            prompt = "You finish a layout and need to send the native project file, all linked high-res photos, and fonts to the printer. What command do you run?",
            options = listOf(
                "InDesign: File > Package • QuarkXPress: File > Collect for Output",
                "InDesign: File > Place • QuarkXPress: File > Step and Repeat",
                "InDesign: Object > Pathfinder • QuarkXPress: Utilities > Usage",
                "InDesign: Edit > Assign Profiles • QuarkXPress: Style > Formats"
            ),
            correctIndex = 0,
            explanation = "Packaging (InDesign) or Collecting for Output (QuarkXPress) gathers every linked image, font, and an IDML/QXP archive into a press-ready folder."
        )
    )

    val kerningChallenges: List<KerningChallengeWord> = listOf(
        KerningChallengeWord(
            id = "AVANT",
            word = "AVANT",
            fontChoice = EditorialFontChoice.PLAYFAIR,
            contextNote = "Diagonal letter pairs like A-V and V-A create huge optical white wedges unless kerned negatively!",
            initialGaps = listOf(14, 14, 4, 8),
            idealGaps = listOf(-8, -8, 2, 0)
        ),
        KerningChallengeWord(
            id = "TYPE",
            word = "TYPE",
            fontChoice = EditorialFontChoice.PLAYFAIR,
            contextNote = "The overhanging crossbar of 'T' next to 'Y' needs tight negative kerning so the 'T' doesn't float away.",
            initialGaps = listOf(16, 4, 6),
            idealGaps = listOf(-10, 0, 2)
        ),
        KerningChallengeWord(
            id = "QUARK",
            word = "QUARK",
            fontChoice = EditorialFontChoice.SPACE_GROTESK,
            contextNote = "Balance the open counter of 'U-A' and the diagonal leg of 'R-K' for uniform Swiss geometric rhythm.",
            initialGaps = listOf(8, 12, 6, 12),
            idealGaps = listOf(2, -3, 2, -2)
        ),
        KerningChallengeWord(
            id = "PICA",
            word = "PICA",
            fontChoice = EditorialFontChoice.PLAYFAIR,
            contextNote = "Notice how the curve of 'C' and diagonal of 'A' leave extra air compared to the vertical stems of 'P-I'.",
            initialGaps = listOf(-4, 8, 14),
            idealGaps = listOf(4, 2, -4)
        ),
        KerningChallengeWord(
            id = "WAVE",
            word = "WAVE",
            fontChoice = EditorialFontChoice.SPACE_GROTESK,
            contextNote = "Three consecutive diagonals (W-A-V) are the classic test for any graphic designer's optical kerning eye.",
            initialGaps = listOf(14, 14, 10),
            idealGaps = listOf(-8, -8, 0)
        )
    )
}
