package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BabyProfile
import com.example.data.model.DiaryEntry
import com.example.data.model.GrowthRecord
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LanguageManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    baby: BabyProfile,
    growthRecords: List<GrowthRecord>,
    recentEntries: List<DiaryEntry>,
    onAddEntryClick: () -> Unit,
    onTimelineClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRecapClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentLang = LanguageManager.currentLanguage.value

    // Calculate age in months
    val ageString = remember(baby.birthDate, currentLang) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthDate = sdf.parse(baby.birthDate) ?: Date()
            val today = Calendar.getInstance().apply {
                set(2026, Calendar.JUNE, 29) // Sync with metadata local time (June 2026)
            }.time

            val startCalendar = Calendar.getInstance().apply { time = birthDate }
            val endCalendar = Calendar.getInstance().apply { time = today }

            val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
            val diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)

            if (diffMonth < 0) {
                if (currentLang == AppLanguage.VI) "Sắp chào đời" else "まもなく誕生"
            } else {
                val years = diffMonth / 12
                val remainingMonths = diffMonth % 12
                if (years > 0) {
                    if (currentLang == AppLanguage.VI) {
                        "$diffMonth tháng tuổi ($years tuổi $remainingMonths tháng)"
                    } else {
                        "生後 $diffMonth ヶ月 ($years 歳 $remainingMonths ヶ月)"
                    }
                } else {
                    if (currentLang == AppLanguage.VI) {
                        "$diffMonth tháng tuổi"
                    } else {
                        "生後 $diffMonth ヶ月"
                    }
                }
            }
        } catch (e: Exception) {
            if (currentLang == AppLanguage.VI) "25 tháng tuổi" else "生後 25 ヶ月"
        }
    }

    val latestGrowth = growthRecords.lastOrNull()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddEntryClick,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.testTag("add_diary_fab")
            ) {
                Icon(Icons.Filled.Add, contentDescription = LanguageManager.getString("write_diary"))
                Spacer(modifier = Modifier.width(8.dp))
                Text(LanguageManager.getString("write_diary"), fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp) // extra padding for FAB
        ) {
            // Heartwarming Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Profile Avatar (with soft color)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                            .clickable { onProfileClick() }
                            .testTag("baby_avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = baby.name.firstOrNull()?.toString()?.uppercase() ?: "B",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentLang == AppLanguage.VI) "Hành trình của ${baby.name}" else "${baby.name}ちゃんの旅",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = ageString,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Language Toggle!
                    IconButton(
                        onClick = { LanguageManager.toggleLanguage() },
                        modifier = Modifier.testTag("language_toggle_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentLang == AppLanguage.VI) "🇯🇵" else "🇻🇳",
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.testTag("profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Cài đặt hồ sơ",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions Hub
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Action 1: Timeline
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTimelineClick() }
                        .testTag("quick_action_timeline"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(LanguageManager.getString("timeline"), fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }

                // Action 2: Smart Search
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSearchClick() }
                        .testTag("quick_action_search"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(LanguageManager.getString("search"), fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }

                // Action 3: AI Recap Reports
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onRecapClick() }
                        .testTag("quick_action_recap"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(LanguageManager.getString("ai_recap"), fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Growth Metric Overview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onProfileClick() }
                    .testTag("growth_metric_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(LanguageManager.getString("latest_growth_stats"), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Height Info
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(LanguageManager.getString("height"), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                            Text(
                                text = if (latestGrowth != null) "${latestGrowth.heightCm} cm" else "92.0 cm",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Divider
                        Box(modifier = Modifier.width(1.dp).height(36.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))

                        // Weight Info
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(LanguageManager.getString("weight"), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                            Text(
                                text = if (latestGrowth != null) "${latestGrowth.weightKg} kg" else "13.5 kg",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Divider
                        latestGrowth?.headCircumferenceCm?.let {
                            Box(modifier = Modifier.width(1.dp).height(36.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))

                            // Head Circumference
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(if (currentLang == AppLanguage.VI) "Vòng đầu" else "頭囲", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                Text(
                                    text = "$it cm",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentLang == AppLanguage.VI) "Cập nhật lần cuối: ${latestGrowth?.date ?: "2026-05-15"}" else "最終更新日: ${latestGrowth?.date ?: "2026-05-15"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Memories Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getString("recent_diaries"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (currentLang == AppLanguage.VI) "Xem tất cả" else "すべて表示",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onTimelineClick() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recent Memories Scroll list
            if (recentEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (currentLang == AppLanguage.VI) "Chưa có nhật ký nào gần đây. Ba mẹ hãy ấn vào nút ghi nhật ký bên dưới nhé!" else "最近の日記はありません。下の日記作成ボタンを押して記録してください！",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentEntries.take(4)) { entry ->
                        RecentMemoryCard(entry, currentLang)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Parenting Quote Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (currentLang == AppLanguage.VI) "\"Trẻ em là những chiếc lá non cần được chăm sóc bằng tất cả lòng yêu thương dịu hiền và sự kiên nhẫn vô bờ bến.\"" else "\"子どもたちは、優しい愛と無限の忍耐をもって育てられるべき青葉です。\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentLang == AppLanguage.VI) "- Khuyết danh" else "- 著者不明",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentMemoryCard(entry: DiaryEntry, currentLang: AppLanguage) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(170.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emotion badge
                val moodColor = when (entry.mood) {
                    "Vui vẻ" -> MaterialTheme.colorScheme.primaryContainer
                    "Ngoan ngoãn" -> MaterialTheme.colorScheme.tertiaryContainer
                    "Mệt mỏi" -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
                
                val localizedMood = when (entry.mood) {
                    "Vui vẻ" -> if (currentLang == AppLanguage.VI) "Vui vẻ" else "喜び"
                    "Hòa đồng" -> if (currentLang == AppLanguage.VI) "Hòa đồng" else "社交的"
                    "Mệt mỏi" -> if (currentLang == AppLanguage.VI) "Mệt mỏi" else "お疲れ"
                    "Khóc nhè" -> if (currentLang == AppLanguage.VI) "Khóc nhè" else "泣き虫"
                    "Ngoan ngoãn" -> if (currentLang == AppLanguage.VI) "Ngoan ngoãn" else "お利口"
                    "Bình thường" -> if (currentLang == AppLanguage.VI) "Bình thường" else "普通"
                    else -> entry.mood
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(moodColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(localizedMood, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Date
                Text(entry.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = entry.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Note snippet
            Text(
                text = entry.note,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tags row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                entry.getTagsList().take(2).forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("#$tag", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (entry.isMilestone) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(LanguageManager.getString("milestone"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }
}

