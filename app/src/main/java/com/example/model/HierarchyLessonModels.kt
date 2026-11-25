package com.example.model

import kotlin.math.roundToInt

enum class ArticleTypeElement(
    val shortLabel: String,
    val fullName: String,
    val roleSummary: String
) {
    KICKER(
        shortLabel = "Kicker",
        fullName = "All-Caps Kicker / Eyebrow",
        roleSummary = "Small uppercase labels need generous positive tracking so letters breathe and separate from the headline."
    ),
    HEADLINE(
        shortLabel = "Headline",
        fullName = "Display Headline",
        roleSummary = "Large display titles require tight optical pair kerning on diagonals, slightly snug tracking, and compact leading."
    ),
    SUBHEAD(
        shortLabel = "Deck",
        fullName = "Standfirst / Subhead",
        roleSummary = "The deck bridges display title and body text—medium size, neutral tracking, and steady 1.25x–1.45x leading."
    ),
    BODY(
        shortLabel = "Body",
        fullName = "Editorial Body Copy",
        roleSummary = "Body copy prioritizes sustained reading rhythm: neutral tracking and comfortable 1.38x–1.58x leading."
    )
}

data class ArticleHierarchyState(
    val kickerTrackingEm: Int,      // in 1/1000 em (-80..+240)
    val headlineKerningEm: Int,     // pair kerning on target diagonal pair (-120..+120)
    val headlineTrackingEm: Int,    // overall headline tracking (-80..+120)
    val headlineLeadingRatio: Float,// 0.85f..1.80f
    val subheadTrackingEm: Int,     // -80..+120
    val subheadLeadingRatio: Float, // 0.95f..1.85f
    val bodyTrackingEm: Int,        // -80..+120
    val bodyLeadingRatio: Float     // 0.95f..2.05f
)

data class HierarchyIssueCheck(
    val id: String,
    val element: ArticleTypeElement,
    val title: String,
    val passed: Boolean,
    val feedback: String
)

data class ArticleHierarchyLesson(
    val id: Int,
    val badgeCode: String,
    val title: String,
    val problemOverview: String,
    val kickerText: String,
    val headlineText: String,
    val kernPairIndex: Int, // index of first char in headlineText for targeted pair kerning
    val kernPairDisplay: String, // e.g. "A/V"
    val subheadText: String,
    val bodyParagraph: String,
    val bylineText: String,
    val kickerSizePt: Float = 10f,
    val headlineSizePt: Float = 26f,
    val subheadSizePt: Float = 14f,
    val bodySizePt: Float = 12f,
    val brokenState: ArticleHierarchyState,
    val idealState: ArticleHierarchyState,
    val evaluate: (ArticleHierarchyState) -> List<HierarchyIssueCheck>
)

data class HierarchyEvaluation(
    val checks: List<HierarchyIssueCheck>,
    val scorePercent: Int,
    val allFixed: Boolean,
    val verdictTitle: String
) {
    fun elementPassed(element: ArticleTypeElement): Boolean {
        val relevant = checks.filter { it.element == element }
        return relevant.isNotEmpty() && relevant.all { it.passed }
    }

    fun elementHasIssue(element: ArticleTypeElement): Boolean {
        return checks.any { it.element == element && !it.passed }
    }
}

object HierarchyLessonCatalog {

