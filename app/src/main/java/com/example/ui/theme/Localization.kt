package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf

enum class AppLanguage(val code: String, val label: String, val flag: String) {
    VI("vi", "Tiếng Việt", "🇻🇳"),
    JA("ja", "日本語", "🇯🇵")
}

object LanguageManager {
    val currentLanguage = mutableStateOf(AppLanguage.VI)

    fun toggleLanguage() {
        currentLanguage.value = if (currentLanguage.value == AppLanguage.VI) AppLanguage.JA else AppLanguage.VI
    }
    
    fun setLanguage(lang: AppLanguage) {
        currentLanguage.value = lang
    }

    fun getString(key: String): String {
        val strings = translations[key] ?: return key
        return if (currentLanguage.value == AppLanguage.VI) strings.first else strings.second
    }
}

// Translations map: key -> Pair(Vietnamese, Japanese)
val translations = mapOf(
    "app_title" to Pair("Nhật ký của bé", "赤ちゃんの成長日記"),
    // Onboarding
    "setup_profile" to Pair("Thiết Lập Hồ Sơ Bé", "プロフィールの設定"),
    "setup_desc" to Pair("Hãy điền thông tin của bé yêu để bắt đầu lưu trữ hành trình khôn lớn.", "赤ちゃんの成長を記録するために、プロフィールの情報を入力してください。"),
    "baby_name" to Pair("Tên của bé", "赤ちゃんの名前"),
    "baby_bdate" to Pair("Ngày sinh của bé (YYYY-MM-DD)", "生年月日 (YYYY-MM-DD)"),
    "gender" to Pair("Giới tính", "性別"),
    "boy" to Pair("Bé Trai", "男の子"),
    "girl" to Pair("Bé Gái", "女の子"),
    "create_profile" to Pair("Tạo Hồ Sơ", "プロフィール作成"),
    // Home
    "baby" to Pair("Bé", "赤ちゃん"),
    "age" to Pair("Tuổi", "年齢"),
    "months" to Pair("tháng tuổi", "ヶ月"),
    "height" to Pair("Chiều cao", "身長"),
    "weight" to Pair("Cân nặng", "体重"),
    "not_updated" to Pair("Chưa cập nhật", "未更新"),
    "timeline" to Pair("Dòng thời gian", "タイムライン"),
    "write_diary" to Pair("Ghi nhật ký", "日記を書く"),
    "search" to Pair("Tìm kiếm", "検索"),
    "profile_stats" to Pair("Hồ sơ & Chỉ số", "プロフィールと指数"),
    "ai_recap" to Pair("AI Tổng Kết", "AIまとめ"),
    "milestone" to Pair("Mốc son", "マイルストーン"),
    "latest_growth_stats" to Pair("Chỉ Số Phát Triển Gần Nhất", "最新の成長指標"),
    "recent_diaries" to Pair("Nhật ký gần đây", "最近の日記"),
    "growth_history" to Pair("Lịch Sử Đo Chỉ Số", "測定指標の履歴"),
    // Add Entry
    "ocr_section" to Pair("1. Hình ảnh & Trích Xuất AI (Mục OCR)", "1. 画像とAI抽出 (OCR)"),
    "device_upload" to Pair("Tự tải ảnh từ thiết bị của bạn", "デバイスから画像をアップロード"),
    "upload_desc" to Pair("Chọn ảnh chụp sổ liên lạc, giấy khen hoặc kỷ niệm của con", "連絡帳、賞状、記念写真など"),
    "sample_desc" to Pair("Hoặc chọn nhanh ảnh mẫu để mô phỏng AI (OCR):", "または、AI(OCR)シミュレーション用のサンプル画像を選択:"),
    "scenario_contact" to Pair("Sổ liên lạc", "連絡帳"),
    "scenario_contact_sub" to Pair("Cô giáo viết", "先生のコメント"),
    "scenario_cert" to Pair("Giấy khen", "賞状"),
    "scenario_cert_sub" to Pair("Bé Ngoan", "良い子"),
    "scenario_zoo" to Pair("Sở thú", "動物園"),
    "scenario_zoo_sub" to Pair("Dã ngoại", "ピクニック"),
    "analyze_ai" to Pair("Phân tích ảnh bằng AI (OCR)", "AI(OCR)で画像を分析"),
    "analyzing_ai" to Pair("AI đang đọc ảnh & phân tích...", "AIが画像を読み取り分析中..."),
    "ocr_result_title" to Pair("3. Kết Quả OCR Trích Xuất (Tự Động Điền)", "3. AIが抽出したOCR結果 (自動入力)"),
    "ocr_apply_desc" to Pair("Nhấn 'Áp dụng' để tự động điền các trường bên dưới.", "「適用」を押して、以下の項目に自動入力します。"),
    "ocr_extracted_title" to Pair("Tiêu đề trích xuất", "抽出されたタイトル"),
    "ocr_teacher_feedback" to Pair("Ý kiến cô giáo", "先生の意見"),
    "ocr_detected_act" to Pair("Hoạt động phát hiện", "検出された活動"),
    "apply_ai_results" to Pair("Áp dụng kết quả AI", "AI結果を適用"),
    "diary_details_section" to Pair("2. Thông Tin Chi Tiết Nhật Ký", "2. 日記の詳細情報"),
    "diary_title" to Pair("Tiêu đề nhật ký", "日記のタイトル"),
    "diary_content" to Pair("Nội dung nhật ký (Cảm xúc, hoạt động...)", "日記の内容 (感情、活動...)"),
    "baby_mood" to Pair("Cảm xúc của bé", "赤ちゃんの気分"),
    "event_tags" to Pair("Nhãn sự kiện (ngăn cách bằng dấu phẩy)", "タグ (カンマ区切り)"),
    "teacher_feedback_opt" to Pair("Nhận xét của giáo viên (nếu có)", "先生 của コメント (あれば)"),
    "specific_activities" to Pair("Hoạt động/Kỹ năng cụ thể", "具体的な活動/スキル"),
    "is_important_milestone" to Pair("Mốc phát triển quan trọng?", "重要なマイルストーンですか？"),
    "milestone_name_opt" to Pair("Tên mốc phát triển (ví dụ: Biết đi, Biết bò...)", "マイルストーン名 (例: 歩き始め、言葉を話し始め...)"),
    "diary_date" to Pair("Ngày ghi nhật ký (YYYY-MM-DD)", "日記の日付 (YYYY-MM-DD)"),
    "save_diary" to Pair("Lưu Nhật Ký", "日記を保存"),
    // Timeline
    "growth_timeline" to Pair("Dòng Thời Gian Trưởng Thành", "成長のタイムライン"),
    "all" to Pair("Tất cả", "すべて"),
    "no_diary_yet" to Pair("Chưa có nhật ký nào được ghi nhận.", "日記がまだ記録されていません。"),
    "teacher_opinion" to Pair("Ý kiến cô giáo", "先生の意見"),
    "activity" to Pair("Hoạt động", "活動"),
    "delete_diary" to Pair("Xóa nhật ký", "日記を削除"),
    // Search
    "search_diaries" to Pair("Tìm Kiếm Nhật Ký", "日記を検索"),
    "search_placeholder" to Pair("Nhập từ khóa tìm kiếm (Ví dụ: bé ngoan, đi học, vẽ...)", "キーワードを入力 (例: 良い子、通学、絵画...)"),
    "no_matching_diary" to Pair("Không tìm thấy nhật ký phù hợp.", "一致する日記が見つかりませんでした。"),
    // Recap
    "ai_recap_journey" to Pair("AI Tổng Kết Hành Trình", "AI成長の旅のまとめ"),
    "recap_month_placeholder" to Pair("Nhập tháng cần tổng kết (YYYY-MM)", "まとめたい月を入力 (YYYY-MM)"),
    "recap_empty_desc" to Pair("Báo cáo trưởng thành hàng tháng của bé sẽ được lưu giữ tại đây.", "赤ちゃんの月間成長レポートがここに保存されます。"),
    "create_growth_journey" to Pair("Tạo Hành Trình Trưởng Thành", "成長の旅を作成"),
    "recap_generating" to Pair("Đang tổng hợp thông tin bằng AI...", "AIが情報をまとめています..."),
    "recap_derived_from" to Pair("Tổng kết này được đúc kết từ nhật ký lưu giữ của con.", "このまとめは、記録された日記に基づいています。"),
    // Profile
    "profile_growth_stats" to Pair("Hồ Sơ & Chỉ Số Phát Triển", "プロフィールと成長指標"),
    "beloved_baby" to Pair("Bé yêu", "大好きな赤ちゃん"),
    "update_new_stats" to Pair("Cập Nhật Chỉ Số Mới", "新しい指標を更新"),
    "weight_kg" to Pair("Cân nặng (kg)", "体重 (kg)"),
    "height_cm" to Pair("Chiều cao (cm)", "身長 (cm)"),
    "head_circum_cm" to Pair("Vòng đầu (cm - tùy chọn)", "頭囲 (cm - オプション)"),
    "measure_date" to Pair("Ngày đo (YYYY-MM-DD)", "測定日 (YYYY-MM-DD)"),
    "add_stats" to Pair("Thêm Chỉ Số", "指標を追加"),
    "no_stats_yet" to Pair("Chưa có chỉ số nào được ghi lại.", "指標の記録がありません。"),
    "delete_stats" to Pair("Xóa chỉ số", "指標を削除"),
    "change_language" to Pair("Ngôn ngữ / 言語", "Ngôn ngữ / 言語"),
    // Timeline screen additions
    "timeline_memories" to Pair("Dòng Kỷ Niệm Của Bé", "赤ちゃんの成長の記録"),
    "all_memories" to Pair("Tất Cả Kỷ Niệm", "すべての思い出"),
    "growth_milestones" to Pair("Mốc Phát Triển", "成長の節目"),
    "teacher_feedback_title" to Pair("Lời dặn cô giáo (AI Trích xuất):", "先生の言葉 (AI抽出):"),
    // Bulk Upload additions
    "bulk_add_tab_single" to Pair("Thêm đơn lẻ", "個別追加"),
    "bulk_add_tab_multiple" to Pair("Thêm hàng loạt (AI)", "一括追加 (AI)"),
    "bulk_select_images" to Pair("Chọn nhiều ảnh từ thiết bị", "複数の画像を選択"),
    "bulk_upload_desc" to Pair("Tải lên nhiều ảnh cùng lúc, AI sẽ xử lý song song và tạo nhật ký nhanh chóng.", "同時に複数の画像をアップロードすると、AIが並行して処理し、日記を素早く作成します。"),
    "bulk_analyze_all" to Pair("AI Phân Tích Tất Cả", "AIで一括分析"),
    "bulk_analyzing" to Pair("AI đang phân tích...", "AI分析中..."),
    "bulk_results_title" to Pair("Kết quả xử lý hàng loạt", "一括処理結果"),
    "bulk_save_all" to Pair("Lưu tất cả vào nhật ký", "すべてを日記に保存"),
    "bulk_no_images" to Pair("Chưa chọn ảnh nào. Hãy tải lên hoặc thử ảnh mô phỏng bên dưới!", "画像が選択されていません。アップロードするか、下のサンプル画像を試してください！"),
    "bulk_scenario_help" to Pair("Chọn nhanh bộ ảnh mẫu để mô phỏng phân tích đồng thời:", "一括分析シミュレーション用のサンプル画像セットを選択:"),
    "bulk_scenario_all" to Pair("Tải 3 ảnh mẫu cùng lúc", "3つのサンプル画像をロード"),
    "save_item" to Pair("Lưu bản ghi này", "この記録を保存"),
    "saved" to Pair("Đã lưu", "保存済み"),
    "analyzing" to Pair("Đang phân tích...", "分析中..."),
    "waiting" to Pair("Đang chờ...", "待機中..."),
    "edit" to Pair("Chỉnh sửa", "編集")
)
