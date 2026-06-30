package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_profile")
data class BabyProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val birthDate: String, // YYYY-MM-DD
    val gender: String, // "Nam" or "Nữ"
    val profileImageUri: String? = null
)