    val lessons: List<ArticleHierarchyLesson> = listOf(
        ArticleHierarchyLesson(
            id = 1,
            badgeCode = "CASE 01 • FEATURE OPENER",
            title = "Floating Headline & Cramped Body",
            problemOverview = "The all-caps kicker is squished, the display headline's A/V pair drifts apart with horsey line spacing, and body lines crash into each other.",
            kickerText = "SWISS DESIGN • ESSAY",
            headlineText = "AVANT-GARDE\nPRINT GRIDS",
            kernPairIndex = 0, // A-V
            kernPairDisplay = "A/V",
            subheadText = "How postwar typographers tamed complex editorial copy using disciplined leading and optical spacing.",
            bodyParagraph = "When line spacing collapses on body text, ascenders collide with descenders and readers lose horizontal rhythm. Conversely, wide leading on a two-line display headline breaks unity so it reads like two unrelated titles.",
            bylineText = "BY CLARA VOGEL • ZURICH ARCHIVE",
            brokenState = ArticleHierarchyState(
                kickerTrackingEm = -40,
                headlineKerningEm = 85,
                headlineTrackingEm = 45,
                headlineLeadingRatio = 1.62f,
                subheadTrackingEm = 0,
                subheadLeadingRatio = 1.35f,
                bodyTrackingEm = 75,
                bodyLeadingRatio = 1.04f
            ),
            idealState = ArticleHierarchyState(
                kickerTrackingEm = 130,
                headlineKerningEm = -55,
                headlineTrackingEm = -15,
                headlineLeadingRatio = 1.12f,
                subheadTrackingEm = 5,
                subheadLeadingRatio = 1.36f,
                bodyTrackingEm = 0,
                bodyLeadingRatio = 1.48f
            ),
            evaluate = { s ->
                listOf(
                    HierarchyIssueCheck(
                        id = "l1_kicker_track",
                        element = ArticleTypeElement.KICKER,
                        title = "All-Caps Kicker Tracking (+80 to +200)",
                        passed = s.kickerTrackingEm in 80..200,
                        feedback = when {
                            s.kickerTrackingEm < 80 -> "Too tight (${s.kickerTrackingEm}): small ALL-CAPS need +80 to +200 tracking to breathe."
                            s.kickerTrackingEm > 200 -> "Too wide (${s.kickerTrackingEm}): letters drift apart into disconnected specks."
                            else -> "Crisp uppercase tracking (+${s.kickerTrackingEm}) establishes a clean eyebrow."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l1_head_kern",
                        element = ArticleTypeElement.HEADLINE,
                        title = "Optical Pair Kerning on A/V (-90 to -20)",
                        passed = s.headlineKerningEm in -90..-20,
                        feedback = when {
                            s.headlineKerningEm > -20 -> "Gap between A and V (${s.headlineKerningEm}) creates a distracting white wedge—tuck it negative (-90 to -20)."
                            s.headlineKerningEm < -90 -> "A and V are colliding (${s.headlineKerningEm})—loosen slightly."
                            else -> "Balanced optical kerning (${s.headlineKerningEm}) locks the A/V diagonals together."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l1_head_lead",
                        element = ArticleTypeElement.HEADLINE,
                        title = "Compact Display Leading (1.02x–1.22x) & Tracking",
                        passed = s.headlineLeadingRatio in 1.02f..1.22f && s.headlineTrackingEm in -40..15,
                        feedback = when {
                            s.headlineLeadingRatio > 1.22f -> "Headline leading (${formatRatio(s.headlineLeadingRatio)}) is too loose—the two lines feel disconnected."
                            s.headlineLeadingRatio < 1.02f -> "Headline lines nearly overlap (${formatRatio(s.headlineLeadingRatio)})—open slightly."
                            s.headlineTrackingEm > 15 -> "Headline tracking (+${s.headlineTrackingEm}) is too wide for display type—tighten below +15."
                            else -> "Display headline reads as one bold, unified typographic block."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l1_body_lead_track",
                        element = ArticleTypeElement.BODY,
                        title = "Body Reading Rhythm (1.38x–1.60x Leading, Neutral Tracking)",
                        passed = s.bodyLeadingRatio in 1.38f..1.60f && s.bodyTrackingEm in -15..20,
                        feedback = when {
                            s.bodyLeadingRatio < 1.38f -> "Body leading (${formatRatio(s.bodyLeadingRatio)}) is cramped—raise to 1.38x–1.60x (~17–19 pt)."
                            s.bodyLeadingRatio > 1.60f -> "Body leading (${formatRatio(s.bodyLeadingRatio)}) feels loose and airy—tighten below 1.60x."
                            s.bodyTrackingEm > 20 || s.bodyTrackingEm < -15 -> "Body tracking (${s.bodyTrackingEm}) disrupts word shapes—keep between -15 and +20."
                            else -> "Comfortable reading rhythm achieved with neutral body tracking and open leading."
                        }
                    )
                )
            }
        ),
        ArticleHierarchyLesson(
            id = 2,
            badgeCode = "CASE 02 • ARCHITECTURE REVIEW",
            title = "Loose Deck & Horsey Body Copy",
            problemOverview = "Excessive body leading makes paragraphs break into isolated stripes, the deck is over-tightened, and the T/O headline pair needs optical kerning.",
            kickerText = "MONOGRAPH • NO. 09",
            headlineText = "TOWARD\nQUIET FORM",
            kernPairIndex = 0, // T-O
            kernPairDisplay = "T/O",
            subheadText = "Concrete pavilions and quiet proportions reveal why negative space carries equal weight to ink.",
            bodyParagraph = "When body copy receives excessive leading above 1.75x, the eye struggles to leap from the end of one line to the start of the next. Meanwhile, a standfirst deck needs clear spacing separation so it anchors the article opening.",
            bylineText = "BY MARTIN KELLER • BASEL EDITION",
            brokenState = ArticleHierarchyState(
                kickerTrackingEm = -20,
                headlineKerningEm = 75,
                headlineTrackingEm = 75,
                headlineLeadingRatio = 1.14f,
                subheadTrackingEm = -50,
                subheadLeadingRatio = 1.76f,
                bodyTrackingEm = -10,
                bodyLeadingRatio = 1.94f
            ),
            idealState = ArticleHierarchyState(
                kickerTrackingEm = 140,
                headlineKerningEm = -50,
                headlineTrackingEm = -15,
                headlineLeadingRatio = 1.12f,
                subheadTrackingEm = 5,
                subheadLeadingRatio = 1.34f,
                bodyTrackingEm = 0,
                bodyLeadingRatio = 1.46f
            ),
            evaluate = { s ->
                listOf(
                    HierarchyIssueCheck(
                        id = "l2_kicker",
                        element = ArticleTypeElement.KICKER,
                        title = "Open Kicker Tracking (+90 to +210)",
                        passed = s.kickerTrackingEm in 90..210,
                        feedback = if (s.kickerTrackingEm in 90..210) {
                            "Clean tracked eyebrow (+${s.kickerTrackingEm}) frames the monograph title."
                        } else {
                            "Increase kicker tracking to +90..+210 so uppercase numerals and letters read effortlessly."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l2_head_to",
                        element = ArticleTypeElement.HEADLINE,
                        title = "Tuck T/O Pair (-85 to -20) & Snug Headline Tracking",
                        passed = s.headlineKerningEm in -85..-20 && s.headlineTrackingEm in -40..15,
                        feedback = when {
                            s.headlineKerningEm !in -85..-20 -> "Tuck the O under the T crossbar (kerning -85 to -20)."
                            s.headlineTrackingEm !in -40..15 -> "Pull overall headline tracking down into -40..+15 so the serif holds weight."
                            else -> "T/O overhang tucked cleanly with cohesive display tracking."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l2_subhead",
                        element = ArticleTypeElement.SUBHEAD,
                        title = "Balanced Deck Spacing (1.24x–1.46x, Neutral Tracking)",
                        passed = s.subheadLeadingRatio in 1.24f..1.46f && s.subheadTrackingEm in -15..25,
                        feedback = when {
                            s.subheadTrackingEm < -15 -> "Subhead tracking (${s.subheadTrackingEm}) is jammed together—bring near 0."
                            s.subheadLeadingRatio > 1.46f -> "Deck leading (${formatRatio(s.subheadLeadingRatio)}) floats too far apart—tighten to 1.24x–1.46x."
                            s.subheadLeadingRatio < 1.24f -> "Deck leading (${formatRatio(s.subheadLeadingRatio)}) is slightly tight—open to 1.24x–1.46x."
                            else -> "Standfirst deck cleanly bridges headline and body copy."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l2_body",
                        element = ArticleTypeElement.BODY,
                        title = "Fix Horsey Body Leading (1.38x–1.58x)",
                        passed = s.bodyLeadingRatio in 1.38f..1.58f && s.bodyTrackingEm in -15..20,
                        feedback = if (s.bodyLeadingRatio in 1.38f..1.58f && s.bodyTrackingEm in -15..20) {
                            "Paragraph holds together as a balanced grey tone on the page."
                        } else {
                            "Reduce excessive body leading into 1.38x–1.58x so lines read as a cohesive paragraph."
                        }
                    )
                )
            }
        ),
        ArticleHierarchyLesson(
            id = 3,
            badgeCode = "CASE 03 • CULTURE ESSAY",
            title = "Colliding Display & Squeezed Copy",
            problemOverview = "Severe negative headline leading causes lines to collide, the W/A pair splits open, and both deck and body copy are overly cramped.",
            kickerText = "LETTERPRESS • ARCHIVE",
            headlineText = "WAX & LEAD\nTYPOGRAPHY",
            kernPairIndex = 0, // W-A
            kernPairDisplay = "W/A",
            subheadText = "From metal sorts locked in iron chases to Bézier outlines, optical spacing defines visual authority.",
            bodyParagraph = "Metal type founders literally shaved overhanging lead blocks—called kerns—so diagonal letters like W and A could nestle smoothly. Today in InDesign and QuarkXPress, optical kerning and thoughtful leading solve the exact same hierarchy challenge.",
            bylineText = "BY ELENA ROSSI • MILAN STUDIO",
            brokenState = ArticleHierarchyState(
                kickerTrackingEm = -35,
                headlineKerningEm = 95,
                headlineTrackingEm = -55,
                headlineLeadingRatio = 0.88f,
                subheadTrackingEm = 55,
                subheadLeadingRatio = 1.00f,
                bodyTrackingEm = -48,
                bodyLeadingRatio = 1.08f
            ),
            idealState = ArticleHierarchyState(
                kickerTrackingEm = 125,
                headlineKerningEm = -60,
                headlineTrackingEm = -10,
                headlineLeadingRatio = 1.12f,
                subheadTrackingEm = 0,
                subheadLeadingRatio = 1.35f,
                bodyTrackingEm = 0,
                bodyLeadingRatio = 1.48f
            ),
            evaluate = { s ->
                listOf(
                    HierarchyIssueCheck(
                        id = "l3_kicker",
                        element = ArticleTypeElement.KICKER,
                        title = "Airy Kicker Tracking (+80 to +200)",
                        passed = s.kickerTrackingEm in 80..200,
                        feedback = if (s.kickerTrackingEm in 80..200) {
                            "Kicker sits cleanly above the headline with clear hierarchy."
                        } else {
                            "Open up kicker tracking (+80 to +200) to relieve squeezed capitals."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l3_head",
                        element = ArticleTypeElement.HEADLINE,
                        title = "Fix W/A Kerning (-95 to -25) & Colliding Leading (1.04x–1.22x)",
                        passed = s.headlineKerningEm in -95..-25 &&
                            s.headlineLeadingRatio in 1.04f..1.22f &&
                            s.headlineTrackingEm in -35..15,
                        feedback = when {
                            s.headlineLeadingRatio < 1.04f -> "Headline lines collide (${formatRatio(s.headlineLeadingRatio)})—raise leading to 1.04x–1.22x."
                            s.headlineKerningEm !in -95..-25 -> "Close the W/A gap with negative pair kerning (-95 to -25)."
                            s.headlineTrackingEm !in -35..15 -> "Set overall headline tracking between -35 and +15."
                            else -> "Headline has strong impact with zero collisions and tight W/A optics."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l3_subhead",
                        element = ArticleTypeElement.SUBHEAD,
                        title = "Open Deck Leading (1.24x–1.46x) & Neutral Tracking",
                        passed = s.subheadLeadingRatio in 1.24f..1.46f && s.subheadTrackingEm in -15..20,
                        feedback = if (s.subheadLeadingRatio in 1.24f..1.46f && s.subheadTrackingEm in -15..20) {
                            "Standfirst reads smoothly between headline and essay text."
                        } else {
                            "Set deck leading to 1.24x–1.46x and pull tracking back near 0."
                        }
                    ),
                    HierarchyIssueCheck(
                        id = "l3_body",
                        element = ArticleTypeElement.BODY,
                        title = "Restore Body Tracking (-10..+20) & Leading (1.38x–1.60x)",
                        passed = s.bodyTrackingEm in -10..20 && s.bodyLeadingRatio in 1.38f..1.60f,
                        feedback = if (s.bodyTrackingEm in -10..20 && s.bodyLeadingRatio in 1.38f..1.60f) {
                            "Body text is crisp, legible, and rhythmically paced."
                        } else {
                            "Relieve squished body tracking (-10 to +20) and raise leading to 1.38x–1.60x."
                        }
                    )
                )
            }
        )
    )

    fun evaluate(lesson: ArticleHierarchyLesson, state: ArticleHierarchyState): HierarchyEvaluation {
        val checks = lesson.evaluate(state)
        val passed = checks.count { it.passed }
        val scorePercent = if (checks.isEmpty()) 100 else ((passed.toFloat() / checks.size) * 100f).roundToInt()
        val allFixed = passed == checks.size
        val verdict = when {
            allFixed -> "EDITORIAL HIERARCHY CERTIFIED"
            scorePercent >= 50 -> "IMPROVING — RESOLVE REMAINING FLAGS"
            else -> "HIERARCHY NEEDS REPAIR"
        }
        return HierarchyEvaluation(
            checks = checks,
            scorePercent = scorePercent,
            allFixed = allFixed,
            verdictTitle = verdict
        )
    }

    fun formatRatio(ratio: Float): String {
        val rounded = (ratio * 100f).roundToInt() / 100f
        return "${rounded}x"
    }

    fun formatEm(em: Int): String = if (em > 0) "+$em" else "$em"
}
