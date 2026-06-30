package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.remote.GeminiOcrResult
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LanguageManager
import com.example.ui.theme.PeachPink
import com.example.ui.viewmodel.BulkOcrItem
import com.example.ui.viewmodel.DiaryViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    viewModel: DiaryViewModel,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentLang = LanguageManager.currentLanguage.value
    val context = LocalContext.current

    val ocrLoading by viewModel.ocrLoading.collectAsStateWithLifecycle()
    val ocrResult by viewModel.ocrResult.collectAsStateWithLifecycle()
    val bulkItems by viewModel.bulkItems.collectAsStateWithLifecycle()
    val bulkLoading by viewModel.bulkLoading.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Single, 1: Bulk

    // Single Form states
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Vui vẻ") }
    var tags by remember { mutableStateOf("") }
    var teacherFeedback by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf("") }
    var isMilestone by remember { mutableStateOf(false) }
    var milestoneTitle by remember { mutableStateOf("") }

    // Initialize date string with current date YYYY-MM-DD
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateString = sdf.format(Date())
    }

    // Auto populate fields if OCR results are returned from Gemini (Single Mode)
    LaunchedEffect(ocrResult) {
        val result = ocrResult
        if (result != null) {
            title = result.title
            dateString = result.date
            mood = result.mood
            tags = result.tags.joinToString(", ")
            teacherFeedback = result.teacherFeedback ?: ""
            activities = result.activities ?: ""
            isMilestone = result.isMilestone
            milestoneTitle = result.milestoneTitle ?: ""
        }
    }

    // Programmatically render text to bitmap to test OCR genuinely!
    fun renderScenarioToBitmap(scenario: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { isAntiAlias = true }

        when (scenario) {
            1 -> { // School comment note
                canvas.drawColor(android.graphics.Color.parseColor("#FFFDF0")) // notebook background
                paint.color = android.graphics.Color.parseColor("#2563EB") // blue lines
                for (i in 1..8) {
                    canvas.drawLine(0f, i * 50f, 600f, i * 50f, paint)
                }
                paint.color = android.graphics.Color.parseColor("#EF4444") // margin line
                canvas.drawLine(100f, 0f, 100f, 450f, paint)

                paint.color = android.graphics.Color.parseColor("#1E3A8A") // dark ink
                paint.textSize = 24f
                canvas.drawText("SỔ LIÊN LẠC MẦM NON - LỚP THỎ NGỌC", 120f, 45f, paint)
                paint.textSize = 20f
                canvas.drawText("Ngày: 2026-06-15", 120f, 95f, paint)
                canvas.drawText("Nhận xét từ cô giáo về bé Mochi hôm nay:", 120f, 145f, paint)
                paint.color = android.graphics.Color.parseColor("#B91C1C") // red handwriting ink
                canvas.drawText("- Con hôm nay rất ngoan và tự giác xúc ăn.", 120f, 195f, paint)
                canvas.drawText("- Trong lớp hăng hái giơ tay phát biểu hát múa.", 120f, 245f, paint)
                canvas.drawText("- Con rất hòa đồng chia sẻ đồ chơi cùng các bạn.", 120f, 295f, paint)
                paint.color = android.graphics.Color.parseColor("#1E3A8A")
                canvas.drawText("Cô chúc con cuối tuần vui vẻ nhé!", 120f, 345f, paint)
            }
            2 -> { // Achievement award
                canvas.drawColor(android.graphics.Color.parseColor("#FEF3C7")) // light gold certificate
                paint.color = android.graphics.Color.parseColor("#D97706") // border outline
                paint.strokeWidth = 10f
                paint.style = Paint.Style.STROKE
                canvas.drawRect(20f, 20f, 580f, 430f, paint)

                paint.style = Paint.Style.FILL
                paint.color = android.graphics.Color.parseColor("#78350F")
                paint.textSize = 28f
                paint.isFakeBoldText = true
                canvas.drawText("GIẤY KHEN DANH DỰ", 160f, 80f, paint)

                paint.textSize = 20f
                paint.isFakeBoldText = false
                canvas.drawText("Trường Mầm Non Tuổi Thơ Đẹp kính tặng bé:", 110f, 150f, paint)
                
                paint.textSize = 24f
                paint.color = android.graphics.Color.parseColor("#DC2626")
                paint.isFakeBoldText = true
                canvas.drawText("BÉ MOCHI (NGUYỄN MINH AN)", 130f, 200f, paint)

                paint.textSize = 18f
                paint.color = android.graphics.Color.parseColor("#78350F")
                paint.isFakeBoldText = false
                canvas.drawText("Đạt danh hiệu: Bé Ngoan Xuất Sắc Tuần lễ tháng 6", 110f, 260f, paint)
                canvas.drawText("Thành tích: Bé đã rất dũng cảm tự lập giúp đỡ bạn bè", 110f, 310f, paint)
                canvas.drawText("Ngày ký: 2026-06-18", 110f, 360f, paint)
            }
            else -> { // Zoo trip story
                canvas.drawColor(android.graphics.Color.parseColor("#ECFDF5")) // light emerald forest background
                paint.color = android.graphics.Color.parseColor("#059669")
                paint.textSize = 28f
                paint.isFakeBoldText = true
                canvas.drawText("CHUYẾN ĐI SỞ THÚ CUỐI TUẦN", 110f, 80f, paint)

                paint.textSize = 18f
                paint.isFakeBoldText = false
                paint.color = android.graphics.Color.parseColor("#065F46")
                canvas.drawText("Ảnh kỷ niệm: Bé Mochi dã ngoại tại Thảo Cầm Viên", 80f, 140f, paint)
                canvas.drawText("- Con thích mê khi tận mắt nhìn chú hươu cao cổ", 80f, 200f, paint)
                canvas.drawText("- Con bập bẹ gọi 'Hươu' rất đáng yêu và chỉ tay reo vui", 80f, 260f, paint)
                canvas.drawText("- Cả nhà đã có buổi picnic thật nhiều tiếng cười", 80f, 320f, paint)
                canvas.drawText("Thời gian: 2026-06-21", 80f, 380f, paint)
            }
        }
        return bitmap
    }

    // Launchers for image source
    var selectedScenario by remember { mutableStateOf(0) }
    var activeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val singleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    selectedScenario = 0
                    activeBitmap = bitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val multipleGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newItems = uris.map { uri ->
                BulkOcrItem(uri = uri, label = uri.lastPathSegment ?: "Image")
            }
            viewModel.setBulkItems(newItems)
        }
    }

    LaunchedEffect(selectedScenario) {
        if (selectedScenario > 0) {
            val bmp = renderScenarioToBitmap(selectedScenario)
            activeBitmap = bmp
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentLang == AppLanguage.VI) "Nhật Ký Của Bé" else "赤ちゃんの成長日記", fontWeight = FontWeight.Bold) },
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
            // Segmented/Tab Row for Single vs. Bulk Mode
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text(LanguageManager.getString("bulk_add_tab_single"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    modifier = Modifier.testTag("tab_single")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text(LanguageManager.getString("bulk_add_tab_multiple"), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Filled.List, contentDescription = null) },
                    modifier = Modifier.testTag("tab_bulk")
                )
            }

            if (activeTab == 0) {
                // SINGLE ENTRY MODE
                Text(
                    text = LanguageManager.getString("ocr_section"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (activeBitmap == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { singleGalleryLauncher.launch("image/*") }
                            .testTag("upload_image_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Upload",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = LanguageManager.getString("device_upload"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = LanguageManager.getString("upload_desc"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("scenario_selector_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = LanguageManager.getString("sample_desc"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    Triple(1, LanguageManager.getString("scenario_contact"), LanguageManager.getString("scenario_contact_sub")),
                                    Triple(2, LanguageManager.getString("scenario_cert"), LanguageManager.getString("scenario_cert_sub")),
                                    Triple(3, LanguageManager.getString("scenario_zoo"), LanguageManager.getString("scenario_zoo_sub"))
                                ).forEach { (id, label, sub) ->
                                    val selected = selectedScenario == id
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
                                            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable { selectedScenario = id }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                            Text(sub, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                activeBitmap?.let { bmp ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Scenario image",
                            modifier = Modifier.fillMaxSize()
                        )

                        IconButton(
                            onClick = { 
                                selectedScenario = 0
                                activeBitmap = null
                                viewModel.clearOcrResult() 
                            },
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Reset", tint = Color.Red)
                        }
                    }

                    Button(
                        onClick = {
                            val stream = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                            val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                            viewModel.analyzeImage(base64, "image/jpeg")
                        },
                        modifier = Modifier.fillMaxWidth().testTag("ai_ocr_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !ocrLoading
                    ) {
                        if (ocrLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(LanguageManager.getString("analyzing_ai"))
                        } else {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text(LanguageManager.getString("analyze_ai"), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = LanguageManager.getString("diary_details_section"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = dateString,
                            onValueChange = { dateString = it },
                            label = { Text(LanguageManager.getString("diary_date")) },
                            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("input_date"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(LanguageManager.getString("diary_title")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "Nhập tiêu đề hoặc để AI tự điền..." else "タイトルを入力するか、AIが自動入力するまでお待ちください...") },
                            modifier = Modifier.fillMaxWidth().testTag("input_title"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text(LanguageManager.getString("diary_content")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "Ghi lại chi tiết cảm xúc, câu nói ngộ nghĩnh hoặc hoạt động hôm nay của con..." else "今日の赤ちゃんの気分、可愛い言葉、または活動の詳細を記録してください...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .testTag("input_note"),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 5
                        )

                        Column {
                            Text(
                                text = LanguageManager.getString("baby_mood"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Vui vẻ", "Hòa đồng", "Ngoan ngoãn", "Mệt mỏi", "Khóc nhè").forEach { option ->
                                    val selected = mood == option
                                    val localizedOption = when (option) {
                                        "Vui vẻ" -> if (currentLang == AppLanguage.VI) "Vui vẻ" else "喜び"
                                        "Hòa đồng" -> if (currentLang == AppLanguage.VI) "Hòa đồng" else "社交的"
                                        "Ngoan ngoãn" -> if (currentLang == AppLanguage.VI) "Ngoan ngoãn" else "お利口"
                                        "Mệt mỏi" -> if (currentLang == AppLanguage.VI) "Mệt mỏi" else "お疲れ"
                                        "Khóc nhè" -> if (currentLang == AppLanguage.VI) "Khóc nhè" else "泣き虫"
                                        else -> option
                                    }
                                    FilterChip(
                                        selected = selected,
                                        onClick = { mood = option },
                                        label = { Text(localizedOption, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                        ),
                                        modifier = Modifier.testTag("mood_chip_$option")
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(LanguageManager.getString("is_important_milestone"), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Switch(
                                checked = isMilestone,
                                onCheckedChange = { isMilestone = it },
                                modifier = Modifier.testTag("milestone_switch")
                            )
                        }

                        if (isMilestone) {
                            OutlinedTextField(
                                value = milestoneTitle,
                                onValueChange = { milestoneTitle = it },
                                label = { Text(LanguageManager.getString("milestone_name_opt")) },
                                placeholder = { Text(if (currentLang == AppLanguage.VI) "Ví dụ: Biết đứng một mình, ngày đầu đi học, tự xúc ăn..." else "例: 一人で立つ、登園初日、自分で食べる...") },
                                modifier = Modifier.fillMaxWidth().testTag("input_milestone_title"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        OutlinedTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            label = { Text(LanguageManager.getString("event_tags")) },
                            placeholder = { Text(if (currentLang == AppLanguage.VI) "Trường học, Giao tiếp, Sức khỏe, Thể chất..." else "学校, コミュニケーション, 健康, 体育...") },
                            modifier = Modifier.fillMaxWidth().testTag("input_tags"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = teacherFeedback.isNotEmpty() || activities.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = LanguageManager.getString("ocr_result_title"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (teacherFeedback.isNotEmpty()) {
                                    OutlinedTextField(
                                        value = teacherFeedback,
                                        onValueChange = { teacherFeedback = it },
                                        label = { Text(LanguageManager.getString("teacher_feedback_opt")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                                if (activities.isNotEmpty()) {
                                    OutlinedTextField(
                                        value = activities,
                                        onValueChange = { activities = it },
                                        label = { Text(LanguageManager.getString("specific_activities")) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val finalTitle = title.trim().ifEmpty {
                            if (currentLang == AppLanguage.VI) "Kỷ niệm ngày $dateString" else "$dateString の思い出"
                        }
                        val finalNote = note.trim().ifEmpty {
                            if (currentLang == AppLanguage.VI) "Hành trình lớn khôn của bé ngày hôm nay." else "今日の赤ちゃんの成長の旅。"
                        }
                        viewModel.addEntry(
                            title = finalTitle,
                            note = finalNote,
                            mood = mood,
                            tags = tags.trim(),
                            teacherFeedback = teacherFeedback.trim().ifEmpty { null },
                            activities = activities.trim().ifEmpty { null },
                            isMilestone = isMilestone,
                            milestoneTitle = milestoneTitle.trim().ifEmpty { null },
                            date = dateString.trim()
                        )
                        viewModel.clearOcrResult()
                        onBackClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_entry_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text(LanguageManager.getString("save_diary"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

            } else {
                // BULK MULTI-IMAGE UPLOAD & CONCURRENT AI PROCESSING MODE
                Text(
                    text = if (currentLang == AppLanguage.VI) "Tải lên hàng loạt & AI phân tích song song" else "一括アップロードとAI並列分析",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (bulkItems.isEmpty()) {
                    // Empty state card to trigger gallery
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { multipleGalleryLauncher.launch("image/*") }
                            .testTag("bulk_upload_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.List,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = LanguageManager.getString("bulk_select_images"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = LanguageManager.getString("bulk_upload_desc"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }

                    // Demo simulation triggers for developers/users (extremely elegant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = LanguageManager.getString("bulk_scenario_help"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Button(
                        onClick = {
                            val list = listOf(
                                BulkOcrItem(
                                    id = "scenario_1",
                                    bitmap = renderScenarioToBitmap(1),
                                    label = LanguageManager.getString("scenario_contact") + " (Mẫu 1)"
                                ),
                                BulkOcrItem(
                                    id = "scenario_2",
                                    bitmap = renderScenarioToBitmap(2),
                                    label = LanguageManager.getString("scenario_cert") + " (Mẫu 2)"
                                ),
                                BulkOcrItem(
                                    id = "scenario_3",
                                    bitmap = renderScenarioToBitmap(3),
                                    label = LanguageManager.getString("scenario_zoo") + " (Mẫu 3)"
                                )
                            )
                            viewModel.setBulkItems(list)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("bulk_scenario_all_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(LanguageManager.getString("bulk_scenario_all"), fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Bulk Items are loaded
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLang == AppLanguage.VI) "${bulkItems.size} ảnh đã chọn" else "${bulkItems.size} 枚の画像を選択済み",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = { viewModel.clearBulkItems() }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (currentLang == AppLanguage.VI) "Xóa tất cả" else "すべてクリア", color = Color.Red)
                            }
                        }
                    }

                    // Concurrent Trigger button
                    Button(
                        onClick = { viewModel.analyzeBulkItems(context) },
                        modifier = Modifier.fillMaxWidth().testTag("bulk_analyze_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !bulkLoading
                    ) {
                        if (bulkLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(LanguageManager.getString("bulk_analyzing"))
                        } else {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text(LanguageManager.getString("bulk_analyze_all"), fontWeight = FontWeight.Bold)
                        }
                    }

                    // Bulk list cards
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        bulkItems.forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Image preview
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.background)
                                        ) {
                                            if (item.bitmap != null) {
                                                Image(
                                                    bitmap = item.bitmap.asImageBitmap(),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else if (item.uri != null) {
                                                val localContext = LocalContext.current
                                                val uriBmp = remember(item.uri) {
                                                    try {
                                                        val ins = localContext.contentResolver.openInputStream(item.uri)
                                                        val b = BitmapFactory.decodeStream(ins)
                                                        ins?.close()
                                                        b
                                                    } catch(e: Exception) { null }
                                                }
                                                if (uriBmp != null) {
                                                    Image(
                                                        bitmap = uriBmp.asImageBitmap(),
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                } else {
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Filled.List, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                                                    }
                                                }
                                            }
                                        }

                                        // Status details
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                            Spacer(modifier = Modifier.height(6.dp))

                                            if (item.isAnalyzing) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(LanguageManager.getString("analyzing"), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                                }
                                            } else if (item.error != null) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(item.error, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                                }
                                            } else if (item.result != null) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF0F9D58), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(if (currentLang == AppLanguage.VI) "Phân tích xong" else "分析完了", fontSize = 12.sp, color = Color(0xFF0F9D58), fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(LanguageManager.getString("waiting"), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                                                }
                                            }
                                        }

                                        // Delete single row item
                                        IconButton(onClick = { viewModel.removeBulkItem(item.id) }) {
                                            Icon(Icons.Filled.Close, contentDescription = "Delete item", tint = MaterialTheme.colorScheme.outline)
                                        }
                                    }

                                    // Display editable OCR result form inside card
                                    item.result?.let { ocr ->
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        var itemTitle by remember(ocr) { mutableStateOf(ocr.title) }
                                        var itemNote by remember(ocr) { mutableStateOf(ocr.teacherFeedback ?: ocr.activities ?: "") }

                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedTextField(
                                                value = itemTitle,
                                                onValueChange = { itemTitle = it },
                                                label = { Text(LanguageManager.getString("diary_title"), fontSize = 11.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                            )
                                            OutlinedTextField(
                                                value = itemNote,
                                                onValueChange = { itemNote = it },
                                                label = { Text(LanguageManager.getString("diary_content"), fontSize = 11.sp) },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    SuggestionChip(
                                                        onClick = {},
                                                        label = { Text("📅 ${ocr.date}", fontSize = 10.sp) }
                                                    )
                                                    SuggestionChip(
                                                        onClick = {},
                                                        label = { Text("😊 ${ocr.mood}", fontSize = 10.sp) }
                                                    )
                                                }

                                                if (item.isSaved) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF0F9D58), modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(LanguageManager.getString("saved"), color = Color(0xFF0F9D58), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    }
                                                } else {
                                                    Button(
                                                        onClick = { viewModel.saveBulkItemToDiary(item.id, itemTitle, itemNote) },
                                                        shape = RoundedCornerShape(8.dp),
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                        modifier = Modifier.height(32.dp)
                                                    ) {
                                                        Text(LanguageManager.getString("save_item"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Save all button
                    val unsavedCount = bulkItems.count { !it.isSaved && it.result != null }
                    if (unsavedCount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                viewModel.saveAllBulkItemsToDiary()
                                onBackClick()
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("bulk_save_all_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = LanguageManager.getString("bulk_save_all") + " ($unsavedCount)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
