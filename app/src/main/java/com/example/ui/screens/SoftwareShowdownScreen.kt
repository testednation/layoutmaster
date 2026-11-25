package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.QuizAttemptEntity
import com.example.model.DtpCurriculum
import com.example.model.RosettaStoneEntry
import com.example.ui.theme.BleedAlertRed
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
fun SoftwareShowdownScreen(
    quizAttempts: List<QuizAttemptEntity>,
    onRecordQuizScore: (modeTitle: String, score: Int, total: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Interactive Showdown Quiz, 1 = InDesign vs QuarkXPress Rosetta Stone Reference
    var activeSubTab by remember { mutableIntStateOf(0) }

    val questions = DtpCurriculum.showdownQuestions
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var runningScore by remember { mutableIntStateOf(0) }
    var quizCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianPasteboard)
            .testTag("software_showdown_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header + Sub-mode Toggle
        item {
            Column {
                Text(
                    text = "InDesign vs. QuarkXPress",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
                Text(
                    text = "Master the shortcuts, prepress vocabulary, and workflow differences between the two DTP giants",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = activeSubTab == 0,
                        onClick = { activeSubTab = 0 },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("Showdown Quiz (${questions.size} Qs)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessCyanGuide.copy(alpha = 0.22f),
                            selectedLabelColor = ProcessCyanGuide
                        ),
                        modifier = Modifier.testTag("tab_showdown_quiz")
                    )
                    FilterChip(
                        selected = activeSubTab == 1,
                        onClick = { activeSubTab = 1 },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = { Text("Id ↔ QXP Rosetta Stone") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ProcessMagentaGuide.copy(alpha = 0.22f),
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("tab_rosetta_stone")
                    )
                }
            }
        }

        if (activeSubTab == 0) {
            if (!quizCompleted) {
                val question = questions[currentQuestionIndex]
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(18.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ProcessMagentaGuide.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = question.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ProcessMagentaGuide,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Question ${currentQuestionIndex + 1} of ${questions.size} • Score: $runningScore",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ProcessYellowGold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = question.prompt,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            question.options.forEachIndexed { idx, optionText ->
                                val isSelected = selectedOptionIndex == idx
                                val isCorrect = idx == question.correctIndex
                                val answered = selectedOptionIndex != null

                                val borderColor = when {
                                    !answered -> StudioBorderSubtle
                                    isCorrect -> PreflightPassGreen
                                    isSelected -> BleedAlertRed
                                    else -> StudioBorderSubtle.copy(alpha = 0.5f)
                                }
                                val bgColor = when {
                                    !answered -> ObsidianPasteboard
                                    isCorrect -> PreflightPassGreen.copy(alpha = 0.16f)
                                    isSelected -> BleedAlertRed.copy(alpha = 0.16f)
                                    else -> ObsidianPasteboard.copy(alpha = 0.6f)
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bgColor)
                                        .border(1.2.dp, borderColor, RoundedCornerShape(12.dp))
                                        .clickable(enabled = !answered) {
                                            selectedOptionIndex = idx
                                            if (idx == question.correctIndex) {
                                                runningScore += 1
                                            }
                                        }
                                        .padding(14.dp)
                                        .testTag("quiz_option_$idx")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (answered && isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Correct Answer",
                                                tint = PreflightPassGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Explanation Box once answered
                            if (selectedOptionIndex != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StudioDockSurface)
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = ProcessCyanGuide,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (selectedOptionIndex == question.correctIndex) {
                                                "Spot On — Press Approved!"
                                            } else {
                                                "Prepress Correction Note:"
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selectedOptionIndex == question.correctIndex) {
                                                PreflightPassGreen
                                            } else {
                                                ProcessYellowGold
                                            }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFD5DCF0)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        if (currentQuestionIndex < questions.size - 1) {
                                            currentQuestionIndex += 1
                                            selectedOptionIndex = null
                                        } else {
                                            quizCompleted = true
                                            onRecordQuizScore(
                                                "InDesign vs. QuarkXPress Showdown",
                                                runningScore,
                                                questions.size
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ProcessCyanGuide,
                                        contentColor = ObsidianPasteboard
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("next_quiz_question_button")
                                ) {
                                    Text(
                                        text = if (currentQuestionIndex < questions.size - 1) {
                                            "Next Challenge →"
                                        } else {
                                            "Finish Showdown & Record Score"
                                        },
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Quiz Completed Summary Card
                item {
                    val percent = (runningScore * 100) / questions.size.coerceAtLeast(1)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PreflightPassGreen, RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ProcessYellowGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Showdown Complete: $runningScore / ${questions.size} ($percent%)",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (percent >= 80) {
                                    "You speak fluent Adobe InDesign AND QuarkXPress! Your score has been archived."
                                } else {
                                    "Review the Id ↔ QXP Rosetta Stone tab to master every shortcut and prepress term."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryMuted
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    currentQuestionIndex = 0
                                    selectedOptionIndex = null
                                    runningScore = 0
                                    quizCompleted = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProcessCyanGuide,
                                    contentColor = ObsidianPasteboard
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play Showdown Again")
                            }
                        }
                    }
                }
            }

            // Recent Quiz Attempts History from Room
            if (quizAttempts.isNotEmpty()) {
                item {
                    Text(
                        text = "Past Showdown Runs",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(quizAttempts.take(5), key = { it.id }) { attempt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioDockSurface)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = attempt.modeTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "${attempt.score}/${attempt.totalQuestions} (${attempt.accuracyPercent}%)",
                            style = MaterialTheme.typography.labelLarge,
                            color = ProcessYellowGold
                        )
                    }
                }
            }
        } else {
            // Sub-Tab 1: InDesign vs. QuarkXPress Rosetta Stone Reference Cards
            items(DtpCurriculum.rosettaStoneEntries, key = { it.concept }) { entry ->
                RosettaStoneComparisonCard(entry = entry)
            }
        }
    }
}

@Composable
private fun RosettaStoneComparisonCard(entry: RosettaStoneEntry) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCardSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioBorderSubtle, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.concept,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Surface(
                    color = ProcessCyanGuide.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = entry.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = ProcessCyanGuide,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Adobe InDesign Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianPasteboard)
                    .border(1.dp, InDesignMagenta.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Id • ADOBE INDESIGN",
                    style = MaterialTheme.typography.labelSmall,
                    color = InDesignMagenta,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = entry.indesignWay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // QuarkXPress Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianPasteboard)
                    .border(1.dp, QuarkClassicTeal.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "QXP • QUARKXPRESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = QuarkClassicTeal,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = entry.quarkWay,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = null,
                    tint = ProcessYellowGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = entry.whyItMatters,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ProcessYellowGold
                )
            }
        }
    }
}
