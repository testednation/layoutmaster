package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DtpGlossaryCatalog
import com.example.model.DtpGlossaryTerm
import com.example.model.GlossaryCategory
import com.example.model.GlossaryTargetModule
import com.example.ui.theme.InDesignMagenta
import com.example.ui.theme.ObsidianPasteboard
import com.example.ui.theme.PreflightPassGreen
import com.example.ui.theme.ProcessCyanGuide
import com.example.ui.theme.ProcessMagentaGuide
import com.example.ui.theme.ProcessYellowGold
import com.example.ui.theme.QuarkClassicTeal
import com.example.ui.theme.StudioBorderSubtle
import com.example.ui.theme.StudioCardSurface
import com.example.ui.theme.StudioDockSurface
import com.example.ui.theme.TextSecondaryMuted

@Composable
fun DtpGlossaryScreen(
    onNavigateToModule: (GlossaryTargetModule) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(GlossaryCategory.ALL) }
    var selectedAlphaRange by rememberSaveable { mutableStateOf("All") }
    var onlyBookmarked by rememberSaveable { mutableStateOf(false) }
    var bookmarkedIds by remember { mutableStateOf(setOf("kerning", "bleed", "pdf_x_standards")) }
    var expandedTermId by remember { mutableStateOf<String?>(DtpGlossaryCatalog.terms.firstOrNull()?.id) }

    val filteredTerms = remember(searchQuery, selectedCategory, selectedAlphaRange, onlyBookmarked, bookmarkedIds) {
        DtpGlossaryCatalog.filterTerms(
            query = searchQuery,
            category = selectedCategory,
            alphaRange = selectedAlphaRange,
            onlyBookmarked = onlyBookmarked,
            bookmarkedIds = bookmarkedIds
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("dtp_glossary_screen"),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Glossary Header & Search Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Interactive DTP Glossary",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Search InDesign & QuarkXPress prepress terms and jump straight into hands-on labs",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    }
                    Surface(
                        color = ProcessCyanGuide.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${filteredTerms.size}/${DtpGlossaryCatalog.terms.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = ProcessCyanGuide,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Instant Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search kerning, bleed, PDF/X, gutter, QuarkXPress...",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search glossary terms",
                            tint = ProcessCyanGuide
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("glossary_clear_search")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = TextSecondaryMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProcessCyanGuide,
                        unfocusedBorderColor = StudioBorderSubtle,
                        focusedContainerColor = StudioCardSurface,
                        unfocusedContainerColor = StudioCardSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("glossary_search_input")
                )

                // Category & Bookmark Filter Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = onlyBookmarked,
                        onClick = { onlyBookmarked = !onlyBookmarked },
                        leadingIcon = {
                            Icon(
                                imageVector = if (onlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Filter saved terms",
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Saved (${bookmarkedIds.size})",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessYellowGold.copy(alpha = 0.22f),
                            selectedLabelColor = ProcessYellowGold
                        ),
                        modifier = Modifier.testTag("glossary_filter_saved")
                    )

                    GlossaryCategory.entries.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                                selectedLabelColor = ProcessCyanGuide
                            ),
                            modifier = Modifier.testTag("glossary_cat_${category.name.lowercase()}")
                        )
                    }
                }

                // Alphabetical Range Quick-Jump Bar
                val alphaRanges = listOf("All", "A-D", "E-K", "L-P", "R-Z")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    alphaRanges.forEach { range ->
                        val active = selectedAlphaRange == range
                        Surface(
                            color = if (active) ProcessMagentaGuide.copy(alpha = 0.24f) else StudioDockSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (active) ProcessMagentaGuide else StudioBorderSubtle,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedAlphaRange = range }
                                .testTag("glossary_alpha_${range.lowercase()}")
                        ) {
                            BoxCenteredLabel(
                                text = range,
                                active = active
                            )
                        }
                    }
                }
            }
        }

        // 2. Empty Search State
        if (filteredTerms.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StudioBorderSubtle, RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = TextSecondaryMuted,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = "No matching DTP terms found",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Try searching for kerning, bleed, PDF/X, master pages, pica, or clear active filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryMuted
                        )
                        OutlinedButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = GlossaryCategory.ALL
                                selectedAlphaRange = "All"
                                onlyBookmarked = false
                            },
                            modifier = Modifier.testTag("glossary_reset_filters_button")
                        ) {
                            Text("Reset Search & Filters")
                        }
                    }
                }
            }
        } else {
            // 3. Interactive Glossary Term Cards
            items(filteredTerms, key = { it.id }) { item ->
                val isExpanded = expandedTermId == item.id
                val isBookmarked = bookmarkedIds.contains(item.id)

                GlossaryTermCard(
                    item = item,
                    expanded = isExpanded,
                    bookmarked = isBookmarked,
                    onToggleExpand = {
                        expandedTermId = if (isExpanded) null else item.id
                    },
                    onToggleBookmark = {
                        bookmarkedIds = if (isBookmarked) {
                            bookmarkedIds - item.id
                        } else {
                            bookmarkedIds + item.id
                        }
                    },
                    onSelectKeyword = { keyword ->
                        searchQuery = keyword
                        selectedCategory = GlossaryCategory.ALL
                        selectedAlphaRange = "All"
                        onlyBookmarked = false
                    },
                    onNavigateToModule = { onNavigateToModule(item.targetModule) }
                )
            }
        }
    }
}

