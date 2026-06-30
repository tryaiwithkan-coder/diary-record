package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BabyProfile
import com.example.data.model.GrowthRecord
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LanguageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    baby: BabyProfile,
    growthRecords: List<GrowthRecord>,
    onAddGrowthRecord: (weight: Double, height: Double, headCircumference: Double?, date: String) -> Unit,
    onDeleteGrowthRecord: (GrowthRecord) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentLang = LanguageManager.currentLanguage.value

    // Form inputs for new record
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var headInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    // Tab state inside growth (0: Chiều cao, 1: Cân nặng)
    var chartTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateInput = sdf.format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LanguageManager.getString("profile_growth_stats"), fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Baby Info Card
            Card(
                modifier = Modifier.fillMaxWidth().testTag("profile_info_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Face,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(baby.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            val genderLabel = if (currentLang == AppLanguage.VI) {
                                "Giới tính: ${baby.gender}"
                            } else {
                                "性別: ${if (baby.gender == "Bé Trai") "男の子" else "女の子"}"
                            }
                            val bdateLabel = if (currentLang == AppLanguage.VI) {
                                "Ngày sinh: ${baby.birthDate}"
                            } else {
                                "生年月日: ${baby.birthDate}"
                            }
                            Text(genderLabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
                            Text(bdateLabel, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Visual Growth Curves
            Text(if (currentLang == AppLanguage.VI) "Biểu Đồ Phát Triển" else "成長グラフ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Card(
                modifier = Modifier.fillMaxWidth().testTag("growth_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Chart Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chartTabs = if (currentLang == AppLanguage.VI) {
                            listOf("Đồ thị Chiều cao", "Đồ thị Cân nặng")
                        } else {
                            listOf("身長グラフ", "体重グラフ")
                        }
                        chartTabs.forEachIndexed { idx, label ->
                            val selected = chartTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
                                    .clickable { chartTab = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw Chart on Canvas
                    GrowthChart(records = growthRecords, showHeight = chartTab == 0, currentLang = currentLang)
                }
            }

            // Add growth metric log form
            Text(LanguageManager.getString("update_new_stats"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Card(
                modifier = Modifier.fillMaxWidth().testTag("growth_record_form"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text(LanguageManager.getString("height_cm")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "ví dụ: 92.5" else "例: 92.5") },
                            modifier = Modifier.weight(1f).testTag("weight_height_input_height"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text(LanguageManager.getString("weight_kg")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "ví dụ: 13.5" else "例: 13.5") },
                            modifier = Modifier.weight(1f).testTag("weight_height_input_weight"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = headInput,
                            onValueChange = { headInput = it },
                            label = { Text(LanguageManager.getString("head_circum_cm")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "ví dụ: 48.0" else "例: 48.0") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text(LanguageManager.getString("measure_date")) },
                            modifier = Modifier.weight(1f).testTag("weight_height_input_date"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    if (showError) {
                        Text(
                            if (currentLang == AppLanguage.VI) "Ba mẹ vui lòng nhập chiều cao và cân nặng hợp lệ nhé!" else "有効な身長と体重を入力してください！",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = {
                            val h = heightInput.toDoubleOrNull()
                            val w = weightInput.toDoubleOrNull()
                            val hc = headInput.toDoubleOrNull()
                            if (h != null && w != null && dateInput.isNotBlank()) {
                                onAddGrowthRecord(w, h, hc, dateInput.trim())
                                heightInput = ""
                                weightInput = ""
                                headInput = ""
                                showError = false
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("add_growth_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(LanguageManager.getString("add_stats"), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Historical List
            Text(LanguageManager.getString("growth_history"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Card(
                modifier = Modifier.fillMaxWidth().testTag("growth_history_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (growthRecords.isEmpty()) {
                        Text(
                            LanguageManager.getString("no_stats_yet"),
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        // Display sorted records list in reverse order (most recent first)
                        growthRecords.reversed().forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text(record.date, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        val subLabel = if (currentLang == AppLanguage.VI) {
                                            "Cao: ${record.heightCm} cm | Nặng: ${record.weightKg} kg" +
                                                    (record.headCircumferenceCm?.let { " | Vòng đầu: $it cm" } ?: "")
                                        } else {
                                            "身長: ${record.heightCm} cm | 体重: ${record.weightKg} kg" +
                                                    (record.headCircumferenceCm?.let { " | 頭囲: $it cm" } ?: "")
                                        }
                                        Text(
                                            text = subLabel,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteGrowthRecord(record) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = LanguageManager.getString("delete_stats"), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom line chart drawing canvas
 */
@Composable
fun GrowthChart(records: List<GrowthRecord>, showHeight: Boolean, currentLang: AppLanguage) {
    if (records.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (currentLang == AppLanguage.VI) "Chưa có chỉ số đo lường để hiển thị." else "表示する測定値がありません。",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
        return
    }

    val points = records.map { if (showHeight) it.heightCm else it.weightKg }
    val maxVal = (points.maxOrNull() ?: 100.0) + (if (showHeight) 5.0 else 2.0)
    val minVal = maxOf(0.0, (points.minOrNull() ?: 0.0) - (if (showHeight) 5.0 else 2.0))
    val range = maxVal - minVal

    val primaryColor = if (showHeight) Color(0xFF8ECAE6) else Color(0xFFFFB5A7)
    val secondaryColor = if (showHeight) Color(0xFF219EBC) else Color(0xFFE07A5F)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
    ) {
        val width = size.width
        val height = size.height

        val stepX = if (points.size > 1) width / (points.size - 1) else width

        // Draw dotted lines/grids
        for (i in 1..3) {
            val gridY = height * i / 4
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(0f, gridY),
                end = Offset(width, gridY),
                strokeWidth = 1f
            )
        }

        // Draw bottom boundary
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )

        // Map coordinates
        val coordinates = points.mapIndexed { idx, value ->
            val ratio = if (range > 0) (value - minVal) / range else 0.5
            val x = idx * stepX
            val y = height - (ratio * height).toFloat()
            Offset(x, y)
        }

        // Draw line connections
        if (coordinates.size > 1) {
            for (i in 0 until coordinates.size - 1) {
                drawLine(
                    color = primaryColor,
                    start = coordinates[i],
                    end = coordinates[i + 1],
                    strokeWidth = 4f
                )
            }
        }

        // Draw points with text badges
        coordinates.forEachIndexed { idx, pt ->
            drawCircle(
                color = secondaryColor,
                radius = 6f,
                center = pt
            )

            // Draw a subtle outer halo
            drawCircle(
                color = secondaryColor.copy(alpha = 0.2f),
                radius = 12f,
                center = pt
            )
        }
    }
}
