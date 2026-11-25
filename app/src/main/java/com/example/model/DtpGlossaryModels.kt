package com.example.model

enum class GlossaryCategory(val label: String) {
    ALL("All"),
    TYPOGRAPHY("Typography"),
    GRIDS_LAYOUT("Grids & Layout"),
    COLOR_PRINT("Color & Print"),
    PREFLIGHT_PDF("Preflight & PDF"),
    SOFTWARE_WORKFLOW("Master & Studio")
}

enum class GlossaryTargetModule(val moduleName: String, val badgeLabel: String) {
    SPREAD_WORKBENCH("Spread Layout Studio", "Studio"),
    MASTER_PAGES("Master Pages Simulator", "Masters"),
    FONT_ANATOMY("Font Anatomy & Kerning", "Anatomy"),
    ARTICLE_HIERARCHY("Article Hierarchy Repair", "Hierarchy"),
    GRID_PRINCIPLES("Grid Design Principles", "Grids"),
    LIVE_PREFLIGHT("Live Preflight Inspector", "Preflight"),
    COLOR_BLEED_LAB("Color Management & Bleed", "Color Lab"),
    PDF_EXPORT_LAB("Print-Ready PDF Export", "PDF Export"),
    SOFTWARE_SHOWDOWN("InDesign vs QXP Showdown", "Showdown")
}

data class DtpGlossaryTerm(
    val id: String,
    val term: String,
    val aliasOrNote: String? = null,
    val category: GlossaryCategory,
    val definition: String,
    val practicalTip: String,
    val inDesignReference: String,
    val quarkXPressReference: String,
    val relatedKeywords: List<String>,
    val targetModule: GlossaryTargetModule,
    val ctaLabel: String
)

object DtpGlossaryCatalog {

