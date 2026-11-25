package com.example.model

enum class GridArchetype(
    val label: String,
    val subtitle: String,
    val columns: Int,
    val gutterPicas: Float,
    val marginPreset: MarginPreset,
    val modularRows: Int,
    val imagePlacement: GridImagePlacement,
    val textAlignment: GridTextAlignment,
    val principleNote: String
) {
    MANUSCRIPT(
        label = "Manuscript (1-Col)",
        subtitle = "Books & Long Essays",
        columns = 1,
        gutterPicas = 1.0f,
        marginPreset = MarginPreset.ASYMMETRIC_BOOK,
        modularRows = 0,
        imagePlacement = GridImagePlacement.FULL_MARGIN,
        textAlignment = GridTextAlignment.JUSTIFIED,
        principleNote = "A single continuous text block relies on generous asymmetric margins (wider bottom & outside) for comfortable thumb rests and serene reading."
    ),
    EDITORIAL_TWO(
        label = "2-Col Editorial",
        subtitle = "Journals & Interviews",
        columns = 2,
        gutterPicas = 1.5f,
        marginPreset = MarginPreset.BALANCED,
        modularRows = 0,
        imagePlacement = GridImagePlacement.SPAN_ONE_COL,
        textAlignment = GridTextAlignment.FLUSH_LEFT,
        principleNote = "Two equal columns divide dense copy into scannable measures while letting photos sit neatly inside one column or span the full width."
    ),
    SWISS_THREE(
        label = "3-Col Swiss",
        subtitle = "Magazines & Architecture",
        columns = 3,
        gutterPicas = 1.5f,
        marginPreset = MarginPreset.BALANCED,
        modularRows = 3,
        imagePlacement = GridImagePlacement.SPAN_TWO_COLS,
        textAlignment = GridTextAlignment.FLUSH_LEFT,
        principleNote = "Three columns unlock dynamic asymmetry: pair a 2-column hero image with a 1-column caption or pull-quote aligned cleanly to the gutter."
    ),
    MODULAR_FOUR(
        label = "4-Col Modular",
        subtitle = "Catalogs & Newspapers",
        columns = 4,
        gutterPicas = 1.0f,
        marginPreset = MarginPreset.NARROW,
        modularRows = 4,
        imagePlacement = GridImagePlacement.SPAN_TWO_COLS,
        textAlignment = GridTextAlignment.FLUSH_LEFT,
        principleNote = "Intersecting vertical columns and horizontal flowlines create spatial modules—ideal for complex catalogs with mixed photography and specs."
    )
}

enum class MarginPreset(
    val label: String,
    val topPicas: Float,
    val bottomPicas: Float,
    val leftPicas: Float,
    val rightPicas: Float,
    val summary: String
) {
    NARROW(
        label = "Narrow (1p6)",
        topPicas = 1.5f,
        bottomPicas = 1.5f,
        leftPicas = 1.5f,
        rightPicas = 1.5f,
        summary = "Maximizes print area for dense newspapers or flyers, but reduces breathing room around copy."
    ),
    BALANCED(
        label = "Balanced (3p0)",
        topPicas = 3.0f,
        bottomPicas = 3.0f,
        leftPicas = 3.0f,
        rightPicas = 3.0f,
        summary = "Standard 0.5-inch (3p0) uniform editorial margin used across magazines and brochures."
    ),
    GENEROUS(
        label = "Generous (4p6)",
        topPicas = 4.5f,
        bottomPicas = 4.5f,
        leftPicas = 4.5f,
        rightPicas = 4.5f,
        summary = "Luxury gallery & monograph framing—abundant white space elevates headlines and photography."
    ),
    ASYMMETRIC_BOOK(
        label = "Swiss Book (Asymmetric)",
        topPicas = 2.5f,
        bottomPicas = 5.0f,
        leftPicas = 2.0f,
        rightPicas = 4.0f,
        summary = "Classic Jan Tschichold / Swiss book canon: tighter inside spine, wider outside & foot margins prevent content from visually sinking."
    )
}

enum class GridTextAlignment(
    val label: String,
    val dtpShortcut: String,
    val guidance: String
) {
    FLUSH_LEFT(
        label = "Flush Left (Rag Right)",
        dtpShortcut = "Cmd+Shift+L",
        guidance = "Even word spacing prevents 'rivers' of white space—the gold standard for narrow multi-column magazine layouts."
    ),
    JUSTIFIED(
        label = "Justified",
        dtpShortcut = "Cmd+Shift+J",
        guidance = "Locks both column edges rigidly to vertical guides. Works great on wide 1–2 col books, but creates ugly gaps in narrow 3–4 col grids!"
    ),
    CENTERED(
        label = "Centered",
        dtpShortcut = "Cmd+Shift+C",
        guidance = "Breaks the strong vertical left-edge axis of a column grid—reserve for display titles, invitations, or short captions."
    )
}