@Composable
private fun BoxCenteredLabel(text: String, active: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (active) Color.White else TextSecondaryMuted,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun GlossaryTermCard(
    item: DtpGlossaryTerm,
    expanded: Boolean,
    bookmarked: Boolean,
    onToggleExpand: () -> Unit,
    onToggleBookmark: () -> Unit,
    onSelectKeyword: (String) -> Unit,
    onNavigateToModule: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (expanded) 1.5.dp else 1.dp,
                color = if (expanded) ProcessCyanGuide.copy(alpha = 0.7f) else StudioBorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggleExpand)
            .testTag("glossary_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Term Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = ProcessCyanGuide.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                text = item.category.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = ProcessCyanGuide,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = PreflightPassGreen.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                text = "→ ${item.targetModule.badgeLabel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PreflightPassGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.term,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.aliasOrNote != null) {
                        Text(
                            text = item.aliasOrNote,
                            style = MaterialTheme.typography.labelSmall,
                            color = ProcessYellowGold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("glossary_bookmark_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (bookmarked) "Remove bookmark" else "Bookmark term",
                            tint = if (bookmarked) ProcessYellowGold else TextSecondaryMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse details" else "Expand details",
                        tint = TextSecondaryMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.definition,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD5DCF0)
            )

            // Expandable Deep Dive: InDesign vs QuarkXPress + Pro Tip + Direct Module Link
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // InDesign vs QuarkXPress Reference Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianPasteboard)
                            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = InDesignMagenta.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Id",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.inDesignReference,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = QuarkClassicTeal.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "QXP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.quarkXPressReference,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Pro Tip Callout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ProcessYellowGold.copy(alpha = 0.12f))
                            .padding(9.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Pro Tip",
                            tint = ProcessYellowGold,
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .size(16.dp)
                        )
                        Text(
                            text = item.practicalTip,
                            style = MaterialTheme.typography.bodySmall,
                            color = ProcessYellowGold
                        )
                    }

                    // Clickable Related Keyword Tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.relatedKeywords.forEach { keyword ->
                            Surface(
                                color = StudioDockSurface,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .border(1.dp, StudioBorderSubtle, RoundedCornerShape(6.dp))
                                    .clickable { onSelectKeyword(keyword) }
                            ) {
                                Text(
                                    text = "#$keyword",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryMuted,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Direct Deep-Link CTA into the Relevant Interactive Module
                    Button(
                        onClick = onNavigateToModule,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProcessCyanGuide,
                            contentColor = ObsidianPasteboard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("glossary_launch_${item.id}")
                    ) {
                        Text(
                            text = item.ctaLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open ${item.targetModule.moduleName}",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