    val terms: List<DtpGlossaryTerm> = listOf(
        DtpGlossaryTerm(
            id = "baseline_grid",
            term = "Baseline Grid",
            aliasOrNote = "Cross-Alignment Rule",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "An invisible horizontal ruled grid that locks text baselines across adjacent columns and facing pages so lines align optically across the spread.",
            practicalTip = "Set your baseline grid increment equal to your body copy leading (for example, 14 pt) so multi-column articles always end evenly.",
            inDesignReference = "Preferences > Grids • Paragraph Panel > Align to Baseline Grid",
            quarkXPressReference = "Preferences > Paragraph > Baseline Grid • Lock to Baseline Grid",
            relatedKeywords = listOf("leading", "columns", "alignment", "grid"),
            targetModule = GlossaryTargetModule.SPREAD_WORKBENCH,
            ctaLabel = "Try Baseline Grid in Studio"
        ),
        DtpGlossaryTerm(
            id = "bleed",
            term = "Bleed",
            aliasOrNote = "3 mm / 0.125 in Safety",
            category = GlossaryCategory.COLOR_PRINT,
            definition = "Artwork, backgrounds, or photos extended beyond the page trim edge so slight mechanical shifts during guillotine cutting never expose raw white paper slivers.",
            practicalTip = "Standard commercial offset print requires 3 mm (0.125 in) bleed on all four sides; wide-format boards often request 5 mm.",
            inDesignReference = "File > Document Setup > Bleed and Slug • Export > Marks & Bleeds",
            quarkXPressReference = "Layout Properties > Bleed Guides • Export PDF > Bleed",
            relatedKeywords = listOf("trim", "crop marks", "slug", "guillotine"),
            targetModule = GlossaryTargetModule.COLOR_BLEED_LAB,
            ctaLabel = "Test Bleed Cut in Color Lab"
        ),
        DtpGlossaryTerm(
            id = "cmyk_vs_rgb",
            term = "CMYK vs. RGB",
            aliasOrNote = "Subtractive Ink vs. Additive Light",
            category = GlossaryCategory.COLOR_PRINT,
            definition = "RGB mixes Red, Green, and Blue light on screens with a wide neon gamut. CMYK mixes Cyan, Magenta, Yellow, and Key (Black) inks on paper and has a narrower printable gamut.",
            practicalTip = "Always soft-proof bright RGB blues and greens in CMYK early so clients aren't shocked by muted ink shifts on press.",
            inDesignReference = "Window > Color > Swatches • View > Proof Colors (CMYK)",
            quarkXPressReference = "Window > Colors • View > Proof Output > Composite CMYK",
            relatedKeywords = listOf("gamut", "icc", "swatches", "ink", "color"),
            targetModule = GlossaryTargetModule.COLOR_BLEED_LAB,
            ctaLabel = "Calibrate CMYK in Color Lab"
        ),
        DtpGlossaryTerm(
            id = "color_bars_reg",
            term = "Color Bars & Registration Marks",
            aliasOrNote = "Pressman Inspection Marks",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Color bars provide CMYK solid and tint patches measured by press densitometers; registration bullseyes let operators align all four ink plates precisely.",
            practicalTip = "Enable them for offset commercial runs when requested, and keep mark offset at least 3 mm so marks never intrude into the bleed zone.",
            inDesignReference = "Export Adobe PDF > Marks and Bleeds > Registration / Color Bars",
            quarkXPressReference = "Export Layout as PDF > Options > Registration: Centered",
            relatedKeywords = listOf("pdf", "offset", "plates", "trim marks"),
            targetModule = GlossaryTargetModule.PDF_EXPORT_LAB,
            ctaLabel = "Toggle Marks in PDF Export"
        ),
        DtpGlossaryTerm(
            id = "crop_trim_marks",
            term = "Crop / Trim Marks",
            aliasOrNote = "Finished Page Hairlines",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Fine corner hairlines printed outside the final page area showing bindery operators exactly where to trim the oversized press sheet.",
            practicalTip = "Never draw manual crop lines inside your artwork—always generate them automatically in the PDF export dialog with proper offset.",
            inDesignReference = "Export Adobe PDF > Marks and Bleeds > Crop Marks",
            quarkXPressReference = "Export Layout as PDF > Registration & Bleed > Crop Marks",
            relatedKeywords = listOf("bleed", "offset", "trimbox", "pdf"),
            targetModule = GlossaryTargetModule.PDF_EXPORT_LAB,
            ctaLabel = "Configure Crop Marks in PDF Export"
        ),
        DtpGlossaryTerm(
            id = "delta_e",
            term = "Delta E (ΔE) & Soft Proofing",
            aliasOrNote = "Color Accuracy Metric",
            category = GlossaryCategory.COLOR_PRINT,
            definition = "A numerical measure of perceived color difference between a target print swatch and your calibrated output. Values below ΔE 2.0 are virtually indistinguishable to the human eye.",
            practicalTip = "Enable Simulate Paper Color when soft-proofing uncoated stocks to preview reduced ink contrast before sending plates to press.",
            inDesignReference = "View > Proof Setup > Custom > Simulate Paper Color",
            quarkXPressReference = "View > Proof Output • Color Manager Source Setup",
            relatedKeywords = listOf("cmyk", "icc", "proof", "swatch"),
            targetModule = GlossaryTargetModule.COLOR_BLEED_LAB,
            ctaLabel = "Match Delta E Swatches"
        ),
        DtpGlossaryTerm(
            id = "dpi_effective_ppi",
            term = "Effective PPI / DPI",
            aliasOrNote = "Print Image Resolution",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Effective PPI measures an image's true pixel density at its scaled size in the layout. Enlarging a 300 PPI image to 200% drops its effective resolution to 150 PPI.",
            practicalTip = "Commercial offset printing requires 300 effective PPI for continuous-tone photos; anything below 150 PPI will look noticeably soft or pixelated.",
            inDesignReference = "Links Panel > Link Info > Actual PPI vs. Effective PPI",
            quarkXPressReference = "Utilities > Usage > Pictures • Measurements > Effective Resolution",
            relatedKeywords = listOf("resolution", "pixelated", "preflight", "300 dpi", "compression"),
            targetModule = GlossaryTargetModule.LIVE_PREFLIGHT,
            ctaLabel = "Fix Low-PPI Assets in Preflight"
        ),
        DtpGlossaryTerm(
            id = "folio_page_numbering",
            term = "Folio & Auto Page Numbering",
            aliasOrNote = "Dynamic Master Marker",
            category = GlossaryCategory.SOFTWARE_WORKFLOW,
            definition = "A folio is the page number on a publication spread. Inserting a Current Page Number special character on a Master Page automatically updates numbers across every linked page.",
            practicalTip = "Place folios on outer corners (left on verso pages, right on recto pages) so readers can flip through pages easily with a thumb.",
            inDesignReference = "Type > Insert Special Character > Markers > Current Page Number",
            quarkXPressReference = "Utilities > Insert Character > Special > Current Box Page # (Cmd+3)",
            relatedKeywords = listOf("master pages", "header", "footer", "recto", "verso"),
            targetModule = GlossaryTargetModule.MASTER_PAGES,
            ctaLabel = "Build Auto-Folios in Master Pages"
        ),
        DtpGlossaryTerm(
            id = "font_anatomy",
            term = "Font Anatomy (x-Height & Cap Height)",
            aliasOrNote = "Baseline, Ascender, Descender",
            category = GlossaryCategory.TYPOGRAPHY,
            definition = "The structural geometry of letterforms: baseline (resting line), x-height (height of lowercase 'x'), cap height, ascenders ('h', 'd'), and descenders ('p', 'g').",
            practicalTip = "Typefaces with a taller x-height appear optically larger at 10 pt and require slightly more leading than small x-height serifs.",
            inDesignReference = "Character Panel • Type > Create Outlines (inspect bounds)",
            quarkXPressReference = "Measurements Palette > Character Attributes",
            relatedKeywords = listOf("baseline", "ascender", "descender", "serif", "counter"),
            targetModule = GlossaryTargetModule.FONT_ANATOMY,
            ctaLabel = "Explore Font Anatomy Sliders"
        ),
        DtpGlossaryTerm(
            id = "gutter",
            term = "Gutter",
            aliasOrNote = "Column & Spine Alley",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "The vertical blank channel separating columns of text (column gutter), or the inner margin space where two facing pages meet at the book binding (spine gutter).",
            practicalTip = "A column gutter should generally be at least equal to your body text leading (typically 12–18 pt / 1p0–1p6) so readers' eyes never jump across columns.",
            inDesignReference = "Layout > Margins and Columns > Gutter",
            quarkXPressReference = "Page > Master Guides & Grid > Column Guides > Gutter Width",
            relatedKeywords = listOf("columns", "margins", "grid", "eye jump"),
            targetModule = GlossaryTargetModule.GRID_PRINCIPLES,
            ctaLabel = "Adjust Gutters in Grid Principles"
        ),
        DtpGlossaryTerm(
            id = "icc_profile",
            term = "ICC Color Profile",
            aliasOrNote = "SWOP v2, FOGRA39, GRACoL",
            category = GlossaryCategory.COLOR_PRINT,
            definition = "A standardized data file describing how a specific press, ink set, and paper stock (coated vs. uncoated) reproduces color and caps Total Area Coverage (TAC).",
            practicalTip = "Uncoated papers absorb more ink (higher dot gain)—always switch to an Uncoated profile like FOGRA29 instead of exporting with a Coated profile.",
            inDesignReference = "Edit > Color Settings • Export PDF > Output > Destination Profile",
            quarkXPressReference = "Edit > Color Setups > Output • Export PDF > Color Profile",
            relatedKeywords = listOf("cmyk", "fogra39", "swop", "gracol", "uncoated", "pdf"),
            targetModule = GlossaryTargetModule.PDF_EXPORT_LAB,
            ctaLabel = "Select ICC Profiles in PDF Export"
        ),
        DtpGlossaryTerm(
            id = "kerning",
            term = "Kerning",
            aliasOrNote = "Single-Pair Optical Spacing",
            category = GlossaryCategory.TYPOGRAPHY,
            definition = "Adjusting the horizontal space between two specific adjacent characters—especially diagonal pairs like A/V, T/O, or W/A—to achieve balanced optical density.",
            practicalTip = "Always hand-kern large headlines and logos. Use Optical kerning in InDesign when mixing typefaces or sizing display titles above 24 pt.",
            inDesignReference = "Cursor between 2 letters > Opt/Alt + Left/Right Arrow",
            quarkXPressReference = "Cursor between 2 letters > Cmd/Ctrl + Shift + [ or ]",
            relatedKeywords = listOf("tracking", "optical", "pairs", "headline", "typography"),
            targetModule = GlossaryTargetModule.FONT_ANATOMY,
            ctaLabel = "Practice Pair Kerning Game"
        ),
        DtpGlossaryTerm(
            id = "kicker_deck",
            term = "Kicker (Eyebrow) & Deck",
            aliasOrNote = "Editorial Entry Points",
            category = GlossaryCategory.TYPOGRAPHY,
            definition = "The kicker is a short all-caps category label above the headline; the deck (standfirst) is a brief summary beneath the headline that pulls readers into the article.",
            practicalTip = "Track all-caps kickers generously (+80 to +180 /1000 em) so tiny capitals remain airy and never visually compete with the headline.",
            inDesignReference = "Paragraph Styles > Nested Kicker & Standfirst Styles",
            quarkXPressReference = "Style Sheets > Paragraph Style Sheets (Kicker / Deck)",
            relatedKeywords = listOf("hierarchy", "tracking", "headline", "subhead", "article"),
            targetModule = GlossaryTargetModule.ARTICLE_HIERARCHY,
            ctaLabel = "Style Kicker & Deck in Hierarchy Lab"
        ),
        DtpGlossaryTerm(
            id = "leading",
            term = "Leading",
            aliasOrNote = "Pronounced 'LED-ing' • Line Spacing",
            category = GlossaryCategory.TYPOGRAPHY,
            definition = "The vertical distance measured from one baseline of text to the next, named after thin strips of lead once inserted between rows of metal type.",
            practicalTip = "Body copy reads comfortably around 120%–150% of type size (e.g., 10/14 pt), whereas multi-line display headlines look sharper tighter (105%–118%).",
            inDesignReference = "Character Panel > Leading • Opt/Alt + Up/Down Arrow",
            quarkXPressReference = "Measurements > Leading • Cmd/Ctrl + Shift + \" or :",
            relatedKeywords = listOf("line height", "baseline", "cramped", "horsey", "hierarchy"),
            targetModule = GlossaryTargetModule.ARTICLE_HIERARCHY,
            ctaLabel = "Fix Leading in Hierarchy Lab"
        ),
        DtpGlossaryTerm(
            id = "margins",
            term = "Margins (Inside vs. Outside)",
            aliasOrNote = "Live Area Frame",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "The protective frame of whitespace surrounding the live content area. In bound books, the inside (binding) margin often needs extra room so text doesn't vanish into the spine.",
            practicalTip = "Classical Swiss and book proportions give generous outer and bottom margins to frame the text block and leave room for thumbs and folios.",
            inDesignReference = "Layout > Margins and Columns (Facing Pages: Inside / Outside)",
            quarkXPressReference = "Page > Master Guides & Grid > Margin Guides",
            relatedKeywords = listOf("grid", "binding", "spine", "whitespace", "book"),
            targetModule = GlossaryTargetModule.GRID_PRINCIPLES,
            ctaLabel = "Compare Margins in Grid Principles"
        ),
        DtpGlossaryTerm(
            id = "master_pages",
            term = "Master Pages (Parent Pages)",
            aliasOrNote = "Reusable Page Templates",
            category = GlossaryCategory.SOFTWARE_WORKFLOW,
            definition = "Non-printing template pages holding repeating headers, footers, folios, and grid guides that automatically propagate across every document page linked to them.",
            practicalTip = "Use an 'A-Master' for standard article pages and a blank 'None' or 'B-Chapter' master for full-bleed covers and chapter openers.",
            inDesignReference = "Pages Panel > A-Parent (formerly A-Master) • Cmd/Ctrl+Shift+Click to override",
            quarkXPressReference = "Page Layout Palette > A-Master A • Drag icon onto document pages",
            relatedKeywords = listOf("parent pages", "folio", "headers", "footers", "propagation"),
            targetModule = GlossaryTargetModule.MASTER_PAGES,
            ctaLabel = "Launch Master Pages Simulator"
        ),
        DtpGlossaryTerm(
            id = "measure_cpl",
            term = "Measure & CPL",
            aliasOrNote = "Line Length • Characters Per Line",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "Measure is the physical width of a text column (measured in picas). Ideal editorial readability falls between 45 and 75 characters per line (CPL).",
            practicalTip = "Ultra-narrow 4-column layouts drop below 35 CPL and force awkward hyphenation or whitespace rivers if justified—prefer flush-left alignment there.",
            inDesignReference = "Control Bar > W (Width in Picas) • Info Panel Character Count",
            quarkXPressReference = "Measurements Palette > Box Width (picas) • Utilities > Word Count",
            relatedKeywords = listOf("columns", "rivers", "pica", "readability", "grid"),
            targetModule = GlossaryTargetModule.GRID_PRINCIPLES,
            ctaLabel = "Inspect Measure in Grid Principles"
        ),
        DtpGlossaryTerm(
            id = "modular_grid",
            term = "Modular Grid & Flowlines",
            aliasOrNote = "Swiss Matrix Layout",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "A grid combining vertical columns with horizontal row divisions (flowlines), creating uniform rectangular modules for snapping photos, pull quotes, and captions.",
            practicalTip = "Anchor imagery across 2 or 3 modules while aligning caption baselines to the exact same flowline for classic Swiss editorial discipline.",
            inDesignReference = "Layout > Create Guides (Rows + Columns fitted to Margins)",
            quarkXPressReference = "Page > Master Guides & Grid > Rows & Columns",
            relatedKeywords = listOf("swiss", "columns", "images", "alignment", "layout"),
            targetModule = GlossaryTargetModule.GRID_PRINCIPLES,
            ctaLabel = "Explore Modular Grids"
        ),
        DtpGlossaryTerm(
            id = "overset_text",
            term = "Overset Text",
            aliasOrNote = "Red [+] Out-Port Error",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Hidden text that does not fit inside the boundaries of its text frame, flagged by a red plus [+] box at the lower-right corner of the frame.",
            practicalTip = "Resolve overset copy by threading the out-port into a linked text frame, expanding frame height, or tightening leading/copy before exporting to PDF.",
            inDesignReference = "Red [+] Out-Port • Window > Output > Preflight (Text Errors)",
            quarkXPressReference = "Red [x] Overflow Box • Linking Tool (Chain Icon) to thread boxes",
            relatedKeywords = listOf("threading", "overflow", "preflight", "text frame", "error"),
            targetModule = GlossaryTargetModule.LIVE_PREFLIGHT,
            ctaLabel = "Resolve Overset in Preflight"
        ),
        DtpGlossaryTerm(
            id = "pdf_x_standards",
            term = "PDF/X-1a vs. PDF/X-4",
            aliasOrNote = "ISO Print-Ready PDF Standards",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "PDF/X-1a:2001 flattens transparency and converts everything to CMYK for older RIPs. PDF/X-4:2008 supports modern live transparency and embedded ICC profiles.",
            practicalTip = "Ask your print vendor which PDF/X profile their RIP runs—PDF/X-4 is today's modern default, while PDF/X-1a remains the safest fallback for legacy presses.",
            inDesignReference = "File > Export > Adobe PDF (Print) > Standard: PDF/X-4 or X-1a",
            quarkXPressReference = "File > Export > Layout as PDF > Verification: PDF/X-4 or X-1a",
            relatedKeywords = listOf("export", "rip", "transparency", "cmyk", "icc"),
            targetModule = GlossaryTargetModule.PDF_EXPORT_LAB,
            ctaLabel = "Practice PDF/X Export Dialog"
        ),
        DtpGlossaryTerm(
            id = "pica_point",
            term = "Pica & PostScript Point",
            aliasOrNote = "1 in = 6 picas = 72 pt",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "The traditional typographic measurement units used across desktop publishing. One pica equals 12 points (written 1p0), and 6 picas equal 1 inch (72 pt).",
            practicalTip = "InDesign and QuarkXPress accept notation like '3p6' (3 picas + 6 points = 42 pt) directly inside any coordinate or margin field.",
            inDesignReference = "Preferences > Units & Increments > Picas (e.g., 3p6)",
            quarkXPressReference = "Preferences > Measurements > Horizontal/Vertical: Picas",
            relatedKeywords = listOf("ruler", "units", "inches", "millimeters", "measure"),
            targetModule = GlossaryTargetModule.SPREAD_WORKBENCH,
            ctaLabel = "Measure in Spread Studio"
        ),
        DtpGlossaryTerm(
            id = "preflight_packaging",
            term = "Preflight & Packaging",
            aliasOrNote = "Pre-Press Flight Check",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Automated inspection checking for missing fonts, broken image links, low-resolution photos, RGB color traps, and overset text prior to handoff or press output.",
            practicalTip = "Never export until the bottom status bar shows 'No errors' (green light). Use Package / Collect for Output whenever archiving native working files.",
            inDesignReference = "Window > Output > Preflight • File > Package (Cmd+Opt+Shift+P)",
            quarkXPressReference = "File > Collect for Output • Job Jackets Preflight Evaluation",
            relatedKeywords = listOf("missing fonts", "links", "errors", "handoff", "collect"),
            targetModule = GlossaryTargetModule.LIVE_PREFLIGHT,
            ctaLabel = "Run Live Preflight Challenge"
        ),
        DtpGlossaryTerm(
            id = "reader_spreads",
            term = "Single Pages vs. Reader Spreads",
            aliasOrNote = "Imposition Requirement",
            category = GlossaryCategory.PREFLIGHT_PDF,
            definition = "Reader spreads export facing pages glued side-by-side (good for screen review). Commercial printers almost always require Single Pages so imposition software can arrange press signatures.",
            practicalTip = "Double-check that 'Pages' (not 'Spreads') is selected under the General tab of your PDF Export dialog before uploading to a print house.",
            inDesignReference = "Export Adobe PDF > General > Export As: Pages (not Spreads)",
            quarkXPressReference = "Export Layout as PDF > Pages > Uncheck 'Spreads'",
            relatedKeywords = listOf("imposition", "pdf", "facing pages", "signatures", "export"),
            targetModule = GlossaryTargetModule.PDF_EXPORT_LAB,
            ctaLabel = "Check Imposition in PDF Export"
        ),
        DtpGlossaryTerm(
            id = "text_wrap_runaround",
            term = "Text Wrap (Runaround)",
            aliasOrNote = "Image Standoff / Offset",
            category = GlossaryCategory.SOFTWARE_WORKFLOW,
            definition = "Forces body text to flow cleanly around a photo, pull quote, or vector graphic with a defined breathing offset so letters never touch image edges.",
            practicalTip = "Apply a consistent wrap offset (around 9–12 pt / 0p9–1p0) on images and watch out for awkward 2-word slivers trapped beside wide graphics.",
            inDesignReference = "Window > Text Wrap (Cmd/Ctrl + Opt/Alt + W)",
            quarkXPressReference = "Item > Runaround (Cmd/Ctrl + T)",
            relatedKeywords = listOf("runaround", "offset", "indesign", "quarkxpress", "layout"),
            targetModule = GlossaryTargetModule.SOFTWARE_SHOWDOWN,
            ctaLabel = "Compare Id vs QXP Terms"
        ),
        DtpGlossaryTerm(
            id = "tracking",
            term = "Tracking",
            aliasOrNote = "Uniform Word & Line Spacing",
            category = GlossaryCategory.TYPOGRAPHY,
            definition = "Adjusts letter spacing uniformly across an entire selection of text—such as an all-caps kicker, headline, or paragraph—measured in 1/1000 em units.",
            practicalTip = "Use positive tracking on small all-caps labels (+80 to +160), slight negative tracking on large display headlines, and keep body copy close to 0.",
            inDesignReference = "Character Panel > Tracking (VA) • Opt/Alt + Left/Right Arrow",
            quarkXPressReference = "Measurements > Tracking • Cmd/Ctrl + Shift + [ or ]",
            relatedKeywords = listOf("kerning", "letter spacing", "em", "kicker", "hierarchy"),
            targetModule = GlossaryTargetModule.ARTICLE_HIERARCHY,
            ctaLabel = "Adjust Tracking in Hierarchy Lab"
        ),
        DtpGlossaryTerm(
            id = "whitespace_rivers",
            term = "Whitespace Rivers",
            aliasOrNote = "Justified Column Defect",
            category = GlossaryCategory.GRIDS_LAYOUT,
            definition = "Distracting vertical or diagonal channels of white space snaking down a justified paragraph when word spaces stretch too wide in a narrow column.",
            practicalTip = "Eliminate rivers by switching narrow columns to Flush Left (Rag Right), enabling hyphenation, or widening column measure.",
            inDesignReference = "Paragraph Panel > Justification Settings & Adobe Paragraph Composer",
            quarkXPressReference = "Edit > H&Js (Hyphenation & Justification) Rules",
            relatedKeywords = listOf("justified", "rag right", "columns", "measure", "grid"),
            targetModule = GlossaryTargetModule.GRID_PRINCIPLES,
            ctaLabel = "See Rivers Live in Grid Principles"
        )
    ).sortedBy { it.term }

