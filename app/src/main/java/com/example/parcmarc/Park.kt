package com.example.parcmarc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "park")
class Park(
    @ColumnInfo var name: String,
    @ColumnInfo var latitude: Double,
    @ColumnInfo var longitude: Double,
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun toString() = name
}