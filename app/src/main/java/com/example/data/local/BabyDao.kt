package com.example.data.local

import androidx.room.*
import com.example.data.model.BabyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM baby_profile ORDER BY id DESC")
    fun getAllBabies(): Flow<List<BabyProfile>>

    @Query("SELECT * FROM baby_profile WHERE id = :id LIMIT 1")
    fun getBabyById(id: Int): Flow<BabyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyProfile): Long

    @Update
    suspend fun updateBaby(baby: BabyProfile)

    @Delete
    suspend fun deleteBaby(baby: BabyProfile)
}
