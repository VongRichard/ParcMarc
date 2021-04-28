package com.example.parcmarc

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkDao {
    @Insert
    suspend fun insert(park: Park): Long

    @Update
    suspend fun update(park: Park)

    @Delete
    suspend fun delete(park: Park)

    @Query("SELECT * FROM park")
    fun getAll(): Flow<List<Park>>

    @Query("SELECT COUNT(*) FROM park")
    fun getCount(): Flow<Int>
}