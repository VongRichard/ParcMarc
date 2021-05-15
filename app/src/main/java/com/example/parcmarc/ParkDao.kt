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

    @Delete
    suspend fun delete(parkImage: ParkImage)

    @Query("SELECT COUNT(*) FROM park")
    fun getCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM park")
    fun getAll(): Flow<List<ParkWithParkImages>>

}