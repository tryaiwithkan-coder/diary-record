package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val babyId: Int,
    val date: String, // YYYY-MM-DD
    val title: String,
    val note: String,
    val mood: String, // "Vui vẻ", "Hòa đồng", "Mệt mỏi", "Khóc nhè", "Ngoan ngoãn"
    val imageUri: String? = null,
    val tags: String = "", // Comma-separated: "Trường học, Giao tiếp"
    val teacherFeedback: String? = null,
    val activities: String? = null,
    val isMilestone: Boolean = false,
    val milestoneTitle: String? = null
) {
    fun getTagsList(): List<String> {
        return if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
