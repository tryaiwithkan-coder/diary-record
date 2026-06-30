package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiaryEntry
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    entries: List<DiaryEntry>,
    onDeleteEntry: (DiaryEntry) -> Unit,
    onBackClick: () -> Unit
) {
    val currentLang = LanguageManager.currentLanguage.value
    var selectedTab by remember { mutableStateOf(0) } // 0: Tất cả, 1: Mốc phát triển
    val filteredEntries = remember(entries, selectedTab) {
        if (selectedTab == 0) {
            entries
        } else {
            entries.filter { it.isMilestone }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LanguageManager.getString("timeline_memories"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = if (currentLang == AppLanguage.VI) "Quay lại" else "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Interactive Tab Selection (Tất cả vs Mốc phát triển)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().testTag("timeline_tabs")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(LanguageManager.getString("all_memories"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Filled.List, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(LanguageManager.getString("growth_milestones"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Filled.Star, contentDescription = null) }
                )
            }

            if (filteredEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.List else Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (selectedTab == 0) {
                                if (currentLang == AppLanguage.VI) "Chưa có kỷ niệm nào được lưu.\nBa mẹ hãy viết nhật ký cho con nhé!" else "保存された思い出はありません。\n今日の日記を書いてみましょう！"
                            } else {
                                if (currentLang == AppLanguage.VI) "Chưa có mốc phát triển nổi bật nào được đánh dấu." else "マークされた重要な成長の節目はありません。"
                            },
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().testTag("timeline_list")
                ) {
                    items(filteredEntries, key = { it.id }) { entry ->
                        TimelineItemRow(
                            entry = entry,
                            currentLang = currentLang,
                            onDelete = { onDeleteEntry(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItemRow(
    entry: DiaryEntry,
    currentLang: AppLanguage,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timeline_item_${entry.id}"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Timeline Track Node
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Bullet node
            val bulletColor = if (entry.isMilestone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(bulletColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (entry.isMilestone) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = bulletColor,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(bulletColor)
                    )
                }
            }

            // Connection line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    .padding(vertical = 4.dp)
            )
        }

        // Detailed Card content
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (entry.isMilestone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header (Date & Emotion & Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.date,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Emotion Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            val localizedMood = when (entry.mood) {
                                "Vui vẻ" -> if (currentLang == AppLanguage.VI) "Vui vẻ" else "喜び"
                                "Hòa đồng" -> if (currentLang == AppLanguage.VI) "Hòa đồng" else "社交的"
                                "Mệt mỏi" -> if (currentLang == AppLanguage.VI) "Mệt mỏi" else "お疲れ"
                                "Khóc nhè" -> if (currentLang == AppLanguage.VI) "Khóc nhè" else "泣き虫"
                                "Ngoan ngoãn" -> if (currentLang == AppLanguage.VI) "Ngoan ngoãn" else "お利口"
                                "Bình thường" -> if (currentLang == AppLanguage.VI) "Bình thường" else "普通"
                                else -> entry.mood
                            }
                            Text(localizedMood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Delete button
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp).testTag("delete_entry_${entry.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = if (currentLang == AppLanguage.VI) "Xóa nhật ký" else "日記を削除",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = entry.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Milestone marker text
                if (entry.isMilestone && !entry.milestoneTitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLang == AppLanguage.VI) "Mốc phát triển: ${entry.milestoneTitle}" else "成長の節目: ${entry.milestoneTitle}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Primary diary text note
                Text(
                    text = entry.note,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )

                // Collapsible advanced sections (AI extracts: Teacher Feedback, Activities)
                AnimatedVisibility(visible = isExpanded || !entry.teacherFeedback.isNullOrBlank() || !entry.activities.isNullOrBlank()) {
                    Column {
                        // Teacher comments section
                        if (!entry.teacherFeedback.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Teacher comment",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(LanguageManager.getString("teacher_feedback_title"), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(entry.teacherFeedback, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }

                        // Baby Activities section
                        if (!entry.activities.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLang == AppLanguage.VI) "Hoạt động: ${entry.activities}" else "活動: ${entry.activities}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom row: Tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // List of hashtags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        entry.getTagsList().forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("#$tag", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    // Click indicator
                    Text(
                        text = if (isExpanded) {
                            if (currentLang == AppLanguage.VI) "Thu nhỏ" else "閉じる"
                        } else {
                            if (currentLang == AppLanguage.VI) "Xem thêm" else "詳細を見る"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
