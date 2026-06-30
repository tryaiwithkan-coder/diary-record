package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DiaryEntry
import com.example.data.remote.GeminiOcrResult
import com.example.data.repository.DiaryRepository
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BulkOcrItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
    val label: String = "",
    val isAnalyzing: Boolean = false,
    val result: GeminiOcrResult? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = DiaryRepository(database.diaryDao())

    private val _babyId = MutableStateFlow<Int?>(null)
    val babyId: StateFlow<Int?> = _babyId.asStateFlow()

    // Reactively observe diary entries for the active baby
    val entries: StateFlow<List<DiaryEntry>> = _babyId
        .filterNotNull()
        .flatMapLatest { id -> repository.getEntriesForBaby(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reactively filter search entries
    val searchResults: StateFlow<List<DiaryEntry>> = combine(_babyId.filterNotNull(), _searchQuery) { id, query ->
        id to query
    }.flatMapLatest { (id, query) ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchEntries(id, query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI OCR States
    private val _ocrLoading = MutableStateFlow(false)
    val ocrLoading: StateFlow<Boolean> = _ocrLoading.asStateFlow()

    private val _ocrResult = MutableStateFlow<GeminiOcrResult?>(null)
    val ocrResult: StateFlow<GeminiOcrResult?> = _ocrResult.asStateFlow()

    // Monthly Recap States
    private val _recapLoading = MutableStateFlow(false)
    val recapLoading: StateFlow<Boolean> = _recapLoading.asStateFlow()

    private val _monthlyRecapText = MutableStateFlow("")
    val monthlyRecapText: StateFlow<String> = _monthlyRecapText.asStateFlow()

    private val _recapEntries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val recapEntries: StateFlow<List<DiaryEntry>> = _recapEntries.asStateFlow()

    fun setBabyId(id: Int) {
        _babyId.value = id
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addEntry(
        title: String,
        note: String,
        mood: String,
        imageUri: String? = null,
        tags: String = "",
        teacherFeedback: String? = null,
        activities: String? = null,
        isMilestone: Boolean = false,
        milestoneTitle: String? = null,
        date: String
    ) {
        val currentBabyId = _babyId.value ?: return
        viewModelScope.launch {
            val entry = DiaryEntry(
                babyId = currentBabyId,
                date = date,
                title = title,
                note = note,
                mood = mood,
                imageUri = imageUri,
                tags = tags,
                teacherFeedback = teacherFeedback,
                activities = activities,
                isMilestone = isMilestone,
                milestoneTitle = milestoneTitle
            )
            repository.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun analyzeImage(base64Image: String, mimeType: String) {
        viewModelScope.launch {
            _ocrLoading.value = true
            _ocrResult.value = null
            try {
                val result = repository.analyzeImageWithGemini(base64Image, mimeType)
                _ocrResult.value = result
            } catch (e: Exception) {
                _ocrResult.value = null
            } finally {
                _ocrLoading.value = false
            }
        }
    }

    fun clearOcrResult() {
        _ocrResult.value = null
    }

    fun generateRecap(babyName: String, monthYear: String) {
        val currentBabyId = _babyId.value ?: return
        viewModelScope.launch {
            _recapLoading.value = true
            _monthlyRecapText.value = ""
            try {
                val monthEntries = repository.getEntriesByMonth(currentBabyId, monthYear)
                _recapEntries.value = monthEntries
                val recap = repository.generateMonthlyRecap(babyName, monthYear, monthEntries)
                _monthlyRecapText.value = recap
            } catch (e: Exception) {
                _monthlyRecapText.value = "Lỗi khi tổng hợp báo cáo tháng: ${e.message}"
            } finally {
                _recapLoading.value = false
            }
        }
    }

    fun clearRecap() {
        _monthlyRecapText.value = ""
        _recapEntries.value = emptyList()
    }

    // Bulk OCR States
    private val _bulkItems = MutableStateFlow<List<BulkOcrItem>>(emptyList())
    val bulkItems: StateFlow<List<BulkOcrItem>> = _bulkItems.asStateFlow()

    private val _bulkLoading = MutableStateFlow(false)
    val bulkLoading: StateFlow<Boolean> = _bulkLoading.asStateFlow()

    fun setBulkItems(items: List<BulkOcrItem>) {
        _bulkItems.value = items
    }

    fun clearBulkItems() {
        _bulkItems.value = emptyList()
        _bulkLoading.value = false
    }

    fun removeBulkItem(itemId: String) {
        _bulkItems.value = _bulkItems.value.filter { it.id != itemId }
    }

    fun updateBulkItemResult(itemId: String, result: GeminiOcrResult) {
        _bulkItems.value = _bulkItems.value.map {
            if (it.id == itemId) it.copy(result = result) else it
        }
    }

    fun updateBulkItemSaved(itemId: String, saved: Boolean) {
        _bulkItems.value = _bulkItems.value.map {
            if (it.id == itemId) it.copy(isSaved = saved) else it
        }
    }

    fun analyzeBulkItems(context: android.content.Context) {
        viewModelScope.launch {
            _bulkLoading.value = true
            val currentItems = _bulkItems.value
            
            // Mark all items as analyzing and clear old results/errors
            _bulkItems.value = currentItems.map { 
                if (!it.isSaved) it.copy(isAnalyzing = true, error = null, result = null) else it 
            }

            // Launch analyses concurrently for all items that are not yet saved
            val jobs = currentItems.map { item ->
                async {
                    if (item.isSaved) return@async
                    
                    try {
                        val bitmap = item.bitmap ?: item.uri?.let { uri ->
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val bmp = android.graphics.BitmapFactory.decodeStream(inputStream)
                            inputStream?.close()
                            bmp
                        }

                        if (bitmap == null) {
                            _bulkItems.value = _bulkItems.value.map {
                                if (it.id == item.id) it.copy(isAnalyzing = false, error = "Không thể tải ảnh") else it
                            }
                            return@async
                        }

                        // Convert to base64
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        val base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)

                        // Call Gemini
                        val result = repository.analyzeImageWithGemini(base64, "image/jpeg")
                        
                        _bulkItems.value = _bulkItems.value.map {
                            if (it.id == item.id) {
                                if (result != null) {
                                    it.copy(isAnalyzing = false, result = result)
                                } else {
                                    it.copy(isAnalyzing = false, error = "AI phân tích thất bại")
                                }
                            } else {
                                it
                            }
                        }
                    } catch (e: Exception) {
                        _bulkItems.value = _bulkItems.value.map {
                            if (it.id == item.id) it.copy(isAnalyzing = false, error = e.message ?: "Lỗi hệ thống") else it
                        }
                    }
                }
            }

            // Await all async jobs to run concurrently!
            jobs.awaitAll()
            _bulkLoading.value = false
        }
    }

    fun saveBulkItemToDiary(itemId: String, customTitle: String? = null, customNote: String? = null) {
        val currentBabyId = _babyId.value ?: return
        val item = _bulkItems.value.find { it.id == itemId } ?: return
        val result = item.result ?: return
        
        viewModelScope.launch {
            val entry = DiaryEntry(
                babyId = currentBabyId,
                date = result.date,
                title = customTitle ?: result.title,
                note = customNote ?: (result.teacherFeedback?.let { "Ý kiến giáo viên: $it\n" } ?: "") + (result.activities ?: "Hành trình khôn lớn."),
                mood = result.mood,
                imageUri = item.uri?.toString(),
                tags = result.tags.joinToString(", "),
                teacherFeedback = result.teacherFeedback,
                activities = result.activities,
                isMilestone = result.isMilestone,
                milestoneTitle = result.milestoneTitle
            )
            repository.insertEntry(entry)
            
            _bulkItems.value = _bulkItems.value.map {
                if (it.id == itemId) it.copy(isSaved = true) else it
            }
        }
    }

    fun saveAllBulkItemsToDiary() {
        val currentBabyId = _babyId.value ?: return
        val unsavedItemsWithResults = _bulkItems.value.filter { !it.isSaved && it.result != null }
        if (unsavedItemsWithResults.isEmpty()) return

        viewModelScope.launch {
            unsavedItemsWithResults.forEach { item ->
                val result = item.result ?: return@forEach
                val entry = DiaryEntry(
                    babyId = currentBabyId,
                    date = result.date,
                    title = result.title,
                    note = (result.teacherFeedback?.let { "Ý kiến giáo viên: $it\n" } ?: "") + (result.activities ?: "Hành trình khôn lớn."),
                    mood = result.mood,
                    imageUri = item.uri?.toString(),
                    tags = result.tags.joinToString(", "),
                    teacherFeedback = result.teacherFeedback,
                    activities = result.activities,
                    isMilestone = result.isMilestone,
                    milestoneTitle = result.milestoneTitle
                )
                repository.insertEntry(entry)
            }
            
            _bulkItems.value = _bulkItems.value.map {
                if (it.result != null) it.copy(isSaved = true) else it
            }
        }
    }
}