enum class GridImagePlacement(
    val label: String,
    val spansDescription: String,
    val isSnappedToGrid: Boolean
) {
    SPAN_ONE_COL(
        label = "1 Column Width",
        spansDescription = "Aligns strictly to Column 1's left and right grid guides.",
        isSnappedToGrid = true
    ),
    SPAN_TWO_COLS(
        label = "2 Columns + Gutter",
        spansDescription = "Crosses 2 columns and absorbs the internal gutter—creating bold Swiss editorial asymmetry.",
        isSnappedToGrid = true
    ),
    FULL_MARGIN(
        label = "Full Margin Span",
        spansDescription = "Anchors across every column from left margin to right margin for maximum visual authority.",
        isSnappedToGrid = true
    ),
    OFF_GRID_BROKEN(
        label = "Off-Grid (Misaligned)",
        spansDescription = "Floats arbitrarily across gutter lines without snapping—visually weakens grid cohesion!",
        isSnappedToGrid = false
    )
}

data class GridLessonState(
    val columns: Int = 3,
    val gutterPicas: Float = 1.5f,
    val marginPreset: MarginPreset = MarginPreset.BALANCED,
    val modularRows: Int = 3, // 0 = off, 3 or 4 = horizontal flowlines
    val textAlignment: GridTextAlignment = GridTextAlignment.FLUSH_LEFT,
    val imagePlacement: GridImagePlacement = GridImagePlacement.SPAN_TWO_COLS,
    val showGuides: Boolean = true
) {
    /**
     * Simulates an 8.5 x 11 inch (51p0 x 66p0) magazine page width to compute authentic pica column widths.
     */
    val pageWidthPicas: Float get() = 51.0f

    val printableWidthPicas: Float
        get() = (pageWidthPicas - marginPreset.leftPicas - marginPreset.rightPicas).coerceAtLeast(18f)

    val singleColumnWidthPicas: Float
        get() {
            val totalGutters = (columns - 1).coerceAtLeast(0) * gutterPicas
            return ((printableWidthPicas - totalGutters) / columns.coerceAtLeast(1)).coerceAtLeast(6f)
        }

    /**
     * Approximate characters per line (CPL) assuming 10pt body copy (~2.5 chars per pica).
     */
    val estimatedCpl: Int
        get() = (singleColumnWidthPicas * 2.45f).toInt().coerceIn(14, 110)

    val hasWhitespaceRiverRisk: Boolean
        get() = textAlignment == GridTextAlignment.JUSTIFIED && columns >= 3

    val hasGutterJumpRisk: Boolean
        get() = columns > 1 && gutterPicas < 1.0f

    val hasCenteredBodyWarning: Boolean
        get() = textAlignment == GridTextAlignment.CENTERED && columns >= 2

    val gridFeedbackMessage: String
        get() = when {
            !imagePlacement.isSnappedToGrid ->
                "IMAGE OFF-GRID: Placed artwork ignores column edges and crosses a gutter arbitrarily. Snap to 1-Col, 2-Col, or Full Margin."
            hasGutterJumpRisk ->
                "TIGHT GUTTER (${DtpCurriculum.formatPicas(gutterPicas)}): Columns sit too close—readers' eyes may accidentally jump across the gutter! Increase to ≥ 1p0."
            hasWhitespaceRiverRisk ->
                "RIVER RISK: Justified text inside narrow ${columns}-column measures (~$estimatedCpl CPL) stretches word spaces. Switch to Flush Left or fewer columns."
            hasCenteredBodyWarning ->
                "WEAK AXIS: Centered body copy dissolves the crisp vertical edge of your ${columns}-column grid. Use Flush Left for clean visual anchors."
            else ->
                "STRONG GRID HARMONY: ${columns}-col layout (${DtpCurriculum.formatPicas(singleColumnWidthPicas)} measure • ~$estimatedCpl CPL) with crisp alignment & grid-snapped imagery."
        }

    val isOptimalGrid: Boolean
        get() = imagePlacement.isSnappedToGrid && !hasGutterJumpRisk && !hasWhitespaceRiverRisk && !hasCenteredBodyWarning
}