    fun filterTerms(
        query: String,
        category: GlossaryCategory,
        alphaRange: String,
        onlyBookmarked: Boolean,
        bookmarkedIds: Set<String>
    ): List<DtpGlossaryTerm> {
        val q = query.trim().lowercase()
        return terms.filter { item ->
            val matchesCategory = category == GlossaryCategory.ALL || item.category == category
            val matchesBookmark = !onlyBookmarked || bookmarkedIds.contains(item.id)
            val firstChar = item.term.firstOrNull()?.uppercaseChar() ?: 'A'
            val matchesAlpha = when (alphaRange) {
                "A-D" -> firstChar in 'A'..'D'
                "E-K" -> firstChar in 'E'..'K'
                "L-P" -> firstChar in 'L'..'P'
                "R-Z" -> firstChar in 'R'..'Z'
                else -> true
            }
            val matchesQuery = q.isEmpty() ||
                item.term.lowercase().contains(q) ||
                (item.aliasOrNote?.lowercase()?.contains(q) == true) ||
                item.definition.lowercase().contains(q) ||
                item.inDesignReference.lowercase().contains(q) ||
                item.quarkXPressReference.lowercase().contains(q) ||
                item.targetModule.moduleName.lowercase().contains(q) ||
                item.relatedKeywords.any { it.lowercase().contains(q) }

            matchesCategory && matchesBookmark && matchesAlpha && matchesQuery
        }
    }
}
