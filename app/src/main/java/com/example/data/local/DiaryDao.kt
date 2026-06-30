package com.example.data.local

import androidx.room.*
import com.example.data.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE babyId = :babyId ORDER BY date DESC, id DESC")
    fun getEntriesForBaby(babyId: Int): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: Int): Flow<DiaryEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry): Long

    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)

    @Query("""
        SELECT * FROM diary_entries 
        WHERE babyId = :babyId AND (
            title LIKE '%' || :query || '%' OR 
            note LIKE '%' || :query || '%' OR 
            tags LIKE '%' || :query || '%' OR 
            mood LIKE '%' || :query || '%' OR
            milestoneTitle LIKE '%' || :query || '%'
        )
        ORDER BY date DESC, id DESC
    """)
    fun searchEntries(babyId: Int, query: String): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE babyId = :babyId AND date LIKE :yearMonth || '%' ORDER BY date ASC")
    suspend fun getEntriesByMonth(babyId: Int, yearMonth: String): List<DiaryEntry>
}
