package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SearchScreen(
    query: String,
    searchResults: List<DiaryEntry>,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val currentLang = LanguageManager.currentLanguage.value
    val searchSuggestions = if (currentLang == AppLanguage.VI) {
        listOf("Trường học", "Vui vẻ", "Tự lập", "Sức khỏe", "Mốc son", "Gia đình")
    } else {
        listOf("学校", "喜び", "自立", "健康", "節目", "家族")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LanguageManager.getString("smart_search"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = if (currentLang == AppLanguage.VI) "Quay lại" else "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input Field
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(LanguageManager.getString("search_placeholder")) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }, modifier = Modifier.testTag("clear_search_button")) {
                            Icon(Icons.Filled.Clear, contentDescription = if (currentLang == AppLanguage.VI) "Xóa chữ" else "クリア")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_bar_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Suggestions title
            Text(
                text = LanguageManager.getString("quick_suggestions"),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestion chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                searchSuggestions.forEach { label ->
                    AssistChip(
                        onClick = { onQueryChange(label) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("suggestion_chip_$label")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results lists
            if (query.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLang == AppLanguage.VI) "Tìm kiếm kỷ niệm ngọt ngào của con" else "赤ちゃんの愛しい思い出を検索",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (currentLang == AppLanguage.VI) {
                                "Ví dụ ba mẹ gõ: \"trường học\", \"vui vẻ\", \"bé ngoan\", \"tự xúc ăn\"..."
                            } else {
                                "例：\"学校\"、\"喜び\"、\"お利口\"、\"自分で食べる\"..."
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = if (currentLang == AppLanguage.VI) {
                                "Không tìm thấy kỷ niệm nào phù hợp cho từ khóa \"$query\""
                            } else {
                                "キーワード \"$query\" に一致する思い出は見つかりませんでした"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Text(
                    text = if (currentLang == AppLanguage.VI) {
                        "Tìm thấy ${searchResults.size} kết quả phù hợp:"
                    } else {
                        "${searchResults.size} 件の一致する結果が見つかりました:"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("search_results_list")
                ) {
                    items(searchResults, key = { it.id }) { entry ->
                        // Standard timeline style card for search simplicity and beauty
                        TimelineItemRow(
                            entry = entry,
                            currentLang = currentLang,
                            onDelete = {} // Simple read-only inside search for consistency
                        )
                    }
                }
            }
        }
    }
}
