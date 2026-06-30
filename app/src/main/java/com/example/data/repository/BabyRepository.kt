package com.example.data.repository

import com.example.data.local.BabyDao
import com.example.data.local.GrowthDao
import com.example.data.model.BabyProfile
import com.example.data.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

class BabyRepository(
    private val babyDao: BabyDao,
    private val growthDao: GrowthDao
) {
    val allBabies: Flow<List<BabyProfile>> = babyDao.getAllBabies()

    fun getBabyById(id: Int): Flow<BabyProfile?> = babyDao.getBabyById(id)

    suspend fun insertBaby(baby: BabyProfile): Long = babyDao.insertBaby(baby)

    suspend fun updateBaby(baby: BabyProfile) = babyDao.updateBaby(baby)

    suspend fun deleteBaby(baby: BabyProfile) = babyDao.deleteBaby(baby)

    fun getGrowthRecords(babyId: Int): Flow<List<GrowthRecord>> = growthDao.getGrowthRecords(babyId)

    suspend fun insertGrowthRecord(record: GrowthRecord): Long = growthDao.insertGrowthRecord(record)

    suspend fun deleteGrowthRecord(record: GrowthRecord) = growthDao.deleteGrowthRecord(record)
}
