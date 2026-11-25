package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArticleHierarchyLesson
import com.example.model.ArticleHierarchyState
import com.example.model.ArticleTypeElement
import com.example.model.HierarchyEvaluation
import com.example.model.HierarchyLessonCatalog
import com.example.ui.theme.ArchivalPaperCream
import com.example.ui.theme.BleedAlertRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.PressInkBlack
import com.example.ui.theme.PressInkMuted
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.TextSecondaryMuted
import kotlin.math.roundToInt

/**
 * Interactive editorial mock-article sheet.
 * Renders real-time kerning (targeted display pair), tracking (uniform letter-spacing),
 * and leading (line-height ratio) across Kicker, Headline, Subhead, and Body Copy.
 */
@Composable
fun MockArticleHierarchyCanvas(
    lesson: ArticleHierarchyLesson,
    state: ArticleHierarchyState,
    evaluation: HierarchyEvaluation,
    selectedElement: ArticleTypeElement,
    showOverlayGuides: Boolean,
    comparingBroken: Boolean,
    onSelectElement: (ArticleTypeElement) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDisplayState = if (comparingBroken) lesson.brokenState else state

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ArchivalPaperCream)
            .border(
                width = if (comparingBroken) 2.dp else 1.dp,
                color = if (comparingBroken) BleedAlertRed else StudioBorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
            .testTag("mock_article_hierarchy_canvas")
    ) {
        // Editorial Folio Header Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (comparingBroken) "ORIGINAL BROKEN PROOF" else "PICAPRESS EDITORIAL PROOF",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (comparingBroken) BleedAlertRed else PressInkMuted
            )
            Text(
                text = "Tap any block to inspect",
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 9.sp,
                color = PressInkMuted.copy(alpha = 0.78f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        HorizontalDivider(color = PressInkBlack.copy(alpha = 0.2f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // 1. KICKER / EYEBROW
        ArticleEditableRegion(
            element = ArticleTypeElement.KICKER,
            selected = selectedElement == ArticleTypeElement.KICKER,
            passed = evaluation.elementPassed(ArticleTypeElement.KICKER),
            showOverlayGuides = showOverlayGuides && !comparingBroken,
            badgeText = "TRK ${HierarchyLessonCatalog.formatEm(activeDisplayState.kickerTrackingEm)}",
            onClick = { onSelectElement(ArticleTypeElement.KICKER) },
            testTag = "article_region_kicker"
        ) {
            // Convert 1/1000 em tracking to sp relative to kicker font size (10sp)
            val kickerLetterSpacingSp = (activeDisplayState.kickerTrackingEm / 1000f * lesson.kickerSizePt * 1.8f).sp
            Text(
                text = lesson.kickerText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = lesson.kickerSizePt.sp,
                letterSpacing = kickerLetterSpacingSp,
                color = Color(0xFFB41F5E) // Swiss editorial crimson accent
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2. DISPLAY HEADLINE (Demonstrates targeted Pair Kerning + overall Tracking + Display Leading)
        ArticleEditableRegion(
            element = ArticleTypeElement.HEADLINE,
            selected = selectedElement == ArticleTypeElement.HEADLINE,
            passed = evaluation.elementPassed(ArticleTypeElement.HEADLINE),
            showOverlayGuides = showOverlayGuides && !comparingBroken,
            badgeText = "${lesson.kernPairDisplay} ${HierarchyLessonCatalog.formatEm(activeDisplayState.headlineKerningEm)} • LD ${HierarchyLessonCatalog.formatRatio(activeDisplayState.headlineLeadingRatio)}",
            onClick = { onSelectElement(ArticleTypeElement.HEADLINE) },
            testTag = "article_region_headline"
        ) {
            val headBaseTrackingSp = (activeDisplayState.headlineTrackingEm / 1000f * lesson.headlineSizePt * 1.4f).sp
            val headPairKerningSp = (activeDisplayState.headlineKerningEm / 1000f * lesson.headlineSizePt * 1.9f).sp
            val headLineHeightSp = (lesson.headlineSizePt * activeDisplayState.headlineLeadingRatio).sp

            val annotatedHeadline = buildAnnotatedString {
                lesson.headlineText.forEachIndexed { idx, ch ->
                    val isKernFirstChar = idx == lesson.kernPairIndex
                    val isKernSecondChar = idx == lesson.kernPairIndex + 1
                    val highlightPair = showOverlayGuides && !comparingBroken &&
                        selectedElement == ArticleTypeElement.HEADLINE &&
                        (isKernFirstChar || isKernSecondChar)

                    val style = SpanStyle(
                        letterSpacing = if (isKernFirstChar) headPairKerningSp else headBaseTrackingSp,
                        background = if (highlightPair) ProcessCyanGuide.copy(alpha = 0.24f) else Color.Transparent,
                        color = if (highlightPair) Color(0xFF0B4F6C) else PressInkBlack
                    )
                    withStyle(style) {
                        append(ch)
                    }
                }
            }

            Text(
                text = annotatedHeadline,
                fontFamily = PlayfairDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = lesson.headlineSizePt.sp,
                lineHeight = headLineHeightSp,
                color = PressInkBlack
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. DECK / SUBHEAD
        ArticleEditableRegion(
            element = ArticleTypeElement.SUBHEAD,
            selected = selectedElement == ArticleTypeElement.SUBHEAD,
            passed = evaluation.elementPassed(ArticleTypeElement.SUBHEAD),
            showOverlayGuides = showOverlayGuides && !comparingBroken,
            badgeText = "TRK ${HierarchyLessonCatalog.formatEm(activeDisplayState.subheadTrackingEm)} • LD ${HierarchyLessonCatalog.formatRatio(activeDisplayState.subheadLeadingRatio)}",
            onClick = { onSelectElement(ArticleTypeElement.SUBHEAD) },
            testTag = "article_region_subhead"
        ) {
            val subTrackingSp = (activeDisplayState.subheadTrackingEm / 1000f * lesson.subheadSizePt * 1.4f).sp
            val subLineHeightSp = (lesson.subheadSizePt * activeDisplayState.subheadLeadingRatio).sp

            Text(
                text = lesson.subheadText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = lesson.subheadSizePt.sp,
                letterSpacing = subTrackingSp,
                lineHeight = subLineHeightSp,
                color = Color(0xFF33302B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Editorial Byline Rule
        Text(
            text = lesson.bylineText,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp,
            color = PressInkMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. BODY COPY
        ArticleEditableRegion(
            element = ArticleTypeElement.BODY,
            selected = selectedElement == ArticleTypeElement.BODY,
            passed = evaluation.elementPassed(ArticleTypeElement.BODY),
            showOverlayGuides = showOverlayGuides && !comparingBroken,
            badgeText = "TRK ${HierarchyLessonCatalog.formatEm(activeDisplayState.bodyTrackingEm)} • ${(lesson.bodySizePt * activeDisplayState.bodyLeadingRatio).roundToInt()}pt (${HierarchyLessonCatalog.formatRatio(activeDisplayState.bodyLeadingRatio)})",
            onClick = { onSelectElement(ArticleTypeElement.BODY) },
            testTag = "article_region_body"
        ) {
            val bodyTrackingSp = (activeDisplayState.bodyTrackingEm / 1000f * lesson.bodySizePt * 1.4f).sp
            val bodyLineHeightSp = (lesson.bodySizePt * activeDisplayState.bodyLeadingRatio).sp

            Text(
                text = lesson.bodyParagraph,
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = lesson.bodySizePt.sp,
                letterSpacing = bodyTrackingSp,
                lineHeight = bodyLineHeightSp,
                color = PressInkBlack.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun ArticleEditableRegion(
    element: ArticleTypeElement,
    selected: Boolean,
    passed: Boolean,
    showOverlayGuides: Boolean,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    val borderColor = when {
        !showOverlayGuides -> Color.Transparent
        selected -> ProcessCyanGuide
        passed -> PreflightPassGreen.copy(alpha = 0.55f)
        else -> BleedAlertRed.copy(alpha = 0.65f)
    }

    val bgTint = when {
        !showOverlayGuides -> Color.Transparent
        selected -> ProcessCyanGuide.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgTint)
            .border(
                width = if (selected && showOverlayGuides) 1.5.dp else if (showOverlayGuides) 1.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = if (showOverlayGuides) 8.dp else 2.dp, vertical = if (showOverlayGuides) 6.dp else 2.dp)
            .testTag(testTag)
    ) {
        if (showOverlayGuides) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (passed) PreflightPassGreen else BleedAlertRed)
                    )
                    Text(
                        text = element.shortLabel.uppercase(),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color(0xFF007EA7) else PressInkMuted
                    )
                }

                Surface(
                    color = if (passed) PreflightPassGreen.copy(alpha = 0.14f) else ProcessMagentaGuide.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (passed) Color(0xFF065F46) else Color(0xFF9D174D),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
        content()
    }
}
