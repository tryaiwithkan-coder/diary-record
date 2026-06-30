package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.DiaryDao
import com.example.data.model.DiaryEntry
import com.example.data.remote.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryRepository(
    private val diaryDao: DiaryDao
) {
    private val geminiService = RetrofitClient.service
    private val moshi: Moshi = RetrofitClient.moshiInstance

    fun getEntriesForBaby(babyId: Int): Flow<List<DiaryEntry>> = diaryDao.getEntriesForBaby(babyId)

    fun getEntryById(id: Int): Flow<DiaryEntry?> = diaryDao.getEntryById(id)

    suspend fun insertEntry(entry: DiaryEntry): Long = diaryDao.insertEntry(entry)

    suspend fun deleteEntry(entry: DiaryEntry) = diaryDao.deleteEntry(entry)

    fun searchEntries(babyId: Int, query: String): Flow<List<DiaryEntry>> = diaryDao.searchEntries(babyId, query)

    suspend fun getEntriesByMonth(babyId: Int, yearMonth: String): List<DiaryEntry> = diaryDao.getEntriesByMonth(babyId, yearMonth)

    /**
     * Sends a base64-encoded image to the Gemini API and parses the returned JSON OCR result.
     */
    suspend fun analyzeImageWithGemini(
        base64Image: String,
        mimeType: String
    ): GeminiOcrResult? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("DiaryRepository", "Gemini API key is not configured or placeholder.")
            return@withContext null
        }

        val prompt = """
            Hãy phân tích hình ảnh này để tạo nhật ký trưởng thành cho bé.
            Nếu ảnh chứa chữ viết tay, bảng tin học tập, sổ liên lạc, bài phát biểu, hoặc giấy khen, hãy dịch và OCR chính xác chữ trong đó.
            Nếu ảnh là hoạt động vui chơi bình thường, hãy tả lại một cách ấm áp và tự nhiên.
            
            Hãy điền các thông tin tìm được vào JSON schema sau:
            {
              "title": "Tiêu đề ngắn gọn của sự kiện hoặc hoạt động của bé (ví dụ: Bé vẽ tranh, Buổi học ngoan ngoãn)",
              "date": "Ngày diễn ra sự kiện dạng YYYY-MM-DD. Nếu không tìm thấy, hãy dùng ngày hiện tại (ví dụ: 2026-06-29)",
              "mood": "Cảm xúc hoặc tâm trạng của bé, chọn một trong các từ: Vui vẻ, Hòa đồng, Mệt mỏi, Khóc nhè, Ngoan ngoãn, Bình thường",
              "teacherFeedback": "Nội dung nhận xét của cô giáo hoặc tóm tắt văn bản ghi chép. Có thể rỗng nếu ảnh không có chữ",
              "activities": "Các hoạt động, kỹ năng cụ thể bé thực hiện trong ảnh",
              "tags": ["Mảng phát triển, tối đa 3 thẻ, ví dụ: Trường học, Sáng tạo, Thể chất, Giao tiếp, Kỹ năng, Gia đình"],
              "isMilestone": true hoặc false (là mốc phát triển quan trọng như biết đi, biết nói, ngày đầu đi học, đạt điểm 10...),
              "milestoneTitle": "Tên mốc phát triển nếu có"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(text = "Bạn là chuyên gia giáo dục mầm non và tâm lý trẻ em Việt Nam. Bạn OCR và phân tích ảnh hoạt động của bé, điền vào JSON thô theo đúng cấu trúc yêu cầu. Không bọc JSON trong markdown ```json.")
                )
            )
        )

        try {
            val response = geminiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                Log.d("DiaryRepository", "Gemini raw response: $jsonText")
                // Clean markdown packaging if any exists
                val cleanedJson = jsonText.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val adapter = moshi.adapter(GeminiOcrResult::class.java)
                return@withContext adapter.fromJson(cleanedJson)
            } else {
                Log.e("DiaryRepository", "Gemini response text is null.")
                null
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Failed to analyze image with Gemini", e)
            null
        }
    }

    /**
     * Synthesizes all logs of a given month into an emotional developmental recap.
     */
    suspend fun generateMonthlyRecap(
        babyName: String,
        monthYear: String,
        entries: List<DiaryEntry>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Chưa cấu hình API Key trong mục Cài đặt hoặc Secrets. Hãy nhập GEMINI_API_KEY để AI tạo báo cáo trưởng thành hàng tháng cho bé nhé!"
        }

        if (entries.isEmpty()) {
            return@withContext "Không tìm thấy nhật ký nào trong tháng $monthYear của bé $babyName để viết recap. Ba mẹ hãy ghi lại nhật ký hàng ngày để AI tổng hợp nhé!"
        }

        val diarySummary = entries.joinToString("\n") { entry ->
            "- [${entry.date}] ${entry.title}: ${entry.note}. Cảm xúc: ${entry.mood}. Tags: ${entry.tags}. Mốc quan trọng: ${if (entry.isMilestone) entry.milestoneTitle ?: "Có" else "Không"}"
        }

        val prompt = """
            Dưới đây là danh sách toàn bộ các nhật ký và hoạt động của bé $babyName trong tháng $monthYear:
            $diarySummary
            
            Dựa trên thông tin này, hãy viết một bài tổng kết sự trưởng thành của bé trong tháng này thật hay, ấm áp và truyền cảm hứng.
            Bố cục gồm:
            1. 🎉 **Tổng quan tháng**: Lời nhắn gửi đáng yêu của AI tới ba mẹ về một tháng tràn ngập niềm vui.
            2. 🌱 **Thể chất & Sức khỏe**: Nhận xét thể chất chung (vận động, sinh hoạt...).
            3. 🗣️ **Giao tiếp & Kỹ năng xã hội**: Đánh giá sự tiến bộ của bé (hòa đồng, tự lập, nói câu dài, phát biểu...).
            4. 🏆 **Dấu mốc đáng nhớ nhất**: Nêu bật các Milestone nổi bật trong tháng của bé.
            5. ❤️ **Lời khuyên ấm áp dành cho Ba Mẹ**: Những hoạt động, lời động viên hoặc trò chơi ba mẹ nên áp dụng trong tháng tới để kích thích sự phát triển của bé.

            Hãy dùng ngôn từ dịu dàng, tràn đầy yêu thương, đậm chất thơ và kể chuyện (emotional storytelling). Hãy sử dụng Markdown đẹp đẽ với nhiều icon ngộ nghĩnh để ba mẹ thích thú khi đọc và dễ chia sẻ lên mạng xã hội.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7f
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(text = "Bạn là trợ lý AI thông minh, đóng vai trò một giáo viên mầm non ưu tú và người bạn tri kỷ của gia đình. Bạn viết báo cáo phát triển của bé dưới dạng một bức thư tình yêu gửi ba mẹ, ngập tràn sự khích lệ và ngọt ngào.")
                )
            )
        )

        try {
            val response = geminiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Không thể tạo báo cáo. Phản hồi từ AI trống rỗng."
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Failed to generate monthly recap", e)
            "Đã xảy ra lỗi khi tạo báo cáo từ AI: ${e.message}. Ba mẹ vui lòng kiểm tra kết nối internet và thử lại sau."
        }
    }
}
