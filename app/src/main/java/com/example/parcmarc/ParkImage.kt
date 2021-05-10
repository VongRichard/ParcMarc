package com.example.parcmarc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "park_image")
class ParkImage(
        @ColumnInfo var parkId: Long,
        @ColumnInfo var imageURI: String
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
