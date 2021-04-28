package com.example.parcmarc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "park")
class Park (
        @ColumnInfo var name: String,
        @ColumnInfo var latitude: Float,
        @ColumnInfo var longitude: Float,
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    override fun toString() = name
}