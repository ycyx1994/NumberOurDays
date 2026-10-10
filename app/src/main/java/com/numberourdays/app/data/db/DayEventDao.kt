package com.numberourdays.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DayEventDao {
    @Query("SELECT * FROM day_events")
    fun observeAll(): Flow<List<DayEventEntity>>

    @Query("SELECT * FROM day_events")
    suspend fun getAll(): List<DayEventEntity>

    @Query("SELECT * FROM day_events WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DayEventEntity?

    @Query("SELECT COUNT(*) FROM day_events")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DayEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DayEventEntity>)

    @Transaction
    suspend fun replaceAll(items: List<DayEventEntity>) {
        clearAll()
        upsertAll(items)
    }

    @Query("DELETE FROM day_events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM day_events")
    suspend fun clearAll()
}
