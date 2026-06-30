package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "growth_record")
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val babyId: Int,
    val date: String, // YYYY-MM-DD
    val weightKg: Double,
    val heightCm: Double,
    val headCircumferenceCm: Double? = null
)
