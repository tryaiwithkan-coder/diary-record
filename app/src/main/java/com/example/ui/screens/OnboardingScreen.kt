package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
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
import com.example.ui.theme.PeachPink
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onProfileCreated: (name: String, birthDate: String, gender: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("2024") }
    var birthMonth by remember { mutableStateOf("05") }
    var birthDay by remember { mutableStateOf("15") }
    var gender by remember { mutableStateOf("Nam") } // Default "Nam"
    var showError by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Heartwarming Hero Icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .testTag("baby_logo_container"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = "Baby Icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Emotional Greeting
            Text(
                text = "Chào mừng Ba Mẹ!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hãy cùng lưu giữ từng khoảnh khắc kỳ diệu trên hành trình trưởng thành khôn lớn của cục cưng nhé.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Baby Profile Card Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_form_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Tạo Hồ Sơ Cho Bé",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Baby Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; showError = false },
                        label = { Text("Tên biệt danh của bé") },
                        placeholder = { Text("Ví dụ: Mochi, Khoai Tây, Bơ...") },
                        leadingIcon = { Icon(Icons.Filled.Face, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("baby_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Gender Selection
                    Column {
                        Text(
                            text = "Giới tính của bé",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("Nam", "Nữ").forEach { option ->
                                val selected = gender == option
                                FilterChip(
                                    selected = selected,
                                    onClick = { gender = option },
                                    label = { 
                                        Text(
                                            option, 
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        ) 
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (option == "Nam") MaterialTheme.colorScheme.primaryContainer else PeachPink,
                                        selectedLabelColor = MaterialTheme.colorScheme.onBackground
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Birthday Form (Custom beautiful date select fields for 100% stability)
                    Column {
                        Text(
                            text = "Ngày sinh nhật của bé",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Day
                            OutlinedTextField(
                                value = birthDay,
                                onValueChange = { if (it.length <= 2) birthDay = it },
                                label = { Text("Ngày") },
                                placeholder = { Text("DD") },
                                modifier = Modifier.weight(1f).testTag("birth_day_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            // Month
                            OutlinedTextField(
                                value = birthMonth,
                                onValueChange = { if (it.length <= 2) birthMonth = it },
                                label = { Text("Tháng") },
                                placeholder = { Text("MM") },
                                modifier = Modifier.weight(1f).testTag("birth_month_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            // Year
                            OutlinedTextField(
                                value = birthYear,
                                onValueChange = { if (it.length <= 4) birthYear = it },
                                label = { Text("Năm") },
                                placeholder = { Text("YYYY") },
                                modifier = Modifier.weight(1.5f).testTag("birth_year_input"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    if (showError) {
                        Text(
                            text = "Ba mẹ vui lòng nhập đầy đủ tên và thông tin hợp lệ nhé!",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action button
            Button(
                onClick = {
                    val d = birthDay.toIntOrNull() ?: 15
                    val m = birthMonth.toIntOrNull() ?: 5
                    val y = birthYear.toIntOrNull() ?: 2024
                    val dateFormatted = String.format("%04d-%02d-%02d", y, m, d)

                    if (name.trim().isNotEmpty() && d in 1..31 && m in 1..12 && y in 2000..2027) {
                        onProfileCreated(name.trim(), dateFormatted, gender)
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("create_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Bắt đầu hành trình", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
