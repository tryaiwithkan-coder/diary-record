package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BabyProfile
import com.example.data.model.DiaryEntry
import com.example.data.model.GrowthRecord
import com.example.data.repository.BabyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BabyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BabyRepository(database.babyDao(), database.growthDao())

    val allBabies: StateFlow<List<BabyProfile>> = repository.allBabies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeBaby = MutableStateFlow<BabyProfile?>(null)
    val activeBaby: StateFlow<BabyProfile?> = _activeBaby.asStateFlow()

    private val _growthRecords = MutableStateFlow<List<GrowthRecord>>(emptyList())
    val growthRecords: StateFlow<List<GrowthRecord>> = _growthRecords.asStateFlow()

    init {
        // Observe all babies and automatically set the active one if none is set
        viewModelScope.launch {
            allBabies.collect { babies ->
                if (babies.isEmpty()) {
                    prepopulateDatabase()
                } else if (_activeBaby.value == null) {
                    _activeBaby.value = babies.first()
                }
            }
        }

        // Observe growth records for the active baby
        viewModelScope.launch {
            activeBaby.collect { baby ->
                if (baby != null) {
                    repository.getGrowthRecords(baby.id).collect { records ->
                        _growthRecords.value = records
                    }
                } else {
                    _growthRecords.value = emptyList()
                }
            }
        }
    }

    fun selectBaby(baby: BabyProfile) {
        _activeBaby.value = baby
    }

    fun addBaby(name: String, birthDate: String, gender: String, profileImageUri: String? = null) {
        viewModelScope.launch {
            val baby = BabyProfile(
                name = name,
                birthDate = birthDate,
                gender = gender,
                profileImageUri = profileImageUri
            )
            val id = repository.insertBaby(baby)
            // Auto select new baby
            _activeBaby.value = baby.copy(id = id.toInt())
        }
    }

    fun updateBaby(baby: BabyProfile) {
        viewModelScope.launch {
            repository.updateBaby(baby)
            if (_activeBaby.value?.id == baby.id) {
                _activeBaby.value = baby
            }
        }
    }

    fun deleteBaby(baby: BabyProfile) {
        viewModelScope.launch {
            repository.deleteBaby(baby)
            if (_activeBaby.value?.id == baby.id) {
                _activeBaby.value = allBabies.value.firstOrNull { it.id != baby.id }
            }
        }
    }

    fun addGrowthRecord(weightKg: Double, heightCm: Double, headCircumferenceCm: Double?, date: String) {
        val baby = _activeBaby.value ?: return
        viewModelScope.launch {
            repository.insertGrowthRecord(
                GrowthRecord(
                    babyId = baby.id,
                    date = date,
                    weightKg = weightKg,
                    heightCm = heightCm,
                    headCircumferenceCm = headCircumferenceCm
                )
            )
        }
    }

    fun deleteGrowthRecord(record: GrowthRecord) {
        viewModelScope.launch {
            repository.deleteGrowthRecord(record)
        }
    }

    private suspend fun prepopulateDatabase() {
        // Insert default baby: Mochi
        val mochi = BabyProfile(
            name = "Bé Mochi",
            birthDate = "2024-05-15",
            gender = "Nam",
            profileImageUri = "sample_mochi" // indicates we can draw a beautiful baby avatar
        )
        val babyId = repository.insertBaby(mochi).toInt()

        // Insert initial growth records
        val initialGrowth = listOf(
            GrowthRecord(babyId = babyId, date = "2024-05-15", weightKg = 3.2, heightCm = 50.0, headCircumferenceCm = 34.0),
            GrowthRecord(babyId = babyId, date = "2024-08-15", weightKg = 5.8, heightCm = 58.0, headCircumferenceCm = 38.5),
            GrowthRecord(babyId = babyId, date = "2024-11-15", weightKg = 8.0, heightCm = 68.0, headCircumferenceCm = 43.0),
            GrowthRecord(babyId = babyId, date = "2025-05-15", weightKg = 10.5, heightCm = 76.0, headCircumferenceCm = 46.0),
            GrowthRecord(babyId = babyId, date = "2025-11-15", weightKg = 12.0, heightCm = 85.0, headCircumferenceCm = 47.0),
            GrowthRecord(babyId = babyId, date = "2026-05-15", weightKg = 13.5, heightCm = 92.0, headCircumferenceCm = 48.0)
        )
        for (g in initialGrowth) {
            repository.insertGrowthRecord(g)
        }

        // Prepopulate standard diary entries directly through the database
        val diaryDao = database.diaryDao()
        val defaultEntries = listOf(
            DiaryEntry(
                babyId = babyId,
                date = "2026-05-20",
                title = "Mochi tự cầm thìa ăn siêu siêu giỏi",
                note = "Hôm nay bé Mochi tự cầm thìa xúc cơm ăn hết veo cả bát nhỏ luôn nha. Dù có rơi vãi lung tung một tí nhưng con tập trung và rất thích thú tự lập. Ba mẹ vui lắm cưng quá!",
                mood = "Vui vẻ",
                imageUri = "sample_photo_eat",
                tags = "Kỹ năng, Tự lập",
                isMilestone = true,
                milestoneTitle = "Biết tự cầm thìa ăn"
            ),
            DiaryEntry(
                babyId = babyId,
                date = "2026-06-10",
                title = "Giấy khen Bé Ngoan tuần học mầm non",
                note = "Cô giáo gửi ảnh chụp Mochi cầm phiếu bé ngoan rạng rỡ ở trường mầm non Tuổi Thần Tiên. Đây là phiếu bé ngoan đầu tiên của cục cưng!",
                mood = "Ngoan ngoãn",
                imageUri = "sample_photo_report",
                tags = "Trường học, Giao tiếp",
                isMilestone = false,
                teacherFeedback = "Mochi tuần này học rất hòa đồng, tích cực tham gia các hoạt động hát múa, vẽ tranh, chủ động giơ tay phát biểu khiến cô rất hài lòng.",
                activities = "Vẽ tranh, Hát múa tập thể, Nhận diện con vật"
            ),
            DiaryEntry(
                babyId = babyId,
                date = "2026-06-18",
                title = "Đi chơi Thảo Cầm Viên cùng ông bà",
                note = "Hôm nay cuối tuần mát mẻ cả nhà mình cùng đưa Mochi đi dã ngoại sở thú Thảo Cầm Viên. Bé thích mê tơi khi thấy chú hươu cao cổ ăn lá cây, con còn bập bẹ chỉ tay nói 'Hươu', 'Khỉ'. Chuyến đi siêu nhiều kỷ niệm ấm áp!",
                mood = "Vui vẻ",
                imageUri = "sample_photo_zoo",
                tags = "Gia đình, Khám phá",
                isMilestone = false
            ),
            DiaryEntry(
                babyId = babyId,
                date = "2026-06-25",
                title = "Mochi hắt hơi sụt sịt mũi nhẹ",
                note = "Do thời tiết thay đổi đột ngột nên tối qua con bị ngạt mũi nhẹ và hắt hơi sụt sịt. Ba mẹ đã nhỏ nước muối sinh lý ấm, bôi dầu tràm và cho con uống siro húng chanh. Con vẫn chơi ngoan và chịu khó ăn cháo ấm.",
                mood = "Mệt mỏi",
                imageUri = "sample_photo_sick",
                tags = "Sức khỏe, Thể chất",
                isMilestone = false
            )
        )
        for (e in defaultEntries) {
            diaryDao.insertEntry(e)
        }
    }
}
