package com.example.parcmarc

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface ParkDao {
    @Insert
    suspend fun insert(park: Park): Long

    @Update
    suspend fun update(park: Park)

    @Delete
    suspend fun delete(park: Park)
}