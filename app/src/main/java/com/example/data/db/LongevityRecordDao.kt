package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LongevityRecordDao {
    @Query("SELECT * FROM longevity_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<LongevityRecordEntity>>

    @Query("SELECT * FROM longevity_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): Flow<LongevityRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: LongevityRecordEntity): Long

    @Query("DELETE FROM longevity_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM longevity_records")
    suspend fun clearAll()
}
