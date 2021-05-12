package com.example.parcmarc

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkImageDao {
    @Insert
    suspend fun insert(parkImage: ParkImage): Long

    @Update
    suspend fun update(parkImage: ParkImage)

    @Delete
    suspend fun delete(parkImage: ParkImage)

    @Query("SELECT * FROM park_image")
    fun getAll(): Flow<List<ParkImage>>

    @Query("SELECT COUNT(*) FROM park_image")
    fun getCount(): Flow<Int>
}