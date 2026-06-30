package com.example.data.local

import androidx.room.*
import com.example.data.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthDao {
    @Query("SELECT * FROM growth_record WHERE babyId = :babyId ORDER BY date ASC")
    fun getGrowthRecords(babyId: Int): Flow<List<GrowthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecord): Long

    @Delete
    suspend fun deleteGrowthRecord(record: GrowthRecord)
}
